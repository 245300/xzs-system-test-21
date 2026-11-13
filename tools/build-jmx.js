// 生成学之思管理端性能测试 JMX：3 线程组、13 采样器、3 事务、CSV、断言、双监听器
const fs = require('fs');
const path = require('path');

const esc = (s) =>
  String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');

function assertion(name, field, value, message) {
  return `            <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="${esc(
    name,
  )}" enabled="true">
              <collectionProp name="Asserion.test_strings">
                <stringProp name="assert_${Math.random().toString(36).slice(2, 8)}">${esc(value)}</stringProp>
              </collectionProp>
              <stringProp name="Assertion.custom_message">${esc(message)}</stringProp>
              <stringProp name="Assertion.test_field">${field}</stringProp>
              <boolProp name="Assertion.assume_success">false</boolProp>
              <intProp name="Assertion.test_type">2</intProp>
            </ResponseAssertion>
            <hashTree/>`;
}

function sampler(name, urlPath, body) {
  return `          <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="${esc(
    name,
  )}" enabled="true">
            <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="用户定义的变量" enabled="true">
              <collectionProp name="Arguments.arguments">
                <elementProp name="" elementType="HTTPArgument">
                  <boolProp name="HTTPArgument.always_encode">false</boolProp>
                  <stringProp name="Argument.value">${esc(body)}</stringProp>
                  <stringProp name="Argument.metadata">=</stringProp>
                </elementProp>
              </collectionProp>
            </elementProp>
            <stringProp name="HTTPSampler.domain">localhost</stringProp>
            <stringProp name="HTTPSampler.port">\${port}</stringProp>
            <stringProp name="HTTPSampler.protocol">http</stringProp>
            <stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp>
            <stringProp name="HTTPSampler.path">${esc(urlPath)}</stringProp>
            <stringProp name="HTTPSampler.method">POST</stringProp>
            <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
            <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
            <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
            <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
            <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
            <stringProp name="HTTPSampler.connect_timeout">10000</stringProp>
            <stringProp name="HTTPSampler.response_timeout">30000</stringProp>
            <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
          </HTTPSamplerProxy>
          <hashTree>
${assertion('断言-状态码200', 'Assertion.response_code', '200', name + ' 状态码不是 200')}
${assertion('断言-业务码为1', 'Assertion.response_data', '"code":1', name + ' 业务码不是 1')}
          </hashTree>`;
}

function threadGroup(name, threads, loops, ramp, samplers) {
  return `      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="${esc(
    name,
  )}" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="循环控制器" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <intProp name="LoopController.loops">${loops}</intProp>
        </elementProp>
        <intProp name="ThreadGroup.num_threads">${threads}</intProp>
        <stringProp name="ThreadGroup.ramp_time">${ramp}</stringProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
        <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
      </ThreadGroup>
      <hashTree>
        <CookieManager guiclass="CookiePanel" testclass="CookieManager" testname="HTTP Cookie管理器" enabled="true">
          <collectionProp name="CookieManager.cookies"/>
          <boolProp name="CookieManager.clearEachIteration">false</boolProp>
          <boolProp name="CookieManager.controlledByThreadGroup">false</boolProp>
        </CookieManager>
        <hashTree/>
        <TransactionController guiclass="TransactionControllerGui" testclass="TransactionController" testname="${esc(
          samplers.txn,
        )}" enabled="true">
          <boolProp name="TransactionController.includeTimers">false</boolProp>
          <boolProp name="TransactionController.parent">false</boolProp>
        </TransactionController>
        <hashTree>
${samplers.items.join('\n')}
        </hashTree>
      </hashTree>`;
}

const loginBody = '{"userName":"admin","password":"${loginPwd}","remember":false}';
const addStudent = (varName) =>
  `{"userName":"${'${' + varName + '}'}_\${__time(,)}\${__threadNum}","password":"123456","realName":"\${${varName}}","age":"18","status":1,"sex":1,"birthDay":"2010-01-01","phone":"13800138001","role":1,"userLevel":1}`;
const addQuestion =
  '{"questionType":1,"subjectId":1,"title":"${questionTitle}_${__time(,)}${__threadNum}","gradeLevel":1,"items":[{"prefix":"A","content":"1"},{"prefix":"B","content":"2"},{"prefix":"C","content":"3"},{"prefix":"D","content":"4"}],"analyze":"性能测试题目","correctArray":["B"],"correct":"B","score":"5","difficult":1}';
const addPaper =
  '{"level":1,"subjectId":1,"paperType":1,"name":"${paperName}_${__time(,)}${__threadNum}","suggestTime":30,"score":"5","titleItems":[{"name":"一、单选题","questionItems":[{"id":${questionId},"questionType":1,"subjectId":1,"title":"性能测试题目1113","gradeLevel":1,"items":[{"prefix":"A","content":"1"},{"prefix":"B","content":"2"},{"prefix":"C","content":"3"},{"prefix":"D","content":"4"}],"analyze":"性能测试题目","correctArray":["B"],"correct":"B","score":"5","difficult":1}]}]}';

const g1 = {
  txn: '事务-用户管理新增学生',
  items: [
    sampler('01-管理员登录', '/api/user/login', loginBody),
    sampler('02-查询学生列表', '/api/admin/user/page/list', '{"userName":"","role":1,"pageIndex":1,"pageSize":10}'),
    sampler('03-新增学生A', '/api/admin/user/edit', addStudent('studentA')),
    sampler('04-新增学生B', '/api/admin/user/edit', addStudent('studentB')),
    sampler('05-复核学生列表', '/api/admin/user/page/list', '{"userName":"","role":1,"pageIndex":1,"pageSize":10}'),
  ],
};
const g2 = {
  txn: '事务-卷题管理新增题目',
  items: [
    sampler('06-管理员登录', '/api/user/login', loginBody),
    sampler('07-查询题目列表', '/api/admin/question/page', '{"id":null,"questionType":null,"level":null,"subjectId":null,"pageIndex":1,"pageSize":10}'),
    sampler('08-新增题目', '/api/admin/question/edit', addQuestion),
    sampler('09-复核题目列表', '/api/admin/question/page', '{"id":null,"questionType":null,"level":null,"subjectId":null,"pageIndex":1,"pageSize":10}'),
  ],
};
const g3 = {
  txn: '事务-试卷管理新增试卷',
  items: [
    sampler('10-管理员登录', '/api/user/login', loginBody),
    sampler('11-查询试卷列表', '/api/admin/exam/paper/page', '{"id":null,"level":null,"subjectId":null,"pageIndex":1,"pageSize":10}'),
    sampler('12-新增试卷', '/api/admin/exam/paper/edit', addPaper),
    sampler('13-复核试卷列表', '/api/admin/exam/paper/page', '{"id":null,"level":null,"subjectId":null,"pageIndex":1,"pageSize":10}'),
  ],
};

const jmx = `<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="学之思管理端系统性能测试-5120245300-2026" enabled="true">
      <stringProp name="TestPlan.comments">实验八 系统测试：学之思管理端性能测试。三个线程组分别覆盖「用户管理新增 2 名学生」「卷题管理新增题目」「试卷管理新增试卷」，线程数 3/1/2、循环 2/3/2；共 13 个 HTTP 采样器、3 个事务、CSV 参数化、公共 HTTP 信息头管理器、Cookie 管理器、每个采样器 2 条响应断言（HTTP 200 + 业务码 1），以及聚合报告与察看结果树监听器。</stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="用户定义的变量" enabled="true">
        <collectionProp name="Arguments.arguments">
          <elementProp name="port" elementType="Argument">
            <stringProp name="Argument.name">port</stringProp>
            <stringProp name="Argument.value">8065</stringProp>
            <stringProp name="Argument.metadata">=</stringProp>
          </elementProp>
          <elementProp name="loginPwd" elementType="Argument">
            <stringProp name="Argument.name">loginPwd</stringProp>
            <stringProp name="Argument.value">123456</stringProp>
            <stringProp name="Argument.metadata">=</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <CSVDataSet guiclass="TestBeanGUI" testclass="CSVDataSet" testname="CSV数据文件设置-新增数据" enabled="true">
        <stringProp name="filename">perf_data.csv</stringProp>
        <stringProp name="fileEncoding">UTF-8</stringProp>
        <stringProp name="variableNames">studentA,studentB,questionTitle,paperName,questionId</stringProp>
        <stringProp name="delimiter">,</stringProp>
        <boolProp name="quotedData">false</boolProp>
        <boolProp name="recycle">true</boolProp>
        <boolProp name="stopThread">false</boolProp>
        <stringProp name="shareMode">shareMode.all</stringProp>
        <boolProp name="ignoreFirstLine">false</boolProp>
      </CSVDataSet>
      <hashTree/>
      <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP信息头管理器-公共请求头" enabled="true">
        <collectionProp name="HeaderManager.headers">
          <elementProp name="Content-Type" elementType="Header">
            <stringProp name="Header.name">Content-Type</stringProp>
            <stringProp name="Header.value">application/json</stringProp>
          </elementProp>
          <elementProp name="request-ajax" elementType="Header">
            <stringProp name="Header.name">request-ajax</stringProp>
            <stringProp name="Header.value">true</stringProp>
          </elementProp>
          <elementProp name="Accept" elementType="Header">
            <stringProp name="Header.name">Accept</stringProp>
            <stringProp name="Header.value">application/json, text/plain, */*</stringProp>
          </elementProp>
          <elementProp name="User-Agent" elementType="Header">
            <stringProp name="Header.name">User-Agent</stringProp>
            <stringProp name="Header.value">Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/157.0.0.0 Safari/537.36</stringProp>
          </elementProp>
        </collectionProp>
      </HeaderManager>
      <hashTree/>

${threadGroup('线程组1-用户管理新增学生', 3, 2, 2, g1)}
${threadGroup('线程组2-卷题管理新增题目', 1, 3, 1, g2)}
${threadGroup('线程组3-试卷管理新增试卷', 2, 2, 2, g3)}

      <ResultCollector guiclass="StatVisualizer" testclass="ResultCollector" testname="聚合报告" enabled="true">
        <boolProp name="ResultCollector.error_logging">false</boolProp>
        <objProp>
          <name>saveConfig</name>
          <value class="SampleSaveConfiguration">
            <time>true</time><latency>true</latency><timestamp>true</timestamp><success>true</success><label>true</label><code>true</code><message>true</message><threadName>true</threadName><dataType>true</dataType><encoding>false</encoding><assertions>true</assertions><subresults>true</subresults><responseData>false</responseData><samplerData>false</samplerData><xml>false</xml><fieldNames>true</fieldNames><responseHeaders>false</responseHeaders><requestHeaders>false</requestHeaders><responseDataOnError>false</responseDataOnError><saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage><assertionsResultsToSave>0</assertionsResultsToSave><bytes>true</bytes><sentBytes>true</sentBytes><url>true</url><threadCounts>true</threadCounts><sampleCount>true</sampleCount><idleTime>true</idleTime><connectTime>true</connectTime>
          </value>
        </objProp>
        <stringProp name="filename">report/jmeter/statistics.csv</stringProp>
      </ResultCollector>
      <hashTree/>
      <ResultCollector guiclass="ViewResultsFullVisualizer" testclass="ResultCollector" testname="察看结果树" enabled="true">
        <boolProp name="ResultCollector.error_logging">false</boolProp>
        <objProp>
          <name>saveConfig</name>
          <value class="SampleSaveConfiguration">
            <time>true</time><latency>true</latency><timestamp>true</timestamp><success>true</success><label>true</label><code>true</code><message>true</message><threadName>true</threadName><dataType>true</dataType><encoding>false</encoding><assertions>true</assertions><subresults>true</subresults><responseData>false</responseData><samplerData>false</samplerData><xml>false</xml><fieldNames>true</fieldNames><responseHeaders>false</responseHeaders><requestHeaders>false</requestHeaders><responseDataOnError>true</responseDataOnError><saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage><assertionsResultsToSave>0</assertionsResultsToSave><bytes>true</bytes><sentBytes>true</sentBytes><url>true</url><threadCounts>true</threadCounts><sampleCount>true</sampleCount><idleTime>true</idleTime><connectTime>true</connectTime>
          </value>
        </objProp>
        <stringProp name="filename"></stringProp>
      </ResultCollector>
      <hashTree/>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
`;

fs.mkdirSync(path.join(__dirname, '..', 'jmeter'), { recursive: true });
fs.writeFileSync(path.join(__dirname, '..', 'jmeter', 'xzs_admin_perf.jmx'), jmx, 'utf8');
console.log('jmx written', jmx.length);
