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
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>${layoutSettings.companyName}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)!}</#if></title>
    <#if layoutSettings.shortcutIcon?has_content>
      <#assign shortcutIcon = layoutSettings.shortcutIcon/>
    <#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
      <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
    </#if>
    <#if shortcutIcon?has_content>
      <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
    </#if>
    <#if layoutSettings.javaScripts?has_content>
        <#--layoutSettings.javaScripts is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
        <#list layoutSettings.javaScripts as javaScript>
            <#if javaScriptsSet.contains(javaScript)>
                <#assign nothing = javaScriptsSet.remove(javaScript)/>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
    </#if>
    <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
        <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#list>
    </#if>
    <#if layoutSettings.styleSheets?has_content>
        <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
        <#list layoutSettings.styleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_STYLESHEET?has_content>
        <#list layoutSettings.VT_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
        <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
        <#list layoutSettings.rtlStyleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
        <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_EXTRA_HEAD?has_content>
        <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
            ${extraHead}
        </#list>
    </#if>
    <#if lastParameters??><#assign parametersURL = "&amp;" + lastParameters></#if>
    <#if layoutSettings.WEB_ANALYTICS?has_content>
      <script language="JavaScript" type="text/javascript">
        <#list layoutSettings.WEB_ANALYTICS as webAnalyticsConfig>
          ${StringUtil.wrapString(webAnalyticsConfig.webAnalyticsCode!)}
        </#list>
      </script>
    </#if>
</head>
<#if layoutSettings.headerImageLinkUrl??>
  <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}?externalLoginKey=${requestAttributes.externalLoginKey?default('')}">
<#else>
  <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl}?externalLoginKey=${requestAttributes.externalLoginKey?default('')}">
</#if>
<#assign organizationLogoLinkURL = "${layoutSettings.organizationLogoLinkUrl!}">
<#assign miniBar = ''>
<#if userLogin?has_content && applicationMenuLocation?has_content>
    <#assign miniBar = 'normal-bar'>
    <#assign cookies = request.getCookies()>
    <#list cookies as cookie>
        <#if cookie.name = "mini-bar">
            <#assign miniBar = cookie.value>
        </#if>
    </#list>
</#if>
<body>
<div id="viewerPlaceHolderContainer" style="display: none; overflow: hidden">
    <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
        <tr>
            <td class="page_header">
                文件预览
            </td>
            <td class="page_header">
                <div style="float:right">
                    <span onclick="closeFilePreview()" class="button"><span>关闭</span></span>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">

                <div id="viewerPlaceHolder" style="height:650px;display:block">
                </div>
            </td>
        </tr>
    </table>
</div>
<div id="reportResultWrapper" style="display: none">
    <div class="row-fluid">
        <div class="span12">
            <div class="widget">
                <div class="head dark">
                    <div class="icon"><span class="icos-list"></span></div>
                    <h2>查看</h2>
                    <ul class="buttons">
                        <li><span onclick="closeCurrentTab(this)"><span class="icos-cancel1" title="关闭"></span></span></li>
                    </ul>
                </div>
                <div class="block">
                    <div id="reportResult" style="width: 100%; height: 500px"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="popupViewer" style="display: none">
    <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
        <tr>
            <td class="page_header">
                查看资料
            </td>
            <td class="page_header">
                <div style="float:right;">
                    <span onclick="closeCurrentTab(this)" class="button"><span>关闭</span></span>
                </div>
            </td>
        </tr>
    </table>
    <div id="popupContent" class="main_ct">

    </div>
</div>
  <div id="wait-spinner" style="display:none">
    <div id="wait-spinner-image"></div>
  </div>
  <div class="page-container ${miniBar}">
  <div class="hidden">
    <a href="#column-container" title="${uiLabelMap.CommonSkipNavigation}" accesskey="2">
      ${uiLabelMap.CommonSkipNavigation}
    </a>
  </div>
  <div id="masthead" style="z-index: 1">
    <ul>
        <li class="menu-switch active glyphicon glyphicon-align-justify"></li>
      <#if layoutSettings.headerImageUrl??>
        <#assign headerImageUrl = layoutSettings.headerImageUrl>
      <#elseif layoutSettings.commonHeaderImageUrl??>
        <#assign headerImageUrl = layoutSettings.commonHeaderImageUrl>
      <#elseif layoutSettings.VT_HDR_IMAGE_URL??>
        <#assign headerImageUrl = layoutSettings.VT_HDR_IMAGE_URL.get(0)>
      </#if>
      <#if headerImageUrl??>
        <#if organizationLogoLinkURL?has_content>
            <li class="org-logo-area"><a href="<@ofbizUrl>${logoLinkURL}</@ofbizUrl>"><img alt="${layoutSettings.companyName}" src="<@ofbizContentUrl>${StringUtil.wrapString(organizationLogoLinkURL)}</@ofbizContentUrl>"></a></li>
            <#else>
            <li class="logo-area"><a href="${logoLinkURL}"><img alt="${layoutSettings.companyName}" src="<@ofbizContentUrl>${StringUtil.wrapString(headerImageUrl)}</@ofbizContentUrl>"/></a></li>
        </#if>
      </#if>
          <#--<li class="glyphicon glyphicon-th header-menu-switch"></li>-->
      <#if layoutSettings.middleTopMessage1?has_content && layoutSettings.middleTopMessage1 != " ">
        <li>
            <div class="last-system-msg" style="width: 30em">
                <div style="height:45px; overflow: hidden">
                        <center>${layoutSettings.middleTopHeader!}</center>
                    <div class="last-system-msg-1" style="display: none">
                        <a href="${layoutSettings.middleTopLink1!}">${layoutSettings.middleTopMessage1!}</a>
                    </div>
                    <div class="last-system-msg-2" style="display: none">
                        <a href="${layoutSettings.middleTopLink2!}">${layoutSettings.middleTopMessage2!}</a>
                    </div>
                    <div class="last-system-msg-3" style="display: none">
                        <a href="${layoutSettings.middleTopLink3!}">${layoutSettings.middleTopMessage3!}</a>
                    </div>
                </div>
            </div>
        </li>
      </#if>
      <li class="preference-area">
          <ul>
          <#if userLogin??>
            <#if layoutSettings.topLines?has_content>
                <li>
              <#list layoutSettings.topLines as topLine>
                <#if topLine.text??>
                  <div>${topLine.text}<a href="${StringUtil.wrapString(topLine.url!)}${StringUtil.wrapString(externalKeyParam)}">${topLine.urlText!}</a></div>
                <#elseif topLine.dropDownList??>
                  <div><#include "component://common/webcommon/includes/insertDropDown.ftl"/></div>
                <#else>
                  <div>${topLine!}</div>
                </#if>
              </#list>
            </li>
            <#else>
              <li>${userLogin.userLoginId}</li>
            </#if>
            <li><a href="<@ofbizUrl>logout</@ofbizUrl>" class="glyphicon glyphicon-log-out" style="font-size: 30px; color:lightskyblue"></a></li>
          <#else/>
            <li>${uiLabelMap.CommonWelcome}! <a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
          </#if>
          <#---if webSiteId?? && requestAttributes._CURRENT_VIEW_?? && helpTopic??-->
          <#if parameters.componentName?? && requestAttributes._CURRENT_VIEW_?? && helpTopic??>
            <#include "component://common/webcommon/includes/helplink.ftl" />
            <li><a style="font-size: 30px; color:lightgreen;" class="glyphicon glyphicon-info-sign <#if pageAvail?has_content>alert</#if>" href="javascript:lookup_popup1('showHelp?helpTopic=${helpTopic}&amp;portalPageId=${parameters.portalPageId!}','help' ,500,500);"></a></li>
          </#if>
          </ul>
      </li>
    </ul>
  </div>
  <#--<br class="clear" />-->
