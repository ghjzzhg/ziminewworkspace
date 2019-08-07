<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="BumphNoticeSignInSearchForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="BumphNoticeSignInSearchForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_title" id="BumphNoticeSignInForSearch_title_title">文档标题</label>
            </td>
            <td>
                <input type="text" name="title" size="25" id="BumphNoticeSignInForSearch_title">
            </td>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_department" id="BumphNoticeSignInForSearch_department_title">发布部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_noticeType" id="BumphNoticeSignInForSearch_noticeType_title">文档类型</label>
            </td>
            <td>
                <select name="noticeType" id="BumphNoticeSignInForSearch_noticeType">
                    <option value="">--请选择--</option>
                <#list noticeTypeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_releaseTimeStart" id="BumphNoticeSignInForSearch_releaseTimeStart_title">发布时间</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="releaseTimeStart" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${releaseTimeStart?default('')}" size="25" maxlength="30" id="BumphNoticeSignInForSearch_releaseTimeStart"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'BumphNoticeSignInForSearch_releaseTimeEnd\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_releaseTimeEnd" id="BumphNoticeSignInForSearch_releaseTimeEnd_title">至</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="releaseTimeEnd" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${releaseTimeEnd?default('')}" size="25" maxlength="30" id="BumphNoticeSignInForSearch_releaseTimeEnd"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'BumphNoticeSignInForSearch_releaseTimeStart\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_hasSignIn" id="BumphNoticeSignInForSearch_hasSignIn_title">签收状态</label>
            </td>
            <td>
                <select name="hasSignIn" id="BumphNoticeSignInForSearch_hasSignIn">
                    <option value="">--请选择--</option>
                <#list hasSignInList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_status" id="BumphNoticeSignInForSearch_status_title">签收进度</label>
            </td>
            <td>
                <select name="status" id="BumphNoticeSignInForSearch_status">
                    <option value="">--请选择--</option>
                <#list statusList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="BumphNoticeSignInForSearch_findButton" id="BumphNoticeSignInForSearch_findButton_title">操作</label>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.bumphNotice.searchSignInRecord();" title="操作">查询</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>