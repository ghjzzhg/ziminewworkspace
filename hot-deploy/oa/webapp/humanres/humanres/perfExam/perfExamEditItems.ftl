<#if perfExamItemsList?has_content>
<#assign lastType=""/>
<#list perfExamItemsList as item>
    <#assign rowSpan = typeCountMap[item.type]?default("1")/>
<tr>
    <#if !lastType?has_content || item.type != lastType>
    <td align="center" rowspan="${rowSpan}">
        <b>${delegator.findOne("TblPerfExamItemType", {"typeId" : item.type}, true).get("description")}</b>
    </td>
    </#if>
    <td>${item.title}</td>
    <td align="center">
    ${item.score}
        <input type="hidden" id="${item.itemId}_score" value="${item.score}">
    </td>
    <td>
        <div id="${item.itemId}" class="score-ruler" name="${item.itemId}" title="${item.title}"></div>
    </td>
    <input id="${item.itemId}_lv1" type="hidden" value="${item.score1}">
    <input id="${item.itemId}_lv2" type="hidden" value="${item.score2}">
    <input id="${item.itemId}_lv3" type="hidden" value="${item.score3}">
    <input id="${item.itemId}_lv4" type="hidden" value="${item.score4}">
    <td>
        <input id="${item.itemId}_slider" class="slider_value" name="${item.itemId}_value" type="text" value="0" size="3" maxlength="3" readonly=""
               onclick="alert('请通过左边滑块改变分值');">
    </td>
    <td>
        <input type="text" style="width:100%; border: none; background-color:transparent;"
               id="${item.itemId}_remark" value="${item.score1}" readonly>
    </td>
</tr>
<script type="text/javascript">
    standardSliderInit("${item.itemId}");
</script>
<#assign lastType=item.type/>
</#list>
</#if>
