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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<#if layoutSettings.VT_STYLESHEET?has_content>
<#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.VT_STYLESHEET as styleSheet>
    <#if styleSheet?index_of("main") == -1>
        <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#if>
    </#list>
</#if>

<#if layoutSettings.styleSheets?has_content>
<#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.styleSheets as styleSheet>
    <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
</#if>
<script src="<@ofbizContentUrl>/images/jquery/jquery-1.11.0.min.js</@ofbizContentUrl>" type="text/javascript"></script>
<#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
        <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
    </#list>
</#if>
    <title>登录系统</title>
    <style>
        /* Cubic Bezier Transition */
        /***
        Login page
        ***/
        /* logo page */
        .login {
            background-color: #666 !important; }

        .login .logo {
            margin: 60px auto 20px auto;
            padding: 15px;
            text-align: center; }

        .login .content {
            background: url(/metronic/images/bg-white-lock.png) repeat;
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
            border-radius: 7px; }

        .login .content h3 {
            color: #eee; }

        .login .content h4 {
            color: #eee; }

        .login .content p,
        .login .content label {
            color: #fff; }

        .login .content .login-form,
        .login .content .forget-form {
            padding: 0px;
            margin: 0px; }

        .login .content .form-control {
            background-color: #fff; }

        .login .content .forget-form {
            display: none; }

        .login .content .register-form {
            display: none; }

        .login .content .form-title {
            font-weight: 300;
            margin-bottom: 25px; }

        .login .content .form-actions {
            background-color: transparent;
            clear: both;
            border: 0px;
            padding: 0px 30px 25px 30px;
            margin-left: -30px;
            margin-right: -30px; }

        .login .content .form-actions .checkbox {
            margin-left: 0;
            padding-left: 0; }

        .login .content .forget-form .form-actions {
            border: 0;
            margin-bottom: 0;
            padding-bottom: 20px; }

        .login .content .register-form .form-actions {
            border: 0;
            margin-bottom: 0;
            padding-bottom: 0px; }

        .login .content .form-actions .checkbox {
            margin-top: 8px;
            display: inline-block; }

        .login .content .form-actions .btn {
            margin-top: 1px; }

        .login .content .forget-password {
            margin-top: 25px; }

        .login .content .create-account {
            border-top: 1px dotted #eee;
            padding-top: 10px;
            margin-top: 15px; }

        .login .content .create-account a {
            display: inline-block;
            margin-top: 5px; }

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
            text-align: center; }

        .login .content .has-error .select2-container i {
            color: #b94a48; }

        .login .content .select2-container a span {
            font-size: 13px; }

        .login .content .select2-container a span img {
            margin-left: 4px; }

        /* footer copyright */
        .login .copyright {
            text-align: center;
            margin: 0 auto;
            padding: 10px;
            color: #eee;
            font-size: 13px; }

        @media (max-width: 480px) {
            /***
            Login page
            ***/
            .login .logo {
                margin-top: 10px; }
            .login .content {
                padding: 30px;
                width: 222px; }
            .login .content h3 {
                font-size: 22px; }
            .login .checkbox {
                font-size: 13px; } }

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
    <a href="index.html">
        <img src="/images/rextec_logo.png" alt="" /> </a>
</div>
<div class="content">
    <!-- BEGIN LOGIN FORM -->
    <form class="login-form" method="post" id="loginform" name="loginform" >
        <input type="hidden" name="JavaScriptEnabled" value="N"/>
        <h3 style="text-align: center" class="form-title">登录</h3>
        <div class="alert alert-danger display-hide">
            <button class="close" data-close="alert"></button>
            <span> 请填写用户名及密码. </span>
        </div>
        <div class="form-group">
            <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
            <label class="control-label visible-ie8 visible-ie9">用户名</label>
            <div class="input-icon">
                <i class="fa fa-user"></i>
                <input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="用户名" name="USERNAME" /> </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密码</label>
            <div class="input-icon">
                <i class="fa fa-lock"></i>
                <input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="密码" name="PASSWORD" /> </div>
        </div>
        <div class="form-actions">
            <label>
                <input type="checkbox" name="remember" value="1" /> 记住我 </label>
            <button type="submit" class="btn green pull-right"> 登录 </button>
        </div>
        <div class="login-options">
            <h4>或使用以下方式直接登录</h4>
            <div class="fa" style="width: 100%; text-align: center;font-size: 2em;">
                <a style="padding: 0 20px" class="fa-qq" data-original-title="QQ" href="javascript:;"> </a>
                <a style="padding: 0 20px" class="fa-weibo" data-original-title="微博" href="javascript:;"> </a>
                <a style="padding: 0 20px" class="fa-weixin" data-original-title="微信" href="javascript:;"> </a>
            </div>
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
    <form class="forget-form" action="index.html" method="post">
        <h3 style="text-align: center">忘记密码 ?</h3>
        <p> 请输入注册时填写的邮箱地址用于接收新密码. </p>
        <div class="form-group">
            <div class="input-icon">
                <i class="fa fa-envelope"></i>
                <input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="Email" name="email" /> </div>
        </div>
        <div class="form-actions">
            <button type="button" id="back-btn" class="btn red btn-outline">返回 </button>
            <button type="submit" class="btn green pull-right"> 重置 </button>
        </div>
    </form>
    <!-- END FORGOT PASSWORD FORM -->
    <!-- BEGIN REGISTRATION FORM -->
    <form class="register-form" action="index.html" method="post">
        <h3 style="text-align: center">注册</h3>
        <p> 请填写您的详细信息: </p>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">姓名</label>
            <div class="input-icon">
                <i class="fa fa-font"></i>
                <input class="form-control placeholder-no-fix" type="text" placeholder="姓名" name="fullname" /> </div>
        </div>
        <div class="form-group">
            <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
            <label class="control-label visible-ie8 visible-ie9">电子邮箱</label>
            <div class="input-icon">
                <i class="fa fa-envelope"></i>
                <input class="form-control placeholder-no-fix" type="text" placeholder="Email" name="email" /> </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">省份</label>
            <select name="country" id="country_list" class="select2 form-control" style="width: 100%">
                <option value="">省/直辖市</option>
                <option value="北京">北京</option>
            </select>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">市/区</label>
            <div class="input-icon">
                <i class="fa fa-location-arrow"></i>
                <input class="form-control placeholder-no-fix" type="text" placeholder="市/区" name="city" /> </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">详细地址</label>
            <div class="input-icon">
                <i class="fa fa-check"></i>
                <input class="form-control placeholder-no-fix" type="text" placeholder="详细地址" name="address" /> </div>
        </div>
        <p> 请填写您的登录账号信息: </p>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">用户名</label>
            <div class="input-icon">
                <i class="fa fa-user"></i>
                <input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="用户名" name="username" /> </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密码</label>
            <div class="input-icon">
                <i class="fa fa-lock"></i>
                <input class="form-control placeholder-no-fix" type="password" autocomplete="off" id="register_password" placeholder="密码" name="password" /> </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密码确认</label>
            <div class="controls">
                <div class="input-icon">
                    <i class="fa fa-check"></i>
                    <input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="密码确认" name="rpassword" /> </div>
            </div>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" name="tnc" /> 我同意
                <a href="javascript:;"> 服务条款 </a> 及
                <a href="javascript:;"> 保密协议 </a>
            </label>
            <div id="register_tnc_error"> </div>
        </div>
        <div class="form-actions">
            <button id="register-back-btn" type="button" class="btn red btn-outline"> 返回 </button>
            <button type="submit" id="register-submit-btn" class="btn green pull-right"> 注册 </button>
        </div>
    </form>
    <!-- END REGISTRATION FORM -->
</div>

<script language="JavaScript" type="text/javascript">
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
              "/metronic/images/login-bg/1.jpg",
              "/metronic/images/login-bg/2.jpg",
              "/metronic/images/login-bg/3.jpg",
              "/metronic/images/login-bg/4.jpg"
          ], {
              fade: 1000,
              duration: 8000
          }
  );
</script>
</body>
</html>