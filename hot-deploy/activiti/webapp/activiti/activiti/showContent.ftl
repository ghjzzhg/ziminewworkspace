<script type="text/javascript">
    $(function () {
        <#if parameters.content?has_content>
            var content = unescapeHtmlText('${parameters.content}');
            $("#showContentForm").html(content);
        </#if>
    })
</script>
<div id="showContentForm">

</div>