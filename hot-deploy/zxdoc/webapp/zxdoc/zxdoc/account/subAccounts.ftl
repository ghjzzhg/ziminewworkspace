<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/account.js?t=20170909" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<style type="text/css">
    .ope-control{
        white-space: nowrap;
    }
</style>
<script type="text/javascript">
    var accountTable;
    $(function(){
        accountTable = initDatatables("#subAccounts", {
            buttons: [{
                text: '新建',
                action: function ( e, dt, node, config ) {
                    displayInLayer('新建账户', {requestUrl: 'NewSubAccount?subAccount=false', width:"500px", height:"650px", layer:{end: function(){
                        accountTable.ajax.reload();
                    }}});
                }, className: 'btn green btn-outline'
            }
            ],
            "serverSide": true,
            "ajax": {
                url: "SubAccountsJson",
                type: "POST",
                data : function (d) {
                    d.type = $("input[name=type]:checked").val();
                }
            },
            "order": [
                [3, 'desc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"name":"fullName", "data": null, "render": function(data, type, row, meta){
                    var fullName = row.fullName ==null?"":row.fullName;
                    var add = row.fullName ==null?"":row.fullName;
//                    if (fullName.length > 12) {
//                        fullName = fullName.substring(0, 12) + "...";
//                    }
                    return fullName
                }},
                {orderable: false,"data": "userLoginId", "render": function(data, type, row, meta){
                    var userLoginId = row.userLoginId  ==null?"":row.userLoginId;
                    var add = row.userLoginId ==null?"":row.userLoginId;
//                    if (userLoginId.length > 12) {
//                        userLoginId = userLoginId.substring(0, 12) + "...";
//                    }
                    return '<span title="'+add+'">' + userLoginId + '</span>'
                }},
                /*{orderable: false,"data": null, render:function(){
                    return "";
                }},
                {orderable: false,"data": null, render:function(){
                    return "";
                }},*/
                {"data": "createdStamp"},
                {orderable: false,"data": null, "render" :function(data, type, row, meta){
                    var status = row.statusId;
                    if(status == "PARTY_UNIDENTIFIED"){
                        return '<i title="未认证" class="font-red fa fa-circle-o">';
                    }else{
                        return '<i title="已认证" class="font-green fa fa-circle">';
                    }
                }},
                {orderable: false,"data": "contactNumber"},
                {
                    orderable: false, "data": null, "className": "ope-control", "render": function (data, type, row) {
                    return Mustache.render($("#operatorCol").html(), {
                        userLoginId: row.userLoginId,
                        partyId: row.partyId,
                        lockDisplay: row.enabled == "Y" ? "": "none",
                        unlockDisplay: row.enabled == "Y" ? "none": ""
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
    function updateSubAccount(partyId) {
        displayInLayer('修改账户', {requestUrl: 'NewSubAccount?subAccount=true&partyId=' + partyId, width:500, height:650, layer:{end: function(){
            accountTable.ajax.reload();
        }}});
    }
    //禁用/解除禁用
    function forbidOrRecover(userLoginId, isRecover) {
        //先不做逻辑删除
        $.ajax({
            url: isRecover ? "RecoverSubAccount": "ForbidSubAccount",
            type: "POST",
            data: {userLoginId: userLoginId},
            dataType: "json",
            success: function(data) {
                showInfo(data.data);
                accountTable.ajax.reload();
            }
        })
    }
    //重置密码
    function resetPwd(userLoginId) {
        $.ajax({
            url: "ResetPassword",
            type: "POST",
            data: {userLoginId: userLoginId},
            dataType: "json",
            success: function(data) {
                layer.alert("重置成功，新密码为：" + data.password);
            }
        })
    }

    function forbidOrRemove(userLoginId){
        var confirmIndex = getLayer().confirm("确定删除该用户吗?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                url: "deleteAccount",
                type: "POST",
                data: {userLoginId: userLoginId},
                dataType: "json",
                success: function (data) {
                    showInfo(data.data);
                    accountTable.ajax.reload();
                }
            })
            getLayer().close(confirmIndex);
        })
    }

</script>
<script id="operatorCol" type="text/html">
    <#--<a class="btn btn-md green btn-outline" href="javascript:;" title="重置密码" onclick="resetPwd('{{userLoginId}}')"> <i class="fa fa-undo"></i> </a>-->
    <a class="btn btn-md green btn-outline" href="javascript:;" title="修改" onclick="updateSubAccount('{{partyId}}')"> <i class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" style="display: {{lockDisplay}}" href="javascript:;" title="禁用" onclick="forbidOrRecover('{{userLoginId}}', false)"> <i class="fa fa-lock"></i> </a>
    <a class="btn btn-md green btn-outline" style="display: {{unlockDisplay}}" href="javascript:;" title="解除禁用" onclick="forbidOrRecover('{{userLoginId}}', true)"> <i class="fa fa-unlock"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:;" title="删除" onclick="forbidOrRemove('{{userLoginId}}', true)"> <i class="fa fa-remove"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">子账号</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="subAccounts">
            <thead>
            <tr>
                <th>序号</th>
                <th>姓名</th>
                <th>账号</th>
                <#--<th>类型</th>-->
                <#--<th>上级</th>-->
                <th>创建时间</th>
                <th>认证状态</th>
                <th>联系电话</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>