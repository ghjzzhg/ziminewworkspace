<#if listOfWorkByWeek?has_content>
    <#assign listOfWorkByWeekId = listOfWorkByWeek.listOfWorkByWeekId>
    <#assign listOfWorkByWeekName = listOfWorkByWeek.listOfWorkByWeekName?default('')>
    <#assign defaultValue = listOfWorkByWeek.defaultValue?default('')>
    <#assign sun = listOfWorkByWeek.sun?default('')>
    <#assign mon = listOfWorkByWeek.mon?default('')>
    <#assign tue = listOfWorkByWeek.tue?default('')>
    <#assign wed = listOfWorkByWeek.wed?default('')>
    <#assign thu = listOfWorkByWeek.thu?default('')>
    <#assign fri = listOfWorkByWeek.fri?default('')>
    <#assign sat = listOfWorkByWeek.sat?default('')>
    <#assign sunName = listOfWorkByWeek.sunName?default('')>
    <#assign monName = listOfWorkByWeek.monName?default('')>
    <#assign tueName = listOfWorkByWeek.tueName?default('')>
    <#assign wedName = listOfWorkByWeek.wedName?default('')>
    <#assign thuName = listOfWorkByWeek.thuName?default('')>
    <#assign friName = listOfWorkByWeek.friName?default('')>
    <#assign satName = listOfWorkByWeek.satName?default('')>
<#else>
    <#assign listOfWorkByWeekName = ''>
    <#assign defaultValue = ''>
    <#assign sun = ''>
    <#assign mon = ''>
    <#assign tue = ''>
    <#assign wed = ''>
    <#assign thu = ''>
    <#assign fri = ''>
    <#assign sat = ''>
    <#assign sunName = ''>
    <#assign monName = ''>
    <#assign tueName = ''>
    <#assign wedName = ''>
    <#assign thuName = ''>
    <#assign friName = ''>
    <#assign satName = ''>
</#if>
<#include "component://widget/templates/chooser.ftl"/>
<form id="ListOfWorkByWeekForm" name="ListOfWorkByWeekForm" class="basic-form">
    <input type="hidden" name="listOfWorkByWeekId" value="${listOfWorkByWeekId?default('')}">
    <div class="screenlet" id="screenlet_1">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">基本信息</li>

            </ul>
            <br class="clear"></div>
        <div id="screenlet_1_col" class="screenlet-body">
            <table cellspacing="0" class="basic-table">
                <tbody>
                <tr>
                    <td class="label">
                        <label for="ListOfWorkByWeekForm_name" id="ListOfWorkByWeekForm_name_title"><b class="requiredAsterisk">*</b>班制名称</label></td>
                    <td>
                        <input type="text" name="listOfWorkByWeekName" size="25" id="ListOfWorkByWeekForm_name" value="${listOfWorkByWeekName}" class="validate[required,custom[onlyLetterChinese]]" maxlength="125">
                    </td>
                    <td class="label">
                        <label for="ListOfWorkByWeekForm_defaultValue"
                               id="ListOfWorkByWeekForm_defaultValue_title">是否缺省</label></td>
                    <td>
                    <#assign yes_no = delegator.findByAnd("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","COMMON_Y_N")) />
                        <#if yes_no?has_content>
                            <#list yes_no as yn>
                                <span>
                                    <label>
                                        <#if defaultValue==yn.enumId>
                                                <input type="radio" name="defaultValue" value="${yn.enumId}" checked> ${yn.description}
                                            <#else >
                                                <input type="radio" name="defaultValue" value="${yn.enumId}"> ${yn.description}
                                        </#if>

                                    </label>
                                 </span>
                            </#list>
                        </#if>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="screenlet" id="screenlet_2">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">班次设定</li>

            </ul>
            <br class="clear">
        </div>
        <div id="screenlet_2_col" class="screenlet-body">
            <input type="hidden" name="a" id="WorkRegimeAddForm_a">
            <table cellspacing="0" class="basic-table">
                <tbody>
                <tr>
                    <td class="label">
                        <label for="WorkRegimeAddForm_sun" id="WorkRegimeAddForm_sun_title">周日</label>
                    </td>

                    <td>
                    <@chooser strTab="选择班次" chooserType="WorkNumAjax" name="work_sun" chooserValue="${sun?default('')}" chooserValueName="${sunName?default('')}" required=false/>
                    </td>
                    <td class="label">
                        <label for="WorkRegimeAddForm_mon" id="WorkRegimeAddForm_mon_title" >周一</label>
                    </td>

                    <td>
                    <@chooser strTab="选择班次" strTab="选择班次" chooserType="WorkNumAjax" name="work_mon" chooserValue="${mon?default('')}" chooserValueName="${monName?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择班次" formName="ListOfWorkByWeekForm" name="work_mon" size="10" value="${mon}" id="coordinatesValue_mon" fieldFormName="WorkNumAjax" position="center"/>-->
                    </td>
                    <td class="label">
                        <label for="WorkRegimeAddForm_tue" id="WorkRegimeAddForm_tue_title">周二</label>
                    </td>

                    <td>
                    <@chooser strTab="选择班次" chooserType="WorkNumAjax" name="work_tue" chooserValue="${tue?default('')}" chooserValueName="${tueName?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择班次" formName="ListOfWorkByWeekForm" name="work_tue" value="${tue}" size="10" id="coordinatesValue_tue" fieldFormName="WorkNumAjax" position="center"/>-->
                    </td>
                    <td class="label">
                        <label for="WorkRegimeAddForm_wed" id="WorkRegimeAddForm_wed_title">周三</label>
                    </td>

                    <td>
                    <@chooser strTab="选择班次" chooserType="WorkNumAjax" name="work_wed" chooserValue="${wed?default('')}" chooserValueName="${wedName?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择班次" formName="ListOfWorkByWeekForm" name="work_wed" value="${wed}" size="10" id="coordinatesValue_wed" fieldFormName="WorkNumAjax" position="center"/>-->
                    </td>
                </tr>
                <tr>
                    <td class="label">
                        <label for="WorkRegimeAddForm_thu" id="WorkRegimeAddForm_thu_title">周四</label>
                    </td>

                    <td>
                    <@chooser strTab="选择班次" chooserType="WorkNumAjax" name="work_thu" chooserValue="${thu?default('')}" chooserValueName="${thuName?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择班次" formName="ListOfWorkByWeekForm" name="work_thu" value="${thu}" size="10" id="coordinatesValue_thu" fieldFormName="WorkNumAjax" position="center"/>-->
                    </td>
                    <td class="label">
                        <label for="WorkRegimeAddForm_fri" id="WorkRegimeAddForm_fri_title">周五</label>
                    </td>

                    <td>
                    <@chooser strTab="选择班次" chooserType="WorkNumAjax" name="work_fri" chooserValue="${fri?default('')}" chooserValueName="${friName?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择班次" formName="ListOfWorkByWeekForm" name="work_fri" value="${fri}" size="10" id="coordinatesValue_fri" fieldFormName="WorkNumAjax" position="center"/>-->
                    </td>
                    <td class="label">
                        <label for="WorkRegimeAddForm_sat" id="WorkRegimeAddForm_sat_title">周六</label>
                    </td>

                    <td colspan="3">
                    <@chooser strTab="选择班次" chooserType="WorkNumAjax" name="work_sat" chooserValue="${sat?default('')}" chooserValueName="${satName?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择班次" formName="ListOfWorkByWeekForm" name="work_sat" value="${sat}" size="10" id="coordinatesValue_sat" fieldFormName="WorkNumAjax" position="center"/>-->
                    </td>
                </tr>
                <tr>
                    <td colspan="8"><a href="#" class="smallSubmit" onclick="$.checkingIn.saveListOfWorkByWeek()">保存</a></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</form>