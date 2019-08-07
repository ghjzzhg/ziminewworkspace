<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/crm.js?t=20170123" type="text/javascript"></script>
<script type="text/javascript">
    var templateTable;
    $(function () {
        templateTable = initDatatables("#policyFileTable",{
            buttons: [{
                    text: '新建',
                    action: function (e, dt, node, config) {
                        displayInLayer('新建文件', {
                            requestUrl: 'newPolicyFile', height: '50%', width: "500px", end: function () {
                                templateTable.ajax.reload();
                            }
                        })
                    }, className: 'btn green btn-outline'
                }],
                "serverSide": true,
                "ajax": {
                    url: "searchPolicyFileList",
                    type: "POST",
                    data: function (d) {
                        d.fileName = $("#fileName").val();
                        d.policySearchOne = $("#policySearchOne").val();
                        d.policySearchTwo = $("#policySearchTwo").val();
                    }
                },
                "order": [
                    [4, 'desc'],
                ],
                "columns": [
                    {"data": null,"targets": 0, orderable: false},
                    {
//                        orderable: false,
                        "data": "dataResourceName"
                    },
                    {
//                        orderable: false,
                        "data": "policyTypeOne"
                    },
                    {
//                        orderable: false,
                        "data": "policyTypeTwo"
                    },
                    {
                        "data": "createdDate"
                    },
                    {
                        orderable: false, "data": null, "render": function (data, type, row) {
                        return Mustache.render($("#operatorCol").html(), {fileId: row.id});
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
            }
        )
    })

    function initPolicyFileTable(){
        templateTable.ajax.reload();
    }

    function searchPolicyTypeTwo(){
        var oneTypeId = $("#policySearchOne").val();
        $.ajax({
            type: 'GET',
            url: "searchPolicyTypeTwo",
            async: true,
            data: {oneTypeId: oneTypeId},
            dataType: 'json',
            success: function (content) {
                var twoTypeList = content.data;
                var twoPolicyHtml = "<option>-请选择-</option>";
                for(var i = 0; i < twoTypeList.length; i++){
                    var type = twoTypeList[i];
                    twoPolicyHtml += "<option value='" + type.enumId + "'> " + type.description + " </option>";
                }
                $("#policySearchTwo").html(twoPolicyHtml);
            }
        });
    }

    function delPolicyFile(fileId){

        var confirmIndex = getLayer().confirm("是否确认删除?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                type: 'GET',
                url: "delPolicyFile",
                async: true,
                data: {fileId: fileId},
                dataType: 'json',
                success: function (data) {
                    showInfo(data.msg);
                    templateTable.ajax.reload();
                }
            });
            getLayer().close(confirmIndex);
        })
    }
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md red btn-outline" href="javascript:delPolicyFile('{{fileId}}')" title="删除"> <i
            class="fa fa-remove"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">政策文件</span>
        </div>
        <div class="tools"></div>
        <div class="actions">
            <a href="#nowhere" onclick="javascript:initPolicyFileTable();" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">文件名称</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                    <input type="text" id="fileName" name="fileName" class="form-control" maxlength=20/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">一级分类</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <#assign policySearchOne = delegator.findByAnd("Enumeration", {"enumTypeId" : "POLICY_CATEGORY"}, null, false)/>
                    <select class="form-control" onchange="searchPolicyTypeTwo()" name="policySearchOne" id="policySearchOne">
                        <option value="">-请选择-</option>
                        <#list policySearchOne as policyTypeOne>
                            <option value="${policyTypeOne.enumId?default('')}">${policyTypeOne.description?default('')}</option>
                        </#list>
                    </select>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">二级分类</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <select class="form-control" id="policySearchTwo">
                        <option value="">-请选择-</option>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">政策文件列表</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="policyFileTable">
            <thead>
            <tr>
                <td style="width: 30px">
                    <label>序号</label>
                </td>
                <td>
                    <label>文件名称</label>
                </td>
                <td style="width: 100px">
                    <label>一级分类</label>
                </td>
                <td style="width: 100px">
                    <label>二级分类</label>
                </td>
                <td style="width: 120px">
                    <label>上传时间</label>
                </td>
                <td style="width: 30px">
                    <label>操作</label>
                </td>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>