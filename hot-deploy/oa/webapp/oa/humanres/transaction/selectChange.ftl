<script type="text/javascript">
    $(function(){
        $('select[name="a"]').change(function(){
            var selected = $('select[name="a"]').val();
            $.ajax({
                type: 'POST',
                url: "TransactionProgressAjax",
                async: true,
                data:{selected:selected},
                dataType: 'html',
                success: function (content) {
                    $("#transactionProgressList").html(content);
                }
            });
        });
    });
</script>