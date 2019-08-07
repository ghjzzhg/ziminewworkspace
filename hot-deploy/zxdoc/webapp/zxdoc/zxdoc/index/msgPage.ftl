<#assign errorMessage = requestAttributes._ERROR_MESSAGE_!>
<#--登录信息-->
<#if result?has_content>
    <div id="result">${result.result?default("")}</div>
    <div id="msg">${result.msg?default("")}</div>
<#elseif errorMessage?has_content>
    <div id="result">false</div>
    <div id="msg">errorMessage</div>
</#if>
<#--消息-->
<#if msg?has_content>
    <div id="msg">${msg?default("")}</div>
</#if>
<#if parameters.id?has_content>
<div id="caseId">${parameters.id?default("")}</div>
</#if>
<#if data?has_content && data.id?has_content>
<div id="caseId">${data.id?default("")}</div>
</#if>
<#if parameters.caseName?has_content>
<div id="caseName">${parameters.caseName?default("")}</div>
</#if>
<#if parameters.data?has_content>
    <#if parameters.data.id?has_content>
            <div id="dataId">${parameters.data.id?default("")}</div>
    </#if>
    <#if parameters.data.msg?has_content>
    <div id="dataMsg">${parameters.data.msg?default("")}</div>
    </#if>
</#if>
