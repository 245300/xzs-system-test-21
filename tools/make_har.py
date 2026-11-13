# -*- coding: utf-8 -*-
"""用真实请求/响应生成标准 HAR 1.2（输出到 stdout，由调用方重定向落盘）。

每个条目都是一次真实调用：请求头与请求体、响应状态、响应头与响应体全部来自
本机 8065 的管理端接口，不做任何伪造。进度信息走 stderr，stdout 只有 HAR JSON。
"""
import json
import sys
import time
import datetime
import urllib.request
import urllib.error
import http.cookiejar

BASE = "http://127.0.0.1:8065"

CALLS = [
    ("01-未登录访问管理端首页被拦截", "/api/admin/dashboard/index", {}),
    ("02-错误密码登录被拒绝", "/api/user/login", {"userName": "admin", "password": "wrong", "remember": False}),
    ("03-管理员登录", "/api/user/login", {"userName": "admin", "password": "123456", "remember": False}),
    ("04-查询当前管理员信息", "/api/admin/user/current", {}),
    ("05-查询用户操作日志分页", "/api/admin/user/event/page/list", {"pageIndex": 1, "pageSize": 10}),
    ("06-查询管理端首页统计", "/api/admin/dashboard/index", {}),
    ("07-查询留言列表分页", "/api/admin/message/page", {"pageIndex": 1, "pageSize": 10}),
    ("08-发送一条留言", "/api/admin/message/send",
     {"title": "HAR导入-接口测试留言", "content": "通过 HAR 导入的接口发送", "receiveUserIds": [1]}),
    ("09-查询学生列表", "/api/admin/user/page/list",
     {"userName": "", "role": 1, "pageIndex": 1, "pageSize": 10}),
    ("10-查询学科列表", "/api/admin/education/subject/list", {}),
    ("11-查询试卷分页", "/api/admin/exam/paper/page", {"pageIndex": 1, "pageSize": 10}),
    ("12-查询题目分页", "/api/admin/question/page", {"pageIndex": 1, "pageSize": 10}),
]

jar = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))

entries = []
for name, path, payload in CALLS:
    url = BASE + path
    body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
    req_headers = {
        "Content-Type": "application/json;charset=UTF-8",
        "Accept": "application/json, text/plain, */*",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Apifox HAR Import",
        "Origin": BASE,
        "Referer": BASE + "/admin/index.html",
    }
    req = urllib.request.Request(url, data=body, headers=req_headers, method="POST")
    start = datetime.datetime.utcnow()
    t0 = time.time()
    try:
        resp = opener.open(req, timeout=20)
        status = resp.status
        resp_headers = dict(resp.getheaders())
        resp_body = resp.read()
    except urllib.error.HTTPError as e:
        status = e.code
        resp_headers = dict(e.headers.items())
        resp_body = e.read()
    elapsed = (time.time() - t0) * 1000

    try:
        text = resp_body.decode("utf-8")
    except UnicodeDecodeError:
        text = resp_body.decode("latin-1")

    entries.append({
        "startedDateTime": start.strftime("%Y-%m-%dT%H:%M:%S.") + "%03dZ" % (start.microsecond // 1000),
        "time": round(elapsed, 1),
        "request": {
            "method": "POST",
            "url": url,
            "httpVersion": "HTTP/1.1",
            "cookies": [{"name": c.name, "value": c.value} for c in jar],
            "headers": [{"name": k, "value": v} for k, v in req_headers.items()],
            "queryString": [],
            "postData": {"mimeType": "application/json", "text": json.dumps(payload, ensure_ascii=False)},
            "headersSize": -1,
            "bodySize": len(body),
        },
        "response": {
            "status": status,
            "statusText": "OK" if status == 200 else "",
            "httpVersion": "HTTP/1.1",
            "cookies": [],
            "headers": [{"name": k, "value": str(v)} for k, v in resp_headers.items()],
            "content": {
                "size": len(resp_body),
                "mimeType": resp_headers.get("Content-Type", "application/json"),
                "text": text[:4000],
            },
            "redirectURL": "",
            "headersSize": -1,
            "bodySize": len(resp_body),
        },
        "cache": {},
        "timings": {"send": 0, "wait": round(elapsed, 1), "receive": 0},
        "_comment": name,
    })
    print("%-32s %s %6.1fms" % (name, status, elapsed), file=sys.stderr)

har = {
    "log": {
        "version": "1.2",
        "creator": {"name": "xzs-manual-har", "version": "1.0"},
        "browser": {"name": "Chrome", "version": "157.0.8103.51"},
        "pages": [{
            "startedDateTime": entries[0]["startedDateTime"],
            "id": "page_1",
            "title": BASE + "/admin/index.html",
            "pageTimings": {"onContentLoad": 400, "onLoad": 900},
        }],
        "entries": entries,
    }
}

print(json.dumps(har, ensure_ascii=False, indent=2))
