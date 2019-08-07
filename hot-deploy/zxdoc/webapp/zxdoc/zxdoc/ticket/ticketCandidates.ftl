<style type="text/css">
    .media-body:hover button{
        display:block!important;
    }
    .btn.btn-md{
        line-height: 14px!important;
    }
</style>
<script type="text/javascript">
    function acceptParty(ticketId, partyId, roleTypeId, caseId) {
        $.ajax({
            url: "AcceptParty",
            type: "POST",
            data: {ticketId: ticketId, partyId: partyId, roleTypeId: roleTypeId, caseId: caseId},
            dataType: "json",
            success: function(content) {
                showInfo(content.data);
                closeCurrentTab();
            }
        })
    }
</script>
<div class="blog-content-2" style="padding: 0 5px;">
    <div class="blog-single-content" style="padding: 0;">
        <div class="blog-comments">
            <div class="c-comment-list">
                <#list partyReasons as partyReason>
                    <div class="media">
                        <div class="media-left">
                            <a href="#">
                                <#if partyReason.avatar?has_content>
                                    <img class="media-object" src="/content/control/imageView?dataResourceId=${partyReason.avatar}&externalLoginKey=${externalLoginKey}">
                                <#else>
                                    <img class="media-object" src="/metronic-web/layout/img/avatar.png">
                                </#if>
                            </a>
                        </div>
                        <div class="media-body" style="position: relative">
                            <h4 class="media-heading">
                                <a href="#">${partyReason.fullName}</a> @
                                <span class="c-date">${partyReason.runForTime}</span>
                            </h4>
                            ${partyReason.reason}
                            <span style="position: absolute;right: 0;">
                            <button onclick="acceptParty('${partyReason.ticketId}', '${partyReason.partyId}', '${partyReason.roleTypeId}', '${caseId?default('')}')" style="display: none;float:right" type="button" class="btn blue btn-md sbold">确认</button>
                            </span>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>
</div>