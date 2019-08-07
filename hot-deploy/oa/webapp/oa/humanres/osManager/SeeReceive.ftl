<#include "component://widget/templates/dropDownList.ftl"/>
<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="checkNote"]', {
            allowFileManager: true
        });
        $("#descriptionLab").html(unescapeHtmlText('${data.list.checkNote?default('')}'));
    });
</script>

<form method="post" action="" id="CheckReceive" class="basic-form" name="CheckReceive">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>仓库名称</label>
            </td>
            <td class="jqv">
                <#if data.list?has_content>
                    <label>${data.list.warehouseName?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>出库类型</label>
            </td>
            <td class="jqv">
                <#if data.list?has_content>
                    <label>${data.list.outInventoryTypeName?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>预计领用日期</label>
            </td>
            <td>
                <#if data.list?has_content>
                    <label>${data.list.receiveDate?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>领用部门</label>
            </td>
            <td>
                <#if data.list?has_content>
                    <label>${data.list.receiveDepartmentName?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>领用人</label>
            </td>
            <td>
                <#if data.list?has_content>
                    <label>${data.list.receivePersonName?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>制单人</label>
            </td>
            <td>
                <#if data.list?has_content>
                    <label>${data.list.makeInfoPersonName?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>是否需要审核</label>
            </td>
            <td>
                <#if data.list?has_content>
                    <#if (data.list.isCheck?default(''))=="Y">
                        <label>是</label>
                    <#elseif (data.list.isCheck?default(''))=="N">
                        <label>否</label>
                    </#if>
                <#else>
                    <label></label>
                </#if>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>审核岗位</label>
            </td>
            <td>
                <#if data.list?has_content>
                    <label>${data.list.positionName?default('')}</label>
                <#else>
                    <label></label>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>审批意见:</label>
            </td>
            <td colspan="3">
                <label id="descriptionLab"></label>
            </td>
        </tr>
        <div class="screenlet-body">
            <div id="ListSubOrgs">
                <script>
                    function removeTr(id){
                        $("#tr_"+id).remove();
                    }
                </script>
                <table cellspacing="0" id="addOutInventoryTable" class="basic-table hover-bar">
                    <tbody>
                    <tr class="header-row-2">
                        <td>
                            <label>货品名称</label>
                        </td>
                        <td>
                            <label>规格</label>
                        </td>
                        <td>
                            <label>类别</label>
                        </td>
                        <td>
                            <label>单位</label>
                        </td>
                        <td>
                            <label>可用数量</label>
                        </td>
                        <td>
                            <label>库存数量</label>
                        </td>
                        <td>
                            <label>本次领用数量</label>
                        </td>
                    </tr>
                    <#if data.productInfoList?has_content>
                        <#list data.productInfoList as line>
                        <tr id="ss">
                            <td>
                                <label>${line.suppliesName?default('')}</label>
                            </td>
                            <td>
                                <label>${line.standardName?default('')}</label>
                            </td>
                            <td>
                                <label>${line.productTypeName?default('')}</label>
                            </td>
                            <td>
                                <label>${line.unitName?default('')}</label>
                            </td>
                            <td>
                                <label>${line.canUseAmount?default('')}</label>
                            </td>
                            <td>
                                <label>${line.repertoryAmount?default('')}</label>
                            </td>
                            <td>
                                <label>${line.number?default("")}</label>
                            </td>
                        </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
        </div>
        </tbody>
    </table>
</form>
