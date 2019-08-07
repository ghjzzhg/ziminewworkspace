<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/scoreManage.js?t=20171010" type="text/javascript"></script>

<script type="text/javascript">
    var templateTable;
    $(function () {
        templateTable = initDatatables("#sample_1", {
            buttons: [{
                text: '新建',
                action: function (e, dt, node, config) {
                    displayInLayer('新建积分策略', { requestUrl: 'EditScoreRule', height: 450, width: 300, end: function () {
                            templateTable.ajax.reload();
                        }
                    })
                }, className: 'btn green btn-outline'
            },
                /*{extend: 'print', text: '打印', className: 'btn dark btn-outline'},
                {extend: 'pdf', className: 'btn green btn-outline'},
                {extend: 'csv', className: 'btn purple btn-outline '}*/],
            "serverSide": true,
            "ajax": {
                url: "ScoreRulesJson",
                type: "POST"
            },
            "order": [
                [1, 'asc']
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "ruleType"},
                {"data": "description"},
                {"data": "maxTimes"},
                {"data": "score"},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#operatorCol").html(), {scoreRuleId: row.scoreRuleId});
                }
                }
            ],
            "fnDrawCallback": function() {
                var api = this.api();
                var startIndex = api.context[0]._iDisplayStart;//获取到本页开始的条数
                api.column(0).nodes().each(function (cell, i) {
                    //此处 startIndex + i + 1;会出现翻页序号不连续，主要是因为startIndex 的原因,去掉即可。
                    cell.innerHTML = startIndex + i + 1;
                });
            }
        });
    })

</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.scoreManage.editScoreRule('{{scoreRuleId}}');"
       title="编辑"> <i class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:$.scoreManage.removeScoreRule('{{scoreRuleId}}');"
       title="删除"> <i class="fa fa-remove"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">积分策略</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>策略名称</th>
                <th>周期</th>
                <th>奖励次数</th>
                <th>积分</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>