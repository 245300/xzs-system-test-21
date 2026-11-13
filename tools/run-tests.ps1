# 在可见窗口中执行自动化测试，便于截取真实终端证据
$Host.UI.RawUI.WindowTitle = "XZS-Maven-Test"
Set-Location C:\5120245301
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-25.0.5.21-hotspot"
Write-Host "==== 学之思学生端 Web 自动化测试（Selenium 4.18.1 + JUnit5）====" -ForegroundColor Cyan
Write-Host "工程目录: C:\5120245301" -ForegroundColor Gray
& "D:\软件测试\Student\tools\maven\bin\mvn" -o test 2>&1 | Tee-Object -FilePath target\mvn-console.log
Write-Host ""
Write-Host "==== 执行结束，按回车关闭窗口 ====" -ForegroundColor Yellow
Read-Host
