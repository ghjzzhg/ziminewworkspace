<link rel="stylesheet" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap.css">
<!--[if lte IE 6]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap-ie6.css">
<![endif]-->
<!--[if lte IE 7]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/ie.css">
<![endif]-->
<link rel="stylesheet" href="/images/lib/ueditor/formdesign/leipi.style.css">
<script type="text/javascript" src="/images/lib/ueditor/dialogs/internal.js"></script>
<div class="content">
    <table class="table table-bordered table-striped table-hover">
     <tr>
        <th><span>控件名称</span><span class="label label-important">*</span></th>
        <th><span>字体大小</span> </th>
    </tr>
    <tr>
        <td><input id="orgname" type="text" placeholder="必填项"/></td>
        <td><input id="orgfontsize" type="text"  value="" class="input-small span1" placeholder="auto"/> px</td>
    </tr>
    <tr>
        <th><span>输入框样式</span> </th>
        <th><span>是否必输</span> </th>
    </tr>
    <tr>
        <td>
            宽 <input id="orgwidth" type="text" value="300" class="input-small span1" placeholder="auto"/> px
            &nbsp;&nbsp;
            高 <input id="orgheight" type="text" value="80" class="input-small span1" placeholder="auto"/> px
        </td>
        <td> <label class="checkbox"><input id="orgrich" type="checkbox"  /> 必输 </label> </td>
    </tr>
    <tr>
        <td colspan="2">
            <label for="orgvalue">默认值</label>
            <textarea class="input-block-level" rows="3" id="orgvalue" placeholder="多行文本框默认值..."></textarea>
        </td>
    </tr>
    </table>
</div>
<script type="text/javascript">
var oNode = null,thePlugins = 'textarea';
var hiddenChooser = window.parent.parent.document.getElementById("hiddenList");
window.onload = function() {
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
        var gValue = oNode.getAttribute('value').replace(/&quot;/g,"\""),gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\""),gFontSize=oNode.getAttribute('orgfontsize'),gWidth=oNode.getAttribute('orgwidth'),gHeight=oNode.getAttribute('orgheight'),gRich=oNode.getAttribute('orgrich');
        
        gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
        $G('orgvalue').value = gValue;
        $G('orgname').value = gTitle;
        if ( gRich == '1' ) {
            $G('orgrich').checked = true ;
        }
        $G('orgfontsize').value = gFontSize;
        $G('orgwidth').value = gWidth;
        $G('orgheight').value = gHeight;
    }
}
dialog.oncancel = function () {
    if( UE.plugins[thePlugins].editdom ) {
        delete UE.plugins[thePlugins].editdom;
    }
};
dialog.onok = function (){
    if($G('orgname').value==''){
        alert('请输入控件名称');
        return false;
    }
    var gValue=$G('orgvalue').value.replace(/\"/g,"&quot;"),gTitle=$G('orgname').value.replace(/\"/g,"&quot;"),gFontSize=$G('orgfontsize').value,gWidth=$G('orgwidth').value,gHeight=$G('orgheight').value;

    if( !oNode ) {
        var textClass = "";
        if($G('orgrich').checked){
            textClass += "validate[required]"
        }
        try {
            var id = guid();
            var html = '<textarea ';
            html += ' title = "' + gTitle + '"';
            html += ' name = "' + id + '"';
            html += ' class = "' + textClass + '"';
            html += ' leipiPlugins = "'+thePlugins+'"';
            html += ' value = "' + gValue + '"';
            if ( $G('orgrich').checked ) {
                html += ' orgrich = "1"';
            } else {
                html += ' orgrich = "0"';
            }
            if( gFontSize != '' ) {
                html += ' orgfontsize = "' + gFontSize + '"';
            } else {
                html += ' orgfontsize = ""';
            }
            if( gWidth != '' ) {
                html += ' orgwidth = "' + gWidth + '"';
            } else {
                html += ' orgwidth = ""';
            }
            if(gHeight != '') {
                html += ' orgheight = "' + gHeight + '"';
            } else {
                html += ' orgheight = ""';
            }
            
            html += ' style = "';
            if( gFontSize != '' ) {
                html += 'font-size:' + gFontSize + 'px;';
            }
            if( gWidth != '' ) {
                html += 'width:' + gWidth + 'px;';
            }
            if( gHeight != '' ) {
                html += 'height:' + gHeight + 'px;';
            }
            html += '">';
            html += gValue + '</textarea>';
            editor.execCommand('insertHtml',html);
            hiddenChooser.value = hiddenChooser.value + id + "," + gTitle + "|text;"
        } catch (e) {
            try {
                editor.execCommand('error');
            } catch ( e ) {
                alert('控件异常，请到 [雷劈网] 反馈或寻求帮助！');
            }
            return false;
        }
    } else {
        oNode.setAttribute('title', gTitle);
        oNode.setAttribute('value',gValue);
        oNode.innerHTML = gValue;
        if( $G('orgrich').checked ) {
            oNode.setAttribute('orgrich', 1);
        } else {
            oNode.setAttribute('orgrich', 0);
        }
        
        if( gFontSize != '' ) {
            oNode.style.fontSize = gFontSize+ 'px';
            oNode.setAttribute('orgfontsize',gFontSize );
        }else{
            oNode.setAttribute('orgfontsize', '');
        }
        if( gWidth != '' ) {
            oNode.style.width = gWidth+ 'px';
            oNode.setAttribute('orgwidth',gWidth );
        }else{
            oNode.setAttribute('orgwidth', '');
        }
        if( gHeight != '' ) {
            oNode.style.height = gHeight+ 'px';
            oNode.setAttribute('orgheight',gHeight );
        }else{
            oNode.setAttribute('orgheight', '');
        }
        delete UE.plugins[thePlugins].editdom;
    }
};

function guid() {
    function S4() {
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    }
    return "fp_"+(S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
}
</script>