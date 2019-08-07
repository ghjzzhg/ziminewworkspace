<#include "component://common/webcommon/includes/uiWidgets/kindeditor.ftl"/>
<link href="/images/lib/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap/js/bootstrap.js"></script>
<link href="/images/lib/select2/select2.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2-bootstrap.min.css" rel="stylesheet" type="text/css"/>
<script src="/images/lib/select2/select2.js" type="text/javascript"></script>
<script src="/images/lib/select2/select2_extended_ajax_adapter.js" type="text/javascript"></script>
<script src="/images/lib/select2/i18n/zh-CN.js" type="text/javascript"></script>
<style type="text/css">
    .ticket-party{
        display:inline-block;
        padding: 5px;
        margin:1px;
        background: lightskyblue;
    }
    .ticket-party .fa-remove{
        color:red;
        cursor: pointer;
        margin-left:10px;
    }
</style>
<script type="text/javascript">
    var s;
    $(function () {
        s = KindEditor.create('textarea[name="ticketContent"]');
        $("#partyTable").on("click", ".choose-party", function(){
            choosePartyGroup(this);
        }).on("click", ".fa-remove", function(){
            removeTicketParty(this);
        }).on("change", ".ticket-party-ctrl", function(){
            var $this = $(this), $tr = $this.closest("tr"), $chooser = $tr.find(".choose-party"), $parties = $tr.find(".ticket-parties");
            if($this.is(":checked")){
                $chooser.show();
            }else{
                $chooser.hide();
                $parties.empty();
                caseParties[$this.val()] = [];
            }
        })
    })
    function createTicket() {
        for(var roleType in caseParties){
            var chosenParties = caseParties[roleType];
            if(chosenParties.length){
                $("input[name=groupName_select_" + roleType + "]").val(chosenParties.join(","));
            }
        }

        var c = s.html();
        $("#ticketContent").val(c)
        var options = {
            beforeSubmit: function () {
                var validation = $('#form1').validationEngine('validate');
                return validation;
            },
            url: "SaveTicket",
            type: "post",
            dataType: "json",
            success: function(content) {
                showInfo(content.data);
                location.href = "ListTickets?iframe=true";
            }
        }
        $("#form1").ajaxSubmit(options);
    }

    function getTemplateName(type) {
        $("#TemplateId").empty();
        $.ajax({
            type:"post",
            url:"getTemplateName",
            data:{templateKey:type},
            dataType:"json",
            success:function (data) {
                var caseTemplate = data.caseTemplate;
                var html= "<option value=''>-请选择-</option>";
                for(var i=0;i<caseTemplate.length;i++)
                {
                    html += "<option value='" + caseTemplate[i].id + "'>" + caseTemplate[i].templateName + "(" + caseTemplate[i].version  + ")</option>";
                }
                $("#TemplateId").append(html);
            }
        })
    }
    var caseParties = {};
    function getRoles(name) {
        var $partyTable = $("#partyTable tbody");
        $partyTable.empty();
        caseParties = {};
        $.ajax({
            type:"post",
            url:"getRoles",
            data:{id:name},
            success:function (data) {
                var list = data.list;
                if(list!=null && list.length!=0)
                {
                    var html = "";
                    for (var i=0;i<list.length;i++)
                    {
                        var roleType = list[i].roleTypeId;
                        if(roleType != "CASE_ROLE_OWNER") {
                            caseParties[roleType] = [];
                            html += "<tr>";
                            html += "<td>" + list[i].description + "</td>";
                            html += "<td><input type='checkbox' class='ticket-party-ctrl' value='" + roleType + "' name='roles'></td>";
                            html += "<td><div class='ticket-parties'></div><input type='hidden' name='groupName_select_" + roleType + "'></td>";
                            html += '<td><a style="float:right;display:none" class="btn btn-sm btn-circle btn-success choose-party" data-role-type="' + roleType + '" href="#nowhere">添加</a></td>';
                            html += "</tr>";
                        }
                    }
                    $partyTable.append(html);
                }
            }
        })
    }

    function removeTicketParty(obj){
        var party = $(obj).closest(".ticket-party"), partyName = $(obj).data("partyName"), type = $(obj).data("roleType");
        party.remove();
        var chosenParties = caseParties[type];
        chosenParties.splice(chosenParties.indexOf(partyName), 1);
    }

    function choosePartyGroup(obj){
        var $tr = $(obj).closest("tr"), $ticketParties = $tr.find(".ticket-parties"), type = $(obj).data("roleType");
        displayInLayer2('选择参与方', {
            requestUrl: 'PartyGroupChooser?defaultCheckPartner=true&type=' + type, width: 700,
            end: function (data) {
                var chosenParties = caseParties[type], name = data.name;

                if(chosenParties.indexOf(name) == -1){
                    var tickeParty = '<div class="ticket-party">';
                    tickeParty += name;
                    tickeParty += '<i class="fa fa-remove" data-party-name="' + name + '" data-role-type="' + type + '"></i>';
                    tickeParty += "</div>";
                    caseParties[type].push(name);
                    $ticketParties.append(tickeParty);
                }
            }
        });
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 发布信息</span>
        </div>
    </div>
    <div class="portlet-body">
        <div>
            <div class="portlet-body form">
                <form id="form1" class="horizontal-form">
                    <div class="form-body" style="padding: 20px;">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label">标题<span class="required"> * </span></label>
                                    <input type="text" placeholder="标题" name="title" class="form-control validate[required]"/>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label">截止日期<span class="required"> * </span></label>
                                    <input type="text" class="form-control validate[required]" name="endTime" onclick="WdatePicker({dateFmt:'yyyy-MM-dd', minDate:'%y-%M-{%d+1}'})"/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label">类型</label>
                                    <#--<#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CASE_CATEGORY"), null, false)>-->
                                <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CASE_CATEGORY"), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
                                    <select class="form-control validate[required]" style="width: 100%" name="type" onchange="getTemplateName(this.value)">
                                        <option value="">-请选择-</option>
                                        <#list enumerations as category>
                                            <option value="${category.enumId}" <#if template?has_content && template.templateKey?has_content && template.templateKey == category.enumId>selected</#if>>${category.description}</option>
                                        </#list>
                                        <option value="other" <#if template?has_content && template.templateKey?has_content && template.templateKey == "other">>selected</#if>>自建模板</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label">模板名</label>
                                <select class="form-control validate[required]" style="width: 100%" id="TemplateId" name="TemplateId" onchange="getRoles(this.value)"></select>

                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group">
                                    <label class="control-label">参与方</label>
                                    <div style="margin-top: 5px" id="roleMember">
                                        <table id="partyTable" class="table table-hover table-striped table-bordered" style="margin:0">
                                            <thead>
                                                <tr>
                                                    <th style="width: 150px">角色</th>
                                                    <th style="width: 150px">是否参与</th>
                                                    <th>企业/机构</th>
                                                    <th style="width: 80px"></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            </tbody>
                                        </table>
                                        <input type="hidden" name="roles" value="CASE_ROLE_OWNER"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group">
                                    <label class="control-label">信息内容描述</label>
                                    <textarea name="ticketContent" id="ticketContent" class="form-control" rows="20"></textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-actions" style="padding: 20px;text-align: center">
                        <button type="button" class="btn blue" onclick="createTicket()">
                            发布<i class="fa"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>