<#--
&lt;#&ndash;
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
&ndash;&gt;

<#if (requestAttributes.externalLoginKey)??><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey!></#if>
<#if (externalLoginKey)??><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey!></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.webapp.control.LoginWorker"].getAppBarWebInfos(security, userLogin, ofbizServerName, "main")>
<#assign displaySecondaryApps = Static["org.ofbiz.webapp.control.LoginWorker"].getAppBarWebInfos(security, userLogin, ofbizServerName, "secondary")>

<#if userLogin?has_content>
  &lt;#&ndash;<div id="main-navigation">&ndash;&gt;
  <div class="main-navigation" style="z-index: 1">
      <div class="main-navigation-arrow"></div>
    <ul>
      <#assign appCount = 0>
      <#assign firstApp = true>
      <#list displayApps as display>
        <#assign thisApp = display.getContextRoot()>
        <#assign selected = false>
        <#if thisApp == contextPath || contextPath + "/" == thisApp>
          <#assign selected = true>
        </#if>
        <#assign servletPath = Static["org.ofbiz.webapp.WebAppUtil"].getControlServletPath(display)>
        <#assign thisURL = StringUtil.wrapString(servletPath)>
        <#if thisApp != "/">
          <#assign thisURL = thisURL + "main">
        </#if>
        <#if layoutSettings.suppressTab?? && layoutSettings.suppressTab?split(",", "r")?seq_contains(display.name)>
        &lt;#&ndash;<#if layoutSettings.suppressTab?? && layoutSettings.suppressTab ==display.name>&ndash;&gt;
          &lt;#&ndash; do not display this component&ndash;&gt;
        <#else>
          &lt;#&ndash;<#if appCount % 4 == 0>&ndash;&gt;
            &lt;#&ndash;<#if firstApp>&ndash;&gt;
              &lt;#&ndash;<li class="first ">&ndash;&gt;
              &lt;#&ndash;<#assign firstApp = false>&ndash;&gt;
            &lt;#&ndash;<#else>&ndash;&gt;
              &lt;#&ndash;</li>&ndash;&gt;
              <li class="<#if selected>selected</#if> <#if firstApp>first</#if>">

                <#assign firstApp = false>
            &lt;#&ndash;</#if>&ndash;&gt;
          &lt;#&ndash;</#if>&ndash;&gt;
          <a class="<#if selected>currentStep<#else>nextStep</#if>" href="${thisURL}${StringUtil.wrapString(externalKeyParam)}"<#if uiLabelMap??> title="${uiLabelMap[display.title]}"><i class="&lt;#&ndash;glyphicon glyphicon-list&ndash;&gt;"></i><span class="before"></span><span>${uiLabelMap[display.title]}</span><span class="after"></span><#else> title="${display.description}"><span class="before"></span><i class="&lt;#&ndash;glyphicon glyphicon-list&ndash;&gt;"></i><span>${display.title}</span><span class="after"></span></#if></a>
          <#assign appCount = appCount + 1>
          </li>
        </#if>
      </#list>
      <#list displaySecondaryApps as display>
        <#assign thisApp = display.getContextRoot()>
        <#assign selected = false>
        <#if thisApp == contextPath || contextPath + "/" == thisApp>
          <#assign selected = true>
        </#if>
          <#assign servletPath = Static["org.ofbiz.webapp.WebAppUtil"].getControlServletPath(display)>
          <#assign thisURL = StringUtil.wrapString(servletPath)>
          <#if thisApp != "/">
            <#assign thisURL = thisURL + "main">
          </#if>
        &lt;#&ndash;<#if appCount % 4 == 0>&ndash;&gt;
          &lt;#&ndash;<#if firstApp>&ndash;&gt;
            &lt;#&ndash;<li class="first ">&ndash;&gt;
            &lt;#&ndash;<#assign firstApp = false>&ndash;&gt;
          &lt;#&ndash;<#else>&ndash;&gt;
            &lt;#&ndash;</li>&ndash;&gt;
            <li class=" <#if selected>selected</#if> <#if firstApp>first</#if>">
          &lt;#&ndash;</#if>&ndash;&gt;
        &lt;#&ndash;</#if>&ndash;&gt;
        <a href="${thisURL}${StringUtil.wrapString(externalKeyParam)}"<#if uiLabelMap??> title="${uiLabelMap[display.description]}"><i class="&lt;#&ndash;glyphicon glyphicon-list&ndash;&gt;"></i><span>${uiLabelMap[display.title]}</span><#else> title="${display.description}"><i class="&lt;#&ndash;glyphicon glyphicon-list&ndash;&gt;"></i><span>${display.title}</span></#if></a>
        &lt;#&ndash;<#assign appCount = appCount + 1>&ndash;&gt;
          </li>
      </#list>
      &lt;#&ndash;<#if appCount != 0>
        </li>
        <li class="last"></li>
      </#if>&ndash;&gt;
    </ul>
  </div>
</#if>
-->
