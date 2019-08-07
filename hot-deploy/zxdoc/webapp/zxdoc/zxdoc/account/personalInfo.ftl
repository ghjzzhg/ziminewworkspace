<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/validationEngine/validationEngine.jquery.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script src="${request.contextPath}/static/account.js?t=20170909" type="text/javascript"></script>
<script type="text/javascript"
        src="/images/lib/validationEngine/languages/jquery.validationEngine-zh_CN.js?t=20171012"></script>
<script type="text/javascript" src="/images/lib/validationEngine/jquery.validationEngine.js?t=20171010"></script>
<script type="text/javascript">
    $(function () {
        $("#passwordForm").validationEngine("attach", {promptPosition: "bottomLeft"});
        $("#distpicker").distpicker({valueType: "code"});
    });

    $(function () {
        $('#myTab a:first').tab('show');//初始化显示哪个tab

        $('#myTab a').click(function (e) {
            e.preventDefault();//阻止a链接的跳转行为
            $(this).tab('show');//显示当前选中的链接及关联的content
        })
    })
</script>
<div class="portlet light ">
    <ul class="nav nav-tabs" id="myTab">
        <li class="active"><a href="#home">密码修改</a></li>
        <li><a href="#personInfo">个人信息修改</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" id="home">
            <div class="portlet-title">
                <div class="caption font-dark">
                    <i class="icon-settings font-green"></i>
                    <span class="caption-subject bold uppercase">密码修改</span>
                </div>
                <div class="tools"></div>
            </div>
            <div class="portlet-body">
                <form id="passwordForm">

                <#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
                <#assign tenantId = requestParameters.userTenantId!>
                    <input type="hidden" name="requirePasswordChange" value="Y"/>
                    <input type="hidden" name="USERNAME" value="${username}"/>
                    <input type="hidden" name="userTenantId" value="${tenantId!}"/>
                    <table class="table table-hover table-striped table-bordered">
                        <tbody>
                        <tr>
                            <td><label class="control-label">原密码</label></td>
                            <td>
                                <input type="password" class="form-control validate[required]" name="PASSWORD"/>
                            </td>
                        </tr>
                        <tr>
                            <td><label class="control-label">新密码</label></td>
                            <td>
                                <input type="password" class="form-control validate[required,custom[passworklimit]]"
                                       name="newPassword" id="newPassword"/>
                            </td>
                        </tr>
                        <tr>
                            <td><label class="control-label">密码确认</label></td>
                            <td>
                                <input type="password"
                                       class="form-control validate[required,equals[newPassword],custom[passworklimit]]"
                                       name="newPasswordVerify"/>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
                <div class="form-group">
                    <div class="margiv-top-10" style="text-align: center;margin-top:10px">
                        <a href="javascript:$.account.updatePassword();" class="btn green"> 保存 </a>
                    </div>
                </div>
            </div>
        </div>
        <div class="tab-pane" id="personInfo">
            <div class="tab-pane active" id="home">
                <div class="portlet-title">
                    <div class="caption font-dark">
                        <i class="icon-settings font-green"></i>
                        <span class="caption-subject bold uppercase">个人信息修改</span>
                    </div>
                    <div class="tools"></div>
                </div>
                <td class="portlet-body">
                    <form id="accountForm">

                    <#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
                    <#assign tenantId = requestParameters.userTenantId!>
                        <input type="hidden" name="type" value="${AccountType?default('')}" id="type"/>
                        <input type="hidden" name="USERNAME" value="${username}"/>
                        <input type="hidden" name="userTenantId" value="${tenantId!}"/>
                        <input type="hidden" name="emailId" <#if SubInfo?has_content>value="${emailId?default('')}"</#if>/>
                        <#if AccountType != "group">
                            <input type="hidden" name="telId" <#if SubInfo?has_content>value="${SubInfo.telId?default('')}"</#if>/>
                            <input type="hidden" name="postalId" <#if SubInfo?has_content>value="${SubInfo.postalId?default('')}"</#if>/>
                        </#if>
                        <input type="hidden" name="partyId" <#if SubInfo?has_content>value="${SubInfo.partyId?default('')}"</#if>/>
                        <table class="table table-hover table-striped table-bordered">
                            <tbody>
                            <tr>
                                <td><label class="control-label">联系人</label></td>
                                <td>
                                    <input type="text" class="form-control validate[required,custom[noSpecial]]" name="contactName" <#if SubInfo??>value="${SubInfo.fullName?default('')}"</#if>/>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="control-label">联系号码</label></td>
                                <td>
                                    <input type="text" class="form-control validate[required,custom[isMobile]]" name="contactNo" <#if SubInfo??>value="${SubInfo.contactNumber?default('')}"</#if>/>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="control-label">email</label></td>
                                <td>
                                    <input type="text" class="form-control validate[required]" name="email" <#if SubInfo??>value="${email?default('')}"</#if>/>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="control-label">省、市、区</label></td>
                                <td>
                                    <label class="control-label visible-ie8 visible-ie9">省、市、区</label>
                                    <div style="position:relative;"><!-- container -->

                                        <div id="distpicker" class="distpicker" <#if SubInfo?? && SubInfo.area?has_content> data-province="${SubInfo.area[0..*2]}0000"  data-city="${SubInfo.area[0..*4]}00" data-district="${SubInfo.area}"</#if>>
                                            <select id="province" class="form-control"></select>
                                            <select id="city" class="form-control"></select>
                                            <select name="area" class="form-control validate[required]"></select>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="control-label">详细地址</label></td>
                                <td>
                                    <input type="text" class="form-control validate[required,custom[noSpecial]]" name="address" <#if SubInfo??>value="${SubInfo.address1?default('')}"</#if>/>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </form>
                    <div class="form-group">
                        <div class="margiv-top-10" style="text-align: center;margin-top:10px">
                            <a href="javascript:$.account.checkAndSave();" class="btn green"> 保存 </a>
                        </div>
                    </div>
            </div>
        </div>
    </div>

</div>