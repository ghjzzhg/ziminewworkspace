<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/scoreManage.js?t=20171010" type="text/javascript"></script>

<script type="text/javascript">
    var templateTable;
    $(function () {
        templateTable = initDatatables("#sample_1", {
            buttons: [
                /*{extend: 'print', text: '打印', className: 'btn dark btn-outline'},
                {extend: 'pdf', className: 'btn green btn-outline'},
                {extend: 'csv', className: 'btn purple btn-outline '}*/],
            "serverSide": true,
            "ajax": {
                url: "UserScoresJson",
                type: "POST",
                dataType:"json",
                data: function (d) {
                    d.userName = $("#userName").val();
                    d.account = $("#account").val();
                }
            },
            "order": [
                [1, 'asc'],
                [3, 'asc']
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "userName"},
                {
                    "data": "score","orderable":false, "render": function (data, type, row) {
                    var scoreOn = row.scoreOn==null?"待定":row.scoreOn;
                    var scoreOff = row.scoreOff==null?"待定":row.scoreOff;
                    return scoreOn + "/" + scoreOff ;
                }
                },
                {"data": "scoreNow"},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#operatorCol").html(), {userLoginId: row.userLoginId});
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
        });
    })
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.scoreManage.editUserScores('{{userLoginId}}');"
       title="编辑"> <i class="fa fa-pencil"></i> </a>
    <a class="btn btn-md green btn-outline" href="javascript:$.scoreManage.userScoreHistory('{{userLoginId}}');"
       title="积分记录"> <i
            class="fa fa-info"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">用户积分</span>
        </div>
        <div class="tools"></div>
        <div class="actions">
            <a href="#nowhere" onclick="javascript:$.scoreManage.searchUserScore()" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">用户名称</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="userName" name="userName" class="form-control"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">账号名称</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="account" name="account" class="form-control"/>
                </div>
            </div>
        </div>
    </div>
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">结果列表</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>用户名称</th>
                <th>积分上下限</th>
                <th>当前积分</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>