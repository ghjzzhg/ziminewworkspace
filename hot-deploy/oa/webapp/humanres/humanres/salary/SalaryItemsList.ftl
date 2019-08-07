<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.salaryItemsList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign commonUrl = "SalaryItems"/>
    <#assign param = ""/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<script language="javascript">
    $(function () {
        $("[id^='alternate-row_']").each(function () {
            $(this).validationEngine("attach", {promptPosition: "topLeft"});
        });
    });

    function editSalaryEntry(b){
        if($('#alternate-row_' + b).validationEngine('validate')){
            var entryId =b;
            var title = $("#title_"+b).val();
            var type = $("#type_"+b).val();
            var relativeEntry = $("#relativeEntry_"+b).val();
            var amount = $("#amount_"+b).val();
            var remarks = $("#remarks_"+b).val();
            $.ajax({
                type: 'POST',
                url: 'editSalaryEntry',
                async: true,
                dataType: 'json',
               data:{entryId:entryId,title:title,type:type,relativeEntry:relativeEntry,amount:amount,remarks:remarks},
                success: function (data) {
                    if(data.data.msg == "FAILED"){
                        showError("相对条目顶层出现死循环");
                    } else {
                        showInfo("保存成功");
                    }

                }
           });
        }
    }
</script>
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>序号</label>
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
            <label>默认值(元)</label>
        </td>
        <td>
            <label>备注</label>
        </td>
        <td>
            <label>保存</label>
        </td>
    </tr>
    <#if data.salaryItemsList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.salaryItemsList as line>
        <tr class="validationEngineContainer" id="alternate-row_${line.entryId}">
            <td>
            ${rowCount + 1}
            </td>

            <td class="jqv">
                <#if line.entryId == "10001">
                    <label>${line.title?default('')}</label>
                    <input type="hidden" name="title" id="title_${line.entryId}" value="${line.title?default('')}"/>
                <#else>
                    <input type="text" name="title" maxlength="10" id="title_${line.entryId}" value="${line.title?default('')}" class="validate[required,custom[onlyLetterChinese]]"/>
                </#if>
            </td>
            <td class="jqv">
                <#if line.entryId == "10001">
                    <#list typeList as status>
                    <#if line.type == status.enumId>
                        <label>${status.description}</label>
                        <input type="hidden" name="type" id="type_${line.entryId}" value="${status.enumId}">
                    <label
                    </#if>
                    </#list>
                <#else>
                    <select id="type_${line.entryId}" name="type" class="validate[required]">
                        <#if typeList?has_content>
                            <#list typeList as status>
                                <option value="${status.enumId}">${status.description}</option>
                            </#list>
                        </#if>
                    </select>
                    <script language="javascript">
                        $(function () {
                            var entryId='${line.entryId?default("")}';
                            var id ="type_"+entryId;
                            var type = '${line.type?default("")}';
                            $("select[id='"+id+"'] option").each(function () {
                                if(type == $(this).val()){
                                    $(this).attr('selected',"true");
                                }
                            });
                        })
                    </script>
                </#if>
            </td>
            <td>
            <#if line.entryId == "10001">
                <#if line.relativeEntry?has_content>
                <#list data.relativeEntryList as status>
                    <#if line.relativeEntry == status.entryId>
                        <label>${status.title}</label>
                        <input type="hidden" name="relativeEntry" id="relativeEntry_${line.entryId}" value="${status.entryId}">
                    </#if>
                </#list>
                <#else>
                    <label>无</label>
                </#if>
            <#else>
                <select id="relativeEntry_${line.entryId}" name="relativeEntry" >
                    <option value="">无</option>
                        <#list data.relativeEntryList as status>
                            <#if status.entryId != line.entryId>
                                <option value="${status.entryId}">${status.title}</option>
                            </#if>
                        </#list>
                </select>
                <script language="javascript">
                    $(function () {
                        var entryId='${line.entryId?default("")}';
                        var id ="relativeEntry_"+entryId;
                        var relativeEntry ='${line.relativeEntry?default("")}';
                        $("select[id='"+id+"'] option").each(function () {
                            if(relativeEntry == $(this).val()){
                                $(this).attr('selected',"true");
                            }
                        });
                    })
                </script>
            </#if>
            </td>
            <td>
                <input type="text" name="amount" maxlength="10" id="amount_${line.entryId}" value="${line.amount?default('')}" class="validate[required,custom[twoDecimalNumber]]"/>
            </td>
            <td>
            <#if line.entryId == "10001">
                <label>${line.remarks?default('')}</label>
                <input type="hidden" name="remarks" id="remarks_${line.entryId}" value="${line.remarks?default('')}"/>
            <#else>
                <input type="text" name="remarks" maxlength="10" id="remarks_${line.entryId}" value="${line.remarks?default('')}"/>
            </#if>
            </td>
            <td colspan="7" align="center">
                            <a href="#" class="smallSubmit" onclick="editSalaryEntry('${line.entryId}')">提交</a>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.salaryItemsList?has_content>
    <@nextPrevAjax targetId="SalaryItemsList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</html>