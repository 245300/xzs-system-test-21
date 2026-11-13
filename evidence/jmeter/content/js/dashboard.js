/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "10-管理员登录"], "isController": false}, {"data": [1.0, 500, 1500, "09-复核题目列表"], "isController": false}, {"data": [1.0, 500, 1500, "事务-试卷管理新增试卷"], "isController": true}, {"data": [1.0, 500, 1500, "08-新增题目"], "isController": false}, {"data": [1.0, 500, 1500, "05-复核学生列表"], "isController": false}, {"data": [1.0, 500, 1500, "06-管理员登录"], "isController": false}, {"data": [1.0, 500, 1500, "01-管理员登录"], "isController": false}, {"data": [1.0, 500, 1500, "02-查询学生列表"], "isController": false}, {"data": [1.0, 500, 1500, "13-复核试卷列表"], "isController": false}, {"data": [1.0, 500, 1500, "12-新增试卷"], "isController": false}, {"data": [1.0, 500, 1500, "03-新增学生A"], "isController": false}, {"data": [1.0, 500, 1500, "事务-卷题管理新增题目"], "isController": true}, {"data": [1.0, 500, 1500, "04-新增学生B"], "isController": false}, {"data": [1.0, 500, 1500, "事务-用户管理新增学生"], "isController": true}, {"data": [1.0, 500, 1500, "11-查询试卷列表"], "isController": false}, {"data": [1.0, 500, 1500, "07-查询题目列表"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 58, 0, 0.0, 12.603448275862073, 5, 34, 10.0, 23.200000000000003, 34.0, 34.0, 42.9948109710897, 81.61065488324685, 21.482203252409192], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["10-管理员登录", 4, 0, 0.0, 17.0, 9, 34, 12.5, 34.0, 34.0, 34.0, 4.08997955010225, 2.642110812883436, 1.62361004601227], "isController": false}, {"data": ["09-复核题目列表", 3, 0, 0.0, 22.0, 14, 34, 18.0, 34.0, 34.0, 34.0, 18.51851851851852, 55.507330246913575, 8.644386574074074], "isController": false}, {"data": ["事务-试卷管理新增试卷", 4, 0, 0.0, 51.25, 33, 82, 45.0, 82.0, 82.0, 82.0, 3.7771482530689333, 24.80044558545798, 8.109434017941455], "isController": true}, {"data": ["08-新增题目", 3, 0, 0.0, 13.333333333333334, 10, 17, 13.0, 17.0, 17.0, 17.0, 19.35483870967742, 6.974546370967742, 13.205645161290322], "isController": false}, {"data": ["05-复核学生列表", 6, 0, 0.0, 10.5, 7, 23, 8.5, 23.0, 23.0, 23.0, 4.676539360872955, 18.847732365549493, 2.0185843725643027], "isController": false}, {"data": ["06-管理员登录", 3, 0, 0.0, 20.333333333333332, 11, 34, 16.0, 34.0, 34.0, 34.0, 17.045454545454543, 10.808771306818182, 6.935813210227273], "isController": false}, {"data": ["01-管理员登录", 6, 0, 0.0, 14.666666666666666, 9, 34, 11.5, 34.0, 34.0, 34.0, 4.552352048558422, 2.9408016407435507, 1.8071592849013656], "isController": false}, {"data": ["02-查询学生列表", 6, 0, 0.0, 8.0, 5, 12, 7.5, 12.0, 12.0, 12.0, 4.658385093167702, 18.77456570263975, 2.01074825310559], "isController": false}, {"data": ["13-复核试卷列表", 4, 0, 0.0, 10.75, 5, 25, 6.5, 25.0, 25.0, 25.0, 4.241781548250265, 10.46775583244963, 1.9054878048780488], "isController": false}, {"data": ["12-新增试卷", 4, 0, 0.0, 16.5, 14, 21, 15.5, 21.0, 21.0, 21.0, 4.1928721174004195, 4.127358490566038, 3.5704926624737947], "isController": false}, {"data": ["03-新增学生A", 6, 0, 0.0, 9.5, 7, 12, 9.5, 12.0, 12.0, 12.0, 4.662004662004662, 4.124781468531469, 2.6269303613053614], "isController": false}, {"data": ["事务-卷题管理新增题目", 3, 0, 0.0, 71.0, 59, 92, 62.0, 92.0, 92.0, 92.0, 10.79136690647482, 75.44120953237409, 21.82863084532374], "isController": true}, {"data": ["04-新增学生B", 6, 0, 0.0, 9.5, 7, 11, 10.0, 11.0, 11.0, 11.0, 4.669260700389105, 4.131201361867705, 2.6310189688715955], "isController": false}, {"data": ["事务-用户管理新增学生", 6, 0, 0.0, 52.16666666666667, 37, 84, 48.5, 84.0, 84.0, 84.0, 4.285714285714286, 44.8974609375, 10.230887276785715], "isController": true}, {"data": ["11-查询试卷列表", 4, 0, 0.0, 7.0, 5, 9, 7.0, 9.0, 9.0, 9.0, 4.223864836325237, 10.423541446673706, 1.897439281942978], "isController": false}, {"data": ["07-查询题目列表", 3, 0, 0.0, 15.333333333333334, 13, 19, 14.0, 19.0, 19.0, 19.0, 19.736842105263158, 59.191252055921055, 9.213096217105264], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 58, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
