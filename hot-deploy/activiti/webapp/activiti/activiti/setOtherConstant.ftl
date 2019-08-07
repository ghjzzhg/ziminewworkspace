<link rel="stylesheet" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap.css">
<!--[if lte IE 6]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/bootstrap-ie6.css">
<![endif]-->
<!--[if lte IE 7]>
<link rel="stylesheet" type="text/css" href="/images/lib/ueditor/formdesign/bootstrap/css/ie.css">
<![endif]-->
<link rel="stylesheet" href="/images/lib/ueditor/formdesign/leipi.style.css">
<script type="text/javascript" src="/images/lib/ueditor/dialogs/internal.js"></script>
<script type="text/javascript" charset="utf-8" src="/images/lib/ueditor/formdesign/jquery-1.7.2.min.js"></script>
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
    function fnDelete() {
        fnRemoveSelectedOptions( oListText ) ;
    }

    // Remove all selected options from a SELECT object
    function fnRemoveSelectedOptions( combo ) {
        // Save the selected index
        var iSelectedIndex = combo.selectedIndex ;
        var oOptions = combo.options ;
        // Remove all selected options
        for ( var i = oOptions.length - 1 ; i >= 0 ; i-- ) {
            if (oOptions[i].selected) combo.remove(i) ;
        }

        // Reset the selection based on the original selected index
        if ( combo.options.length > 0 ) {
            if ( iSelectedIndex >= combo.options.length ) iSelectedIndex = combo.options.length - 1 ;
            combo.selectedIndex = iSelectedIndex ;
        }
    }

    function fnSelect( combo ) {
        var iIndex = combo.selectedIndex ;
        oListText.selectedIndex    = iIndex ;
        var olistText    = document.getElementById( "orgtext" ) ;
        olistText.value    = oListText.value ;
    }

    function fnModify() {
        var iIndex = oListText.selectedIndex ;
        if ( iIndex < 0 ) return ;
        var olistText    = document.getElementById( "orgtext" ) ;
        oListText.options[ iIndex ].innerHTML    = fnHTMLEncode( olistText.value ) ;
        oListText.options[ iIndex ].value        = olistText.value ;
        olistText.value    = '' ;
        olistText.focus() ;
    }

    function fnMove( steps ) {
        fnChangeOptionPosition( oListText, steps ) ;
    }

    function fnSetSelectedValue() {
        var iIndex = oListText.selectedIndex ;
        if ( iIndex < 0 ) return ;
        var olistText = document.getElementById( "orgvalue" ) ;
        olistText.innerHTML = oListText.options[ iIndex ].value ;
    }

    // Moves the selected option by a number of steps (also negative)
    function fnChangeOptionPosition( combo, steps ) {
        var iActualIndex = combo.selectedIndex ;
        if ( iActualIndex < 0 ){
            return ;
        }
        var iFinalIndex = iActualIndex + steps ;
        if ( iFinalIndex < 0 ){
            iFinalIndex = 0 ;
        }
        if ( iFinalIndex > ( combo.options.length - 1 ) ) {
            iFinalIndex = combo.options.length - 1 ;
        }
        if ( iActualIndex == iFinalIndex ) {
            return ;
        }
        var oOption = combo.options[ iActualIndex ] ;
        if(oOption.value=="") {
            var sText    = fnHTMLDecode( oOption.value ) ;
        } else {
            var sText    = fnHTMLDecode( oOption.innerHTML ) ;
        }
        combo.remove( iActualIndex ) ;
        oOption = fnAddComboOption( combo, sText, sText, null, iFinalIndex ) ;
        oOption.selected = true ;
    }


    // Add a new option to a SELECT object (combo or list)
    function fnAddComboOption( combo, optionText, optionValue, documentObject, index ) {
        var oOption ;
        if ( documentObject ) {
            oOption = documentObject.createElement("option") ;
        } else {
            oOption = document.createElement("option") ;
        }
        if ( index != null ) {
            combo.options.add( oOption, index ) ;
        } else {
            combo.options.add( oOption ) ;
        }
        oOption.innerHTML = optionText.length > 0 ? fnHTMLEncode( optionText ) : '&nbsp;' ;
        oOption.value     = optionValue ;
        return oOption ;
    }

    function fnHTMLEncode( text ) {
        if ( !text ) {
            return '' ;
        }
        text = text.replace( /&/g, '&amp;' ) ;
        text = text.replace( /</g, '&lt;' ) ;
        text = text.replace( />/g, '&gt;' ) ;
        return text ;
    }


    function fnHTMLDecode( text ) {
        if ( !text ) {
            return '' ;
        }
        text = text.replace( /&gt;/g, '>' ) ;
        text = text.replace( /&lt;/g, '<' ) ;
        text = text.replace( /&amp;/g, '&' ) ;
        return text ;
    }

    function fnSetAttribute( element, attName, attValue ) {
        if ( attValue == null || attValue.length == 0 ){
            element.removeAttribute( attName, 0 ) ;
        } else {
            element.setAttribute( attName, attValue, 0 ) ;
        }
    }
    function setConstantType(v){
        $("#orglist").html("")
        var typeValue = $(v).val();
        $.ajax({
            type: 'post',
            url: "searchConstantList",
            data: {typeId:typeValue},
            async: true,
            dataType: 'json',
            success: function (content) {
                var typeList = new Array(content.typeList);
                for(var i = 0; i < typeList[0].length; i++){
                    fnAddComboOption( oListText, typeList[0][i].description,typeList[0][i].enumId) ;
                }
            }
        });
    }
    function guid() {
        function S4() {
            return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
        }
        return "fp_"+(S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
    }
    function fnAdd() {
        var olistText    = document.getElementById( "orgtext" ) ;
        fnAddComboOption( oListText, olistText.value, olistText.value ) ;
        oListText.selectedIndex = oListText.options.length - 1 ;
        olistText.value    = '' ;
        olistText.focus() ;
    }

    function setChooserValue(value,split1,oId, lastValue){
        var v = value.value;
        var values = v.split(split1);
        var valueList = "";
        var flag = false;
        console.log(values.length);
        for(var i = 0; i < values.length; i++){
            var id = values[i].substring(0,values[i].indexOf(","));
            if(oId != id && "" != values[i]){
                valueList +=  values[i] + split1
            }else {
                flag = true;
            }
        }
        value.value = valueList + lastValue;
    }
</script>
<div class="content">
    <table class="table table-bordered table-striped table-hover">
        <tr>
            <th><span>控件名称</span><span class="label label-important">*</span></th>
            <th><span>控件样式</span> </th>
        </tr>
        <tr>
            <td><input id="orgname" placeholder="必填项" type="text"/></td>
            <td> 宽：<input id="orgwidth" type="text" value="150" class="input-small span1"/> px&nbsp;&nbsp;&nbsp;&nbsp;高：<input id="orgsize" type="text" class="input-small span1" value="1"/> 行</td>
        </tr>
        <tr>
            <th><span>类型</span></th>
            <th><span>初始选定</span> </th>
        </tr>
        <tr>
            <th>
                <select id="constantType" onchange="setConstantType(this)">
                    <option value="">--请选择--</option>
                <#list typeList as type>
                    <option value="${type.enumTypeId}">${type.description?default('')}</option>
                </#list>
                </select>
            </th>
            <td> <span id="orgvalue" class="uneditable-input" style="height:20px;"></span> </td>
        </tr>
        <tr>
            <th><span>是否必选</span></th>
            <th></th>
        </tr>
        <tr>
            <th>
                <label class="checkbox inline"><input id="orghide" type="checkbox"/> 必选 </label>
            </th>
            <td> </td>
        </tr>
        <tr>
            <th colspan="2">
                <span>列表值</span> <span class="label label-important">*</span>
            </th>
        </tr>
        <tr>
            <td colspan="2">
                <select id="orglist"  multiple="multiple" class="span14"></select>
            </td>
        </tr>
        <tr>
            <td>
                <div class="btn-group pull-right">
                    <a title="新增" onclick="fnAdd();" class="btn btn-primary"><i class="icon-white icon-plus"></i></a>
                    <a title="修改" onclick="fnModify();" class="btn btn-default"><i class="icon-edit"></i></a>
                </div>
                <input type="text" placeholder="输入列表值..." class="span2" id="orgtext">
            </td>
            <td>
                <div class="btn-group">
                    <button title="上移" onclick="fnMove(-1);" class="btn btn-default"><i class="icon-arrow-up"></i></button>
                    <button title="下移" onclick="fnMove(1);" class="btn btn-default"><i class="icon-arrow-down"></i></button>
                    <button title="设为初始化时选定值" onclick="fnSetSelectedValue();" class="btn btn-default"><i class="icon-ok-circle"></i></button>
                    <button title="删除" onclick="fnDelete();" class="btn btn-default"><i class="icon-ban-circle"></i></button>
                </div>
            </td>
        </tr>
    </table>
</div>
<script type="text/javascript">
    var oNode = null,oListText='',thePlugins = 'select';
    var hiddenChooser = window.parent.parent.document.getElementById("hiddenList");
    var valueList = window.parent.parent.document.getElementById("valueList");
    window.onload = function() {
        oListText = $G('orglist');

        if( UE.plugins[thePlugins].editdom ){
            oNode = UE.plugins[thePlugins].editdom;
            var gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\""),gWidth=oNode.getAttribute('orgwidth'),gSize=oNode.getAttribute('size'),gHidden=oNode.getAttribute('orghide');
            gTitle = gTitle==null ? '' : gTitle;
            if (gHidden == '1')
            {
                $G('orghide').checked = true;
            }
            $G('orgvalue').innerHTML = oNode.value;
            $G('orgname').value = gTitle;
            $G('orgsize').value = gSize;
            $G('orgwidth').value = gWidth;
            for ( var i = 0 ; i < oNode.options.length ; i++ ) {

                var sval    = oNode.options[i].value ;
                var sText    = oNode.options[i].text ;
                if(sval != ""){
                    fnAddComboOption( oListText,sText , sval ) ;
                }
            }
        }
        /*$('#showTips').popover();*/
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
        if( oListText.options.length == 0 ) {
            alert('请添加或者选择下拉菜单选项！');
            return false;
        }
        var gSize = $G('orgsize').value ;
        if ( gSize == null || isNaN( gSize ) || gSize < 1 ) {
            gSize = '' ;
        }
        var gWidth=$G('orgwidth').value;
        var textClass = "validate[";
        if($G('orghide').checked){
            textClass += "required,"
        }

        if( !oNode ) {
            try {
                var id = guid();
                //oNode = document.createElement("select");
                oNode = createElement('select',id);
                oNode.setAttribute('title',$G('orgname').value);
                oNode.setAttribute('leipiPlugins',thePlugins );
                oNode.setAttribute('size',gSize);
                if ( $G('orghide').checked ) {
                    oNode.setAttribute('orghide',1);
                } else {
                    oNode.setAttribute('orghide',0);
                }
                if ( $G('orgwidth').value!= '' ) {
                    oNode.style.width =  $G('orgwidth').value+ 'px';
                    //oNode.setAttribute('style','width:'+ $G('orgwidth').value + 'px;');
                }
                if( gWidth != '' ) {
                    oNode.style.width = gWidth + 'px';
                    oNode.setAttribute('orgwidth',gWidth );
                }

                // Add all available options.
                fnAddComboOption( oNode, "请选择","");
                var vList = "";
                for ( var i = 0 ; i < oListText.options.length ; i++ ) {
                    var sval    = oListText.options[i].value ;
                    var sText    = oListText.options[i].text ;
                    if(sval != ""){
                        vList += sval + "=" + sText + ";"
                    }
                    if ( sval.length == 0 ) {
                        sText = sval ;
                    }

                    var oOption = fnAddComboOption( oNode, sText, sval) ;
                    if ( sval == $G('orgvalue').innerHTML ) {
                        fnSetAttribute( oOption, 'selected', 'selected' ) ;
                        oOption.selected = true ;
                    }
                }
                //firefox要利用span
                editor.execCommand('insertHtml','<span leipiplugins="select">'+oNode.outerHTML+'</span>');
                var oneChooserValue = id + "," + $G('orgname').value + "|select;"
                var twoChooserValue = id + "," + vList + "-";
                setChooserValue(hiddenChooser,";",id, oneChooserValue)
                setChooserValue(valueList,"-",id, twoChooserValue)
                return true ;
            } catch ( e ) {
                try {
                    editor.execCommand('error');
                } catch ( e ) {
                    alert('控件异常！');
                }
                return false;
            }
        } else {
            var name = oNode.getAttribute("name");
            oNode.setAttribute('title', $G('orgname').value);
            oNode.setAttribute('size',gSize);
            if( $G('orghide').checked ) {
                oNode.setAttribute('orghide', 1);
            } else {
                oNode.setAttribute('orghide', 0);
            }
            if( gWidth != '' ) {
                oNode.style.width = gWidth + 'px';
                oNode.setAttribute('orgwidth',gWidth );
            }
            // Remove all options.
            while ( oNode.options.length > 0 ){
                oNode.remove(0) ;
            }
            fnAddComboOption( oNode, "请选择","");
            var vList = "";
            for ( var i = 0 ; i < $G('orglist').options.length ; i++ ) {
                var sval    = oListText.options[i].value ;
                var sText    = oListText.options[i].text ;
                if ( sText.length == 0 ) {
                    sText = sText ;
                }
                vList += sval + "=" + sText + ";"
                var oOption = fnAddComboOption( oNode, sText, sval ) ;
                if ( sText == $G('orgvalue').innerHTML ) {
                    fnSetAttribute( oOption, 'selected', 'selected' ) ;
                    oOption.selected = true ;
                }
            }
            var oneChooserValue = name + "," + $G('orgname').value + "|select;"
            var twoChooserValue = name + "," + vList + "-";
            console.log(oneChooserValue);
            console.log(twoChooserValue);
            setChooserValue(hiddenChooser,";",name, oneChooserValue)
            setChooserValue(valueList,"-",name, twoChooserValue)
            delete UE.plugins[thePlugins].editdom;
        }
    };
</script>