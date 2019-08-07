function popupView(name, options) {
    var width = 800, height = 600;
    if(options){
        if(options.width){
            width = options.width;
        }
        if(options.height){
            height = options.height;
        }
    }

    var oriName = name;
    var type = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    name = encodeURIComponent(encodeURIComponent(name));
    var content = "";
    if((options && options.download) || "pdf,png,jpg,jpeg,gif,bmp".indexOf(type) == -1){
        downloadServerAttachment(name);
    }else {
        try{
            if(type == "jpg" || type == "png"){
                    content = '<div class="row-fluid"><div class="span12"><div class="widget"><div class="head dark"><div class="icon"><span class="icos-list"></span></div><h2>查看资料</h2> <ul class="buttons"><li><span onclick="closeCurrentTab(this)"><span class="icos-undo" title="关闭"></span></span></li></ul></div>'
                    content += '<img alt="" src=/imageView?fileName=' + name + '>';
                    content += '</div></div></div>'
                    $("#popupContent").html(content);
                    displayInTab3("viewFileTab", "查看资料", $("#popupContent").html(), {width:width, height:height});
            }else if(type == "pdf"){
                var wrapperContent = $("#reportResultWrapper").html();
                wrapperContent = wrapperContent.replace("reportResult", "reportResult4embed");
                displayInTab("printPreview", "打印", wrapperContent, {width:920, height:height});
                embedPDF('/imageView?fileName=' + oriName, "reportResult4embed");
            }
        }catch(e){
            showInfo("您的浏览器不支持在线查看文件，改为下载文件");
            downloadServerAttachment(name);
        }
    }
}

function downloadServerAttachment(name){
    var exportIframe = $("#exportIframe");
    var source = "downloadUploadFile?fileName=" + name;
    if(exportIframe.length > 0){
        exportIframe.attr("src",source);
    }else{
        $('<iframe id="exportIframe" src="' + source + '" style="visibility:hidden; height: 0"></iframe>').appendTo("body");
    }
}

function embedPDF(url, divId) {
    var Sys = checkBrowser();
    if(Sys.safari){
        var objectHtml = '<object data="'+ url + '#pagemode=bookmarks&scrollbars=1&toolbar=0&statusbar=0&messages=1&navpanes=1" type="application/pdf" width="100%" height="100%">' +
            '<param name="pagemode" value="bookmarks">' +
            '<param name="toolbar" value="0">' +
            '<param name="scrollbar" value="1">' +
            '<param name="statusbar" value="0">' +
            '<param name="messages" value="1">' +
            '<param name="navpanes" value="1">' +
          '<a href="'+ url + '">下载</a>' +
        '</object>';
        $("#" + divId).html(objectHtml);
    }else{
        var myPDF = new PDFObject({
            url:url,
            pdfOpenParams:{ pagemode:'bookmarks', scrollbars:'1', toolbar:'0', statusbar:'0', messages:'1', navpanes:'1' }
        }).embed(divId);
    }
}

function createChooser(options){
    var chooserId = options.chooserId;
    $.ajax({
        type:'POST',
        url:options.action,
        data: options.param,
        async:true,
        dataType:'html',
        success:function (content) {
            displayInTab(chooserId, 'chooser', content, options);
        }
    });
}

function eraseChooser(obj){
    $(obj).siblings("input").val("");
}

function showDatePicker(obj){
    $(obj).siblings("input").datepicker("show");
}
