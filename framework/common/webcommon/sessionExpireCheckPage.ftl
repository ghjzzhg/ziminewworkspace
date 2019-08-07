<script type="text/javascript" src="/images/jquery/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/images/lib/common.js?t=20171106"></script>
<script type="text/javascript" src="/images/lib/layer/layer.js?t=20170909"></script>
<script type="text/javascript">
    function showLoader(msg) {
        //覆盖showLoader方法，避免定时检测session时出现遮罩层
    }
    $(function(){
        checkUseSessionExpire();
    })

    function checkUseSessionExpire(){
        setTimeout(function(){
            jQuery.ajax({
                type: 'GET',
                url: "<@ofbizContentUrl>${request.contextPath}</@ofbizContentUrl>/control/checkSessionExpire",
                async: true,
                dataType: 'html',
                success: function (response, status, xhr) {
                    var ct = xhr.getResponseHeader("content-type") || "";
                    if (ct.indexOf('html') > -1) {
                        showUserSessionExpireAlert(response);
                    }else{
                        top.document.getElementById("expireSessionCheckIframe").contentWindow.checkUseSessionExpire();
                    }
                },
                error: function(){
                    checkUseSessionExpire();
                }
            })
        }, 600000);//8小时+200秒=29000000
    }
</script>