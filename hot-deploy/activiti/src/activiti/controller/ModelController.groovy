import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.activiti.bpmn.converter.BpmnXMLConverter
import org.activiti.bpmn.model.BpmnModel
import org.activiti.editor.constants.ModelDataJsonConstants
import org.activiti.editor.language.json.converter.BpmnJsonConverter
import org.activiti.engine.ManagementService
import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngines
import org.activiti.engine.RepositoryService
import org.activiti.engine.repository.Deployment
import org.activiti.engine.repository.DeploymentBuilder
import org.activiti.engine.repository.Model
import org.activiti.engine.repository.NativeModelQuery
import org.activiti.engine.repository.ProcessDefinition
import org.apache.commons.collections.BeanMap
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilHttp
import org.ofbiz.webapp.event.EventUtil

public Object modelList() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    ManagementService managementService = processEngine.getManagementService();
    NativeModelQuery nativeModelQuery = repositoryService.createNativeModelQuery();
    /*nativeModelQuery.sql("select count(*) from " + managementService.getTableName(org.activiti.engine.repository.Model.class) + " where tenant_id_ is null or tenant_id_ = '' or tenant_id_ = #{tenant}");
    def tenantId = httpRequest.getAttribute("userTenantId");
    nativeModelQuery.parameter("tenant", tenantId);
    criteria.setFullListSize((int) nativeModelQuery.count());*/

    String tenantId = request.getAttribute("userTenantId");
    nativeModelQuery.sql("select * from " + managementService.getTableName(org.activiti.engine.repository.Model.class) + " where tenant_id_ is null or tenant_id_ = '' or tenant_id_ = #{tenant} order by last_update_time_ desc");
    nativeModelQuery.parameter("tenant", tenantId);
    List<Model> models = nativeModelQuery.list();
    List<Map<String, Object>> modelList = new ArrayList<>();
    for (Model model: models) {
        Map map = new BeanMap(model)
        modelList.add(map);
    }
    request.setAttribute("modelList", modelList);
//    context.modelList = modelList;
    return "success";
}

public Object saveModel(){
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode properties = objectMapper.createObjectNode();

        String name = request.getParameter("name");
        String key = request.getParameter("key");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String tenantId = request.getAttribute("userTenantId");

        properties.put("name", StringUtils.stripToEmpty(name));
        properties.put("process_id", StringUtils.stripToEmpty(key));
        properties.put("category", StringUtils.stripToEmpty(category));
        properties.put("documentation", StringUtils.stripToEmpty(description));
        editorNode.put("properties", properties);
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        org.activiti.engine.repository.Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        description = StringUtils.defaultString(description);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(StringUtils.defaultString(key));
        modelData.setCategory(category);
        modelData.setTenantId(tenantId);

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
//      response.sendRedirect(request.getContextPath() + "/workflow/service/editor?id=" + modelData.getId());
    } catch (Exception e) {
        Debug.logError(e, this.getClass().getName());
        throw new RuntimeException("创建模型失败");
    }
    return EventUtil.returnSuccess(request, "模型创建成功");
//    request.setAttribute("data", "模型创建成功");
//    request.setAttribute("status", true);
//    return "success";
}

public Object updateCategory(){
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        String category = request.getParameter("category");
        Model model = repositoryService.getModel(modelId);
        model.setCategory(category);
        repositoryService.saveModel(model);

    } catch (Exception e) {
        Debug.logError(e, this.getClass().getName());
        throw new RuntimeException("更新模型分类失败");
    }
    return EventUtil.returnSuccess(request, "更新模型分类成功");
//    request.setAttribute("data", "更新模型分类成功");
//    request.setAttribute("status", true);
//    return "success";
}

public Object deployModel(){
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String tenantId = request.getAttribute("userTenantId");

        String modelId = request.getParameter("modelId");
        org.activiti.engine.repository.Model modelData = repositoryService.getModel(modelId);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        byte[] bpmnBytes = null;

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        String processName = modelData.getName() + ".bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes));
        deploymentBuilder.tenantId(tenantId);
        deploymentBuilder.category(modelData.category);
        Deployment deployment = deploymentBuilder.deploy();

        //设置流程定义的类型，默认值为模型的命名空间字段值
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.id).list().get(0);
        repositoryService.setProcessDefinitionCategory(processDefinition.id, modelData.category);

        modelData.setDeploymentId(deployment.id);
        repositoryService.saveModel(modelData);
    } catch (Exception e) {
        Debug.logError(e, "根据模型部署流程失败", this.getClass().getName());
        throw new RuntimeException("根据模型部署流程失败");
    }
    return EventUtil.returnSuccess(request, "模型部署成功");
//    request.setAttribute("data", "模型部署成功");
//    request.setAttribute("status", true);
//    return "success";
}

public Object exportModel(){
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        String modelId = request.getParameter("modelId");
        org.activiti.engine.repository.Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

        String filename = bpmnModel.getMainProcess().getName() + ".bpmn20.xml";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bpmnBytes);
        UtilHttp.streamContentToBrowser(response, inputStream, bpmnBytes.length, "text/xml", filename);
    } catch (Exception e) {
        Debug.logError(e, "导出model的xml文件失败", this.getClass().getName());
        throw new RuntimeException("导出model的xml文件失败");
    }
    return null;
}

public Object deleteModel(){
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        String modelId = request.getParameter("modelId");
        repositoryService.deleteModel(modelId);
    } catch (Exception e) {
        Debug.logError(e, "删除模型失败", this.getClass().getName());
        throw new RuntimeException("删除模型失败");
    }
    return EventUtil.returnSuccess(request, "模型删除成功");
//    request.setAttribute("data", "模型删除成功");
//    request.setAttribute("status", true);
//    return "success";
}