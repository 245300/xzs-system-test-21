package demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 脚手架自带 Calculator 的单元测试。
 *
 * <p>保留该测试是为了让工程内的示例代码也有用例覆盖，
 * 避免未覆盖类拉低整体覆盖率。</p>
 *
 * @author 熊锦豪
 */
public class CalculatorTest {

    /**
     * 两个正数相加应返回正确结果。
     */
    @Test
    @DisplayName("正数相加")
    void shouldAddPositiveNumbers() {
        assertEquals(5, new Calculator().add(2, 3));
    }

    /**
     * 包含负数时也应返回正确结果。
     */
    @Test
    @DisplayName("包含负数相加")
    void shouldAddNegativeNumbers() {
        assertEquals(-1, new Calculator().add(2, -3));
    }
}
