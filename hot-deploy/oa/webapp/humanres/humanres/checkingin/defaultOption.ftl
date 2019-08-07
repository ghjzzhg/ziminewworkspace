<script language="JavaScript">
    $(function () {
        $("#SearchScheduleOfWorkForm_type option").each(function(){
            var state = $(this).val();
            if(state == "WORK_SCHEDULE_AUTO"){
                $(this).attr("selected",true);
            }
        })
    });
</script>