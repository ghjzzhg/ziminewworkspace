<link rel="stylesheet" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap.css">
<!--[if lte IE 6]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap-ie6.css">
<![endif]-->
<!--[if lte IE 7]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/ie.css">
<![endif]-->
<link rel="stylesheet" href="/images/lib/ueditor/formdesign/leipi.style.css">
<script type="text/javascript" src="/images/lib/ueditor/dialogs/internal.js"></script>
    <script type="text/javascript">
function createElement(type, name)
{     
    var element = null;     
    try {        
        element = document.createElement('<'+type+' name="'+name+'">');     
    } catch (e) {}   
    if(element==null) {     
        element = document.createElement(type);     
        element.name = name;     
    } 
    return element;     
}
    </script>
<div class="content">
    <table class="table table-bordered table-striped table-hover">
     <tr>
        <th><span>控件名称</span><span class="label label-important">*</span></th>
        <th><span>选中状态</span></th>
    </tr>
    <tr>
        <td><input id="orgname" placeholder="必填项" type="text"/> </td>
        <td>
            <label class="radio"><input id="orgchecked0" checked="checked" name="checked" type="radio"> 不选中 </label>
            <label class="radio"><input id="orgchecked1" name="checked" type="radio"> 选中 </label>
        </td>
    </tr>
    </table>
</div>
<script type="text/javascript">
var oNode = null,thePlugins = 'checkbox';
window.onload = function() {
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
        var gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\"");
        $G('orgname').value = gTitle;
        var checked = oNode.getAttribute('checked');
        checked ? $G('orgchecked1').checked = true : $G('orgchecked0').checked = true;
    }
}
dialog.oncancel = function () {
    if( UE.plugins[thePlugins].editdom ) {
        delete UE.plugins[thePlugins].editdom;
    }
};
dialog.onok = function (){
    if( $G('orgname').value == '') {
        alert('控件名称不能为空');
        return false;
    }
    var gTitle=$G('orgname').value.replace(/\"/g,"&quot;");
    if( !oNode ) {
        try {
            oNode = createElement('input',gTitle);
            oNode.setAttribute('title',gTitle);
            oNode.setAttribute('leipiPlugins',thePlugins );
            oNode.setAttribute('type','checkbox');
            if ($G('orgchecked1').checked) {
                oNode.setAttribute('checked','checked');
            } else {
                oNode.checked = false;
            }
            editor.execCommand('insertHtml',oNode.outerHTML);
            return true ;
        } catch ( e ) {
            try {
                editor.execCommand('error');
            } catch ( e ) {
                alert('控件异常，请到 [雷劈网] 反馈或寻求帮助！');
            }
            return false;
        }
    } else {
        oNode.setAttribute('title',gTitle);
        if ($G('orgchecked1').checked) {
            oNode.setAttribute('checked','checked');
        } else {
            oNode.removeAttribute('checked');
        }
        delete UE.plugins[thePlugins].editdom; 
        return true;
    }
};
</script>