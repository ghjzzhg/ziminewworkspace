<script language="javascript">
    $(function () {
        freemarkerMould1 = KindEditor.create('textarea[name="freemarkerMould"]', {
            allowFileManager: true,
            items:[],
            readonlyMode:true,
            resizeType:0
        });
    });
    $(function () {
        $(".ke-toolbar").hide();
        $(".ke-statusbar").hide();
    });

    function putSendSalary(){
        showInfo("打印工资条接口");
    }
    function sendail(){
        showInfo("发送邮件");
    }
</script>
<a href="#" onclick="putSendSalary()" class="smallSubmit">打印</a>
<a href="#" onclick="sendail()" class="smallSubmit">发送邮件</a>
<div>
<#list freeMarkerList as freeMarkerList>
    <textarea name="freemarkerMould" id="freemarkerMould" style="width: 100%;height: 160px">${freeMarkerList}</textarea>
</#list>
</div>

<#--${((freemarkerMould)!'')?xhtml}-->
