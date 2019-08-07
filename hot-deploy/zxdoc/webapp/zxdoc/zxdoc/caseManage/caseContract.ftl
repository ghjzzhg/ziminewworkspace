<script type="text/javascript">
    function openContract(groupName) {
        displayInLayer('查看合同详情', {
            requestUrl: 'openContractInfo', height: '60%', width: "800px",data:{groupName:groupName,caseId:$("#caseId").val()}})
    }
</script>

<input type="hidden" value="${parameters.caseId}" id="caseId">
<#if parameters.hasContract?has_content>
    <#list parameters.hasContract as hasContract>
    <div class="row">
        <div class="col-md-12">
            <div class="tab-content">
                <div id="tab_1" class="tab-pane active mt-element-step">
                    <div class="step-line">
                        <div class="col-md-4 mt-step-col first done">
                            <div class="mt-step-number bg-white">甲</div>
                            <div class="mt-step-title uppercase font-grey-cascade">${parameters.group.groupName!}</div>
                            <div class="mt-step-content font-grey-cascade"></div>
                        </div>
                        <div class="col-md-4 mt-step-col done">
                            <div class="mt-step-number bg-white"><a href="#" onclick="openContract('${hasContract}')">合</a></div>
                            <div class="mt-step-title uppercase font-grey-cascade">已签约</div>
                        </div>
                        <div class="col-md-4 mt-step-col done last">
                            <div class="mt-step-number bg-white">乙</div>
                            <div class="mt-step-title uppercase font-grey-cascade">${hasContract}</div>
                            <div class="mt-step-content font-grey-cascade"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </#list>
</#if>

<#if parameters.groupName?has_content>
    <#list parameters.groupName as groupName>
    <div class="row">
        <div class="col-md-12">
            <div class="tab-content">
                <div id="tab_1" class="tab-pane active mt-element-step">
                    <div class="step-line">
                        <div class="col-md-4 mt-step-col first done">
                            <div class="mt-step-number bg-white">甲</div>
                            <div class="mt-step-title uppercase font-grey-cascade">${parameters.group.groupName!}</div>
                            <div class="mt-step-content font-grey-cascade"></div>
                        </div>
                        <div class="col-md-4 mt-step-col error">
                            <div class="mt-step-number bg-white">合</div>
                            <div class="mt-step-title uppercase font-grey-cascade">未签约</div>
                        </div>
                        <div class="col-md-4 mt-step-col done last">
                            <div class="mt-step-number bg-white">乙</div>
                            <div class="mt-step-title uppercase font-grey-cascade">${groupName}</div>
                            <div class="mt-step-content font-grey-cascade"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </#list>
</#if>
<#if !parameters.hasContract?has_content && !parameters.groupName?has_content>
<div class="portlet-title" align="center">
    <div class="caption font-red sbold" style="font-size: larger"> 暂时没有合同</div>
</div>
</#if>