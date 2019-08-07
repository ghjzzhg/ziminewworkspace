<script type="text/javascript">
    jQuery(function(){
        $(".icon-qrcode").fancybox({
            width : 600,
            height : 300,
            fitToView : false,
            autoSize : false,
            type : 'image'});
    })

    function closeEditorFrame(){
        ajaxUpdateAreas('ProcessList,ProcessListOnly,sortField=sn');
        var frame = document.getElementById("processEditor"),
                frameDoc = frame.contentDocument || frame.contentWindow.document;
        frameDoc.removeChild(frameDoc.documentElement);
    }
</script>
<div class="dynamic-form-dialog">
    <form class='dynamic-form' method='post'>
        <table class='sectBorder' border='0' cellspacing='0' cellpadding='0'>
            <tbody id='dynamic-form-tbody'></tbody>
        </table>
    </form>
</div>
<iframe id="processEditor" name="processEditor" frameborder="no" style="overflow-y:auto; border: 0;width:100%; min-height:800px;">
</iframe>