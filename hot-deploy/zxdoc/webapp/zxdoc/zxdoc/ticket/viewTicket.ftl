<script type="application/javascript">
    $(function () {
        $("#ticketContent").html(unescapeHtmlText('${ticketInfo.description?default("")}'));
    })
</script>
<div class="portlet light">
    <div class="portlet-body">
        <div class="blog-page blog-content-2">
            <div class="row">
                <div class="col-lg-12">
                    <div class="blog-single-content bordered blog-container" style="height: 600px;overflow: auto">
                        <div class="blog-single-head">
                            <h1 class="blog-single-head-title">${ticketInfo.title}</h1>
                            <div class="blog-single-head-date">
                                <i class="icon-calendar font-blue"></i>
                                <a href="javascript:;">${ticketPartyName} ${ticketInfo.startTime}</a>
                            </div>
                        </div>
                        <div class="blog-single-desc">
                            <p id="ticketContent"></p>
                        </div>
                        <#--<div class="blog-comments">-->
                            <#--<h3 class="sbold blog-comments-title">竞选(30)</h3>-->
                            <#--<div class="c-comment-list">-->
                                <#--<div class="media">-->
                                    <#--<div class="media-left">-->
                                        <#--<a href="#">-->
                                            <#--<img class="media-object" alt="" src="/metronic-web/avatars/team1.jpg"> </a>-->
                                    <#--</div>-->
                                    <#--<div class="media-body">-->
                                        <#--<h4 class="media-heading">-->
                                            <#--<a href="#">中喜</a> @-->
                                            <#--<span class="c-date">2016-07-18 10:10</span>-->
                                        <#--</h4>-->
                                        <#--竞选内容描述-->
                                    <#--</div>-->
                                <#--</div>-->
                                <#--<div class="media">-->
                                    <#--<div class="media-left">-->
                                        <#--<a href="#">-->
                                            <#--<img class="media-object" alt="" src="/metronic-web/avatars/team3.jpg"> </a>-->
                                    <#--</div>-->
                                    <#--<div class="media-body">-->
                                        <#--<h4 class="media-heading">-->
                                            <#--<a href="#">XX</a> @-->
                                            <#--<span class="c-date">2016-07-18 9:30</span>-->
                                        <#--</h4> 竞选内容描述.-->
                                        <#--<div class="media">-->
                                            <#--<div class="media-left">-->
                                                <#--<a href="#">-->
                                                    <#--<img class="media-object" alt="" src="/metronic-web/avatars/team4.jpg"> </a>-->
                                            <#--</div>-->
                                            <#--<div class="media-body">-->
                                                <#--<h4 class="media-heading">-->
                                                    <#--<a href="#">苏宁环球</a> 回复-->
                                                    <#--<span class="c-date">2016-07-18 9:40</span>-->
                                                <#--</h4> 回复内容. </div>-->
                                        <#--</div>-->
                                    <#--</div>-->
                                <#--</div>-->
                                <#--<div class="media">-->
                                    <#--<div class="media-left">-->
                                        <#--<a href="#">-->
                                            <#--<img class="media-object" alt="" src="/metronic-web/avatars/team7.jpg"> </a>-->
                                    <#--</div>-->
                                    <#--<div class="media-body">-->
                                        <#--<h4 class="media-heading">-->
                                            <#--<a href="#">YY</a> @-->
                                            <#--<span class="c-date">2016-07-18 08:40</span>-->
                                        <#--</h4> 竞选内容描述. </div>-->
                                <#--</div>-->
                            <#--</div>-->
                            <#--<h3 class="sbold blog-comments-title">我要参与</h3>-->
                            <#--<form action="#">-->
                                <#--<div class="form-group">-->
                                    <#--<textarea rows="8" name="message" placeholder="竞选说明 ..." class="form-control c-square"></textarea>-->
                                <#--</div>-->
                                <#--<div class="form-group">-->
                                    <#--<button type="submit" class="btn blue uppercase btn-md sbold btn-block">提交</button>-->
                                <#--</div>-->
                            <#--</form>-->
                        <#--</div>-->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>