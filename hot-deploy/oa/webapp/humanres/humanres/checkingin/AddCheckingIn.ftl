<#assign checkingInTime_c_hour = checkingInTime_c_hour?default('')>
<#assign checkingInTime_c_minutes = checkingInTime_c_minutes?default('')>
<#assign partyId = partyId?default('')>
<script language="javascript">
    $(function () {

        var partyId = '${partyId?default("")}';
        var checkingInTime_c_hour = '${checkingInTime_c_hour?default("")}';
        var checkingInTime_c_minutes = '${checkingInTime_c_minutes?default("")}';

        if('' != partyId){
            $("select[name='checkingInTime_c_hour'] option").each(function () {
                if(checkingInTime_c_hour == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[name='checkingInTime_c_minutes'] option").each(function () {
                if(checkingInTime_c_minutes == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
        };

        $("input[name='checkingInType'][value= 'CHECKING_IN']").attr("checked",true);
    })
</script>