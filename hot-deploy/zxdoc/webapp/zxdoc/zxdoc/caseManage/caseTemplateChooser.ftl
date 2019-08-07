<link rel="stylesheet" href="/images/lib/qtip/jquery.qtip.min.css">
<script type="text/javascript" src="/images/lib/qtip/jquery.qtip.min.js"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<script type="text/javascript">
    $(function(){
        $(".tip").qtip({
            position: {
                my: 'top left',  // Position my top left...
                at: 'bottom right', // at the bottom right of...
            },
            style: {
                classes: 'qtip-dark qtip-bootstrap'
            }
        })
        $(".portlet-body").attr("class", "portlet-body row");

        $(".portlet-body").on("shown.bs.collapse hidden.bs.collapse", function(){
            var openLayer = getLayer();
            openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
        })
    })
    function displayTemplate(templateName, templateId) {
        if(templateId){
            location.href = "FillCaseBasicInfo?iframe=true&templateName=" + templateName + '&templateId=' + templateId;
        }else{
            location.href = "FillBlankCaseBasicInfo?iframe=true";
        }
    }
    function  addPrivateTemplate(){
        displayInLayer('新模板', {requestUrl: 'NewCaseTemplate', data:{privateFlag:"true"}, height: '80%', width: '80%', end: function(){
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index);
        }})
    }
</script>
<div class="portlet light">
<#--<div class="portlet-title">
    <div class="caption font-red-sunglo">
        <i class="icon-settings font-red-sunglo"></i>
        <span class="caption-subject bold uppercase"> 有效模板</span>
    </div>
</div>-->
    <div class="portlet-body">
        <div class="mt-element-list">
            <div class="mt-list-head list-default green-seagreen">
                <div class="list-head-title-container">
                    <h3 class="list-title uppercase sbold">有效模板</h3>
                </div>
            </div>
            <div class="mt-list-container list-default ext-1 group <#if data.casePartyTypeId == "PARTY_GROUP">col-md-4<#else>col-md-12</#if>">
                <div class="mt-list-title uppercase">
                    <div class="panel-collapse collapse in" aria-expanded="true">
                        <ul>
                            <li class="mt-list-item done">
                                <a href="javascript:displayTemplate('', '');" class="tip">无模板CASE</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <#if data.casePartyTypeId == "PARTY_GROUP">
                <div class="mt-list-container list-default ext-1 group col-md-4">
                    <div class="mt-list-title uppercase">
                        <div class="panel-collapse collapse in" aria-expanded="true">
                            <ul>
                                <li class="mt-list-item done">
                                    <a href="javascript:addPrivateTemplate();" class="tip">新建模版</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </#if>
            <#if data.caseTemplates?has_content>
                <#list data.caseTemplates.keySet() as group>
                        <#assign eleSize = 0>
                        <#if data.caseTemplates.get(group)?has_content>
                            <#assign eleSize = data.caseTemplates.get(group).size()>
                            <div class="mt-list-container list-default ext-1 group col-md-4" style="<#if (group_index + 1) % 3 == 1>clear: left;</#if>">
                            <a class="list-toggle-container" data-toggle="collapse" href="#${group}" aria-expanded="true">
                                <div <#if group == "privateTemplate">style="background-color: #578EBE;</#if>" class="list-toggle done uppercase"> <#if group == "privateTemplate">自建模版<#else>${group}</#if>
                                    <span class="badge badge-default pull-right bg-white font-green bold">
                                    ${eleSize}
                                            </span>
                                </div>
                            </a>
                            <div class="panel-collapse collapse" id="${group}" aria-expanded="true">
                                <ul>
                                    <#list data.caseTemplates.get(group) as template>
                                        <li class="mt-list-item done">
                                            <div class="list-item-content">
                                                <h3 class="uppercase">
                                                    <a href="javascript:displayTemplate('${template.templateName}', '${template.id}');" class="tip" title="${template.remark!}">${template.templateName}-V${template.version!1}</a>
                                                    <#if template.dataResourceId?has_content>
                                                        <a href="javascript:viewPdfInLayer('${template.dataResourceId}');" class="fa fa-info font-red" title="说明">&nbsp;</a>
                                                    </#if>
                                                    <#if template.partyId?has_content && data.casePartyTypeId == "PARTY_GROUP">
                                                            <a title="修改" href="javascript:$.caseManage.editCaseTemplate('${template.id}','true')">
                                                                <i class="fa fa-pencil-square-o"></i>
                                                            </a>
                                                            <a title="删除" href="javascript:$.caseManage.deleteCaseTemplate('${template.id}','true')">
                                                                <i class="icon-close"></i>
                                                            </a>
                                                    </#if>
                                                </h3>
                                            </div>
                                        </li>
                                    </#list>
                                </ul>
                            </div>
                            </div>
                        </#if>
                </#list>
            </#if>
        </div>
    </div>
</div>