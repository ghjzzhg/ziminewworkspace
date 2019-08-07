<#assign scheduleDay = parameters.scheduleDay>
<form id="ScheduleForm" name="ScheduleForm" class="basic-form">
    <input type="hidden" name="workLogId" value="${parameters.workLogId?default('')}">
    <input type="hidden" name="scheduleId" value="${parameters.scheduleId?default('')}">
    <table class="basic-table">
        <tr>
            <td>
                <label for="ScheduleForm_scheduleTitle" id="ScheduleForm_scheduleTitle_title">标题:</label>
            </td>
            <td ><input type="text" name="scheduleTitle" id="ScheduleForm_scheduleTitle_title" value="${parameters.scheduleTitle?default('')}" data-prompt-position="bottomLeft" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="10"></td>
        </tr>
        <tr>
            <td >
                <label for="ScheduleForm_scheduleStartDatetime" id="ScheduleForm_scheduleStartDatetime_title">开始时间:</label>
            </td>
            <td>
                <#if parameters.scheduleStartDatetime?has_content>
                    <#assign startTimeH = parameters.scheduleStartDatetime?substring(11,13)>
                    <#assign startTimeM = parameters.scheduleStartDatetime?substring(19,21)>
                </#if>
                <@scheduleTime name='scheduleStartDatetime' timeH=startTimeH timeM=startTimeM></@scheduleTime>
            </td>
        </tr>
        <tr>
            <td>
                <label for="ScheduleForm_scheduleEndDatetime" id="ScheduleForm_scheduleEndDatetime_title">结束时间:</label>
            </td>
            <td>
            <#if parameters.scheduleEndDatetime?has_content>
                <#assign endTimeH = parameters.scheduleEndDatetime?substring(11,13)>
                <#assign endTimeM = parameters.scheduleEndDatetime?substring(19,21)>
            </#if>
            <@scheduleTime name='scheduleEndDatetime' timeH=endTimeH timeM=endTimeM></@scheduleTime>
            </td>
        </tr>
        <tr>
            <td>内容：</td>
            <td><textarea name="scheduleContent" class="validate[required]" >${parameters.scheduleContent?default('')}</textarea></td>
        </tr>
        <tr>
            <td><a href="#" class="smallSubmit" onclick="$.workLog.saveSchedule();">确定</a></td>
        </tr>
    </table>
</form>

<#macro scheduleTime name timeH='' timeM=''>
    <input type="text" name="${name}_show" value="${scheduleDay?substring(0,10)}" disabled size="10">
    <input type="hidden" name="${name}" value="${scheduleDay?substring(0,10)}">

    <select name="${name}_hour">
        <#list 1..9 as list>
            <#if timeH != ''>
                <#if timeH?substring(1,2)?number == list>
                    <option value="0${list}" selected>0${list}</option>
                <#else >
                    <option value="0${list}">0${list}</option>
                </#if>
            <#else >
                <option value="0${list}" <#if list == 8>selected</#if>>0${list}</option>
            </#if>
        </#list>
        <#list 10..23 as list>
            <#if timeH != '' && timeH?number == list>
                <option value="${list}" selected>${list}</option>
            <#else >
                <option value="${list}">${list}</option>
           </#if>
       </#list>
    </select>:
    <select name="${name}_minute">
        <#list 0..5 as list>
            <#if timeM != ''>
                <#if timeM?substring(0,1)?number == list>
                    <option value="${list}0" selected>${list}0</option>
                <#else >
                    <option value="${list}0">${list}0</option>
                </#if>
            <#else >
                <option value="${list}0" <#if list == 0>selected</#if>>${list}0</option>
            </#if>
        </#list>
    </select>
</#macro>