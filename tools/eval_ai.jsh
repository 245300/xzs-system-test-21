import cn.swust.cqa.component.ComprehensiveProjectSupport;
import cn.swust.cqa.ai.AiEvaluationFactory;
import cn.swust.cqa.rule.AiRule;
import cn.swust.cqa.ai.tongyi.TongyiConfig;

String PRJ = "C:\\5120245301";
System.setProperty("cqa.app.home", "D:\\软件测试\\Student");

TongyiConfig.loadConfig();
var ai = AiEvaluationFactory.createAiEvaluation();

AiRule mRule = ComprehensiveProjectSupport.loadAiRuleForSub(PRJ, "manualTest");
var mData = ai.evaluation(PRJ + "\\doc\\学之思系统手工测试记录.xlsx", PRJ, "熊锦豪", "学之思系统测试", mRule, false);
Float mScore = mData.getScore();
System.out.println("SUB manualTest = " + mScore);
System.out.println("AFTER manualTest = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "manualTest", mScore));

AiRule rRule = ComprehensiveProjectSupport.loadAiRuleForSub(PRJ, "testReport");
var rData = ai.evaluation(PRJ + "\\doc\\学之思系统综合测试报告.md", PRJ, "熊锦豪", "学之思系统测试", rRule, false);
Float rScore = rData.getScore();
System.out.println("SUB testReport = " + rScore);
System.out.println("AFTER testReport = " + ComprehensiveProjectSupport.afterSubItemRun(PRJ, "testReport", rScore));

System.out.println("MERGED = " + ComprehensiveProjectSupport.mergeScores(PRJ));
ComprehensiveProjectSupport.writeTotalScoreReport(PRJ);
System.out.println("REPORT WRITTEN");
/exit
