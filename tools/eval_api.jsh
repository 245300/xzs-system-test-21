import cn.swust.cqa.component.ComprehensiveAdaptiveEvaluator;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

System.setProperty("cqa.app.home", "D:\\软件测试\\Student");
var dir = Paths.get("C:\\5120245301\\api");
var scripts = Files.list(dir).filter(p -> p.toString().toLowerCase().endsWith(".json")).sorted().map(Path::toString).collect(Collectors.toList());
System.out.println("SCRIPTS=" + scripts.size());
var es = new cn.swust.cqa.domain.ExperimentScore();
es.setExperimentName("学之思系统测试");
var score = ComprehensiveAdaptiveEvaluator.evaluateApi("C:\\5120245301", scripts, es, null);
System.out.println("API_SCORE=" + score);
/exit
