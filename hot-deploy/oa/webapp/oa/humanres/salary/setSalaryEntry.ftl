<script language="javascript">
    var urlValue = "";
    $(function() {
        var valueArr = new Array();
        <#if salaryInfo.salaryId?has_content>
            $("#salaryId").val('${salaryInfo.salaryId}');
        </#if>
        <#if salaryInfo.salaryHistoryList?has_content>
            <#list salaryInfo.salaryHistoryList as list>
                valueArr.push('${list.entryId}-${list.newAmount}');
            </#list>
        </#if>
        if(null != valueArr && valueArr.length > 0){
            $("input[name='subBox']").each(function () {
                var qid = $(this).attr("id");
                var id = qid.substr(qid.indexOf("_") + 1, qid.length);
                for(var i = 0;i < valueArr.length; i++){
                    var value = valueArr[i];
                    var entryId = value.substr(0,value.indexOf("-"));
                    var newAmount = value.substr(value.indexOf("-")+1,value.length);
                    if(id == entryId){
                        $("#checkBox_"+id).attr("checked",true);
                        $("#amout_"+id).val(newAmount);
                    }
                }
            });
        }else{
            $("input[name='subBox']").each(function () {
                var qid = $(this).attr("id");
                var id = qid.substr(qid.indexOf("_") + 1, qid.length);
                var useId = $("#use_"+id).val();
                if(useId == "1"){
                    $("#checkBox_"+id).attr("checked",true);
                }
            });
        }
        $("#checkAll").click(function() {
            $('input[name="subBox"]').attr("checked",this.checked);
        });
        var $subBox = $("input[name='subBox']");
        $subBox.click(function(){
            $("#checkAll").attr("checked",$subBox.length == $("input[name='subBox']:checked").length ? true : false);
        });
    });
</script>
<input type="hidden" id="salaryId">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <input type="checkbox" id="checkAll">
        </td>
        <td>
            <label>标题</label>
        </td>
        <td>
            <label>款项类型</label>
        </td>
        <td>
            <label>相对条目</label>
        </td>
        <td>
            <label>上次数据</label>
        </td>
        <td>
            <label>默认值</label>
        </td>
    </tr>
    <#if salaryInfo.salaryItemsList?has_content>
        <#list salaryInfo.salaryItemsList as line>
        <tr<#if alt_row> class="alternate-row"</#if>>
            <td align="center">
                <input type="checkbox" name="subBox" id="checkBox_${line.entryId}">
                <input type="hidden" id="use_${line.entryId}" value="${line.allUseEntry}">
                <input type="hidden" id="entryId_${line.entryId}" value="${line.entryId}">
            </td>
            <td>
                ${line.title?default("")}
            </td>
            <td>
                ${line.typeFor?default("")}
            </td>
            <td>
                ${line.relativeEntryFor?default("")}
            </td>
            <td>
                ${line.oldAmount?default("")}
            </td>
            <td>
                <input type="text" id="amout_${line.entryId}" value="${line.amount?default("")}">
            </td>
        </tr>
        </#list>
    </#if>
    </tbody>
</table>

<table cellspacing="0">
    <tbody>
    <tr class="header-row-2">
        <td class="label">
            <label >生效日期:</label>
        </td>
        <td>
            <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
            value="${salaryInfo.startTime?default(.now)}" size="25" maxlength="30" id="startTime" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
        </td>
        <td class="label">
            <label >调整日期:</label>
        </td>
        <td>
        <@htmlTemplate.renderDateTimeField name="adjustmentTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
        value="${salaryInfo.adjustmentTime?default(.now)}" size="25" maxlength="30" id="adjustmentTime" dateType="" shortDateInput=true timeDropdownParamName=""
        defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
        timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
        </td>
    </tr>
    <tr class="header-row-2">
        <td class="label">
            <label >备注:</label>
        </td>
        <td colspan="3">
            <textarea id="salaryText">${salaryInfo.remarks?default('')}</textarea>
        </td>
        <td class="label">
            <label >录入人:</label>
        </td>
        <td>
            ${salaryInfo.loginName}
            <input id="loginId" type="hidden" value="${salaryInfo.inputId}">
        </td>
    </tr>
   <#-- <tr class="header-row-2">

    </tr>-->
    </tbody>
</table>
</html>
