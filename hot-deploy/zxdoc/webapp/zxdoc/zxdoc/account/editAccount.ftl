<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="${request.contextPath}/static/account.js?t=20170909" type="text/javascript"></script>
<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<script type="text/javascript">
    $(function () {
        $("#accountForm").validationEngine("attach", {promptPosition: "bottomLeft"});
        $("#distpicker").distpicker({valueType: "code"});
    })

    function checkboxOnclick(checkbox) {
        if (checkbox.checked == true) {
            $("[name='creditCode'").val("");
            $("[name='range'").val("");
            $("#signone").html("身份证号");
            $("#signtwo").html("职业资格证号");
            $("[name='creditCode'").attr("class","form-control placeholder-no-fix validate[required,custom[isIdCardNo]]");
        } else {
            $("[name='creditCode'").val("");
            $("[name='range'").val("");
            $("#signone").html("征信代码");
            $("#signtwo").html("经营范围");
            $("[name='creditCode'").attr("class","form-control placeholder-no-fix validate[required]");
        }
    }

    function checkEmail(v) {
        var email = $(v).val();
        if(email.indexOf("@qq.com") >= 0){
            $("#emailMsg").show();
        }else{
            $("#emailMsg").hide();
        }
    }
</script>
<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }
    .distpicker .form-control{
        max-width: none;
    }
</style>
<div class="portlet light">
    <div class="portlet-body row">
        <form id="accountForm" name="accountForm">
            <#if subAccountInfo?has_content && subAccountInfo.partnerCategory?has_content>
                <input type="hidden" name="groupName" value="${subAccountInfo.groupName?default('')}"></input>
            <div class="form-group col-xs-6">
                <label class="control-label">合伙机构名称</label>
                <div>${subAccountInfo.groupName?default('')}</div>
            </div>
                <#if subAccountInfo.partnerCategory == "G">
                    <div class="form-group col-xs-6">
                        <label class="control-label">合伙人名称<span class="required"> * </span></label>
                        <input type="text" class="form-control validate[required]" name="partnerGroupName" maxlength="50"
                               value="${subAccountInfo.partnerGroupName?default('')}"></input>
                    </div>
                </#if>
            <#else>
                <div class="form-group col-xs-6">
                    <label class="control-label">机构名称<span class="required"> * </span></label>
                    <input type="text" class="form-control validate[required]" name="groupName" maxlength="50"
                           <#if subAccountInfo??>value="${subAccountInfo.groupName?default('')}"</#if>></input>
                </div>
            </#if>
            <div class="form-group col-xs-6">
                <label class="control-label">联系人<span class="required"> * </span></label>
                <input type="text" class="form-control validate[required]" name="contactName" maxlength="20"
                       value="<#if subAccountInfo??>${subAccountInfo.fullName?default('')}</#if>"></input>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">联系电话<span class="required"> * </span></label>
            <#if subAccountInfo??>
                <input type="hidden" name="telId" value="${subAccountInfo.telId?default('')}"/>
            </#if>
                <input type="text" class="form-control validate[required,custom[isMobile]]" name="contactNo"
                       value="<#if subAccountInfo??>${subAccountInfo.contactNumber?default('')}</#if>"></input>
            </div>
            <div class="form-group col-xs-12">
                <label class="control-label">省、市、区</label>
                <div style="position:relative;"><!-- container -->

                    <div id="distpicker" class="distpicker" <#if subAccountInfo?? && area?has_content> data-province="${area[0..*2]}0000"  data-city="${area[0..*4]}00" data-district="${area}"</#if>>
                        <select id="province" class="form-control"></select>
                        <select id="city" class="form-control"></select>
                        <select id="district" name="area" class="form-control validate[condRequired[province,city]]"></select>
                    </div>
                </div>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">地址</label>
                <input type="text" class="form-control" name="address"
                       value="<#if subAccountInfo??>${address1?default('')}</#if>"></input>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">Email<span class="required"> * </span></label>
            <#if subAccountInfo??>
                <input type="hidden" name="emailId" value="${emailId?default('')}"/>
            </#if>
                <input type="text" onchange="checkEmail(this)" class="form-control validate[required,custom[email]]" name="email"
                       value="${email?default('')}"/>
                <label id="emailMsg" style="display: none; color: red">您使用的是qq邮箱注册，信息会被拦截在垃圾邮箱中，请注意查看</label>
            </div>
            <div class="form-group col-xs-12" style="text-align: center">
                <div class="margiv-top-10">
                <#if subAccountInfo??>
                    <input type="hidden" name="partyId" value="${subAccountInfo.partyId?default('')}">
                    <input type="hidden" name="postalId" value="${subAccountInfo.postalId?default('')}"/>
                    <a href="javascript:$.account.updateGroupAccount();" class="btn green"> 保存 </a>
                </#if>
                </div>
            </div>
        </form>
    </div>
</div>
<#if !subAccountInfo??>
<div class="note note-info">
        <pre>
        温馨提示
        <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
        1.<span style="color: red">上传文件的大小请不要超过${fileSize?default("50")}兆</span>
        2.<span style="color: red">文件名称不要超过50个字符。</span>
        3.证件格式目前为"*.jpeg,*.png,*.jpg,*.gif,*./pdf"
        </pre>
</div>
</#if>
