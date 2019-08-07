<script type="text/javascript">
    $(document).ready(function () {
        /*给选择要显示的字段赋值(workGroup)*/
        $("#workGroupItem").multiSelect({keepOrder:true});
    });
</script>

<div class="screenlet-body">
    <form name="workScheduleForm" method="post" action="autoWorkSchedule" style="margin: 0;">
        <table cellspacing="0" class="basic-table">
            <tr>
                <td bgcolor="#E0E0E0" width="120">　<b>部门选择：</b></td>
                <td>
                    <select>
                        <option value="1">财务部</option>
                        <option value="2">人事部</option>
                        <option value="3">开发部</option>
                    </select>
                </td>

            </tr>
            <tr>
                <td colspan="4">
                    <div class="col-xs-12">
                        <select name="workGroups" id="workGroupItem" multiple="true" style="display: none">
                            <#if param?has_content>
                                <#if param=="person">
                                    <option value="1">胡建</option>
                                    <option value="2">赵冬冬</option>
                                    <option value="3">毛家寒</option>
                                    <option value="4">左国庆</option>
                                <#elseif param=="range">
                                    <option value="1">子部门一</option>
                                    <option value="2">子部门二</option>
                                    <option value="3">子部门三</option>
                                </#if>

                            </#if>

                        </select>
                    </div>
                </td>
            </tr>
            <tr align="center">
                <td colspan="2">
                    <a href="#" class="smallSubmit">确定</a>
                </td>
            </tr>
        </table>
    </form>
</div>