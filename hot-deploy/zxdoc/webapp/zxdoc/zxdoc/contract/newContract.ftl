<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap/js/bootstrap.js"></script>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2-bootstrap.min.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="/images/lib/select2/select2.js" type="text/javascript"></script>
<script src="/images/lib/select2/i18n/zh-CN.js" type="text/javascript"></script>
<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>
<script type="text/javascript">
    var personSelect;
    $(function () {

        $("#fileFieldName").attachment({
            showLink: true,
            wrapperClass: 'btn btn-success'
        });
        $("#newContract").validationEngine("attach", {promptPosition: "topLeft"});
        $(".choose-party").on("click", choosePartyGroup);
        addSelectPerson($("#targetGroup").val(), '${data.secondPersonId?default('')}');
    })
    //    function addContract() {
    //        var data = {
    //            contractId: $("#contractId").val(),
    //            firstPartyName: $("#contractA").val(),
    //            secondPartyName: $("#contractB").val(),
    //            startDate: $("#startDate").val(),
    //            dateClose: $("#endDate").val(),
    //            relateCase: $("#case").val(),
    //            isCharge: $("#charge").prop("checked") ? "yes" : "no",
    //        }
    //        $.ajax({
    //            type: 'post',
    //            dataType: 'json',
    //            url: 'addContract',
    //            data: data,
    //            success: function (content) {
    //                showInfo(content.msg);
    //                displayInside('ListContracts')
    //            }
    //        })
    //    }


    function addContract() {
        var fileFlag = false;
        $("input[name^='fileFieldName']").each(function () {
            var fileName = $(this).val();
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                fileFlag = true;
                return;
            }
        })
        if(fileFlag){
            showError("文件名称长度请小于50个字符");
            return;
        }
        var options = {
            beforeSubmit: function () {
                return $("#newContract").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                var msg = $(data).find("#msg").html();
                if (msg == "添加成功") {
                    showInfo(msg);
                    displayInside('ListContracts')
                } else if (msg == "修改成功") {
                    showInfo(msg);
                    displayInside('ListContracts')
                }
                else {
                    showError(msg);
                }
            },
            url: "addContract",
            type: 'post'
        };
        $("#newContract").ajaxSubmit(options);
    }

    function choosePartyGroup(){
        displayInLayer2('选择乙方', {
            requestUrl: 'PartyGroupChooser', width: 700,
            end: function (data) {
                $("#targetGroupName").html(data.name);
                $("#secondPartyName").val(data.name);
                $("#targetGroup").val(data.id);
                addSelectPerson(data.id);
            }
        });
    }

    function addSelectPerson(groupId, selectPersonId) {
        var secondPerson = $("#secondPersonName");
        if(secondPerson.hasClass("select2-hidden-accessible")){
            secondPerson.select2("destroy");
        }
        secondPerson.find("option").remove();
        if(groupId){
            $.ajax({
                url: "searchPerson",
                type: "POST",
                dataType: 'json',
                data:{groupIdOrName: groupId},
                success: function(data){
                    secondPerson.select2({data: data.data});
                    if(selectPersonId){
                        secondPerson.val(selectPersonId).trigger("change");
                    }
                }
            });
        }else{
            secondPerson.select2();
        }
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
        <#if data?has_content>
            <span class="caption-subject bold uppercase"> 合同编辑</span>
        <#else>
            <span class="caption-subject bold uppercase"> 新合同</span>
        </#if>
        </div>
    </div>
    <div class="portlet-body">
        <div class="col-xs-2 col-md-2"></div>
        <div class="col-xs-8 col-md-8">
            <form id="newContract">
            <#if contract?has_content><input type="hidden" name="contractId" id="contractId"
                                         value="${contract.contractId?default('')}"/></#if>
                <table class="table table-hover table-striped table-bordered">
                    <tbody>
                    <tr>
                        <td>
                            <label class="control-label">合同名称<span class="required"> * </span></label>
                        </td>
                        <td>
                            <input type="text" class="form-control validate[required,custom[noSpecial]]"
                                   name="contractName"
                                   <#if contract?has_content>value="${contract.contractName?default("")}"</#if>/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">甲方<span class="required"> * </span></label>
                        </td>
                        <td>
                        <#if data?has_content>${data.firstPartyName?default('')}</#if>
                            <input type="hidden" id="firstPartyId" name="firstPartyName" value="<#if data?has_content>${data.firstPartyName?default('')}</#if>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">乙方<span class="required"> * </span></label>
                        </td>
                        <td>
                            <span id="targetGroupName"><#if data?has_content>${data.secondPartyName?default('')}</#if></span>
                            <input id="secondPartyName" name="secondPartyName" type="hidden" <#if data?has_content>value="${data.secondPartyName?default('')}"</#if>>
                            <input id="targetGroup" type="hidden" name="secondPartyId" value="<#if data?has_content>${data.secondPartyId?default('')}</#if>" data-jqv-prompt-at="#targetGroupName" class="validate[required]">
                            <div style="display: inline-block;float: right">
                                <a style="float:right" class="btn btn-sm btn-circle btn-success choose-party" href="#nowhere">选择</a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">乙方联系人<span class="required"> * </span></label>
                        </td>
                        <td>
                            <select style="width: 100%;" name="secondPersonName" id="secondPersonName" class="validate[required]">
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">开始日期<span class="required"> * </span></label>
                        </td>
                        <td>
                        <input type="text" class="form-control validate[required]" id="startDate" name="startDate"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd', maxDate:'#F{$dp.$D(\'dateClose\')}'})"
                               <#if contract?has_content>value="${contract.startDate?default("")}"</#if>"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">结束日期<span class="required"> * </span></label>
                        </td>
                        <td>
                            <input type="text" class="form-control validate[required]" id="dateClose" name="dateClose"
                                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd', minDate:'#F{$dp.$D(\'startDate\')}'})"
                                   <#if contract?has_content>value="${contract.dateClose?default("")}"</#if>/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">关联CASE</label>
                        </td>
                        <td>
                        <#--新合同-->
                        <#if contract?has_content>
                            <#if relatedContractList?length gt 0>
                                <#list relatedContractList as item>
                                    <label class="col-xs-6">
                                        <input type="checkbox" name="relatedCase"
                                               value="${item.caseId}" checked/>${item.title}
                                    </label>
                                </#list>
                            </#if>
                        </#if>
                        <#if relatedCaseList?length gt 0>
                            <#list relatedCaseList as relatedCase>
                                <label class="col-xs-6">
                                    <input type="checkbox" name="relatedCase"
                                           value="${relatedCase.caseId}"/>${relatedCase.title}
                                </label>
                            </#list>
                        </#if>
                        <#--<#else>
                            <#if relatedCaseList?length gt 0>
                                <#list relatedCaseList as relatedCase>
                                    <label class="col-xs-6">
                                        <span>${relatedCase.title}</span>
                                    </label>
                                </#list>
                            </#if>
                        </#if>-->

                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="charge" class="control-label">收费</label>
                        </td>
                        <td>
                        <#--${data.charge}-->
                        <#if contract?has_content><#--判断是新建还是修改-->
                            <#if contract.isCharge?default('') == "Y">
                                <input name="isCharge" type="checkbox" checked="checked"/>
                            <#else> <input name="isCharge" type="checkbox"/>
                            </#if>

                        <#else><input name="isCharge" type="checkbox"/>
                        </#if>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">合同附件</label>
                        </td>
                        <td id="fileinput-td">
                        <#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
                        <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
                        <#assign fileSize = fileSize?number * 1048576>
                            <input type="text" name="fileFieldName" id="fileFieldName" value=${dataResourceId?default('')} data-btn-style="float:right;" data-btn-style="float:right" data-size-limit="${fileSize}" style="width:1px;height:1px;">
                    </tr>
                    </tbody>
                </table>
            </form>
            <div class="form-group" align="center">
                <div class="margiv-top-10">
                    <a href="javascript:;" class="btn green" onclick="addContract()"> 提交 </a>
                </div>
            </div>
            <div class="note note-info">
<pre style="font-family: 微软雅黑!important;">
    温馨提示
<#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
    1.<span style="color: red">上传文件的总大小请不要超过${fileSize?default("50")}兆</span>
    2.<span style="color: red">文件名称不要超过50个字符。</span>
    3.甲方为您的所属的机构，乙方可为机构也可以是合伙人,联系人为对应的子账号
    4.如果选择了关联case，在case详情的合同页面可查看合同信息
    5.提交合同后，会在客户管理中增加甲乙双方的客户关系。
</pre>
            </div>
        </div>
    </div>
</div>
