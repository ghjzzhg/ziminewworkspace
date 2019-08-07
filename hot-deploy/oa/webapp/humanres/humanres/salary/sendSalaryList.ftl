<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.salaryPayOffList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchSalaryPayOffList"/>
    <#assign sortParam = ""/>
    <#assign param = sortParam/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<script language="javascript">
    $(function () {
        $("[name='moneh']").each(function () {
            $(this).html(unescapeHtmlText($(this).html()));
        });
    });
</script>
<input type="hidden" id="salaryType" value="">
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">搜索</li>
        </ul>
    </div>
    <table class="basic-table hover-bar">
        <tr>
            <td>
                <label>发送状态</label>
            </td>
            <td>
                <select id="sendStratus" onchange="showSalaryStartus(this)">
                    <option value="">全部</option>
                    <#list salaryTypeList as line>
                        <option value="${line.enumId}">${line.description}</option>
                    </#list>
                </select>
            </td>
            <td id="salaryStatusLabel">
                <label>薪资状态</label>
            </td>
            <td id="salaryStatusSelect">
                <select id="salaryStatus">
                    <option value="">全部</option>
                    <option value="1">正常</option>
                    <option value="0">异常</option>
                </select>
            </td>
            <td>
                <a href="#" onclick="searchSalaryPayOffList()" class="smallSubmit">查询</a>
                <a href="#" onclick="editSalaryList()" class="smallSubmit">批处理</a>
                <a href="#" onclick="$.salary.printSalaryBill();" class="smallSubmit">工资条</a>
            </td>
        </tr>
    </table>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">薪资报告一览表</li>
        </ul>
    </div>
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr class="header-row-2">
            <td>
                <input onclick="showMonthInfo(this,'${month}')" type="checkbox">
            </td>
            <td>
                <label>部门</label>
            </td>
            <td>
                <label>工号</label>
            </td>
            <td>
                <label>姓名</label>
            </td>
            <td>
                <label>岗位</label>
            </td>
            <td>
                <label>性别</label>
            </td>
            <td>
                <span>${month}月</span>
            </td>
        </tr>
        <#if data.salaryPayOffList?has_content>
            <#assign alt_row = false>
            <#assign rowCount = viewIndex * viewSize>
            <#list data.salaryPayOffList as line>
            <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <tr class="alternate-row" id="salaryList">
                <td  align="center">
                    <span name="moneh">${line["check" + month]?default("")}</span>
                </td>
                <td>
                    ${line.groupName?default("")}
                </td>
                <td>
                    ${line.workerSn?default("")}
                </td>
                <td>
                    ${line.name?default("")}
                </td>
                <td>
                    ${line.groupName?default("")}
                </td>
                <td>
                    ${line.postName?default("")}
                </td>
                <td>
                    <span name="moneh">${line["moneh" + month]?default("")}</span>
                </td>
            </tr>
                <#assign rowCount = rowCount + 1>
                <#assign alt_row = !alt_row>
            </#list>
        </#if>
        </tbody>
    </table>
</div>
<#if data.salaryPayOffList?has_content>
    <@nextPrevAjax targetId="SalaryPayOffListDiv" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</html>
<script language="JavaScript">
    function showMonthInfo(v,month){
        $("input[name^='month_"+month+"_']").each(function (){
            if ($(v).attr("checked")) {
                $(this).attr("checked", true);
            } else {
                $(this).attr("checked", false);
            }
        })
    }

    function showSalaryStartus(v){
        var value = $(v).val();
        if(value != "SEND_TYPE_NOTSEND"){
            $("#salaryStatusLabel").css('display' ,'');
            $("#salaryStatusSelect").css('display' ,'');
        }else{
            $("#salaryStatusLabel").css('display' ,'none');
            $("#salaryStatusSelect").css('display' ,'none');
        }
    }

    function searchSalaryPayOffList(){
        var type =  $("#sendStratus").val();
        var salaryStatus =  $("#salaryStatus").val();
        $.ajax({
            type: 'POST',
            url: "searchSalaryPayOffList",
            async: true,
            data:{month:'${month}',year:'${year}',partyId:'${data.partyId?default("")}',department:'${data.department?default("")}',position:'${data.position?default("")}',type:type,salaryStatus:salaryStatus},
            dataType: 'html',
            success: function (content) {
                $("#sendSalaryLists").html(content);
                $("#salaryType").val(type);
                if(type == "SEND_TYPE_NOTSEND"){
                    $("#salaryStatusLabel").css('display' ,'none');
                    $("#salaryStatusSelect").css('display' ,'none');
                }
                $("select[id='sendStratus'] option").each(function () {
                    if(type == $(this).val()){
                        $(this).attr('selected',"true");
                    }
                });
                $("select[id='salaryStatus'] option").each(function () {
                    if(salaryStatus == $(this).val()){
                        $(this).attr('selected',"true");
                    }
                });
            }
        });
    }

    function editSalaryList(){
        var typeList = new Array;
        var i = 0;
        var errorFlag = false;
        var flag = false;
        var num = 0;
        $("input[name^='month_']").each(function (){
            if ($(this).attr("checked")) {
                var value = $(this).val();
                var valuestr = value.substr(0,value.lastIndexOf(","));
                var type = valuestr.substr(valuestr.lastIndexOf(",")+1,valuestr.length);
                if(type == "SEND_TYPE_SEND"){
                    errorFlag = true;
                    return false;
                }
                if(typeList.length <= 0){
                    typeList[i] = type;
                }
                if(typeList.indexOf(type) < 0){
                    flag = true;
                    return false;
                }
                num++;
                i++;
            }
        })
        if(errorFlag){
            showInfo("已发状态不可批处理");
            return;
        }
        if(flag){
            showInfo("请检查薪资发放，类型需一致！");
            return;
        }
        var no = 0;
        $("input[name^='month_']").each(function (){
            if ($(this).attr("checked")) {
                var valueInfo = $(this).val();
                var partyId = valueInfo.substr(0, valueInfo.indexOf(","));
                var partyValue = valueInfo.substr(valueInfo.indexOf(",") + 1, valueInfo.length);
                var year = partyValue.substr(0, partyValue.indexOf(","));
                var yearValue = partyValue.substr(partyValue.indexOf(",") + 1, partyValue.length);
                var month = yearValue.substr(0, yearValue.indexOf(","));
                var monthValue = yearValue.substr(yearValue.indexOf(",") + 1, yearValue.length);
                var sendId = monthValue.substr(0, monthValue.indexOf(","));
                var sendIdValue = monthValue.substr(monthValue.indexOf(",") + 1, monthValue.length);
                var status = sendIdValue.substr(0, sendIdValue.indexOf(","));
                var statusValue = sendIdValue.substr(sendIdValue.indexOf(",") + 1, sendIdValue.length);
                var paySalary = statusValue.substr(0, statusValue.length);
                no++;
                $.ajax({
                    type: 'POST',
                    url: "batchSetSalaryInfo",
                    async: true,
                    data:{month:month,year:year,partyId:partyId,sendId:sendId,status:status,paySalary:paySalary},
                    dataType: 'html',
                    success: function (content) {
                        if(num == no){
                            showSubMenuAjax('SalaryPayOff');
                        }
                    }
                });
            }
        });
    }
</script>
