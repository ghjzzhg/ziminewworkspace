<script>
    /*function toEventPage() {
        var externalLoginKey = getExternalLoginKey();
        displayInside('/ckfinder/control/main?externalLoginKey='+externalLoginKey);
    }*/
</script>
<div class="portlet light tasks-widget ">
    <div class="portlet-body">
        <div class="task-content">
            <div class="scroller" style="height: 339px;" data-always-visible="1" data-rail-visible="0">
                <ul class="feeds">
                    <#if fileList?has_content>
                        <#list fileList as file>
                            <li>
                                <div class="col1">
                                    <div class="cont">
                                        <div class="cont-col1">
                                            <div class="label label-sm label-info">
                                                <i class="fa fa-info"></i>
                                            </div>
                                        </div>
                                        <div class="cont-col2">
                                            <div class="desc"><span class="label label-warning">${file.ownershipName?default('')} 《<a href="javascript:viewPdfInLayer('${file.fileId}')" title="${file.allFileName}"> ${file.fileName}</a>》</span>.
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="cont-col2">
                                    <div class="date"> ${file.fullName?default("")} ${file.createFileTime?string("yyyy-MM-dd HH:mm:ss")}<#if file.fileHidtoryFlag == "1">更新<#else>上传</#if></div>
                                </div>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
        </div>
    </div>
</div>