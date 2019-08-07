
<script src="/images/jquery/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="/images/lib/layer/layer.js?t=20170909" type="text/javascript"></script>
<script src="/images/lib/common.js?t=20171106" type="text/javascript"></script>
<div align="center">
<img style="width: 90%" src="/content/control/imageView?dataResourceId=${parameters.dataResourceId}&externalLoginKey=${externalLoginKey}">
</div>
<script type="text/javascript">
    $(window).load(function() {//使用load是因为图片加载在ready之后
        var openLayer = getLayer();
        openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
    });
</script>
