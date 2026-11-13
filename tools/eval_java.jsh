import cn.swust.cqa.component.ComprehensiveProjectSupport;
import cn.swust.cqa.component.EvaluationHandle;
import cn.swust.cqa.domain.ExperimentScore;
import cn.swust.cqa.domain.EvnConfig;

System.setProperty("cqa.app.home", "D:\\软件测试\\Student");
var es = new ExperimentScore();
es.setExperimentName("学之思系统测试");
es.setType("COMPREHENSIVE_WEB");
es.setLanguage("java");
es.setBasePath("C:\\5120245301");
var env = new EvnConfig() {};
env.setMavenDir("D:\\软件测试\\Student\\tools\\maven");
env.setStudentName("熊锦豪");
ComprehensiveProjectSupport.syncSubRuleToEngineRuleJson("C:\\5120245301", "java");
var s = EvaluationHandle.runJavaEvaluation("C:\\5120245301", es, env);
System.out.println("RUN_JAVA_SCORE=" + s);
/exit
