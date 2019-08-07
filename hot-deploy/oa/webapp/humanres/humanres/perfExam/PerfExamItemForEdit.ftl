<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if perfExamItem?has_content>
    <#assign itemId = perfExamItem.itemId?default('')>
    <#assign department = perfExamItem.department?default('')>
    <#assign score = perfExamItem.score?default('')>
    <#assign parentType = perfExamItem.parentType?default('')>
    <#assign type = perfExamItem.type?default('')>
    <#assign title = perfExamItem.title?default('')>
    <#assign sortOrder = perfExamItem.sortOrder?default('')>
    <#assign score1 = perfExamItem.score1?default('')>
    <#assign score2 = perfExamItem.score2?default('')>
    <#assign score3 = perfExamItem.score3?default('')>
    <#assign score4 = perfExamItem.score4?default('')>
    <#assign remark = perfExamItem.remark?default('')>
<#else>
    <#assign itemId = ''>
    <#assign department = ''>
    <#assign score = ''>
    <#assign parentType = ''>
    <#assign type = ''>
    <#assign title = ''>
    <#assign sortOrder = ''>
    <#assign score1 = ''>
    <#assign score2 = ''>
    <#assign score3 = ''>
    <#assign score4 = ''>
    <#assign remark = ''>
</#if>

<form method="post" action="/hr/control/GetExamItems" id="EditPerfExamItem" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="EditPerfExamItem">
    <input type="hidden" name="saveLink" id="EditPerfExamItem_saveLink" onclick="javascript:$.perfExam.savePerfExamItem('');">
    <input type="hidden" name="itemId" id="EditPerfExamItem_itemId" value="${itemId}">
    <div class="fieldgroup" id="_G560_">
        <div class="fieldgroup-title-bar">
        </div>
        <div id="_G560__body" class="fieldgroup-body">
            <table cellspacing="0" class="basic-table hover-bar">
                <tbody><tr>
                    <td class="label">
                        <label for="EditPerfExamItem_department" id="EditPerfExamItem_department_title">所属部门</label></td>
                    <td>
                    <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}" required=false/>
                    </td>
                    <td class="label">
                        <label for="EditPerfExamItem_score" id="EditPerfExamItem_score_title"><span class="requiredAsterisk">*</span>该项总分</label></td>
                    <td>
                        <input type="text" name="score" value="${score}" class="validate[required,custom[onlyNumberSp]]" size="25" id="EditPerfExamItem_score">
                    </td>
                </tr>
                <tr class="alternate-row">
                    <td class="label">
                        <label for="EditPerfExamItem_parentType" id="EditPerfExamItem_parentType_title">考评类别</label></td>
                    <td>
                        <select name="parentType" id="EditPerfExamItem_parentType">
                        <#list perfExamTypes as type >
                            <option value="${type.id}" <#if perfExamType == '${type.id}'>selected="selected"</#if>>${type.name}</option>
                        </#list>
                        </select>
                    </td>
                    <td class="label">
                        <label for="EditPerfExamItem_type" id="EditPerfExamItem_type_title">子类别</label></td>
                    <td>
                        <select name="type" id="EditPerfExamItem_type">
                        <#list perfExamSubTypes as type >
                            <option value="${type.id}" <#if perfExamSubType == '${type.id}'>selected="selected"</#if>>${type.name}</option>
                        </#list>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="label">
                        <label for="EditPerfExamItem_title" id="EditPerfExamItem_title_title"><span class="requiredAsterisk">*</span>标题</label></td>
                    <td>
                        <input type="text" name="title" value="${title}" class="validate[required]" size="25" id="EditPerfExamItem_title">
                    </td>
                    <td class="label">
                        <label for="EditPerfExamItem_sortOrder" id="EditPerfExamItem_sortOrder_title"><span class="requiredAsterisk">*</span>顺序</label></td>
                    <td>
                        <input type="text" name="sortOrder" value="${sortOrder}" class="validate[required,custom[onlyNumberSp]]" size="5" id="EditPerfExamItem_sortOrder">
                        <span class="tooltip">相同类别显示顺序，数字越小越靠前</span>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="fieldgroup" id="_G561_">
    <div class="fieldgroup-title-bar">
        <ul>
            <li class="ms-hover expanded">
                <a onclick="javascript:toggleCollapsiblePanel(this, '_G561__body', '展开', '收起');" title="收起">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;结果说明</a>
            </li>
        </ul>
    </div>
    <div id="_G561__body" class="fieldgroup-body" style="display: block;">
        <table cellspacing="0" class="basic-table hover-bar">
            <tbody><tr class="alternate-row">
                <td class="label">
                    <label for="EditPerfExamItem_score1" id="EditPerfExamItem_score1_title"><span class="requiredAsterisk">*</span>优</label></td>
                <td colspan="4">
                    <input type="text" name="score1" value="${score1}" class="validate[required]" size="80" id="EditPerfExamItem_score1">
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="EditPerfExamItem_score2" id="EditPerfExamItem_score2_title"><span class="requiredAsterisk">*</span>良</label></td>
                <td colspan="4">
                    <input type="text" name="score2" value="${score2}" class="validate[required]" size="80" id="EditPerfExamItem_score2">
                </td>
            </tr>
            <tr class="alternate-row">
                <td class="label">
                    <label for="EditPerfExamItem_score3" id="EditPerfExamItem_score3_title"><span class="requiredAsterisk">*</span>中</label></td>
                <td colspan="4">
                    <input type="text" name="score3" value="${score3}" class="validate[required]" size="80" id="EditPerfExamItem_score3">
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="EditPerfExamItem_score4" id="EditPerfExamItem_score4_title"><span class="requiredAsterisk">*</span>差</label></td>
                <td colspan="4">
                    <input type="text" name="score4" value="${score4}" class="validate[required]" size="80" id="EditPerfExamItem_score4">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
    <div class="fieldgroup" id="_G562_">
    <div class="fieldgroup-title-bar">
    </div>
    <div id="_G562__body" class="fieldgroup-body">
        <table cellspacing="0" class="basic-table hover-bar">
            <tbody>
            <tr class="alternate-row">
                <td class="label">
                    <label for="EditPerfExamItem_remark" id="EditPerfExamItem_remark_title">备注</label></td>
                <td colspan="4">
                    <textarea name="remark" cols="50" rows="3" id="EditPerfExamItem_remark">${remark}</textarea>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="EditPerfExamItem_saveLink" class="hide" id="EditPerfExamItem_saveLink_title">操作</label></td>
                <td colspan="4">
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.perfExam.savePerfExamItem('${itemId}');" title="操作">保存</a>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    </div>
    <table cellspacing="0" class="basic-table hover-bar">
</table>
</form>