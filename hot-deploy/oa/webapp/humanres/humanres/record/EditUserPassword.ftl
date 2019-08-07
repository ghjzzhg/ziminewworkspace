<form method="post" id="editUserPassword" name="editUserPassword" action="<@ofbizUrl>createUserLogin</@ofbizUrl>" class="basic-form">
    <input type="hidden" name="userLoginId" value="${editUserLogin.userLoginId}"/>
    <input type="hidden" name="partyId" value="${editUserLogin.partyId}"/>
    <table cellspacing="0" class="basic-table">
        <tbody>
        <#if "${data.isPermission}" == "false">
        <tr>
            <td class="label">
                当前密码  </td>
            <td>
                <input type="password" name="currentPassword" size="25">
            </td>
        </tr>
        <#else>
            <input type="hidden" name="currentPassword" size="25" value="111">
        </tr>
        </#if>


        <tr>
            <td class="label">
                新密码</td>
            <td>
                <input type="password" name="newPassword" id="newPassword" size="25" class="validate[required,maxSize[6]]">
            </td>
        </tr>
        <tr>
            <td class="label">
                新密码确认</td>
            <td>
                <input type="password" name="newPasswordVerify" size="25" class="validate[required,equals[newPassword]]">
            </td>
        </tr>
        <tr>
            <td class="label">密码提示</td>
            <td>
                <input type="text" name="passwordHint" size="25" value="${editUserLogin.passwordHint?default('')}" class="validate[required]">
            </td>
        </tr>
        <tr>
            <td class="label">
                启用?  </td>
            <td>
              <span class="ui-widget">
                <select name="enabled" size="1">
                    <option value="Y">是</option>
                    <option selected="selected" value="N">否</option>
                </select>
              </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                权限组
            </td>
            <td>
              <span class="ui-widget">
            <#list securityGroups as group>
            <label>
                <input type="checkbox" name="securityGroups" value="${group.groupId}" <#if assignedGroups?contains(group.groupId)>checked</#if>>${group.description}
            </label>
            </#list>
              </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddHolidayInfoForm_addLink" class="hide" id="AddHolidayInfoForm_addLink_title">保存</label>
            </td>
            <td>
                <a class="smallSubmit" href="#" title="保存" onclick="$.recordManagement.updateUserLogin()">保存</a>
            </td>
        </tr>
        </tbody></table>
</form>