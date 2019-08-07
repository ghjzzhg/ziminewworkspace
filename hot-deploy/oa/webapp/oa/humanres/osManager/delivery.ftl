
<script>
    function checkValue(nos){
        var deliveryAmount = $("#deliveryAmount_"+nos).val();
        var receivedAmount = $("#receivedAmount_"+nos).val();
        var number =$("#number_"+nos).val();
        if(deliveryAmount>number-receivedAmount){
            showInfo("发货量总和不能大于应发货量");
        }
    }
</script>
<form method="post" action="" id="deliveryReceive" class="basic-form" name="deliveryReceive">
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
                    <label>${data.list.checkJob?default('')}</label>
                <#else>
                    <label></label>
                </#if>
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
                        <td>
                            <label>已领数量</label>
                        </td>
                        <td>
                            <label>发货数量</label>
                        </td>
                    </tr>
                    <#assign deliveryNum=-1>
                    <#if data.productInfoList?has_content>
                        <#list data.productInfoList as line>
                        <tr id="ss">
                            <td>
                                <label>${line.suppliesName?default('')}</label>
                                <input type="hidden" name="addProductId" value="${line.addProductId?default('')}">
                                <input type="hidden" name="notReceiveAmount" value="${line.notReceiveAmount?default('0')}">
                                <input type="hidden" name="osManagementId" value="${line.osManagementId?default('0')}">
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
                                <label>${line.number?default("0")}</label>
                                <input type="hidden" name="number" id="number_${line_index}" value="${line.number?default('0')}">
                            </td>
                            <td>
                                <label>${line.receivedAmount?default('')}</label>
                                <input type="hidden" name="receivedAmount" id="receivedAmount_${line_index}" value="${line.receivedAmount?default('0')}">
                            </td>
                            <#assign deliveryNum = line.number - line.receivedAmount/>
                            <#if deliveryNum<=0>
                                <td class="jqv">
                                    <label><b class="requiredAsterisk">${deliveryNum}</b></label>
                                    <input type="hidden" name="deliveryAmount" value="${deliveryNum}">
                                </td>
                            <#else>
                                <td class="jqv">
                                    <input type="text" class="validate[required,custom[numberWithoutDecimal]]" onblur="checkValue(${line_index})" name="deliveryAmount" id="deliveryAmount_${line_index}" value="${deliveryNum}">
                                </td>
                            </#if>
                        </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
        </div>
        <#if 0<deliveryNum>
            <tr>
                <td >
                    <a name="add" class="smallSubmit" href="#" onclick="$.InventoryManagement.saveDelivery(${data.list.receiveId?default('')})">提交</a>
                </td>
            </tr>
        </#if>
        </tbody>
    </table>
</form>
