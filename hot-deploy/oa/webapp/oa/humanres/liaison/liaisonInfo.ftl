<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://oa/webapp/oa/humanres/liaison/informationReply.ftl"/>
<#include "component://oa/webapp/oa/humanres/liaison/auditInformationLiaison.ftl"/>
<#include "component://oa/webapp/oa/humanres/liaison/SignForLiaison.ftl"/>
<#include "component://oa/webapp/oa/humanres/liaison/replyInformationLiaison.ftl"/>
<div id="LiaisonInfo">
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
    <form name="" class="basic-form">
        <div>
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                工作联络单
            </div>
            <div>
                <div style="float: left">
                    <span class="label">发文单位：<b style="color:blue">${liaisonMap.departmentName}</b></span>
                </div>
                <div style="float: right">
                    <span class="label">编号：<b style="color:blue">${liaisonMap.number}</b></span><br>
                    <span class="label">日期：<b style="color:blue">${liaisonMap.createdStamp?string("yyyy-MM-dd HH:mm:ss")}</b></span>
                </div>
            </div>
            <div>
                <div>
                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tbody>
                            <td colspan="6">
                                <table cellpadding="0" cellspacing="0" border="1" width="100%" style="border-collapse: collapse">
                                    <tbody>
                                        <tr>
                                            <td class="label">
                                                <label>主　　送:</label>
                                            </td>
                                            <td>
                                            ${liaisonMap.mainPersonNameString?default('')}
                                            </td>
                                            <td class="label">
                                                <label>抄　　送:</label>
                                            </td>
                                            <td>
                                            ${liaisonMap.copyPersonNameString?default('')}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="label">
                                                <label>事　　由:</label>
                                            </td>
                                            <td colspan="3">
                                            ${liaisonMap.title?default('')}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="label">
                                                <label>发文意图:</label>
                                            </td>
                                            <td colspan="3">
                                            ${liaisonMap.contactListType?default('')}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="label">
                                                <label>联络单内容:</label>
                                            </td>
                                            <td colspan="3" id="content">
                                            </td>
                                            <script>
                                                $("#content").append(unescapeHtmlText('${liaisonMap.content?default("")}'));
                                            </script>
                                        </tr>
                                        <tr>
                                            <td class="label">
                                                <label>发文部门:</label>
                                            </td>
                                            <td>
                                            ${liaisonMap.departmentName?default('')}
                                            </td>
                                            <td class="label">
                                                <label>经办人:</label>
                                            </td>
                                            <td>
                                            <#assign person = delegator.findOne("Person",{"partyId":liaisonMap.fullName},false)>
                                            ${person.fullName?default('')}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="label">
                                                <label>是否回复:</label>
                                            </td>
                                            <td>
                                            <#assign ifResponse = liaisonMap.ifResponse?default('')>
                                            <#if ifResponse == 'yes'>是<#elseif ifResponse =='no'>否</#if>
                                            </td>
                                            <#if liaisonMap.ifResponse =='yes'>
                                                <td class="label">
                                                    <label>希望回复日期:</label>
                                                </td>
                                                <td>
                                                ${liaisonMap.responseTime?default('')}
                                                </td>
                                            </#if>
                                        </tr>
                                 </table>
                        </td>
                        </tbody>
                    </table>
                    </td>
                    </tr>
                    </tbody>
                    </table>
                </div>
                <#--<div class="screenlet-title-bar">
                    <ul>
                        <li class="h3">审核信息</li>
                    </ul>
                    <br class="clear">
                </div>
                <div id="auditInformationTableList_td">
                <@auditInformationTableList workSheetAuditInformationList=workSheetAuditInformationList liaisonMap=liaisonMap/>
                </div>-->
            </div>
        </div>
    </form>

</div>
