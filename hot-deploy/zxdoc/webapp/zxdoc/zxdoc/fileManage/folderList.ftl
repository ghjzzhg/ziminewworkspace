<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>

<script type="text/javascript">
    var templateTable;
    $(function(){
        templateTable = initDatatables("#folderTemplates", {
            buttons: [{
                text: '新建',
                action: function (e, dt, node, config) {
                    displayInLayer('新模板', {requestUrl: 'NewFolderTemplate', height: 450, width: 800, end: function(){
                        templateTable.ajax.reload();
                    }})
                }, className: 'btn green btn-outline'
            }],
            "serverSide": true,
            "ajax": {
                url: "FolderTemplatesJson",
                type: "POST"
            },
            "order": [
                [1, 'asc'],
                [3, 'desc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "templateKey"},
                {"data": "templateName"},
                {"data": "version"},
                {"data": "createdStamp"},
                {"data": "activeTime"},
                {orderable: false, "data": "remark"},
                {"data": "active", "render": function(data, type, row){
                    if(row.active == "Y"){
                        return "生效"
                    }else{
                        return "失效"
                    }
                }},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                        return Mustache.render($("#operatorCol").html(), {templateId: row.id});
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
    <a class="btn btn-md green btn-outline" href="javascript:$.caseManage.editCaseTemplate('{{templateId}}','false');" title="编辑"> <i class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:;" title="失效"> <i class="fa fa-ban"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:$.caseManage.deleteCaseTemplate('{{templateId}}','false');" title="删除"> <i class="fa fa-remove"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">CASE模板管理</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="folderTemplates">
            <thead>
            <tr>
                <th>序号</th>
                <th>模板Key</th>
                <th>模板名称</th>
                <th>版本</th>
                <th>创建时间</th>
                <th>生效时间</th>
                <th>备注</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>