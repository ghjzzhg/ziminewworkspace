<script type="text/javascript">
    var arrangeRemarks;
    $(function () {
        arrangeRemarks = KindEditor.create('#ArrangeVehicleOrder_arrangeRemarks', {
            allowFileManager: true
        });
        $("#ArrangeVehicleOrder_reviewRemarks_title").parent().next().html(unescapeHtmlText('${orderVehicleDetailMap.reviewRemarks?default("")}'));
    });
</script>
