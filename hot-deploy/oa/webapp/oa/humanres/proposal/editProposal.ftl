<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="proposalContent"]', {
            allowFileManager: true
        });
    });
    function addItem(id,name){
        $("#ProposalSearchForm_proposalType").append('<option value="'+id+'">'+name+'</option>');
    }

</script>
<div>
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top"><img
                        src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="proposalForm" id="proposalForm" class="basic-form">
        <div>
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                改善提案
            </div>

            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                            <tr>
                                <td class="label">　
                                    <label><span class="requiredAsterisk">*</span>提案编号：</label>
                                </td>
                                <td class="jqv">
                                    <#if data.proposal?has_content>
                                        <input name="proposalNumber" type="text" class="validate[required]" value="${data.proposal.proposalNumber?default('')}"/>
                                    <#else>
                                        <input name="proposalNumber" type="text" class="validate[required]" value="${data.proposalNumber?default('')}"/>
                                    </#if>
                                </td>

                                <td class="label">
                                    <label><span class="requiredAsterisk">*</span>提案类别：</label>
                                </td>
                                <td class="jqv">
                                    <#if data.proposal?has_content>
                                        <@dropDownList dropDownValueList=data.proposalTypeList selectedValue="${data.proposal.proposalType?default('')}" enumTypeId="PROPOSAL_TYPE" inputSize="15"  name="proposalType" callback="addItem"/>
                                        <input type="hidden" name="proposalId" value="${data.proposal.proposalId?default('')}">
                                   <#else>
                                        <@dropDownList dropDownValueList=data.proposalTypeList enumTypeId="PROPOSAL_TYPE" inputSize="15"  name="proposalType" callback="addItem"/>
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label for="Proposal_c" id="Proposal_c_title">提交人：</label>
                                </td>
                                <td>
                                    <#if data.proposal?has_content>
                                        <@chooser name="submitPerson" chooserType="LookupEmployee" chooserValue="${data.proposal.submitPerson?default('')}"/>
                                    <#else>
                                        <@chooser name="submitPerson" chooserType="LookupEmployee"/>
                                    </#if>
                                </td>

                                <td class="label">　
                                    <label><span class="requiredAsterisk">*</span>跟进部门：</label>
                                </td>
                                <td class="jqv">
                                    <#if data.proposal?has_content>
                                        <@chooser name="department" chooserType="LookupDepartment" chooserValue="${data.proposal.department?default('')}"/>
                                    <#else>
                                        <@chooser name="department" chooserType="LookupDepartment"/>
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label><span class="requiredAsterisk">*</span>提案标题：</label>
                                </td>
                                <td colspan="4" class="jqv">
                                    <#if data.proposal?has_content>
                                        <input type="text" name="proposalTitle" class="validate[required,custom[onlyLetterNumberChinese]]" value="${data.proposal.proposalTitle?default('')}">
                                    <#else>
                                        <input type="text" name="proposalTitle" class="validate[required,custom[onlyLetterNumberChinese]]">
                                    </#if>
                                </td>
                            </tr>

                            <tr>
                                <td class="label">　
                                    <label><span class="requiredAsterisk">*</span>发放范围：</label>
                                </td>
                                <td colspan="4" class="jqv">
                                    <#if data.proposal?has_content>
                                        <@dataScope id="Proposal_canSeePerson" name="canSeePerson" dataId="${data.proposal.proposalId?default()}" entityName="TblProposal" required=true/>
                                    <#else>
                                        <@dataScope id="Proposal_canSeePerson" name="canSeePerson" dataId="" entityName="TblProposal" required=true/>
                                    </#if>
                                </td>
                            </tr>

                            <tr>
                                <td class="label">　
                                    <label><span class="requiredAsterisk">*</span>现状描述：</label>
                                </td>
                                <td colspan="4" class="jqv">
                                    <#if data.proposal?has_content>
                                        <textarea name="nowDescript">${data.proposal.nowDescript?default('')}</textarea>
                                    <#else>
                                        <textarea name="nowDescript"></textarea>
                                    </#if>
                                    <span class="tooltip">对现状的具体说明和分析</span>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label><span class="requiredAsterisk">*</span>提案内容：</label>
                                </td>
                                <td colspan="4" class="jqv">
                                    <#if data.proposal?has_content>
                                        <textarea name="proposalContent">${data.proposal.proposalContent?default('')}</textarea>
                                    <#else>
                                        <textarea name="proposalContent"></textarea>
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label>预期效果：</label>
                                </td>
                                <td colspan="4">
                                    <#if data.proposal?has_content>
                                        <textarea name="futureEffect">${data.proposal.futureEffect?default('')}</textarea>
                                    <#else>
                                        <textarea name="futureEffect"></textarea>
                                    </#if>
                                    <span class="tooltip">预计在哪些方面可以取得成效</span>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                </tbody>
                <tr align="center">
                    <td>
                        <a href="#" class="smallSubmit" title="提交" onclick="$.proposal.saveProposal()">提交</a>
                    </td>
                </tr>
            </table>

        </div>
    </form>
</div>