<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<style type="text/css">
    table td{
        white-space: nowrap;
    }
</style>
<script type="text/javascript">

    var templateTable;
    $(function () {
        templateTable = initDatatables("#sample_1", {
            "serverSide": true,
            "ajax": {
                url: "chatPersonJson",
                type: "POST",
                data: function (d) {
                    d.personName = $("#personName").val();
                    d.groupName = $("#groupName").val();
                }
            },
            "order": [
                [1, 'desc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {
                    "data": "fullName",
                    "render": function (data, type, row, meta) {
                        var partyId = row.partyId;
                        var fullName = row.fullName;
                        var openFireJid = row.openFireJid;
                        return "<div jid='" + openFireJid + "'><img id='clickImg' class='avatar' style='width: 25px; height: 25px;margin:2px;' alt='' src='/content/control/partyAvatar?partyId=" + partyId + "'/><a href='javascript:void(0);' onclick='openOrCreateChatRoom(\"" + partyId + "\")'>" + fullName + "</a></div>";
                    }
                },
                {"data": "groupName"}
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
                var openLayer = getLayer();
                openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
            }
        });
    })


    function searchPersonByName() {
        $("#personResult").empty();
        $.ajax({
            type: "post",
            url: "addOtherPeople",
            data: {data: $("#fileName").val()},
            dataType: "json",
            success: function (result) {
                var html = "";
                var members = result.members;
                if (members.length == 0) {
                    html += "<br><div style='margin-left: 5px;'><b style='color: red'>没有相关人员</b></div>";
                }
                for (var i = 0; i < members.length; i++) {
                    html += "<div jid='" + members[i].openFireJid + "'><img id='clickImg' class='avatar' style='width: 51px; height: 51px;margin:5px 10px;' alt='' src='/content/control/partyAvatar?partyId=" + members[i].partyId + "'/><a href='javascript:void(0);' onclick='openOrCreateChatRoom(\"" + members[i].partyId + "\")'>" + members[i].fullName + "(" + members[i].groupName + ")</a></div>";
                }
                $("#personResult").html(html);
            }
        })
    }

    //打开或者创建聊天室
    function openOrCreateChatRoom(id) {
        //检查聊天室是否存在
        $.ajax({
            type: "post",
            url: "checkRoomExist",
            data: {personId: id},
            async: false,
            success: function (data) {
                getLayer().close(top.searchChatPartyIndex);
                if (data.data == "no") {
                    top.createInstantMessage(id);
                } else {
                    top.sendPrivateInvite(data.goalName, data.chatRoomJID, data.chatRoomName);
                    top.openInstantMessages(data.chatRoomName, data.chatRoomJID);
                }
            }
        })
    }
</script>
<#--<div style="margin-top: 5px;">
    <div class="input-group" style="width: 200px;margin-left: 5px;">
    <span class="input-group-addon input-circle-left">
        <a onclick="searchPersonByName();" class="icon-magnifier"></a>
    </span>
        <input type="text" class="form-control input-circle-right" id="fileName" placeholder="联系人名称">
    </div>
    <div id="personResult">
    </div>
</div>-->


<div class="portlet light ">
    <div class="portlet-body">
        <div class="row" style="width: 100%;margin-left: 1%">
            <div class="form-group" style="float: left;width: 44%">
                <label class="control-label">姓名</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="personName" class="form-control" style="width: 80%;"/>
                </div>
            </div>
            <div class="form-group " style="float: left;width: 44%">
                <label class="control-label">公司名</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <input type="text" id="groupName" class="form-control" style="width: 80%;"/>
                </div>
            </div>
            <div class="actions" style="margin-top: 25px;float:left;width: 10%">
                <a href="#nowhere" onclick="javascript:templateTable.ajax.reload();" class="btn btn-circle green">
                    <i class="fa fa-search"></i> 查询 </a>
            </div>
        </div>
        <#--<div class="portlet-title">
            <div class="caption font-dark">
                <i class="icon-settings font-green"></i>
                <span class="caption-subject bold uppercase">结果列表</span>
            </div>
        </div>-->
        <div class="portlet-body">
            <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>姓名</th>
                    <th>公司名</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>