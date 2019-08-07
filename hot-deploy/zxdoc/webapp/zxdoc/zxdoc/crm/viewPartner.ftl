<div class="portlet light">
    <div class="portlet-body row">
        <div class="col-xs-6"></div>
        <div class="col-xs-12">
        <#if !customer?has_content>
            <#assign customer = {}>
        </#if>
            <form id="partnerForm" action="addPartner" method="post">
                <input type="hidden" name="partyId" value="${customer.partyId?default('')}">
                <table class="table table-hover table-striped table-bordered">
                    <tbody>
                    <tr style="width: 500px;">
                        <td class="col-xs-2"><label class="control-label">客户名称</label></td>
                        <td>
                            <label>
                                <div style="width:600px; word-wrap: break-word;">
                                ${customer.customerName?default('')}
                            </label>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">省、市、区</label></td>
                        <td>
                            <label>${site?default('')}</label>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">地址</label></td>
                        <td>
                            <label>
                                <div style="width:600px;word-wrap: break-word;">
                                ${customer.address1?default('')}
                                </div>
                            </label>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">企业网站</label></td>
                        <td>
                            <label>
                                <div style="width:600px;word-wrap: break-word;">
                                ${customer.infoString?default('')}
                                </div>
                            </label>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">客户等级</label></td>
                        <td>
                            <label>${customer.descriptionStatus?default('')}</label>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">客户状态</label></td>
                        <td>
                            <label>${customer.descriptionLevel?default('')}</label>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">备注</label></td>
                        <td>
                            <div style="width:600px;word-wrap: break-word;">
                            ${customer.remarks?default('')}
                            </div>
                        </td>
                    </tr>
                    <tr style="width: 500px;">
                        <td style="width: 50px;"><label class="control-label">联系人</label></td>
                        <td>
                        <#if personInfo?has_content>
                            <#list personInfo as num>
                                <label>${num.firstName?default('')}</label>
                            </#list>
                        </#if>
                        </td>
                    </tr>
                    <#if personInfo?has_content>
                        <#list personInfo as num>
                            <#if num.contactNumber??>
                            <tr style="width: 500px;">
                                <td style="width: 50px;"><label class="control-label">联系电话</label></td>
                                <td>
                                    <label>${num.contactNumber?default('')}</label>
                                </td>
                            </tr>
                            </#if>
                        </#list>
                    </#if>
                    </tbody>
                </table>
                <div class="form-group col-md-12" style="text-align: center">
                    <div class="margiv-top-10">
                        <a href="javascript:closeCurrentTab(this)" class="btn green"> 关闭 </a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>