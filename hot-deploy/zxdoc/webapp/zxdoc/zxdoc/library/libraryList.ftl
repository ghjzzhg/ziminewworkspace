<div class="portlet light">
    <div class="portlet-body">
        <div>
        <#if parameters.libraryData.resultMap?has_content>
            <#list parameters.libraryData.resultMap.keySet() as myKey>
                <#assign partyInfo = parameters.libraryData.partyInfoByUserLogin[myKey]>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">资料发布者</h3>
                    </div>
                    <div class="panel-body">
                        <div style="display: inline-block">
                            <div class="author-img">
                                <img class="avatar" height="80px" style="width: 80px" alt="" src="/content/control/partyAvatar?partyId=${partyInfo.partyId}&externalLoginKey=${externalLoginKey}"/>
                            </div>
                            <div class="author-text library-index">
                                <div>${partyInfo.fullName?default(partyInfo.groupName!)}&nbsp;<i class="font-green fa fa-vimeo" title="已认证"></i></div>
                                <div class="font-blue">注册会计师&nbsp;<i class="fa fa-thumbs-o-up"></i></div>
                            </div>
                        </div>
                        <div style="display: inline-block;vertical-align: top">
                            发布人信息介绍
                        </div>
                    </div>
                </div>
                <div class="panel panel-info">
                    <div class="panel-heading">
                        <h3 class="panel-title">资料信息</h3>
                    </div>
                    <div class="panel-body">
                        <#list parameters.libraryData.resultMap[myKey] as list>
                            <div class="files col-xs-6 col-md-4 library-index">
                                <div>
                                    <#if list.dataResourceName?index_of(".")!=-1>
                                        <#assign fileType = (list.dataResourceName?split("."))[list.dataResourceName?split(".")?size-1] >
                                        <#if fileType=="doc" || fileType=="docx">
                                            <i class="font-green fa fa-file-word-o"></i>
                                        <#elseif fileType=="xls" || fileType=="xlsx">
                                            <i class="font-green fa fa-file-excel-o"></i>
                                        <#elseif fileType=="gif" || fileType=="png" || fileType=="bmp" || fileType=="jpg" || fileType=="jpeg">
                                            <i class="font-green fa fa-file-image-o"></i>
                                        <#elseif fileType=="zip">
                                            <i class="font-green fa fa-file-zip-o"></i>
                                        <#elseif fileType=="ppt" || fileType=="pptx">
                                            <i class="font-green fa fa-file-powerpoint-o"></i>
                                        <#elseif fileType=="pdf">
                                            <i class="font-green fa fa-file-pdf-o"></i>
                                        <#else>
                                            <i class="font-green fa fa-file-text-o"></i>
                                        </#if>
                                    <#else>
                                        <i class="font-green fa fa-file-text-o"></i>
                                    </#if>
                                    <a href="javascript:viewPdfInLayer('${list.dataResourceId?default('')}')">${list.dataResourceName?default('')}</a>
                                    <#--<a style="text-decoration:none" href="/content/control/downloadUploadFile?dataResourceId=${list.dataResourceId?default('')}&externalLoginKey=${externalLoginKey?default('')}" title="下载"><i class="fa fa-download"></i> </a>-->
                                    <a style="text-decoration:none" href="javascript:searchScore('${list.id?default('')}')" title="下载"><i class="fa fa-download"></i> </a>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </#list>
        </#if>
        </div>
    </div>
</div>