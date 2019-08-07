<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>

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
                url: "GetContractsJson",
                type: "POST",
                    data: function (d) {
                    d.contractParty = $("#contractA").val();
                    d.startDate = $("#startDate").val();
                    d.dateClose = $("#dateClose").val();
                }
            },
            "order": [
                [4, 'desc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "contractName", "render": function(data, type, row, meta){
                    var contractName = row.contractName ==null?"":row.contractName;
                    var add = row.contractName  ==null?"":row.contractName;
//                    if (contractName.length > 12) {
//                        contractName = contractName.substring(0, 12) + "...";
//                    }
                    return '<span title="'+add+'">' + contractName + '</span>'
                }},
                {"data": "secondPartyName", "render": function(data, type, row, meta){
                    var secondPartyName = row.secondPartyName ==null?"":row.secondPartyName;
                    var add = row.secondPartyName ==null?"":row.secondPartyName;
//                    if (secondPartyName.length > 12) {
//                        secondPartyName = secondPartyName.substring(0, 12) + "...";
//                    }
                    return '<span title="'+add+'">' + secondPartyName + '</span>'
                }},
                {"data": "fullName", "render": function(data, type, row, meta){
                    var fullName = row.fullName ==null?"":row.fullName;
                    var add = row.fullName ==null?"":row.fullName;
//                    if (fullName.length > 12) {
//                        fullName = fullName.substring(0, 12) + "...";
//                    }
                    return '<span title="'+add+'">' + fullName + '</span>'
                }},
                {"data": "startDate"},
                {"data": "dateClose"},
                {"data": "relateCase"},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#operatorCol").html(), {
                        contractId: data.contractId,
                        display: data.editable ? "": "none"
                    });
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
    function deleteContract(id) {

        var confirmIndex = getLayer().confirm("确认删除吗?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                type: 'post',
                dataType: 'json',
                url: 'deleteContract',
                data: {"contractId": id},
                success: function (content) {
                    displayInside('ListContracts')
                    //伪刷新
                    // $("#" + id).remove();
                    showInfo(content.data)
                }
            })
            getLayer().close(confirmIndex);
        })

    }
    //    条件查找合同
    function searchContracts() {
        templateTable.ajax.reload();
    }
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:;" title="修改"
       onclick="displayInside ('editContract?contractId={{contractId}}')" style="display: {{display}}"> <i
            class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:;" title="删除"
       onclick="deleteContract('{{contractId}}')" style="display: {{display}}"> <i class="fa fa-remove"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">签约合同</span>
        </div>
        <div class="tools"></div>
        <div class="actions">
            <a href="#nowhere" onclick="searchContracts('NewContract?contractId={{contractId}}')" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">乙方公司</label>

                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="contractA" name="contractA" class="form-control"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">开始日期</label>
                <div class="input-group">
        <span class="input-group-addon">
        <i class="fa fa-calendar"></i>
        </span>
                    <input type="text" class="form-control" name="startDate" id="startDate"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">结束日期</label>
                <div class="input-group">
        <span class="input-group-addon">
        <i class="fa fa-calendar"></i>
        </span>
                    <input type="text" class="form-control" name="dateClose" id="dateClose"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
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
    <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
        <thead>
        <tr>
            <th>序号</th>
            <th>合同名称</th>
            <#--<th>甲方</th>-->
            <th>乙方</th>
            <th>乙方人员</th>
            <th>开始时间</th>
            <th>结束时间</th>
            <th>关联CASE</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
