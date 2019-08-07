<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>

<script type="text/javascript" src="${request.contextPath}/static/account.js?t=20170909"></script>

<script type="text/javascript">
    var accountTable;
    $(function(){
        accountTable = initDatatables("#pendingAccounts", {
            buttons: [],
            "serverSide": true,
            "ajax": {
                url: "PendingAccountsJson",
                type: "POST",
                data : function (d) {
                    d.type = $("input[name=type]:checked").val();
                }
            },
            "order": [
                [2, 'asc'],
            ],
            "columns": [
                {
                    orderable: false,
                    "className": "dt-center",
                    "data": null,
                    "render": function (data, type, row, meta) {
                        return "<input type='checkbox' name='userLoginId' value='" + row.userLoginId + "'/>";
                    }
                },
                {
                    orderable: false,
                    "className": "dt-center",
                    "data": null,
                    "render": function (data, type, row, meta) {
                        return meta.row + 1;
                    }
                },
                {"name":"groupName", "data": null, "render": function(data, type, row, meta){
                    var partyId = row.userLoginId;
                   /* return '<a href="javascript:displayInLayer(\'企业信息\', {requestUrl: \'ViewBussiness?partyId=\'+row.userLoginId, height: 600});">' + row.groupName + '</a>'*/
                    return Mustache.render($("#url").html(), {
                        partyId: data.partyId,
                        groupName: data.groupName
                    })
                }},
                {orderable: false,"data": "description"},
                {"data": "createdStamp"},
                {orderable: false,"data": "fullName"},
                {orderable: false,"data": "contactNumber"},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                        return Mustache.render($("#operatorCol").html(), {userLoginId: row.userLoginId});
                    }
                }

            ]
        });
        updateBadgeNum();

        $(".type-buttons .btn").on("click", function(){
            setTimeout(function(){accountTable.ajax.reload()}, 100);
        })

    })
    function updateBadgeNum(){
        $.ajax({
            url: "PendingAccountsCountJson",
            type: "POST",
            success: function(data){
                var counts = data.counts, total = 0;
                $(".badge").addClass("hide");
                for(var i in counts){
                    var count = counts[i];
                    var type = count.roleTypeId, num = count.partyId, badge = $("input[name=type][value=" + type + "]").siblings(".badge");
                    if(num > 0){
                        total += num;
                        badge.html(num).removeClass("hide");
                    }else{
                        badge.addClass("hide");
                    }
                }
                var totalBadge = $("input[name=type][value='']").siblings(".badge");
                if(total > 0){
                    totalBadge.html(total).removeClass("hide");
                }else{
                    totalBadge.addClass("hide");
                }
            }
        });
    }

    function batchPassRegistration(){
        var checkedUser = [];
        $("input[name=userLoginId]:checked").each(function(){
            checkedUser.push($(this).val());
        });

        if(checkedUser.length){
            $.account.passRegistration(checkedUser.join(","));
        }else{
            showError("请选择账户后操作！");
        }
    }


    function batchRejectRegistration() {
        var checkedUser = [];
        $("input[name=userLoginId]:checked").each(function(){
            checkedUser.push($(this).val());
        });

        if(checkedUser.length){
            $.account.rejectRegistration(checkedUser.join(","));
        }else{
            showError("请选择账户后操作！");
        }
    }
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md grey-salsa btn-outline" onclick="$.account.rejectRegistration('{{userLoginId}}')" title="拒绝"> <i class="fa fa-ban"></i> </a>
    <a class="btn btn-md green btn-outline" href="#nowhere" onclick="$.account.passRegistration('{{userLoginId}}')" title="通过"> <i class="fa fa-gavel"></i> </a>
</script>
<script type="text/html" id="url">
    <a href="javascript:displayInLayer('企业信息', {requestUrl: 'ViewBussiness?partyId={{partyId}}', height: 600});">{{groupName}}</a>
</script>
<style type="text/css">
    .btn .badge, .btn .label{
        top: -19px;
    }
    .btn.blue:not(.btn-outline) .badge{
        background: #f36a5a;
        color: white;
    }
    .btn.blue{
        height:35px;
    }
</style>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">账户审核</span>
        </div>
    </div>
    <div class="portlet-body">
        <div style="margin-bottom: 10px">
            <div class="type-buttons btn-group" data-toggle="buttons">
                <label class="btn blue active">
                    <input type="radio" class="toggle" name="type" value=""> 全部 <span class="badge badge-default hide">0</span></label>
            <#assign enumerations = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), [], false)>
            <#list enumerations as category>
                <label class="btn blue">
                    <input type="radio" class="toggle" name="type" value="${category.roleTypeId}"> ${category.description}<span class="badge badge-danger hide">0</span>
                </label>
            </#list>
            </div>
        </div>
        <div>
            <table class="table table-striped table-bordered table-hover order-column" id="pendingAccounts">
                <thead>
                <tr>
                    <th>
                        选择
                    </th>
                    <th>
                        序号
                    </th>
                    <th>名称 </th>
                    <th>类型 </th>
                    <th>注册时间 </th>
                    <th>联系人 </th>
                    <th>联系电话 </th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div class="margin-top-10">
            <a class="btn btn-md grey-salsa" onclick="batchRejectRegistration()" title="批量拒绝"> <i class="fa fa-ban"></i> </a>
            <a class="btn btn-md green" href="#nowhere" onclick="batchPassRegistration()" title="批量通过"> <i class="fa fa-gavel"></i> </a>
        </div>
    </div>
</div>