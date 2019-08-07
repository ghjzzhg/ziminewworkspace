<body>
<script type="text/javascript">
    //打开编辑邮件页面
    function editEmailInfo(id) {
        displayInLayer('邮件编辑', {requestUrl: '/zxdoc/control/editEmailInfo',data:{emailId:id}, height: '88%',width:'919px;'});
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">邮件管理</span>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
        <#if emailInfo?has_content>
            <#list emailInfo as list1>
            <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12 margin-bottom-10">
                <div <#if list1_index%3==0>class="dashboard-stat blue"<#elseif (list1_index-1)%3==0>class="dashboard-stat red"<#else>class="dashboard-stat green"</#if>>
                    <div class="visual">
                        <i class="fa fa-briefcase fa-icon-medium"></i>
                    </div>
                    <div class="details">
                        <div class="number"> <span style="font-size: 20px;">${list1.subject}</span> </div>
                        <div class="desc"> ${list1.description} </div>
                    </div>
                    <a class="more" href="#nowhere" onclick="javascript:editEmailInfo('${list1.emailTemplateSettingId}');"> 编辑
                        <i class="m-icon-swapright m-icon-white"></i>
                    </a>
                </div>
            </div>
            </#list>
        </#if>
        </div>
    </div>
</div>
</body>