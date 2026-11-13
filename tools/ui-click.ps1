param(
    [string]$Action = "",
    [int]$X = 0, [int]$Y = 0,
    [string]$Text = "",
    [string]$Script = ""
)

Add-Type -AssemblyName System.Windows.Forms
Add-Type @"
using System;
using System.Runtime.InteropServices;
public class NI {
    [DllImport("user32.dll")] public static extern bool SetCursorPos(int x, int y);
    [DllImport("user32.dll")] public static extern void mouse_event(uint f, uint dx, uint dy, uint d, UIntPtr e);
    [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
    [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr h, int n);
    [DllImport("user32.dll")] public static extern bool IsIconic(IntPtr h);
    [DllImport("user32.dll")] public static extern IntPtr GetForegroundWindow();
    [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr h);
    [DllImport("user32.dll")] public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, UIntPtr dwExtraInfo);
    [StructLayout(LayoutKind.Sequential)] public struct MOUSEINPUT { public int dx; public int dy; public uint mouseData; public uint dwFlags; public uint time; public IntPtr dwExtraInfo; }
    [StructLayout(LayoutKind.Explicit)] public struct INPUTUNION { [FieldOffset(0)] public MOUSEINPUT mi; }
    [StructLayout(LayoutKind.Sequential)] public struct INPUT { public uint type; public INPUTUNION u; }
    [DllImport("user32.dll", SetLastError=true)] public static extern uint SendInput(uint n, INPUT[] p, int size);
    public const uint MOVE = 0x0001, ABSOLUTE = 0x8000, IDOWN = 0x0002, IUP = 0x0004;
    public const uint LD = 0x0002, LU = 0x0004;
}
"@

function Focus-Apifox {
    $p = Get-Process -Name Apifox -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -ne "" } | Select-Object -First 1
    if (-not $p) { Write-Output "no apifox"; return $false }
    if ([NI]::IsIconic($p.MainWindowHandle)) { [NI]::ShowWindow($p.MainWindowHandle, 9) | Out-Null; Start-Sleep -Milliseconds 500 }
    for ($i = 0; $i -lt 5; $i++) {
        [NI]::keybd_event(0x12, 0, 0, [UIntPtr]::Zero)
        [NI]::keybd_event(0x12, 0, 2, [UIntPtr]::Zero)
        Start-Sleep -Milliseconds 100
        [NI]::BringWindowToTop($p.MainWindowHandle) | Out-Null
        [NI]::SetForegroundWindow($p.MainWindowHandle) | Out-Null
        Start-Sleep -Milliseconds 350
        if ([NI]::GetForegroundWindow() -eq $p.MainWindowHandle) { return $true }
    }
    return $false
}

function Hide-Others {
    Get-Process | Where-Object { $_.MainWindowTitle -ne "" -and $_.ProcessName -ne "Apifox" -and $_.ProcessName -ne "java" } |
        ForEach-Object { [NI]::ShowWindow($_.MainWindowHandle, 6) | Out-Null }
    Start-Sleep -Milliseconds 400
}

function Do-Click([int]$px, [int]$py) {
    [NI]::SetCursorPos($px, $py) | Out-Null
    Start-Sleep -Milliseconds 150
    [NI]::mouse_event([NI]::LD, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 70
    [NI]::mouse_event([NI]::LU, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 500
}

function Do-AtomicClick([int]$px, [int]$py) {
    $sw = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds.Width
    $sh = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds.Height
    $ax = [int](($px * 65535) / $sw)
    $ay = [int](($py * 65535) / $sh)
    $inputs = New-Object 'NI+INPUT[]' 3
    for ($i = 0; $i -lt 3; $i++) { $inputs[$i].type = 0 }
    $inputs[0].u.mi.dx = $ax; $inputs[0].u.mi.dy = $ay
    $inputs[0].u.mi.dwFlags = ([NI]::MOVE -bor [NI]::ABSOLUTE)
    $inputs[1].u.mi.dx = $ax; $inputs[1].u.mi.dy = $ay
    $inputs[1].u.mi.dwFlags = ([NI]::MOVE -bor [NI]::ABSOLUTE -bor [NI]::IDOWN)
    $inputs[2].u.mi.dx = $ax; $inputs[2].u.mi.dy = $ay
    $inputs[2].u.mi.dwFlags = ([NI]::MOVE -bor [NI]::ABSOLUTE -bor [NI]::IUP)
    [NI]::SendInput(3, $inputs, [System.Runtime.InteropServices.Marshal]::SizeOf([type][NI+INPUT])) | Out-Null
    Start-Sleep -Milliseconds 600
}

function Do-FastClick([int]$px, [int]$py) {
    [NI]::SetCursorPos($px, $py) | Out-Null
    Start-Sleep -Milliseconds 15
    [NI]::mouse_event([NI]::LD, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 30
    [NI]::mouse_event([NI]::LU, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 500
}

function Do-RClick([int]$px, [int]$py) {
    [NI]::SetCursorPos($px, $py) | Out-Null
    Start-Sleep -Milliseconds 150
    [NI]::mouse_event(0x0008, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 70
    [NI]::mouse_event(0x0010, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 600
}

function Do-Drag([int]$x1, [int]$y1, [int]$x2, [int]$y2) {
    [NI]::SetCursorPos($x1, $y1) | Out-Null
    Start-Sleep -Milliseconds 200
    [NI]::mouse_event([NI]::LD, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 250
    for ($i = 1; $i -le 14; $i++) {
        $mx = [int]($x1 + ($x2 - $x1) * $i / 14)
        $my = [int]($y1 + ($y2 - $y1) * $i / 14)
        [NI]::SetCursorPos($mx, $my) | Out-Null
        Start-Sleep -Milliseconds 50
    }
    Start-Sleep -Milliseconds 250
    [NI]::mouse_event([NI]::LU, 0, 0, 0, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 600
}

function Do-Wheel([int]$delta) {
    $v = if ($delta -lt 0) { [uint32](4294967296 + $delta) } else { [uint32]$delta }
    [NI]::mouse_event(0x0800, 0, 0, $v, [UIntPtr]::Zero)
    Start-Sleep -Milliseconds 500
}

function Do-Shot([string]$path) {
    Add-Type -AssemblyName System.Windows.Forms, System.Drawing
    $b = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
    $bmp = New-Object System.Drawing.Bitmap $b.Width, $b.Height
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.CopyFromScreen($b.X, $b.Y, 0, 0, $bmp.Size)
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Output ("shot " + $path)
}

function Do-Type([string]$t) {
    Add-Type -AssemblyName System.Windows.Forms
    [System.Windows.Forms.SendKeys]::SendWait($t)
    Start-Sleep -Milliseconds 400
}

function Do-Key([string]$k) {
    Add-Type -AssemblyName System.Windows.Forms
    [System.Windows.Forms.SendKeys]::SendWait($k)
    Start-Sleep -Milliseconds 400
}

if ($Action -ne "") {
    switch ($Action) {
        "focus" { Write-Output ("focus=" + (Focus-Apifox)) }
        "hide"  { Hide-Others; Write-Output "hidden" }
        "click" { Focus-Apifox | Out-Null; Do-Click $X $Y }
        "shot"  { Do-Shot $Text }
        default { Write-Output "unknown action" }
    }
    exit
}

if ($Script -ne "") {
    Hide-Others | Out-Null
    Focus-Apifox | Out-Null
    foreach ($step in ($Script -split ";")) {
        $step = $step.Trim()
        if ($step -eq "") { continue }
        $idx = $step.IndexOf(":")
        $cmd = if ($idx -gt 0) { $step.Substring(0, $idx) } else { $step }
        $arg = if ($idx -gt 0) { $step.Substring($idx + 1) } else { "" }
        switch ($cmd) {
            "click" { $p = $arg -split ","; Do-Click ([int]$p[0]) ([int]$p[1]) }
            "hover" { $p = $arg -split ","; [NI]::SetCursorPos([int]$p[0], [int]$p[1]) | Out-Null; Start-Sleep -Milliseconds 400 }
            "rclick" { $p = $arg -split ","; Do-RClick ([int]$p[0]) ([int]$p[1]) }
            "wheel" { Do-Wheel ([int]$arg) }
            "fastclick" { $p = $arg -split ","; Do-FastClick ([int]$p[0]) ([int]$p[1]) }
            "aclick" { $p = $arg -split ","; Do-AtomicClick ([int]$p[0]) ([int]$p[1]) }
            "drag"  { $p = $arg -split ","; Do-Drag ([int]$p[0]) ([int]$p[1]) ([int]$p[2]) ([int]$p[3]) }
            "wait"  { Start-Sleep -Milliseconds ([int]$arg) }
            "shot"  { Do-Shot $arg }
            "type"  { Do-Type $arg }
            "paste" { Set-Clipboard -Value $arg; Start-Sleep -Milliseconds 300; Do-Key "^v" }
            "key"   { Do-Key $arg }
            "focus" { Focus-Apifox | Out-Null }
            default { Write-Output ("skip " + $cmd) }
        }
    }
}
