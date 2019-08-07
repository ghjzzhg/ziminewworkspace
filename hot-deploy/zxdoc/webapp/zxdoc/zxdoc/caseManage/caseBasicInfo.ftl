<link href="/images/lib/select2/select2.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2-bootstrap.min.css" rel="stylesheet" type="text/css"/>
<script src="/images/lib/select2/select2.js" type="text/javascript"></script>
<script src="/images/lib/select2/select2_extended_ajax_adapter.js" type="text/javascript"></script>
<script src="/images/lib/select2/i18n/zh-CN.js" type="text/javascript"></script>
<#include "component://common/webcommon/includes/uiWidgets/kindeditor.ftl"/>
<script>
    var textareaHtml;
    $(function(){
        $("#caseTemplateForm").validationEngine("attach", {promptPosition: "bottomLeft",scroll:false});
        textareaHtml = KindEditor.create('textarea[name="caseRemark"]');
        /*initGroupSelect();
        initPersonSelect();
        initselect();*/

        $("#partyTable").on("click", ".choose-party", function(){
            choosePartyPerson(this);
        })
        $("#partyTable").on("click", ".clear-party", function(){
            clearPartyPerson(this);
        })

        $(".match-person").select2();
    })

    function initselect(){
        $("span[id^='select2-CASE_ROLE_']").each(function () {
            var id = $(this).attr("id");
            if(id.indexOf("-preson-select") >= 0 && id.indexOf("select2-") >= 0 && id.indexOf("-container") >= 0 && id.indexOf("-s2id_") < 0){
                var id1 = id.replace(new RegExp("select2-"),'');
                var id2 = id1.replace(new RegExp("-container"),'');
                $(this).attr("id","s2id_" + id2);
            }
        })
    }

    function initGroupSelect() {
        $(".provider-select").select2({
            allowClear: true,
            placeholder: "-- 请选择 --",
            dataAdapter: $.fn.select2.amd.require('select2/data/extended-ajax'),
        <#if defaultProviders?has_content>
            defaultResults: [
                <#list defaultProviders as provider>
                    <#if provider?index != 0>
                        ,
                    </#if>
                    {id: "${provider.partyId}", text: "${provider.groupName}"}
                </#list>
            ],
        </#if>
            ajax: {
                url: "ProviderSelectJson",
                dataType: 'json',
                delay: 500,
                data: function (params) {
                    return {
                        q: params.term, // search term
                        type: $(this.context).attr("roleTypeId"),
                        page: params.page
                    };
                },
                processResults: function (data, params) {
                    params.page = params.page || 1;
                    return {
                        results: data.data?data.data : data,
                        pagination: {
                            more: (params.page * 10) < data.totalCount
                        }
                    };
                },
                cache: true
            },
            escapeMarkup: function (markup) {
                return markup;
            }, // let our custom formatter work
            minimumInputLength: 2
        });
    }

    function initPersonSelect(list){
        $(".provider-select-person").select2({
            allowClear: true,
            placeholder: "-- 请选择 --",
            dataAdapter: $.fn.select2.amd.require('select2/data/extended-ajax'),
            defaultResults: list,
            ajax: {
                url: "ProviderPersonSelectJson",
                dataType: 'json',
                delay: 500,
                data: function (params) {
                    var roleTypeId = $(this.context).attr("roleTypeId");
                    var groupIdOrName = $("#" + roleTypeId + "-select").val();
                    return {
                        q: params.term, // search term
                        type: roleTypeId,
                        groupIdOrName: groupIdOrName,
                        page: params.page
                    };
                },
                processResults: function (data, params) {
                    params.page = params.page || 1;
                    return {
                        results: data.data?data.data : data,
                        pagination: {
                            more: (params.page * 10) < data.totalCount
                        }
                    };
                },
                cache: true
            },
            escapeMarkup: function (markup) {
                return markup;
            }, // let our custom formatter work
            minimumInputLength: 2
        });
    }

    function saveData() {
        var htmlValue = textareaHtml.html();
        $('textarea[name="caseRemark"]').val(htmlValue)
        var options = {
            beforeSubmit: function () {
                return $("#caseTemplateForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                //直接按模板设置的步骤来，不允许创建的时候修改
                <#--location.href="EditCaseTemplateNodeGroups?iframe=true&isFromCase=true&templateId=${templateId}&caseId=" + data.caseId;                -->
<#if caseData?has_content>
                showInfo("CASE更新成功");
<#else>
    showInfo("CASE创建成功");
</#if>
                closeCurrentTab();
            },
            url: "<#if parameters.caseId?has_content>updateCaseBasicInfo<#else>SaveCase</#if>",
            type: "post"
        };
        $("#caseTemplateForm").ajaxSubmit(options);
    }
    function showParty(obj, type) {
        var checker = $(obj), select = checker.parent().next();
        if(checker.is(":checked")){
            select.show();
        }else {
            select.hide();
        }
        $("#" + type + "-select").val("").trigger("change");
        $("#" + type + "-preson-select").val("").trigger("change");
    }

    function nextSaveData() {
        var htmlValue = textareaHtml.html();
        $('textarea[name="caseRemark"]').val(htmlValue)
        var options = {
            beforeSubmit: function () {
                return $("#caseTemplateForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                location.href = "EditCaseTemplateNodeGroups?iframe=true&isFromCase=false&blankCaseSessionKey=" + data.data.blankCaseSessionKey;
                <#--location.href = "FixedCaseTemplate?iframe=true&isFromCase=true&templateId=${templateId}&caseId=" + data.caseId;-->
                //saveCaseProgress(data);
            },
            url: "SaveBlankCase",
            type: "post"
        };
        $("#caseTemplateForm").ajaxSubmit(options);
    }

    //该方法用于：当公司改变时，清空人员，并且重新加载值
    function emptyAndLoadPerson(value,type) {
        /*$("#" + type + "-preson-select").select2("destroy").empty();*/
        showTop10(value, type);
    }

    function showTop10(value,type) {
        $.ajax({
            type:"post",
            url:"ProviderPersonSelectJson",
            async:true,
            data:{groupIdOrName:value,type:type},
            success:function(data)
            {
                var list = data.data;
                initPersonSelect(list);
                initselect();
            }
        });
    }

    function initPersonChooser(target, groupName) {
        $.ajax({
            type:"post",
            url:"ProviderPersonSelectJson",
            async:true,
            data:{groupIdOrName:groupName},
            success:function(data)
            {
                var list = data.data;
                target.find("option").remove();
                target.select2({data: list});
            }
        });
    }

    function choosePartyPerson(obj){
        var $td = $(obj).closest("td"), type = $(obj).data("roleType");
        displayInLayer2('选择项目经理', {
            requestUrl: 'PartyPersonChooser?type=' + type, width: 500, height: 600,
                end: function (data) {
                    $td.find("input[name^=personName_]").val(data.id);
                    $td.find(".party-person-name").html(data.person);
                    var group = $td.parent().find(".group-name");
                    group.html(data.name);
                }
        });
    }

    function clearPartyPerson(obj){
        var $td = $(obj).closest("td");
        $td.find("input[name^=personName_]").val("");
        $td.find(".party-person-name").html("");
        var group = $td.parent().find(".group-name");
        group.html("");
    }
</script>
<div class="portlet light" align="center">
    <div class="portlet-body">
        <form id="caseTemplateForm">
            <input type="hidden" name="blankCaseSessionKey" value="${blankCaseSessionKey!}">
            <input type="hidden" name="templateId" value="${templateId!}">
            <input type="hidden" name="caseId" value="${parameters.caseId!}">
            <table class="table table-hover table-striped table-bordered" style="min-width: 900px">
                <tbody>
                <tr>
                    <td style="min-width: 120px"><label class="control-label">标题<span class="required"> * </span></label></td>
                    <td>
                        <input type="text" class="form-control validate[required,custom[noSpecial]]" maxlength="50" name="caseName" <#if caseData?has_content> value="${caseData.title}" </#if>/>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align: middle"><label class="control-label">参与方<span class="required"> * </span></label></td>
                    <td style="padding: 0">
                    <table id="partyTable" class="table table-hover table-striped table-bordered" style="margin:0">
                        <tbody>
                        <tr>
                            <th style="width: 10%">角色</th>
                            <th>企业/机构</th>
                            <th style="width: 30%">项目经理</th>
                        </tr>

                    <#assign roleTypes = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), null, false)>
                    <#list roleTypes as roleType>
                        <#if !templateId?has_content || (templateId?has_content && (roles?index_of(roleType.roleTypeId) > -1))>
                            <#assign requiredRole = false>
                            <#assign matchRole = false>
                            <#if userRoleType = roleType.roleTypeId>
                                <#assign matchRole = true>
                            </#if>
                            <#if roleType.roleTypeId == 'CASE_ROLE_OWNER' || matchRole>
                                <#assign requiredRole = true>
                            </#if>
                            <#if caseManagers?keys?seq_contains(roleType.roleTypeId)>
                                <#assign caseManager = caseManagers[roleType.roleTypeId]>
                            <#else>
                                <#assign caseManager = []>
                            </#if>
                            <tr>
                            <td >
                                    <label class="control-label" style="margin-bottom: 10px;float:left;position:relative">
                                        <input class="hide" type="checkbox" name="roles" value="${roleType.roleTypeId}" <#if requiredRole>checked="checked"</#if>/>${roleType.description}
                                        <#if requiredRole>
                                            <span class="required"> * </span>
                                        </#if>
                                    </label>
                                </td>
                                <td>
                                    <span class="group-name"><#if caseManager?has_content>${caseManager[2]}</#if></span>
                                </td>
                                <td style="text-align: center">
                                <#--具体人员-->
                                    <#if matchRole>
                                    <select id="${roleType.roleTypeId}-preson-select" roleTypeId="${roleType.roleTypeId}" class="provider-select-person validate[required] match-person"
                                    <#--onchange="emptyAndLoadPerson(this.value,'${roleType.roleTypeId}')"-->
                                            name="personName_${roleType.roleTypeId}" style="width:100%">
                                        <#if persons?has_content>
                                            <#list persons as person>
                                                <option value="${person.partyId}" <#if (userPersonId?has_content && userPersonId == person.partyId) || (caseManager?has_content && caseManager[0] == person.partyId)>selected</#if>>${person.fullName!}<#if !userPartner && person.partnerType?has_content> [合伙人]</#if></option>
                                            </#list>
                                        </#if>
                                    </select>
                                    <#else>
                                        <span id="${roleType.roleTypeId}-preson" class="party-person-name"><#if caseManager?has_content>${caseManager[1]}</#if></span>
                                        <input type="hidden" name="personName_${roleType.roleTypeId}" <#if caseManager?has_content>value="${caseManager[0]}"</#if> <#if requiredRole>data-jqv-prompt-at="#${roleType.roleTypeId}-preson" class="validate[required]"</#if>>
                                        <a style="float:right" class="btn btn-sm btn-circle btn-danger clear-party" href="#nowhere">删除</a>
                                        <a style="float:right" class="btn btn-sm btn-circle btn-success choose-party" data-role-type="${roleType.roleTypeId}" href="#nowhere">选择</a>
                                    </#if>
                                </td>
                            </tr>
                        </#if>
                    </#list>
                        </tbody>
                    </table>
                    </td>
                </tr>
                <#if baseTimes?has_content>
                <#list baseTimes as baseTime>
                    <tr>
                        <td><label class="control-label">${baseTime.name}</label></td>
                        <td>
                            <#if oldTimes?has_content>
                            <#assign oldTime = oldTimes[baseTime.id]!>
                            <input type="text" class="form-control" name="baseTime_${baseTime.id}" value="${oldTime!}"
                                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                            <#else>
                                <input type="text" class="form-control" name="baseTime_${baseTime.id}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </#if>
                <tr>
                    <td><label class="control-label">预计结束时间</label></td>
                    <td>
                        <input type="text" name="dueDate"  class="form-control"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" <#if caseData?has_content && caseData.dueDate?has_content> value="${caseData.dueDate?string("yyyy-MM-dd")}" </#if>/>
                    </td>
                </tr>
                <tr>
                    <td><label class="control-label">概述</label></td>
                    <td>
                        <textarea class="form-control" name="caseRemark"  rows="3"><#if caseData?has_content>${caseData.summary!}</#if></textarea>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
        <div class="form-group">
            <div class="margiv-top-10">
            <#if templateId?has_content || parameters.caseId?has_content>
                <a href="javascript:void(0);" class="btn green" onclick="saveData()"> <#if caseData?has_content>保存<#else>创建</#if> </a>
            <#else>
                <a href="javascript:nextSaveData();" class="btn green"> 下一步 </a>
            </#if>
            </div>
        </div>

    </div>
</div>
<div class="note note-info">
            <pre>
                温馨提示

                1.在创建CASE时，企业参与方项目经理必须选定。
                2.其他参与方如不需参与，请将其项目经理留空。
                3.CASE仅创建者及企业参与方主账号具有修改权限，其他人员无权修改。
                4.此处参与方项目经理变更后，原项目经理将不再参与本CASE，如有必要请至CASE详情>>成员中添加其为新成员。
            </pre>
</div>
