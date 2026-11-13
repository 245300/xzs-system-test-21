import cn.swust.cqa.component.ComprehensiveProjectSupport;
import cn.swust.cqa.jmeter.JMeterTestEvaluator;

System.setProperty("cqa.app.home", "D:\\软件测试\\Student");
ComprehensiveProjectSupport.syncSubRuleToEngineRuleJson("C:\\5120245301", "jmeter");
var rule = JMeterTestEvaluator.evaluateScriptQuality(
    "学之思系统测试-性能测试",
    "C:\\5120245301\\jmeter\\xzs_admin_perf.jmx",
    "D:\\软件测试\\Student\\tools\\jmeter\\bin",
    "C:\\5120245301",
    "C:\\5120245301\\report\\jmeter",
    false);
System.out.println("JMETER_SCORE=" + rule.getScore());
System.out.println("threadGroup=" + rule.getReqMinThreadGroup() + " success=" + rule.getSuccessCount() + " elapsed=" + rule.getMinElapsed());
/exit
