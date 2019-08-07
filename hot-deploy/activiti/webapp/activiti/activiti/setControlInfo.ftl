<script language="javascript">
    var editors;
    $("#controlName").data("promptPosition", "bottomLeft")
    function setControlInfo(){
        var addHtml = "";
        var type = "${parameters.type?default('')}";
        if(type == "button"){
            addHtml = '<input type="button" id="buttonTest" name="'
                    + $("#controlName").val() +'" value="'+ $("#controlValue").val() +'">';
        }else if (type == "text"){
            addHtml = '<input type="text" name="'
                    + $("#controlName").val() +'">';
        }else if (type == "select"){
            addHtml = '<select name="' + $("#controlName").val() + '"><option>--请选择--</option></select>';
        }else if (type == "checkBox"){
            addHtml = '<input type="checkbox" name="' + $("#controlName").val() + '">'
        }else if (type == "radio"){
            addHtml = '<input type="radio" name="' + $("#controlName").val() + '">';
        }else if (type = 'textarea'){
            addHtml = '<textarea name="' + $("#controlName").val() + '">';
        }
        editors.execCommand('inserthtml',addHtml);
        closeCurrentTab();
    }
</script>
<form name="childPlanForm" id="childPlanForm" class="basic-form">
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label><b class="requiredAsterisk">*</b>控件名称:</label>
                </td>
                <td class="jqv">
                    <input type="text" class="validate[required,custom[onlyLetterNumber]]" id="controlName" name="controlName"/>
                </td>
                <td class="label">
                    <label>默认值:</label></td>
                <td>
                    <input type="text" name="controlValue" id="controlValue"/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    数据类型
                </td>
                <td>
                    <select id="controlType">
                        <option value="text">普通文本</option>
                        <option value="email">邮箱地址</option>
                        <option value="int">整数</option>
                        <option value="float">小数</option>
                        <option value="idcard">身份证号码</option>
                    </select>
                </td>

                <td class="label">
                    <label for="WorkReport_d" id="WorkReport_d_title">对齐方式</label>
                </td>
                <td>
                    <select id="orgalign">
                        <option value="left">左对齐</option>
                        <option value="center">居中对齐</option>
                        <option value="right">右对齐</option>
                    </select>
                </td>

            </tr>

            <tr>
                <td class="label">
                    <label for="WorkReport_e"
                           id="WorkReport_e_title">长  X  宽   &   字体大小</label>
                </td>
                <td class="jqv" >
                    <input type="text" class="validate[required,custom[onlyNumberSp],maxSize[3]]" size="3"> X <input type="text" class="validate[required,custom[onlyNumberSp],maxSize[3]]" size="5"> & <input type="text" class="validate[required,custom[onlyNumberSp],maxSize[3]]" size="5">
                </td>
                <td class="label">
                    <label for="WorkReport_f" id="WorkReport_f_title"><b class="requiredAsterisk">*</b>可见性</label></td>
                <td>
                    <input id="orghide" type="checkbox"/>隐藏
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div align="center">
        <a href="#" onclick="setControlInfo()" class="smallSubmit">保存</a>
    </div>
</form>