<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script src="${request.contextPath}/static/account.js?t=20170909" type="text/javascript"></script>
<link href="/images/lib/select2/select2.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2-bootstrap.min.css" rel="stylesheet" type="text/css"/>
<script src="/images/lib/select2/select2.js" type="text/javascript"></script>
<script src="/images/lib/select2/select2_extended_ajax_adapter.js" type="text/javascript"></script>
<script src="/images/lib/select2/i18n/zh-CN.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        $("#accountForm").validationEngine("attach", {promptPosition: "topRight"});
        $("#distpicker").distpicker({valueType: "code"});
        initGroupSelect();
    })

    function initGroupSelect() {
        $(".provider-select").select2({
            allowClear: true,
            placeholder: "-- 请选择 --",
            dataAdapter: $.fn.select2.amd.require('select2/data/extended-ajax'),
        <#--<#if defaultProviders?has_content>
            defaultResults: [
                <#list defaultProviders as provider>
                    <#if provider?index != 0>
                        ,
                    </#if>
                    {id: "${provider.partyId}", text: "${provider.groupName}"}
                </#list>
            ],
        </#if>-->
            ajax: {
                url: "SelectAllGroupJson",
                dataType: 'json',
                delay: 500,
                data: function (params) {
                    return {
                        q: params.term, // search term
                        type: $(this.context).attr("roleTypeId"),
                        page: params.page
                    };
                },
                processResults: function (data, params) {
                    params.page = params.page || 1;
                    return {
                        results: data.data?data.data : data,
                        pagination: {
                            more: (params.page * 10) < data.totalCount
                        }
                    };
                },
                cache: true
            },
            escapeMarkup: function (markup) {
                return markup;
            }, // let our custom formatter work
            minimumInputLength: 2
        });
    }

    //保存新建的子账户
    function saveCompanySubInfo() {
        var options = {
            beforeSubmit: function () {
                return $("#accountForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功");
                closeCurrentTab();
            },
            url: "saveCompanySubInfo",
            type: 'post'
        };
        $("#accountForm").ajaxSubmit(options);
    }

    //编辑子账户
    function editCompanySubInfo() {
        $("#pass").remove();
        $("#pass1").remove();
        $("#username").remove();
        var options = {
            beforeSubmit: function () {
                return $("#accountForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功");
                closeCurrentTab();
            },
            url: "editCompanySubInfo",
            type: 'post'
        };
        $("#accountForm").ajaxSubmit(options);
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
</style>
<div class="portlet light">
    <div class="portlet-body">
        <form id="accountForm" name="accountForm">
            <div class="form-group col-xs-12">
                <label class="control-label">机构名<span class="required"> * </span></label>
                <#if groupName?? && partyIdFrom??>
                <input type="text" class="form-control" disabled value="${groupName}">
                <#else >
                <select id="group-select" class="provider-select validate[required]" name="groupName" style="width: 100%" >
                    <option value="">-请选择-</option>
                </#if>
                </select>
            </div>
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
            <div class="form-group col-xs-6">
                <label class="control-label">省、市、区<span class="required"> * </span></label>
                <div style="position:relative;"><!-- container -->
                    <div id="distpicker" class="distpicker" <#if subAccountInfo?? && subAccountInfo.area?has_content> data-province="${subAccountInfo.area[0..*2]}0000"  data-city="${subAccountInfo.area[0..*4]}00" data-district="${subAccountInfo.area}"</#if>>
                        <select id="province" class="form-control"></select>
                        <select id="city" class="form-control"></select>
                        <select name="area" class="form-control validate[required]"></select>
                    </div>
                </div>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">地址</label>
                <input type="text" class="form-control" name="address" value="<#if subAccountInfo??>${subAccountInfo.address1?default('')}</#if>"></input>
            </div>
            <div class="form-group col-xs-6" style="display: <#if subAccountInfo??>none</#if>" id="username">
                <label class="control-label">登录账户<span class="required"> * </span></label>
                <input type="text" class="form-control validate[required,custom[onlyLetterNumberChinese]]"  maxlength="20" name="username" value="<#if subAccountInfo??>${subAccountInfo.userLoginId?default('')} </#if>"></input>
            </div>
        <#if subAccountInfo??>
        <#else >
            <div class="form-group col-xs-6" id="pass">
                <label class="control-label">初始密码<span class="required"> * </span></label>
                <input type="password" class="form-control validate[required,custom[passworklimit]]" name="password" id="password"></input>
            </div>
            <div class="form-group col-xs-6" id="pass1">
                <label class="control-label">密码确认<span class="required"> * </span></label>
                <input type="password" class="form-control validate[required, equals[password],custom[passworklimit]]" name="passwordConfirm"></input>
            </div>
        </#if>
            <div class="form-group col-xs-6">
                <label class="control-label">Email<span class="required"> * </span></label>
            <#if subAccountInfo??>
                <input type="hidden" name="emailId" value="${emailId?default('')}"/>
            </#if>
                <input type="text" class="form-control validate[required,custom[email]]" onchange="checkEmail(this)" name="email" <#if subAccountInfo??>value="${email?default('')}"</#if>/>
                <label id="emailMsg" style="display: none; color: red">您使用的是qq邮箱注册，信息会被拦截在垃圾邮箱中，请注意查看</label>
            </div>
            <div class="form-group col-xs-12" style="text-align: center">
                <div class="margiv-top-10">
                <#if subAccountInfo??>
                    <input type="hidden" name="partyId" value="${subAccountInfo.partyId?default('')}">
                </#if>
                <#if subAccountInfo??>
                    <input type="hidden" name="postalId" value="${subAccountInfo.postalId?default('')}"/>
                </#if>
                <#if subAccountInfo??>
                    <a href="javascript:editCompanySubInfo();" class="btn green"> 保存 </a>
                <#else>
                    <a href="javascript:saveCompanySubInfo();" class="btn green"> 保存 </a>
                </#if>
                </div>
            </div>
        </form>
    </div>
</div>