<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<style type="text/css">
    .control-col{
        white-space: nowrap;
    }
</style>
<script type="text/javascript">
    var accountTable;

    $(function () {
        accountTable = initDatatables("#accounts", {
            buttons: [{
                text: '新建',
                action: function (e, dt, node, config) {
                    var typeRemark = $("#typeRemark").val();
                    /*if(typeRemark=="group") {*/
                        displayInLayer2('新建企业/机构账户', {
                            requestUrl: 'NewAccount', width: 850, height: "70%",layer: {
                                maxmin: false,
                                end: function () {
                                    accountTable.ajax.reload();
                                }
                            }
                        });
                    /*}else {
                        displayInLayer2('新建子账户', {
                            requestUrl: 'createAccountForCompany', width: 800, height: 600, layer: {
                                maxmin: false,
                                end: function () {
                                    accountTable.ajax.reload();
                                }
                            }
                        });
                    }*/
                }, className: 'btn green btn-outline'
            },
                {
                    text: '批量续费',
                    action: function (e, dt, node, config) {
                        var typeRemark = $("#typeRemark").val();
                        if(typeRemark=="group") {
                            batchPassRegistration();
                        }else
                        {
                            showError("子账户尚且不支持续费！");
                        }
                    }, className: 'btn yellow btn-outline'
                }/*,
                {extend: 'print', text: '打印', className: 'btn dark btn-outline'},
                {extend: 'pdf', className: 'btn green btn-outline'},
                {extend: 'csv', className: 'btn purple btn-outline '}*/
            ],
            "serverSide": true,
            "ajax": {
                url: "ManageAccountsJson",
                type: "POST",
                data: function (d) {
                    d.SearchGroupName = $("#groupName").val();
                    d.SearchUserLogin = $("#userLogin").val();
                    d.SearchCategorys = $("#categorys").val();
                    d.SearchContactNumber = $("#contactNumber").val();
                    d.enabled = $("#enabled").val();
                    d.accountType = $("#accountType").val();
                    d.personName = $("#personName").val();
                }
            },
            "order": [
                [2, 'asc'],
            ],
            "columns": [
                {
                    orderable: false,
                    "data": null,
                    "render": function (data, type, row, meta) {
                        return "<input type='checkbox' name='partyId' value='" + row.partyId + "'/>";
                    }
                },
                {"data": null,"targets": 0, orderable: false},
                {
                    "name": "groupName", "data": null, "render": function (data, type, row, meta) {
                    var typeRemark = $("#typeRemark").val();
                    var groupName = row.groupName;
                    var title = row.groupName;
//                    if(groupName.length>12)
//                    {
//                        groupName = groupName.substring(0,12) + "...";
//                    }
                    if(typeRemark=="group") {
                        return Mustache.render($("#url").html(), {
                            partyId: data.partyId,
                            groupName: groupName,
                            title: title
                        })
                    }else{
                        return "<span title='title'>" + groupName + "</span>";
                    }
                }
                },
                /*{
                    "name": "personName", orderable: false, "data": null, "render": function (data, type, row, meta) {
                    var personName = row.personName;
                    if (personName) {
                        return personName;
                    } else {
                        return "";
                    }
                }
                },*/
                {orderable: false, "data": null, "render": function (data, type, row, meta) {
                    var userLoginId = row.userLoginId;
                    var title = row.userLoginId;
//                    if (userLoginId.length > 12) {
//                        userLoginId = userLoginId.substring(0, 12) + "...";
//                    }
                    return "<span title='" + title + "'>" + userLoginId + "</span>";
                }
                },
                {orderable: false, "data":  null, "render": function (data, type, row, meta) {
                    var type = row.type;
                    var description = row.description;
                    var fullName = row.fullName;
                    if(type!=null&&type=="subAccount")
                    {
                        return fullName;
                    }else
                    {
                        return row.isPartner ? "合伙人" : description;
                    }
                }},
                {"data": "createdStamp"},
                {orderable: false, "data": "contactNumber"},
                {
                    orderable: false, "data": null, "render": function (data, type, row, meta) {
                    var address1 = row.address1;
                    var geoName = row.geoName;
                    if (geoName != null && address1 != null) {
                        var title = geoName + ' ' + address1;
                        var add = geoName + ' ' + address1;
//                        if (add.length > 16) {
//                            add = add.substring(0, 16) + "...";
//                        }
                        return "<span title='" + title + "'>" + add + "</span>";
                    }
                    if (geoName == null && address1 != null) {
                        var title = address1;
                        var add = address1;
//                        if (add.length > 16) {
//                            add = add.substring(0, 16) + "...";
//                        }
                        return "<span title='" + title + "'>" + add + "</span>";
                    }
                    if (geoName == null && address1 == null) {
                        return " <span style='color:red'>该机构没有填写联系地址</span>";
                    }
                    if (geoName != null && address1 == null) {
                        var title = geoName;
                        var add = geoName;
//                        if (add.length > 16) {
//                            add = add.substring(0, 16) + "...";
//                        }
                        return "<span title='" + title + "'>" + add + "</span>";
                    }
                }
                },
                {orderable: false, "data": null,"render": function (data, type, row, meta) {
                    var CueTime = row.CueTime;
                    var enddate = new Date(CueTime);
                    var now = new Date();
                    if(now.getTime()<=enddate.getTime())
                    {
                        return "<span style='color:green'>"+CueTime+"</span>";
                    }else
                    {
                        return "<span style='color:red'>"+CueTime+"</span>";
                    }
                }},
                {
                    orderable: false, "data": null, className: "control-col","render": function (data, type, row) {
                    var typeRemark = $("#typeRemark").val();
                    if(typeRemark=="group") {
                        return Mustache.render($("#operatorCol").html(),
                                {
                                    userLoginId: row.userLoginId,
                                    partyId: row.partyId,
                                    lockDisplay: row.enabled == "Y" ? "" : "none",
                                    unlockDisplay: row.enabled == "Y" ? "none" : ""
                                });
                    }else {
                        return Mustache.render($("#operatorPersonCol").html(),
                                {
                                    userLoginId: row.userLoginId,
                                    partyId: row.partyId,
                                    lockDisplay: row.enabled == "Y" ? "" : "none",
                                    unlockDisplay: row.enabled == "Y" ? "none" : ""
                                });
                    }
                }
                }

            ],
            "fnDrawCallback": function(){
                var api = this.api();
                var startIndex= api.context[0]._iDisplayStart;//获取到本页开始的条数
                api.column(1).nodes().each(function(cell, i) {
                    //此处 startIndex + i + 1;会出现翻页序号不连续，主要是因为startIndex 的原因,去掉即可。
                    cell.innerHTML = startIndex + i + 1;
                });
            }
        });
        $("#nameCondition").hide();

    })

    function editManage(id) {
        displayInLayer('编辑账号', {
            requestUrl: 'showManage?userLoginId=' + id, height: 600, width: 700, end: function () {
                accountTable.ajax.reload();
            }
        })
    }

    //子账户账户编辑
    function editManageForCompany(id) {
        displayInLayer2('编辑账号', {
            requestUrl: 'showManageForCompany?userLoginId=' + id, height: 600, width: 800, end: function () {
                accountTable.ajax.reload();
            }
        })
    }

    //个人账户续费
    function renewVip(id) {
        displayInLayer('账户续费', {
            requestUrl: 'renewVip?partyId='+id, height: "300px", width: 400, end: function () {
                accountTable.ajax.reload();
            }
        })
    }

    function batchPassRegistration(){
        var checkedUser = [];
        $("input[name=partyId]:checked").each(function(){
            checkedUser.push($(this).val());
        });

        if(checkedUser.length){
            displayInLayer('账户续费', {
                requestUrl: 'renewVip?partyId='+checkedUser.join(","), height: "60%", width: 600, end: function () {
                    accountTable.ajax.reload();
                }
            })
        }else{
            showError("请选择账户后操作！");
        }
    }


    function changeCondition(value) {
        if(value=="person")
        {
            $("#nameCondition").show();
            $("input[name=groupName]").val("");
            $("#groupCondition").hide();
            $("#categorys").val("");
            $("#groupType").hide();
        }else{
            $("input[name=personName]").val("");
            $("#nameCondition").hide();
            $("#groupCondition").show();
            $("#groupType").show();
        }
    }

    //恢复与禁用
    function forbidOrRecover(userLoginId, isRecover) {
        var confirmIndex = getLayer().confirm("是否确认禁用或恢复该账户?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                url: isRecover ? "RecoverAccount" : "ForbidAccount",
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

    //重置密码
    function resetpassword(userLoginId)
    {
        var confirmIndex = getLayer().confirm("确定重置该用户密码吗?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                url: "resetPassword",
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

    //账户删除
    function deleteAccount(userLoginId) {
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

    //账户查询
    function queryAccount() {
        var accountType = $("#accountType").val();
        $("#typeRemark").val(accountType);
        accountTable.ajax.reload();
        if(accountType == "group")
        {
            $("#roleType").html("类型");
        }else{
            $("#roleType").html("姓名");
        }
    }
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:resetpassword('{{userLoginId}}');" title="重置密码"> <i class="fa fa-lock"></i> </a>
    <a class="btn btn-md green btn-outline" href="javascript:;editManage('{{userLoginId}}');" title="修改"> <i
            class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" style="display: {{lockDisplay}}" href="javascript:;" title="禁用" onclick="forbidOrRecover('{{userLoginId}}', false)"> <i class="fa fa-lock"></i> </a>
    <a class="btn btn-md green btn-outline" style="display: {{unlockDisplay}}" href="javascript:;" title="解除禁用" onclick="forbidOrRecover('{{userLoginId}}', true)"> <i class="fa fa-unlock"></i> </a>
    <#--<a class="btn btn-md red btn-outline" href="javascript:;" title="删除"> <i class="fa fa-remove"></i> </a>-->
    <a class="btn btn-md red btn-outline" href="javascript:;" title="删除" onclick="deleteAccount('{{userLoginId}}')"> <i class="glyphicon glyphicon-remove"></i> </a>
    <a class="btn btn-md yellow btn-outline" href="javascript:;renewVip('{{partyId}}')" title="续费"> <i
            class="glyphicon glyphicon-shopping-cart"></i> </a>
</script>
<script id="operatorPersonCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:resetpassword('{{userLoginId}}');" title="重置密码"> <i class="fa fa-lock"></i> </a>
    <a class="btn btn-md green btn-outline" href="javascript:;editManageForCompany('{{userLoginId}}');" title="修改"> <i
            class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" style="display: {{lockDisplay}}" href="javascript:;" title="禁用" onclick="forbidOrRecover('{{userLoginId}}', false)"> <i class="fa fa-lock"></i> </a>
    <a class="btn btn-md green btn-outline" style="display: {{unlockDisplay}}" href="javascript:;" title="解除禁用" onclick="forbidOrRecover('{{userLoginId}}', true)"> <i class="fa fa-unlock"></i> </a>
    <#--<a class="btn btn-md red btn-outline" href="javascript:;" title="删除"> <i class="fa fa-remove"></i> </a>-->
    <a class="btn btn-md red btn-outline" href="javascript:;" title="删除" onclick="deleteAccount('{{userLoginId}}')"> <i class="glyphicon glyphicon-remove"></i> </a>
</script>
<script type="text/html" id="url">
    <a href="#nowhere" onclick="javascript:displayInLayer('认证信息', {requestUrl: 'ViewQualification?partyId={{partyId}}', height: 600, scrollContent:true});" title="{{title}}">{{groupName}}</a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">账号管理</span>
        </div>
        <div class="actions">
            <a href="#nowhere" onclick="queryAccount()" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
            <input hidden id="typeRemark" value="group">
        </div>
    </div>
    <div class="portlet-body">
        <form id="searchForm" name="searchForm">
            <div class="row">
                <div class="form-group col-sm-2">
                    <label class="control-label">账户类型</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                        <select name="accountType" class="form-control select" onchange="changeCondition(this.value)" id="accountType">
                            <option value="group">企业/机构</option>
                            <option value="person">子账户</option>
                        </select>
                    </div>
                </div>
                <div class="form-group col-sm-2" id="groupCondition">
                    <label class="control-label">机构名称</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                        <input type="text" name="groupName" class="form-control" id="groupName" maxlength="20"/>
                    </div>
                </div>
                <div class="form-group col-sm-2" id="nameCondition">
                    <label class="control-label">姓名</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                        <input type="text" name="personName" class="form-control" id="personName"/>
                    </div>
                </div>
                <div class="form-group col-sm-2">
                    <label class="control-label">账号</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                        <input type="text" name="userLogin" class="form-control" id="userLogin"/>
                    </div>
                </div>
                <div class="form-group col-sm-2" id="groupType">
                    <label class="control-label">机构类型</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <#assign enumerations = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), [], false)>
                        <select class="form-control select" name="type" id="categorys">
                            <option value="">-请选择-</option>
                        <#list enumerations as category>
                            <option value="${category.roleTypeId}">${category.description}</option>
                        </#list>
                        </select>
                    </div>
                </div>
                <div class="form-group col-sm-2">
                    <label class="control-label">账户状态</label>
                    <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                       <select class="form-control select" name="enabled" id="enabled">
                           <option value="Y">可用</option>
                           <option value="J">禁用</option>
                       </select>
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

        <table class="table table-striped table-bordered table-hover order-column" id="accounts">
            <thead>
            <tr>
                <th>选择</th>
                <th>
                    序号
                </th>
                <th>所属机构</th>
                <#--<th>姓名</th>-->
                <th>账号</th>
                <th id="roleType">类型</th>
                <th>创建时间</th>
                <th>联系电话</th>
                <th <#--class="hidden-md"-->>联系地址</th>
                <th>到期时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>