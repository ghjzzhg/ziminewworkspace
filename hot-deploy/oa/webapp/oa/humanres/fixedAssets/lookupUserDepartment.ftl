<script type="text/javascript">

    function lookupUserDepartment(value) {
        $.ajax({
            type: 'POST',
            url: "lookupUserDepartment",
            async: true,
            data:{partyId: value},
            success: function (data) {
                console.log(data);
                $('input[name="useDepartment"]').val(data.data.department);
                //$("#24_lookupId_FixedAssetsForm_useDepartment_lookupDescription").val(data.data.departmentName);
            }
        });
    }
</script>
