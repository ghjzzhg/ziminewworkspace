<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#include "component://widget/templates/dropDownList.ftl"/>
<#if fixedAssetsMap?has_content>
    <#assign fixedAssetsId = fixedAssetsMap.fixedAssetsId?default('')>
    <#assign fixedAssetsCode = fixedAssetsMap.fixedAssetsCode?default('')>
    <#assign fixedAssetsName = fixedAssetsMap.fixedAssetsName?default('')>
    <#assign fixedAssetsManager = fixedAssetsMap.fixedAssetsManager?default('')>
    <#assign limitYear = fixedAssetsMap.limitYear?default('')>
    <#assign assetMaker = fixedAssetsMap.assetMaker?default('')>
    <#assign canLendOut = fixedAssetsMap.canLendOut?default('')>
    <#assign assetStatus = fixedAssetsMap.assetStatus?default('')>
    <#assign reserveStatus = fixedAssetsMap.reserveStatus?default('')>
    <#assign buyDate = fixedAssetsMap.buyDate?default('')>
    <#assign assetStandard = fixedAssetsMap.assetStandard?default('')>
    <#assign keepPlace = fixedAssetsMap.keepPlace?default('')>
    <#assign assetType = fixedAssetsMap.assetType?default('')>
    <#assign assetValue = fixedAssetsMap.assetValue?default('')>
    <#assign assetCount = fixedAssetsMap.assetCount?default('')>
    <#assign department = fixedAssetsMap.department?default('')>
    <#assign usePerson = fixedAssetsMap.usePerson?default('')>
    <#assign useDepartment = fixedAssetsMap.useDepartment?default('')>
    <#assign startDate = fixedAssetsMap.startDate?default('')>
    <#assign endDate = fixedAssetsMap.endDate?default('')>
    <#assign remarks = fixedAssetsMap.remarks?default('')>
<#else>
    <#assign fixedAssetsId = ''>
    <#assign fixedAssetsCode = '${number}'>
    <#assign fixedAssetsName = ''>
    <#assign fixedAssetsManager = ''>
    <#assign limitYear = ''>
    <#assign assetMaker = ''>
    <#assign canLendOut = ''>
    <#assign assetStatus = ''>
    <#assign reserveStatus = ''>
    <#assign buyDate = ''>
    <#assign assetStandard = ''>
    <#assign keepPlace = ''>
    <#assign assetType = ''>
    <#assign assetValue = ''>
    <#assign assetCount = ''>
    <#assign department = ''>
    <#assign useDepartment = ''>
    <#assign startDate = ''>
    <#assign endDate = ''>
    <#assign remarks = ''>
</#if>
<script language="javascript">
    $(function () {
    });
</script>
<form method="post" action="" id="FixedAssetsForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="FixedAssetsForm">
    <table cellspacing="0" class="basic-table">
        <input type="hidden" name="fixedAssetsId" value="${fixedAssetsId?default('')}"/>
        <#if !fixedAssetsId?has_content>
            <input type="hidden" name="fixedAssetsCode1" value="${number}"/>
        </#if>
        <tbody>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_fixedAssetsCode" id="FixedAssetsForm_fixedAssetsCode_title">资产编码</label>
            </td>
            <td>
                <label>${fixedAssetsCode?default('')}</label>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_fixedAssetsName" id="FixedAssetsForm_fixedAssetsName_title"><b class="requiredAsterisk">*</b>资产名称</label>
            </td>
            <td class="jqv">
                <input type="text" name="fixedAssetsName" value="${fixedAssetsName}" class="validate[required,custom[onlyLetterNumberChinese],maxSize[30]]" data-prompt-position="centerRight" size="25" id="FixedAssetsForm_fixedAssetsName"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_fixedAssetsManager" id="FixedAssetsForm_fixedAssetsManager_title"><b class="requiredAsterisk">*</b>管理者</label>
            </td>
            <td  class="jqv">
            <@chooser chooserType="LookupEmployee" name="fixedAssetsManager" importability=true chooserValue="${fixedAssetsManager?default('')}"/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_limitYear" id="FixedAssetsForm_limitYear_title"><b class="requiredAsterisk">*</b>使用年限</label>
            </td>
            <td  class="jqv">
                <input type="text" name="limitYear" value="${limitYear}" class="validate[required,custom[onlyNumberSp],maxSize[9]]" size="25" id="FixedAssetsForm_limitYear"/>
                <span class="tooltip">单位：年</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_assetMaker" id="FixedAssetsForm_assetMaker_title">生产厂家</label>
            </td>
            <td>
                <input type="text" name="assetMaker" value="${assetMaker}" class="validate[custom[onlyLetterNumberChinese],maxSize[125]]" size="25" id="FixedAssetsForm_assetMaker"/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_canLendOut" id="FixedAssetsForm_canLendOut_title"><b class="requiredAsterisk">*</b>可否外借</label>
            </td>
            <td  class="jqv">
                <select name="canLendOut" class="validate[required]" id="FixedAssetsForm_canLendOut" size="1">
                    <option value="">--请选择--</option>
                    <option value="Y" <#if canLendOut == 'Y'>selected="selected"</#if>>可外借</option>
                    <option value="N" <#if canLendOut == 'N'>selected="selected"</#if>>不可外借</option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_assetStatus" id="FixedAssetsForm_assetStatus_title"><b class="requiredAsterisk">*</b>资产状态</label>
            </td>
            <td  class="jqv">
                <select name="assetStatus" id="FixedAssetsForm_assetStatus" class="validate[required]">
                    <option value="">--请选择--</option>
                <#list assetStatusList as type >
                    <option value="${type.enumId}" <#if assetStatus == '${type.enumId}'>selected="selected"</#if>>${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_reserveStatus" id="FixedAssetsForm_reserveStatus_title"><b class="requiredAsterisk">*</b>预约状态</label>
            </td>
            <td class="jqv">
                <select name="reserveStatus" id="FixedAssetsForm_reserveStatus" class="validate[required]">
                    <option value="">--请选择--</option>
                <#list assetReserveList as type >
                    <option value="${type.enumId}" <#if reserveStatus == '${type.enumId}'>selected="selected"</#if>>${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_buyDate" id="FixedAssetsForm_buyDate_title"><b class="requiredAsterisk">*</b>购买日期</label></td>
            <td colspan="3" class="jqv">
            <@htmlTemplate.renderDateTimeField name="buyDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${buyDate?default('')}" size="25" maxlength="30" id="FixedAssetsForm_buyDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'FixedAssetsForm_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_assetStandard" id="FixedAssetsForm_assetStandard_title"><b class="requiredAsterisk">*</b>资产规格</label>
            </td>
            <td class="jqv">
                <input type="text" name="assetStandard" value="${assetStandard}" class="validate[required,custom[onlyLetterNumber],maxSize[30]]" size="25" id="FixedAssetsForm_assetStandard"/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_keepPlace" id="FixedAssetsForm_keepPlace_title">存放地点</label>
            </td>
            <td>
                <input type="text" name="keepPlace" value="${keepPlace}" class="validate[custom[onlyLetterNumberChinese],maxSize[125]]" size="25" id="FixedAssetsForm_keepPlace"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_assetType" id="FixedAssetsForm_assetType_title"><b class="requiredAsterisk">*</b>资产类别</label>
            </td>
            <td class="jqv">
            <@dropDownList dropDownValueList=assetTypeList enumTypeId="FIXED_ASSETS_TYPE" inputSize="10" name="assetType" callback="$.fixedAssets.changeAssetType"  selectedValue="${assetType?default('')}"/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_assetValue" id="FixedAssetsForm_assetValue_title">资产价值</label>
            </td>
            <td>
                <input type="text" name="assetValue" value="${assetValue}" class="validate[custom[twoDecimalNumber],maxSize[9]]" size="25" id="FixedAssetsForm_assetValue">
                <span class="tooltip">单位：元</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_assetCount" id="FixedAssetsForm_assetCount_title"><b class="requiredAsterisk">*</b>资产数量</label>
            </td>
            <td class="jqv">
                <input type="text" name="assetCount" value="${assetCount}" class="validate[required,custom[onlyNumberSp],maxSize[8]]" size="25" id="FixedAssetsForm_assetCount"/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_department" id="FixedAssetsForm_department_title"><b class="requiredAsterisk">*</b>归属部门</label>
            </td>
            <td class="jqv">
            <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_usePerson" id="FixedAssetsForm_usePerson_title">使用人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="usePerson" importability=true chooserValue="${usePerson?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_useDepartment" id="FixedAssetsForm_useDepartment_title">使用部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="useDepartment" importability=true chooserValue="${useDepartment?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_startDate" id="FixedAssetsForm_startDate_title">开始时间</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${startDate?default('')}" size="25" maxlength="30" id="FixedAssetsForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'FixedAssetsForm_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="FixedAssetsForm_endDate" id="FixedAssetsForm_endDate_title">结束时间</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${endDate?default('')}" size="25" maxlength="30" id="FixedAssetsForm_endDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'FixedAssetsForm_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForm_remarks" id="FixedAssetsForm_remarks_title">备注</label>
            </td>
            <td colspan="3">
                <textarea name="remarks" class="validate[maxSize[500]]" cols="60" rows="3" id="FixedAssetsForm_remarks">${remarks}</textarea>
            </td>
        </tr>
        <tr>
            <td>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.fixedAssets.saveFixedAssets();" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>