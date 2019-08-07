<div>
    <div style="text-align: center" class="portlet-notice-title">
        <a href="/email/control/main">您现在有5封新邮件</a>
    </div>
    <#list emails as email>
        <div class="portlet-notice-item">
            [${email.date}] ${email.title}
        </div>
    </#list>
</div>