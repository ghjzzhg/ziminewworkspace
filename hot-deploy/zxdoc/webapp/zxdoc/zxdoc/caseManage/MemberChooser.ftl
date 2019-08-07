<div class="portlet light ">
    <div class="portlet-body">
<#if members.keySet()?size == 0>
    无可添加人员
<#else>
<script>
    function saveMember() {
        var options = {
            url: "AddCasePartyMember",
            type: "post",
            dataType: "json",
            success: function (data) {
                showInfo(data.data);
                closeCurrentTab();
            }
        }
        $("#casePartyMember").ajaxSubmit(options);
    }
</script>

<form id="casePartyMember">
    <input type="hidden" name="caseId" value="${caseId}">
    <input type="hidden" name="groupPartyId" value="${partyId}"/>
    <table class="table table-hover table-striped table-bordered">
        <tbody>
        <tr>
            <td><label class="control-label">人员</label></td>
            <td>
                <#if memberId??>
                    <#--<input type="hidden" name="partyId" value="${memberId}"/>-->
                    <input type="hidden" name="isAdd" value="false"/>
                    <select name="partyId" class="form-control">
                        <#list members.keySet() as partyMemberId>
                            <option value="${partyMemberId}">
                            ${members.get(partyMemberId)}
                            </option>
                        </#list>
                    </select>
                <#else>
                    <input type="hidden" name="isAdd" value="true"/>
                    <select name="partyId" class="form-control">
                        <#list members.keySet() as partyMemberId>
                            <option value="${partyMemberId}">
                            ${members.get(partyMemberId)}
                            </option>
                        </#list>
                    </select>
                </#if>
            </td>
        </tr>
        <tr>
            <td><label class="control-label">角色</label></td>
            <td>
                <select name="roleTypeId" class="form-control">
                    <#list roleType as memberRole>
                        <option value="${memberRole.roleTypeId!}">${memberRole.description!}</option>
                    </#list>
                </select>
            </td>
        </tr>
        </tbody>
    </table>
</form>
<center>
    <div>
        <a href="javascript:void(0);" class="btn green" onclick="saveMember()"> 完成 </a>
    </div>
</center>
</#if>
</div>
</div>
