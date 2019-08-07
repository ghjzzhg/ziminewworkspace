<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('#meetingNotice_content', {
            allowFileManager: true
        });

        $("textarea[name='checkPerson']").click(function () {
            displayInTab3("AddTransactionTab", "与会人员", {
                requestUrl: "checkPerson",
                data: {param: "person"},
                width: "600px",
                position: 'top'
            });
        });
        $("textarea[name='range']").click(function () {
            displayInTab3("AddTransactionTab", "设置浏览权限", {
                requestUrl: "checkPerson",
                data: {param: "range"},
                width: "600px",
                position: 'top'
            });
        });
    });
    function checkStatus(checkbox){
        if ( checkbox.checked == true){
            var content1 = document.getElementById('meetingNotice_participants').innerHTML;
            var stringId = document.getElementById('meetingNotice_participants_participants').value;
            document.getElementById('BumphNotice_noticeDataScope').innerHTML = content1;
            document.getElementById('noticeDataScope_user').value = stringId;
        } else{
            document.getElementById('BumphNotice_noticeDataScope').innerHTML = " ";
            document.getElementById('noticeDataScope_user').value = "";
        }
    }
    function cleanArea(){
        document.getElementById('meetingNotice_extParticipants').value = "";
    }
</script>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#if meetingNoticeMap?has_content>
    <#assign meetingNoticeId = meetingNoticeMap.meetingNoticeId?default('')>
    <#--<#assign releaseGroupName = meetingNoticeMap.groupName?default('')>-->
    <#assign releaseTime = meetingNoticeMap.releaseTime?default('')>
    <#assign meetingName = meetingNoticeMap.meetingName?default('')>
    <#assign meetingStartTime = meetingNoticeMap.meetingStartTime?default('')>
    <#assign meetingEndTime = meetingNoticeMap.meetingEndTime?default('')>
    <#assign meetingPlace = meetingNoticeMap.meetingPlace?default('')>
    <#assign presenter = meetingNoticeMap.presenter?default('')>
    <#assign hasSignIn = meetingNoticeMap.hasSignIn?default('')>
    <#assign meetingNoticeNumber = meetingNoticeMap.meetingNoticeNumber?default('')>
    <#assign meetingTheme = meetingNoticeMap.meetingTheme?default('')>
    <#assign extParticipants = meetingNoticeMap.extParticipants?default('')>
    <#assign signInPersonList = delegator.findByAnd("TblSignInPerson",{"noticeId":meetingNoticeId,"signInPersonType":"TblMeetingNotice"})>

    <#if signInPersonList?has_content>
        <#assign staffIds = "">
        <#list signInPersonList as sipList>
            <#assign staffIds = staffIds + sipList.staffId + ",">
        </#list>
        <#if staffIds?has_content&&staffIds!=" ">
            <#assign staffIds = staffIds?trim>
        </#if>
    </#if>
    <#assign scopeData = dispatcher.runSync("getDataScope", Static["org.ofbiz.base.util.UtilMisc"].toMap("entityName", "TblMeetingNotice", "dataId", meetingNoticeId, "dataAttr", "", "userLogin", userLogin))/>
    <#if scopeData?has_content>
        <#assign description=scopeData.description?default("")/>
    </#if>
    <#assign scope = " ">
    <#assign content = meetingNoticeMap.content?default('')>
<#else >
    <#assign meetingNoticeId = ''>
    <#--<#assign releaseGroupName = ''>-->
    <#assign releaseTime = ''>
    <#assign meetingName = ''>
    <#assign meetingStartTime = ''>
    <#assign meetingEndTime = ''>
    <#assign meetingPlace = ''>
    <#assign presenter = ''>
    <#assign hasSignIn = ''>
    <#assign meetingNoticeNumber = '${number}'>
    <#assign meetingTheme = ''>
    <#assign extParticipants = ''>
    <#assign participants = " ">
    <#assign scope = " ">
    <#assign content = ''>
</#if>
<#assign strSingIn ="">
<#if signInPersonList?has_content>
    <#list signInPersonList as signIn>
        <#assign strSingIn = strSingIn+","+signIn.staffId>
    </#list>
    <#assign strSingIn = strSingIn?substring(1)>
</#if>
<div style="padding: 20px">
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
    <form name="meetingNoticeForm" id="meetingNoticeForm" class="basic-form">
        <input type="hidden" name="meetingNoticeId" value="${meetingNoticeId}">
        <div>
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                会 议 通 知
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
                                        <label for="meetingNotice_meetingNoticeNumber" id="meetingNotice_meetingNoticeNumber_title"><b class="requiredAsterisk">*</b>会议编号：</label>
                                    </td>
                                    <td colspan="3" class="jqv">
                                        <label>${meetingNoticeNumber}</label>
                                        <input id="meetingNotice_meetingNoticeNumber" name="meetingNoticeNumber"
                                               type="hidden" value="${meetingNoticeNumber}"/>
                                        <#--<span class="tooltip">(一般为3位流水号，无需更改，由系统按流水号顺序自动生成)</span>-->
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_meetingName" id="meetingNotice_meetingName_title"><b class="requiredAsterisk">*</b>会议名称：</label>
                                    </td>
                                    <td colspan="3" class="jqv">
                                        <input type="text" style="width: 100%" id="meetingNotice_meetingName" name="meetingName" value="${meetingName}" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="125">
                                    </td>

                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_meetingTheme" id="meetingNotice_meetingTheme_title"><b class="requiredAsterisk">*</b>会议主题：</label>
                                    </td>
                                    <td colspan="3" class="jqv">
                                        <input type="text" style="width: 100%" id="meetingNotice_meetingTheme" name="meetingTheme" value="${meetingTheme}" class="validate[required]" maxlength="125">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_meetingTime" id="meetingNotice_meetingTime_title"><b class="requiredAsterisk">*</b>会议时间:</label>
                                    </td>
                                    <td colspan="3" class="jqv">
                                    <@htmlTemplate.renderDateTimeField name="meetingStartTime" event="" action="" className="validate[required,past[meetingEndTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
                                    value="${meetingStartTime?default('')}" size="25" maxlength="30" id="meetingNotice_meetingStartTime"
                                    dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'meetingNotice_meetingEndTime\\')}'" shortDateInput=false timeDropdownParamName=""
                                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>至

                                    <@htmlTemplate.renderDateTimeField name="meetingEndTime" event="" action="" className="validate[required,future[meetingStartTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
                                    value="${meetingEndTime?default('')}" size="25" maxlength="30" id="meetingNotice_meetingEndTime"
                                    dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'meetingNotice_meetingStartTime\\')}'" shortDateInput=false timeDropdownParamName=""
                                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>

                                     <#--   <input type="date" id="meetingNotice_meetingTime" name="meetingStartTime" value="${meetingStartTime}">至
                                        <input type="date" id="meetingNotice_meetingTime" name="meetingEndTime" value="${meetingEndTime}">-->
                                    </td>

                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_meetingPlace" id="meetingNotice_meetingPlace_title"><b class="requiredAsterisk">*</b>会议地点：</label>
                                    </td>
                                    <td class="jqv">
                                        <input type="text" id="meetingNotice_meetingPlace" name="meetingPlace" value="${meetingPlace}" class="validate[required]" maxlength="125">
                                    </td>
                                    <td class="label">　
                                        <label for="meetingNotice_presenter" id="meetingNotice_presenter_title"><b class="requiredAsterisk">*</b>主 持 人：</label>
                                    </td>
                                    <td class="jqv">
                                        <input type="text" id="meetingNotice_presenter" name="presenter" value="${presenter}" class="validate[required]">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                            <label for="meetingNotice_participants" id="meetingNotice_participants_title"><b class="requiredAsterisk">*</b>参加人员：</label>
                                    </td>
                                    <td colspan="2" class="jqv">
                                    <@selectStaff id="meetingNotice_participants" name="participants" value="${staffIds?default('')}" multiple=true required=true />
                                    </td>
                                    <td>
                                        <input type="checkbox" name="hasSignIn" value="Y">需签收确认<br>
                                        <input type="checkbox">发送手机短信<br>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_extParticipants" id="meetingNotice_extParticipants_title">手工添加系统中没有的参会人员：</label>
                                    </td>
                                    <td colspan="2">
                                        <textarea id="meetingNotice_extParticipants" name="extParticipants">${extParticipants?default('')}</textarea>
                                    </td>
                                    <td>
                                        <a href="#" onclick="cleanArea()" class="smallSubmit">清除</a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_i" id="meetingNotice_i_title">发放范围：</label><br>
                                        <span class="tooltip">(留空则全员均可浏览)</span>
                                    </td>
                                    <td colspan="2">
                                    <@dataScope id="BumphNotice_noticeDataScope" name="noticeDataScope" dataId="${meetingNoticeId?default('')}" entityName="TblMeetingNotice"/>
                                    </td>
                                    <td>
                                        <input type="checkbox" onclick="checkStatus(this)">仅与会人员<br>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingNotice_k" id="meetingNotice_k_title"><b class="requiredAsterisk">*</b>会议主要讨论事宜：</label>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="jqv">
                                        <textarea id="meetingNotice_content" name="content" style="width:100%;" class="validate[required]" >${content}</textarea>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                    <tr align="center">
                        <td>
                            <a href="#" class="smallSubmit" onclick="$.meetingNotice.saveMeetingNotice(template)">发布通知</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </form>
</div>