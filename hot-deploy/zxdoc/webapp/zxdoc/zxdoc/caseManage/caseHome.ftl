<link href="${request.contextPath}/static/case-home.min.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<style>
    .skipped {
        text-decoration: line-through;
    }

    .feeds li .col2 {
        width: 120px;
        margin-left: -120px;
    }

    #caseTaskInfo .mt-list-item .list-icon-container {
        float: right;
    }

    #caseTaskInfo .mt-list-item .list-item-content {
        padding-left: 5px;
    }
    body{
        overflow-y: hidden;
    }
</style>
<script type="text/javascript">
    var caseMemberTable;
    var caseCooperationRecordTable;
    $(function () {
        $("#caseRemarkForm").validationEngine("attach", {promptPosition: "bottomLeft"});
        //获取case进度
        getProgress();
        isTicketCase("${caseId}");

        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            var target = $(e.target).attr("href") // activated tab
            if(target.indexOf("tab_1") > -1){
                parent.adjustContentFrame();
            }
        });

        $(".tab-content").on("shown.bs.collapse hidden.bs.collapse", function(){
            parent.adjustContentFrame();
        })
    })

    //根据case类型是否隐藏相关发布信息
    function isTicketCase(caseId) {
        $.ajax({
            type:"post",
            url:"isTicketCase",
            data:{caseId:caseId},
            dataType:"json",
            success:function (data) {
                if(data.isTicket=="Y") {
                    $("#caseInfoId").show();
                }
            }
        })
    }

    function adjustChatHisTable() {
    <#if !isPartyGroup>
        if (!caseCooperationRecordTable) {
            caseCooperationRecordTable = initDatatables("#caseCooperationRecords", {
                "ajax": {
                    url: "GetCaseCooperationRecordJson?caseId=${caseId}",
                    type: "POST"
                },
                "rowId": 'staffId',
                autoWidth: false,
                "columns": [
                    {orderable: false, "data": "chatRoomName"},
                    {orderable: false, "data": "groupNames"},
                    {orderable: false, "data": "createdStamp", width: 200},
                    {
                        orderable: false, "data": null, width: 50, "render": function (data, type, row) {
                        return Mustache.render($("#chatRoomOperation").html(), {
                            chatRoomName: data.chatRoomName,
                            chatRoomId: data.chatRoomId
                        });
                    }
                    },
                ],
                "order": [
                    [2, 'desc'],
                ],
                paging: false,
                initComplete: function(){
                    parent.adjustContentFrame();
                }
            });
        }else{
            caseCooperationRecordTable.ajax.reload();
        }
    </#if>
//        setTimeout(function(){caseCooperationRecordTable.columns.adjust()}, 100);
    }

    function adjustFileTable() {
        if(datable){
            setTimeout(function () {
               /* var height = $("#example").height() + 30;
                var width = $("#fileListWrapper").width();
                var windowheight = $(window).height() - 200;
                if(windowheight < height ){
                    height = windowheight + 10;
                    width = width + 15
                }
                $("#example").parent().attr("style","height:" + height + "px;overflow-x: auto;width:" + width + "px;" )*/
                datable.columns.adjust();
            }, 100);
        }
    }

    function initCaseMemberTable(obj) {
        if (!caseMemberTable) {
            caseMemberTable = initDatatables("#casePartyMembers", {
                "ajax": {
                    url: "CasePartyMemberJson?partyId=${groupId}&caseId=${caseId}",
                    type: "POST"
                },
                "rowId": 'staffId',
                "columns": [
                    {orderable: false, "data": "groupRoleType"},
                    {orderable: false, "data": "groupName"},
                    {orderable: false, "data": "fullName"},
                    {orderable: false, "data": "description"},
                    {
                        orderable: false, "data": null, "render": function (data, type, row) {
                        if (!data.editable) {
                            return "";
                        } else {
                            if (!data.isPerson) {
                                if (data.isPersonManager && data.isManager) {
                                    return Mustache.render($("#operatorCol").html(), {memberId: data.personPartyId});
                                } else if (!data.isManager && !data.isPersonManager) {
                                    return Mustache.render($("#operatorRemoveCol").html(), {memberId: data.personPartyId});
                                } else {
                                    return "";
                                }
                            } else {
                                return "";
                            }
                        }

                    }
                    },
                ],
                paging: false,
                initComplete: function(){
                    parent.adjustContentFrame();
                }
            });
        }
    }
    function getProgress() {
        $.caseManage.getProgress('${partyId}', '${caseId}');
    }

    function getCaseRemark() {
        $.ajax({
            url: "CaseRemarkJson",
            type: "POST",
            data: {"caseId": "${caseId}"},
            dataType: "json",
            async: true,
            success: function (content) {
                var data = content.data;
                if (data) {
                    var remarkTpl = $("#caseRemarkTpl").html(), htmlStr = "";
                    for (var i in data) {
                        var row = data[i];
                        htmlStr += Mustache.render(remarkTpl, {remark: row.remark, createTime: row.createdStamp});
                    }
                    $("#caseRemarks").html(htmlStr);
                }
                parent.adjustContentFrame();
            }
        });
    }

    function changeMember(partyId, caseId, memberId) {
        addCasePartyMember(partyId, caseId, memberId);
    }
    function removeMember(memberId, caseId) {
        $.ajax({
            url: "RemoveMember",
            type: "get",
            data: {"memberId": memberId, "caseId": caseId},
            dataType: "json",
            success: function (data) {
                showInfo(data.data);
                if (memberId == "${partyId}") {
                    displayInside('ProviderCases')
                } else {
                    caseMemberTable.ajax.reload();
                }
            }
        })
    }
    function addCasePartyMember(partyId, caseId, memberId) {
        displayInLayer(!memberId ? "人员选择" : "人员修改", {
            requestUrl: !memberId ? "MemberChooser" : "MemberChooser?memberId=" + memberId,
            data: {"partyId": partyId, "caseId": caseId},
            layer: {
                maxmin: false,
                area: ['400px', '300px'],
                end: function () {
                    caseMemberTable.ajax.reload();
                }
            }
        })
    }
    function createCaseChatRoom(caseId) {
        displayInLayer("新建协作主题", {
            requestUrl: "CreateCaseChatRoom",
            data: {"caseId": caseId},
            width: 450,
            layer: {
                end: function () {
                    if (parent.chatRoomInfo.chatRoomJID) {
                        openCaseChat(parent.chatRoomInfo.chatRoomName, parent.chatRoomInfo.chatRoomJID);
                        parent.chatRoomInfo = {};
//                        caseCooperationRecordTable.ajax.reload(null, false);
                    }
                }
            }
        })
    }

    function initCaseTask() {
        var fileList = unescapeHtmlText('${filePathList}')
        $.ajax({
            url: "initCaseTask",
            type: "POST",
            data: {
                "caseId": "${caseId}",
                partyGroupId: '${groupId}',
                fileList: fileList,
                companyPartyId: '${companyPartyId}'
            },
            dataType: "html",
            async: true,
            success: function (content) {
                $("#caseTaskInfo").html(content);
                parent.adjustContentFrame();
            }
        });
    }

    //case合同的初始化
    function initCaseContract(caseId) {
        $.ajax({
            type: "post",
            url: "initCaseContract",
            data: {caseId: caseId},
            dataType: "html",
            success: function (content) {
                $("#caseContractInfo").html(content);
                parent.adjustContentFrame();
            }
        })
    }

    function finishCaseNote(progressId) {
        displayInLayer('完成步骤', {
            requestUrl: 'CompleteCaseStep?progressId=' + progressId, height: 300, end: function () {
                getProgress();
            }
        })
    }

    //打开case概述
    function openCaseSummary(caseId) {
        $.ajax({
            type: "post",
            data: {caseId: caseId},
            url: "openCaseSummary",
            success: function (str) {
                top.layer.open({
                    type: 1,
                    zIndex: 10,
                    maxmin: true,
                    title: "CASE概述",
                    closeBtn: 1, //不显示关闭按钮
                    shade: 0.2,
                    area: ['600px', '500px'],
//                    offset: ['100px', '400px'],
//                    time: 99999999, //不自动关闭
                    shift: 2,
                    content: str.summary==null?"":str.summary,
                });
            }
        })
    }

    //打开相关信息
    function showTicketInfo(caseId) {
        top.displayInLayer2("相关发布信息", {
            requestUrl: 'showTicketInfo?caseId=' + caseId, height: 400,width:600})
    }
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:;"
       onclick="changeMember('${groupId}', '${caseId}' ,'{{memberId}}')" title="重新指定"> <i class="fa fa-pencil"></i> </a>
</script>
<script id="operatorRemoveCol" type="text/html">
    <a class="btn btn-md red btn-outline" href="javascript:;" onclick="removeMember('{{memberId}}', '${caseId}')"
       title="移除"> <i class="fa fa-remove"></i> </a>
</script>
<script id="chatRoomOperation" type="text/html">
    <a class="btn btn-md grey-salsa btn-outline" href="javascript:;"
       onclick="openCaseChat('{{chatRoomName}}', '{{chatRoomId}}')"> <i class="fa fa-comments"></i> </a>
</script>
<script id="fileDownloadLink" type="text/html">
    <a href="{{fileURL}}">{{fileName}}</a>
</script>
<script id="filesTable" type="text/html">
    <table class="table table-hover table-striped table-bordered">
        <tbody>
        <tr class="{{templateFileClass}}">
            <td style="width: 80px"><label class="control-label">模板文件</label></td>
            <td>
                {{{templateFileLinks}}}
            </td>
        </tr>
        <tr class="{{completeFileClass}}">
            <td style="width: 80px"><label class="control-label">发布文件</label></td>
            <td>
                {{{completedFileLinks}}}
            </td>
        </tr>
        </tbody>
    </table>
</script>
<script id="caseProgressChildStepAtom" type="text/html">
    <li class="{{childStepDoneClass}}">
        <div class="timeline list-item-content">
            <div class="timeline-badge">
                <img class="timeline-badge-userpic"
                     src="/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey=${externalLoginKey}">
                <div class="timeline-notice">
                    <i class="icon-bell hide font-red" style="font-size:20px;cursor:pointer;display: {{displayNotice}}"
                       title="提醒他"></i>
                    <i class="fa fa-comment-o hide font-green" style="vertical-align:top;font-size:20px;cursor:pointer"
                       title="沟通"></i>
                </div>
            </div>
            <div class="{{arrowClass}}"></div>
            <span class="{{progressTitleClass}}">{{progressTitle}}</span><br/>
            <span class="timeline-body-time font-grey-cascade pull-right">{{groupName}}  {{progressTime}}</span>
            <div class="timeline-body-content">
                {{completeDesc}}&nbsp;
                <div class="btn-group" style="float:left;display: {{displayBtn}};">
                    <button class="btn btn-circle green btn-outline btn-sm dropdown-toggle" type="button"
                            data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                        操作
                        <i class="fa fa-angle-down"></i>
                    </button>
                    <ul class="dropdown-menu" role="menu">
                        <li>
                            <a href="javascript:$.caseManage.uploadCompletedCaseFile('${filePathList}', '${companyPartyId}', '{{progressId}}');">发布文档 </a>
                        </li>
                        <li>
                            <a href="javascript:$.caseManage.completeCaseStep('{{progressId}}', true);">忽略 </a>
                        </li>
                        <li>
                            <a href="javascript:finishCaseNote('{{progressId}}');">完成步骤 </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="list-item-content">
            <div class="timeline-body-content">
                {{{filesTable}}}
            </div>

        </div>
    </li>
</script>
<script id="caseProgressAtom" type="text/html">
    <div class="portlet-body">
        <div class="mt-element-list">
            <div class="mt-list-container list-simple ext-1 group">
                <a class="list-toggle-container" data-toggle="collapse" href="{{divIdChooser}}" aria-expanded="false">
                    <div class="{{stepDoneClass}}">{{stepTitle}}
                        <span class="badge badge-default pull-right bg-white font-green bold">{{childStepAmount}}</span>
                    </div>
                </a>
                <div class="panel-collapse collapse {{isDoing}}" id="{{divId}}">
                    <ul>
                        {{{caseProgressChildStepAtom}}}
                    </ul>
                </div>
            </div>
        </div>
    </div>
</script>
<script type="text/html" id="caseRemarkTpl">
    <li>
        <div class="col1" style="width: 85%">
            <div class="cont">
                <div class="cont-col1">
                    <div class="label label-info">
                        <i class="fa fa-bullhorn"></i>
                    </div>
                </div>
                <div class="cont-col2">
                    <div class="desc"> {{remark}}</div>
                </div>
            </div>
        </div>
        <div class="col2" style="width: 200px;float: right">
            <div class="date" style="min-width: 50px"> {{createTime}}</div>
        </div>
    </li>
</script>
<div class="profile">
    <div class="tabbable-line tabbable-full-width">
        <ul class="nav nav-tabs">
            <li class="active">
                <a href="#tab_1_1" data-toggle="tab"> <i class="font-green fa fa-home"></i>概要 </a>
            </li>
            <li>
                <a href="#tab_1_2" onclick="initCaseMemberTable(this)" data-toggle="tab"> <i
                        class="font-green fa fa-user"></i>成员 </a>
            </li>
            <li>
                <a href="#tab_1_3" data-toggle="tab" onclick="adjustFileTable()"> <i class="font-green fa fa-file"></i>文件
                </a>
            </li>
            <li>
                <a href="#tab_1_6" data-toggle="tab" onclick="initCaseContract('${caseId}')"> <i
                        class="font-green fa fa-link"></i>合同 </a>
            </li>
        <#if !isPartyGroup>
            <li>
                <a href="#tab_1_8" data-toggle="tab" onclick="initCaseTask()"> <i class="font-green fa fa-calendar"></i>任务
                </a>
            </li>
            <li>
                <a href="#tab_1_11" data-toggle="tab" onclick="adjustChatHisTable()"> <i
                        class="font-green fa fa-comments"></i>协作历史 </a>
            </li>
        </#if>
            <li>
                <a href="#tab_1_22" data-toggle="tab" onclick="getCaseRemark()"> <i
                        class="font-green fa fa-sticky-note"></i>备忘录 </a>
            </li>
        </ul>
        <div class="tab-content">
            <div class="tab-pane active" id="tab_1_1">
                <div class="row">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-6 profile-info">
                                <h1 class="font-green sbold uppercase">${caseName}
                                <#if !isPartyGroup>
                                    <a class="btn btn-md green btn-outline" href="javascript:;"
                                       onclick="createCaseChatRoom('${caseId}')" title="创建case协作讨论组"> <i
                                            class="fa fa-comments"></i> </a>
                                </#if>
                                </h1>
                                <p> 发布信息时提供的信息内容展示. </p>
                                <p>
                                    <a href="javascript:showTicketInfo('${caseId}');" id="caseInfoId" style="display: none"> 相关发布信息 </a>
                                    <a href="javascript:openCaseSummary('${caseId}');"> CASE概述</a>
                                </p>
                                <ul class="list-inline">
                                <#if area?has_content>
                                    <li>
                                        <i class="fa fa-map-marker"></i> ${area?default('')} </li>
                                </#if>
                                    <li>
                                        <i class="fa fa-calendar"></i> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(caseCreateTime, "yyyy-MM-dd")}
                                    </li>
                                    <li>
                                        <i class="fa fa-briefcase"></i> ${caseType?default('')} </li>
                                </ul>
                            </div>
                            <!--end col-md-6-->
                            <div class="col-md-12">
                                <div class="portlet light portlet-fit bg-inverse bordered">
                                    <div class="portlet-title">
                                        <div class="caption font-red sbold"> CASE进度</div>
                                    </div>
                                    <div class="portlet-body">
                                        <div class="white-bg white-bg" id="caseProgress">

                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!--end col-md-4-->
                        </div>
                        <!--end row-->
                    </div>
                </div>
            </div>
            <!--tab_1_2-->
            <div class="tab-pane" id="tab_1_2">
                <div class="row">
                    <div class="col-md-12">
                        <form id="casePartyMember">
                            <input type="hidden" name="caseId" value="${caseId}"/>
                            <input type="hidden" name="groupPartyId" value="${groupId}"/>
                            <table id="casePartyMembers"
                                   class="table table-striped table-bordered table-advance table-hover">
                                <thead>
                                <tr>
                                    <th>
                                        <i class="fa fa-briefcase"></i> 类型
                                    </th>
                                    <th>
                                        <i class="fa fa-briefcase"></i> 参与方
                                    </th>
                                    <th>
                                        <i class="fa fa-user"></i> 人员
                                    </th>
                                    <th>
                                        <i class="fa fa-clock-o"></i> 角色
                                    </th>
                                    <th>
                                    <#if (isRoot && !isyManager) || !isManagerParty>
                                        <a class="btn btn-md yellow btn-outline" href="javascript:;" title="指派人员"
                                           onclick="addCasePartyMember('${groupId}', '${caseId}')"> <i
                                                class="fa fa-plus"></i> </a>
                                    </#if>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                        </form>
                    </div>
                </div>
            </div>
            <div class="tab-pane" id="tab_1_3">
            ${screens.render("component://ckfinder/widget/CommonScreens.xml#embedFileList")}
            </div>
            <!--end tab-pane-->
            <div class="tab-pane" id="tab_1_6">
                <div id="caseContractInfo">
                </div>
            </div>

            <div class="tab-pane" id="tab_1_8">
                <div id="caseTaskInfo">

                </div>
            </div>
            <div class="tab-pane" id="tab_1_11">
                <div class="portlet-body">
                    <table id="caseCooperationRecords"
                           class="table table-striped table-bordered table-advance table-hover">
                        <thead>
                        <tr>
                            <th>
                                <i class="fa fa-briefcase"></i> 协作主题
                            </th>
                            <th class="hidden-xs">
                                <i class="fa fa-user"></i> 参与方
                            </th>
                            <th>
                                <i class="fa fa-clock-o"></i> 开始时间
                            </th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </div>
            <!--tab-pane-->
            <div class="tab-pane" id="tab_1_22">
                <div class="scroller" data-height="290px" data-always-visible="1" data-rail-visible1="1">
                    <form id="caseRemarkForm" role="form" action="#" style="margin-bottom: 10px">
                        <input type="hidden" name="caseId" value="${caseId}"/>
                        <input type="hidden" name="partyId" value="${userLogin.partyId}"/>
                        <div class="form-group">
                            <label class="control-label">添加备忘录</label>
                            <input type="text" name="remark" maxlength="120" placeholder="备忘内容请不要超过120个字符"
                                   class="form-control validate[required,custom[noSpecial]]"/></div>
                        <div class="margiv-top-10">
                            <a href="javascript:;" onclick="$.caseManage.createCaseRemark()" class="btn green"> 添加 </a>
                        </div>
                    </form>
                    <ul id="caseRemarks" class="feeds">
                    </ul>
                </div>
            </div>
            <!--end tab-pane-->
        </div>
    </div>
</div>