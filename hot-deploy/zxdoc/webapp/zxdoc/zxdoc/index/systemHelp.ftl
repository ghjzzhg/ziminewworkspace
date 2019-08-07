<script type="text/javascript" src="/zxdoc/static/index/js/jquery.min.js"></script>
<script type="text/javascript" src="/zxdoc/static/OfficeControlFunctions.js?t=20161208"></script>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<script language="javascript" src="/zxdoc/static/capture/niuniucapture.js?v=20151108"></script>
<script language="javascript">
    <#assign hostUrl = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "host-url")>
    $(function () {
        $.getScript("/zxdoc/static/ntkoofficecontrol.js?t=" + (new Date()).getTime(), function (data, textStatus, jqxhr) {
            try {

<#outputformat "UNXML">
                intializePage('${hostUrl}content/control/imageView?fileName=helpFile1&externalLoginKey=' + getExternalLoginKey());
</#outputformat>
            } catch (e) {//控件出现错误改为pdf查看
                console.log(e);
            }
        });
    })

    function DocumentOpened(a) {
        OFFICE_CONTROL_OBJ.toolbars = false;
        //        OFFICE_CONTROL_OBJ.PrintPreview();
    }
</script>
<script language="JScript" for="TANGER_OCX" event="OnDocumentOpened(TANGER_OCX_str,TANGER_OCX_obj)">
    DocumentOpened();
</script>
<div id="docWindow">
    <div class="portlet light bordered">
        <div class="portlet-body" id="workspace">
            <div id="ntko-container" class="ntko-container"
                 style="clear:left;height:600px;z-index:-100;position: relative">

            </div>
        </div>
    </div>
</div>