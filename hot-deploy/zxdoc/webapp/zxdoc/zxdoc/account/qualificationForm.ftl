<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/validationEngine/languages/jquery.validationEngine-zh_CN.js?t=20171012"></script>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript">
    $(function() {
        $("#authentication").validationEngine("attach", {promptPosition: "topLeft"});
    })
    function submitAuthentication() {
        var options = {
            beforeSubmit: function () {
                var validation = $('#authentication').validationEngine('validate');
                return validation;
            },
            async: false,
            dataType: 'json',
            type: 'post',
            url: 'submitAuthentication',
            success: function () {
                showInfo("提交认证成功");
                displayInside("SubAccounts");
            },
            error: function () {
                showInfo("认证不成功");
            }
        }
        $('#authentication').ajaxSubmit(options);
    }
    function submitPersonQualification() {
        var options = {
            beforeSubmit: function () {
                var validation = $('#authentication').validationEngine('validate');
                return validation;
            },
            type: 'post',
            dataType: 'json',
            url: 'submitPersonQualification',
            success: function () {
                showInfo("提交认证成功");
                displayInside('Qualification')
            }
        }
        $('#authentication').ajaxSubmit(options);
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 实名认证</span>
            <span class="label label-sm label-success">${qualificationDesc}</span>
            <span class="label label-sm label-success">${qualificationStatus}</span>
        </div>
    </div>
    <div class="portlet-body">
        <form role="form" action="#" id="authentication">
        <#if qualificationType?hasContent && (qualificationType == "COMPANY" || qualificationType == "PROVIDER")>
            <input type="hidden" name="partyId" value="<#if address?hasContent>${address.partyId?default('')}</#if>">
            <input type="hidden" name="contactMechId"
                   value="<#if address?hasContent>${address.contactMechId?default('')}</#if>">
            <div class="form-group">
                <label class="control-label">征信代码</label>
                <input type="text" placeholder="征信代码" class="form-control" name="regNumber"/></div>
            <div class="form-group">
                <label class="control-label">企业地址</label>
                <input type="text" placeholder="详细地址" class="form-control" name="paAddress1"
                       value="<#if address?hasContent>${address.paAddress1?default("")}</#if>"/></div>
            <div class="form-group">
                <label class="control-label">网址地址</label>
                <input type="text" placeholder="网址地址" class="form-control validate[custom[url]]" name="infoString"/></div>
            <div class="form-group">
                <label class="control-label">营业执照扫描件</label>
                <div>
                    <div class="fileinput fileinput-new" data-provides="fileinput">
                        <div class="fileinput-preview thumbnail" data-trigger="fileinput"
                             style="width: 200px; height: 150px;"></div>
                        <div>
                            <span class="btn red btn-outline btn-file">
                                <span class="fileinput-new"> 选择文件 </span>
                                <span class="fileinput-exists"> 重新选择 </span>
                                <input type="file" name="regPhoto">
                            </span>
                            <a href="javascript:;" class="btn red fileinput-exists" data-dismiss="fileinput"> 删除 </a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label">经营范围</label>
                <textarea class="form-control" rows="3" name="bizScope"></textarea>
            </div>
            <div class="margiv-top-10">
                <a href="javascript:submitAuthentication();" class="btn green"> 提交 </a>
            </div>
        </#if>
        <#if qualificationType?hasContent&&qualificationType=="PROVIDER_PERSON">
            <div class="form-group">
                <input type="hidden" name="partyId" value="${personInfo.partyId?default('')}">
                <label class="control-label">姓名</label>
                <input type="text" placeholder="姓名" class="form-control" name="fullName" value="${personInfo.fullName?default('')}"/></div>
            <div class="form-group">
                <label class="control-label">身份证</label>
                <input type="text" placeholder="身份证号" class="form-control validate[custom[isIdCardNo]]" name="idCard" /></div>
            <div class="form-group">
                <label class="control-label">职业资格证号</label>
                <input type="text" placeholder="职业资格证号" class="form-control" name="qualifiNum"/></div>
            <div class="form-group">
                <label class="control-label">职业资格证扫描件</label>
                <div>
                    <div class="fileinput fileinput-new" data-provides="fileinput">
                        <div class="fileinput-preview thumbnail" data-trigger="fileinput"
                             style="width: 200px; height: 150px;"></div>
                        <div>
                            <span class="btn red btn-outline btn-file">
                                <span class="fileinput-new"> 选择文件 </span>
                                <span class="fileinput-exists"> 重新选择 </span>
                                <input type="file" name="regPhoto">
                            </span>
                            <a href="javascript:;" class="btn red fileinput-exists" data-dismiss="fileinput"> 删除 </a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="margiv-top-10">
                <a href="javascript:submitPersonQualification();" class="btn green"> 提交 </a>
            </div>
        </#if>
        </form>
    </div>
</div>