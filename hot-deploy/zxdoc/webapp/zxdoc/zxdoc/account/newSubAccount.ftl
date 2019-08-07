<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script src="${request.contextPath}/static/account.js?t=20170909" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        $("#accountForm").validationEngine("attach", {promptPosition: "bottomLeft"});
        $("#distpicker").distpicker({valueType: "code"});
    })
    
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
</style>
<div class="portlet light">
    <div class="portlet-body">
        <form id="accountForm" name="accountForm">
            <div class="form-group col-xs-6">
                <label class="control-label">姓名<span class="required"> * </span></label>
                <input type="text" class="form-control validate[required]" maxlength="20" name="contactName" <#if subAccountInfo??>value="${subAccountInfo.fullName?default('')}"</#if>></input>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">联系电话<span class="required"> * </span></label>
            <#if subAccountInfo??>
                <input type="hidden" name="telId" value="${subAccountInfo.telId?default('')}"/>
            </#if>
                <input type="text" class="form-control validate[required,custom[isMobile]]" name="contactNo" value="<#if subAccountInfo??>${subAccountInfo.contactNumber?default('')}</#if>"></input>
            </div>
            <div class="form-group col-xs-12">
                <label class="control-label">省、市、区<span class="required"> * </span></label>
                <div style="position:relative;"><!-- container -->
                <#if subAccountInfo?? && subAccountInfo.area?has_content>
                    <div id="distpicker" class="distpicker" data-province="${subAccountInfo.area[0..*2]}0000"  data-city="${subAccountInfo.area[0..*4]}00" data-district="${subAccountInfo.area}">
                    <#else>
                    <div id="distpicker" class="distpicker" data-province="310000" data-city="310100" data-district="310115">
                </#if>
                        <select id="province" class="form-control"></select>
                        <select id="city" class="form-control"></select>
                        <select name="area" class="form-control validate[required]"></select>
                    </div>
                </div>
            </div>
            <div class="form-group col-xs-12">
                <label class="control-label">地址</label>
                <input type="text" class="form-control" name="address" value="<#if subAccountInfo??>${subAccountInfo.address1?default('')}</#if>"></input>
            </div>
            <div class="form-group col-xs-12">
                <label class="control-label">Email<span class="required"> * </span></label>
            <#if subAccountInfo??>
                <input type="hidden" name="emailId" value="${subAccountInfo.emailId?default('')}"/>
            </#if>
                <input type="text" class="form-control validate[required,custom[email]]" onchange="checkEmail(this)" name="email" <#if subAccountInfo??>value="${subAccountInfo.infoString?default('')}"</#if>></input>
                <label id="emailMsg" style="display: none; color: red">您使用的是qq邮箱注册，信息会被拦截在垃圾邮箱中，请注意查看</label>
            </div>
        <#if !subAccountInfo?has_content>
            <div class="form-group col-xs-6" id="username">
                <label class="control-label">登录账户<span class="required"> * </span></label>
                <input type="text" class="form-control validate[required,custom[onlyLetterNumberChinese]]"  maxlength="20" name="username" value="<#if subAccountInfo??>${subAccountInfo.userLoginId?default('')} </#if>"></input>
            </div>
        </#if>
            <div class="form-group col-xs-6" id="pass">
                <label class="control-label">初始密码<#if !subAccountInfo?has_content><span class="required"> * </span></#if></label>
                <input type="password" class="form-control validate[<#if !subAccountInfo?has_content>required,</#if> custom[passworklimit]]" name="password" id="password"></input>
            </div>
            <div class="form-group col-xs-6" id="pass1">
                <label class="control-label">密码确认<#if !subAccountInfo?has_content><span class="required"> * </span></#if></label>
                <input type="password" class="form-control validate[<#if !subAccountInfo?has_content>required,custom[passworklimit],</#if> equals[password]]" name="passwordConfirm"></input>
            </div>
        <#if subAccountInfo??>
            <div class="form-group col-xs-12" id="username">
                <label class="control-label">登录账户：</label>${subAccountInfo.userLoginId?default('')}
                <input type="hidden" name="username" value="${subAccountInfo.userLoginId?default('')}"></input>
                <span style="color:red;">(不修改密码时密码留空)</span>
            </div>
        </#if>
            <div class="form-group col-xs-12" style="text-align: center">
                <div class="margiv-top-10">
                <#if subAccountInfo??>
                    <input type="hidden" name="partyId" value="${subAccountInfo.partyId?default('')}">
                </#if>
                <#if subAccountInfo??>
                    <input type="hidden" name="postalId" value="${subAccountInfo.postalId?default('')}"/>
                </#if>
                <#if subAccountInfo??>
                    <a href="javascript:$.account.editSubInfo();" class="btn green"> 保存 </a>
                <#else>
                    <a href="javascript:$.account.saveSubInfo();" class="btn green"> 保存 </a>
                </#if>
                </div>
            </div>
        </form>
    </div>
</div>