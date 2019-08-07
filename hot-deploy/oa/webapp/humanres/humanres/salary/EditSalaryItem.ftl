
<script language="javascript">
    $(function () {
        $("#titleId").each(function () {
            $(this).validationEngine("attach", {promptPosition: "topLeft"});
        });
    });
    function checkAmount(){
        var amoumnt = $("#EditSalaryItem_amount").val();
        var relativeEntry = $("#EditSalaryItem_relativeEntry").val();
        if (relativeEntry != "" && amoumnt > 100){
            showError("百分比不得大于100！");
        }
        if(relativeEntry != ""){
            document.getElementById("yuan").style.display = "none"
            document.getElementById("percent").style.display = "inline-block"
        } else {
            document.getElementById("yuan").style.display = "inline-block"
            document.getElementById("percent").style.display = "none"
        }
    }
    function checkRelativeEntry(){
        var amoumnt = $("#EditSalaryItem_amount").val();
        var relativeEntry = $("#EditSalaryItem_relativeEntry").val();
        if (relativeEntry != "" && amoumnt > 100){
            showError("百分比不得大于100！");
        }
    }

</script>

<form id="EditSalaryItem" name="EditSalaryItem">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr>
            <td>
                <label><b class="requiredAsterisk">*</b>标题</label>
            </td>
            <td class="jqv">
                <input type="text" name="title" id="titleId"  maxlength="20" class="validate[required,custom[onlyLetterNumberChinese]]" value="">
            </td>
        </tr>
        <tr>
            <td>
                <label>款项类型</label>
            </td>
            <td class="jqv">
                <select id="type" name="type" class="validate[required]">
                <#if typeList?has_content>
                    <#list typeList as status>
                        <option value="${status.enumId}">${status.description}</option>
                    </#list>
                </#if>
                </select>
                <span class="tooltip">"显示"表示仅用于显示作用，已包含在应扣或应发中了</span>
            </td>
        </tr>
        <tr>
            <td>
                <label>相对条目</label>
            </td>
            <td>
                <select id="EditSalaryItem_relativeEntry" name="relativeEntry" onchange="checkAmount();">
                    <option value="">无</option>
                <#list data.relativeEntryList as status>
                    <option value="${status.entryId}">${status.title}</option>
                </#list>
                </select>
                <span class="tooltip">"是否基于其他条目计算"</span>
            </td>
        </tr>
        <tr>
            <td>
                <label><b class="requiredAsterisk">*</b>默认值</label>
            </td>
            <td class="jqv">
                <input type="text" name="amount" id="EditSalaryItem_amount" class="validate[required,custom[twoDecimalNumber]]" maxlength="20" value="" onchange="checkRelativeEntry()">
                <label id="yuan" class="tooltip" style="display: inline-block;">(元)</label>
                <label id="percent" class="tooltip" style="display: none;">(%)</label>
                <span class="tooltip">"百分比使用整数表示，例如15%填入数字15而不是0.15"</span>
            </td>
        </tr>
        <tr>
            <td>
                <label>备注</label>
            </td>
            <td>
            <textarea name="remarks" maxlength="20" cols="50"/>
            </td>
        </tr>
        <tr>
            <td>
                <a href="#" class="smallSubmit" onclick="$.salary.saveSalaryEntry()">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
<script type="text/javascript">
    $("#EditSalaryItem").validationEngine("attach", {promptPosition: "topLeft"});
</script>