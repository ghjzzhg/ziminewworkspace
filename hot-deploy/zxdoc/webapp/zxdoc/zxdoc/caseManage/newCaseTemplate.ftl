<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href="/images/lib/typeahead/typeahead.css" xmlns="http://www.w3.org/1999/html"
      xmlns="http://www.w3.org/1999/html"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2-bootstrap.min.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="/images/lib/typeahead/handlebars.min.js" type="text/javascript"></script>
<script src="/images/lib/typeahead/typeahead.bundle.min.js" type="text/javascript"></script>
<script src="/images/lib/select2/select2.js" type="text/javascript"></script>
<script src="/images/lib/select2/i18n/zh-CN.js" type="text/javascript"></script>
<script type="text/javascript">
    var formData;
    $(function(){
        $("#caseTemplateForm").validationEngine("attach", {promptPosition: "topLeft"});
        formData = $("#caseTemplateForm").serialize();
    })
</script>
<#if !template?has_content>
    <#assign template={}>
</#if>
<div class="portlet light ">
    <div class="portlet-body">
        <div style="width: 90%;margin:0 auto">
            <form id="caseTemplateForm">
                <input type="hidden" id="templateId" name="id" value="${template.id?default('')}">
                <input type="hidden" id="privateFlag" name="privateFlag" value="${privateFlag?default('')}">
            <table class="table table-hover table-striped table-bordered">
                <tbody>
                <tr>
                    <td style="width: 20%;"> <label class="control-label">模板名称<span class="required"> * </span></label></td>
                    <td>
                        <input type="text" class="form-control validate[required]" name="templateName" value="${template.templateName?default('')}"></input>
                    </td>
                </tr>
                <#if (privateFlag?has_content && privateFlag == "false") || template.templateKey?has_content>
                    <tr>
                        <td> <label class="control-label">模板类别</label></td>
                        <td>
                        <#assign categoryGroups = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CASE_CATEGORY_GROUP"), null, false)>
                            <select class="form-control select" name="templateGroup">
                                <option value="">-请选择-</option>
                            <#list categoryGroups as category>
                                <option value="${category.enumId}" <#if template.templateGroup?has_content && template.templateGroup == category.enumId>selected</#if>>${category.description}</option>
                            </#list>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td> <label class="control-label">CASE类型<span class="required"> * </span></label></td>
                        <td>
                        <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CASE_CATEGORY"), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
                            <select class="form-control select" name="templateKey">
                            <#list enumerations as category>
                                <option value="${category.enumId}" <#if template.templateKey?has_content && template.templateKey == category.enumId>selected</#if>>${category.description}</option>
                            </#list>
                            </select>
                        </td>
                    </tr>
                </#if>
                <tr>
                    <td> <label class="control-label">参与方</label></td>
                    <td>
                        <#assign loginUserGroupInfo = delegator.findByAnd("BasicGroupInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId), null, false)>
                        <#assign roleTypes = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), null, false)>
                        <#list roleTypes as roleType>
                            <label>
                                <input type="checkbox"  class="validate[required]" <#if roleType.roleTypeId == "CASE_ROLE_OWNER" || (loginUserGroupInfo?has_content && loginUserGroupInfo[0].roleTypeId == roleType.roleTypeId)> onclick="return false" </#if> name="roles"<#if (template.roles?has_content && template.roles?index_of(roleType.roleTypeId) != -1) || roleType.roleTypeId == "CASE_ROLE_OWNER" || (loginUserGroupInfo?has_content && loginUserGroupInfo[0].roleTypeId == roleType.roleTypeId)>checked="checked"</#if> value="${roleType.roleTypeId}"/>${roleType.description}
                            </label>
                        </#list>
                    </td>
                </tr>
                <tr>
                    <td> <label class="control-label">说明文件</label></td>
                    <td>
                    <#assign dataResource = delegator.findByAnd("DataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("dataResourceId", template.dataResourceId), null, false)>
                        <@fileinput name="uploadedFile" oldFileInputName="oldUploadedFile" multiple=false files=dataResource/>
                    </td>
                </tr>
                <tr>
                    <td> <label class="control-label">备注</label></td>
                    <td>
                        <textarea class="form-control" name="remark" rows="3">${template.remark?default('')}</textarea>
                    </td>
                </tr>
                </tbody>
            </table>
            </form>
            <div class="form-group" style="text-align: center">
                <div class="margiv-top-10">
                    <a href="javascript:$.caseManage.<#if template.id?has_content>update<#else>create</#if>CaseTemplate();" class="btn green"> 下一步 </a>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="note note-info">
            <pre>
                温馨提示
                <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
                1.参与方中企业是必须选择的，所以在页面中企业默认选中并且不能修改
                2.在机构为自己自己的模版，不是使用系统模版时。默认为立即生效，并且不能修改。
                3.在点击下一步时请确保<span style="color: red">文件的大小不超过${fileSize?default("50")}兆</span>
                4.<span style="color: red">文件名称不要超过50个字符。</span>
            </pre>
</div>