param(
    [Parameter(Mandatory = $true)][string]$Base
)

$ErrorActionPreference = 'Stop'

$jsonPath = Join-Path $Base 'tools\manual-data.json'
$namesPath = Join-Path $Base 'tools\sheet-names.txt'
$outPath = Join-Path $Base 'doc\xzs-manual-test.xlsx'
$shotDir = Join-Path $Base 'doc\screenshots'

$raw = Get-Content -Raw -Encoding UTF8 $jsonPath
$data = $raw | ConvertFrom-Json
$sheetNames = Get-Content -Encoding UTF8 $namesPath

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$excel.DisplayAlerts = $false
$excel.ScreenUpdating = $false

$wb = $excel.Workbooks.Add()
while ($wb.Sheets.Count -lt 7) {
    $wb.Sheets.Add([System.Reflection.Missing]::Value, $wb.Sheets.Item($wb.Sheets.Count)) | Out-Null
}
for ($i = 1; $i -le 7; $i++) {
    $wb.Sheets.Item($i).Name = $sheetNames[$i - 1]
}

function Set-Val($ws, $r, $c, $v) {
    $cell = $ws.Cells.Item($r, $c)
    $cell.Value2 = [string]$v
}

function Style-Title($ws, $cols, $text) {
    $rng = $ws.Range($ws.Cells.Item(1, 1), $ws.Cells.Item(1, $cols))
    $rng.Merge()
    $rng = $ws.Range($ws.Cells.Item(1, 1), $ws.Cells.Item(1, $cols))
    $rng.Value2 = $text
    $rng.Font.Bold = $true
    $rng.Font.Size = 15
    $rng.HorizontalAlignment = -4108
    $rng.Interior.Color = 15921906
    $ws.Rows.Item(1).RowHeight = 28
}

function Style-Header($ws, $row, $cols) {
    $rng = $ws.Range($ws.Cells.Item($row, 1), $ws.Cells.Item($row, $cols))
    $rng.Font.Bold = $true
    $rng.Font.Color = 16777215
    $rng.Interior.Color = 6972388
    $rng.HorizontalAlignment = -4108
    $rng.WrapText = $true
    $rng.Borders.LineStyle = 1
    $ws.Rows.Item($row).RowHeight = 24
}

function Style-Body($ws, $r1, $r2, $cols) {
    if ($r2 -lt $r1) { return }
    $rng = $ws.Range($ws.Cells.Item($r1, 1), $ws.Cells.Item($r2, $cols))
    $rng.WrapText = $true
    $rng.Borders.LineStyle = 1
    $rng.Borders.Weight = 2
}

function Set-Widths($ws, $widths) {
    for ($i = 0; $i -lt $widths.Count; $i++) {
        $ws.Columns.Item($i + 1).ColumnWidth = $widths[$i]
    }
}

# Sheet 1: overview
$ws = $wb.Sheets.Item(1)
Style-Title $ws 2 '学之思系统手工功能测试记录'
$r = 2
foreach ($row in $data.overview) {
    Set-Val $ws $r 1 $row[0]
    Set-Val $ws $r 2 $row[1]
    $ws.Cells.Item($r, 1).Font.Bold = $true
    $r++
}
Style-Body $ws 2 ($r - 1) 2
Set-Widths $ws @(18, 130)
$ws.Rows.Item("2:$($r-1)").RowHeight = 30

# Sheet 2: cases
$ws = $wb.Sheets.Item(2)
Style-Title $ws 8 '测试用例表'
$headers = @('用例编号','功能模块','测试标题','前置条件','测试步骤','测试数据','预期结果','优先级')
for ($c = 0; $c -lt $headers.Count; $c++) { Set-Val $ws 2 ($c + 1) $headers[$c] }
Style-Header $ws 2 8
$r = 3
foreach ($item in $data.cases) {
    Set-Val $ws $r 1 $item.id
    Set-Val $ws $r 2 $item.module
    Set-Val $ws $r 3 $item.title
    Set-Val $ws $r 4 $item.pre
    Set-Val $ws $r 5 $item.steps
    Set-Val $ws $r 6 $item.data
    Set-Val $ws $r 7 $item.expected
    Set-Val $ws $r 8 $item.priority
    $ws.Rows.Item($r).RowHeight = 48
    $r++
}
Style-Body $ws 3 ($r - 1) 8
Set-Widths $ws @(11, 16, 26, 28, 46, 22, 40, 8)

# Sheet 3: execution
$ws = $wb.Sheets.Item(3)
Style-Title $ws 8 '测试执行记录'
$headers = @('用例编号','功能模块','测试标题','实际结果','执行状态','执行时间','测试环境','执行人')
for ($c = 0; $c -lt $headers.Count; $c++) { Set-Val $ws 2 ($c + 1) $headers[$c] }
Style-Header $ws 2 8
$r = 3
foreach ($item in $data.exec) {
    Set-Val $ws $r 1 $item.id
    Set-Val $ws $r 2 $item.module
    Set-Val $ws $r 3 $item.title
    Set-Val $ws $r 4 $item.actual
    Set-Val $ws $r 5 $item.status
    Set-Val $ws $r 6 $item.time
    Set-Val $ws $r 7 $item.env
    Set-Val $ws $r 8 $item.who
    if ($item.status -eq '失败') {
        $ws.Cells.Item($r, 5).Interior.Color = 13551615
        $ws.Cells.Item($r, 5).Font.Color = 255
        $ws.Cells.Item($r, 5).Font.Bold = $true
    } else {
        $ws.Cells.Item($r, 5).Interior.Color = 13561798
    }
    $ws.Rows.Item($r).RowHeight = 48
    $r++
}
Style-Body $ws 3 ($r - 1) 8
Set-Widths $ws @(11, 16, 26, 52, 9, 17, 30, 10)

# Sheet 4: defects
$ws = $wb.Sheets.Item(4)
Style-Title $ws 10 '缺陷记录'
$headers = @('缺陷编号','缺陷名称','所属模块','严重程度','复现步骤','实际结果','预期结果','状态','关联用例','缺陷来源')
for ($c = 0; $c -lt $headers.Count; $c++) { Set-Val $ws 2 ($c + 1) $headers[$c] }
Style-Header $ws 2 10
$r = 3
foreach ($item in $data.bugs) {
    Set-Val $ws $r 1 $item.id
    Set-Val $ws $r 2 $item.name
    Set-Val $ws $r 3 $item.module
    Set-Val $ws $r 4 $item.severity
    Set-Val $ws $r 5 $item.steps
    Set-Val $ws $r 6 $item.actual
    Set-Val $ws $r 7 $item.expected
    Set-Val $ws $r 8 $item.status
    Set-Val $ws $r 9 $item.caseId
    Set-Val $ws $r 10 $item.source
    $ws.Rows.Item($r).RowHeight = 62
    $r++
}
Style-Body $ws 3 ($r - 1) 10
Set-Widths $ws @(10, 28, 20, 8, 46, 42, 36, 7, 11, 14)

# Sheet 5: stats
$ws = $wb.Sheets.Item(5)
Style-Title $ws 3 '统计汇总与数据核对'
$headers = @('统计项','数值','说明')
for ($c = 0; $c -lt $headers.Count; $c++) { Set-Val $ws 2 ($c + 1) $headers[$c] }
Style-Header $ws 2 3
$r = 3
foreach ($row in $data.stats) {
    Set-Val $ws $r 1 $row[0]
    Set-Val $ws $r 2 $row[1]
    Set-Val $ws $r 3 $row[2]
    $ws.Rows.Item($r).RowHeight = 26
    $r++
}
Style-Body $ws 3 ($r - 1) 3
Set-Widths $ws @(34, 22, 60)

# Sheet 6: screenshots
$ws = $wb.Sheets.Item(6)
Style-Title $ws 3 '界面截图与说明'
$headers = @('序号','截图','说明')
for ($c = 0; $c -lt $headers.Count; $c++) { Set-Val $ws 2 ($c + 1) $headers[$c] }
Style-Header $ws 2 3
$r = 3
foreach ($item in $data.shots) {
    Set-Val $ws $r 1 $item[0]
    Set-Val $ws $r 3 $item[2]
    $img = Join-Path $shotDir $item[1]
    if (Test-Path $img) {
        $left = $ws.Cells.Item($r, 2).Left + 2
        $top = $ws.Cells.Item($r, 2).Top + 2
        $ws.Shapes.AddPicture($img, 0, -1, $left, $top, 426, 240) | Out-Null
    }
    $ws.Rows.Item($r).RowHeight = 186
    $r++
}
Style-Body $ws 3 ($r - 1) 3
Set-Widths $ws @(6, 64, 70)
$ws.Cells.Item(2, 2).HorizontalAlignment = -4108

# Sheet 7: UI and compatibility
$ws = $wb.Sheets.Item(7)
Style-Title $ws 8 '界面测试与兼容性测试记录'
$headers = @('编号','类型','测试项','测试步骤','预期结果','实际结果','状态','测试环境')
for ($c = 0; $c -lt $headers.Count; $c++) { Set-Val $ws 2 ($c + 1) $headers[$c] }
Style-Header $ws 2 8
$r = 3
foreach ($item in $data.ui) {
    for ($c = 0; $c -lt 8; $c++) { Set-Val $ws $r ($c + 1) $item[$c] }
    $ws.Rows.Item($r).RowHeight = 42
    $r++
}
Style-Body $ws 3 ($r - 1) 8
Set-Widths $ws @(8, 14, 24, 42, 40, 42, 8, 46)

$wb.Worksheets.Item(1).Activate()
$wb.SaveAs($outPath, 51)
$wb.Close($false)
$excel.Quit()

[System.Runtime.InteropServices.Marshal]::ReleaseComObject($ws) | Out-Null
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($wb) | Out-Null
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($excel) | Out-Null
[GC]::Collect()
[GC]::WaitForPendingFinalizers()

Write-Output ("saved: " + $outPath)
