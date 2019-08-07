<#if fileTypeMap?has_content>
    <#assign fileTypeId = fileTypeMap.fileTypeId?default('')>
    <#assign parentId = fileTypeMap.parentId?default('')>
    <#assign parentTypeName = fileTypeMap.parentTypeName?default('')>
    <#assign sonTypeName = fileTypeMap.sonTypeName?default('')>
</#if>
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
    <form name="addFileTypeForm" id="addFileTypeForm">
        <input type="hidden" value="${fileDataId?default('')}" name="fileDataId">
        <div style="width:100%;">
            <div>
                <div style="border:1px solid;">
                    <table style="width:100%" class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tr>
                            <td class="label">
                                <label for="parentTypeName"><b class="requiredAsterisk">*</b>文档父类别名称：</label>
                            </td>
                            <td colspan="2">
                            <#if fileTypeMap?has_content>
                                <input type="hidden" name="Id" id="Id_title" value="${parentId?default('')}" />
                                <input type="hidden" name="parentTypeName" id="parentTypeName_title" value="${parentTypeName?default('')}" />
                            ${parentTypeName?default('')}
                            <#else>
                                <input type="hidden" name="Id" id="Id_title" value="${parameters.parentId?default('')}" />
                                <input type="hidden" name="parentTypeName" id="parentTypeName_title" value="${parameters.typeName?default('')}" />
                               ${parameters.typeName}
                            </#if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="sonTypeName"><b class="requiredAsterisk">*</b>文档类别名称：</label>
                            </td>
                            <td colspan="2">
                            <#if fileTypeMap?has_content>
                                <input type="hidden" name="fileTypeId" id="Id_title" value="${fileTypeId?default('')}" />
                                <input type="text" name="sonTypeName" id="sonTypeName_title" maxlength="10" value="${sonTypeName?default('')}" class="validate[required,custom[onlyLetterNumberChinese]]"/>
                            <#else>
                                <input type="text" name="sonTypeName" id="sonTypeName_title" maxlength="10" value="" class="validate[required,custom[onlyLetterNumberChinese]]"/>
                            </#if>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="7" align="center">
                            <a href="#" class="smallSubmit" onclick="$.FileData.saveFileType()">提交</a>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>