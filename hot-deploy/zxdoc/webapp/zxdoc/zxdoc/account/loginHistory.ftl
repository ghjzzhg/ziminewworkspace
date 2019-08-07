<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function(){
        var oTable = initDatatables("#sample_1",{
            "ajax": {
                url: "loginHistoryJson",
                type: "POST",
            },
            "serverSide": true,
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data":"fullName"},
                {"data":"userLoginId"},
                {"data":"fromDate"},
                {"data":"clientIpAddress"}
            ],
            "order": [
                [3, 'desc']
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
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">登录历史</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>姓名</th>
                <th>账号</th>
                <th>登录时间</th>
                <th>登录IP</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>