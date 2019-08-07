<#include "component://widget/templates/dropDownList.ftl"/>

<script language="javascript">
    function changeValue(){
        if($("input[id='isTrue']").attr("checked")=="checked"){
            $("input[name='repertoryWarning']").val("Y")
        } else if($("input[id='isFalse']").attr("checked")=="checked"){
            $("input[name='repertoryWarning']").val("N")
        }
    }
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="remark"]', {
            allowFileManager: true
        });

    });
    function changeProductType(){
        var productTypeId = $('#productTypeId option:selected') .val();
        $.ajax({
            type: 'post',
            url: "searchWarehouse",
            async: true,
            data:{productTypeId: productTypeId},
            dataType: 'json',
            success:function (data) {
                $("#warehouseName").text(data.data.warehouseName);
                $("input[name='warehouseId']").val(data.data.warehouseId);
                $("#MaximumSafety").text(data.data.MaximumSafety);
                $("#MinimumSafety").text(data.data.MinimumSafety);
                $("#unit").text(data.data.unit);
                var array = new Array(data.data.locationList);
                var locations = array[0];
                var htmlStr = "<select class='validate[required]' name='storageLocation' id='storageLocation'><option value=''>--请选择--</option>";
                for(var i=0;i<locations.length;i++){
                    htmlStr = htmlStr +"<option value='"+locations[i].locationId+"'>"+locations[i].locationName+"</option>";
                }
                htmlStr = htmlStr + "</selcet>"
                console.log(htmlStr);
                $("#storageLocation").remove();
                $("#storageLocationTd").append(htmlStr);
            },
        });
    }
</script>
<form method="post" action="" id="EditOsManager" class="basic-form" name="EditOsManager">
<table cellspacing="0" class="basic-table">
    <tbody>
    <tr>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>物品名称</label>
        </td>
        <td class="jqv">
            <#if data.list?has_content>
                <input type="text" class="validate[required,custom[onlyLetterNumberChinese]]" data-prompt-position="centerRight" maxlength="20" name="suppliesName" value="${data.list.suppliesName?default('')}">
            <#else >
                <input type="text" class="validate[required,custom[onlyLetterNumberChinese]]" data-prompt-position="centerRight" maxlength="20" name="suppliesName" value="">
            </#if>
        </td>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>货品类型</label>
        </td>
        <td class="jqv">
            <select class="validate[required]" name="productTypeId" id="productTypeId" onchange="changeProductType()" data-prompt-position="centerRight">
                <option value="">--请选择--</option>
            <#if data.productTypeList?has_content && data.list?has_content>
                <#list data.productTypeList as list>
                    <#if list.productTypeId?has_content && data.list.productTypeId?has_content  && (data.list.productTypeId?default("")) == (list.productTypeId?default(""))>
                        <option value="${list.productTypeId?default('')}" selected >${list.typeName?default("")}</option>
                    <#else>
                        <option value="${list.productTypeId?default('')}">${list.typeName?default("")}</option>
                    </#if>
                </#list>
            <#elseif data.productTypeList?has_content>
                <#list data.productTypeList as list>
                    <option value="${list.productTypeId?default('')}">${list.typeName?default("")}</option>
                </#list>
            </#if>
            </select>
        </td>
    </tr>
    <tr>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>存放仓库</label>
        </td>
        <td class="jqv">
            <#if data.list?has_content>
                <label id="warehouseName">${data.list.warehouseName}</label>
            <#else>
                <label id="warehouseName"></label>
            </#if>
        </td>

        <td class="label">
            <label><b class="requiredAsterisk">*</b>储存货位</label>
        </td>
        <td class="jqv" id="storageLocationTd">
            <select class="validate[required]" name="storageLocation" id="storageLocation">
                <option value="">--请选择--</option>
            <#if data.locationList?has_content && data.list?has_content>
                <#list data.locationList as list>
                    <#if list.locationId?has_content && data.list.storageLocation?has_content  && (data.list.storageLocation?default("")) == (list.locationId?default(""))>
                        <option value="${list.locationId?default('')}" selected >${list.locationName?default("")}</option>
                    <#else>
                        <option value="${list.locationId?default('')}">${list.locationName?default("")}</option>
                    </#if>
                </#list>
            <#elseif data.locationList?has_content>
                <#list data.locationList as list>
                    <option value="${list.locationId?default('')}">${list.locationName?default("")}</option>
                </#list>
            </#if>
            </select>
        </td>
    </tr>
    <tr>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>库存上限</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <label id="MaximumSafety">${data.list.MaximumSafety?default('')}</label>
        <#else >
            <label id="MaximumSafety"></label>
        </#if>
        </td>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>库存下限</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <label id="MinimumSafety">${data.list.MinimumSafety?default('')}</label>
        <#else >
            <label id="MinimumSafety"></label>
        </#if>
        </td>
    </tr>
    <tr>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>单位</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <label id="unit">${data.list.unitName?default('')}</label>
        <#else >
            <label id="unit"></label>
        </#if>
        </td>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>库存总数</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <input type="text" class="validate[required,custom[number]]" maxlength="9" name="repertoryAmount" value="${data.list.repertoryAmount?default('')}">
        <#else >
            <input type="text" class="validate[required,custom[number]]" maxlength="9" name="repertoryAmount" value="">
        </#if>
        </td>
    </tr>
    <tr>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>是否使用库存预警</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <#if (data.list.repertoryWarning?default(""))=="Y">
                <input type="radio" name="radio" id="isTrue" checked onclick="changeValue()">是
                <input type="radio" name="radio" id="isFalse" onclick="changeValue()">否
                <input type="hidden" name="repertoryWarning" value="Y">
            <#else>
                <input type="radio" name="radio" id="isTrue" onclick="changeValue()">是
                <input type="radio" name="radio" id="isFalse" checked onclick="changeValue()">否
                <input type="hidden" name="repertoryWarning" value="N">
            </#if>
        <#else >
            <input type="radio" name="radio" id="isTrue" checked onclick="changeValue()">是
            <input type="radio" name="radio" id="isFalse" onclick="changeValue()">否
            <input type="hidden" name="repertoryWarning" value="Y">
        </#if>
        </td>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>规格</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <@dropDownList dropDownValueList=data.standardList enumTypeId="PRODUCT_STANDARD" name="standard"  selectedValue="${data.list.standard?default('')}"/>
        <#else >
            <@dropDownList dropDownValueList=data.standardList enumTypeId="PRODUCT_STANDARD" name="standard"  selectedValue=""/>
        </#if>
        </td>
        </tr>
    <tr>
        <td class="label">
            <label><b class="requiredAsterisk">*</b>单价</label>
        </td>
        <td class="jqv">
        <#if data.list?has_content>
            <input type="text" class="validate[required,custom[twoDecimalNumber]]" maxlength="9" name="price" value="${data.list.price?default('')}">
        <#else >
            <input type="text" class="validate[required,custom[twoDecimalNumber]]" maxlength="9" name="price" value="">
        </#if>
        </td>
    </tr>
    <tr>
        <td class="label">
            <label>备注</label>
        </td>
        </tr>
    <tr>
        <td class="jqv" colspan="4">
        <#if data.list?has_content>
            <textarea name="remark">${data.list.remark?default('')}</textarea>
        <#else >
            <textarea name="remark"></textarea>
        </#if>

        </td>
    </tr>
    <tr align="center">
        <td colspan="4">
        <#if data.list?has_content>
            <input type="hidden" name="osManagementId" value="${data.list.osManagementId}">
        </#if>
            <a href="#" class="smallSubmit" onclick="$.osManager.createOsManager()">提交</a>
        </td>
    </tr>
    </tbody>
</table>
</form>
