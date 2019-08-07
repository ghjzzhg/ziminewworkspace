var savedPictureContent = '';
var extendName = '';
var captureObj = null;

/*
用于初始化牛牛截图控件，此函数需要在页面加载完成后立即调用 
在此函数中，您可以设置相关的截图的UI控制，如，画笔大小、边框颜色等等 【这部分信息在niuniucapture.js中也有默认值，直接修改默认值也可 】
*/
function InitCapture(authKey)
{
    captureObj = new NiuniuCaptureObject();
    captureObj.NiuniuAuthKey = authKey;
    //此处可以设置相关参数 
    captureObj.TrackColor = rgb2value(255, 0, 0);
    captureObj.EditBorderColor = rgb2value(0, 0, 255);
    
    //设置工具栏的TOOLTIP  
    //captureObj.ToolTipText = "tipRectangle|tipCircle|tipArrow|tipBrush|tipGlitter|tipMosaic|tipText|tipUndo|tipSave|tipCancel|tipFinish|Finish";
    
    //设置控件加载完成以及截图完成的回调函数
    captureObj.FinishedCallback = OnCaptureFinishedCallback;
    captureObj.PluginLoadedCallback = PluginLoadedCallback;
    
    //初始化控件 
    captureObj.InitNiuniuCapture();
}

/*
当控件成功加载后回调的的函数，您可以在此控制相应的UI显示  
*/
function PluginLoadedCallback(success)
{
    if (success) {
	    $('#btnReload').hide();
        $('#btnCapture').show();
    }
}

//根据是否是Chrome新版本来控制下载不同的控件安装包
function ShowDownLoad()
{
    if(captureObj.IsNeedCrx())
    {
        ShowChromeInstallDownload(); 
    }
    else
    {
        ShowIntallDownload();
    }
}

/*
需要下载控件安装包的提示信息，您可以根据需要进行调整 
*/
function ShowIntallDownload()
{
    showError("请先安装截图控件，完成安装后请刷新页面或关闭浏览器后重试！");
    // $('#btnReload').show();
    // $('#btnCapture').hide();
	// var date = new Date();
    $('#downCapture').attr('src', "/zxdoc/static/capture/CaptureInstall.exe");
    $('#downloadNotice').show();
}

/*
需要下载Chrome扩展安装包的下载提示信息
*/
function ShowChromeInstallDownload()
{
    // showError("请使用IE浏览器，暂不支持谷歌浏览器插件");
    // return;
    // var date = new Date();
    //
    // $('#downaddr').attr('href', "/zxdoc/static/capture/CaptureInstallChrome.exe");
    // $('#downCapture').attr('src', "/zxdoc/static/capture/CaptureInstallChrome.exe?" + date.getMinutes() + date.getSeconds());
    // $('#downloadNotice').show();
    // $('#btnReload').show();
    // $('#btnCapture').hide();
}

/*
当提示安装控件后，需要重新加载控件来使用截图；
也有部分是需要刷新浏览器的
*/
function ReloadPlugin() 
{
    //如果是Chrome42以上版本，此处需要刷新页面才能让扩展在此页面上生效 
    if(captureObj.IsNeedCrx())
	{
		 location.reload();
		 return;
	}
    captureObj.LoadPlugin();
    $('#btnReload').hide();
     $('#btnCapture').show();
     if(captureObj.pluginValid())
     {
        $('#downloadNotice').hide();
        showInfo("截图控件已经安装完毕，您可以进行截图了。");
     }
     else
     {
        var browserInfo = "查看控件是否被浏览器阻止";
        var brow=$.browser;
        var bInfo="";
        if(brow.msie) 
        {
            browserInfo = "通过浏览器设置中的加载项查看[截图控件]是否加载并正常运行";
        }
        /*else if(brow.mozilla)
        {
            //about:addons
            browserInfo = "请检查浏览器地址拦下是否有询问是否启用控件的提示，如：<img src=\"./image/ffnotice.png\" border=0 />，如果有，则允许控件运行；";
            browserInfo += "或者在地址拦中键入<strong>about:addons</strong>来启用控件";
        }
        else if(brow.safari) 
        {
            //chrome://plugins
            browserInfo = "请检查浏览器地址拦下是否有询问是否启用控件的提示，如：<img src=\"./image/ffnotice.png\" border=0 />，如果有，则允许控件运行；";
            browserInfo += "或者在地址拦中键入<strong>chrome://plugins</strong>来启用控件";
        }*/
        //else if(brow.opera) {bInfo="Opera "+brow.version;}
        
        showInfo('截图控件未能识别到，请按如下步骤检查:<br/>1. 确定您已经下载控件安装包并正常安装 <br/>2. ' + browserInfo
            + '<br/>3. 刷新页面或重新启动浏览器试下');
     }
}

/*
截图入口函数，用于控制UI标签的显示 
*/
function StartCapture() {
    showInfo('正在截图中...');
    var captureRet = Capture();
    //从返回值来解析显示  
    if(!captureRet)
    {
        ShowDownLoad();
    }
}

/*
此函数是根据在测试页面上的不同选项来进行截图，在实际应用中，您只需要根据您实际需要的截图模式，传入相应的参数即可 
*/
function Capture() 
{
	var defaultName = "1.jpg"; //此处为了防止上传的数据过大，建议使用JPG格式 
    var hideFlag = 0;
    var autoFlag = 0;
    var captureRet = true;
        return captureObj.DoCapture("1.jpg", hideFlag, 0, 0, 0, 0, 0);
    }

/*
此处是截图后的回调函数，用于将截图的详细信息反馈回来，你需要调整此函数，完成图像数据的传输与显示  
*/
function OnCaptureFinishedCallback(type, x, y, width, height, info, content,localpath) 
{
    if(type < 0)
    {
        //需要重新安装控件
        ShowDownLoad();
        return;
    }
    switch(type)
    {
        case 1:
        {
            // showInfo('截图完成： x:' + x + ',y:' + y + ',widht:' + width + ',height:' + height);
            //UploadCaptureData(content, localpath);
            sendChatMessage("P*I*C*T*U*R*E:" + content, $("#chatWithJID").val());
	          break;
        }
        case 2:
        {
            showInfo('您取消了截图');
             break;        
        }
        case 3:
        {
            // showInfo('您保存了截图到本地： x:' + x + ',y:' + y + ',widht:' + width + ',height:' + height);
            //UploadCaptureData(content, localpath);
	         break;
        }
        case 4:
        {
            if (info == '0') {
                showInfo('从剪贴板获取到了截图： ' + localpath);
	            UploadCaptureData(content, localpath);
            }
            else {
                showInfo('从剪贴板获取图片失败。');
            }            
	        break;
        }
    }                 
}

//控制上传
function UploadCaptureData(content, localpath)
{
    savedPictureContent = content;
    
    //获取图片的扩展名 
    var pos = localpath.lastIndexOf('.');
    extendName = localpath.substr(pos + 1);
    showInfo('截图完成');
		UploadData();
	}


/*
实际上传图像数据的函数，此处主要是将BASE64的图像数据，通过AJAX的方式POST到服务器保存成文件，并且显示在页面上
*/
function UploadData() {
    showInfo("开始上传数据" + savedPictureContent);
    //TODO:上传的数据除了图片外，还可以包含自己需要传递的参数
    var data = "userid=test111&extendname=" + extendName + "&picdata=" + encodeURIComponent(savedPictureContent);	
             
    $.ajax({
            type: "POST",
        url: "./upload.ashx",//TODO:通过openfire发送图片
            dataType: "json",
            data: data, 
            success: function (obj) {
            if (obj.code == 0) {
                showInfo('上传成功，图片地址：' + obj.info);
		        }
            else {
                showError('上传失败 :' + obj.info);
		        }					
            },
        error: function () {
            showError('由于网络原因，上传失败。');
        }
        });  
}  

function TestSetWatermarkPicture()
{
    captureObj.SetWatermarkPicture("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");
}
