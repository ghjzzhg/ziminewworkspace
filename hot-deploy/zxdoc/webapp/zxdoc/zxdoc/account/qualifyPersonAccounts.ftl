<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script type="text/javascript" src="${request.contextPath}/static/account.js?t=20170909"></script>
<script type="text/javascript">
    var accountTable;
    $(function () {
        accountTable = initDatatables("#qualifyAccounts", {
            buttons: [],
//            "serverSide": true,
            "ajax": {
                url: "QualifyPersonAccountsJson",
                type: "POST",
                data:function(){

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
                        return "<input type='checkbox' name='personId' value='" + row.personId + "'/>";
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
                {orderable: false,  "data": null,"render": function (data, type, row, meta) {
                    return Mustache.render($("#url").html(), {
                        personId: row.personId,
                        fullName:row.fullName
                    });
                } },
                {orderable: false, "data": "groupName"},
                {"data": "createdStamp"},
                {orderable: false, "data": "contactNumber"},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#operatorCol").html(), {personId: row.personId});
                }
                }
            ]
        })
    })
    function batchPassPersonQualification(){
        var checkedUser = [];
        $("input[name=personId]:checked").each(function(){
            checkedUser.push($(this).val());
        });
        if(checkedUser.length){
            $.account.passPersonQualification(checkedUser.join(","));
        }else {
            showError("请选择账户后操作！");
        }
    }
    function batchDeletePersonQualification(){
        var checkedUser = [];
        $("input[name=personId]:checked").each(function(){
            checkedUser.push($(this).val());
        })
        if(checkedUser.length){
            $.account.deletePersonQualify(checkedUser.join(","));
        }else {
            showError("请选择账户后操作！");
        }
    }
</script>
<script id="operatorCol" type="text/html">
        <a class="btn btn-md grey-salsa btn-outline" href="javascript:$.account.deletePersonQualify('{{personId}}');" title="拒绝"> <i class="fa fa-ban"></i> </a>
    <a class="btn btn-md green btn-outline" href="javascript:$.account.passPersonQualification('{{personId}}')"
       title="通过"> <i class="fa fa-gavel"></i> </a>
</script>
<script id="url" type="text/html">
    <a href="javascript:displayInLayer('认证信息',{requestUrl:'ViewQualificationPerson?personId={{personId}}', height: 600});">{{fullName}}</a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">实名认证</span>
        </div>
    </div>
    <div class="portlet-body">
        <div>
            <table class="table table-striped table-bordered table-hover order-column" id="qualifyAccounts">
                <thead>
                <tr>
                    <th>
                        选择
                    </th>
                    <th>
                        序号
                    </th>
                    <th>姓名</th>
                    <th>所属机构</th>
                    <th>注册时间</th>
                    <th>联系电话</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div class="margin-top-10">
            <a class="btn btn-md grey-salsa" href="javascript:batchDeletePersonQualification();" title="批量拒绝"> <i class="fa fa-ban"></i> </a>
            <a class="btn btn-md green" href="#nowhere" onclick="batchPassPersonQualification()" title="批量通过"> <i
                    class="fa fa-gavel"></i> </a>
        </div>
    </div>
</div>