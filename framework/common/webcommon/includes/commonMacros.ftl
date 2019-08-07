<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#--
To use these macros in your template, insert the following line in
your template file:
<#include "component://common/webcommon/includes/commonMacros.ftl"/>
-->

<#assign
  dayValueList = Static["org.ofbiz.service.calendar.ExpressionUiHelper"].getDayValueList(locale)
  monthValueList = Static["org.ofbiz.service.calendar.ExpressionUiHelper"].getMonthValueList(locale)
/>

<#macro NullMacro></#macro>

<#macro DateField formName="" fieldName="" fieldValue="" fieldClass="">
  <#if javaScriptEnabled>
    <@htmlTemplate.renderDateTimeField name="${fieldName}" event="${event!}" action="${action!}" className="${fieldClass!''}" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${fieldValue!''}" size="25" maxlength="30" id="${fieldName}1" dateType="" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
  <#else>
      <input type="text" name="${fieldName}"<#if fieldValue?has_content> value="${fieldValue}"</#if><#if fieldClass?has_content> class="${fieldClass}"</#if> maxlength="25" size="25"/>
  </#if>
  <span class="tooltip">${uiLabelMap.CommonFormatDateTime}</span>
  <#if fieldClass == "required">
    <span class="tooltip">${uiLabelMap.CommonRequired}</span>
  </#if>
</#macro>

<#macro MonthField fieldName="" fieldValue=-1 fieldClass="">
  <select name="${fieldName}"<#if fieldClass?has_content> class="${fieldClass}"</#if>>
    <#list monthValueList as monthValue>
      <option value="${monthValue.value}"<#if monthValue.value == fieldValue> selected="selected"</#if>>${monthValue.description}</option>
    </#list>
  </select>
  <#if fieldClass == "required">
    <span class="tooltip">${uiLabelMap.CommonRequired}</span>
  </#if>
</#macro>

<#macro HourOfDayField fieldName="" fieldValue=-1 fieldClass="">
  <select name="${fieldName}"<#if fieldClass?has_content> class="${fieldClass}"</#if>>
    <#list 0..23 as i>
      <option value="${i}"<#if i == fieldValue> selected="selected"</#if>>${i}</option>
    </#list>
  </select>
  <#if fieldClass == "required">
    <span class="tooltip">${uiLabelMap.CommonRequired}</span>
  </#if>
</#macro>

<#macro MinuteField fieldName="" fieldValue=-1 fieldClass="">
  <select name="${fieldName}"<#if fieldClass?has_content> class="${fieldClass}"</#if>>
    <#list 0..59 as i>
      <option value="${i}"<#if i == fieldValue> selected="selected"</#if>>${i}</option>
    </#list>
  </select>
  <#if fieldClass == "required">
    <span class="tooltip">${uiLabelMap.CommonRequired}</span>
  </#if>
</#macro>

<#macro DayOfWeekField fieldName="" fieldValue=-1 fieldClass="">
  <select name="${fieldName}"<#if fieldClass?has_content> class="${fieldClass}"</#if>>
    <#list dayValueList as dayValue>
      <option value="${dayValue.value}"<#if dayValue.value == fieldValue> selected="selected"</#if>>${dayValue.description}</option>
    </#list>
  </select>
  <#if fieldClass == "required">
    <span class="tooltip">${uiLabelMap.CommonRequired}</span>
  </#if>
</#macro>

<#macro DayOfMonthField fieldName="" fieldValue=-1 fieldClass="">
  <select name="${fieldName}"<#if fieldClass?has_content> class="${fieldClass}"</#if>>
    <#list 1..31 as i>
      <option value="${i}"<#if i == fieldValue> selected="selected"</#if>>${i}</option>
    </#list>
  </select>
  <#if fieldClass == "required">
    <span class="tooltip">${uiLabelMap.CommonRequired}</span>
  </#if>
</#macro>

<#macro fieldErrors fieldName>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName, true, errorMessageList)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="errorMessage">${errorMsg}</li>
      </#list>
    </ul>
  </#if>
</#macro>

<#macro fieldErrorsMulti fieldName1 fieldName2 fieldName3 fieldName4>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName1, fieldName2, fieldName3, fieldName4, true, errorMessageList)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="errorMessage">${errorMsg}</li>
      </#list>
    </ul>
  </#if>
</#macro>

<#macro fileinput name files=[] oldFileInputName="" multiple=true multipleSuffix="files" thumbnail=false thumbnailWidth="" thumbnailHeight=150 selectText="选择文件" changeText="重新选择">
<div style="width: 100%;position:relative;<#if multiple>padding-right:40px;</#if>" data-suffix="${multipleSuffix}" class="fileinput-wrapper">
    <#if multiple>
  <i class="fa fa-plus font-green" style="font-size: 28px;position: absolute;bottom:10px;right:12px;cursor:pointer;" title="添加" onclick="addFileinput(this)"></i>
    </#if>
    <#if files?has_content>
        <#if !oldFileInputName?has_content>
            <#assign oldFileInputName="old" + name?cap_first/>
        </#if>
        <#list files as file>
            <#assign fileData=delegator.findOne("DataResource", {"dataResourceId": file.dataResourceId}, true)>
            <div>
                <input type="hidden" name="${oldFileInputName}-${file?index}" value="${fileData.dataResourceId}"/>
                <i class="fa fa-remove font-red" style="cursor: pointer;margin-right:10px" onclick="deleteOldFile(this)"></i><a href="#nowhere" onclick="">${fileData.dataResourceName}</a>
            </div>
        </#list>
    </#if>
<div class="fileinput-wrapper">
    <#if multiple>
    <i class="fa fa-minus font-red" style="cursor:pointer;" title="删除" onclick="deleteFileinput(this)"></i>
    </#if>
<#if thumbnail>
  <div class="fileinput fileinput-new" data-provides="fileinput">
      <div class="fileinput-preview thumbnail" data-trigger="fileinput" style="width: ${thumbnailWidth}px; height: ${thumbnailHeight}px;"> </div>
      <div>
          <span class="btn red btn-outline btn-file">
              <span class="fileinput-new"> ${selectText} </span>
              <span class="fileinput-exists"> ${changeText} </span>
              <input type="file" name="${name}<#if multiple>-0-${multipleSuffix}</#if>">
          </span>
          <a href="javascript:;" class="btn red fileinput-exists" data-dismiss="fileinput"> 删除 </a>
      </div>
  </div>
<#else>
  <div class="fileinput fileinput-new" data-provides="fileinput">
        <span class="btn green btn-file">
            <span class="fileinput-new"> ${selectText} </span>
            <span class="fileinput-exists"> ${changeText} </span>
            <input type="file" name="${name}<#if multiple>-0-${multipleSuffix}</#if>">
        </span>
      <span class="fileinput-filename" <#if thumbnailWidth?has_content>style="width: ${thumbnailWidth}px;overflow: auto"</#if>> </span> &nbsp;
      <a href="javascript:;" class="close fileinput-exists" data-dismiss="fileinput"> </a>
  </div>
</#if>
</div>
</div>
</#macro>

<#macro fileinputSelect name files=[] allowLocalUpload=true oldFileInputName="" multiple=true multipleSuffix="files" thumbnail=false thumbnailWidth=200 thumbnailHeight=150 selectText="选择文件" changeText="重新选择">
<#if !oldFileInputName?has_content>
    <#local oldFileInputName="old" + name?cap_first/>
</#if>
<div style="width: 100%;position:relative;padding-right:40px;" data-name="${name}" data-old-name="${oldFileInputName}" data-suffix="${multipleSuffix}" class="fileinput-wrapper fileinput-selection">
    <#if multiple>
  <i class="fa fa-upload font-green" style="font-size: 18px;position: absolute;bottom:0px;right:12px;cursor:pointer;" title="添加" onclick="openFileinputSelection(this, ${allowLocalUpload?string})"></i>
    </#if>
    <#if files?has_content>
        <div class="empty-selection hide">&nbsp;</div>

        <#list files as file>
            <#assign fileData=delegator.findOne("DataResource", {"dataResourceId": file.dataResourceId}, true)>
            <div class="file-row">
                <input type="hidden" name="${oldFileInputName}-${file?index}" value="${fileData.dataResourceId}"/>
                <i class="fa fa-remove font-red" style="cursor: pointer;margin-right:10px" onclick="deleteOldFile(this)"></i><a href="#nowhere" onclick="">${fileData.dataResourceName}</a>
            </div>
        </#list>
    <#else>
        <div class="empty-selection">&nbsp;</div>
    </#if>
</div>
</#macro>

<#macro fileinputMulti id name files=[] multiple=true selectText="添加附件" buttonWidth=80 buttonHeight=30 buttonClass="btn green" fileTypeLimit='*.*' fileSizeLimit='50MB' fileNumberLimit=10>
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<script type="text/javascript" src="/images/lib/uploadify/jquery.uploadify.min.js"></script>
<input type="hidden" name="${name}" id="fileIdField-${id}" value="">
<input type="file" id="uploadify-${id}">
<div id="fileQueue-${id}" class="file-queue">
    <#if files?has_content>
        <#list files as file>
            <#assign fileData=delegator.findOne("DataResource", {"dataResourceId": file.dataResourceId}, true)?default("")>
            <#if fileData?has_content>
                <div>
                    <div instanceid="uploadify-${id}" class="file-item" fileid="${file.dataResourceId}">
                        <span class="fileName">${fileData.dataResourceName}</span>
                        <span class="data"> - <span class="fa fa-close" title="删除" onclick="removeFileUpload('${file.dataResourceId}', afterFileUploadRemoved)"></span></span>
                    </div>
                </div>
            </#if>
        </#list>
    </#if>
</div>
<script type="text/javascript">
    <#if files?has_content>
    $(function(){
        var uploadFileIdArray = $("#fileIdField-${id}").data("uploadFileIdArray");
        if(!uploadFileIdArray){
            uploadFileIdArray = [];
        }
        <#list files as file>
            uploadFileIdArray.push('${file.dataResourceId}');
        </#list>
        $("#fileIdField-${id}").val(uploadFileIdArray.join(","));
        $("#fileIdField-${id}").data("uploadFileIdArray", uploadFileIdArray);
    })
    </#if>
    $("#uploadify-${id}").uploadify({
        'swf': '/images/lib/uploadify/uploadify.swf',
        'uploader':"/content/control/FineUploader?externalLoginKey=${externalLoginKey}",
        'fileObjName': '${name}',
        'method'   : 'post',
        'preventCaching' : false,
        'queueID': 'fileQueue-${id}',
        'auto': true,
        'multi': ${multiple?c},
        'width' : ${buttonWidth?c},
        'height' : ${buttonHeight?c},
        'buttonClass' : '${buttonClass}',
        'buttonText': '${selectText}',
        'fileSizeLimit' : '${fileSizeLimit}',
        'queueSizeLimit' : ${fileNumberLimit?c},
        'fileTypeExts': '${fileTypeLimit}',
        'overrideEvents': ['onUploadSuccess'],
        'onUploadSuccess' : function(file, data, response) {
            data = jQuery.parseJSON(data);
            var fileId = data.data.path;
            var uploadFileIdArray = $("#fileIdField-${id}").data("uploadFileIdArray");
            if(!uploadFileIdArray){
                uploadFileIdArray = [];
            }
            uploadFileIdArray.push(fileId);
            var fileIdField = $("#fileIdField-${id}");
            fileIdField.val(uploadFileIdArray.join(","));
            fileIdField.data("uploadFileIdArray", uploadFileIdArray);
            var fileItem = $('#' + file.id);
            fileItem.attr("fileId", fileId).find('.data').html(' - <span class="fa fa-close" title="删除" onclick="removeFileUpload(\'' + fileId + '\', afterFileUploadRemoved)"></span>');
            var fileType = file.type.toLowerCase().substring(1);
            if("png,jpg,jpeg,gif,bmp".indexOf(fileType) > -1) {//图片
                fileItem.find(".fileBtn").html('<a class="attachment-image" target="_blank" href="/content/control/imageView?fileName=' + fileId + '&externalLoginKey=${externalLoginKey}">' + file.name + '</a>');
                fileItem.find(".attachment-image").fancybox({
                    'overlayShow':false,
                    'transitionIn':'elastic',
                    'transitionOut':'elastic'
                });
            }else if("pdf,doc,docx,ppt,pptx,xls,xlsx".indexOf(fileType) > -1){//pdf及office文件
                fileItem.find(".fileBtn").on("click", function () {
                    displayInLayer("文件预览", {
                        requestUrl: "/content/control/FileHandler?dataResourceId=" + fileId + "&externalLoginKey=${externalLoginKey?default('')}",
                        height: '600px'
                    })
                })
            }
        },
        <#noparse>
            'itemTemplate': '<div id="${fileID}" instanceId="${instanceID}" class="file-item"><span class="fileName"><span class="fileBtn">${fileName}</span> (${fileSize})</span><span class="data"></span></div>'
        </#noparse>
    });
</script>
</#macro>