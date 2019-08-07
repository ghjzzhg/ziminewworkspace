<style type="text/css">
    .help-content > div{
        width: 800px;
        margin: 0 auto;
    }

    .help-content > div > img {
        width: 100%;
    }
</style>
<#if files?has_content>
<div class="help-content">
    <#list files as fileId>
        <div>
            <#--<img src="/zxdoc/static/help/幻灯片${fileId}.png">-->
            <img src="/zxdoc/static/help/%E5%B9%BB%E7%81%AF%E7%89%87${fileId}.png">
        </div>
    </#list>
</div>
</#if>