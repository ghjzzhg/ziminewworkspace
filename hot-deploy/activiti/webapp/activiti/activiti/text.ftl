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
function guid() {
    function S4() {
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    }
    return "fp_"+(S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
}
    </script>
<div class="content">
    <table class="table table-bordered table-striped table-hover">
     <tr>
        <th><span>控件名称</span><span class="label label-important">*</span></th>
        <th><span>默认值</span> </th>
    </tr>
    <tr>
        <td><input type="text" id="orgname" placeholder="必填项"></td>
        <td><input type="text" id="orgvalue" placeholder="无则不填"></td>
    </tr>
    <tr>
        <th><span>数据类型</span> </th>
        <th><span>对齐方式</span> </th>
    </tr>
    <tr>
        <td>
             <select id="orgtype">
                <option value="text">普通文本</option>
                <option value="email">邮箱地址</option>
                <option value="int">整数</option>
                <option value="float">小数</option>
                <option value="idcard">身份证号码</option>
            </select>   
        </td>
        <td>
            <select id="orgalign">
                <option value="left" >左对齐</option>
                <option value="center">居中对齐</option>
                <option value="right">右对齐</option>
            </select>
        </td>
    </tr>
    <tr>
        <th><span>&nbsp;&nbsp;&nbsp;&nbsp;长&nbsp;&nbsp;X&nbsp;&nbsp;宽&nbsp;&nbsp;&nbsp;&&nbsp;&nbsp;&nbsp;字体大小</span> </th>
        <th><span>是否必输</span> </th>
    </tr>
    <tr>
        <td>
            <input id="orgwidth" type="text" value="150" class="input-small span1" placeholder="auto"/>
            X
            <input id="orgheight" type="text" value="" class="input-small span1" placeholder="auto"/>
            &
            <input id="orgfontsize" type="text"  value="" class="input-small span1" placeholder="auto"/> px

        </td>
        <td>
            <label class="checkbox inline"><input id="orghide" type="checkbox"/> 必输 </label>
        </td>
    </tr>

    </table>
</div>
<script type="text/javascript" charset="utf-8" src="/images/lib/ueditor/formdesign/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
var oNode = null,thePlugins = 'text';
var hiddenChooser = window.parent.parent.document.getElementById("hiddenList");
window.onload = function() {
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
		var gValue = oNode.getAttribute('value').replace(/&quot;/g,"\""),gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\""),gHidden=oNode.getAttribute('orghide'),gFontSize=oNode.getAttribute('orgfontsize'),gAlign=oNode.getAttribute('orgalign'),gWidth=oNode.getAttribute('orgwidth'),gHeight=oNode.getAttribute('orgheight'),gType=oNode.getAttribute('orgtype');
		gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
		$G('orgvalue').value = gValue;
        $G('orgname').value = gTitle;
        if (gHidden == '1')
        {
            $G('orghide').checked = true;
        }
        $G('orgfontsize').value = gFontSize;
        $G('orgwidth').value = gWidth;
        $G('orgheight').value = gHeight;
        $G('orgalign').value = gAlign;
        $G('orgtype').value = gType;
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
    var gValue=$G('orgvalue').value.replace(/\"/g,"&quot;"),gTitle=$G('orgname').value.replace(/\"/g,"&quot;"),gFontSize=$G('orgfontsize').value,gAlign=$G('orgalign').value,gWidth=$G('orgwidth').value,gHeight=$G('orgheight').value,gType=$G('orgtype').value;
    var textClass = "validate[";
    if($G('orghide').checked){
        textClass += "required,"
    }
    if(gType == "email"){
        textClass += "custom[isEmail]";
    }else if(gType == "int"){
        textClass += "custom[integer]";
    }else if(gType == "float"){
        textClass += "custom[number]";
    }else if(gType == "idcard"){
        textClass += "custom[isIdCardNo]";
    }
    textClass += "]"
    if( !oNode ) {
        try {
            var id = guid();
            oNode = createElement('input',gTitle);
            oNode.setAttribute('type','text');
            oNode.setAttribute('title',gTitle);
            oNode.setAttribute('value',gValue);
            oNode.setAttribute('class',textClass);
            oNode.setAttribute('name',id);
            oNode.setAttribute('leipiPlugins',thePlugins);
            if ( $G('orghide').checked ) {
                oNode.setAttribute('orghide',1);
            } else {
                oNode.setAttribute('orghide',0);
            }
            if( gFontSize != '' ) {
                oNode.style.fontSize = gFontSize + 'px';
                //style += 'font-size:' + gFontSize + 'px;';
                oNode.setAttribute('orgfontsize',gFontSize );
            }
            if( gAlign != '' ) {
                //style += 'text-align:' + gAlign + ';';
                oNode.style.textAlign = gAlign;
                oNode.setAttribute('orgalign',gAlign );
            }
            if( gWidth != '' ) {
                oNode.style.width = gWidth+ 'px';
                //style += 'width:' + gWidth + 'px;';
                oNode.setAttribute('orgwidth',gWidth );
            }
            if( gHeight != '' ) {
                oNode.style.height = gHeight+ 'px';
                //style += 'height:' + gHeight + 'px;';
                oNode.setAttribute('orgheight',gHeight );
            }
            if( gType != '' ) {
                oNode.setAttribute('orgtype',gType );
            }
            //oNode.setAttribute('style',style );
            //oNode.style.cssText=style;//ie
            editor.execCommand('insertHtml',oNode.outerHTML);
            hiddenChooser.value = hiddenChooser.value + id + "," + gTitle + "|" + gType + ";"
        } catch (e) {
            try {
                editor.execCommand('error');
            } catch ( e ) {
            }
            return false;
        }
    } else {
        
        oNode.setAttribute('title', gTitle);
        oNode.setAttribute('value', $G('orgvalue').value);
        if( $G('orghide').checked ) {
            oNode.setAttribute('orghide', 1);
        } else {
            oNode.setAttribute('orghide', 0);
        }
        if( gFontSize != '' ) {
            oNode.style.fontSize = gFontSize+ 'px';
            oNode.setAttribute('orgfontsize',gFontSize );
        }else{
            oNode.style.fontSize = '';
            oNode.setAttribute('orgfontsize', '');
        }
        if( gAlign != '' ) {
            oNode.style.textAlign = gAlign;
            oNode.setAttribute('orgalign',gAlign );
        }else{
            oNode.setAttribute('orgalign', '');
        }
        if( gWidth != '' ) {
            oNode.style.width = gWidth+ 'px';
            oNode.setAttribute('orgwidth',gWidth );
        }else{
            oNode.style.width = '';
            oNode.setAttribute('orgwidth', '');
        }
        if( gHeight != '' ) {
            oNode.style.height = gHeight+ 'px';
            oNode.setAttribute('orgheight',gHeight );
        }else{
            oNode.style.height = '';
            oNode.setAttribute('orgheight', '');
        }
        if( gType != '' ) {
            oNode.setAttribute('orgtype',gType );
        }else{
            oNode.setAttribute('orgtype', '');
        }
        delete UE.plugins[thePlugins].editdom;
    }
};
</script>
