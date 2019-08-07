<style type="text/css">
    .media-body:hover  button {
        display: block !important;
    }
    .btn.btn-md{
        line-height: 14px!important;
    }
</style>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>


<script id="answerTpl" type="text/html">
    <div class="media">
        <div class="media-left">
            <a href="#">
                <img class="media-object" alt="" src="/content/control/partyAvatar?partyId=${userLogin.partyId}&externalLoginKey=${externalLoginKey}"></a>
        </div>
        <div class="media-body" style="width:1000px;word-break: break-all;">
            <h4 class="media-heading">
                <a href="#">
                ${context.userLogin.userLoginId?default('')}
                </a> @
                <span class="c-date">{{createdStamp}}</span>
            </h4>
            {{answerContent}}
        </div>
    </div>
</script>

<script type="text/javascript">
    $(window).load(function(){
        parent.adjustContentFrame();
    })
    $(function () {
        $("#messageForm").validationEngine("attach", {promptPosition: "topLeft"});
    })
    function handelAnswer(obj) {
        var data = {
            questionId: $(obj).attr("id"),
            answerContent: $("#answerContent").val(),
        }
        if($("#messageForm").validationEngine("validate")) {
            $.ajax({
                type: 'POST',
                url: 'handelAnswer',
                async: false,
                dataType: 'json',
                data: data,
                success: function (data) {
//                console.log(data);
                    var content = "";
                    for (var i = 0; i < data.data.length; i++) {
                        var parameters = {
                            answerFrom: data.data[i].answerFrom,
                            createdStamp: data.data[i].createdStamp,
                            answerContent: data.data[i].answerContent
                        }
                        content = Mustache.render($("#answerTpl").html(), parameters);
                    }
                    $("#aaa").append(content);
                    $("#answerContent").val("");
                    parent.adjustContentFrame();
                }
            });
        }
    }
</script>


<script type="text/javascript">
    function getAnswer(obj) {
        $.ajax({
            type: 'POST',
            url: 'getAnswer',
            dataType: 'json',
            data: {answerId: $(obj).attr("id")},
            success: function (content) {
                showInfo(content.msg);
                location.href = "WendaHistory?iframe=true";
            }
        });
    }


    function giveAnswer(obj, questionId) {
        layer.prompt({
                    title: '请回复',
                    formType: 2,
                }, function (value, index) {
                    if (!/^\s+$/gi.test($("#infoId").val()) && !/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test($("#infoId").val())) {
                        $.ajax({
                            type: 'POST',
                            url: 'handelAnswer',
                            async: false,
                            dataType: 'json',
                            data: {answerTo: $(obj).attr("id"), questionId: questionId, answerContent: value},
                            success: function (data) {
                                var content = "";
                                for (var i = 0; i < data.data.length; i++) {
                                    var parameters = {
                                        answerFrom: data.data[i].answerFrom,
                                        createdStamp: data.data[i].createdStamp,
                                        answerContent: data.data[i].answerContent,
                                        answerTo: data.data[i].answerTo
                                    }
                                    content = Mustache.render($("#answerTpl").html(), parameters);
                                }
                                $(obj).parent().append(content);
                                layer.close(index);
                                //$("(#aaa)>div").html(content);
                            }
                        });
                    }else if (/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test($("#infoId").val())){
                        showError("不允许输入特殊字符！");
                    }else if (/^\s+$/gi.test($("#infoId").val())){
                        showError("请输入内容！");
                    }
                }
        );
    }

    function deliverScore(deliverTo) {
        displayInLayer('赠送积分', { requestUrl: 'DeliverScore', width: 400, data: {partyIdTo: deliverTo}});
    }
</script>
<div class="portlet light">
    <div class="portlet-body">
        <div class="blog-page blog-content-2">
            <div class="row">
                <div class="col-lg-12">
                    <div class="blog-single-content bordered blog-container" style="overflow: auto">
                    <div class="blog-single-head"  style="width:1000px;word-break: break-all;">
                        <#if find?has_content>
                    <#list find.keySet() as myKeys>
                        <#if myKeys.rejected?has_content>
                            <h1 class="blog-single-head-title">${myKeys.questionOverview}</h1>
                            <div class="blog-single-head-date">
                                <i class="icon-calendar font-blue"></i>
                                <a href="javascript:;">${myKeys.createdStamp}</a>
                            </div>
                            <ul class="list-inline">
                                <li>
                                    <i class="fa fa-tag font-blue"></i><#list find.get(myKeys) as list2>
                                    <#list description as list>
                                        <#if list2.questionType?has_content&&list2.questionType=list.enumId>
                                        ${list.description}
                                        </#if>
                                    </#list>
                                    <#if list2.isStandard="N">
                                    ${list2.questionType?default('')}
                                    </#if>
                                </#list> </li>
                            </ul>
                        </div>
                            <div class="blog-single-desc">
                                <h3 class="sbold blog-comments-title">驳回理由</h3>
                                <p> ${myKeys.rejected}</p>
                            </div>
                            <#if context.userLogin.userLoginId=myKeys.userLoginId && myKeys.statusId != 'QUESTION_STATUS_COMPLETE'>
                            <div style="margin-top: 5px">
                                <input type="button" value="编辑" class="btn blue"
                                       onclick="displayInside('${request.contextPath}/control/NewQuestion?questionId=${myKeys.questionId}')">
                            </div>
                            </#if>
                        <#else>
                            <h1 class="blog-single-head-title">${myKeys.questionOverview?default("")}</h1>
                            <div class="blog-single-head-date">
                                <i class="icon-calendar font-blue"></i>
                                <a href="javascript:;">${myKeys.createdStamp?string("yyyy-MM-dd HH:mm:ss")}</a>
                            </div>
                            <ul class="list-inline">
                                <li>
                                    <i class="fa fa-tag font-blue"></i><#list find.get(myKeys) as list2>
                                    <#list description as list>
                                        <#if list2.questionType?has_content&&list2.questionType=list.enumId>
                                        ${list.description}
                                        </#if>
                                    </#list>
                                    <#if list2.isStandard="N">
                                    ${list2.questionType?default('')}
                                    </#if>
                                </#list> </li>
                            </ul>
                        </div>
                            <div class="blog-single-desc" style="width:1000px;word-break: break-all;">
                                 ${myKeys.questionDetail?default("")}
                            </div>
                            <#if fileList?has_content>
                                <div style="margin-bottom: 10px">
                                    <h3 class="sbold blog-comments-title">附件</h3>
                                    <div class="c-comment-list">
                                        <#list fileList as file>
                                            <div style="display: inline-block;padding: 5px; border-bottom: 1px solid lightseagreen;">
                                            <a style="text-decoration: none" href="javascript:viewPdfInLayer('${file.dataResourceId?default("")}')" title="${file.dataResourceName?default("")}"> ${file.dataResourceName?default("")}</a>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </#if>
                            <#if context.userLogin.userLoginId=myKeys.userLoginId && myKeys.statusId != 'QUESTION_STATUS_COMPLETE'>
                            <div style="margin-top: 5px">
                                <input type="button" value="编辑" class="btn blue"
                                       onclick="displayInside('${request.contextPath}/control/NewQuestion?questionId=${myKeys.questionId}')">
                            </div>
                            </#if>
                        </#if>
                        <#if answerMap?has_content>
                            <div class="blog-comments">
                                <h3 class="sbold blog-comments-title">采纳回答</h3>
                                <div class="c-comment-list" id="aaa">

                                        <#assign answerParty = delegator.findOne("UserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId", answerMap.answerFrom), false) />
                                        <#if answerMap.answerTo?has_content>
                                        <#else>
                                            <div class="media">
                                                <div class="media-left">
                                                    <a href="#">
                                                        <img class="media-object" alt=""
                                                             src="/content/control/partyAvatar?partyId=${answerParty.partyId}&externalLoginKey=${externalLoginKey}"></a>
                                                </div>
                                                <div class="media-body" style="width:1000px;word-break: break-all;">
                                                    <h4 class="media-heading">
                                                        <a href="#">
                                                            <#if myKeys.isAnonymous?has_content && myKeys.isAnonymous == "Y" && (userMap?has_content) && (answerMap.answerFrom!=userMap.userLogin.userLoginId)&&(myKeys.userLoginId!=userMap.userLogin.userLoginId)>
                                                                匿名
                                                            <#else>${answerMap.answerFrom?default('')}
                                                            </#if></a> @
                                                        <span class="c-date">${answerMap.createdStamp}</span>
                                                    </h4>
                                                ${answerMap.answerContent}
                                            </div>
                                        </#if>
                                </div>
                            </div>
                        </#if>
                        <div class="blog-comments">
                            <h3 class="sbold blog-comments-title">回答</h3>
                            <div class="c-comment-list" id="aaa">
                                <#list show as content>
                                    <#assign answerParty = delegator.findOne("UserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId", content.answerFrom), false) />
                                    <#if content.answerTo?has_content>
                                    <#else>
                                        <div class="media">
                                            <div class="media-left">
                                                <a href="#">
                                                    <img class="media-object" alt=""
                                                         src="/content/control/partyAvatar?partyId=${answerParty.partyId}&externalLoginKey=${externalLoginKey}"></a>
                                            </div>
                                            <div class="media-body" style="width:1000px;word-break: break-all;position: relative">
                                                <h4 class="media-heading">
                                                    <a href="#">
                                                        <#if myKeys.isAnonymous?has_content && myKeys.isAnonymous == "Y" && (content.answerFrom!=context.userLogin.userLoginId)&&(myKeys.userLoginId!=context.userLogin.userLoginId)>
                                                            匿名
                                                        <#else>${content.answerFrom?default('')}
                                                        </#if></a> @
                                                    <span class="c-date">${content.createdStamp}</span>
                                                </h4>
                                            <span>${content.answerContent!}</span>
                                                <span style="position: absolute;right: 0;">
                                                <#if content.isAdopt="N">
                                                    <#if content.answerFrom!=context.userLogin.userLoginId>
                                                        <#if myKeys.userLoginId==context.userLogin.userLoginId>
                                                            <#if  myKeys.statusId=="QUESTION_STATUS_WAIT_ANSWER">
                                                            <button id="${content.answerId}"
                                                                    style="display: none;float:right"
                                                                    type="button" class="btn blue btn-md sbold"
                                                                    onclick="getAnswer(this)"><span>采纳</span>
                                                            </button>
                                                            </#if>
                                                        </#if>
                                                    </#if>
                                                <#else>
                                                    <button id="${content.answerId}" style="display: none;float:right"
                                                            type="button" class="btn blue btn-md sbold"
                                                            onclick="getAnswer(this)" disabled>已采纳
                                                    </button>
                                                </#if>
                                                <#if content.answerFrom!=context.userLogin.userLoginId>

                                                    <#if  myKeys.statusId != 'QUESTION_STATUS_COMPLETE'>
                                                        <button id="${content.answerId}" style="display: none;float:right"
                                                            type="button" class="btn blue btn-md sbold"
                                                            onclick="giveAnswer(this,${myKeys.questionId})">回复
                                                    </button>
                                                     </#if>
                                                    <button id="${content.answerId}" style="display: none;float:right"
                                                            type="button" class="btn green btn-md sbold"
                                                            onclick="deliverScore('${answerParty.partyId}')">送分
                                                    </button>
                                                </#if>
                                                </span>
                                            <#--回复位置-->
                                                <#list show as content1>
                                                    <#assign answerParty1 = delegator.findOne("UserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId", content1.answerFrom), false) />
                                                    <#if content1.answerTo?has_content>
                                                        <#if content.answerId=content1.answerTo>
                                                            <div class="media">
                                                                <div class="media-left" style="width:10px;word-break: break-all;">
                                                                    <a href="#">
                                                                        <img class="media-object" alt=""
                                                                             src="/content/control/partyAvatar?partyId=${answerParty1.partyId}&externalLoginKey=${externalLoginKey}"></a>
                                                                </div>
                                                                <div class="media-body" style="width:1000px;word-break: break-all;">
                                                                    <h4 class="media-heading">
                                                                        <a href="#">
                                                                        ${content1.answerFrom!}</a> @
                                                                        <span class="c-date">${content1.createdStamp}</span>
                                                                    </h4>
                                                                ${content1.answerContent!}
                                                                    <#if content1.isAdopt="N">
                                                                        <#if content1.answerFrom!=context.userLogin.userLoginId>
                                                                            <#if myKeys.userLoginId==context.userLogin.userLoginId>
                                                                            <#if  myKeys.statusId=="QUESTION_STATUS_WAIT_ANSWER">
                                                                                <button id="${content1.answerId}"
                                                                                        style="display: none;float:right"
                                                                                        type="button"
                                                                                        class="btn blue btn-md sbold"
                                                                                        onclick="getAnswer(this)">采纳
                                                                                </button>
                                                                            </#if>
                                                                            </#if>
                                                                        </#if>
                                                                    <#else>
                                                                        <button id="${content1.answerId}"
                                                                                style="display: none;float:right"
                                                                                type="button"
                                                                                class="btn blue btn-md sbold"
                                                                                onclick="getAnswer(this)" disabled>已采纳
                                                                        </button>
                                                                    </#if>
                                                                </div>
                                                            </div>
                                                        </#if>
                                                    </#if>
                                                </#list>
                                            </div>
                                        </div>
                                    </#if>
                                </#list>
                            </div>
                        </div>
                        <#if find?has_content>
                            <#list find.keySet() as myKeys>
                            <#if myKeys.statusId=="QUESTION_STATUS_WAIT_ANSWER">
                        <h3 class="sbold blog-comments-title">我要回答</h3>
                        <form action="#" id="messageForm">
                            <div class="form-group">
                <textarea rows="8" name="message" placeholder="回答内容不超过1000个字符" maxlength="1000" class="form-control c-square validate[required,custom[noSpecial]]"
                          id="answerContent"></textarea>
                            </div>
                            <div class="form-group">
                                <button id="${myKeys.questionId}" type="button"
                                        class="btn blue uppercase btn-md sbold btn-block"
                                        onclick="handelAnswer(this)">提交
                                </button>
                            </div>
                        </form>
                            </#if>
                            </#list>
                        </#if>
                    </#list>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>