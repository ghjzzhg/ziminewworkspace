<script type="text/javascript">
    <#if pageFlag == "borrowRegisterConfirm" >
        function activitiClickPassFunction(){
            $.fixedAssets.saveborrowAssetsConfirm();
        }
        function activitiClickRejectFunction(){
            //驳回事件
            $.fixedAssets.rejectBorrowAssetsConfirm()
        }
        function activitiClickBackToStartFunction(){
            //驳回指发起人事件
        }
        function activitiClickTerminateFunction(){
            //中止时间
        }
        function activitiClicksetAllotFunction(){
            //转办事件
        }
    <#elseif pageFlag == "ReturnAssetsForm">
        function activitiClickPassFunction(){
            $.fixedAssets.saveReturnAssets('${userLogin.partyId}');
        }
        function activitiClickRejectFunction(){
            //驳回事件
        }
        function activitiClickBackToStartFunction(){
            //驳回指发起人事件
        }
        function activitiClickTerminateFunction(){
            //中止时间
        }
        function activitiClicksetAllotFunction(){
            //转办事件
        }
    </#if>
</script>