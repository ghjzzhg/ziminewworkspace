<script type="text/javascript">
    function closeEditorFrame(){
        ajaxUpdateAreas('ModelList,ModelListOnly,sortField=sn');
        var frame = document.getElementById("modelEditor"),
                frameDoc = frame.contentDocument || frame.contentWindow.document;
        frameDoc.removeChild(frameDoc.documentElement);
    }
    $(function(){
        $("#ModelList").find("table >tbody > tr:eq(1) .icon-pencil").click();
    })
</script>
<iframe id="modelEditor" name="modelEditor" frameborder="no" style="overflow-y:auto; border: 0;width:100%; min-height:800px;">
</iframe>