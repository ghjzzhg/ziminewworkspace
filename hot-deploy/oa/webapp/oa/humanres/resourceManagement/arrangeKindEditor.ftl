<script type="text/javascript">
    var arrangeRemarks;
    $(function () {
        arrangeRemarks = KindEditor.create('#ArrangeResourceOrder_arrangeRemarks', {
            allowFileManager: true
        });
        $("#ArrangeResourceOrder_auditRemarks_title").parent().next().html(unescapeHtmlText('${data.auditRemarks?default("")}'));
    });
</script>
