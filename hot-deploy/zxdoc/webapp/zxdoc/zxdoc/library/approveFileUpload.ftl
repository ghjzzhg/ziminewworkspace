<style type="text/css">
    .media-body:hover > button {
        display: block !important;
    }
</style>
<script type="text/javascript">
    function changeLibraryStatus(obj, status) {
        var reason = $("#message").val();
        $.ajax({
            type: 'post',
            url: 'changeLibraryStatus',
            dataType: 'json',
            data: {id: $(obj).attr("id"), status: status, reason: reason},
            success: function (context) {
                showInfo(context.msg);
                displayInside("ApproveFileUploads")
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
                        <div class="blog-single-head">
                            <h1 class="blog-single-head-title">${parameters.data.title?default('')}</h1>
                            <div class="blog-single-head-date">
                                <i class="icon-calendar font-blue"></i>
                                <a href="javascript:;">${parameters.data.createdStamp?default('')}</a>
                            </div>
                            <ul class="list-inline">
                                <li>
                                    <i class="fa fa-tag font-blue"></i> ${parameters.data.description?default('')}
                                </li>
                            </ul>
                        </div>
                        <div class="blog-single-desc">
                            <p> ${parameters.data.introduce!} </p>


                        <div id="fileContent">
                            <button type="button" class="btn blue uppercase btn-md sbold" onclick="viewPdfInLayer('${dataResourceId}')">预览资料文件</button>
                            <#--<iframe frameborder="0" src="/content/control/FileHandler?dataResourceId=${dataResourceId}&externalLoginKey=${externalLoginKey}" width="100%" height="600px"></iframe>-->
                        <#--${screens.render("component://content/widget/content/DataResourceScreens.xml#FileHandler")}-->
                        </div>
                        </div>
                        <div class="blog-comments" style="width:100%">
                            <form action="#">
                                <div class="form-group">
                                    <textarea rows="8" name="message" id="message" placeholder="理由 ..."
                                              class="form-control c-square"></textarea>
                                </div>
                                <div class="form-group">
                                    <button type="button" id="${parameters.data.id?default('')}"
                                            class="btn blue uppercase btn-md sbold"
                                            onclick="changeLibraryStatus(this,'Y')">通过</button>
                                    <button type="button" id="${parameters.data.id?default('')}"
                                            class="btn grey uppercase btn-md sbold"
                                            onclick="changeLibraryStatus(this,'N')">驳回</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>







