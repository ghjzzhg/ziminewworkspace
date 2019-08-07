<div id="listQuestions">
<#--根据问题类型查询问题-->
    <script type="text/javascript" src="/images/lib/twbsPagination/jquery.twbsPagination.min.js"></script>
<script type="text/javascript">
    function searchTypeQuestion(questionType, page) {
        $("#searchType").val(questionType);
        $.ajax({
            type: 'GET',
            url: "ListQuestions",
            async: true,
            dataType: 'html',
            data: {questionType:questionType, filterParty: $("#filterParty").val(),isPage:"Y", page: page},
            success: function (content) {
                var contentHtml = $(content).find("#questionContent").html();
                $("#questionContent").html($(contentHtml));
                if(!page){
                    destroyPaginator();
                    initPaginator();
                }
                parent.adjustContentFrame();
            }
        });
    }

    function initPaginator(){
        var totalPage = parseInt($("#totalPage").val());
        window.pageObj = $('#pagination').twbsPagination({
            totalPages: totalPage == 0 ? 1 : totalPage,
            visiblePages: 10,
            first:"<<",
            last:">>",
            prev:"<",
            next:">",
            initiateStartPageClick: false,
            hideOnlyOnePage: true,
            onPageClick: function (event, page) {
                var questionType = $("#searchType").val() || "";
                searchTypeQuestion(questionType, page);
            }
        }).on('page', function (event, page) {
        });
    }
    function destroyPaginator(){
        if(window.pageObj){
            $('#pagination').twbsPagination("destroy");
        }
    }
<#if !filterParty?has_content>
    $(function () {
        initPaginator();
        parent.adjustContentFrame();
    });
    </#if>
</script>
<style type="text/css">
    .btn-group.question-type label {
        margin-left: 10px !important;
    }
</style>

<div class="portlet light">
    <input type="hidden" id="filterParty" value="${filterParty?default("")}">
    <input type="hidden" id="searchType" value="">
    <#if !filterParty?has_content>
        <#if parameters.questionTypeList?has_content>
            <div class="portlet-body">
                <div>
                    <div class="btn-group question-type" data-toggle="buttons">
                            <label class="btn blue active" name="questionType" onclick="searchTypeQuestion('')" id="questionTypeAll">
                            <input type="radio" class="toggle"> 全部</label>
                            <#list parameters.questionTypeList as questionType>
                                <label class="btn blue" onclick="searchTypeQuestion('${questionType.enumId}')">
                                    <input type="radio" name="questionType" class="toggle" value="${questionType.enumId}"> ${questionType.description} </label>
                            </#list>
                    </div>
                </div>
        </#if>
    </#if>
        <div class="table-scrollable table-scrollable-borderless" id="questionContent">
            <input type="hidden" id="totalPage" value="${totalPage?string}">
            <table class="table table-hover table-light">
                <tr>
                    <td style="padding: 0"></td>
                    <td style="width: 50px;padding: 0"></td>
                    <td style="width: 150px;padding: 0"></td>
                </tr>
            <#if questionList?has_content>
                <#list questionList as question>
                    <tr>
                        <td>
                            <a href="#nowhere"
                               onclick="displayInside('${request.contextPath}/control/AnswerQuestion?questionId=${question.questionId}')">
                        <#if isPage?has_content && isPage=="Y">
                            ${question.questionOverview?default('')}
                        <#else>
                            <#assign nameLength = StringUtil.xmlDecodedLength(question.questionOverview!)>
                            <#if nameLength gt 50>
                            ${StringUtil.xmlDecodedSubstring(question.questionOverview!, 0, 50)}...
                            <#else>
                            ${question.questionOverview!}
                            </#if>
                        </#if>
                            </a>

                            <#if userLogin.partyId == question.targetUser?default('')>
                                <span class="label label-sm label-danger ">
                                    涉密
                                </span>
                            </#if>
                            <#if question.types?has_content>
                                <#list question.types as qType>
                                    <#assign qTypeName=qType.typeName?default(qType.questionType?default(''))>
                                    <#if qTypeName?has_content>
                                        <span class="label label-sm label-info ">
                                            ${qTypeName}
                                        </span>
                                    </#if>
                                </#list>
                            </#if>
                        </td>
                        <td style="white-space: nowrap">
                            回答${question.answerNum?string}
                        </td>
                        <td style="white-space: nowrap">
                            ${question.createdStamp?string("yyyy-MM-dd HH:mm:ss")}
                        </td>
                    </tr>
                </#list>
            </#if>
            </table>
        </div>
    <nav aria-label="Page navigation" style="text-align: center">
        <ul class="pagination" id="pagination"></ul>
    </nav>
    </div>
</div>
</div>