<script src="/images/jquery/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="/images/lib/layer/layer.js?t=20170909" type="text/javascript"></script>
<script type="application/javascript" language="JavaScript">
    $(function () {
        setTimeout(function() {
            var fileId = "${fileId?default("")}";
            if (fileId != null && fileId != "") {
                var form = $("<form>");//定义一个form表单
                form.attr("style", "display:none");
                form.attr("target", "");
                form.attr("method", "post");
                form.attr("action", "/ckfinder/control/action?externalLoginKey=${externalLoginKey}&command=DownloadFile&fileId=" + fileId);
                $("body").append(form);//将表单放置在web中
                form.submit();//表单提交
            } else {
                showError("下载错误，请刷新后重试!")
            }
        },2000)
        setTimeout(function() {
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index);
        },3000)

    })
</script>
<div align="center" id="txtView" style="height: 100%; width: 100%">
该文件不支持预览，即将下载！
</div>
