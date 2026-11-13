package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.example.page.ProfilePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 学之思学生端登录、首页与核心页面的 Selenium 自动化测试。
 *
 * <p>主流程对应实验要求：打开登录页，输入用户名和密码，点击登录进入首页，
 * 点击头像进入个人中心，切换到资料修改，依次填写真实姓名、年龄、性别、
 * 出生日期、手机号，点击更新并校验结果。其余用例覆盖登录异常与主要菜单页。</p>
 *
 * @author 熊锦豪
 */
public class StudentProfileTest {

    // 本地部署的学生端入口，默认使用本地环境保证可重复执行
    private static final String LOCAL_LOGIN_URL = "http://127.0.0.1:8065/student/index.html";

    // 课程远端学生端入口，网络可用时通过 -Dxzs.url 切换
    private static final String REMOTE_LOGIN_URL = "https://www.mindskip.net:7001/";

    // 学生端可用的测试账号
    private static final String USER_NAME = "student";
    private static final String PASSWORD = "123456";

    // 浏览器驱动，由 JUnit 生命周期方法创建和销毁
    private WebDriver driver;

    // 显式等待对象，所有元素查找都经过它
    private WebDriverWait wait;

    /**
     * 每个用例执行前启动 Chrome，并指定驱动位置。
     */
    @BeforeEach
    void setUp() {
        // 显式声明驱动路径，避免依赖系统环境变量
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // 使用真实窗口运行，避免部分站点对无头浏览器做访问限制
        options.addArguments("--window-size=1600,1000", "--disable-gpu", "--no-sandbox",
                "--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * 每个用例结束后关闭浏览器，释放会话。
     */
    @AfterEach
    void tearDown() {
        // 驱动可能因异常未创建，先判空再退出
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 打开学生端登录页，输入账号密码并点击登录。
     */
    private void login() {
        String loginUrl = System.getProperty("xzs.url", LOCAL_LOGIN_URL);
        driver.get(loginUrl);
        // 根节点出现说明前端应用已经挂载
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("app")));

        WebElement userNameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='用户名']")));
        userNameInput.clear();
        userNameInput.sendKeys(USER_NAME);

        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='密码']")));
        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., '登录')]"))).click();
        // 登录成功后地址切换到学生首页
        wait.until(ExpectedConditions.urlContains("#/index"));
    }

    /**
     * 登录后直接进入学生端的某个路由页面。
     *
     * @param route 形如 #/paper/index 的路由
     */
    private void openRoute(String route) {
        driver.get(LOCAL_LOGIN_URL + route);
        wait.until(ExpectedConditions.urlContains(route));
    }

    /**
     * 等待页面正文出现指定文字。
     *
     * @param text 期望出现的文字
     */
    private void waitForText(String text) {
        wait.until(d -> d.findElement(By.tagName("body")).getText().contains(text));
    }

    /**
     * 验证学生使用正确账号可以登录并进入首页。
     */
    @Test
    @DisplayName("AT-01 学生登录成功后进入首页")
    void shouldLoginAndReachHomePage() {
        login();
        assertTrue(driver.getCurrentUrl().contains("#/index"), "登录后地址应跳转到学生首页");
        waitForText("任务中心");
        // 远端地址只作为环境说明保留，不参与本地执行
        assertEquals(true, REMOTE_LOGIN_URL.startsWith("https://"), "远端入口应为 HTTPS 地址");
    }

    /**
     * 验证错误密码会被拒绝，并给出统一提示。
     */
    @Test
    @DisplayName("AT-02 错误密码登录被拒绝")
    void shouldRejectWrongPassword() {
        driver.get(LOCAL_LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("app")));
        WebElement userNameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='用户名']")));
        userNameInput.sendKeys(USER_NAME);
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='密码']"));
        passwordInput.sendKeys("wrong123");
        driver.findElement(By.xpath("//button[contains(., '登录')]")).click();

        assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".el-message"), "用户名或密码错误")), "应提示用户名或密码错误");
        assertTrue(driver.getCurrentUrl().contains("#/login"), "登录失败后应停留在登录页");
    }

    /**
     * 验证空用户名会被拒绝。
     */
    @Test
    @DisplayName("AT-03 空用户名登录被拒绝")
    void shouldRejectEmptyUsername() {
        driver.get(LOCAL_LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("app")));
        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='密码']")));
        passwordInput.sendKeys(PASSWORD);
        driver.findElement(By.xpath("//button[contains(., '登录')]")).click();

        assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".el-message"), "用户名或密码错误")), "应提示用户名或密码错误");
        assertFalse(driver.getCurrentUrl().contains("#/index"), "空用户名不得进入首页");
    }

    /**
     * 验证登录后可以修改个人资料，并出现成功提示。
     *
     * @throws IOException 截图目录创建失败时抛出
     */
    @Test
    @DisplayName("AT-04 学生登录后修改个人资料")
    void shouldUpdateStudentProfile() throws IOException {
        login();

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.openFromAvatar();
        assertTrue(profilePage.openProfileTab(), "资料修改表单应可见");

        profilePage.fillRealName("学生")
                .fillAge("18")
                .fillPhone("13800138000");
        profilePage.selectSex("男");
        profilePage.fillBirthDay("2019-09-01");
        profilePage.clickUpdate();

        assertTrue(profilePage.waitForUpdateSuccess(), "提交后应出现成功提示");
        assertEquals("学生", profilePage.readRealName(), "表单中的真实姓名应与提交值一致");

        // 保存运行截图，作为测试报告中的执行证据
        saveScreenshot("doc/screenshots/自动化-资料修改成功.png");
    }

    /**
     * 验证试卷中心展示固定试卷。
     */
    @Test
    @DisplayName("AT-05 试卷中心展示固定试卷")
    void shouldListFixedPapers() {
        login();
        openRoute("#/paper/index");
        waitForText("一年级语文基础测试");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("固定试卷"), "应展示固定试卷分类");
    }

    /**
     * 验证考试记录页展示已提交的答卷。
     */
    @Test
    @DisplayName("AT-06 考试记录展示已完成答卷")
    void shouldOpenExamRecord() {
        login();
        openRoute("#/record/index");
        waitForText("一年级语文基础测试");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("完成"), "记录状态应为完成");
    }

    /**
     * 验证错题本展示错题与正确答案。
     */
    @Test
    @DisplayName("AT-07 错题本展示错题与正确答案")
    void shouldShowWrongQuestion() {
        login();
        openRoute("#/question/index");
        waitForText("1+1等于几");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("正确答案"), "应展示正确答案");
    }

    /**
     * 验证个人中心展示学生信息。
     */
    @Test
    @DisplayName("AT-08 个人中心展示学生信息")
    void shouldOpenProfileCenter() {
        login();
        openRoute("#/user/index");
        waitForText("个人信息");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("注册时间"), "应展示注册时间");
    }

    /**
     * 验证消息中心展示消息列表。
     */
    @Test
    @DisplayName("AT-09 消息中心展示消息列表")
    void shouldShowMessageCenter() {
        login();
        openRoute("#/user/message");
        waitForText("期中考试安排");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("未读"), "应展示消息的未读状态");
    }

    /**
     * 验证退出登录后回到登录页。
     */
    @Test
    @DisplayName("AT-10 退出登录回到登录页")
    void shouldLogout() {
        login();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".el-badge"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class, 'el-dropdown-menu__item') and contains(., '退出')]"))).click();
        wait.until(ExpectedConditions.urlContains("#/login"));
        assertTrue(driver.getCurrentUrl().contains("#/login"), "退出后应回到登录页");
    }

    /**
     * 把当前浏览器画面保存到工程目录。
     *
     * @param relativePath 相对于工程根目录的图片路径
     * @throws IOException 文件写入失败时抛出
     */
    private void saveScreenshot(String relativePath) throws IOException {
        Path target = Path.of(System.getProperty("user.dir"), relativePath);
        Files.createDirectories(target.getParent());
        // 浏览器的截图先落到临时文件，再复制到目标位置
        Path source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
