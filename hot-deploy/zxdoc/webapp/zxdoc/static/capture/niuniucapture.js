/*******************************************************************************
牛牛截图的JS部分的核心流程封装在此文件中，绝大部分情况下，您不需要修改此文件中的JS内容，它已经包含了在所有浏览器上应用
牛牛截图所需要的所有代码，您只需要去修改capturewrapper.js，将相应的函数修改成与您的UI匹配的即可 
*******************************************************************************/

/*******************************************************************************/
//设置截图的参数  
var emPensize = 1;		//设置画笔大小
var emDrawType = 2;		//设置是腾讯风格还是360风格 0： 腾讯风格   1： 360风格
var	emTrackColor = 3;		//自动识别的边框的颜色
var	emEditBorderColor = 4;	//文本输入的边框颜色
var	emTransparent = 5;		//工具栏的透明度
var	emWindowAware = 6;		//设置是否禁用随着DPI放大
var	emSetSaveName = 8;		//设置保存时的开始文字     免费版本无效
var	emSetMagnifierBkColor = 9; //设置放大镜的背景色，不设置则透明
var	emSetMagnifierLogoText = 10; //设置放大镜上的LOGO字符，可提示快捷键，如： 牛牛截图(CTRL + SHIFT + A)     免费版本无效
var emSetWatermarkPictureType = 20;						//设置水印的类型 
var	emSetWatermarkPicturePath = 21;						//设置水印的路径 
var	emSetWatermarkTextType = 22;						//设置水印文字的类型 
var	emSetWatermarkTextValue = 23;                       //设置水印文字的字符串
var emSetMosaicType = 24;               //指定马赛克的类型，1为矩形，2为画线 
var emSetTooltipText = 25;               //用于设置工具栏图标的TOOLTIP文字 
/*******************************************************************************/

function rgb2value(r, g, b)
{
    return r | g << 8 | b << 16;
}

var captureObjSelf = null;
function onpluginLoaded()
{
    captureObjSelf.pluginLoaded();
}

function NiuniuCaptureObject() 
{
    var self = this;
    captureObjSelf = this;
    this.PenSize = 2;
    this.DrawType = 0;
    this.TrackColor = rgb2value(255, 0, 0);
    this.EditBorderColor = rgb2value(255, 0, 0);
    this.Transparent = 240;
    this.WindowAware = 1;
    this.MosaicType = 1;
    this.SaveName = "保存";
    this.MagnifierLogoText = "截图";
    this.WatermarkPictureType = "2|100|100|400|400|20";
    this.WatermarkPicturePath = "";
    this.WatermarkTextType = "2|100|100|100|40|20|0|150|30|80,55,55,55";
    this.WatermartTextValue = "";
    this.NiuniuAuthKey = "";
    this.ToolTipText = "";  //tipRectangle|tipCircle|tipArrow|tipBrush|tipGlitter|tipMosaic|tipText|tipUndo|tipSave|tipCancel|tipFinish|txtFinish
    
    this.FinishedCallback = null;
    this.PluginLoadedCallback = null;
    
    this.IsNeedCrx = function()
    {
        return false;
        var isChrome = self.IsRealChrome();
        var chromeMainVersion = self.GetChromeMainVersion();
        if(isChrome && chromeMainVersion > 41)
        {
	        return true;
        }
        return false;
    }
  
    this.LoadPlugin = function()
    {
        var obj = $('#capturecontainer');
        if(obj.length < 1)
        {
            $("body").append('<div id="capturecontainer" style="height:0px;width:0px;"></div>');
            obj = $('#capturecontainer');
        }        
        obj.html('');
        obj.html('<object id="niuniuCapture" type="application/x-niuniucaptureRexTec" width="0" height="0"><param name="onload" value="onpluginLoaded" /></object>');
    }
    
    this.niuniuCapture = function ()
    {
        return document.getElementById('niuniuCapture');
    }
        
    this.addEvent = function(obj, name, func)
    {
        if (obj.attachEvent) {
            obj.attachEvent("on"+name, func);
        } else {
            obj.addEventListener(name, func, false); 
        }
    }
    
    this.pluginValid = function()
    {
        try
        {
            if(self.niuniuCapture().valid)
            {
                return true;
            }
        }
        catch(e)
        {
        }
        return false;        
    }

    this.OnCaptureFinished = function(x, y, width, height, content,localpath) 
    {
	    self.OnCaptureFinishedEx(1, x, y, width, height, "", content,localpath);      
    }
    
    this.OnCaptureFinishedEx = function(type, x, y, width, height, info, content,localpath) 
    {
        //交给上层去处理截图完成后的事项 
        if(self.FinishedCallback != null)
        {
            self.FinishedCallback(type, x, y, width, height, info, content,localpath);
        }
        else
        {
            alert("截图完成的事件未绑定，将不能对图片进行处理，请指定FinishedCallback回调函数");
        }
    }
    
    this.pluginLoaded = function()
    {
        if(!self.pluginValid())
        {
            if(self.PluginLoadedCallback != null)
            {
                self.PluginLoadedCallback(false);
            }
            return false;
        }   
        
        //此函数必需调用，传递正确的参数，且必需先于其他函数调用  
        self.niuniuCapture().InitCapture(self.NiuniuAuthKey); 
        
        self.niuniuCapture().InitParam(emPensize, self.PenSize);
        self.niuniuCapture().InitParam(emDrawType, self.DrawType);
        self.niuniuCapture().InitParam(emTrackColor, self.TrackColor);
        self.niuniuCapture().InitParam(emEditBorderColor, self.EditBorderColor);
        self.niuniuCapture().InitParam(emTransparent, self.Transparent);
         
        self.niuniuCapture().InitParam(emSetSaveName, self.SaveName);            
        self.niuniuCapture().InitParam(emSetMagnifierLogoText, self.MagnifierLogoText);
        self.niuniuCapture().InitParam(emSetMosaicType, self.MosaicType);
        
        //设置工具栏上的按钮TOOLTIP  
        self.niuniuCapture().InitParam(emSetTooltipText, self.ToolTipText);
        //niuniuCapture().InitParam(23, "测试文字.");
        //此BASE64字符串表示牛牛默认的水印图片，可以替换
       // niuniuCapture().InitParam(21, "iVBORw0KGgoAAAANSUhEUgAAAF0AAABQCAYAAAB773kdAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNXG14zYAAAAWdEVYdENyZWF0aW9uIFRpbWUAMDQvMDkvMTX+60k3AAAFXUlEQVR4nO2c3XWjSBCFr/fMMxoisDeC9UZgO4LRRLD4kIAmgsERrBwA5+AIRorAOAKPIlgUASsS0D5QaFiJn+6uamhkfW+W6KK4LhXd1QVX+/0eXPLQiwCs/Lj4yTbmMHno3QKY+3ERcexccUUnR979uLhiGZoIeejtAfzJCbDfBPxYCtiYGqxrZomeh94CwB2ADcfOxNgAuKNrN8JY9Dz0bgBE9Oe/pnYmSHWtEWmgDSfSEwCzI0c+AtW1zlBqoI2R6LW0UnHWs5Yj6tdqlGa0RT9KKxcM0oxJpCf4lVYqMgM7UyU7+ls7zWiJ3pBW2hw5Z7KGz7TSjLLol7TSi3Ka0Yn0JU7TCgDAj4tUw86k6bjWGRQXTUqi56E3B/BFza0PzRfSqpNe0fPQ+4zuG8VWw6lzoeuaE9KsFZVIT9CSVohMwca5kXV81zub6RT9klaM6UwzraIrpJWKVN+nyZMqHNOaZj51DUJ3Wqm4yUPvXuG4c+JG4ZgqzZxEfOMmBv00fjAdu1Dy1Y+LVf2DE9HpJ/ETwPWAjp0zWwC3flwcKrFNOT3CRXBJrnG0kv9fpFNufh3UpY/DQ7WaPYh+SSvWOaSZenqJcBHcJoc0c7Xf7y9pZVgeqkhPxvTig5Gwm436oHtFBrWFVhsvflwEiudLAPzFONcOwE19iieNRLNRHwF4gm8B6Gz+LsCrfM5Q+myNIUQ3bsohAp2oo2MD5jm5PndiVfQ89ALwZkTPJrtSNOaZcd5r8t0KtiM9Yozd+HFhHHE0ltPuFzHGdmJNdIEoDwTc4NiwFu02Iz1gjH2S6HUnG08MEwHXhyasTBmZi603Py7u5bwB8tBL0dyvo8KDdLeDrUiPDMftYCe6ArJtQiTnRol4pDOj/JsfF729I/T0R3XcQiUVUQfW34Z+iUa7jUgPDMe9KQoeAXhHmS7uALzTZ52Q7TdD3wLDcY2IRjq1lf1jMLR36U3RnQD4o+WQDcqFVGvUM0sSv/txkRmMO0E60iPDca2rzjz0Pueht0QZ3W2Cg757z0Nv2bYLz1ytRobjThCLdEaUr/24aOwRoftDAv35/hblPzJtsbuCWT+PSLRLRnpgMGbbNo7y9CvMFljXAF47cn0As6JYYDDmBJFIZ+TKp+MHYRVyty6NuZ7+Id81bYmUfaUiPYDZzel7vf2Mlt0p5AQH2UrrS3o6p67ggFDZVzKnB+joYe9gB+AeZTmVs/mgwgtKH1OY+bnw4yLhOiE9Zaz6H3VvUjvwNjpsn2sNzbp+F7ZqL3OUETX17oItyuhe9R6pgZXaCzl5i/LnPFVeUPapiAoOWIr0OhT1CYZLH1x2KFOJuNgV1kUHDgunFWRnJTbYoHyfS2bzJIOIXiHQHmET5TYPLkN0Axygi3oc8pyKPA4lODCw6ABA81yXhH+UmHvrMLjogFPCDy44MJLowEF4Tm8Kl+cxBAcGvpE2wdw0NkV881uH0SK9xhzmm8Ym7NDwxNuQjC66UO+hDmI1FFNGFx04lA3WA5xqbXOlqYoTohML2E0zO1juxlXFGdFp6W3zxZpL28t7VZwRnVjCTrTv4NCbUp0SnW5wNsRZjn3zrOOU6IQV0S3YNMY50SkiJWcya5eiHHBQdELyjabOvR3VOdFpc1tyxTjve6fW0DgnOsq5tHTfixPz84rRC151hB70bcL6A7k6uBbpc9jZwJ5h5CJXHddEv52obS0uoo+Aa6JnE7WthWuiJxO1rYVTotOTEzY2rB9devO1U6IDVjoFRtnx78I50QHRToHRdvy7cFJ0InLEhjjOii5QbXSuuljhrOgEp0LoXHWxwnXR05HGWuU/oYwAt7g/Ov4AAAAASUVORK5CYII=");
         //注：以上设置LOGO及保存名的接口，免费版本无效
        
        //添加控件的事件监听 
	    self.addEvent(self.niuniuCapture(), 'CaptureFinishedEx', self.OnCaptureFinishedEx);
	    //以下这个事件主要是用于兼容旧的浏览器控件的事件通知
	    self.addEvent(self.niuniuCapture(), 'CaptureFinished', self.OnCaptureFinished);
    	
    	
    	if(self.PluginLoadedCallback != null)
        {
            self.PluginLoadedCallback(true);
        }
    }
    
    this.SetWatermarkPicture = function(watermarPicData)
    {
          self.WatermarkPicturePath = watermarPicData;
          //设置测试的水印图片的Base64字符串，此操作应该是在页面加载中处理比较合适 
          if(!self.pluginValid())
            return;
          self.niuniuCapture().InitParam(emSetWatermarkPicturePath, self.WatermarkPicturePath);        
          self.niuniuCapture().InitParam(emSetWatermarkPictureType, self.WatermarkPictureType);
    }

    this.SetWatermarkText = function(watermarkText)
    {
        self.WatermarkTextValue = watermarkText;
        //设置测试的水印文字，此操作应该是在页面加载中处理比较合适 
        if(!self.pluginValid())
            return;
		//nShowType|nMinWidth|nMinHeight|nVerticalInterval|nOffset|nFontSize|nIsBold|nTextWidth|nTextHeight|colorText
		 self.niuniuCapture().InitParam(emSetWatermarkTextValue, self.WatermarkTextValue);
         self.niuniuCapture().InitParam(emSetWatermarkTextType, self.WatermarkTextType);
    }
    
    
    this.SavePicture = function(savename)
    {
         if(self.pluginValid())
         {
            self.niuniuCapture().SavePicture(savename);
         }
    }
    
    this.GetCursorPosition = function()
    {
        if(self.pluginValid())
        {
            var val = self.niuniuCapture().GetCursorPosition();
            return val;
        }
        return "";
    }
    
    
    
    
    this.NewCaptureParamObject = function(defaultpath, hideCurrWindow, autoCaptureFlag, x, y, width, height)
    {  
        var obj = new Object(); 
        obj.IsGBK = 0;				//是否是GBK编码，这样会涉及到编码转换  
        obj.AuthKey = self.NiuniuAuthKey;  //						
	    obj.Pensize = self.PenSize;		//设置画笔大小
	    obj.DrawType = self.DrawType;			//设置是腾讯风格还是360风格
	    obj.TrackColor= self.TrackColor;		//自动识别的边框的颜色
	    obj.EditBorderColor= self.EditBorderColor;	//文本输入的边框颜色
	    obj.Transparent = self.Transparent;		//工具栏的透明度
	    obj.SetSaveName = self.SaveName;									//设置保存时的开始文字
	    obj.SetMagnifierLogoText = self.MagnifierLogoText;						//设置放大镜上的LOGO字符   
	    obj.SetWatermarkPictureTypeEx = self.WatermarkPictureType;						//设置水印的类型 
	    obj.SetWatermarkPicturePath = self.WatermarkPicturePath;						//设置水印的路径 
	    obj.SetWatermarkTextTypeEx=self.WatermarkTextType;							//设置水印文字的类型 
	    obj.SetWatermarkTextValue= self.WatermarkTextValue;						//设置水印文字
        obj.MosaicType = self.MosaicType;          //设置马赛克的类型 
        obj.SetToolbarText = self.ToolTipText;
        
	    //以下是截图时传递的参数 
	    obj.DefaultPath = defaultpath;
	    obj.HideCurrentWindow = hideCurrWindow;
	    obj.AutoCaptureFlag = autoCaptureFlag;
	    obj.x = x;
	    obj.y = y;
	    obj.Width = width;
	    obj.Height = height;
        return obj; 
    } 

    //此函数用于绑定在Chrome42以上的版本时，扩展在截图完成后进行事件通知的处理 
    this.BindChromeCallback = function()
    {
	    document.addEventListener('NiuniuCaptureEventCallBack', function(evt) { 
	        var _aoResult = evt.detail;	
	        if(_aoResult.Result == -2)
	        {
		        self.OnCaptureFinishedEx(-1, 0, 0, 0, 0, "", "", "");   //通知重新下载控件  
	        }
	        if(_aoResult.Result != -1)
	        {
		        self.OnCaptureFinishedEx(_aoResult.Type, _aoResult.x, _aoResult.y, _aoResult.Width, _aoResult.Height, _aoResult.Info, _aoResult.Content, _aoResult.LocalPath);
	        }
	        else
	        {
		        alert("出错："  + _aoResult.Info);
	        }
	    });
    }

   


    this.IsRealChrome = function()
    {
        try
        {
            var agent = window.navigator.userAgent.toLowerCase();            
            var isQQBrowser = agent.indexOf("qqbrowser") != -1;
            if(isQQBrowser)
            {
                return false;
            }
			var isUBrowser = agent.indexOf("ubrowser") != -1;
			if(isUBrowser)
            {
                return false;
            }
            var isChrome = agent.indexOf("chrome") != -1;
            if(isChrome)
            {
                if(chrome&&chrome.runtime)
                {
                    return true;
                }
            }
            return false;
        }
        catch(e)
        {
        }
        return false;    
    }

    this.GetChromeMainVersion = function()
    {
        var gsAgent=navigator.userAgent.toLowerCase();
        var gsChromeVer=""+(/chrome\/((\d|\.)+)/i.test(gsAgent)&&RegExp["$1"]);
        
        if(gsChromeVer != "false") 
            return parseInt(gsChromeVer);
        return 0;
    }
    
    
    this.DoCaptureForChrome = function(name, hide, AutoCapture, x, y, width, height)
    {
        var obj = self.NewCaptureParamObject(name, hide, AutoCapture, x, y, width, height);
	    try
	    {    		
	        var json = $.toJSON(obj);
    		
		    var CrxEventFlag = '';
		    var objFlag = $('#' + CrxEventFlag);
		    if(objFlag.length < 1)
		    {
			    return false;
		    }
		    var evt = document.createEvent("CustomEvent");			
	        evt.initCustomEvent(CrxEventFlag, true, false, json); 
	        document.dispatchEvent(evt);		
	        return true;
	    }
	    catch(e)
	    {
    		
	    }  
	    return false;       
    }

    this.DoCapture = function(name, hide, AutoCapture, x, y, width, height)
    {
        if(self.IsNeedCrx())
        {
             return self.DoCaptureForChrome(name, hide, AutoCapture, parseInt(x), parseInt(y), parseInt(width), parseInt(height));
        }   
                 
        if(!self.pluginValid())
        {
            return false;
        }
        self.niuniuCapture().Capture(name, hide, AutoCapture, x, y, width, height);
        return true;            
    }
 
    this.InitNiuniuCapture = function()
    {
        if(!self.IsNeedCrx())
        {
            self.LoadPlugin();   
        }
	    else
	    {
		    self.BindChromeCallback();
	    }
    }

}
