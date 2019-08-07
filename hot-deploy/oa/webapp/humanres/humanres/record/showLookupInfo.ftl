<script type="text/javascript">
    $(function () {
        var partyId = '${resultMap.partyId?default('')}';
        $('input[name="partyId"]').data("promptPosition", "bottomLeft");
        if(partyId != ''){
            $("#TransactionRecordCreate_lastGroup").html("${resultMap.lastGroup?default('')}");
            $("#TransactionRecordCreate_lastPost").html("${resultMap.lastPost?default('')}");
        }
    })
    function showLookupInfo(value) {
        $.ajax({
            type: 'POST',
            url: "TransactionRecord",
            async: true,
            data:{partyId: value},
            dataType: 'json',
            success: function (data) {
                if(data.data.flag == '1'){
                    $("#TransactionRecordCreate_lastGroup").html(data.data.lastGroup);
                    $("#TransactionRecordCreate_lastPost").html(data.data.lastPost);
                    $("select[name='lastPosition'] option").each(function () {
                        if(data.data.lastPosition == $(this).val()){
                            $(this).attr('selected',"true");
                        }
                    })
                }
            }
        });
    }
</script>