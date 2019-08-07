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
                url: "ScoreHistoryJson",
                type: "POST",
                data: function (d) {
                    d.userName = $("#userName").val();
                    d.account = $("#account").val();
                    d.startTime = $("#startTime").val();
                    d.endTime = $("#endTime").val();
                    d.scoreRule = $("#scoreRule").val();
                }
            },
            "order": [
                [5, 'desc']
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "userName"},
                {"data": "userLoginId"},
                {"data": "scoreChange"},
                {"data": "description"},
                {"data": "createdStamp"},
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
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">积分日志</span>
        </div>
        <div class="tools"></div>
        <div class="actions">
            <a href="#nowhere" onclick="javascript:$.scoreManage.searchScoreHistory()" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">姓名</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="userName" class="form-control"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">账号</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="account" class="form-control"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">起始时间</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </span>
                    <input type="text" class="form-control" id="startTime"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">结束时间</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </span>
                    <input type="text" class="form-control" id="endTime"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">积分规则</label>
                <select class="form-control" id="scoreRule">
                    <option value="">-请选择-</option>
                    <#if scoreRule?has_content>
                        <#list scoreRule as rule>
                            <option value="${rule.enumId?default('')}">${rule.description?default('')}</option>
                        </#list>

                    </#if>
                </select>
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
                <th>姓名</th>
                <th>账户</th>
                <th>积分变更</th>
                <th>规则</th>
                <th>变更时间</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>