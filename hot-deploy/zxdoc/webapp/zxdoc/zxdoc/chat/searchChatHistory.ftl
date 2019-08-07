<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script type="text/javascript">

    var templateTable;
    $(function () {
        templateTable = initDatatables("#sample_1", {
            "serverSide": true,
            "paging": false, // 禁止分页
            "ordering":false,//禁止排序
                    "ajax": {
                url: "chatHistoryJson",
                type: "POST"
            },
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {
                    "data": "fullName",
                    "render": function (data, type, row, meta) {
                        var partyId = row.partyId;
                        var fullName = row.fullName;
                        var openFireJid = row.openFireJid;
                        return "<div jid='" + openFireJid + "'><img id='clickImg' class='avatar' style='width: 51px; height: 51px;margin:5px 10px;' alt='' src='/content/control/partyAvatar?partyId=" + partyId + "'/><a href='javascript:void(0);' onclick='openOrCreateChatRoom(\"" + partyId + "\")'>" + fullName + "</a></div>";
                    }
                },
                {"data": "groupName"},
                {"data": "lastChatTime"}
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


<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">最近聊天历史</span>
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>姓名</th>
                <th>公司名</th>
                <th>上次打开聊天室时间</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>