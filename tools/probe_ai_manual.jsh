import cn.swust.cqa.ai.AiEvaluationFactory;
import cn.swust.cqa.ai.AiEvalutionData;
import cn.swust.cqa.ai.SubAiEvalutionData;
import cn.swust.cqa.component.ComprehensiveProjectSupport;
import cn.swust.cqa.rule.AiRule;

System.setProperty("cqa.app.home", "D:\\软件测试\\Student");
cn.swust.cqa.ai.tongyi.TongyiConfig.loadConfig();
var ai = AiEvaluationFactory.createAiEvaluation();
AiRule rule = ComprehensiveProjectSupport.loadAiRuleForSub("C:\\5120245301", "manualTest");
AiEvalutionData d = ai.evaluation(
    "C:\\5120245301\\doc\\学之思系统手工测试记录.xlsx",
    "C:\\5120245301", "熊锦豪", "学之思系统测试", rule, false);
System.out.println("SCORE=" + d.getScore());
System.out.println("COMMENTS=" + d.getComments());
if (d.getDetails() != null) {
    for (SubAiEvalutionData s : d.getDetails()) {
        System.out.println("DIM=" + s.getLabel() + " | " + s.getSubScore());
    }
}
/exit
