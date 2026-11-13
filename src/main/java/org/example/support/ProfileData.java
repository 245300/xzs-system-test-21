package org.example.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 学生个人资料的数据模型与校验工具。
 *
 * <p>页面上的真实姓名、年龄、手机号等字段在提交前需要做基本校验，
 * 把校验规则独立成纯 Java 类，便于用 JUnit 做参数化与边界测试。</p>
 *
 * @author 熊锦豪
 */
public final class ProfileData {

    // 手机号固定为 11 位数字
    private static final int PHONE_LENGTH = 11;

    // 年龄的合法区间，与业务约定保持一致
    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 120;

    // 出生日期使用页面上同款的日期格式
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String realName;
    private final int age;
    private final String sex;
    private final String birthDay;
    private final String phone;

    /**
     * 构造一份个人资料。
     *
     * @param realName 真实姓名
     * @param age 年龄
     * @param sex 性别
     * @param birthDay 出生日期，格式为 yyyy-MM-dd
     * @param phone 手机号
     */
    public ProfileData(String realName, int age, String sex, String birthDay, String phone) {
        this.realName = normalize(realName);
        this.age = age;
        this.sex = normalize(sex);
        this.birthDay = normalize(birthDay);
        this.phone = normalize(phone);
    }

    /**
     * 校验真实姓名：去掉首尾空格后长度必须在 2 到 20 之间。
     *
     * @param value 待校验的姓名
     * @return 合法返回 true
     */
    public static boolean isValidRealName(String value) {
        String text = normalize(value);
        // 空姓名不允许提交
        if (text.isEmpty()) {
            return false;
        }
        return text.length() >= 2 && text.length() <= 20;
    }

    /**
     * 校验年龄：必须落在 1 到 120 之间。
     *
     * @param value 待校验的年龄
     * @return 合法返回 true
     */
    public static boolean isValidAge(int value) {
        // 下界与上界都要检查，防止负数或超大值
        return value >= MIN_AGE && value <= MAX_AGE;
    }

    /**
     * 校验手机号：必须是 11 位数字且以 1 开头。
     *
     * @param value 待校验的手机号
     * @return 合法返回 true
     */
    public static boolean isValidPhone(String value) {
        String text = normalize(value);
        // 长度不符合时直接判定失败
        if (text.length() != PHONE_LENGTH) {
            return false;
        }
        // 首位必须是 1
        if (text.charAt(0) != '1') {
            return false;
        }
        // 其余位置必须是数字
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验出生日期：格式正确且不能晚于今天。
     *
     * @param value 待校验的日期
     * @return 合法返回 true
     */
    public static boolean isValidBirthDay(String value) {
        String text = normalize(value);
        // 空值直接判定失败
        if (text.isEmpty()) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(text, DATE_FORMAT);
            // 未来日期不允许作为出生日期
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            // 格式错误时按非法输入处理
            return false;
        }
    }

    /**
     * 去掉首尾空格，null 统一转换为空字符串。
     *
     * @param value 原始文本
     * @return 规范化后的文本
     */
    public static String normalize(String value) {
        // null 安全，避免调用方额外判空
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    public String getRealName() {
        return realName;
    }

    public int getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getPhone() {
        return phone;
    }
}
