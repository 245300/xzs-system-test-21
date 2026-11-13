// 生成 3 个 Apifox CLI 集合（每个场景 8 个请求，共 24 个请求）
const fs = require('fs');
const path = require('path');

const BASE = 'http://127.0.0.1:8065';
const OUT = path.join(__dirname, '..', 'api');

function step(name, url, body, tests) {
  const headers = [
    { key: 'Content-Type', value: 'application/json' },
    { key: 'request-ajax', value: 'true' },
    {
      key: 'User-Agent',
      value:
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/157.0.0.0 Safari/537.36',
    },
  ];
  return {
    name,
    metaInfo: { type: 'http' },
    request: {
      method: 'POST',
      url: BASE + url,
      headers,
      body: { mode: 'raw', raw: JSON.stringify(body) },
    },
    event: [
      {
        listen: 'prerequest',
        script: {
          type: 'text/javascript',
          exec: [
            "pm.request.headers.upsert({key:'Content-Type', value:'application/json'});",
            "pm.variables.set('adminName', 'admin');",
            "pm.variables.set('adminPwd', '123456');",
          ],
        },
      },
      {
        listen: 'test',
        script: {
          type: 'text/javascript',
          exec: [
            "pm.test('HTTP 200', function () { pm.response.to.have.status(200); });",
            'const body = pm.response.json();',
          ].concat(tests),
        },
      },
    ],
  };
}

const assertOk = (extra = []) =>
  ["pm.test('业务码为 1', function () { pm.expect(body.code).to.equal(1); });"].concat(extra);

const assertUnauthorized = [
  "pm.test('未登录业务码 401', function () { pm.expect(body.code).to.equal(401); });",
  "pm.test('提示未登录', function () { pm.expect(body.message).to.include('未登录'); });",
];

const scene1 = {
  apifoxCli: '1.1',
  info: {
    name: '场景一：管理端登录-首页接口测试',
    description: '覆盖未登录拦截、错误密码、管理员登录、首页统计、当前用户与操作日志。',
  },
  name: '场景一：管理端登录-首页接口测试',
  item: [
    {
      name: '场景一：管理端登录-首页接口测试',
      item: [
        step('01-未登录访问管理端首页被拦截', '/api/admin/dashboard/index', {}, assertUnauthorized),
        step(
          '02-错误密码登录被拒绝',
          '/api/user/login',
          { userName: '{{adminName}}', password: 'wrong123', remember: false },
          [
            "pm.test('错误密码业务码非 1', function () { pm.expect(body.code).to.not.equal(1); });",
            "pm.test('提示用户名或密码错误', function () { pm.expect(body.message).to.include('用户名或密码错误'); });",
          ],
        ),
        step(
          '03-管理员正确登录',
          '/api/user/login',
          { userName: '{{adminName}}', password: '{{adminPwd}}', remember: false },
          assertOk([
            "pm.test('返回 admin', function () { pm.expect(body.response.userName).to.equal('admin'); });",
            "pm.test('提示成功', function () { pm.expect(body.message).to.include('成功'); });",
          ]),
        ),
        step('04-查询首页统计数据', '/api/admin/dashboard/index', {}, assertOk([
          "pm.test('返回对象', function () { pm.expect(body.response).to.be.an('object'); });",
        ])),
        step('05-再次查询首页统计', '/api/admin/dashboard/index', {}, assertOk()),
        step('06-查询当前管理员信息', '/api/admin/user/current', {}, assertOk([
          "pm.test('用户名为 admin', function () { pm.expect(body.response.userName).to.equal('admin'); });",
        ])),
        step(
          '07-查询用户操作日志分页',
          '/api/admin/user/event/page/list',
          { userId: null, userName: '', pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回日志数组', function () { pm.expect(body.response.list).to.be.an('array'); });",
          ]),
        ),
        step('08-登录态下再次查询首页', '/api/admin/dashboard/index', {}, assertOk()),
      ],
    },
  ],
};

const scene2 = {
  apifoxCli: '1.1',
  info: {
    name: '场景二：管理端登录-留言列表接口测试',
    description: '覆盖未登录拦截、留言分页、发送人筛选、发送留言、空标题反向校验。',
  },
  name: '场景二：管理端登录-留言列表接口测试',
  item: [
    {
      name: '场景二：管理端登录-留言列表接口测试',
      item: [
        step(
          '01-未登录访问留言列表被拦截',
          '/api/admin/message/page',
          { sendUserName: null, pageIndex: 1, pageSize: 10 },
          assertUnauthorized,
        ),
        step(
          '02-管理员登录',
          '/api/user/login',
          { userName: '{{adminName}}', password: '{{adminPwd}}', remember: false },
          assertOk(),
        ),
        step(
          '03-查询留言列表分页',
          '/api/admin/message/page',
          { sendUserName: null, pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回留言数组', function () { pm.expect(body.response.list).to.be.an('array'); });",
            "pm.test('返回总数', function () { pm.expect(body.response.total).to.be.a('number'); });",
          ]),
        ),
        step(
          '04-按发送人筛选留言',
          '/api/admin/message/page',
          { sendUserName: 'admin', pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回分页结构', function () { pm.expect(body.response.pageNum).to.be.a('number'); });",
          ]),
        ),
        step(
          '05-校验每页条数参数',
          '/api/admin/message/page',
          { sendUserName: null, pageIndex: 1, pageSize: 5 },
          assertOk([
            "pm.test('列表长度不超过 5', function () { pm.expect(body.response.list.length).to.be.at.most(5); });",
          ]),
        ),
        step(
          '06-发送一条留言',
          '/api/admin/message/send',
          {
            title: '接口测试留言-学之思',
            content: '这是一条用于实验八管理端留言列表接口测试的留言。',
            receiveUserIds: [1],
          },
          assertOk(),
        ),
        step(
          '07-发送后再次查询留言列表',
          '/api/admin/message/page',
          { sendUserName: null, pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('留言总数不少于 1', function () { pm.expect(body.response.total).to.be.at.least(1); });",
          ]),
        ),
        step(
          '08-空标题发送留言被拒绝',
          '/api/admin/message/send',
          { title: '', content: '空标题反向用例', receiveUserIds: [1] },
          [
            "pm.test('空标题业务码非 1', function () { pm.expect(body.code).to.not.equal(1); });",
            "pm.test('返回错误提示', function () { pm.expect(body.message).to.be.a('string'); });",
          ],
        ),
      ],
    },
  ],
};

const scene3 = {
  apifoxCli: '1.1',
  info: {
    name: '场景三：管理端登录-用户与卷题管理接口测试',
    description: '覆盖未登录拦截、学生/管理员分页、学科列表、试卷与题目分页、题目详情。',
  },
  name: '场景三：管理端登录-用户与卷题管理接口测试',
  item: [
    {
      name: '场景三：管理端登录-用户与卷题管理接口测试',
      item: [
        step(
          '01-未登录访问学生列表被拦截',
          '/api/admin/user/page/list',
          { userName: '', role: 1, pageIndex: 1, pageSize: 10 },
          assertUnauthorized,
        ),
        step(
          '02-管理员登录',
          '/api/user/login',
          { userName: '{{adminName}}', password: '{{adminPwd}}', remember: false },
          assertOk(),
        ),
        step(
          '03-查询学生列表',
          '/api/admin/user/page/list',
          { userName: '', role: 1, pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回学生数组', function () { pm.expect(body.response.list).to.be.an('array'); });",
          ]),
        ),
        step(
          '04-查询管理员列表',
          '/api/admin/user/page/list',
          { userName: '', role: 0, pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回管理员数组', function () { pm.expect(body.response.list).to.be.an('array'); });",
          ]),
        ),
        step('05-查询学科列表', '/api/admin/education/subject/list', {}, assertOk([
          "pm.test('返回学科数组', function () { pm.expect(body.response).to.be.an('array'); });",
        ])),
        step(
          '06-查询试卷分页',
          '/api/admin/exam/paper/page',
          { id: null, level: null, subjectId: null, pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回试卷数组', function () { pm.expect(body.response.list).to.be.an('array'); });",
          ]),
        ),
        step(
          '07-查询题目分页',
          '/api/admin/question/page',
          { id: null, questionType: null, level: null, subjectId: null, pageIndex: 1, pageSize: 10 },
          assertOk([
            "pm.test('返回题目数组', function () { pm.expect(body.response.list).to.be.an('array'); });",
          ]),
        ),
        step('08-查询题目详情', '/api/admin/question/select/1', {}, assertOk([
          "pm.test('题目 id 为 1', function () { pm.expect(body.response.id).to.equal(1); });",
        ])),
      ],
    },
  ],
};

fs.mkdirSync(OUT, { recursive: true });
for (const [file, data] of [
  ['场景1-管理端登录-首页.json', scene1],
  ['场景2-管理端登录-留言列表.json', scene2],
  ['场景3-管理端登录-用户与卷题管理.json', scene3],
]) {
  fs.writeFileSync(path.join(OUT, file), JSON.stringify(data, null, 2), 'utf8');
  console.log('wrote', file);
}
