param(
    [Parameter(Mandatory = $true)][string]$Action,
    [int]$X = 0, [int]$Y = 0,
    [int]$X2 = 0, [int]$Y2 = 0,
    [string]$Text = ""
)

Add-Type @"
using System;
using System.Runtime.InteropServices;
public class NativeInput {
    [DllImport("user32.dll")] public static extern bool SetCursorPos(int x, int y);
    [DllImport("user32.dll")] public static extern void mouse_event(uint f, uint dx, uint dy, uint d, UIntPtr e);
    [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
    [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr h, int n);
    [DllImport("user32.dll")] public static extern bool IsIconic(IntPtr h);
    [DllImport("user32.dll")] public static extern IntPtr GetForegroundWindow();
    [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr h);
    [DllImport("user32.dll")] public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, UIntPtr dwExtraInfo);
    public const uint LEFTDOWN = 0x0002, LEFTUP = 0x0004, RIGHTDOWN = 0x0008, RIGHTUP = 0x0010;
}
"@

function Move-To([int]$px, [int]$py) {
    [NativeInput]::SetCursorPos($px, $py) | Out-Null
    Start-Sleep -Milliseconds 120
}

function Focus-Target([string]$procName) {
    $p = Get-Process -Name $procName -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -ne "" } | Select-Object -First 1
    if (-not $p) { Write-Output "focus: target not found"; return $false }
    if ([NativeInput]::IsIconic($p.MainWindowHandle)) {
        [NativeInput]::ShowWindow($p.MainWindowHandle, 9) | Out-Null   # SW_RESTORE
        Start-Sleep -Milliseconds 600
    }
    for ($i = 0; $i -lt 4; $i++) {
        # 先敲一下 Alt：Windows 允许“刚收到输入的进程”切换前台
        [NativeInput]::keybd_event(0x12, 0, 0, [UIntPtr]::Zero)
        [NativeInput]::keybd_event(0x12, 0, 2, [UIntPtr]::Zero)
        Start-Sleep -Milliseconds 120
        [NativeInput]::BringWindowToTop($p.MainWindowHandle) | Out-Null
        [NativeInput]::SetForegroundWindow($p.MainWindowHandle) | Out-Null
        Start-Sleep -Milliseconds 450
        if ([NativeInput]::GetForegroundWindow() -eq $p.MainWindowHandle) { return $true }
    }
    Write-Output "focus: failed to take foreground"
    return $false
}

function Click-At([int]$px, [int]$py, [bool]$right = $false) {
    Move-To $px $py
    if ($right) {
        [NativeInput]::mouse_event([NativeInput]::RIGHTDOWN, 0, 0, 0, [UIntPtr]::Zero)
        Start-Sleep -Milliseconds 60
        [NativeInput]::mouse_event([NativeInput]::RIGHTUP, 0, 0, 0, [UIntPtr]::Zero)
    } else {
        [NativeInput]::mouse_event([NativeInput]::LEFTDOWN, 0, 0, 0, [UIntPtr]::Zero)
        Start-Sleep -Milliseconds 60
        [NativeInput]::mouse_event([NativeInput]::LEFTUP, 0, 0, 0, [UIntPtr]::Zero)
    }
    Start-Sleep -Milliseconds 350
}

switch ($Action) {
    "focus"   { Write-Output ("focus=" + (Focus-Target "Apifox")) }
    "click"   { Focus-Target "Apifox" | Out-Null; Click-At $X $Y $false }
    "rclick"  { Focus-Target "Apifox" | Out-Null; Click-At $X $Y $true }
    "dclick"  { Focus-Target "Apifox" | Out-Null; Click-At $X $Y $false; Click-At $X $Y $false }
    "click2"  { Focus-Target "Apifox" | Out-Null; Click-At $X $Y $false; Click-At $X2 $Y2 $false }
    "move"    { Move-To $X $Y }
    "drag" {
        Move-To $X $Y
        [NativeInput]::mouse_event([NativeInput]::LEFTDOWN, 0, 0, 0, [UIntPtr]::Zero)
        Start-Sleep -Milliseconds 200
        $steps = 12
        for ($i = 1; $i -le $steps; $i++) {
            $mx = [int]($X + ($X2 - $X) * $i / $steps)
            $my = [int]($Y + ($Y2 - $Y) * $i / $steps)
            [NativeInput]::SetCursorPos($mx, $my) | Out-Null
            Start-Sleep -Milliseconds 40
        }
        Start-Sleep -Milliseconds 200
        [NativeInput]::mouse_event([NativeInput]::LEFTUP, 0, 0, 0, [UIntPtr]::Zero)
        Start-Sleep -Milliseconds 400
    }
    "type" {
        Add-Type -AssemblyName System.Windows.Forms
        [System.Windows.Forms.SendKeys]::SendWait($Text)
        Start-Sleep -Milliseconds 300
    }
    "enter" {
        Add-Type -AssemblyName System.Windows.Forms
        [System.Windows.Forms.SendKeys]::SendWait("{ENTER}")
        Start-Sleep -Milliseconds 400
    }
    "shot" {
        Add-Type -AssemblyName System.Windows.Forms, System.Drawing
        $b = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
        $bmp = New-Object System.Drawing.Bitmap $b.Width, $b.Height
        $g = [System.Drawing.Graphics]::FromImage($bmp)
        $g.CopyFromScreen($b.X, $b.Y, 0, 0, $bmp.Size)
        $bmp.Save($Text, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Output "saved $Text"
    }
    default { Write-Output "unknown action $Action" }
}
