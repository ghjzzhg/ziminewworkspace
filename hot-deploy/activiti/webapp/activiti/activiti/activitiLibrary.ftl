<#macro createFieldHtml prop className warnContent>
    <td width='20%' class='fmLbl'>${prop.name}${warnContent}</td>
    <td class='fmData'><input type='text' id='${prop.id}' name='fp_${prop.id}' class='${className}'/>
</#macro>
<#macro createStringField prop className warnContent>
    <td width='20%' class='fmLbl'>${prop.name}${warnContent}</td>
    <td class='fmData'><input type='text' id='${prop.id}' name='fp_${prop.id}' class='${className}'/>
</#macro>
<#macro createDateField prop className warnContent>
    <td width='20%' class='fmLbl'>${prop.name}${warnContent}</td>
    <td class='fmData'><input type='text' id='${prop.id}' name='fp_${prop.id}' class='datepicker ${className}'/>
</#macro>