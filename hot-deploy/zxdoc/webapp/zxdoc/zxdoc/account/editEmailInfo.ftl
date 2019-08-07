<#include "component://common/webcommon/includes/uiWidgets/kindeditor.ftl"/>
<script type="text/javascript">
    var s;
    $(function(){
        s = KindEditor.create('textarea[name="info"]');
        $("[data-name='image']").remove();
        $("[data-name='table']").remove();
        $("[data-name='clearhtml']").remove();
        $("[data-name='template']").remove();
        $("[data-name='code']").remove();
        $("[data-name='emoticons']").remove();
        $("[data-name='baidumap']").remove();
        $("[data-name='anchor']").remove();
    });

    //保存邮件编辑
    function saveEmailInfo() {
        var c = s.html();
        $("#info").val(c)
        var options = {
            url: "saveEmailInfo",
            type: "post",
            dataType: "json",
            success: function(content) {
                showInfo("编辑成功");
                closeCurrentTab();
            }
        }
        $("#form1").ajaxSubmit(options);
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">邮件编辑</span>
        </div>
    </div>
    <div class="portlet-body">
        <form id="form1">
        <input name="emailId" id="emailId" value="${emailId!}" type="hidden">
        <table class="table table-hover table-striped table-bordered">
            <tr>
                <td style="min-width: 100px;">
                    <label class="control-label">当前邮件内容</label>
                </td>
                <td>
                    <div>
                        <textarea name="info" id="info" class="form-control" rows="20">${html!}</textarea>
                    </div>
                </td>
            </tr>
        </table>
        <div class="form-group" align="center">
            <div class="margiv-top-10">
                <a onclick="saveEmailInfo()" class="btn green"> 提交 </a>
            </div>
        </div>
        </form>
    </div>
</div>
<div class="note note-info">
<pre>
    温馨提示

    1.请在完成编辑后进行必要的审查
    2.<span style="color: red">如果出现$ {} <>类似标识以及相关代码，可以移动位置，不要进行编辑、删除以及截断，否则会造成邮件信息不全等各类问题</span>
    3.为了您有更好的体验，请不要上传图片和文件！
</pre>
</div>