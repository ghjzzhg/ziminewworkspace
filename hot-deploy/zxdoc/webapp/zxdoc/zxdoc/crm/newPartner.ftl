<link type="text/css" rel="stylesheet" href="/images/lib/typeahead/typeahead.css"/>
<link href="/images/lib/validationEngine/validationEngine.jquery.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="/images/lib/typeahead/handlebars.min.js" type="text/javascript"></script>
<script src="/images/lib/typeahead/typeahead.bundle.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script type="text/javascript" src="/images/lib/validationEngine/jquery.validationEngine.js?t=20171010"></script>
<script src="${request.contextPath}/static/crm.js?t=20170123" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        var numbers = new Bloodhound({
            datumTokenizer: function (d) {
                return Bloodhound.tokenizers.whitespace(d.num);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            local: [
                {num: '财务'},
                {num: '法律'},
                {num: '投资'}
            ]
        });

        // initialize the bloodhound suggestion engine
        numbers.initialize();

        $('#questionType').tagsinput({
            typeaheadjs: {
                name: 'questionType',
                displayKey: 'num',
                valueKey: 'num',
                source: numbers.ttAdapter()
            }
        });

        $("#distpicker").distpicker({valueType: "code"});

        $("#partnerForm").validationEngine("attach", {promptPosition: "bottomLeft"});
    })
</script>
<div class="portlet light">
<#--<div class="portlet-title">
    <div class="caption font-red-sunglo">
        <i class="icon-settings font-red-sunglo"></i>
        <span class="caption-subject bold uppercase"> 新客户</span>
    </div>
</div>-->
    <div class="portlet-body row">
        <div class="col-xs-2"></div>
        <div class="col-xs-12">
        <#if !customer?has_content>
            <#assign customer = {}>
        </#if>
            <form id="partnerForm" action="addPartner" method="post">
                <input type="hidden" name="id" value="${customer.id?default('')}">
                <input type="hidden" name="partyId" value="${customer.partyId?default('')}">
                <input type="hidden" name="partyRelationshipTypeId" value="${partyRelationshipTypeId?default('')}">
                <table class="table table-hover table-striped table-bordered">
                    <tbody>
                    <tr>
                        <td style="min-width: 100px"><label class="control-label">客户名称<span class="required"> * </span></label></td>
                        <td>
                        <#if partyRelationshipTypeId == "ORG_LINK_CONTACT">
                            <span>${customer.customerName?default('')}</span>
                            <input type="hidden"
                        <#else>
                            <input type="text
                             <input type="text" class="form-control validate[required,custom[noSpecial]]"
                        </#if>
                            name="customerName"
                            value="${customer.customerName?default('')}" maxlength="50" ></input>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">省、市、区<span class="required"> * </span></label></td>
                        <td>
                            <span id="s2id_area"></span>
                            <div class="jqv"><!-- container -->
                            <#if partyRelationshipTypeId == "ORG_LINK_CONTACT">
                                <span>${site?default('')}</span>
                                <input type="hidden" name="area" value="${postalCode?default('')}"/>
                            <#else>
                                <div id="distpicker" class="distpicker" <#if postalCode?has_content> data-province="${postalCode[0..*2]}0000"  data-city="${postalCode[0..*4]}00" data-district="${postalCode}"</#if>>
                                    <select id="province" class="form-control"></select>
                                    <select id="city" class="form-control"></select>
                                    <select name="area" class="form-control validate[required]"></select>
                                </div>
                            </#if>
                            </div>

                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">地址<span class="required"> * </span></label></td>
                        <td>
                        <#if partyRelationshipTypeId == "ORG_LINK_CONTACT">
                            <span>${customer.address1?default('')}</span>
                            <input type="hidden"
                        <#else>
                                <input type="text" class="form-control validate[required,custom[noSpecial]]"
                        </#if> name="address"
                                       value="${customer.address1?default('')}"></input>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">企业网站</label></td>
                        <td>
                        <#if partyRelationshipTypeId == "ORG_LINK_CONTACT">
                            <span>${customer.infoString?default('')}</span>
                            <input type="hidden"
                        <#else>
                                <input type="text" class="form-control validate[custom[url]]"
                        </#if>
                                       name="webAddress"
                                       value="${customer.infoString?default('')}"></input>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">客户等级</label></td>
                        <td>
                            <select class="form-control" name="customerLevel">
                                <option value="">-请选择-</option>
                            <#if List2?has_content>
                                <#list List2 as list>
                                    <option value="${list.enumId?default('')}"
                                            <if  list.enumId==customer.customerLevel>selected="selected"</if>>${list.description}</option>
                                </#list>
                            </#if>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">客户状态</label></td>
                        <td>
                            <select class="form-control" name="customerStatus">
                                <option value="">-请选择-</option>
                            <#if List3?has_content>
                                <#list List3 as list2>
                                    <option value="${list2.enumId?default('')}"
                                            <if list2.enumId==customer.customerStatus>selected="selected"</if>>${list2.description}</option>
                                </#list>
                            </#if>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">备注</label></td>
                        <td>
                                <textarea class="form-control validate[custom[noSpecial,maxSize[1000]]" rows="3"
                                          name="remarks" placeholder="备注信息不超过1000个字符" maxlength="1000"><#if customer?has_content>${customer.remarks?default('')}</#if></textarea>
                        </td>
                    </tr>
                    <#if personInfo?has_content && personInfo?size &gt; 0>
                        <#list personInfo as num>
                            <#--<#if num.firstName?has_content || num.contactNumber?has_content>-->
                            <tr>
                                <td><label class="control-label">联系人</label></td>
                                <td>
                                    <#if partyRelationshipTypeId == "ORG_LINK_CONTACT">
                                        <span>${num.firstName?default('')}</span>
                                        <input type="hidden"
                                    <#else>
                                            <input type="text" class="form-control validate[custom[noSpecial]]"
                                    </#if> name="contactPerson"
                                                   value="${num.firstName?default('')}"></input>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="control-label">联系电话</label></td>
                                <td>
                                    <#if partyRelationshipTypeId == "ORG_LINK_CONTACT">
                                        <span>${num.contactNumber?default('')}</span>
                                        <input type="hidden"
                                    <#else>
                                            <input type="text" class="form-control validate[custom[isMobile]]"
                                    </#if> name="contactNumber"
                                                   value="${num.contactNumber?default('')}"></input>
                                </td>
                            </tr>
                            <#--</#if>-->
                        </#list>
                    <#else>
                    <tr>
                        <td><label class="control-label">联系人</label></td>
                        <td>
                            <input type="text" class="form-control validate[custom[noSpecial]]" name="contactPerson" maxlength=20/>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">联系电话</label></td>
                        <td>
                            <input type="text" class="form-control validate[custom[isMobile]]" name="contactNumber"/>
                        </td>
                    </tr>
                    </#if>
                    </tbody>
                </table>
                <div class="form-group col-md-12" style="text-align: center">
                    <div class="margiv-top-10">
                    <#--<button type="button" class="btn green" href="javascript:$.partner.<#if customer.partyId?has_content>save<#else>add</#if>Partner();">保存</button>-->
                        <a href="javascript:$.partner.<#if customer?has_content>savePartner()<#else>addPartner('${partnersFlag?default('')}')</#if>;"
                           class="btn green"> 保存 </a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>