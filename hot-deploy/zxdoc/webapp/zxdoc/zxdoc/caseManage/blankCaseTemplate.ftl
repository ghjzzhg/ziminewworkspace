<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function(){
        var table = $('#sample_1');
        var oTable = table.dataTable({

            // Internationalisation. For more info refer to http://datatables.net/manual/i18n
            "language": {
                "aria": {
                    "sortAscending": ": activate to sort column ascending",
                    "sortDescending": ": activate to sort column descending"
                },
                "emptyTable": "无记录",
                "info": "当前 _START_ - _END_ 总共 _TOTAL_ 条",
                "infoEmpty": "无记录",
                "infoFiltered": "(filtered1 from _MAX_ total entries)",
                "zeroRecords": "无记录"
            },

            // Or you can use remote translation file
            //"language": {
            //   url: '//cdn.datatables.net/plug-ins/3cfcc339e89/i18n/Portuguese.json'
            //},

            // setup buttons extension: http://datatables.net/extensions/buttons/

            // scroller extension: http://datatables.net/extensions/scroller/
            scrollY:        300,
            deferRender:    true,
            scroller:       false,
            paging: true,
            stateSave:      true,
            "searching": false,
            "lengthChange": false,
            "order": [
                [0, 'asc']
            ],
            // set the initial value
            "pageLength": 10,

            "dom": "<'row' <'col-md-12'B>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>", // horizobtal scrollable datatable

            // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
            // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js).
            // So when dropdowns used the scrollable div should be removed.
            //"dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
        });
    })
</script>
<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }
</style>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">空白模板</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>任务节点</th>
                <th>执行人</th>
                <th>期限</th>
                <th>资料模板</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>1</td>
                <td><input type="text" class="form-control"/></td>
                <td><select class="form-control">
                    <option>CASE创建人员</option>
                    <option>企业</option>
                    <option>会计</option>
                    <option>律师</option>
                    <option>券商</option>
                    <option>其他机构</option>
                </select></td>
                <td>CASE开始第<input type="text" class="form-control" style="width: 50px;display:inline">天</td>
                <td><input type="file"></td>
                <td>
                    <a class="btn btn-md green btn-outline" href="javascript:;" title="编辑"> <i class="fa fa-pencil"></i> </a>
                    <a class="btn btn-md red btn-outline" href="javascript:;" title="删除"> <i class="fa fa-remove"></i> </a>
                    <a class="btn btn-md green btn-outline" href="javascript:;" title="添加"> <i class="fa fa-level-down"></i> </a>
                </td>
            </tr>
            <tr>
                <td>2</td>
                <td><input type="text" class="form-control"/></td>
                <td><select class="form-control">
                    <option>CASE创建人员</option>
                    <option>企业</option>
                    <option>会计</option>
                    <option>律师</option>
                    <option>券商</option>
                    <option>其他机构</option>
                </select></td>
                <td>CASE开始第<input type="text" class="form-control" style="width: 50px;display:inline">天</td>
                <td><input type="file"></td>
                <td>
                    <a class="btn btn-md green btn-outline" href="javascript:;" title="编辑"> <i class="fa fa-pencil"></i> </a>
                    <a class="btn btn-md red btn-outline" href="javascript:;" title="删除"> <i class="fa fa-remove"></i> </a>
                    <a class="btn btn-md green btn-outline" href="javascript:;" title="添加"> <i class="fa fa-level-down"></i> </a>
                </td>
            </tr>
            </tbody>
        </table>
        <div class="form-group">
            <div class="margiv-top-10">
                <a href="javascript: history.go(-1);" class="btn grey"> 返回 </a>
                <a href="javascript:;" class="btn green"> 创建CASE </a>
            </div>
        </div>
    </div>
</div>