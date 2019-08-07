<link rel="stylesheet" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap.css">
<!--[if lte IE 6]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap-ie6.css">
<![endif]-->
<!--[if lte IE 7]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/ie.css">
<![endif]-->
<link rel="stylesheet" href="/images/lib/ueditor/formdesign/leipi.style.css">
<script type="text/javascript" src="/images/lib/ueditor/dialogs/internal.js"></script>

<div class="well">
    <div class="alert alert-info">
        控件列表
    </div>
    <p>
        <button type="button" onclick="leipiDialog.exec('text');" class="btn btn-info btn-small">单行输入框</button>
        <button type="button" onclick="leipiDialog.exec('textarea');" class="btn btn-info btn-small">多行输入框</button>
        <button type="button" onclick="leipiDialog.exec('select');" class="btn btn-info btn-small">下拉菜单</button>
        <#--<button type="button" onclick="leipiDialog.exec('select');" class="btn btn-info btn-small">下拉菜单</button>-->
        <button type="button" onclick="leipiDialog.exec('radios');" class="btn btn-info btn-small">单选框</button>
        <button type="button" onclick="leipiDialog.exec('checkboxs');" class="btn btn-info btn-small">复选框</button>
        <button type="button" onclick="leipiDialog.exec('department');" class="btn btn-info btn-small">部门</button>
        <button type="button" onclick="leipiDialog.exec('post');" class="btn btn-info btn-small">岗位</button>
        <button type="button" onclick="leipiDialog.exec('personnel');" class="btn btn-info btn-small">人员</button>
        <button type="button" onclick="leipiDialog.exec('date');" class="btn btn-info btn-small">日期</button>
    </p>
    <br />
</div>
<script type="text/javascript">
    var leipiDialog = {
        exec : function (method) {
            editor.execCommand(method);
            dialog.close(true);
        }
    };
</script>