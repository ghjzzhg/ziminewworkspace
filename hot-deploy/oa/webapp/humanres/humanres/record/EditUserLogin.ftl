<form method="post" id="createUserLogin" name="createUserLogin" action="<@ofbizUrl>createUserLogin</@ofbizUrl>" class="basic-form">
    <input type="hidden" name="partyId" value="${partyId}"/>
    <input type="hidden" name="enabled"/>
    <input type="hidden" name="externalAuthId"/>
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <b class="requiredAsterisk">*</b>用户登录账户  </td>
            <td>
                <input type="text" name="userLoginId" class="validate[required]" size="25">*
            </td>
        </tr>
        <tr>
            <td class="label">
                <b class="requiredAsterisk">*</b>当前密码  </td>
            <td>
                <input type="password" id="currentPassword" name="currentPassword" size="25" class="validate[required,maxSize[6]]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <b class="requiredAsterisk">*</b>当前密码确认</td>
            <td>
                <input type="password" name="currentPasswordVerify" size="25" class="validate[required,equals[currentPassword]]">
            </td>
        </tr>
        <tr>
            <td class="label">密码提示</td>
            <td>
                <input type="text" name="passwordHint" size="25" class="validate[required]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <b class="requiredAsterisk">*</b>首次登录必须修改密码?  </td>
            <td>
              <span class="ui-widget">
                <select name="requirePasswordChange" id="AddUserLogin_requirePasswordChange" size="1">
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
                          <input type="checkbox" name="securityGroups" value="${group.groupId}">${group.description}
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
                <a class="smallSubmit" href="#" title="保存" onclick="$.recordManagement.saveUserLogin()">保存</a>
            </td>
        </tr>
        </tbody></table>
</form>