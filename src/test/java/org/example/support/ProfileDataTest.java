package org.example.support;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 个人资料校验规则的单元测试。
 *
 * <p>使用 JUnit 5 的参数化测试覆盖正常值、边界值和异常值，
 * 保证页面提交前的校验逻辑与业务约定一致。</p>
 *
 * @author 熊锦豪
 */
public class ProfileDataTest {

    /**
     * 合法姓名应通过校验。
     *
     * @param name 姓名
     */
    @ParameterizedTest
    @DisplayName("合法姓名通过校验")
    @ValueSource(strings = {"张三", "欧阳修", "Li Hua", "一二三四五六七八九十"})
    void shouldAcceptValidRealName(String name) {
        assertTrue(ProfileData.isValidRealName(name));
    }

    /**
     * 空姓名与超长姓名应被拒绝。
     *
     * @param name 非法姓名
     */
    @ParameterizedTest
    @DisplayName("非法姓名被拒绝")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "张", "一二三四五六七八九十一二三四五六七八九十一"})
    void shouldRejectInvalidRealName(String name) {
        assertFalse(ProfileData.isValidRealName(name));
    }

    /**
     * 年龄边界值应通过校验。
     *
     * @param age 年龄
     */
    @ParameterizedTest
    @DisplayName("年龄边界值通过校验")
    @ValueSource(ints = {1, 18, 60, 120})
    void shouldAcceptAgeInRange(int age) {
        assertTrue(ProfileData.isValidAge(age));
    }

    /**
     * 超出范围的年龄应被拒绝。
     *
     * @param age 年龄
     */
    @ParameterizedTest
    @DisplayName("越界年龄被拒绝")
    @ValueSource(ints = {-1, 0, 121, 999})
    void shouldRejectAgeOutOfRange(int age) {
        assertFalse(ProfileData.isValidAge(age));
    }

    /**
     * 合法手机号应通过校验。
     *
     * @param phone 手机号
     */
    @ParameterizedTest
    @DisplayName("合法手机号通过校验")
    @ValueSource(strings = {"13800138000", "19912345678", "15880088200"})
    void shouldAcceptValidPhone(String phone) {
        assertTrue(ProfileData.isValidPhone(phone));
    }

    /**
     * 位数不足、首位非 1、含非数字的手机号应被拒绝。
     *
     * @param phone 手机号
     */
    @ParameterizedTest
    @DisplayName("非法手机号被拒绝")
    @NullAndEmptySource
    @ValueSource(strings = {"158800882", "23800138000", "1380013800a", "138001380001"})
    void shouldRejectInvalidPhone(String phone) {
        assertFalse(ProfileData.isValidPhone(phone));
    }

    /**
     * 合法出生日期应通过校验。
     *
     * @param birthDay 日期
     */
    @ParameterizedTest
    @DisplayName("合法出生日期通过校验")
    @ValueSource(strings = {"2019-09-01", "2000-01-01", "1999-12-31"})
    void shouldAcceptValidBirthDay(String birthDay) {
        assertTrue(ProfileData.isValidBirthDay(birthDay));
    }

    /**
     * 空值、格式错误与未来日期应被拒绝。
     *
     * @param birthDay 日期
     */
    @ParameterizedTest
    @DisplayName("非法出生日期被拒绝")
    @NullAndEmptySource
    @ValueSource(strings = {"", "2019/09/01", "2030-01-01", "2019-13-01"})
    void shouldRejectInvalidBirthDay(String birthDay) {
        assertFalse(ProfileData.isValidBirthDay(birthDay));
    }

    /**
     * 规范化方法应去掉首尾空格并处理 null。
     *
     * @param raw 原始文本
     * @param expected 期望结果
     */
    @ParameterizedTest
    @DisplayName("文本规范化")
    @CsvSource({"' 张三 ','张三'", "'student','student'", "'   ',''"})
    void shouldNormalizeText(String raw, String expected) {
        assertEquals(expected, ProfileData.normalize(raw));
    }

    /**
     * null 应被规范化为空字符串。
     */
    @Test
    @DisplayName("null 规范化为空字符串")
    void shouldNormalizeNullToEmpty() {
        assertEquals("", ProfileData.normalize(null));
    }

    /**
     * 两个字符是姓名长度的下边界。
     */
    @Test
    @DisplayName("两个字符的姓名合法")
    void shouldAcceptTwoCharacterRealName() {
        assertTrue(ProfileData.isValidRealName("张三"));
    }

    /**
     * 单个字符低于姓名长度下边界。
     */
    @Test
    @DisplayName("单字符姓名非法")
    void shouldRejectOneCharacterRealName() {
        assertFalse(ProfileData.isValidRealName("张"));
    }

    /**
     * 二十个字符是姓名长度的上边界。
     */
    @Test
    @DisplayName("二十个字符的姓名合法")
    void shouldAcceptTwentyCharacterRealName() {
        assertTrue(ProfileData.isValidRealName("一二三四五六七八九十一二三四五六七八九十"));
    }

    /**
     * 超过二十个字符的姓名应被拒绝。
     */
    @Test
    @DisplayName("二十一个字符的姓名非法")
    void shouldRejectTwentyOneCharacterRealName() {
        assertFalse(ProfileData.isValidRealName("一二三四五六七八九十一二三四五六七八九十一"));
    }

    /**
     * 纯空格姓名规范化后为空，不应通过校验。
     */
    @Test
    @DisplayName("纯空格姓名非法")
    void shouldRejectBlankRealName() {
        assertFalse(ProfileData.isValidRealName("   "));
    }

    /**
     * 姓名首尾空格应在校验前被规范化。
     */
    @Test
    @DisplayName("带首尾空格的姓名合法")
    void shouldAcceptRealNameWithSpaces() {
        assertTrue(ProfileData.isValidRealName(" 张三 "));
    }

    /**
     * 年龄下边界 1 岁应通过校验。
     */
    @Test
    @DisplayName("最小年龄 1 岁合法")
    void shouldAcceptMinimumAge() {
        assertTrue(ProfileData.isValidAge(1));
    }

    /**
     * 年龄上边界 120 岁应通过校验。
     */
    @Test
    @DisplayName("最大年龄 120 岁合法")
    void shouldAcceptMaximumAge() {
        assertTrue(ProfileData.isValidAge(120));
    }

    /**
     * 0 岁低于年龄下边界。
     */
    @Test
    @DisplayName("0 岁非法")
    void shouldRejectZeroAge() {
        assertFalse(ProfileData.isValidAge(0));
    }

    /**
     * 121 岁超过年龄上边界。
     */
    @Test
    @DisplayName("121 岁非法")
    void shouldRejectAgeAboveMaximum() {
        assertFalse(ProfileData.isValidAge(121));
    }

    /**
     * 11 位且以 1 开头的数字手机号合法。
     */
    @Test
    @DisplayName("以 1 开头的 11 位手机号合法")
    void shouldAcceptPhoneStartingWithOne() {
        assertTrue(ProfileData.isValidPhone("10000000000"));
    }

    /**
     * 10 位手机号位数不足。
     */
    @Test
    @DisplayName("10 位手机号非法")
    void shouldRejectPhoneWithTenDigits() {
        assertFalse(ProfileData.isValidPhone("1380013800"));
    }

    /**
     * 12 位手机号位数超长。
     */
    @Test
    @DisplayName("12 位手机号非法")
    void shouldRejectPhoneWithTwelveDigits() {
        assertFalse(ProfileData.isValidPhone("138001380001"));
    }

    /**
     * 首位不是 1 的手机号应被拒绝。
     */
    @Test
    @DisplayName("首位为 2 的手机号非法")
    void shouldRejectPhoneStartingWithTwo() {
        assertFalse(ProfileData.isValidPhone("23800138000"));
    }

    /**
     * 当天日期可以作为出生日期。
     */
    @Test
    @DisplayName("今天可以作为出生日期")
    void shouldAcceptBirthDayToday() {
        assertTrue(ProfileData.isValidBirthDay(LocalDate.now().toString()));
    }

    /**
     * 明天晚于今天，不能作为出生日期。
     */
    @Test
    @DisplayName("明天不能作为出生日期")
    void shouldRejectBirthDayTomorrow() {
        assertFalse(ProfileData.isValidBirthDay(LocalDate.now().plusDays(1).toString()));
    }

    /**
     * 使用斜杠分隔的日期格式不符合页面约定。
     */
    @Test
    @DisplayName("斜杠日期格式非法")
    void shouldRejectBirthDayWithSlash() {
        assertFalse(ProfileData.isValidBirthDay("2019/09/01"));
    }

    /**
     * 月份为 13 的日期不是合法日历日期。
     */
    @Test
    @DisplayName("月份越界日期非法")
    void shouldRejectBirthDayWithInvalidMonth() {
        assertFalse(ProfileData.isValidBirthDay("2019-13-01"));
    }

    /**
     * 规范化应去掉首尾空格但保留中间内容。
     */
    @Test
    @DisplayName("规范化去除首尾空格")
    void shouldNormalizeLeadingAndTrailingSpaces() {
        assertEquals("张 三", ProfileData.normalize("  张 三  "));
    }

    /**
     * 构造器应保存规范化后的资料，getter 返回同一份数据。
     */
    @Test
    @DisplayName("构造器与 getter 返回资料")
    void shouldKeepProfileValues() {
        ProfileData data = new ProfileData(" 学生 ", 18, " 男 ", "2019-09-01", "158800882");
        assertEquals("学生", data.getRealName());
        assertEquals(18, data.getAge());
        assertEquals("男", data.getSex());
        assertEquals("2019-09-01", data.getBirthDay());
        assertEquals("158800882", data.getPhone());
    }
}
