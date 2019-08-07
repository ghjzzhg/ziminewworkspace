<%@ include file="/static/common/taglibs.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  
  <link rel="stylesheet" href="${staticContextPath}/js/module/workflow/viewer/css/style.css" type="text/css" media="screen">
  <script src="${staticContextPath}/js/module/workflow/viewer/jstools.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/raphael.js" type="text/javascript" charset="utf-8"></script>
  
  <script src="${staticContextPath}/js/module/workflow/viewer/jquery/jquery.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/jquery/jquery.progressbar.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/jquery/jquery.asyncqueue.js" type="text/javascript" charset="utf-8"></script>
  
  <script src="${staticContextPath}/js/module/workflow/viewer/Color.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/Polyline.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/ActivityImpl.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/ActivitiRest.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/LineBreakMeasurer.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/ProcessDiagramGenerator.js" type="text/javascript" charset="utf-8"></script>
  <script src="${staticContextPath}/js/module/workflow/viewer/ProcessDiagramCanvas.js" type="text/javascript" charset="utf-8"></script>
  
  <style type="text/css" media="screen">
    
  </style>
</head>
<body>
<div class="wrapper">
  <div id="pb1"></div>
  <div id="overlayBox" >
    <div id="diagramBreadCrumbs" class="diagramBreadCrumbs" onmousedown="return false" onselectstart="return false"></div>
    <div id="diagramHolder" class="diagramHolder"></div>
    <div class="diagram-info" id="diagramInfo"></div>
  </div>
</div>
<script language='javascript'>
var DiagramGenerator = {};
var pb1;
$(document).ready(function(){
  var processDefinitionId = "${processDefinitionId}";
  var processInstanceId = "${processInstanceId}";
  
  pb1 = new $.ProgressBar({
    boundingBox: '#pb1',
    label: 'Progressbar!',
    on: {
      complete: function() {
        this.set('label', '完成!');
        if (processInstanceId) {
          ProcessDiagramGenerator.drawHighLights(processInstanceId);
        }
      },
      valueChange: function(e) {
        this.set('label', e.newVal + '%');
      }
    },
    value: 0
  });
  
  ProcessDiagramGenerator.options = {
    diagramBreadCrumbsId: "diagramBreadCrumbs",
    diagramHolderId: "diagramHolder",
    diagramInfoId: "diagramInfo",
    on: {
      click: function(canvas, element, contextObject){
        var mouseEvent = this;
        //console.log("[CLICK] mouseEvent: %o, canvas: %o, clicked element: %o, contextObject: %o", mouseEvent, canvas, element, contextObject);

        if (contextObject.getProperty("type") == "callActivity") {
          var processDefinitonKey = contextObject.getProperty("processDefinitonKey");
          var processDefinitons = contextObject.getProperty("processDefinitons");
          var processDefiniton = processDefinitons[0];
          //console.log("Load callActivity '" + processDefiniton.processDefinitionKey + "', contextObject: ", contextObject);

          // Load processDefinition
        ProcessDiagramGenerator.drawDiagram(processDefiniton.processDefinitionId);
        }
      },
      rightClick: function(canvas, element, contextObject){
        var mouseEvent = this;
        //console.log("[RIGHTCLICK] mouseEvent: %o, canvas: %o, clicked element: %o, contextObject: %o", mouseEvent, canvas, element, contextObject);
      },
      over: function(canvas, element, contextObject){
        var mouseEvent = this;
        //console.log("[OVER] mouseEvent: %o, canvas: %o, clicked element: %o, contextObject: %o", mouseEvent, canvas, element, contextObject);

        // TODO: show tooltip-window with contextObject info
        ProcessDiagramGenerator.showActivityInfo(contextObject);
      },
      out: function(canvas, element, contextObject){
        var mouseEvent = this;
        //console.log("[OUT] mouseEvent: %o, canvas: %o, clicked element: %o, contextObject: %o", mouseEvent, canvas, element, contextObject);

        ProcessDiagramGenerator.hideInfo();
      }
    }
  };

  ActivitiRest.options = {
    processInstanceHighLightsUrl: "${ctx}/workflow/service/process-instance/{processInstanceId}/highlights?callback=?",
    processDefinitionUrl: "${ctx}/workflow/service/process-definition/{processDefinitionId}/diagram-layout?callback=?",
    processDefinitionByKeyUrl: "${ctx}/workflow/service/process-definition/{processDefinitionKey}/diagram-layout?callback=?"
  };
  
  if (processDefinitionId) {
    ProcessDiagramGenerator.drawDiagram(processDefinitionId);
    
  } else {
    alert("processDefinitionId parameter is required");
  }
});


</script>
</body>
</html>
