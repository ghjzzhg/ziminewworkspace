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
        var vList = "";
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


//checkboxs
function isIE()
{
    if(window.attachEvent){   
        return true;
    }
    return false;
}
//moveRow在IE支持而在火狐里不支持！以下是扩展火狐下的moveRow
if (!isIE()) {
    function getTRNode(nowTR, sibling) {
        while (nowTR = nowTR[sibling]) if (nowTR.tagName == 'TR') break;
        return nowTR;
    }
    if (typeof Element != 'undefined') {
        Element.prototype.moveRow = function(sourceRowIndex, targetRowIndex) //执行扩展操作
        {
            if (!/^(table|tbody|tfoot|thead)$/i.test(this.tagName) || sourceRowIndex === targetRowIndex) return false;
            var pNode = this;
            if (this.tagName == 'TABLE') pNode = this.getElementsByTagName('tbody')[0]; //firefox会自动加上tbody标签，所以需要取tbody，直接table.insertBefore会error
            var sourceRow = pNode.rows[sourceRowIndex],
            targetRow = pNode.rows[targetRowIndex];
            if (sourceRow == null || targetRow == null) return false;
            var targetRowNextRow = sourceRowIndex > targetRowIndex ? false: getTRNode(targetRow, 'nextSibling');
            if (targetRowNextRow === false) pNode.insertBefore(sourceRow, targetRow); //后面行移动到前面，直接insertBefore即可
            else { //移动到当前行的后面位置，则需要判断要移动到的行的后面是否还有行，有则insertBefore，否则appendChild
                if (targetRowNextRow == null) pNode.appendChild(sourceRow);
                else pNode.insertBefore(sourceRow, targetRowNextRow);
            }
        }
    }
}

/*删除tr*/
function fnDeleteRow(obj)
{
    var oTable = document.getElementById("options_table");
    while(obj.tagName !='TR')
    {
        obj = obj.parentNode;
    }
    oTable.deleteRow(obj.rowIndex);
}
/*上移*/
function fnMoveUp(obj)
{
    var oTable = document.getElementById("options_table");
    while(obj.tagName !='TR')
    {
        obj = obj.parentNode;
    }
    var minRowIndex = 1;
    var curRowIndex = obj.rowIndex;
    if(curRowIndex-1>=minRowIndex)
    {
        oTable.moveRow(curRowIndex,curRowIndex-1); 
    }
    
}
/*下移*/
function fnMoveDown(obj)
{
    var oTable = document.getElementById("options_table");
    while(obj.tagName !='TR')
    {
        obj = obj.parentNode;
    }
    var maxRowIndex = oTable.rows.length;
    var curRowIndex = obj.rowIndex;
    if(curRowIndex+1<maxRowIndex)
    {
        oTable.moveRow(curRowIndex,curRowIndex+1); 
    }
}

/*生成tr*/
function fnAddComboTr(gName,obj)
{
    var oTable = document.getElementById('options_table');
    var new_tr_node= oTable.insertRow(oTable.rows.length);
    var new_td_node0 = new_tr_node.insertCell(0),new_td_node1 = new_tr_node.insertCell(1),new_td_node2 = new_tr_node.insertCell(2);

    var sChecked = '';
    if(obj.checked) sChecked = 'checked="checked"';
    if(!obj.name) obj.name = '';
    if(!obj.value) obj.value = '';
    new_td_node0.innerHTML = '<td><input type="radio" '+sChecked+' name="'+gName+'"></td>';
    new_td_node1.innerHTML = '<td><input type="text" value="'+obj.value+'" name="'+gName+'" placeholder="选项值"></td>';
    new_td_node2.innerHTML ='<td><div class="btn-group"><a title="上移" class="btn btn-small btn-info" href="javascript:void(0);" onclick="fnMoveUp(this)"><i class="icon-white icon-arrow-up"></i></a><a title="下移" class="btn btn-small btn-info" href="javascript:void(0);" onclick="fnMoveDown(this)"><i class="icon-white icon-arrow-down"></i></a><a title="删除" class="btn btn-small btn-default" href="javascript:void(0);" onclick="fnDeleteRow(this)"><i class="icon-ban-circle"></i></a></div></td>';
    return true;
}
function fnAdd() {
    var dName = $G('hidname').value;
    if(!dName) dName = '1';
    fnAddComboTr(dName,{
        "checked":false,
        "name":'leipiNewField',
        "value":''
    });
}
/*组合checkbox*/
function fnParseOptions(gName,gChecked)
{
    var oTable = document.getElementById('options_table');
    var nTr = oTable.getElementsByTagName('tr'),trLength = nTr.length,html="";
    for(var i=0;i<trLength;i++)
    {
        var inputs = nTr[i].getElementsByTagName('input');

        if(inputs.length>0)
        {
            if(!inputs[1].value) continue;
            var sChecked = '';
            if(inputs[0].checked) sChecked = 'checked="checked"';
            html += '<input name="'+gName+'" value="'+inputs[1].value+'" '+sChecked+' type="radio"/>'+inputs[1].value+'&nbsp;';
            vList += inputs[1].value + "=" + inputs[1].value + ";"
            if(gChecked=='orgchecked1')//竖排
                html+='<br/>';
        }
    }
    //alert(html);
    return html;

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
    <input id="hidname"  type="hidden"/>
    <table class="table table-bordered table-striped">
     <tr>
        <th><span>控件名称</span><span class="label label-important">*</span></th>
        <th><span>排列方式</span></th>
    </tr>
    <tr>
        <td><input id="orgname" placeholder="必填项" type="text"/> </td>
        <td>
            <label class="radio" title="选项一 选项二"><input id="orgchecked0" checked="checked" name="checked" type="radio"> 横排 </label>
            <label class="radio" title="选项一&#10;选项二"><input id="orgchecked1" name="checked" type="radio" > 竖排 </label>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table class="table table-hover table-condensed" id="options_table">
                <tr>
                    <th>选中</th>
                    <th>选项值</th>
                    <th>操作</th>
                </tr>
                <!--tr>
                    <td><input type="checkbox" checked="checked"></td>
                    <td><input type="text" value="选项一"></td>
                    <td>
                        <div class="btn-group">
                            <a title="上移" class="btn btn-small btn-info" href="#"><i class="icon-white icon-arrow-up"></i></a>
                            <a title="下移" class="btn btn-small btn-info" href="#"><i class="icon-white icon-arrow-down"></i></a>
                            <a title="删除" class="btn btn-small btn-default"><i class="icon-ban-circle"></i></a>
                        </div>
                    </td>
                </tr-->

            </table>
            <a title="添加选项" class="btn btn-primary" onclick="fnAdd();">添加选项</a>
        </td>
    </tr>


    </table>
</div>
<script type="text/javascript">
var oNode = null,thePlugins = 'radios';
var hiddenChooser = window.parent.parent.document.getElementById("hiddenList");
var valueList = window.parent.parent.document.getElementById("valueList");
window.onload = function() {
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
        var gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\"");
        var gName=oNode.getAttribute('name').replace(/&quot;/g,"\"");
        $G('orgname').value = gTitle;
        $G('hidname').value = gName;
        var checked = oNode.getAttribute('orgchecked');
        checked=='orgchecked1' ? $G('orgchecked1').checked = true : $G('orgchecked0').checked = true;

        var inputTags = oNode.getElementsByTagName('input');
        var length = inputTags.length;
        var aInputs = [];
        for(var i=0;i<length;i++)
        {
            //testEle.setAttribute("test","aaa"); // 自定义属性 设置  
            //testEle.attributes["test"].nodeValue; // 获得 
            if(inputTags[i].type =='radio')
                fnAddComboTr(gName,inputTags[i]);
        }
        

    }
}
dialog.oncancel = function () {
    if( UE.plugins[thePlugins].editdom ) {
        delete UE.plugins[thePlugins].editdom;
    }
};
dialog.onok = function (){
    if( $G('orgname').value == '') {
        alert('控件名称不能为空！');
        return false;
    }
    var gTitle=$G('orgname').value.replace(/\"/g,"&quot;");

    var gChecked = 'orgchecked0';
    if ($G('orgchecked1').checked) gChecked = 'orgchecked1';
        

    if( !oNode ) {
        try {
            var id = guid();
            vList = "";
            var options = fnParseOptions(id,gChecked);
            console.log(options);
            if(!options)
            {
                alert('请添加选项');
                return false;
            }
            //{|- 使用边界，防止用户删除了span标签
            var html = '<span leipiplugins="radios" title="'+gTitle+'" name="'+gTitle+'">';
                html +=options;
                html +='</span>';
            editor.execCommand('insertHtml',html);
            var oneChooserValue = id + "," + $G('orgname').value + "|select;"
            var twoChooserValue = id + "," + vList + "-";
            setChooserValue(hiddenChooser,";",id, oneChooserValue)
            setChooserValue(valueList,"-",id, twoChooserValue)
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
        var gName=oNode.getAttribute('name').replace(/&quot;/g,"\"");
        oNode.setAttribute('title',gTitle);
        oNode.setAttribute('orgchecked',gChecked);
        oNode.innerHTML = fnParseOptions(gName,gChecked);
        var oneChooserValue = gName + "," + $G('orgname').value + "|select;"
        var twoChooserValue = gName + "," + vList + "-";
        setChooserValue(hiddenChooser,";",gName, oneChooserValue)
        setChooserValue(valueList,"-",gName, twoChooserValue)
        delete UE.plugins[thePlugins].editdom; 
        return true;
    }
};
function guid() {
    function S4() {
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    }
    return "fp_"+(S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
}
</script>