<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/three-level-picker/css/three-level-picker.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript" src="/images/lib/three-level-picker/js/three-level-picker.js"></script>
<script type="text/javascript" src="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="/zxdoc/static/docTypes.js"></script>
<script type="text/javascript" src="/images/jquery/ui/js/jquery-ui-1.10.3.js"></script>
<link href="/images/jquery/ui/js/jquery-ui.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script type="application/javascript" language="JavaScript">
    /**
     * 下载文件
     */
//    function downloadFile(fileId){
//        var form=$("<form>");//定义一个form表单
//        form.attr("style","display:none");
//        form.attr("target","");
//        form.attr("method","post");
//        form.attr("action","/ckfinder/control/action?command=DownloadFile&fileId="+fileId);
//        $("body").append(form);//将表单放置在web中
//        form.submit();//表单提交
//    }

</script>
<div style="height: 600px;overflow-y: auto">
<table id="example" class="display no-footer dataTable" cellspacing="0" style="width: 100%;" role="grid">
    <thead>
    <tr style="background: #f7f7f7;border-radius: 2px;border: 1px solid #d2d2d2;color: #888;overflow: hidden;"
        role="row">
        <td class="sorting_disabled" rowspan="1" colspan="1" style="width: 250px;">文件名</td>
        <td class="sorting_disabled" rowspan="1" colspan="1" style="width: 50px;">创建人</td>
        <td class="sorting_disabled" rowspan="1" colspan="1" style="width: 50px;">版本号</td>
        <td class="sorting_disabled" rowspan="1" colspan="1">目录</td>
        <td class="sorting_disabled" rowspan="1" colspan="1" style="width: 150px;">创建时间</td>
    </tr>
    </thead>
    <tbody id="fileTable">
    <#if fileList?has_content>
        <#list fileList as line>
        <tr <#if line_index % 2 == 0>role='row' class='odd'</#if>>
            <td>
                <a href="#nowhere" onclick="viewPdfInLayer('${line.fileId}')"><span title="${line.fileName}" name="fileId_${line_index}"><#if line.fileName?length &gt; 15>${line.fileName}<#else>${line.fileName}</#if></span></a>
                <#if line.isLibrary=="N">
                    <a style="text-decoration:none" href="/content/control/downloadUploadFile?dataResourceId=${line.fileId}&externalLoginKey=${externalLoginKey?default('')}" title="下载"><i class="fa fa-download"></i> </a>
                <#else>
                    <a style="text-decoration:none" href="javascript:searchScore('${line.id?default('')}')" title="下载"><i class="fa fa-download"></i> </a>
                </#if>
            </td>
            <td>${line.createFullName?default('')}</td>
            <td>${line.fileVersion?default('')}</td>
            <td><#if line.filePath?length &gt; 40>${line.filePath?substring(0,40)}...<#else>${line.filePath}</#if></td>
            <td>${line.createTime?default('')}</td>
        </tr>
        </#list>
    </#if>
    </tbody>
</table>
</div>