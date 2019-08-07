<script type="text/javascript">
    $(function () {
        $("#orderResourceView_auditRemarks_title").parent().next().html(unescapeHtmlText('${data.auditRemarks?default("")}'));
        $("#orderResourceView_arrangeRemarks_title").parent().next().html(unescapeHtmlText('${data.arrangeRemarks?default("")}'));
    });
</script>
