<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/three-level-picker/css/three-level-picker.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript" src="/images/lib/three-level-picker/js/three-level-picker.js"></script>
<script type="text/javascript" src="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="/zxdoc/static/docTypes.js"></script>
<script type="text/javascript" src="/images/jquery/ajaxfileupload.js"></script>
<script type="text/javascript" src="/images/jquery/ui/js/jquery-ui-1.10.3.js"></script>
<link href="/images/jquery/ui/js/jquery-ui.css" rel="stylesheet" type="text/css" />

<script type="application/javascript">
    var otherModules = unescapeHtmlText("${parameters.otherModules?default('')}");
    var fileSharePartyId = unescapeHtmlText("${parameters.fileSharePartyId?default('')}");
    var folderStructure = unescapeHtmlText("${parameters.folderStructure?default('')}");
    var name = "";

    /**
     * 上传文件
     */
    function uploadFile(fileStatus,fileId){
        var folderId = unescapeHtmlText("${parameters.folderId?default('')}");
        var rootId = unescapeHtmlText("${parameters.rootId?default('')}");
        //设置加载图标的显示
        //加载层
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajaxFileUpload({
            url:'/ckfinder/control/action?command=fileUpload&rootFolderId=' + rootId + '&currentFolder=' + folderId + "&fileStatus=" + fileStatus + "&fileId=" + fileId + "&fileSharePartyId=" + fileSharePartyId + "&otherModules=" + otherModules + "&folderStructure=" + folderStructure,
            secureuri:false,
            fileElementId:'filePath',//file标签的id
            dataType: 'xml',//返回数据的类型
            success: function (data, status) {
                layer.close(index);
                var fileInfo = xmlToJson(data);
                if(fileInfo.Connector.fileError != null){
                    var code = fileInfo.Connector.fileError.error.attributes.errorCode;
                    if(code == "602"){
                        var size = fileInfo.Connector.fileError.error.attributes.fileSize;
                        showError("上传失败，文件请控制大小在" + size + "M之内！");
                        closeCurrentTab(name); //再执行关闭
                    }else if(code == "0"){
                        showInfo("上传成功");
                        closeCurrentTab(name); //再执行关闭
                    }else{
                        showError("上传发生异常，请刷新后重试");
                        closeCurrentTab(name); //再执行关闭
                    }
                }else{
                    showInfo("上传成功");
                    closeCurrentTab(name); //再执行关闭
                }
            },
            error: function(data, status, e){
                layer.close(index);
                //这里处理的是网络异常，返回参数解析异常，DOM操作异常
                showInfo("上传发生异常");
            }
        });
    }

    /**
     * xml 转换为json
     * @param xml
     * @returns {{}}
     */
    function xmlToJson(xml) {
        // Create the return object
        var obj = {};
        if (xml.nodeType == 1) { // element
            // do attributes
            if (xml.attributes.length > 0) {
                obj["attributes"] = {};
                for (var j = 0; j < xml.attributes.length; j++) {
                    var attribute = xml.attributes.item(j);
                    obj["attributes"][attribute.nodeName] = attribute.nodeValue;
                }
            }
        } else if (xml.nodeType == 3) { // text
            obj = xml.nodeValue;
        }
        if (xml.hasChildNodes()) {
            for(var i = 0; i < xml.childNodes.length; i++) {
                var item = xml.childNodes.item(i);
                var nodeName = item.nodeName;
                if (typeof(obj[nodeName]) == "undefined") {
                    obj[nodeName] = xmlToJson(item);
                } else {
                    if (typeof(obj[nodeName].length) == "undefined") {
                        var old = obj[nodeName];
                        obj[nodeName] = [];
                        obj[nodeName].push(old);
                    }
                    obj[nodeName].push(xmlToJson(item));
                }
            }
        }
        return obj;
    };

    //在上传文件之前要先确定文件是否存在重复
    function  checkFileStatus(){
        var currentFolder = unescapeHtmlText("${parameters.folderId?default('')}");
        var rootId = unescapeHtmlText("${parameters.rootId?default('')}");
        var fileName = $("#filePath").val();
        var filesub = "";
        if(fileName != null && fileName != ''){
            filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
            name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length);
            $.ajax({
                type: 'post',
                url: "checkFileStatus",
                async: false,
                dataType: 'json',
                data:{rootFolderId:rootId,currentFolder:currentFolder, fileName:filesub, fileSharePartyId: fileSharePartyId, otherModules:otherModules},
                success: function (content) {
                    if(content.fileStatus == "0"){
                        uploadFile('','');
                    }else{
                        var fileId = content.fileId;
                        selectFileType(fileId);
                    }
                }
            });
        }else{
            showError("请选择上传文件！")
        }
    }

    function selectFileType(fileId){
        layer.confirm($("#dialog-confirm").html(), {
            shade: 0.2,
            btn: ['覆盖','新版本'] //按钮
        }, function(){
            uploadFile("1",fileId);
        }, function(){
            uploadFile("2",fileId);
        });
    }
</script>
<div class="portlet-body form" style="height: 320px">
    <div style="text-align: center;padding: 10px">
        <div class="fileinput fileinput-new" data-provides="fileinput">
            <div class="fileinput-preview thumbnail" data-trigger="fileinput"
                 style="width: 200px; height: 150px;"></div>
            <div>
                            <span class="btn red btn-outline btn-file">
                                <span class="fileinput-new"> 选择文件 </span>
                                <span class="fileinput-exists"> 重新选择 </span>
                                <input type="file" name="filePath" id="filePath">
                            </span>
                <a href="javascript:;" class="btn red fileinput-exists" data-dismiss="fileinput"> 删除 </a>
            </div>
        </div>
    </div>
    <div class="form-actions" style="padding: 20px;text-align: center">
        </br>
        <button type="button" class="btn blue" onclick="window.checkFileStatus();">
            <i class="fa fa-check"></i> 确认
        </button>
    </div>
</div>
<div id="dialog-confirm" style="display: none" title="上传文件">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>文件出现重复，请选择！</p>
</div>
<div class="note note-info">
    <pre>
    温馨提示
    <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
    1.<span align="center" style="color: red">上传文件大小请不要超过${fileSize?default("50")}兆</span>。
    2.<span style="color: red">文件名称不要超过50个字符。</span>
    </pre>
</div>
