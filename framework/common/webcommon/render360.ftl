<#--${ua.browser}
${ua.browserVersion.majorVersion}
${ua.operatingSystem}-->
<#if !isXp?has_content>
    <#assign ua = Static["eu.bitwalker.useragentutils.UserAgent"].parseUserAgentString(request.getHeader("User-Agent"))>
    <#assign isXp = ua.operatingSystem == 'WINDOWS_XP'>
    <#assign isIE = ua.browser?index_of('IE') gt -1>
    <#if isIE>
        <#assign ieVersion = ua.browserVersion.majorVersion?number>
    </#if>
</#if>
<meta name="renderer" content="<#if isXp || (isIE && ieVersion lt 9)>webkit<#else>ie-stand</#if>"/>
