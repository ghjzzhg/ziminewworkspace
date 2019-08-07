<#include "component://content/webapp/content/uploadFileList.ftl"/>
<div id="photographicDiv" style="height: 200px">
    <#--fileUpload中id不可变-->
<@fileUpload id="photographic" name="photographic" showThumbnail=true extension='jpeg,jpg,gif,png' dropZones="photographicDiv" onComplete="fileRetruen" showLink=true readonly=false showTip=false simple="上传" download=true/>
</div>