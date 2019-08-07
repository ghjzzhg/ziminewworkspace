<#if parameters.contracts?has_content>
<div>
    <table class="table table-striped table-bordered table-advance table-hover">
        <thead>
        <tr>
            <th>合同名称</th>
            <th>甲方</th>
            <th>乙方</th>
            <th>开始时间</th>
            <th>结束时间</th>
            <th>是否收费</th>
        </tr>
        </thead>
        <tbody>
    <#list parameters.contracts as contract>
        <tr>
            <td>
            ${contract.contractName}
            </td>
            <td>${contract.firstPartyName}
            </td>
            <td>${contract.secondPartyName}
            </td>
            <td>
            ${contract.startDate}
            </td>
            <td>
            ${contract.dateClose}
            </td>
            <td>
            <#if contract.isCharge=="Y">是<#else>否 </#if>
            </td>
        </tr>
    </#list>
    </tbody>
    </table>
</div>
<#else>
    <div class="step-line">
        <div>该参与方没有合同记录！</div>
    </div>
</#if>