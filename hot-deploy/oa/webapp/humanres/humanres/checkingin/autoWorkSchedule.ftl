<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#if departmentIds?has_content>
    <#assign departmentArr = departmentIds?split(",")>
    <#assign departmentSize = departmentArr?size>
</#if>
<#if listOfWorkIds?has_content>
    <#assign listOfWorkArr = listOfWorkIds?split(",")>
    <#assign listOfWorkSize = listOfWorkArr?size>
<#else>
    <#assign listOfWorkArr = " ">
    <#assign listOfWorkSize = 0>
</#if>
<script type="text/javascript">
    $(document).ready(function () {
        /*给选择要显示的字段赋值(workGroup)*/
        /*$("#workGroupItem").multiSelect({keepOrder: true});*/
        $("#workOrderItem").multiSelect({keepOrder: true});
        <#--<#if departmentArr?has_content>-->
            <#--var workGroupItem_arr = new Array(${departmentSize});-->
            <#--<#list 0..departmentSize - 2 as i>-->
                <#--workGroupItem_arr[${i}] = '${departmentArr[i]}';-->
            <#--</#list>-->
            <#--for(var j =0; j < workGroupItem_arr.length - 1; j ++){-->
                <#--$("#workGroupItem").multiSelect('select',workGroupItem_arr[j]);-->
            <#--}-->
        <#--</#if>-->

        <#if listOfWorkArr?has_content >
            var workOrderItem_arr = new Array(${listOfWorkSize});
            <#list 0..listOfWorkSize - 2 as i>
                workOrderItem_arr[${i}] = '${listOfWorkArr[i]}';
            </#list>

            for(var j =0; j < workOrderItem_arr.length - 1; j ++){
                $("#workOrderItem").multiSelect('select',workOrderItem_arr[j]);
            }
        </#if>


        $("#ms_select_y").click(function () {
            $('#workOrderItem').multiSelect('select_all');
        });
        $("#ms_deselect_n").click(function () {
            $('#workOrderItem').multiSelect('deselect_all');
        });

        /*$("#ms_select_ye").click(function () {
            $('#workGroupItem').multiSelect('select_all');
        });
        $("#ms_deselect_no").click(function () {
            $('#workGroupItem').multiSelect('deselect_all');
        });*/
    });

    function saveAutoSchedule() {
        if($("#autoWorkSchedulForm").validationEngine('validate')){
            var selectGroupValues = $("#releaseDepartment_dept_only").val();
            var i = selectGroupValues.split(',').length;
            console.log(i);
            var selectOrderValues ="";
            var autoWorkScheduleId = $("#autoWorkScheduleId").val();
            var startDate = $("#autoWorkSchedulForm_startDate").val();
            var endDate = $("#autoWorkSchedulForm_endDate").val();
            var j = 0;
            $("#ms-workOrderItem").find(".ms-selection .ms-selected").each(function(){
                var name = $(this).attr("name");
                selectOrderValues += name +",";
                j++;
            });
            console.log(i);
            if(i < j){
                showInfo("“排班部门”个数不能少于“排班班次”个数！");
                return;
            }
            closeCurrentTab();
            displayInTab3("ShowAutoScheduleTab", "自动排班", {
                requestUrl: "showAutoSchedule",
                data: {
                    autoWorkScheduleId:autoWorkScheduleId,
                    groupValues: selectGroupValues,
                    orderValues: selectOrderValues,
                    startDate: startDate,
                    endDate: endDate
                },
                width: "900px",
                height: 600,
                position:'center'
            });
        }
    }

    function showLookupInfo(id) {
        console.log(id);
        $.ajax({
            type: 'POST',
            url: "AutoWorkScheduleGroup",
            async: true,
            data:{partyIdTo: id},
            dataType: 'html',
            success: function (context) {
                $("#auto_work_schedule_group").html(context);
                $("#workGroupItem").multiSelect({keepOrder:true});
            }
        });
    }
    $(function () {
        $("#autoWorkSchedulForm_releaseDepartment").data("promptPosition", "bottomLeft");
    });
</script>
<div class="screenlet-body">
    <form id="autoWorkSchedulForm" method="post" name="autoWorkSchedule" class="basic-form">
    <#assign listOfWork = delegator.findAll("TblListOfWork",false)>
        <input type="hidden" id="autoWorkScheduleId" value="${autoWorkScheduleId?default('')}"/>
        <input type="hidden" name="VIEW_SIZE" value="25"/>
        <input type="hidden" name="PAGING" value="Y"/>
        <input type="hidden" name="noConditionFind" value="Y"/>

        <table cellspacing="0" class="basic-table">
            <tr>
                <td class="label">　
                    <label for="autoWorkSchedulForm_noticeDataScope" id="autoWorkSchedulForm_noticeDataScope_title"><b class="requiredAsterisk">*</b>排班部门</label>  </label>
                </td>
                <td class="jqv" colspan="4">
                <@dataScope id="autoWorkSchedulForm_releaseDepartment" name="releaseDepartment" dataId="${autoWorkScheduleId?default('')}" entityName="TblAutoWorkSchedule" required=true dept=true level=false position=false user=false like=false/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="autoWorkSchedulForm_startDate" id="autoWorkSchedulForm_startDate_title"><b class="requiredAsterisk">*</b>开始日期</label>  </td>
                <td class="jqv">
                <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                value="${startDate?default('')}" size="25" maxlength="30" id="autoWorkSchedulForm_startDate"
                dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'autoWorkSchedulForm_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
                hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="autoWorkSchedulForm_endDate" id="autoWorkSchedulForm_endDate_title"><b class="requiredAsterisk">*</b>结束日期</label>  </td>
                <td class="jqv">
                <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                value="${endDate?default('')}" size="25" maxlength="30" id="autoWorkSchedulForm_endDate"
                dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'autoWorkSchedulForm_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
                hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
            </tr>
            <#--<tr>-->
                <#--<td class="label">-->
                    <#--<label for="autoWorkSchedulForm_department" id="autoWorkSchedulForm_department_title"> 部门<b style="color: red;">*</b></label>  </td>-->
                <#--<td valign="middle" class="jqv">-->
                    <#--<@htmlTemplate.lookupField value="" className="validate[required]" formName="autoWorkSchedule" name="releaseDepartment" id="releaseDepartment" fieldFormName="LookupDepartment" position="center"/>-->
                <#--</td>-->
            <#--</tr>-->
            <tr>
                <td class="label">
                    <label for="autoWorkSchedulForm_workOrderItem" id="autoWorkSchedulForm_workOrderItem_title"><b class="requiredAsterisk">*</b>排班班次</label>  </td>
                <td colspan="4">
                    <#--<div class="lefthalf" id="auto_work_schedule_group">-->
                        <#--<select name="workGroups" id="workGroupItem" multiple="multiple" style="display: none">-->
                            <#--<#if groupList?has_content>-->
                                <#--<#list groupList as list>-->
                                    <#--<#assign group = delegator.findOne("PartyGroup",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",list.partyIdTo),false)>-->
                                    <#--<option value="${list.partyIdTo}" name = "${list.partyIdTo}">${group.groupName}</option>-->
                                <#--</#list>-->
                            <#--</#if>-->
                        <#--</select>-->
                        <#--<a class="smallSubmit" href="#" id="ms_select_ye" title="全选">全选</a>-->
                        <#--<a class="smallSubmit" href="#" id="ms_deselect_no" title="全部不选">全部不选</a>-->
                    <#--</div>-->
                    <div class="lefthalf">
                        <select name="workOrders" id="workOrderItem" multiple="multiple" style="display: none" class="validate[required]">
                           <#if listOfWork?has_content>
                               <#list listOfWork as list>
                                    <option value="${list.listOfWorkId}" name="${list.listOfWorkId}">${list.listOfWorkName}</option>
                               </#list>
                           </#if>
                        </select>
                        <a class="smallSubmit" href="#" id="ms_select_y" title="全选">全选</a>
                        <a class="smallSubmit" href="#" id="ms_deselect_n" title="全部不选">全部不选</a>

                    </div>
                </td>
            </tr>
            <tr>
                <td align="center" colspan="2">
                    <a href="#" class="buttontext" onclick="saveAutoSchedule()">${uiLabelMap.CommonSave}</a>
                </td>
            </tr>
        </table>
    </form>
</div>