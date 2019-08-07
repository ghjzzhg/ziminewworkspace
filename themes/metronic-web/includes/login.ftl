<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "component://common/webcommon/includes/commonMacros.ftl"/>
    <#include "component://common/webcommon/render360.ftl"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<#if layoutSettings.VT_STYLESHEET?has_content>
<#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.VT_STYLESHEET as styleSheet>
        <#if styleSheet?index_of("main") == -1>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>"
                  type="text/css"/>
        </#if>
    </#list>
</#if>

<#if layoutSettings.styleSheets?has_content>
<#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.styleSheets as styleSheet>
        <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>"
              type="text/css"/>
    </#list>
</#if>
    <script src="<@ofbizContentUrl>/images/jquery/jquery-1.11.0.min.js</@ofbizContentUrl>"
            type="text/javascript"></script>
<#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
        <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"
                type="text/javascript"></script>
    </#list>
</#if>
    <link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
    <link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
    <link href="/images/lib/validationEngine/validationEngine.jquery.css" rel="stylesheet" type="text/css"/>
    <link href="/images/jquery/autocomplete/jquery-ui.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
    <script type="text/javascript" src="/images/lib/form/jquery.form.js"></script>
    <script type="text/javascript" src="/images/lib/validationEngine/languages/jquery.validationEngine-zh_CN.js?t=20171012"></script>
    <script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
    <link href="/images/lib/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="/images/lib/validationEngine/jquery.validationEngine.js?t=20171010"></script>
    <script type="text/javascript" src="/images/lib/bootstrap-switch/js/bootstrap-switch.min.js"></script>
        <script type="text/javascript" src="/images/jquery/ui/js/jquery.cookie-1.4.0.js"></script>
        <script type="text/javascript" src="/images/jquery/autocomplete/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/images/lib/common.js?t=20171106"></script>
        <script type="text/javascript" src="/images/lib/amplify/amplify.core.min.js"></script>
        <script type="text/javascript" src="/images/lib/amplify/amplify.store.min.js"></script>
        <script src="/images/lib/attachment.js?t=20171105" type="text/javascript"></script>
    <script type="text/javascript">
        $(function () {
            if (self != top && "${parameters.iframe?default('false')}" === "true") {
                top.window.location.reload();
            }
            $("#distpicker").distpicker({valueType: "code"});
            $("#regPhoto").attachment({
                showLink: true,
                wrapperClass: 'btn btn-success'
            });
        })
    </script>
    <title><#if parameters.frameLogin?has_content>登录系统<#else>注册</#if></title>
    <style>
        body{
            font-family: 'Microsoft YaHei',微软雅黑,Arial,Helvetica,sans-serif,'宋体'!important;
        }
        /* Cubic Bezier Transition */
        /***
        Login page
        ***/
        /* logo page */
        .login {
            background-color: #666 !important;
        }

        .login .logo {
            margin: 60px auto 20px auto;
            padding: 15px;
            text-align: center;
        }

        .login .content {
            background: url(/metronic-web/images/bg-white-lock.png) repeat;
            width: 360px;
            margin: 0 auto;
            margin-bottom: 0px;
            padding: 30px;
            padding-top: 20px;
            padding-bottom: 15px;
            -webkit-border-radius: 7px;
            -moz-border-radius: 7px;
            -ms-border-radius: 7px;
            -o-border-radius: 7px;
            border-radius: 7px;
        }

        .login .content h3 {
            color: #eee;
        }

        .login .content h4 {
            color: #eee;
        }

        .login .content p,
        .login .content label {
            color: #fff;
        }

        .login .content .login-form,
        .login .content .forget-form {
            padding: 0px;
            margin: 0px;
        }

        .login .content .form-control {
            background-color: #fff;
        }
        <#if parameters.forget?has_content>
        .login .content .register-form, .login .content .login-form {
            display: none;
        }
        <#elseif parameters.frameLogin?has_content>
        .login .content .register-form, .login .content .forget-form {
            display: none;
        }
        <#else>
        .login .content .forget-form, .login .content .login-form {
            display: none;
        }
        </#if>

        .login .content .form-title {
            font-weight: 300;
            margin-bottom: 25px;
        }

        .login .content .form-actions {
            background-color: transparent;
            clear: both;
            border: 0px;
            padding: 0px 30px 25px 30px;
            margin-left: -30px;
            margin-right: -30px;
        }

        .login .content .form-actions .checkbox {
            margin-left: 0;
            padding-left: 0;
        }

        .login .content .forget-form .form-actions {
            border: 0;
            margin-bottom: 0;
            padding-bottom: 20px;
        }

        .login .content .register-form .form-actions {
            border: 0;
            margin-bottom: 0;
            padding-bottom: 0px;
        }

        .login .content .form-actions .checkbox {
            margin-top: 8px;
            display: inline-block;
        }

        .login .content .form-actions .btn {
            margin-top: 1px;
        }

        .login .content .forget-password {
            margin-top: 25px;
        }

        .login .content .create-account {
            border-top: 1px dotted #eee;
            padding-top: 10px;
            margin-top: 15px;
        }

        .login .content .create-account a {
            display: inline-block;
            margin-top: 5px;
        }

        /* select2 dropdowns */
        .login .content .select2-container i {
            display: inline-block;
            position: relative;
            color: #ccc;
            z-index: 1;
            top: 1px;
            margin: 4px 4px 0px -1px;
            width: 16px;
            height: 16px;
            font-size: 16px;
            text-align: center;
        }

        .login .content .has-error .select2-container i {
            color: #b94a48;
        }

        .login .content .select2-container a span {
            font-size: 13px;
        }

        .login .content .select2-container a span img {
            margin-left: 4px;
        }

        /* footer copyright */
        .login .copyright {
            text-align: center;
            margin: 0 auto;
            padding: 10px;
            color: #eee;
            font-size: 13px;
        }
        .distpicker .form-control{
            max-width: 100px;
            padding: 2px;
        }

        @media (max-width: 480px) {
            /***
            Login page
            ***/
            .login .logo {
                margin-top: 10px;
            }

            .login .content {
                padding: 30px;
                width: 222px;
            }

            .login .content h3 {
                font-size: 22px;
            }

            .login .checkbox {
                font-size: 13px;
            }
        }

    </style>
    <style type="text/css">
        .code {
            /*background:url(code_bg.jpg);*/
            font-family: Arial;
            font-style: italic;
            color: blue;
            font-size: 20px;
            border: 0;
            padding: 2px 3px;
            letter-spacing: 3px;
            font-weight: bolder;
            float: left;
            cursor: pointer;
            width: 120px;
            height: 50px;
            line-height: 40px;
            text-align: center;
            vertical-align: middle;

        }

        /*a {
            text-decoration: none;
            font-size: 12px;
            color: #288bc4;
        }

        a:hover {
            text-decoration: underline;
        }*/

        .clickable {
            cursor: default;
        }
        .highlight {
            background-color: #9ACCFB;
        }
        .register-form .form-group{
            width: 320px;
            float: left;
            position:relative;
        }
        .help-info{
            position:absolute;
            right: -30px;
            top: 7px;
        }

        #partnerButton .bootstrap-switch{
            top: -2px;
        }

        #partnerButton .bootstrap-switch span{
            height: 32px;
        }

        #partnerTypeCtrl .bootstrap-width {
            width:85px!important;
        }
    </style>
    <link href="/images/lib/awesomefont/css/font-awesome.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="/images/lib/backstretch/jquery.backstretch.min.js"></script>
    <script type="text/javascript" src="/images/lib/select2/select2.min.js"></script>
</head>
<body class="login">
<#if requestAttributes.uiLabelMap??><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>
<#assign demo = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "demo")>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if "Y" == demo>
    <#assign username = 'demooa'/>
    <#assign password = 'demooa'/>
</#if>
<#if username != "">
    <#assign focusName = false>
<#else>
    <#assign focusName = true>
</#if>

<#--<div style="position: relative;height: 100%">
    <div style="width: 450px;height: 220px; position: absolute; left: 50%; top: 30%;margin-left:-650px">
        <form class="form-horizontal" role="form" method="post" runat="server" id="loginform" name="loginform" style="position: absolute; right: -500px; top: -50px;background: url(/metronic/images/loginwenzi.png) no-repeat">
            <div style="top: 85px; position: absolute; right: 40px;width: 250px">
                <input type="text" name="USERNAME" value="${username}" size="20" style="width: 237px"/>
            </div>
            <div style="top: 148px; position: absolute; right: 40px;width: 251px">
                <input type="password" name="PASSWORD" value="${password}" size="20" style="width: 237px"/>
            </div>
            <#if ("Y" == useMultitenant) >
                <#if !requestAttributes.userTenantId??>
                    <tr>
                        <td class="label">${uiLabelMap.CommonTenantId}</td>
                        <td><input type="text" name="userTenantId" value="${parameters.userTenantId!}" size="20"/></td>
                    </tr>
                <#else>
                    <input type="hidden" name="userTenantId" value="${requestAttributes.userTenantId!}"/>
                </#if>
            </#if>
            <div style="top: 212px; position: absolute; right: 97px;width: 200px">
                <button runat="server" id="btsubmit" type="submit" style="width: 100%;background: transparent;border: 0; height:40px;box-shadow: none" class="btn btn-primary" data-loading-text="登录处理中..."></button>
            </div>
        </form>
        <div style="width: 400px;height: 100px; position: absolute; left: -100px; bottom: -150px; background: url(Images/2.png)"></div>
    </div>

</div>-->
<div class="logo">
    <p class="mb-0" style="padding-left: 27px;color:lightseagreen;font-size:30px">带您进入资本的蓝洞</p>
</div>
<div class="content" style="width: 400px;">
    <!-- BEGIN LOGIN FORM -->
    <form class="login-form" method="post" id="loginform" name="loginform">
        <input type="hidden" name="JavaScriptEnabled" value="N"/>
        <h3 style="text-align: center" class="form-title">登录资协网</h3>
        <div class="alert alert-danger display-hide">
            <button class="close" data-close="alert"></button>
            <span> 请填写用户名及密码. </span>
        </div>
        <div class="form-group">
            <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
            <label class="control-label visible-ie8 visible-ie9">用户名</label>
            <div class="input-icon">
                <i class="fa fa-user"></i>
                <input class="form-control placeholder-no-fix validate[required]" type="text" placeholder="用户名"
                       id="loginUserName" name="USERNAME"/></div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密码</label>
            <div class="input-icon">
                <i class="fa fa-lock"></i>
                <input class="form-control placeholder-no-fix validate[required]" type="password" placeholder="密码"
                       id="loginPassword" name="PASSWORD"/></div>
        </div>
        <div class="form-actions">
            <label>
                <input type="checkbox" id="rememberMe" name="remember" value="1" style="width:20px; height:20px;border:1px solid #999999;vertical-align: middle"/> 记住账户信息 </label>
            <button type="button" class="btn green pull-right" onclick="frameLogin()"> 登录</button>
        </div>
        <div class="forget-password">
            <h4>忘记密码 ?</h4>
            <p> 请点击
                <a href="javascript:;" id="forget-password"> 这里 </a> 重置密码. </p>
        </div>
        <div class="create-account">
            <p> 没有账号 ?&nbsp;
                <a href="javascript:;" id="register-btn"> 注册 </a>
            </p>
        </div>
    </form>
    <!-- END LOGIN FORM -->
    <!-- BEGIN FORGOT PASSWORD FORM -->
    <form class="forget-form" id="forget-form" method="post">
        <h3 style="text-align: center">忘记密码 ?</h3>
        <p> 请输入您的登录名以便于核实您的身份. </p>
        <div class="form-group">
            <div class="input-icon">
                <i class="fa fa-envelope"></i>
                <input class="form-control validate[required]" type="text" placeholder="登录名"
                       name="username"/></div>
        </div>
        <p> 请输入您的邮箱地址用于接收新密码. </p>
        <div class="form-group">
            <div class="input-icon">
                <i class="fa fa-envelope"></i>
                <input class="form-control placeholder-no-fix validate[required]" type="text" placeholder="Email"
                       name="email"/></div>
        </div>
        <div class="form-actions">
        <#--<button type="button" id="back-btn" class="btn red btn-outline">返回 </button>-->
            <a class="btn red btn-outline" href="${request.contextPath}/control/<#if parameters.frameLogin?has_content>frameLogin?frameLogin=true<#else>index</#if>">返回</a>
            <button type="button" class="btn green pull-right" onclick="submitForgetPassword()"> 重置</button>
        </div>
    </form>
    <!-- END FORGOT PASSWORD FORM -->
    <!-- BEGIN REGISTRATION FORM -->
    <form class="register-form" method="post" id="register-form">
        <h3 style="text-align: center;margin-bottom: 20px;">注册</h3>
        <div class="form-group">
            <div class="input-icon" style="width: 48%;display:inline-block">
                <p> 注册类型: </p>
                <i class="fa fa-check"></i>
            <#assign enumerations = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), [], false)>
                <select class="form-control select validate[required]" name="groupType" id="groupType"
                        onchange="changeRoleType(this);">
                <#list enumerations as category>
                    <option value="${category.roleTypeId}"
                            <#if category.roleTypeId == 'CASE_ROLE_OWNER'>selected</#if>>${category.description}</option>
                </#list>
                </select>
            </div>
            <div id="partnerButton" style="visibility: hidden;width:50%;display:inline-block;" class="input-icon">
                <p> 您是合伙人吗: </p>
                <input type="checkbox" class="make-switch" checked data-on="success" data-on-color="success" data-off-color="warning" data-size="small">
                <input type="hidden" id="isPartner" name="isPartner" value="isNotPartner"/>
                <div id="partnerTypeCtrl" style="visibility: hidden;display: inline-block;width:85px;">
                    <input type="checkbox" class="partnerType-switch" data-on="success" data-on-color="success" data-off-color="warning" data-size="small">
                    <input type="hidden" name="partnerType" id="partnerType" value="G"/>
                </div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00"
                  title="选择您的机构类型，包含：会所、其他机构、企业、律所、劵商。"></span>
            </div>
        </div>
        <div class="form-group" style="display:none" id="partnerGroup">
            <label class="control-label visible-ie8 visible-ie9">合伙人名称</label>
            <div class="input-icon">
                <i class="fa fa-font"></i>
                <input class="form-control placeholder-no-fix" type="text" placeholder="合伙人名称(不填则与合伙机构同名)"
                       name="partnerGroupName" maxlength="50"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请填写合伙人名称，留空与合伙机构同名"></span>
            </div>
        </div>
        <div class="form-group" id="group">
            <label class="control-label visible-ie8 visible-ie9">企业名称</label>
            <div class="input-icon" id="searchName">
                <i class="fa fa-font"></i>
                <input class="form-control placeholder-no-fix validate[required]" type="text" placeholder="企业名称"
                       id="groupName"
                       name="groupName" maxlength="50"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请填写正确的企业名称！"
                  id="sign1"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">联系人</label>
            <div class="input-icon">
                <i class="fa fa-font"></i>
                <input class="form-control placeholder-no-fix validate[required]" type="text" placeholder="联系人"
                       name="contactName" maxlength="20"/>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00"
                  title="请填写一个有效的联系人，以便我们和您取得联系" id="sign2"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">联系电话</label>
            <div class="input-icon">
                <i class="fa fa-font"></i>
                <input class="form-control placeholder-no-fix validate[required,custom[isMobile]]" id="phoneNum" type="text" placeholder="联系电话"
                       name="contactNo"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请填写一个有效的联系电话"
                  id="sign3"></span>
            </div>
        </div>
        <div class="form-group">
            <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
            <label class="control-label visible-ie8 visible-ie9">电子邮箱</label>
            <div class="input-icon">
                <i class="fa fa-envelope"></i>
                <input class="form-control"  onchange="checkEmail(this)" placeholder-no-fix validate[required,custom[email]]" type="text"
                       placeholder="Email" name="email"/>
               <label id="emailMsg" style="display: none; color: blanchedalmond">QQ邮箱可能会拦截系统邮件，请注意垃圾箱中被误判的邮件。</label>
                       </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00"
                  title="请填写一个有效的邮箱，我们将不定时的推送消息" id="sign4"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">省、市、区</label>
            <div style="position:relative;"><!-- container -->
                <div id="distpicker" class="distpicker" data-province="310000" data-city="310100" data-district="310115">
                    <select id="province" class="form-control"></select>
                    <select id="city" class="form-control"></select>
                    <select name="area" class="form-control validate[required]"></select>
                </div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请选择所在的实际区域"
                  id="sign5"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">详细地址</label>
            <div class="input-icon">
                <i class="fa fa-check"></i>
                <input class="form-control placeholder-no-fix" type="text" placeholder="详细地址" name="address"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00"
                  title="请填写实际地址，以便我们的邮寄或联络" id="sign6"></span>
            </div>
        </div>
        <p style="width: 340px;float: left;"> 请填写您的登录账号信息: </p>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">企业简称</label>
            <div class="input-icon">
                <i class="fa fa-user"></i>
                <input class="form-control placeholder-no-fix validate[required,custom[onlyLetterNumberChinese]]" type="text" 
                       placeholder="企业简称" id="registerUserName" name="username" maxlength="20"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="系统主账号登录后可管理内部子账号"
                  id="sign7"></span>
            </div>
        </div>
        <div class="form-group" id="partnerLogin" style="display: none">
            <label class="control-label visible-ie8 visible-ie9">个人登录账号</label>
            <div class="input-icon">
                <i class="fa fa-user"></i>
                <input class="form-control placeholder-no-fix validate[custom[onlyLetterNumberChinese]]" type="text" 
                       placeholder="个人登录账号" name="partnerUsername" maxlength="20"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="合伙人个人子账户"
                  id="sign7"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密码</label>
            <div class="input-icon">
                <i class="fa fa-lock"></i>
                <input class="form-control placeholder-no-fix validate[required,custom[passworklimit]]" type="password"
                       id="register_password" placeholder="密码" name="password"/></div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00"
                  title="请填写一个有效的密码，密码长度为6-16位，且必须包含大小写字母和数字" id="sign8"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密码确认</label>
            <div class="controls">
                <div class="input-icon">
                    <i class="fa fa-check"></i>
                    <input class="form-control placeholder-no-fix validate[required,equals[register_password],custom[passworklimit]]"
                           type="password" placeholder="密码确认" name="rpassword"/></div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请确认您的密码"
                  id="sign9"></span>
            </div>
        </div>
        <p style="width: 340px;float: left;"> 请填写您的认证信息:
        </p>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">征信代码</label>
            <div class="controls">
                <div class="input-icon">
                    <i class="fa fa-check"></i>
                    <input type="text" placeholder="征信代码" name="regNumber"
                           class="form-control placeholder-no-fix validate[required,custom[isCreditCode]]" maxlength="18"/></div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请填写征信代码"
                  id="sign11"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">经营范围</label>
            <div class="controls">
                <div class="input-icon">
                    <i class="fa fa-check"></i>
                    <input type="text" placeholder="经营范围" name="infoString"
                           class="form-control placeholder-no-fix validate[required]" maxlength="120"/></div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请填写经营范围"
                  id="sign12"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">公司网站</label>
            <div class="controls">
                <div class="input-icon">
                    <i class="fa fa-check"></i>
                    <input type="text" placeholder="公司网站" name="webAddress"
                           class="form-control placeholder-no-fix validate[custom[url]]"/></div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请填写公司网站"
                  id="sign12"></span>
            </div>
        </div>
        <div class="form-group">
            <#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
            <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
            <#assign fileSize = fileSize?number * 1048576>
                <input type="text" name="regPhoto" id="regPhoto" data-btn-title="上传营业执照" data-btn-style="float:right" data-ori-url="registerUpload" data-extension="pdf,jpg,jpeg,png,bmp,gif" data-size-limit="${fileSize}" style="width:1px;height:1px;">
                <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="请上传营业执照，以便核实。支持的上传格式为图片或者pdf，文件大小请不要超过50M且文件名不要超过50个字符。"
                  id="sign12"></span>
                </div>
        </div>
        <div class="form-group">
            <div class="controls">
                <div style="float: left;width: 50%;">
                    <div class="input-icon">
                        <i class="fa fa-font"></i>
                        <input class="form-control placeholder-no-fix validate[required,custom[onlyNumberSp]]" maxlength="4" type="text" placeholder="短信验证码"
                               name="verificationCode" id="verificationCode"/>
                    </div>
                </div>
                <div style="position: absolute;right:0">
                    <a href="javascript:sendVerificationCode();" style="width:130px;float: left" id="seedInfo" class="btn default btn-block"> 发送短信验证码 </a>
                </div>
            </div>
            <div class="help-info">
            <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="验证码有效期为1分钟"></span>
            </div>
        </div>
        <div class="form-group" style="height: 80px;">
            <div class="controls">
                <div style="float: left;width: 50%;">
                    <div class="input-icon">
                        <i class="fa fa-bell"></i>
                        <input style="float:left;" type="text" id="inputCode" name="inputCode"
                               class="form-control placeholder-no-fix validate[required]" onBlur=""
                               type="text"
                               placeholder="图片验证码"/>
                    </div>
                </div>
                <div style="position: absolute;right:0">
                    <img src="/ckfinder/control/getCaptcha?time=' + (new Date()) + '" onclick="checkImg();"
                         id="imageCode" style="cursor:pointer;height: 34px"/>
                </div>
                <div class="help-info">
                    <span class="glyphicon glyphicon-question-sign" style="font-size:18px;color:#9c7d00" title="点击图片刷新验证码"></span>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" name="tnc" class="validate[required]"/> 我同意
                <a href="javascript:openServices();"> 服务条款 </a>
                <#--及-->
                <#--<a href="javascript:openSecrecy() ;"> 保密协议 </a>-->
            </label>
            <div id="register_tnc_error"></div>
        </div>
        <div class="form-actions">
        <#--<button id="register-back-btn" type="button" class="btn red btn-outline"> 返回 </button>-->
            <a class="btn red btn-outline" href="${request.contextPath}/control/<#if parameters.frameLogin?has_content>frameLogin?frameLogin=true<#else>index</#if>">返回</a>
            <button type="button" id="register-submit-btn" class="btn green pull-right" onclick="register();"> 注册
            </button>
        </div>
    </form>
    <!-- END REGISTRATION FORM -->
</div>

<script language="javascript" type="text/javascript">

    $(function () {
        checkImg();//用于解决第一次输入验证码不通过的bug
        $("a[class='close fileinput-exists']").attr("style", "top: 15px;position: relative;")

        if ($.cookie("rmbUser") || $.cookie("rmbPass")) {
            if ($.cookie("rmbUser") == "true") {
                $("#rememberMe").prop("checked", true);
                $("#loginUserName").val($.cookie("username"));
            }
            if ($.cookie("rmbPass") == "true") {
                $("#rememberMe").prop("checked", true);
                $("#loginPassword").val($.cookie("loginPassword"));
            }
        } else {
            if (amplify.store("rmbUser") == "true") {
                $("#rememberMe").prop("checked", true);
                $("#loginUserName").val(amplify.store("username"));
            }
            if (amplify.store("rmbPass") == "true") {
                $("#rememberMe").prop("checked", true);
                $("#loginPassword").val(amplify.store("loginPassword"));
            }
        }
        <#if parameters.frameLogin?has_content>
        $(window).on("keydown", function(event){
            if($("#loginform").is(":visible") && event.keyCode == 13){
                event.preventDefault();
                frameLogin();
                return false;
            }
        });
        </#if>
    })
    function changeRoleType(data) {
        $("#groupName").val("");
        var selectValue = $(data).val();
        if(selectValue == "CASE_ROLE_OWNER"){
            $("#partnerButton").css("visibility", "hidden");
        }else{
            $("#partnerButton").css("visibility", "visible");
        }
        $("#isPartner").val("isNotPartner");
        if($(".make-switch").bootstrapSwitch("state")){
            $(".make-switch").bootstrapSwitch("state", false)
        }
        $("[name='regNumber']").attr("placeholder", "征信代码");
        $("[name='regNumber']").attr("maxlength", "18");
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
    function sendVerificationCode(){
        var flag = $("#phoneNum").validationEngine("validate");
        if(!flag){
            settime();
            $.ajax({
                type: "POST",
                url: "/zxdoc/control/sendVerificationCode",
                data: {phoneNum: $("#phoneNum").val()},
                dataType: "json",
                success: function () {
                    showInfo("发送成功");
                }
            });
        }else{
            showError("请正确填写联系电话");
        }
    }

    var countdown = 60
    function settime() {
        if (countdown == 0) {
            $("#seedInfo").prop("disabled",false);
            $("#seedInfo").prop("href","javascript:sendVerificationCode();");
            $("#seedInfo").html("重新获取验证码");
            countdown = 60;
            return;
        } else {
            $("#seedInfo").prop("disabled",true);
            $("#seedInfo").prop("href","#");
            $("#seedInfo").html("重新发送(" + countdown + ")");
            countdown--;
        }
        setTimeout(function() {settime() } ,1000)
    }

    function checkCodess() {
        if ($("#register-form").validationEngine("validate") && $("#inputCode").val() != null && $("#inputCode").val() != "") {
            $.ajax({
                type: "POST",
                url: "/zxdoc/control/checkCodes",
                async:false,
                data: {code1: $("#inputCode").val(),phoneNum: $("#phoneNum").val(),verificationCode:$("#verificationCode").val()},
                dataType: "json",
                success: function (data) {
                    if (data.result != null && data.result != "") {
                        showInfo(data.result);
                        checkImg();
                    } else {
                        register();
                    }
                }
            });
        } else {

        }
    }

    function checkImg() {
        $("#imageCode").attr("src", "getCaptcha?time=" + (new Date()));
    }

    function openServices() {
        displayInLayer('服务条款', {requestUrl: '/zxdoc/control/showServices', height: "60%", width: "50%"});
    }

    function openSecrecy() {
        displayInLayer('保密协议', {requestUrl: '/zxdoc/control/showSecrecy', height: "60%", width: "50%"});
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
            groupNameInput.closest(".form-group").find("label").html(placeHolderName);
            searchGroup();
        }
        else
        {
            $("#partnerTypeCtrl").css("visibility", "hidden");
            $('.partnerType-switch').bootstrapSwitch("state",false);
            $("#partnerGroup").hide();
            $("[name='partnerGroupName'").val("");
            $("#isPartner").val("N");
            var groupNameInput = $("[name='groupName']");
            var placeHoderName = groupNameInput.attr("placeholder").replace("合伙", "");
            groupNameInput.attr("placeholder", placeHoderName);
            groupNameInput.closest(".form-group").find("label").html(placeHolderName);
            destroySearchGroup();
            updateGroupLabels($("#groupType").val());
        }
    }

    function changePartnerType(event, state) {
    	console.log("state:"+state);
        if(state)
        {
            $("#partnerType").val("P");
            $("[name='regNumber']").attr("placeholder", "身份证号");
            $("#sign11").attr("title", "请正确填写您的身份证号！");
            $("[name='regNumber'").attr("class","form-control placeholder-no-fix validate[required,custom[isIdCardNo]]");
            $("[name='infoString']").attr("placeholder", "职业资格证号");
            $("#sign12").attr("title", "请填写职业资格证号");
            $("#partnerLogin").show();
        }
        else
        {
            $("#partnerType").val("G");
            $("[name='regNumber']").attr("placeholder", "征信代码");
            $("[name='regNumber']").attr("maxlength", "18");
            $("#sign11").attr("title", "请正确填写征信代码！");
            $("[name='regNumber'").attr("class","form-control placeholder-no-fix validate[required,custom[isCreditCode]]");
            $("[name='infoString']").attr("placeholder", "经营范围");
            $("[name='infoString']").attr("maxlength", "20");
            $("#sign12").attr("title", "请填写经营范围");
            $("#partnerLogin").hide();
        }
    }


    //删除cookie中的值
    function delAllCookie(){
        var myDate=new Date();
        myDate.setTime(-1000);//设置时间
        var data=document.cookie;
        var dataArray=data.split("; ");
        for(var i=0;i<dataArray.length;i++){
            var varName=dataArray[i].split("=");
            //不需要清空提示信息的cookie,且
            if(varName[0]!="showMsg") {
                document.cookie = varName[0] + "=''; expires=" + myDate.toGMTString();
            }
        }

    }

    function saveRemember() {
        var loginUserName = $("#loginUserName").val();
        var loginPassword = $("#loginPassword").val();
        if(loginUserName == null || loginUserName.trim() == "" || loginPassword.trim() == "" || loginPassword.trim() == ""){
            alert("请填写用户名密码！");
            return false;
        }
        //删除cookie,用于修复退出登录记住的账户显示不正确的bug
        delAllCookie();
        //记住用户名
        if($("#rememberMe").is(':checked'))
        {
            var username = $("#loginUserName").val();
            amplify.store( "rmbUser", "true" );
            amplify.store( "username", username );
            amplify.store( "rmbPass", "true" );
            amplify.store( "loginPassword", loginPassword );
        }else {
            amplify.store( "rmbUser", "false" );
            amplify.store( "username", "" );
            amplify.store( "rmbPass", "false" );
            amplify.store( "loginPassword", "" );
        }
    }

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
    function checkEmail(v) {
        var email = $(v).val();
        if(email.indexOf("@qq.com") >= 0){
            $("#emailMsg").show();
        }else{
            $("#emailMsg").hide();
        }
    }

    function frameLogin() {
        //先尝试退出登录避免账号混乱
        $.ajax({
            url:"/zxdoc/control/logout",
            type:"GET",
            dataType:"html",
            async: false,
            success:function(){
                //无需任何处理
            },
            error:function(){
                //无需任何处理
            }
        })
        var options = {
            beforeSubmit: function () {
                return $("#loginform").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                if(data.ajax){
                    saveRemember();
                    layer.msg("登录成功，即将跳转...");
                    location.href = "main";
                }
            },
            url: "ajaxCheckLogin",
            type: 'post'
        };
        $("#loginform").ajaxSubmit(options);
    }

    function submitForgetPassword() {
        var options = {
            beforeSubmit: function () {
                return $("#forget-form").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                if(data.data=="重置成功")
                {
                    layer.msg("重置成功，即将跳转到登录，请至邮箱内查收新密码！", {
//                        time: 0 //不自动关闭
//                        ,btn: ['确定']
                        shade: 0.2 //遮罩透明度
                        ,end: function(index){
                            location.href = '/zxdoc/control/frameLogin?frameLogin=true';
                        }
                    });
                }else {
                    layer.msg(data.data, {
//                        time: 0 //不自动关闭
//                        ,btn: ['确定']
                        shade: 0.2 //遮罩透明度
                    });
                }
            },
            url: "resetPassInfo",
            type: 'post'
        };
        $("#forget-form").ajaxSubmit(options);
    }

    $("#register-form").validationEngine("attach", {promptPosition: "topRight"});
    function register() {
        var fileName = $("input[name='regPhoto']").val();
        if(fileName != null && fileName != '') {
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
        }

        if("Y" == $("#isPartner").val()){//合伙人名称最好不能与合伙机构的相同
            if($("#registerUserName").val() == $("#groupName").val()){
                showError("合伙人登录账号不允许使用合伙机构名称");
                return;
            }
        };

        var options = {
            beforeSubmit: function () {
                if($("#partnerType").val() == "P" && !$("#register-form input[name=partnerUsername]").val()){
                    showError("个人合伙人请录入个人登录账号");
                    return false;
                }
                return $("#register-form").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                var result = $(data).find("#result").html();
                if (result != "false") {
                    var msg = "注册成功，即将跳转...";
                    layer.msg(msg, {time: 10000});
                    var email = $("input[name='email']").val();
                    $("#register-submit-btn").attr('disabled',"true");
                    setTimeout(function(){
                        var username = "";
                        if($("#partnerType").val() == "P"){
                            username = $("#register-form input[name=partnerUsername]").val();
                        }else{
                            username = $("#register-form input[name=username]").val();
                        }
                        var password = $("#register-form input[name=password]").val();
                        $.ajax({
                            url: "ajaxCheckLogin",
                            type: "POST",
                            dataType: "json",
                            data:{"USERNAME": username, "PASSWORD": password},
                            success: function (data) {
                                if(data.ajax){
                                    location.href = "main";
                                }
                            }
                        });
                    }, 5000);
                } else {
                    var msg = $(data).find("#msg").html();
                    showError(msg);
                }
            },
            url: "register",
            type: 'post'
        };
        $("#register-form").ajaxSubmit(options);
    }

    document.loginform.JavaScriptEnabled.value = "Y";
    <#if focusName>
    document.loginform.USERNAME.focus();
    <#else>
    document.loginform.PASSWORD.focus();
    </#if>


    jQuery('#register-btn').click(function () {
        jQuery('.login-form').hide();
        jQuery('.register-form').show();
    });

    jQuery('#register-back-btn').click(function () {
        jQuery('.login-form').show();
        jQuery('.register-form').hide();
    });

    jQuery('#forget-password').click(function () {
        jQuery('.login-form').hide();
        jQuery('.forget-form').show();
    });

    jQuery('#back-btn').click(function () {
        jQuery('.login-form').show();
        jQuery('.forget-form').hide();
    });

    $.backstretch([
                "/metronic-web/images/login-bg/1.jpg",
                "/metronic-web/images/login-bg/2.jpg",
                "/metronic-web/images/login-bg/3.jpg",
                "/metronic-web/images/login-bg/4.jpg"
            ], {
                fade: 1000,
                duration: 8000
            }
    );

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
                        term: request.term, 'groupTips': $('#searchName #groupName').val(),"groupType":$("#groupType").val()
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
</body>
</html>