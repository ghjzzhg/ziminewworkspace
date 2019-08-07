<#if listOfWork?has_content>
    <#assign listOfWorkId = listOfWork.listOfWorkId?default('')>
    <#assign toWorkTime_c_hour = listOfWork.toWorkTime_c_hour?default('')>
    <#assign toWorkTime_c_minutes = listOfWork.toWorkTime_c_minutes?default('')>
    <#assign getOffWorkTime_c_hour = listOfWork.getOffWorkTime_c_hour?default('')>
    <#assign getOffWorkTime_c_minutes = listOfWork.getOffWorkTime_c_minutes?default('')>
</#if>
<script language="javascript">
    $(function () {

        var listOfWorkId = '${listOfWorkId?default("")}';
        var toWorkTime_c_hour = '${toWorkTime_c_hour?default("")}';
        var toWorkTime_c_minutes = '${toWorkTime_c_minutes?default("")}';
        var getOffWorkTime_c_hour = '${getOffWorkTime_c_hour?default("")}';
        var getOffWorkTime_c_minutes = '${getOffWorkTime_c_minutes?default("")}';

        if('' != listOfWorkId){
            $("select[name='toWorkTime_c_hour'] option").each(function () {
                if(toWorkTime_c_hour == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[name='toWorkTime_c_minutes'] option").each(function () {
                if(toWorkTime_c_minutes == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[name='getOffWorkTime_c_hour'] option").each(function () {
                if(getOffWorkTime_c_hour == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[name='getOffWorkTime_c_minutes'] option").each(function () {
                if(getOffWorkTime_c_minutes == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
        };
    })
</script>