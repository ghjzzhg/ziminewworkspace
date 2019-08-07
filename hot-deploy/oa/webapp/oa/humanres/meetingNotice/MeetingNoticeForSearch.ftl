<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="MeetingNoticeSearchForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="MeetingNoticeSearchForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="MeetingNoticeForSearch_meetingName" id="MeetingNoticeForSearch_meetingName_title">会议名称</label>
            </td>
            <td>
                <input type="text" name="meetingName" size="25" id="MeetingNoticeForSearch_meetingName">
            </td>
            <td class="label">
                <label for="MeetingNoticeForSearch_meetingTheme" id="MeetingNoticeForSearch_meetingTheme_title">会议主题</label>
            </td>
            <td>
                <input type="text" name="meetingTheme" size="25" id="MeetingNoticeForSearch_meetingTheme">
            </td>
            <td class="label">
                <label for="MeetingNoticeForSearch_releaseDepartment" id="MeetingNoticeForSearch_releaseDepartment_title">发布部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="releaseDepartment" importability=true chooserValue="${releaseDepartment?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="MeetingNoticeForSearch_startTime" id="MeetingNoticeForSearch_startTime_title">会议时间</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${startTime?default('')}" size="25" maxlength="30" id="MeetingNoticeForSearch_startTime"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'MeetingNoticeForSearch_endTime\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="MeetingNoticeForSearch_endTime" id="MeetingNoticeForSearch_endTime_title">至</label>
            </td>
            <td colspan="3">
            <@htmlTemplate.renderDateTimeField name="endTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${endTime?default('')}" size="25" maxlength="30" id="MeetingNoticeForSearch_endTime"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'MeetingNoticeForSearch_startTime\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="MeetingNoticeForSearch_findButton" id="MeetingNoticeForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.meetingNotice.searchMeetingNotice();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="MeetingNoticeForSearch_findButton" id="MeetingNoticeForSearch_findButton_title">操作</label>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.meetingNotice.addMeetingNotice();" title="发布会议通知">发布会议通知</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>