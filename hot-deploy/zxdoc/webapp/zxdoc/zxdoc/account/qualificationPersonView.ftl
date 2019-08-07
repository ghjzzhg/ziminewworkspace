<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<script type="text/javascript">
    $(function(){
        $("a.attachment-image").fancybox({
            'overlayShow':false,
            'transitionIn':'elastic',
            'transitionOut':'elastic'
        });
    })
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 实名认证</span>
            <span class="label label-sm label-success">${parameters.data.qualificationStatus?default('')}</span>
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-hover table-striped table-bordered">
            <tbody>
            <tr>
                <td> 姓名</td>
                <td>
                    <div style="word-break: break-all">
                        ${parameters.data.info.fullName?default('')}
                    </div>
                </td>
            </tr>
            <tr>
                <td> 身份证</td>
                <td>
                ${parameters.data.info.idCard?default('')}
                </td>
            </tr>
            <tr>
                <td> 职业资格证号</td>
                <td>
                    <textarea class="form-control" rows="3">${parameters.data.info.qualifiNum?default('')}</textarea>
                </td>
            </tr>
            <tr>
                <td> 职业资格证扫描件</td>
                <td>
                <#if parameters.data.info.dataResourceId?has_content>
                    <a class="attachment-image" href="/content/control/imageView?fileName=${parameters.data.info.dataResourceId?default('')}&externalLoginKey=${externalLoginKey}">
                        <img src="/content/control/imageView?fileName=${parameters.data.info.dataResourceId?default('')}&externalLoginKey=${externalLoginKey}" width="200px">
                    </a>
                </#if>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>