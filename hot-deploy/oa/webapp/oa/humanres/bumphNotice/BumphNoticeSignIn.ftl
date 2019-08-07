<#--noticeId:通知Id、list-->
<#assign currentPartyId = partyId>
<#assign noticeId = noticeId>
<#assign valueList = signInPersonList>
<#assign statusList = statusList>
<#assign hasSignInList = hasSignInList>

<script language="javascript">
    function confirmButton(id,noticeId,staffId,signInStatus,status,remark) {
        $.ajax({
            type: 'POST',
            url: "updateSignInPerson",
            async: true,
            dataType: 'json',
            data:{singnInPersonId:id,noticeId:noticeId,staffId:staffId,signInStatus:signInStatus,signInPersonStatus:status,remark:remark},
            success: function (content) {
                showInfo(content.msg);
                closeCurrentTab();
                $.bumphNotice.searchSignInRecord();
            }
        });
    }
</script>
<div class="yui3-skin-sam">
    <form name="SignInForm" id="SignInForm" class="basic-form">
        <div class="yui3-skin-audio-light">
            <div>
                <label class="h2" style="color:red">注意：本文档需要以下员工签收确认，请列表中员工在下面直接签收确认：</label>

                <div>
                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tbody>
                        <tr align="center" bgcolor="#E0E0E0" class="header-row-2">
                            <td width="15%"><strong>签收者部门</strong></td>
                            <td width="10%"><strong>姓名</strong></td>
                            <td width="15%"><strong>岗位</strong></td>
                            <td width="15%"><strong>签收时间</strong></td>
                            <td width="10%"><strong>签收否</strong></td>
                            <td width="15%"><strong>当前状态</strong></td>
                            <td width="10%"><strong>备 注</strong></td>
                            <td width="10%"><strong>操作</strong></td>
                        </tr>
                            <#if valueList?has_content>
                                <#assign item=0>
                                <#list valueList as sipList>
                                    <tr>
                                        <td>${sipList.departmentName}</td>
                                        <td>${sipList.fullName}</td>
                                        <td>${sipList.positionDesc?default('')}</td>
                                        <td><#if sipList.signInTime?has_content>${sipList.signInTime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
                                        <td>
                                            <#if sipList.hasSignIn!="NS_Y">
                                                <select name="signInStatus" id="signInStatus${item}">
                                                    <#list hasSignInList as hasSignIn>
                                                        <option value="${hasSignIn.enumId}">${hasSignIn.description}</option>
                                                    </#list>
                                                </select>
                                            <#else >
                                                <#assign hasSignInEnum  = delegator.findOne("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId",sipList.hasSignIn), false)/>
                                                ${hasSignInEnum.description}
                                            </#if>
                                        </td>
                                        <td>
                                            <#if sipList.signInPersonStatus!="NS_UNDERSTAND">
                                                <select name="signInPersonStatus" id="status${item}">
                                                    <#list statusList as status>
                                                        <#if status.enumId==sipList.signInPersonStatus>
                                                            <option value="${status.enumId}" selected>${status.description}</option>
                                                        <#else >
                                                            <option value="${status.enumId}">${status.description}</option>
                                                        </#if>
                                                    </#list>
                                                </select>
                                            <#else >
                                                <#assign statusEnum  = delegator.findOne("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId",sipList.signInPersonStatus), false)/>
                                                ${statusEnum.description}
                                            </#if>
                                        </td>
                                        <td>
                                            <#if sipList.signInPersonStatus!="NS_UNDERSTAND">
                                                <input type="text" name="remark" id="remark${item}" value="${sipList.remark?default('')}">
                                            <#else>
                                                <input type="text" name="remark" id="remark${item}" value="${sipList.remark?default('')}" readonly="readonly">
                                            </#if>
                                        </td>
                                        <td>
                                            <#if currentPartyId == sipList.staffId >
                                                <#if sipList.signInPersonStatus!="NS_UNDERSTAND">
                                                    <a href="#" class="smallSubmit" name="submit_${item}"
                                                        onclick="confirmButton('${sipList.singnInPersonId}','${sipList.noticeId}','${sipList.staffId}',$('#signInStatus'+${item}).val(),$('#status'+${item}).val(),$('#remark'+${item}).val())">确认</a>
                                                </#if>
                                            <#else>
                                                <#assign hasSignInEnum  = delegator.findOne("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId",sipList.hasSignIn), false)/>
                                                ${hasSignInEnum.description}
                                            </#if>
                                        </td>
                                    </tr>
                                <#assign item = item+1>
                                </#list>
                            </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>