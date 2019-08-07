<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>

<script language="javascript">
    $(function () {
        $("[id^='salaryList_']").each(function () {
            $(this).validationEngine("attach", {promptPosition: "topLeft"});
        });
        $("#item").validationEngine("attach", {promptPosition: "topLeft"});
    });

    function delSalaryItem(id){
        $("#"+id).remove();
    }
</script>
<div class="button-bar">
    <#if data.status == "SEND_TYPE_NOTSEND"  || data.status == "SEND_TYPE_DISAPPROVE">
        <a class="buttontext create" href="javascript:$.salary.saveSendDetail('','${data.status?default('')}')">保存为草稿</a>
        <a class="buttontext create" href="javascript:$.salary.saveSendDetail('SEND_TYPE_NOTEXAMINE','${data.status?default('')}');">提交审核</a>
        <label style="tooltip" text="'实发工资'不需要填写，保存或提交审核之后系统会自动结算"></label>
    </#if>
    <#if data.status == "SEND_TYPE_NOTEXAMINE">
        <a class="buttontext create" href="javascript:$.salary.submitSendDetail('SEND_TYPE_DISAPPROVE','${data.sendId?default("")}');">驳回</a>
        <a class="buttontext create" href="javascript:$.salary.submitSendDetail('SEND_TYPE_APPROVE','${data.sendId?default("")}');">通过</a>
    </#if>
    <#if data.status == "SEND_TYPE_APPROVE">
        <a class="buttontext create" href="javascript:$.salary.submitSendDetail('SEND_TYPE_SEND','${data.sendId?default("")}')"> 确认已发</a>
        <a class="buttontext create" href="javascript:showInfo('打印')">打印</a>
        <a class="buttontext create" href="">发送邮件至员工邮箱</a>
    </#if>
</div>

<form name="submitpersonForm" id="submitpersonForm" class="basic-form">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody id="salaryEntrysList">
    <tr class="header-row-2">
        <td>
            <label>标题</label>
            <input type="hidden" value="${data.partyId}" id="partyId">
            <input type="hidden" value="${data.year}" id="year">
            <input type="hidden" value="${data.month}" id="month">
            <input type="hidden" value="${data.sendId?default("")}" id="sendId">
        </td>
        <td>
            <label>款项类型</label>
        </td>
        <td>
            <label>相对条目</label>
        </td>
        <#--<td>-->
            <#--<label>上月数值</label>-->
        <#--</td>-->
        <td>
            <label>数值</label>
        </td>
        <td>
            <label>备注</label>
        </td>
        <td>操作</td>
    </tr>
    <#if data.salaryHistoryList?has_content>
        <#list data.salaryHistoryList as line>
        <tr class="validationEngineContainer" id="salaryList_${line.entryId}">
            <td>
                ${line.title?default("")}
            </td>
            <td>
                ${line.typeName?default("")}
            </td>
            <td>
                ${line.relativeCategory?default("")}
            </td>
            <td  class="jqv">
                <#if data.status == "SEND_TYPE_NOTSEND"  || data.status == "SEND_TYPE_DISAPPROVE">
                    <input type="text" size="10" maxlength="10" value="${line.newAmount?default("")}" class="validate[required,custom[twoDecimalNumber]]" name="amount_${line.entryId}:${line.id?default("")}">
                </#if>
                <#if data.status != "SEND_TYPE_NOTSEND" && data.status != "SEND_TYPE_DISAPPROVE">
                    ${line.newAmount?default("")}
                </#if>
                <#if line.oldAmount?has_content>
                    (${line.oldAmount?default("")})
                </#if>
            </td>
            <td>
                ${line.remarks?default("")}
            </td>
            <td>
            </td>
        </tr>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.status != "SEND_TYPE_NOTSEND"  && data.status != "SEND_TYPE_DISAPPROVE">
    <div style="text-align: right">
        总计：<input type="text" id="salaryParty" size="15" disabled value="${data.paySalary?default("")}" style="color: #D60000">
    </div>
</#if>
<#if data.status == "SEND_TYPE_NOTSEND"  || data.status == "SEND_TYPE_DISAPPROVE">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr class="validationEngineContainer" id="item">
            <td><b>添加条目</b></td>
            <td>
                <#assign salaryEntryList = delegator.findByAnd("TblSalaryEntry",{"allUseEntry" :"2"},null,false)/>
                    <select id="entryList">
                    <#list salaryEntryList as list>
                        <option value="${list.entryId}-${list.type}">${list.title?default("")}</option>
                    </#list>
                </select>
            </td>
            <td class="jqv">
                <input type="text" id="salaryValue"  class="validate[custom[twoDecimalNumber]]">
            </td>
            <td>
                <a href="#" onclick="addSalaryItem()" class="smallSubmit">添加</a>
            </td>
            <td></td>
        </tr>
        </tbody>
    </table>
</#if>
</html>
<script language="JavaScript">
    var send;
    var num = 0;
    function addSalaryItem(){
        var submitperson =  $('#submitpersonForm').validationEngine('validate');
        if(submitperson){
            num = num + 1;
            var entry = $("#entryList").val();
            var entryId = entry.substr(0,entry.indexOf("-"));
            var entryType = entry.substr(entry.indexOf("-")+1,entry.length);
            var salaryValue = $("#salaryValue").val();
            var entityName = $("#entryList option:selected").text();
            var entryTypeName = "";
            if("SEND" == entryType){
                entryTypeName = "应发";
            }else{
                entryTypeName = "应扣";
            }
            $("#salaryEntrysList").append('<tr class="validationEngineContainer" id="trSalary_'+num+'"><td><label>'
                    + entityName + '</label></td><td><label>' + entryTypeName + '</label></td><td><label></label></td><td class="jqv">' +
                    '<input type="text"  class="validate[required,custom[twoDecimalNumber]]" name="amount_'+entryId+':" value="' + salaryValue + '"></td><td></td><td>' +
                    ' <a class="buttontext create" href="javascript:delSalaryItem('+"'trSalary_"+num+"'"+')">删除</a></td></tr>');
        }
    }
</script>
</form>
