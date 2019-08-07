<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>

<script type="text/javascript">
    var oTable;
    $(function(){
        oTable = initDatatables('#sample_1', {
            // Internationalisation. For more info refer to http://datatables.net/manual/i18n
            "language": {
                "aria": {
                    "sortAscending": ": activate to sort column ascending",
                    "sortDescending": ": activate to sort column descending"
                },
                "emptyTable": "无记录",
                "info": "当前 _START_ - _END_ 总共 _TOTAL_ 条",
                "infoEmpty": "无记录",
                "infoFiltered": "(filtered1 from _MAX_ total entries)",
                "zeroRecords": "无记录"
            },
            "ajax": {
                url: "GetTickets",
                type: "POST"
            },
            "serverSide": true,
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "title","render": function(data, type, row, meta) {
                    var title = row.title  ==null?"":row.title;
                    var add = row.title ==null?"":row.title;
//                    if (title.length > 12) {
//                        title = title.substring(0, 12) + "...";
//                    }
                    return "<span title='" + add + "'>" + title + "</span>";
                }
                },
                {"data": null, "render": function(data, type, row, meta) {
                    var result = "";
                    $(data.ticketRoleList).each(function() {
                        if(this.roleTypeId != "CASE_ROLE_OWNER"){
                            if(this.status == "SUCCESS"){
                                result += Mustache.render($("#isConfirmed").html(), {
                                            groupName: this.groupName,
                                            description: this.description
                                        }) + ","
                            }else if(this.status == "ONGOING"){
                                result += Mustache.render($("#isConfirming").html(), {
                                            groupName: this.groupName,
                                            description: this.description,
                                            roleTypeId: this.roleTypeId,
                                            ticketId: data.id,
                                            caseId: data.caseId
                                        }) + ","
                            }else if(this.status == "NONE"){
                                result +=  Mustache.render($("#none").html(), {
                                            description: this.description
                                        }) + ","
                            }
                        }
                    });
                    result = result.slice(0, result.length - 1);
                    return result;
                }},
                {"data": "startTime"},
                {"data": "endTime"},
                {"data": null, "render": function(data, type, row, meta) {
                    var result = ""
                    if(data.ticketStatus == "unComplete"){
                        result += "<i class=\"font-red fa fa-unlock\" title=\"缺少服务提供商\"></i>&nbsp;"
                    } else if(data.ticketStatus == "complete") {
                        result += "<i class=\"font-green fa fa-lock\" title=\"已锁定服务提供商\"></i>"
                    }
                    if(data.caseId){
                        result += Mustache.render($("#showCase").html(), {
                            caseId: data.caseId
                        })
                    }
                    return result;
                }},
                {"data": null, "render": function(data, type, row, meta) {
                    var displayCreateCase = "inline-block";
                    var displayDelete = "inline-block";
                    if(data.caseId){
                        displayCreateCase = "none";
                        displayDelete = "none";
                    }
                    return Mustache.render($("#operation").html(), {
                        ticketId: data.id,
                        displayCreateCase: displayCreateCase,
                        displayDelete: displayDelete,
                        ticketStatus: data.ticketStatus
                    });
                    //return "操作"
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
            "ordering": false
        });
    });
    //选择参加竞选的参与方
    function showPartyReason(description, roleTypeId, ticketId, caseId) {
        displayInLayer(description, {
            requestUrl: "TicketCandidates?roleTypeId=" + roleTypeId + "&ticketId=" + ticketId + "&caseId=" + caseId,
            height: 400,
            end: function() {
                oTable.ajax.reload();
            }
        })
    }
    function createCaseByTicket(ticketId, ticketStatus) {
        if(ticketStatus == "unComplete"){
            var openLayer = getLayer();
            openLayer.confirm("参与机构未全部就位，确定创建CASE吗？", {
                btn:["是", "否"]
            }, function(index) {
//                var index = openLayer.getFrameIndex(window.name); //先得到当前iframe层的索引
                getLayer().close(index); //再执行关闭
                $.ajax({
                    url: "CreateCaseByTicket",
                    type: "POST",
                    data: {ticketId: ticketId},
                    dataType: "json",
                    success: function(data) {
                        showInfo(data.data);
                        oTable.ajax.reload();
                    }
                });
            }, function(index) {
                getLayer().close(index);
            });
        }else{
            $.ajax({
                url: "CreateCaseByTicket",
                type: "POST",
                data: {ticketId: ticketId},
                dataType: "json",
                success: function(data) {
                    showInfo(data.data);
                    oTable.ajax.reload();
                }
            });
        }
    }
    function deleteCaseByTicket(ticketId) {

        var confirmIndex = getLayer().confirm("确定是否归档?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                url: "deleteCaseByTicket",
                type: "POST",
                data: {ticketId: ticketId},
                dataType: "json",
                success: function (data) {
                    if (data.data) {
                        showInfo(data.data);
                        oTable.ajax.reload();
                    }
                }
            })
            getLayer().close(confirmIndex);
        })
    }
</script>
<style type="text/css">
    table tbody td {
        white-space: nowrap;
    }
</style>
<script type="text/html" id="isConfirmed">
    <span class="font-green" title="{{groupName}}">{{description}}<i class="fa fa-lock"></i></span>
</script>
<script type="text/html" id="isConfirming">
    <a href="javascript:;" onclick="showPartyReason('{{description}}', '{{roleTypeId}}', '{{ticketId}}', '{{caseId}}')" class="font-red">{{description}}<i class="fa fa-bell"></i></a>
</script>
<script type="text/html" id="none">
    {{description}}
</script>
<script type="text/html" id="showCase">
    <a href="javascript:;" onclick="displayInside('${request.contextPath}/control/CaseHome?caseId={{caseId}}')"><i class="font-green fa fa-file-text" title="已创建CASE"></i></a>
</script>
<script type="text/html" id="operation">
    <a class="btn btn-md green btn-outline" href="javascript:;" onclick="createCaseByTicket('{{ticketId}}','{{ticketStatus}}')" title="创建CASE" style="display: {{displayCreateCase}}"> <i class="fa fa-gavel"></i> </a>
    <#--<a class="btn btn-md red btn-outline" href="javascript:;" title="删除" style="display: {{displayDelete}}"> <i class="fa fa-remove"></i> </a>-->
    <a class="btn btn-md red btn-outline" href="javascript:;" onclick="deleteCaseByTicket('{{ticketId}}')" title="归档"> <i class="fa fa-ban"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">发布历史</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>标题</th>
                <th>服务类型</th>
                <th>发布时间</th>
                <th>截止时间</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>