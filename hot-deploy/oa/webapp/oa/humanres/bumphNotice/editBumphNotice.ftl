<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="content"]', {
            allowFileManager: true
        });

        $("textarea[name='checkPerson']").click(function () {
            displayInTab3("AddTransactionTab", "需签收确认人员", {
                requestUrl: "checkPerson",
                data: {param: "person"},
                width: "600px",
                position: "center"
            });
        });
        $("textarea[name='range']").click(function () {
            displayInTab3("AddTransactionTab", "设置浏览权限", {
                requestUrl: "checkPerson",
                data: {param: "range"},
                width: "600px",
                position: "center"
            });
        });
    });
    
    jQuery(document).ready(function(){
        jQuery('#noticeForm').validationEngine('attach', {
            scroll:true
        });
    });
    function changeNoticeType(id,name){
        $("#BumphNoticeForSearch_noticeType").append('<option value="'+id+'">'+name+'</option>');
    }
</script>
<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#assign noticeId = parameters.noticeId>
<#if noticeId?has_content>
    <#assign notice = delegator.findOne("NoticeInfo",{"noticeId":noticeId},false)>
    <#assign signInPersonList = delegator.findByAnd("TblSignInPerson",{"noticeId":noticeId,"signInPersonType":"TblNotice"})>
</#if>

<#if notice?has_content>
    <#assign noticeId = notice.noticeId?default('')>
    <#assign department = notice.department?default('')>
    <#assign noticeHead = notice.noticeHead?default('')>
    <#assign title = notice.title?default('')>
    <#assign hasFeedback = notice.hasFeedback?default('')>
    <#assign noticeYear = notice.noticeYear?default('')>
    <#assign noticeNumber = notice.noticeNumber?default('')>
    <#assign useTemplate = notice.useTemplate?default('')>
    <#assign content = notice.content?default('')>
    <#assign releasePerson = notice.releasePerson?default('')>
    <#assign releaseTime = notice.releaseTime?default('')>
    <#assign lastEditTime = notice.lastEditTime?default('')>
    <#assign lastEditPerson = notice.lastEditPerson?default('')>
    <#assign noticeType = notice.noticeType?default('')>
</#if>

<#if signInPersonList?has_content>
    <#assign staffIds = "">
    <#list signInPersonList as sipList>
        <#assign staffIds = staffIds + sipList.staffId + ",">
    </#list>
    <#if staffIds?has_content&&staffIds!=" ">
        <#assign staffIds = staffIds?trim>
    </#if>
</#if>
<#--<@chooser chooserValue="10163"/>-->
<div class="yui3-skin-sam">
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top"><img
                        src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="noticeForm" id="noticeForm" class="basic-form" action="#">
        <input type="hidden" value="${noticeId?default('')}" name="noticeId">
        <div class="yui3-skin-audio-light">
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                发布新公文/通知
            </div>
            <div>
                <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                    <tbody>
                    <tr>
                        <td colspan="6">
                            <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                   style="border-collapse: collapse">
                                <tbody>
                                <tr>
                                    <td class="label">
                                        <label for="BumphNotice_department" id="BumphNotice_department_title"><b class="requiredAsterisk">*</b>发布部门：</label>
                                    </td>
                                    <td colspan="2" class="jqv">
                                        <#if noticeId?has_content>
                                            <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}" required=true/>
                                            <#--<@htmlTemplate.lookupField className="validate[required]" value="${department?default('')}" formName="noticeForm" name="department" id="BumphNotice_department" fieldFormName="LookupDepartment" position="center"/>-->
                                        <#else>
                                            <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${departmentId?default('')}" required=true/>
                                            <#--<@htmlTemplate.lookupField value="${departmentId?default('')}" formName="noticeForm" name="department" id="BumphNotice_department" fieldFormName="LookupDepartment" position="center"  className="validate[required]"/>-->
                                        </#if>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_noticeHead" id="BumphNotice_noticeHead_title"><b class="requiredAsterisk">*</b>文档抬头单位：</label>
                                    </td>
                                    <td colspan="2">
                                    <@dropDownList dropDownValueList=noticeHeadList enumTypeId="NOTICE_HEAD" inputSize="15" name="noticeHead"  selectedValue="${noticeHead?default('')}"/>
                                        <#--<select id="BumphNotice_noticeHead" name="noticeHead" class="validate[required]">-->
                                        <#--<#if noticeHeadList?has_content>-->
                                            <#--<#list noticeHeadList as head>-->
                                            <#--<#if noticeHead?has_content&&noticeHead==head.enumId>-->
                                                <#--<option value="${head.enumId}" selected>${head.description}</option>-->
                                            <#--<#else >-->
                                                <#--<option value="${head.enumId}">${head.description}</option>-->
                                            <#--</#if>-->
                                            <#--</#list>-->
                                        <#--</#if>-->
                                        <#--</select>-->
                                        <#--<a class="smallSubmit" href="#" onclick="showNoticeHeadManagement()" title="增加文档抬头单位">增加文档抬头单位</a>-->
                                    </td>

                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_signInPerson" id="BumphNotice_signInPerson_title"><b class="requiredAsterisk">*</b>需签收确认人员：</label>
                                    </td>
                                    <td class="jqv">
                                    <@selectStaff id="BumphNotice_signInPerson" name="signInPerson" value="${staffIds?default('')}" multiple=true required=true/>

                                    </td>
                                    <td><input type="checkbox" value="">
                                        <span class="tooltip">发送手机短信</span></td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_noticeDataScope" id="BumphNotice_noticeDataScope_title"><b class="requiredAsterisk">*</b>文档的发放范围：</label>
                                    </td>
                                    <td class="jqv">
                                    <@dataScope id="BumphNotice_noticeDataScope" name="noticeDataScope" dataId="${noticeId?default('')}" entityName="TblNotice" required=true/>
                                    </td>

                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_title" id="BumphNotice_title_title"><b class="requiredAsterisk">*</b>文档标题：</label>
                                    </td>
                                    <td>
                                        <input type="text" id="BumphNotice_title" name="title" value="${title?default('')}" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20">
                                    </td>
                                    <td>
                                        本文档
                                        <select name="hasFeedback">
                                            <#if hasFeedbackList?has_content>
                                                <#list hasFeedbackList as has>
                                                    <#if hasFeedback?has_content&&hasFeedback==has.enumCode>
                                                        <option value="${has.enumCode}" selected>${has.description}</option>
                                                    <#else >
                                                        <option value="${has.enumCode}">${has.description}</option>
                                                    </#if>
                                                </#list>
                                            </#if>

                                        </select>
                                        反馈
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_noticeType" id="BumphNotice_noticeType_title"><b class="requiredAsterisk">*</b>文档类型：</label>
                                    </td>
                                    <td colspan="2" class="jqv">
                                    <@dropDownList dropDownValueList=noticeTypeList enumTypeId="NOTICE_TYPE" name="noticeType" inputSize="12" selectedValue="${noticeType?default('')}" callback="changeNoticeType"/>

                                        <#--<select id="BumphNotice_noticeNumber" name="noticeType" class="validate[required]">-->
                                            <#--<#if noticeTypeList?has_content>-->
                                                <#--<#list noticeTypeList as type>-->
                                                    <#--<#if noticeType?has_content&&noticeType==type.enumId>-->
                                                        <#--<option value="${type.enumId}" selected>${type.description}</option>-->
                                                    <#--<#else >-->
                                                        <#--<option value="${type.enumId}">${type.description}</option>-->
                                                    <#--</#if>-->
                                                <#--</#list>-->
                                            <#--</#if>-->
                                        <#--</select>-->
                                        <#--<a class="smallSubmit" href="#" onclick="noticeTypeManagement()" title="增加文档类型">增加文档类型</a>-->
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_noticeNumber" id="BumphNotice_noticeNumber_title"><b class="requiredAsterisk">*</b>文档编号：</label>
                                    </td>
                                    <td colspan="2" class="jqv">
                                        <span class="tooltip">RECTEC</span>
                                        <select id="BumphNotice_noticeYear" name="noticeYear">
                                            <#list noticeYearList as year>
                                                <#if noticeYear?has_content&&noticeYear==year.noticeYear>
                                                    <option value="${year.noticeYear}" selected>${year.noticeYear}</option>
                                                <#else >
                                                    <option value="${year.noticeYear}">${year.noticeYear}</option>
                                                </#if>
                                            </#list>
                                        </select>
                                        <span class="tooltip">CWB</span>
                                        <span class="tooltip">第</span>
                                            <#if noticeId?has_content>
                                                <label>${noticeNumber?default('')}</label>
                                                <input type="hidden" id="BumphNotice_noticeNumber" name="noticeNumber" value="${noticeNumber?default('')}">
                                            <#else>
                                                <label>${number?default('')}</label>
                                                <input type="hidden" id="BumphNotice_noticeNumber" name="noticeNumber" value="${number?default('')}">
                                            </#if>
                                        <span class="tooltip">号</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="BumphNotice_useTemplate" id="BumphNotice_useTemplate_title">使用模板：</label>
                                    </td>
                                    <td colspan="2">
                                        <select id="BumphNotice_useTemplate" name="useTemplate"
                                                onchange="$.bumphNotice.changeNoticeTemplate(this.value, '${noticeId?default('')}')">
                                            <option value="">--请选择--</option>
                                            <#if noticeId?has_content>
                                                <#list templateListNew as list>
                                                    <#if useTemplate?has_content && useTemplate == list.noticeTemplateId>
                                                        <option value="${list.noticeTemplateId}" selected >${list.noticeTemplateName}</option>
                                                    <#else>
                                                        <option value="${list.noticeTemplateId}">${list.noticeTemplateName}</option>
                                                    </#if>
                                                </#list>
                                            <#else>
                                                <#if templateNameListAdd?has_content>
                                                    <#list templateNameListAdd as templateList>
                                                        <option value="${templateList.noticeTemplateId}">${templateList.noticeTemplateName}</option>
                                                    </#list>
                                                </#if>
                                            </#if>
                                        </select>
                                        <a class="smallSubmit" href="#" onclick="useTemplateManagement(null,'${noticeId?default('')}')" title="增加模板">增加模板</a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label" >　
                                        <label for="BumphNotice_content" id="BumphNotice_content_title">
                                            <b class="requiredAsterisk">*</b>文档内容：</label>
                                    </td>
                                    <td colspan="2"></td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="jqv">
                                        <textarea name="content" class="validate[required]">${content?default('')}</textarea>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                    <tr align="center">
                        <td>
                            <a href="#" class="smallSubmit" onclick="$.bumphNotice.saveNotice(template)">发布公文</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </form>
</div>