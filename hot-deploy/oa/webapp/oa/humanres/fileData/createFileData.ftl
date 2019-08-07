<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>

<#if fileDataMap?has_content>
    <#assign fileDataId = fileDataMap.fileDataId?default('')>
    <#assign parentTypeName = fileDataMap.parentTypeName?default('')>
    <#assign sonTypeName = fileDataMap.sonTypeName?default('')>
    <#assign parentName = fileDataMap.parentName?default('')>
    <#assign sonName = fileDataMap.sonName?default('')>
    <#assign documentTitle = fileDataMap.documentTitle?default('')>
    <#assign documentNumber = fileDataMap.documentNumber?default('')>
    <#assign versionNumber = fileDataMap.versionNumber?default('')>
    <#assign feedback = fileDataMap.feedback?default('')>
    <#assign documentStatus = fileDataMap.documentStatus?default('')>
    <#assign releaseDepartment = fileDataMap.releaseDepartment?default('')>
    <#assign releaseDate = fileDataMap.releaseDate?default('')>
    <#assign modificationDate = fileDataMap.modificationDate?default('')>
    <#assign auditor = fileDataMap.auditor?default('')>
    <#assign remarks = fileDataMap.remarks?default('')>
    <#assign reason = fileDataMap.reason?default('')>
    <#assign oldFileDataId = fileDataMap.oldFileDataId?default('')>
    <#assign status = fileDataMap.status?default('')>
    <#assign browseStaff = fileDataMap.browseStaff?default('')>
    <#assign documentPublishingRange = fileDataMap.documentPublishingRange?default('')>
    <#assign documentContent = fileDataMap.documentContent?default('')>
    <#assign fileList = fileDataMap.fileList?default("")>
    <#assign fileId = fileDataMap.fileId?default("")>
    <#assign list = fileDataMap.list?default("")>
</#if>
<script language="javascript">
    var documentContent;
    var remarks;
    var reason;
    $(function () {
        documentContent = KindEditor.create('textarea[name="documentContent"]', {
            allowFileManager: true
        });
        remarks = KindEditor.create('textarea[name="remarks"]', {
            allowFileManager: true
        });
        reason = KindEditor.create('textarea[name="reason"]', {
            allowFileManager: true
        });
        var fileDataIds = '${fileDataId?default("")}';
        var fileId = '${fileId?default("")}';
        var documentStatus = '${documentStatus?default("")}';
        var feedback = '${feedback?default("")}';
        var parentTypeName='${parentTypeName?default("")}';
        var sonTypeName='${sonTypeName?default("")}';
        if('' != fileDataIds){
            $("#fileId").val(fileId);
            $("select[id='documentStatus'] option").each(function () {
                if(documentStatus == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[id='feedback'] option").each(function () {
                if(feedback == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[id='parentTypeName'] option").each(function () {
                if(parentTypeName == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[id='sonTypeName'] option").each(function () {
                if(sonTypeName == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
        };
    })
    function changeSonTypeName(obj) {
        var Id = obj.value;
        $.ajax({
            type: 'POST',
            url: "searchSonTypeName",
            async: true,
            dataType: 'json',
            data:{fileTypeId:Id},
            success: function (data) {
                var value, optStr,list=data.data.list,str="";
                if(list.length>0){
                    for(var n=0;n<list.length;n++){
                        value=list[n].fileTypeId;
                        optStr=list[n].typeName;
                        str+="<option value='"+value+"'>"+optStr+"</option>";
                        $("#sonTypeName").html(str);
                    }
                }else{
                    $("#sonTypeName").html(str);
                }
            }
        });
    }
</script>
<div class="yui3-skin-sam">
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
    <form name="fileDataCreateForm" id="fileDataCreateForm">
        <input type="hidden" value="${fileDataId?default('')}" name="fileDataId">
        <div style="width:100%;">
            <div>
                <div style="border:1px solid;">
                    <table style="width:100%" class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tr>
                            <td class="label">
                                <label for="parentTypeName"><b class="requiredAsterisk">*</b>文档类别：</label>
                            </td>
                            <td colspan="2">
                                <select id="parentTypeName" name="parentTypeName" onchange="changeSonTypeName(this)" class="validate[required]">
                                    <#if fileTypeList?has_content>
                                        <#list fileTypeList as status>
                                            <option value="${status.fileTypeId}">${status.typeName}</option>
                                        </#list>
                                    </#if>
                                </select>
                            </td>
                            <td class="label">
                                <label for="sonTypeName"><b class="requiredAsterisk">*</b>文档子类别：</label>
                            </td>
                            <td colspan="2">
                           <#if fileDataId?has_content>
                               <select id="sonTypeName" name="sonTypeName" >
                                   <#if list?has_content>
                                       <#list list as status>
                                           <option value="${status.fileTypeId}">${status.typeName}</option>
                                       </#list>
                                   </#if>
                               </select>
                           <#else>
                               <select id="sonTypeName" name="sonTypeName" class="validate[required]" >
                                   <#if fileTypeLists?has_content>
                                       <#list fileTypeLists as status>
                                           <option value="${status.fileTypeId}">${status.typeName}</option>
                                       </#list>
                                   </#if>
                               </select>
                           </#if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="documentTitle"><b class="requiredAsterisk">*</b>文档标题：</label>
                            </td>
                            <td colspan="2" class="jqv">
                                <input type="text" name="documentTitle" maxlength="10" id="documentTitle_title" value="${documentTitle?default('')}" class="validate[required,custom[onlyLetterNumberChinese]]"/>
                            </td>
                            <td class="label">
                                <label for="documentNumber"><b class="requiredAsterisk">*</b>文档编号：</label>
                            </td>
                            <td colspan="2" class="jqv">
                                <#if fileDataId?has_content>
                                    <input type="hidden" name="documentNumber" id="documentNumber_title" value="${documentNumber?default('')}" />${documentNumber?default('')}
                                <#else>
                                    <input type="hidden" name="documentNumber" id="documentNumber_title" value="${number?default('')}" />${number?default('')}
                                </#if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="feedback">可否反馈：</label>
                            </td>
                            <td colspan="2">
                                <select id="feedback" name="feedback">
                                <#if feedbackList?has_content>
                                    <#list feedbackList as status>
                                        <option value="${status.enumId}">${status.description}</option>
                                    </#list>
                                </#if>
                                </select>
                            </td>
                            <td class="label">
                                <label for="documentStatus">文档状态：</label>
                            </td>
                            <td colspan="3">
                                <select id="documentStatus" name="documentStatus">
                                <#if documentStatusList?has_content>
                                    <#list documentStatusList as status>
                                        <option value="${status.enumId}">${status.description}</option>
                                    </#list>
                                </#if>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="releaseDepartment"><b class="requiredAsterisk">*</b>发布部门：</label>
                            </td>
                            <td colspan="2" class="jqv">
                               <#if fileDataId?has_content>
                                   <@chooser chooserType="LookupDepartment" name="releaseDepartment" importability=true chooserValue="${releaseDepartment?default('')}" required=true/>
                                <#else>
                                   <@chooser chooserType="LookupDepartment" name="releaseDepartment" importability=true chooserValue="${department?default('')}" required=true/>
                                   <#--<@htmlTemplate.lookupField value="${department?default('')}" formName="fileDataCreateForm" name="releaseDepartment" id="releaseDepartment" fieldFormName="LookupDepartment" position="center"  className="validate[required]"/>-->
                                </#if>
                            </td>
                            <td class="label">
                                <label for="releaseDate"><b class="requiredAsterisk">*</b>发布日期：</label>
                            </td>
                            <td colspan="2" style="margin-right: 28px">
                            <@htmlTemplate.renderDateTimeField name="releaseDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
                            value="${releaseDate?default(.now)}" size="25" maxlength="30" id="releaseDate" dateType="" shortDateInput=false timeDropdownParamName=""
                            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                <script language="JavaScript"
                                        type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="fileId" id="fileId_title">附件：</label>
                            </td>
                            <td colspan="2">
                                <div>
                                <@showFileList id="accessory_div" hiddenId="fileId" fileList=fileList?default('')></@showFileList>
                                </div>
                            </td>
                            <td class="label">
                                <label for="auditor" ><b class="requiredAsterisk">*</b>审核人：</label>
                            </td>
                            <td colspan="2" class="jqv">
                            <@selectStaff id="auditor" name="auditor" value="${auditor?default('')}"  multiple=false required=true/>
                            </td>
                        </tr>
                        <#if !fileDataMap?has_content || status.equals("审核通过")>
                            <tr>
                                <td class="label">文档版本号</td>
                                <td>
                                    <input type="text" name="versionNumber" value="${versionNumber?default('')}" />
                                </td>
                                <td></td>
                                <td></td>
                            </tr>
                        </#if>
                        <tr>
                            <td class="label">
                                <label for="browseStaff" ><b class="requiredAsterisk">*</b>需确认浏览人员：</label>
                            </td>
                            <td colspan="10" class="jqv">
                            <@selectStaff id="browseStaff" name="browseStaff" value="${browseStaff?default('')}"  multiple=true required=true/>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="documentPublishingRange"><b class="requiredAsterisk">*</b>文档发布范围：</label>
                            </td>
                            <td colspan="10" class="jqv">
                            <@dataScope id="documentPublishingRange" name="documentPublishingRange" dataId="${fileDataId?default('')}" entityName="TblFileData" required=true/>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="remarks">备注说明：</label>
                            </td>
                            <td colspan="10">
                                <textarea style="height: 80px" name="remarks" style="width:100%"   id="remarks_title">${remarks?default('')}</textarea>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="documentContent">文档内容：</label>
                            </td>
                            <td colspan="10">
                                <textarea name="documentContent" id="documentContent_title" style="width:100%">${documentContent?default('')}</textarea>
                            </td>
                        </tr>
                        <#if fileDataMap?has_content>
                            <#if status.equals("审核通过")>
                                <tr>
                                    <td class="label">
                                        <label for="reason"><b class="requiredAsterisk">*</b>修订原因、内容、位置：</label>
                                    </td>
                                    <td colspan="10">
                                        <textarea name="reason" class="validate[required]" style="width:100%">${reason?default('')}</textarea>
                                        <input type="hidden" name="oldFileDataId" value="${oldFileDataId?default('')}">
                                        <input type="hidden" name="status" value="${status?default('')}">
                                    </td>
                                </tr>
                            <#else>
                                <tr style="display: none">
                                    <td class="label">
                                        <label for="reason">修订原因、内容、位置：</label>
                                    </td>
                                    <td colspan="10">
                                        <textarea name="reason" style="width:100%">${reason?default('')}</textarea>
                                        <input type="hidden" name="oldFileDataId" >
                                    </td>
                                </tr>
                            </#if>
                        <#else>
                            <tr style="display: none">
                                <td class="label">
                                    <label for="reason">修订原因、内容、位置：</label>
                                </td>
                                <td colspan="10">
                                    <textarea name="reason" style="width:100%">${reason?default('')}</textarea>
                                    <input type="hidden" name="oldFileDataId" >
                                </td>
                            </tr>
                        </#if>
                        <tr>
                            <td colspan="7" align="center">
                            <a href="#" class="smallSubmit" onclick="$.FileData.saveFileData()">提交</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>