<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<style type="text/css">
    .control-col{
        white-space: nowrap;
    }
</style>
<script type="text/javascript">
    var templateTable;
    $(function(){
        templateTable = initDatatables("#caseTemplates", {
            buttons: [{
                text: '新建',
                action: function (e, dt, node, config) {
                    displayInLayer('新模板', {requestUrl: 'NewCaseTemplate',data:{privateFlag:"false"}, height: '80%',layer:{end: function(data){
                        templateTable.ajax.reload();
                    }}});
                }, className: 'btn green btn-outline'
            },{
                text: '企业目录修复按钮',
                action: function () {
                    layer.msg('该功能只限于配置文件中增加企业目录后，旧企业数据同步，如果配置文件没有变动，请勿点击确认按钮', {
                        time: 5000 //不自动关闭
//                        shade: 0.1 //遮罩透明度
                        ,btn: ['确定','取消']
                        ,yes: function(index){
                            $.ajax({
                                type: 'GET',
                                url: "orgFolderPatch",
                                async: true,
                                dataType: 'json',
                                success: function (content) {
                                    layer.close(index);
                                }
                            });

                        }
                    });
                }, className: 'btn green btn-outline'
            }],
            "serverSide": true,
            "ajax": {
                url: "CaseTemplatesJson",
                type: "POST",
                data: function (d) {
                    d.templateName = $("#templateName").val();
                }
            },
            "order": [
                [3, 'desc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "templateKeyName","render": function(data, type, row, meta) {
                    var templateKeyName = row.templateKeyName  ==null?"":row.templateKeyName;
                    var add = row.templateKeyName ==null?"":row.templateKeyName;
                    return "<span title='" + add + "'>" + templateKeyName + "</span>";
                }},
                {"data": "templateName","render": function(data, type, row, meta) {
                    var templateName = row.templateName  ==null?"":row.templateName;
                    var add = row.templateName ==null?"":row.templateName;
                    return "<span title='" + add + "'>" + templateName + "</span>";
                }},
                {"data": "createdStamp"},
                {orderable: false, "data": "remark"},
                {"data": "active", "render": function(data, type, row){
                    if(row.active == "Y"){
                        return "生效"
                    }else{
                        return "失效"
                    }
                }},
                {
                    orderable: false, "data": null, className: "control-col" ,"render": function (data, type, row) {
                        var html = $("#operatorCol").html();
                        if(row.active != "Y"){
                            html += $("#loseEfficacyTrue").html();
                        }
//                        else{
//                            html += $("#loseEfficacyFalse").html();
//                        }
                        return Mustache.render(html, {templateId: row.id});
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
            },
            initComplete: function(){
                parent.adjustContentFrame();
            }
        });
    });

</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.caseManage.editCaseTemplate('{{templateId}}','false');" title="编辑"> <i class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:$.caseManage.deleteCaseTemplate('{{templateId}}','false');" title="删除"> <i class="fa fa-remove"></i> </a>
</script>
<#--<script id="loseEfficacyFalse" type="text/html">-->
    <#--<a class="btn btn-md red btn-outline" href="javascript:$.caseManage.loseEfficacy('{{templateId}}',false);" title="失效"> <i class="fa fa-ban"></i> </a>-->
<#--</script>-->
<script id="loseEfficacyTrue" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.caseManage.loseEfficacy('{{templateId}}',true);" title="生效"> <i class="fa fa-check"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">CASE模板管理</span>
        </div>
        <div class="actions">
            <a href="javascript: templateTable.ajax.reload();" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
            <input hidden id="typeRemark" value="group">
        </div>
    </div>
    <div class="portlet-body">
        <form id="searchForm" name="searchForm">
            <div class="row">
                <div class="form-group col-sm-2">
                    <label class="control-label">模板名称</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                        <input type="text" name="templateName" class="form-control" id="templateName" maxlength="20"/>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">结果列表</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="caseTemplates">
            <thead>
            <tr>
                <th style="max-width: 25px">序号</th>
                <th>case类型</th>
                <th>模板名称</th>
                <th>创建时间</th>
                <th style="min-width: 200px">备注</th>
                <th>状态</th>
                <th style="max-width: 130px">操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>