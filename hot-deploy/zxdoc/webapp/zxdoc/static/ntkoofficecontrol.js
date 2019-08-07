if($("#TANGER_OCX").length == 0){
    var s, classid, Sys = {}, ua = navigator.userAgent.toLowerCase(), ntko = "";
    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] : (s = ua.match(/trident\/([\d.]+)/)) ? Sys.ie9 = s[1] : (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] : (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] : (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] : (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
    classid = window.navigator.platform=="Win32"? "A64E3073-2016-4baf-A89D-FFE1FAA10EC0": "A64E3073-2016-4baf-A89D-FFE1FAA10EE0";
    var cab="/zxdoc/static/OfficeControl" + (window.navigator.platform=="Win32"? ".cab":"x64.cab") + "#version=5,0,4,0";
    if(Sys.ie || Sys.ie9){
        ntko = '<!-- 用来产生编辑状态的ActiveX控件的JS脚本-->   '
            + '<!-- 因为微软的ActiveX新机制，需要一个外部引入的js-->   '
            + '<object id="TANGER_OCX" classid="clsid:'+classid+'"    '
            + 'codebase="' + cab + '" width="100%" height="100%">   '
            + '<param name="IsUseUTF8URL" value="1">   '
            + '<param name="IsUseUTF8Data" value="1">   '
            + '<param name="BorderStyle" value="1">   '
            + '<param name="BorderColor" value="14402205">   '
            + '<param name="TitlebarColor" value="15658734">   '
            + '<param name="TitlebarTextColor" value="0">   '
            + '<param name="MenubarColor" value="14402205">   '
            + '<param name="MenuButtonColor" VALUE="16180947">   '
            + '<param name="MenuBarStyle" value="3">   '
            + '<param name="MenuButtonStyle" value="7">   '
            + '<param name="WebUserName" value="NTKO">   '
            + '<param name="Caption" value="在线文档">   '
            + '<param name="ProductCaption" value="上海迈格投资管理有限公司">'
            + '<param name="ProductKey" value="FBE9DD38F3027B95739B302B59546765928735F3">'
            + '<SPAN STYLE="color:red">不能装载文档控件，请确认使用IE9以上浏览器。</SPAN>   '
            + '</object>'
    }else if(Sys.chrome){
        ntko = '<object id = "TANGER_OCX" clsid = "{' + classid + '}" ' +
            'type = "application/ntko-plug" codebase = "' + cab + '" ' +
            'width = "100%" height = "100%" ' +
            'foronsavetourl = "OnComplete2" ' +
            'foronbeginopenfromurl = "OnComplete" ' +
            'forondocumentopened = "DocumentOpened" ' +
            'forondocumentclosed = "DocumentClosed" ' +
            'foronpublishashtmltourl = "publishashtml" ' +
            'foronpublishaspdftourl = "publishaspdf" ' +
            'foronsaveasotherformattourl = "saveasotherurl" ' +
            'forondowebget = "dowebget" ' +
            'forondowebexecute = "webExecute" ' +
            'forondowebexecute2 = "WebExecute" ' +
            'foronfilecommand = "filecommand" ' +
            'foroncustommenucmd2 = "CustomMenuCmd" ' +
            'foroncustomtoolbarcommand = "CustomToolBarCom" ' +
            'foronsignselect = "signselect" ' +
            '_isuseutf8url = "-1" _isuseutf8data = "-1" ' +
            '_borderstyle = "1" ' +
            '_bordercolor = "14402205" ' +
            '_menubarcolor = "14402205" ' +
            '_menubuttoncolor = "16180947" ' +
            '_menubarstyle = "3" ' +
            '_menubuttonstyle = "7" ' +
            '_webusername = "NTKO" ' +
            '_customtoolbar = "-1" ' +
            '_ProductCaption="上海迈格投资管理有限公司"  ' +
            '_ProductKey="FBE9DD38F3027B95739B302B59546765928735F3" ' +
            '_caption = "文档控件示例演示" >'
            + '<SPAN STYLE="color:red">不能装载文档控件，请使用Setup.exe文件安装。</SPAN>'
            + '</object>'
    }else if(Sys.firefox){
        ntko = '<object id = "TANGER_OCX" clsid = "{A39F1330-3322-4a1d-9BF0-0BA2BB90E970}" ' +
            'type = "application/ntko-plug" codebase = "/zxdoc/static/OfficeControl.cab#version=5,0,3,0" ' +
            'width = "100%" height = "100%" ' +
            'foronsavetourl = "OnComplete2" ' +
            'foronbeginopenfromurl = "OnComplete" ' +
            'forondocumentopened = "DocumentOpened" ' +
            'forondocumentclosed = "DocumentClosed" ' +
            'foronpublishashtmltourl = "publishashtml" ' +
            'foronpublishaspdftourl = "publishaspdf" ' +
            'foronsaveasotherformattourl = "saveasotherurl" ' +
            'forondowebget = "dowebget" ' +
            'forondowebexecute = "webExecute" ' +
            'forondowebexecute2 = "WebExecute" ' +
            'foronfilecommand = "filecommand" ' +
            'foroncustommenucmd2 = "CustomMenuCmd" ' +
            'foroncustomtoolbarcommand = "CustomToolBarCom" ' +
            'foronsignselect = "signselect" ' +
            '_isuseutf8url = "-1" _isuseutf8data = "-1" ' +
            '_borderstyle = "1" ' +
            '_bordercolor = "14402205" ' +
            '_menubarcolor = "14402205" ' +
            '_menubuttoncolor = "16180947" ' +
            '_menubarstyle = "3" ' +
            '_menubuttonstyle = "7" ' +
            '_webusername = "NTKO" ' +
            '_customtoolbar = "-1" ' +
            '_ProductCaption="上海迈格投资管理有限公司"  ' +
            '_ProductKey="FBE9DD38F3027B95739B302B59546765928735F3" ' +
            '_caption = "文档控件示例演示" >'
            + '<SPAN STYLE="color:red">不能装载文档控件，请使用Setup.exe文件安装。</SPAN>'
            + '</object>'
    }else{

    };

    $(".ntko-container").html(ntko);
}
