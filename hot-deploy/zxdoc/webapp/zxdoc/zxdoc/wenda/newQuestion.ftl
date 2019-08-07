<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link type="text/css" rel="stylesheet" href="/images/lib/typeahead/typeahead.css"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/select2/select2-bootstrap.min.css" rel="stylesheet" type="text/css"/>

<link type="text/css" rel="stylesheet" href="/images/lib/typeahead/typeahead.css"/>
<link href="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet" type="text/css"/>
<script src="/images/lib/typeahead/handlebars.min.js" type="text/javascript"></script>
<script src="/images/lib/typeahead/typeahead.bundle.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.min.js"></script>

<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="/images/lib/typeahead/handlebars.min.js" type="text/javascript"></script>
<script src="/images/lib/typeahead/typeahead.bundle.min.js" type="text/javascript"></script>
<script src="/images/lib/select2/select2.js" type="text/javascript"></script>
<script src="/images/lib/select2/select2_extended_ajax_adapter.js" type="text/javascript"></script>
<script src="/images/lib/select2/i18n/zh-CN.js" type="text/javascript"></script>
<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>
<script type="text/javascript">


    $(function () {
        $("#question").validationEngine("attach", {promptPosition: "topLeft"});

        $("#fileFieldName").attachment({
            showLink: true,
            wrapperClass: 'btn btn-success'
        });
        var numbers = new Bloodhound({
            datumTokenizer: function (d) {
                return Bloodhound.tokenizers.whitespace(d.num);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            local: [
                {num: '财务'},
                {num: '法律'},
                {num: '投资'}
            ]
        });

        // initialize the bloodhound suggestion engine
        numbers.initialize();

        $('#questionType').tagsinput({
            typeaheadjs: {
                name: 'questionType',
                displayKey: 'num',
                valueKey: 'num',
                source: numbers.ttAdapter()
            }
        });

        $(".choose-party").on("click", function(){
            choosePartyPerson(this);
        })
        $(".clear-party").on("click", function(){
            clearPartyPerson(this);
        })
    })

    function toggleTargetAccount(obj) {
        if ($(obj).attr("checked")) {
            $("#targetArea").show();
            $("#checkSelect").val("Y");
            $("#targetPerson").addClass("validate[required]");
        } else {
            $("#targetArea").hide();
            $("#checkSelect").val("");
            clearPartyPerson();
            $("#targetPerson").removeClass("validate[required]");
        }
    }


    function choosePartyPerson(){
        displayInLayer2('选择咨询对象', {
            requestUrl: 'PartyPersonChooser?type=CASE_ROLE_OWNER,CASE_ROLE_ACCOUNTING,CASE_ROLE_INVESTOR,CASE_ROLE_LAW,CASE_ROLE_STOCK', width: 500, height: 600,
            end: function (data) {
                $("#targetPersonName").html(data.name + " " + data.person);
                $("#targetPerson").val(data.id);
            }
        });
    }

    function clearPartyPerson(){
        $("#targetPersonName").html("");
        $("#targetPerson").val("");
    }

    function handelQuestion() {
        var options = {
            beforeSubmit: function () {
                return $("#question").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                showInfo("提交成功");
                closeCurrentTab();
                displayInside('WendaHistory');
            },
            url: "handelQuestion",
            type: 'post'
        };
        $("#question").ajaxSubmit(options);
    }

    function saveQuestion() {
        var options = {
            beforeSubmit: function () {
                return $("#question").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                showInfo("提交成功");
                displayInside('WendaHistory')
            },
            url: "saveQuestion",
            type: 'post'
        };
        $("#question").ajaxSubmit(options);
    }

</script>
<div class="portlet light" style="min-height: 600px;">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 提问</span>
        </div>
    </div>
    <div class="portlet-body">
        <form role="form" action="#" id="question">
            <div class="form-group col-md-12">
                <label class="control-label">问题概述<span class="required"> * </span></label>
            <#if question?has_content>
                <textarea class="form-control validate[required,custom[noSpecial]]" rows="3" id="overview"
                          name="questionOverview" maxlength="100"
                          placeholder="概述信息不超过100个字符">${question.questionOverview?default('')}</textarea>
            <#else>
                <textarea class="form-control validate[required,custom[noSpecial]]" rows="3" id="overview"
                          name="questionOverview" maxlength="100" placeholder="概述信息不超过100个字符"></textarea>
            </#if>
            </div>

        <#--匿名要写onchange-->
            <div class="form-group col-md-12">
                <label class="control-label">问题细节</label>
            <#if question?has_content>
                <textarea class="form-control validate[custom[noSpecial]]" rows="6" id="detail"
                          name="questionDetail" maxlength="1000"
                          placeholder="细节信息不超过1000个字符">${question.questionDetail?default('')}</textarea>
            <#else >
                <textarea class="form-control validate[custom[noSpecial]]" rows="6" id="detail" name="questionDetail"
                          maxlength="1000" placeholder="细节信息不超过1000个字符"></textarea>
            </#if>
            </div>

            <div class="form-group col-md-6">
                <input type="checkbox" value="Y" name="isAnonymous"
                       <#if !question?has_content || question.isAnonymous="Y">checked</#if> id="anonymous">
                <label class="control-label" for="anonymous">匿名提问</label>
            </div>
        <#--附件-->
            <div class="form-group col-md-6" style="min-width: 535px;">
            <#assign requireTargetPerson = !question?has_content || question.isSecret?default('')="Y">
                <input type="checkbox" value="Y" name="isSecret"
                       <#if requireTargetPerson>checked</#if> id="secret"
                       onchange="toggleTargetAccount(this)">
                <input id="checkSelect" <#if requireTargetPerson>value="Y"
                       <#else>value=""</#if> type="hidden">
                <label class="control-label" for="secret">涉密</label>
                <span id="targetArea" <#if !requireTargetPerson>style="display:none"</#if>>
                <label class="control-label">&nbsp;&nbsp;指定对象:</label>
                <span id="targetPersonName" class="party-person-name">${groupName?default('')} ${fullName?default('')}</span>
                <input id="targetPerson" type="hidden" name="targetPerson" value="${partyId?default('')}" data-jqv-prompt-at="#targetPersonName" <#if requireTargetPerson>class="validate[required]"</#if>>
                <div style="display: inline-block;float: right">
                    <a style="float:right" class="btn btn-sm btn-circle btn-danger clear-party" href="#nowhere">删除</a>
                    <a style="float:right" class="btn btn-sm btn-circle btn-success choose-party" href="#nowhere">选择</a>
                </div>
                    </span>
            </div>
            <div class="form-group col-md-6">
                <label class="control-label">问题类型(选定正确类型会得到更为专业的回答)</label>
                <div>
                <#list description as list>
                    <#if questionType?has_content>
                        <label><input type="checkbox" value="${list.enumId?default('')}" id="questionType1"
                                      name="questionType1"<#list questionType as type><#if type.isStandard="Y"&&type.questionType=list.enumId>checked</#if></#list>>${list.description?default('')}
                        </label>
                    <#else>
                        <label><input type="checkbox" value="${list.enumId?default('')}" id="questionType1"
                                      name="questionType1">${list.description?default('')}</label>
                    </#if>
                </#list>
                    <input type="text" name="questionType2" class="validate[custom[noSpecial]]"
                           id="questionType2"
                           maxlength="20" <#if questionType?has_content><#list questionType as type><#if type.isStandard="N">value="${type.questionType?default('')}"</#if></#list></#if>>

                <#--<#list questionType as list>-->
                <#--<input type="checkbox" class="form-control" id="questionType" name="questionType">${list.questionType}</input>-->
                <#--</#list>-->
                <#--<input type="checkbox" class="form-control" id="questionType" name="questionType"/>-->
                </div>
            </div>

            <div class="form-group col-md-6">
                <label class="control-label">相关附件</label>
            <#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
            <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
            <#assign fileSize = fileSize?number * 1048576>
                <div>
            <input type="text" name="fileFieldName" id="fileFieldName" value=${dataResourceId?default('')} data-btn-style="float:right;" data-btn-style="float:right" data-size-limit="${fileSize}" style="width:1px;height:1px;">
                </div>
            </div>
        <div class="form-group col-md-6">
        <div>
        <label class="control-label">
        悬赏积分
        </label>
<#if question?has_content>
    ${(question.integral?default(0))?string}分
<#else>
            <#if maxScore &gt; 0>
                <input type="text" name="integral" style="width: 50px" class="validate[min[0],max[${maxScore?string}],custom[integer]]"
                       <#if question?has_content && question.integral?has_content>value="${question.integral?default('')}"</#if>
                       id="integral">(最高可悬赏 ${maxScore?string}分)
            <#else>&nbsp;您当前无可赠送积分
            </#if>
</#if>
        </div>
        </div>
            <div class="form-group col-md-12" style="text-align: center">
                <div class="margiv-top-10">
                <#if question?has_content>
                    <input type="hidden" id="${question.questionId}" name="questionId" value="${question.questionId}">
                    <a href="javascript:saveQuestion();" class="btn blue">保存</a>
                <#else>
                    <a href="javascript:handelQuestion();" class="btn green"> 提交 </a>
                </#if>
                </div>
            </div>
        </form>
    </div>
</div>
<div class="note note-info">
            <pre>
                温馨提示
            <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
                1.<span style="color: red">上传文件总大小请不要超过${fileSize?default("50")}兆</span>
                2.<span style="color: red">文件名称不要超过50个字符。</span>
            </pre>
</div>
