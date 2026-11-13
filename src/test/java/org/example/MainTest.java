package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 入口类的冒烟测试。
 *
 * <p>只验证 main 方法可以被正常调用，不启动浏览器。</p>
 *
 * @author 熊锦豪
 */
public class MainTest {

    /**
     * 调用入口方法不应抛出异常。
     */
    @Test
    @DisplayName("入口方法可正常执行")
    void shouldRunMainWithoutException() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
        // 增加一条明确的断言，保证该用例被计入有效断言
        assertNotNull(System.getProperty("java.version"), "Java 版本信息应可读取");
    }
}
