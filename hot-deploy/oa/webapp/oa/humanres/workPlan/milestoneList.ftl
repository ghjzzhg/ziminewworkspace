<#macro showMilestoneList workPlanId trId="milestone_" name='milestoneId_o_'>
<script type="text/javascript">
    var milestoneItem = 0;
    function addMilestone() {
        milestoneItem += 1;
        $("#workPlan_milestone").append('<tr id="milestone_' + milestoneItem + '"><input type="hidden" name="milestoneId_o_'+ milestoneItem +'"><td style="position:relative">里程碑' + milestoneItem + '：<input type="text" id="milestoneTime_o_'+ milestoneItem +'" name="milestoneTime_o_'+ milestoneItem +'"></td>' +
                '<td>说明：<input type="text" name="milestoneDescription_o_'+ milestoneItem +'" style="width: 80%"></td>' +
                '<td width="10%"><a class="smallSubmit" onclick="deleteMilestone($(this))" name="' + milestoneItem + '">删除</a></td></tr>');
        initDatePicker({input: "#milestoneTime_o_"+ milestoneItem});
    }
    function deleteMilestone(obj) {
        var milestone_index = $(obj).attr("name");
        var trObj = $("#" + "${trId}" + milestone_index);
        $(trObj).find("input[name='"+ "${name}" + milestone_index +"']").each(function(){
            var hiddenValue = $(this).val();
            if(hiddenValue != null && hiddenValue != ''){
                $.ajax({
                    type: 'post',
                    url: "deleteMilestone",
                    data: {"milestoneId": hiddenValue},
                    async: true,
                    dataType: 'json',
                    success: function (content) {
                        showInfo(content.returnValue);
                        $("#" + "${trId}" + milestone_index).detach();
                    }
                });
            }else{
                $("#" + "${trId}" + milestone_index).detach();
            }
        });

    }
</script>
<table id="workPlan_milestone">
    <tr>
        <td><a class="smallSubmit" onclick="addMilestone()">添加里程碑</a></td>
        <td></td>
        <td></td>
    </tr>
    <#if workPlanId?has_content>
        <#assign milestoneList = delegator.findByAnd("TblMilestone", {"workPlanId" :workPlanId}, null, false)>
        <#assign milestoneSize = milestoneList?size>
        <script>
            milestoneItem = parseInt("${milestoneSize}");
        </script>
        <#assign milestone_index = 1>
        <#list milestoneList as list>
            <tr id="milestone_${milestone_index}">
                <input type="hidden" name="milestoneId_o_${milestone_index}" value="${list.milestoneId}">
                <td width="40%">
                    里程碑${milestone_index}:
                    <@htmlTemplate.renderDateTimeField name="milestoneTime_o_${milestone_index}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                    value="${list.milestoneTime}" size="25" maxlength="30" id="milestoneTime_o_${milestone_index}" dateType="" shortDateInput=false timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td>说明：<input type="text" name="milestoneDescription_o_${milestone_index}"
                              value="${list.milestoneDescription}" style="width: 80%"></td>
                <td width="10%"><a class="smallSubmit" name ="${milestone_index}" onclick="deleteMilestone($(this))">删除</a></td>
            </tr>
            <#assign milestone_index = milestone_index + 1>
        </#list>
    </#if>
</table>
<script type="text/javascript">

</script>
</#macro>

