<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css" />
<link href="/images/jquery/autocomplete/jquery-ui.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script type="text/javascript" src="/images/lib/bootstrap-switch/js/bootstrap-switch.min.js"></script>
<script src="${request.contextPath}/static/account.js?t=20170909" type="text/javascript"></script>
<script type="text/javascript" src="/images/jquery/autocomplete/jquery-ui.min.js"></script>
<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>
<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<script type="text/javascript">
    function changeRoleType(data) {
        $("#groupName").val("");
        var selectValue = $(data).val();
        if(selectValue == "CASE_ROLE_OWNER"){
            $("#partnerButton").css("visibility", "hidden");
        }else{
            $("#partnerButton").css("visibility", "visible");
        }
        $("#isPartner").val("isNotPartner");
        $(".make-switch").bootstrapSwitch("state", false)
        $("[name='regNumber']").attr("placeholder", "征信代码");
        $("[name='regNumber']").attr("maxlength", "20");
        $("#sign11").attr("title", "请正确填写征信代码！");
        $("[name='regNumber'").attr("class","form-control placeholder-no-fix validate[required]");
        $("[name='infoString']").attr("placeholder", "经营范围");
        $("[name='infoString']").attr("maxlength", "20");
        $("#sign12").attr("title", "请填写经营范围");

        destroySearchGroup();
        updateGroupLabels(selectValue);
    }

    function updateGroupLabels(selectValue){
        if (selectValue == "CASE_ROLE_ACCOUNTING") {
            $("#groupLabel").html("会所名称");
            $("[name='groupName']").attr("placeholder", "会所名称");
            $("#sign1").attr("title", "请填写正确的会所名称！");
            $("[name='username']").attr("placeholder", "会所简称");
            $("#sign7").attr("title", "请填写一个会所简称以便登录");
        } else if (selectValue == "CASE_ROLE_INVESTOR") {
            $("#groupLabel").html("其他机构名称");
            $("[name='groupName']").attr("placeholder", "其他机构名称");
            $("#sign1").attr("title", "请填写正确的其他机构名称！");
            $("[name='username']").attr("placeholder", "其他机构简称");
            $("#sign7").attr("title", "请填写一个其他机构简称以便登录");
        } else if (selectValue == "CASE_ROLE_LAW") {
            $("#groupLabel").html("律所名称");
            $("[name='groupName']").attr("placeholder", "律所名称");
            $("#sign1").attr("title", "请填写正确的律所名称！");
            $("[name='username']").attr("placeholder", "律所简称");
            $("#sign7").attr("title", "请填写一个律所简称以便登录");
        } else if (selectValue == "CASE_ROLE_OWNER") {
            $("#groupLabel").html("企业名称");
            $("[name='groupName']").attr("placeholder", "企业名称");
            $("#sign1").attr("title", "请填写正确的企业名称！");
            $("[name='username']").attr("placeholder", "企业简称");
            $("#sign7").attr("title", "请填写一个企业简称以便登录");
        } else if (selectValue == "CASE_ROLE_STOCK") {
            $("#groupLabel").html("劵商名称");
            $("[name='groupName']").attr("placeholder", "劵商名称");
            $("#sign1").attr("title", "请填写正确的劵商名称！");
            $("[name='username']").attr("placeholder", "劵商简称");
            $("#sign7").attr("title", "请填写一个劵商简称以便登录");
        }
    }


    function changeIsPartner(event, state) {
        if(state)
        {
            $("#partnerGroup").show();
            $("#partnerTypeCtrl").css("visibility", "visible");
            $("#isPartner").val("Y");
            var groupNameInput = $("[name='groupName']");
            var placeHolderName = "合伙机构名称";
            groupNameInput.attr("placeholder", placeHolderName);
            $("#groupLabel").html(placeHolderName);
            searchGroup();
        }
        else
        {
            $('.partnerType-switch').bootstrapSwitch("state",false);
            $("#partnerGroup").hide();
            $("#partnerTypeCtrl").css("visibility", "hidden");
            $("[name='partnerGroupName'").val("");
            $("#isPartner").val("N");
            $("#groupLabel").html(placeHolderName);

            destroySearchGroup();
            updateGroupLabels($("#groupType").val());
        }
    }

    function changePartnerType(event, state) {
        if(state)
        {
            $("#partnerType").val("P");
            $("[name='regNumber']").attr("placeholder", "身份证号");
            $("#regNumberLabel").html("身份证号");
            $("#sign11").attr("title", "请正确填写您的身份证号！");
            $("[name='regNumber'").attr("class","form-control placeholder-no-fix validate[required,custom[isIdCardNo]]");
            $("[name='infoString']").attr("placeholder", "职业资格证号");
            $("#infoStringLabel").html("职业资格证号");
            $("#sign12").attr("title", "请填写职业资格证号");
            $("#partnerLogin").show();
        }
        else
        {
            $("#partnerType").val("G");
            $("[name='regNumber']").attr("placeholder", "征信代码");
            $("#regNumberLabel").html("征信代码");
            $("[name='regNumber']").attr("maxlength", "20");
            $("#sign11").attr("title", "请正确填写征信代码！");
            $("[name='regNumber'").attr("class","form-control placeholder-no-fix validate[required]");
            $("[name='infoString']").attr("placeholder", "经营范围");
            $("#infoStringLabel").html("经营范围");
            $("[name='infoString']").attr("maxlength", "20");
            $("#sign12").attr("title", "请填写经营范围");
            $("#partnerLogin").hide();
        }
    }

    $(function(){
        $("#regPhoto").attachment({
            showLink: true,
            wrapperClass: 'btn btn-success'
        });
        $('.make-switch').bootstrapSwitch({
            onText: "是",
            offText: "否",
            state: false,
            onColor: "info",
            offColor: "danger",
            onSwitchChange: changeIsPartner
        });
        $('.partnerType-switch').bootstrapSwitch({
            onText: "个人",
            offText: "机构",
            state: false,
            onColor: "info",
            offColor: "danger",
            onSwitchChange: changePartnerType
        });
        $("#accountForm").validationEngine("attach", {promptPosition: "bottomLeft"});
        $("#distpicker").distpicker({valueType: "code"});
    })


    function destroySearchGroup(){
        if($("#groupName").autocomplete("instance")){
            $("#groupName").autocomplete("destroy");
        }
    }
    function searchGroup(){
        $( "#groupName" ).autocomplete({
            source: function( request, response ) {
                $.ajax( {
                    'url':'/zxdoc/control/searchGroupNameJsonp', //服务器的地址
                    type:'POST',
                    data:{
                        term: request.term, 'groupTips': $('#groupName').val(),"groupType":$("#groupType").val()
                    },
                    dataType: "json",
                    success: function( data ) {
                        response( data.data );
                    }
                });
            },
            minLength: 1,
            select: function( event, ui ) {
//                    log( "Selected: " + ui.item.value + " aka " + ui.item.id );
            }
        });
    };
</script>
<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }

    #partnerButton .bootstrap-switch{
        top: -2px;
    }

    #partnerButton .bootstrap-switch span{
        height: 32px;
    }
    .fileinput-wrapper, .fileinput-wrapper > div{
        margin-top:0;
    }
</style>
<div class="portlet light">
    <div class="portlet-body row">
        <form id="accountForm" name="accountForm">

            <div class="form-group col-xs-6">
                <label class="control-label">单位类型<span class="required"> * </span></label>
                <#assign enumerations = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), [], false)>
                <select class="form-control select validate[required]" name="groupType" id="groupType"
                        onchange="changeRoleType(this);">
                <#list enumerations as category>
                    <option value="${category.roleTypeId}"
                            <#if category.roleTypeId == 'CASE_ROLE_OWNER'>selected</#if>>${category.description}</option>
                </#list>
                </select>
            </div>
            <div id="partnerButton" class="form-group col-xs-6" style="visibility: hidden;display:inline-block;">
                <label class="control-label">合伙人</label>
                <div>
                <input type="checkbox" class="make-switch" data-on="success" data-on-color="success" data-off-color="warning" data-size="small">
                <input type="hidden" id="isPartner" name="isPartner" value="isNotPartner"/>
                <span id="partnerTypeCtrl" style="visibility: hidden;display: inline-block;">
                        <input type="checkbox" class="partnerType-switch" data-on="success" data-on-color="success" data-off-color="warning" data-size="small">
                        <input type="hidden" name="partnerType" id="partnerType" value="G"/>
                    </span>
                </div>
            </div>
            <div class="form-group col-xs-6" style="display:none" id="partnerGroup">
                <label class="control-label">合伙人名称</label>
                <input class="form-control placeholder-no-fix" type="text" placeholder="合伙人名称(不填则与合伙机构同名)"
                       name="partnerGroupName" maxlength="50"/>
            </div>
            <div class="form-group col-xs-6" id="group">
                <label class="control-label" id="groupLabel">企业名称</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required]" type="text" placeholder="企业名称"
                       id="groupName"
                       name="groupName" maxlength="50"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">联系人</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required]" type="text" placeholder="联系人"
                       name="contactName" maxlength="20"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">联系电话</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required,custom[isMobile]]" id="phoneNum" type="text" placeholder="联系电话"
                       name="contactNo"/>
            </div>
            <div class="form-group col-xs-6">
                <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
                <label class="control-label">电子邮箱</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required,custom[email]]" type="text"
                placeholder="Email" name="email"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">省、市、区</label>
                <div style="position:relative;"><!-- container -->
                    <div id="distpicker" class="distpicker" data-province="310000" data-city="310100" data-district="310115">
                        <select id="province" class="form-control"></select>
                        <select id="city" class="form-control"></select>
                        <select name="area" class="form-control validate[condRequired[province,city]]"></select>
                    </div>
                </div>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">详细地址</label>
                <input class="form-control placeholder-no-fix" type="text" placeholder="详细地址" name="address"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">登录账户</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required,custom[onlyLetterNumberChinese]]" type="text" 
                       placeholder="登录账户" id="registerUserName" name="username" maxlength="20"/>
            </div>
            <div class="form-group col-xs-6" id="partnerLogin" style="display: none">
                <label class="control-label">个人登录账号</label><span class="required"> * </span>
                <div class="input-icon">
                    <i class="fa fa-user"></i>
                    <input class="form-control placeholder-no-fix validate[custom[onlyLetterNumberChinese]]" type="text" 
                           placeholder="个人登录账号" name="partnerUsername" maxlength="20"/></div>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">密码</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required,custom[passworklimit]]" type="password"
                        id="register_password" placeholder="密码" name="password"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">密码确认</label><span class="required"> * </span>
                <input class="form-control placeholder-no-fix validate[required,equals[register_password],custom[passworklimit]]"
                       type="password"  placeholder="密码确认" name="rpassword"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label" id="regNumberLabel">征信代码</label><span class="required"> * </span>
                <input type="text"  placeholder="征信代码" name="regNumber"
                       class="form-control placeholder-no-fix validate[required]" maxlength="50"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label" id="infoStringLabel">经营范围</label><span class="required"> * </span>
                <input type="text"  placeholder="经营范围" name="infoString"
                       class="form-control placeholder-no-fix validate[required]" maxlength="120"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">公司网站</label>
                <input type="text"  placeholder="公司网站" name="webAddress"
                       class="form-control placeholder-no-fix validate[custom[url]]"/>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">证件上传</label>
            <#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
            <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
            <#assign fileSize = fileSize?number * 1048576>
                <div>
                    <input type="text" name="regPhoto" id="regPhoto" data-btn-style="float:right" data-ori-url="registerUpload" data-extension="pdf,jpg,jpeg,png,bmp,gif" data-size-limit="${fileSize}" style="width:1px;height:1px;">
                </div>
            </div>
            <div class="form-group col-xs-6">
                <label class="control-label">&nbsp;</label>
                <div class="margiv-top-10">
                    <a href="javascript:$.account.createGroupAccount();" class="btn green"> 保存 </a>
                </div>
            </div>
        </form>
    </div>
</div>

<div class="note note-info">
        <pre>
        温馨提示
        <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
        1.<span style="color: red">上传文件的大小请不要超过${fileSize?default("50")}兆</span>
        2.<span style="color: red">文件名称不要超过50个字符。</span>
        3.证件格式目前为"*.jpeg,*.png,*.jpg,*.gif,*./pdf"
        </pre>
</div>
