<script type="text/javascript">
    $(document).ready(function () {
        /*给选择要显示的字段赋值(workGroup)*/
        $("#workGroupItem").multiSelect({keepOrder:true});
        $("#workOrderItem").multiSelect({keepOrder: true});

        $("#ms_select_y").click(function () {
            $('#workOrderItem').multiSelect('select_all');
            $('#workOrderItem').multiSelect('select_all');
        });
        $("#ms_deselect_n").click(function () {
            $('#workOrderItem').multiSelect('deselect_all');
        });

        $("#ms_select_ye").click(function () {
            $('#workGroupItem').multiSelect('select_all');
        });
        $("#ms_deselect_no").click(function () {
            $('#workGroupItem').multiSelect('deselect_all');
        });
    });

    function showAutoSchedulingCalendar() {
        closeCurrentTab();
        displayInTab3("OrganizationTab", "新增个人排班", {requestUrl: "AutoSchedulingCalendar", data: {type:"AutoScheduling"}, width: "800px"});
    }
</script>

<div class="screenlet-body">
    <form name="workScheduleForm" method="post" action="autoWorkSchedule" style="margin: 0;">

        <input type="hidden" name="VIEW_SIZE" value="25"/>
        <input type="hidden" name="PAGING" value="Y"/>
        <input type="hidden" name="noConditionFind" value="Y"/>

        <table cellspacing="0" class="basic-table">
            <tr>
                <td class="label" align="right" valign="middle">
                </td>
                <td valign="middle">
                    <div>
                        开始日期<input type="text" name="a" size="20" value=""/>
                    </div>
                </td>
                <td valign="middle">
                    <div>
                        结束日期<input type="text" name="b" size="20" value=""/>
                    </div>
                </td>
                <td valign="middle">
                    <div>
                        部门
                        <select>
                            <option value="">财务部</option>
                            <option value="">人事部</option>
                        </select>
                    </div>
                </td>
            </tr>

            <tr>
                <td colspan="4">
                    <div class="lefthalf">
                        <select name="workGroups" id="workGroupItem" multiple="true" style="display: none">
                            <option value="1">车间甲</option>
                            <option value="2">车间乙</option>
                            <option value="3">车间丙</option>
                            <option value="4">车间丁</option>
                        </select>
                        <a class="smallSubmit" href="#" id="ms_select_ye" title="全选">全选</a>
                        <a class="smallSubmit" href="#" id="ms_deselect_no" title="全部不选">全部不选</a>
                    </div>
                    <div class="lefthalf">
                        <select name="workOrders" id="workOrderItem" multiple="multiple" style="display: none">
                            <option value="1">早班</option>
                            <option value="2">中班</option>
                            <option value="3">晚班</option>
                        </select>
                        <a class="smallSubmit" href="#" id="ms_select_y" title="全选">全选</a>
                        <a class="smallSubmit" href="#" id="ms_deselect_n" title="全部不选">全部不选</a>

                    </div>
                </td>
            </tr>
            <tr>
                <td align="center" colspan="2">
                    <a href="#" class="buttontext" onclick="showAutoSchedulingCalendar()">${uiLabelMap.CommonSave}</a>
                </td>
            </tr>
        </table>
    </form>
</div>