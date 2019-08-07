<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if recordMap?has_content>
    <#assign partyId = recordMap.partyId?default("")>
    <#assign workerSn = recordMap.workerSn?default("")>
    <#assign fullName = recordMap.fullName?default("")>
    <#assign birthDay = recordMap.birthDay?default("")>
    <#assign gender = recordMap.gender?default("")>
    <#assign ifMarried = recordMap.ifMarried?default("")>
    <#assign age = recordMap.age?default("")>
    <#assign salary = recordMap.salary?default("")>
    <#assign politicalStatus = recordMap.politicalStatus?default("")>
    <#assign phoneNumber = recordMap.phoneNumber?default("")>
    <#assign familyAddress = recordMap.familyAddress?default("")>
    <#assign domicileType = recordMap.domicileType?default("")>
    <#assign placeOfOrigin = recordMap.placeOfOrigin?default("")>
    <#assign cardId = recordMap.cardId?default("")>
    <#assign department = recordMap.department?default("")>
    <#assign post = recordMap.post?default("")>
    <#assign workDate = recordMap.workDate?default("")>
    <#assign position = recordMap.position?default("")>
    <#assign laborType = recordMap.laborType?default("")>
    <#assign degree = recordMap.degree?default("")>
    <#assign diploma = recordMap.diploma?default("")>
</#if>
<#--<script language="JavaScript">
    $(function () {
           $('input[name="fullName"]').data("promptPosition", "bottomLeft");
        })
</script>-->
<form method="post" action="" id="CreateEmployee" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="CreateEmployee">
    <input type="hidden" name="partyId" value="${partyId?default('')}" id="CreateEmployee_partyId">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label for="CreateEmployee_fullName" id="CreateEmployee_fullName_title"><b class="requiredAsterisk">*</b>员工姓名：</label>  </td>
            <td width="40%" class="jqv">
                <input type="text" name ="fullName" value="${fullName?default("")}" class="validate[required,maxSize[10],custom[onlyLetterChinese]]">
            </td>
            <td class="label">
                <label for="CreateEmployee_workerSn" id="CreateEmployee_workerSn_title">员工工号：</label>
            </td>
            <td colspan="2" class="jqv">
                ${workerSn?default('')}
                <input type="hidden" name="workerSn" value="${workerSn?default('')}" id="CreateEmployee_workerSn">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_birthDay" id="CreateEmployee_birthDay_title">出生日期：</label>  </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="birthDay" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${birthDay?default('')}" size="25" maxlength="30" id="CreateEmployee_birthDay"
            dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'CreateEmployee_workDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="CreateEmployee_gender" id="CreateEmployee_gender_title"><b class="requiredAsterisk">*</b>员工性别：</label>  </td>
            <td colspan="2" class="jqv">
            <#assign genderList = delegator.findByAnd("Enumeration", {"enumTypeId" : "GENDER"})>
                <select name="gender" id="CreateEmployee_gender" class="validate[required]">
                <#if genderList?has_content>
                    <option value="" >请选择</option>
                    <#list genderList as list>
                        <#if gender?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_ifMarried" id="CreateEmployee_ifMarried_title">婚否：</label>  </td>
            <td class="jqv">
            <#assign ifMarriedList = delegator.findByAnd("Enumeration", {"enumTypeId" : "IS_MARRIED"})>
                <select name="ifMarried" id="CreateEmployee_ifMarried" size="1">
                <#if ifMarriedList?has_content>
                    <option value="" >请选择</option>
                    <#list ifMarriedList as list>
                        <#if ifMarried?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="CreateEmployee_age" id="CreateEmployee_age_title">年龄：</label>  </td>
            <td colspan="2" class="jqv">
                <input type="text" name ="age" value="${age?default("")}" class="validate[custom[onlyNumberSp],maxSize[2]],minSize[2]]">
            </td>

        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_salary" id="CreateEmployee_salary_title"><b class="requiredAsterisk">*</b>基本工资：</label>  </td>
            <td class="jqv">
                <input type="text" name ="salary" value="${salary?default("")}" class="validate[required,maxSize[20],custom[twoDecimalNumber]]">
            </td>
            <td class="label">
                <label for="CreateEmployee_politicalStatus" id="CreateEmployee_politicalStatus_title">政治面貌：</label>  </td>
            <td colspan="2" class="jqv">
            <#assign politicalStatusList = delegator.findByAnd("Enumeration", {"enumTypeId" : "POLITICAL_STATUS"})>
                <select name="politicalStatus" id="CreateEmployee_politicalStatus" size="1">
                <#if politicalStatusList?has_content>
                    <option value="" >请选择</option>
                    <#list politicalStatusList as list>
                        <#if politicalStatus?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_phoneNumber" id="CreateEmployee_phoneNumber_title"><b class="requiredAsterisk">*</b>手机号：</label>  </td>
            <td class="jqv">
                <input type="text" name ="phoneNumber" value="${phoneNumber?default("")}" class="validate[required,custom[isMobile]]">
            </td>
            <td class="label">
                <label for="CreateEmployee_familyAddress" id="CreateEmployee_familyAddress_title"><b class="requiredAsterisk">*</b>家庭住址：</label>  </td>
            <td colspan="2" class="jqv">
                <input type="text" name ="familyAddress" value="${familyAddress?default("")}" class="validate[required,maxSize[125]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_domicileType" id="CreateEmployee_domicileType_title"><b class="requiredAsterisk">*</b>户籍类型：</label>  </td>
            <td class="jqv">
            <#assign domicileTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "DOMICILE_TYPE"})>
                <select name="domicileType" id="CreateEmployee_domicileType" size="1">
                <#if domicileTypeList?has_content>
                    <#list domicileTypeList as list>
                        <#if domicileType?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="CreateEmployee_placeOfOrigin" id="CreateEmployee_placeOfOrigin_title">籍贯：</label>  </td>
            <td colspan="2" class="jqv">
                <input type="text" name ="placeOfOrigin" value="${placeOfOrigin?default("")}" class="validate[maxSize[20]]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_cardId" id="CreateEmployee_cardId_title"><b class="requiredAsterisk">*</b>身份证号：</label>
            </td>
            <td class="jqv">
                <input type="text" name ="cardId" id="CreateEmployee_cardId" value="${cardId?default("")}" class="validate[required,custom[isIdCardNo]]">
            </td>
            <td class="label">
                <label for="CreateEmployee_department" id="CreateEmployee_department_title"><b class="requiredAsterisk">*</b>直属部门：</label>  </td>
            <td colspan="2" class="jqv">
                <@chooser chooserType="LookupDepartment" name="department" chooserValue="${department?default('')}" required=true/>
            </td>

        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_post" id="CreateEmployee_post_title"><b class="requiredAsterisk">*</b>岗位：</label>  </td>
            <td class="jqv">
            <@chooser chooserType="LookupOccupation" name="post" chooserValue="${post?default('')}" required=true/>
            </td>
            <td class="label">
                <label for="CreateEmployee_workDate" id="CreateEmployee_workDate_title"><b class="requiredAsterisk">*</b>入职日期：</label>  </td>
            <td colspan="2" class="jqv">
            <@htmlTemplate.renderDateTimeField name="workDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${workDate?default('')}" size="25" maxlength="30" id="CreateEmployee_workDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'CreateEmployee_birthDay\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_position" id="CreateEmployee_position_title"><b class="requiredAsterisk">*</b>职级：</label>  </td>
            <td>
            <#assign positionList = delegator.findByAnd("Enumeration", {"enumTypeId" : "POSITION_LEVEL"})>
                <select name="position" id="CreateEmployee_position" class="validate[required]">
                <#if positionList?has_content>
                    <option value="" >请选择</option>
                    <#list positionList as list>
                        <#if position?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="CreateEmployee_laborType" id="CreateEmployee_laborType_title"><b class="requiredAsterisk">*</b>用工类型：</label>  </td>
            <td colspan="2" class="jqv">
            <#assign laborTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "LABOR_TYPE"})>
                <select name="laborType" id="CreateEmployee_laborType" class="validate[required]">
                <#if laborTypeList?has_content>
                    <option value="" >请选择</option>
                    <#list laborTypeList as list>
                        <#if laborType?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>

        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_degree" id="CreateEmployee_degree_title">所获学位：</label>
            </td>
            <td>
            <#assign degreeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "EDUCATION_DEGREE"})>
                <select name="degree" id="CreateEmployee_degree" size="1">
                <#if degreeList?has_content>
                    <option value="" >请选择</option>
                    <#list degreeList as list>
                        <#if degree?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="CreateEmployee_diploma" id="CreateEmployee_diploma_title">所获学历：</label>
            </td>
            <td colspan="2" class="jqv">
            <#assign diplomaList = delegator.findByAnd("Enumeration", {"enumTypeId" : "EDUCATION_DIPLOMA"})>
                <select name="diploma" id="CreateEmployee_diploma" size="1">
                <#if diplomaList?has_content>
                    <#list diplomaList as list>
                        <#if diploma?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>
                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreateEmployee_createButton" id="CreateEmployee_createButton_title">操作</label>  </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.saveEmployee()" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>