<form id="AddOccupationMember" class="basic-form" name="AddOccupationMember" action="addOccupationMember">
    <input type="hidden" name="positionId" value="${positionId}">
<table class="basic-table hover-bar">
    <tr>
        <td class="label">
            选择员工
        </td>
        <td>
            <@htmlTemplate.lookupField title="选择员工" formName="AddOccupationMember" name="member" id="member" fieldFormName="LookupStaff" position="center"/>
        </td>
    </tr>
    <tr>
        <td class="label">
            已有岗位
        </td>
        <td>
            <input type="hidden" name="removePositionIds">
        </td>
    </tr>
    <tr>
        <td class="label">
            岗位调整类型
        </td>
        <td>
            <label>
                <input type="radio" name="type" value="1">兼任
            </label>
            <label>
                <input type="radio" name="type" value="2">转岗<span class="tooltip">(勾选需要转出的岗位)</span>
            </label>
        </td>
    </tr>
    <tr>
        <td></td>
        <td>
            <a href="#nowhere" class="smallSubmit" onclick="$.organization.saveOccupationMember();">保存</a>
        </td>
    </tr>
</table>
</form>