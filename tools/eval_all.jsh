import cn.swust.cqa.component.ComprehensiveProjectSupport;
import cn.swust.cqa.component.ComprehensiveAdaptiveEvaluator;
import cn.swust.cqa.component.EvaluationHandle;
import cn.swust.cqa.component.ComprehensiveReportWriter;
import cn.swust.cqa.domain.ExperimentScore;
import cn.swust.cqa.domain.EvnConfig;
import cn.swust.cqa.jmeter.JMeterTestEvaluator;
import cn.swust.cqa.ai.AiEvaluationFactory;
import cn.swust.cqa.rule.AiRule;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

String PRJ = "C:\\5120245301";
System.setProperty("cqa.app.home", "D:\\软件测试\\Student");

var es = new ExperimentScore();
es.setExperimentName("学之思系统测试");
es.setType("COMPREHENSIVE_WEB");
es.setLanguage("java");
es.setBasePath(PRJ);

var env = new EvnConfig() {};
env.setMavenDir("D:\\软件测试\\Student\\tools\\maven");
env.setJmeterDir("D:\\软件测试\\Student\\tools\\jmeter");
env.setStudentName("熊锦豪");

// 1) java
ComprehensiveProjectSupport.syncSubRuleToEngineRuleJson(PRJ, "java");
Float javaScore = EvaluationHandle.runJavaEvaluation(PRJ, es, env);
System.out.println("SUB java = " + javaScore);
System.out.println("AFTER java = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "java", javaScore));

// 2) api
ComprehensiveProjectSupport.syncSubRuleToEngineRuleJson(PRJ, "api");
var apiDir = Paths.get(PRJ, "api");
var scripts = Files.list(apiDir).filter(p -> p.toString().toLowerCase().endsWith(".json"))
        .sorted().map(Path::toString).collect(Collectors.toList());
Float apiScore = ComprehensiveAdaptiveEvaluator.evaluateApi(PRJ, scripts, es, env);
System.out.println("SUB api = " + apiScore);
System.out.println("AFTER api = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "api", apiScore));

// 3) jmeter
ComprehensiveProjectSupport.syncSubRuleToEngineRuleJson(PRJ, "jmeter");
var jm = JMeterTestEvaluator.evaluateScriptQuality("学之思系统测试-性能测试",
        PRJ + "\\jmeter\\xzs_admin_perf.jmx",
        "D:\\软件测试\\Student\\tools\\jmeter\\bin",
        PRJ, PRJ + "\\report\\jmeter", false);
Float jmeterScore = jm.getScore();
System.out.println("SUB jmeter = " + jmeterScore);
System.out.println("AFTER jmeter = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "jmeter", jmeterScore));

// 4) manualTest  (AI)
cn.swust.cqa.ai.tongyi.TongyiConfig.loadConfig();
var ai = AiEvaluationFactory.createAiEvaluation();
AiRule manualRule = ComprehensiveProjectSupport.loadAiRuleForSub(PRJ, "manualTest");
var manualData = ai.evaluation(PRJ + "\\doc\\学之思系统手工测试记录.xlsx", PRJ, "熊锦豪", "学之思系统测试", manualRule, false);
Float manualScore = manualData.getScore();
System.out.println("SUB manualTest = " + manualScore);
System.out.println("AFTER manualTest = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "manualTest", manualScore));

// 5) testReport (AI)
AiRule reportRule = ComprehensiveProjectSupport.loadAiRuleForSub(PRJ, "testReport");
var reportData = ai.evaluation(PRJ + "\\doc\\学之思系统综合测试报告.md", PRJ, "熊锦豪", "学之思系统测试", reportRule, false);
Float reportScore = reportData.getScore();
System.out.println("SUB testReport = " + reportScore);
System.out.println("AFTER testReport = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "testReport", reportScore));

System.out.println("MERGED = " + ComprehensiveProjectSupport.mergeScores(PRJ));
/exit
