package org.example.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 学之思学生端个人中心的页面对象。
 *
 * <p>覆盖从右上角头像进入个人中心、切换到资料修改页、填写资料并提交的完整操作。
 * 表单控件的 id 由前端框架动态生成，因此统一按标签文字定位。</p>
 *
 * @author 熊锦豪
 */
public class ProfilePage {

    // 显式等待的最长时间
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final WebDriver driver;
    private final WebDriverWait wait;

    /**
     * 创建个人中心页面对象。
     *
     * @param driver 浏览器驱动
     */
    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /**
     * 点击右上角头像，并在下拉菜单中点击个人中心。
     */
    public void openFromAvatar() {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".el-badge"))).click();
        // 下拉菜单会被挂载到 body 末尾，等它可见后再点击
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class, 'el-dropdown-menu__item') and contains(., '个人中心')]"))).click();
    }

    /**
     * 在个人中心点击资料修改页签。
     *
     * @return 页签切换成功返回 true
     */
    public boolean openProfileTab() {
        wait.until(ExpectedConditions.urlContains("/user/index"));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'el-tabs__item') and contains(., '资料修改')]"))).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(., '真实姓名')]"))).isDisplayed();
    }

    /**
     * 填写真实姓名。
     *
     * @param realName 真实姓名
     * @return 当前页面对象
     */
    public ProfilePage fillRealName(String realName) {
        WebElement input = inputByLabel("真实姓名");
        input.clear();
        input.sendKeys(realName);
        return this;
    }

    /**
     * 填写年龄。
     *
     * @param age 年龄
     * @return 当前页面对象
     */
    public ProfilePage fillAge(String age) {
        WebElement input = inputByLabel("年龄");
        input.clear();
        input.sendKeys(age);
        return this;
    }

    /**
     * 选择性别。
     *
     * @param sex 性别文字，例如 男、女
     */
    public void selectSex(String sex) {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='性别']"))).click();
        // 下拉选项由前端渲染在 body 中，按可见文字精确匹配
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class, 'el-select-dropdown__item')][.//span[text()='" + sex + "']]"))).click();
    }

    /**
     * 填写出生日期。
     *
     * @param birthDay 形如 2019-09-01 的日期
     */
    public void fillBirthDay(String birthDay) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='选择日期']")));
        input.clear();
        input.sendKeys(birthDay);
        // 日期控件需要回车确认输入值
        input.sendKeys(Keys.ENTER);
    }

    /**
     * 填写手机号。
     *
     * @param phone 手机号
     * @return 当前页面对象
     */
    public ProfilePage fillPhone(String phone) {
        WebElement input = inputByLabel("手机");
        input.clear();
        input.sendKeys(phone);
        return this;
    }

    /**
     * 点击更新按钮提交资料。
     */
    public void clickUpdate() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., '更新')]"))).click();
    }

    /**
     * 等待更新成功的提示。
     *
     * @return 出现成功提示返回 true
     */
    public boolean waitForUpdateSuccess() {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".el-message"), "成功"));
    }

    /**
     * 读取当前表单中的真实姓名。
     *
     * @return 真实姓名输入框的值
     */
    public String readRealName() {
        return inputByLabel("真实姓名").getAttribute("value");
    }

    /**
     * 按表单标签定位其后的第一个输入框。
     *
     * @param label 表单标签文字
     * @return 输入框元素
     */
    private WebElement inputByLabel(String label) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(., '" + label + "')]/following::input[1]")));
    }
}
