<script language="JavaScript">
    $(function () {
        $("#stateType option").each(function(){
            var state = $(this).val();
            if(state == "CONTRACT_STATUS_B"){
                $(this).attr("selected",true);
            }
        })
    });
</script>