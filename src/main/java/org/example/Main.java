package org.example;

/**
 * 自动化测试工程的入口说明。
 *
 * <p>真正的测试用例在 src/test/java 下，由 Maven Surefire 调用 JUnit 5 执行。
 * 保留 main 方法是为了方便在 IDE 中直接查看工程用途，不影响测试运行。</p>
 *
 * @author 熊锦豪
 */
public final class Main {

    private Main() {
    }

    /**
     * 打印工程说明。
     *
     * @param args 命令行参数，未使用
     */
    public static void main(String[] args) {
        // 这里只输出提示，不启动浏览器
        System.out.println("学之思学生端自动化测试工程，请运行 mvn test 执行 JUnit 5 用例。");
    }
}
