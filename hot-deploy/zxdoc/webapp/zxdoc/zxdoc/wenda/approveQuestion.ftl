<style type="text/css">
    .media-body:hover > button {
        display: block !important;
    }
</style>

<#--改状态-->
<script type="text/javascript">
    function changeQuestionStatus(obj, statusId) {
        var rejected = $("#rejected").val();
        $.ajax({
            type: 'POST',
            url: 'changeQuestionStatus',
            dataType: 'json',
            data: {questionId: $(obj).attr("id"), statusId: statusId, rejected: rejected},
            success: function (content) {
                showInfo(content.msg)
            }
        });
    }
</script>

<div class="portlet light">
    <div class="portlet-body">
        <div class="blog-page blog-content-2">
            <div class="row">
                <div class="col-lg-12">
                    <div class="blog-single-content bordered blog-container" style="height: 600px;overflow: auto">
                    <#list find.keySet() as myKeys>
                        <div class="blog-single-head">
                            <h1 class="blog-single-head-title">${myKeys.questionOverview?default('')}</h1>
                            <div class="blog-single-head-date">
                                <i class="icon-calendar font-blue"></i>
                                <a href="javascript:;">${myKeys.createdStamp?default('')}</a>
                            </div>
                            <ul class="list-inline">
                                <li>
                                    <i class="fa fa-tag font-blue"></i> <#list find.get(myKeys) as list2>
                                    <#list description as list>
                                        <#if list2.questionType?has_content&&list2.questionType=list.enumId>
                                        ${list.description}
                                        </#if>
                                    </#list>
                                    <#if list2.isStandard="N">
                                    ${list2.questionType?default('')}
                                    </#if>
                                </#list></li>
                            </ul>
                        </div>
                        <div class="blog-single-desc">
                            <p> ${myKeys.questionDetail?default('')}</p>
                        </div>
                        <div class="blog-comments" style="width:100%">
                            <form action="#">
                                <div class="form-group">
                                    <textarea id="rejected" rows="8" name="message" placeholder="理由 ..."
                                              class="form-control c-square"></textarea>
                                </div>
                                <div class="form-group">
                                    <button id="${myKeys.questionId?default('')}" type="button"
                                            class="btn blue uppercase btn-md sbold"
                                            onclick="changeQuestionStatus(this,'QUESTION_STATUS_WAIT_ANSWER')">通过
                                    </button>
                                    <button id="${myKeys.questionId?default('')}" type="button"
                                            class="btn grey uppercase btn-md sbold"
                                            onclick="changeQuestionStatus(this,'QUESTION_STATUS_REJECTED')">驳回
                                    </button>
                                </div>
                            </form>
                        </div>
                    </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>