<link href="/images/lib/validationEngine/validationEngine.jquery.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/validationEngine/jquery.validationEngine.js?t=20171010"></script>
<div class="input-group" style="width: 200px;float: right">
    <#--input-circle-left-->
    <span class="input-group-addon ">
        <a onclick="searchFileListByName();" class="icon-magnifier"></a>
    </span>
    <input type="text" class="form-control validate[custom[noSpecial]]" maxlength="20" id="fileName" placeholder="文件名称">
</div>