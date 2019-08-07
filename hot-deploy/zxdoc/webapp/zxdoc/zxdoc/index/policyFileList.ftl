<script type="text/javascript" src="/zxdoc/static/index/js/jquery.min.js"></script>
<style type="text/css">
    table.altrowstable {
        font-family: verdana, arial, sans-serif;
        font-size: 11px;
        color: #333333;
        border-width: 1px;
        border-color: #a9c6c9;
        border-collapse: collapse;
    }

    table.altrowstable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }

    table.altrowstable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }
</style>
<script type="application/javascript">
    var policyTable;
    $(function(){
        var minHeight = $("#fileTable").height()+900;
        if($("#policy").height() < minHeight){
            $("#policy").height(minHeight);
        }
        $("#policy").css("background", "transparent").backstretch([
                    "/metronic-web/images/login-bg/1.jpg",
                    "/metronic-web/images/login-bg/2.jpg",
                    "/metronic-web/images/login-bg/3.jpg",
                    "/metronic-web/images/login-bg/4.jpg"
                ], {
                    fade: 1000,
                    duration: 8000
                }
        );

        policyTable = initDatatables("#fileTable", {
            buttons: [],
            "serverSide": true,
            "ajax": {
                url: "PolicyFilesJson?category=${parameters.category!}",
                type: "POST",
                dataType:"json",
                data: function (d) {
                    d.searchKey = $("#searchKey").val();
                }
            },
             ordering: false,
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                        return Mustache.render($("#fileLink").html(), {fileId: row.fileId, fileName: row.fileName, fileShortName: row.fileShortName});
                    }
                },
                {"data": "publishDate"},
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#downloadLink").html(), {fileId: row.fileId, fileName: row.fileName, fileShortName: row.fileShortName});
                }
                }
            ],
            "fnDrawCallback": function(){
                var api = this.api();
                var startIndex= api.context[0]._iDisplayStart;//获取到本页开始的条数
                api.column(0).nodes().each(function(cell, i) {
                    //此处 startIndex + i + 1;会出现翻页序号不连续，主要是因为startIndex 的原因,去掉即可。
                    cell.innerHTML = startIndex + i + 1;
                });
            }
        })
    })

    //清空搜索条件
    function emptySearchCondition() {
        $("#searchKey").val("");
        policyTable.ajax.reload();
    }
</script>
<script type="text/html" id="fileLink">
    <a onclick="viewPdfInLayer('{{fileId}}')" href="#nowhere" title="{{fileName}}">{{fileShortName}}</a>
</script>
<script type="text/html" id="downloadLink">
    <a href="/zxdoc/control/downloadPolicyFile?dataResourceId={{fileId}}">下载</a>
</script>
<div id="policy" >
<section class="page-section pt-80 pb-0" style="padding-top: 30px;">
    <div class="container">
        <div class="row">
            <div class="mb-20">
                <h2 class="title-section mb0 mt-0 text-center"><#if categoryName?has_content>${categoryName!}<#else>政策文件</#if> </h2>
                <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                <p class="text-center mb-30"></p>

                <div class="col-xs-8 col-xs-offset-2">
                    <div class="col-xs-8 col-xs-offset-2 mb-20">
                        <div class="input-group">
                            <input type="text" id="searchKey" class="form-control" value="${searchKey!''}" name="searchKey" style="width: 100%;height:34px;" placeholder="请输入查询关键字"/>
                            <span class="input-group-btn">
                                <button class="btn green" type="button" onclick="policyTable.ajax.reload();">查询</button>
                                <button class="btn red" type="button" onclick="emptySearchCondition()">重置</button>
                            </span>
                        </div>
                    </div>
                </div>
                <div>
                    <table id="fileTable" style="color: #1b1919" class="table table-striped table-bordered table-hover order-column">
                        <thead>
                            <tr>
                                <th style="min-width: 40px">序号</th>
                                <th>
                                    文件名称
                                </th>
                                <th style="min-width: 150px">
                                    发布日期
                                </th>
                                <th style="min-width: 50px">
                                    下载
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
</div>