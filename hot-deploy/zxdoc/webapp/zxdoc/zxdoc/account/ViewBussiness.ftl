<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<script type="text/javascript">
    $(function () {
        $("a.attachment-image").fancybox({
            'overlayShow': false,
            'transitionIn': 'elastic',
            'transitionOut': 'elastic'
        });
    })
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase">企业信息</span>
        <#--<span class="label label-sm label-success">${parameters.data.description?default('')}</span>
        <span class="label label-sm label-success">${parameters.data.qualificationStatus?default('')}</span>-->
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-hover table-striped table-bordered">
            <tbody>
            <tr>
                <td> 类型</td>
                <td>
                ${partyGroup.description?default('')}
                </td>
            </tr>
            <tr>
                <td> 注册时间</td>
                <td>
                ${partyGroup.createdStamp?string("yyyy-MM-dd HH:mm:ss")}
                </td>
            </tr>
            <tr>
                <td> 联系人</td>
                <td>
                ${partyGroup.fullName?default('')}
                </td>
            </tr>
            <tr>
                <td> 联系电话</td>
                <td>
                ${partyGroup.contactNumber?default('')}
                </td>
            </tr>
            <tr>
                <td> 邮箱</td>
                <td>
                ${partyGroup.email?default('')}
                </td>
            </tr>
            <tr>
                <td>地址：</td>
                <td>${partyGroup.area?default("")} ${partyGroup.address1?default('')}</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>