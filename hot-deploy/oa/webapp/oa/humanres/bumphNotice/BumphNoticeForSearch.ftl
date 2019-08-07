<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="BumphNoticeSearchForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="BumphNoticeSearchForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="BumphNoticeForSearch_title" id="BumphNoticeForSearch_title_title">文档标题</label>
            </td>
            <td>
                <input type="text" name="title" size="25" id="BumphNoticeForSearch_title">
            </td>
            <td class="label">
                <label for="BumphNoticeForSearch_releaseDepartment" id="BumphNoticeForSearch_releaseDepartment_title">发布部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="releaseDepartment" importability=true chooserValue="${releaseDepartment?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="BumphNoticeForSearch_noticeType" id="BumphNoticeForSearch_noticeType_title">文档类型</label>
            </td>
            <td>
                <select name="noticeType" id="BumphNoticeForSearch_noticeType">
                    <option value="">--请选择--</option>
                <#list noticeTypeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="BumphNoticeForSearch_releaseTimeStart" id="BumphNoticeForSearch_releaseTimeStart_title">发布日期</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="releaseTimeStart" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${releaseTimeStart?default('')}" size="25" maxlength="30" id="BumphNoticeForSearch_releaseTimeStart" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'BumphNoticeForSearch_releaseTimeEnd\\')}'"
            shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="BumphNoticeForSearch_releaseTimeEnd" id="BumphNoticeForSearch_releaseTimeEnd_title">至</label>
            </td>
            <td colspan="3">
            <@htmlTemplate.renderDateTimeField name="releaseTimeEnd" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${releaseTimeEnd?default('')}" size="25" maxlength="30" id="BumphNoticeForSearch_releaseTimeEnd" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'BumphNoticeForSearch_releaseTimeStart\\')}'"
            shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="BumphNoticeForSearch_findButton" id="BumphNoticeForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.bumphNotice.searchNotice();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="BumphNoticeForSearch_create" id="BumphNoticeForSearch_create_title">操作</label>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.bumphNotice.addBumphNotice(null,'发布通知');" title="操作">新增公文通知</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>