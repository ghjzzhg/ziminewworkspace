<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }
</style>
<script type="application/javascript">
    function saveCaseProgress() {
        var options = {
            beforeSubmit: function () {
                return $("#templateNodesForm").validationEngine("validate");
            },
            dataType: "json",
            success: function(content){
                showInfo(content.data);
                closeCurrentTab();
            },
            url: "SaveCaseProgress?caseId=${caseId}",
            type: "post"
        };
        $("#templateNodesForm").ajaxSubmit(options);
    }
</script>
<div class="portlet light ">
    <div class="portlet-body">
        <div class="form-group">
            <div class="margiv-top-10">
                <a href="javascript:void(0);" class="btn green" onclick="saveCaseProgress()"> 完成 </a>
            </div>
        </div>
    </div>
</div>