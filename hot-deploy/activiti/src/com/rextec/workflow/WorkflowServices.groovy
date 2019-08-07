package com.rextec.workflow

import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngines
import org.activiti.engine.RepositoryService
import org.activiti.engine.repository.DeploymentBuilder
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

import java.nio.ByteBuffer
import java.util.zip.ZipInputStream

public Map<String, Object> uploadProcess() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();

    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();

    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String tenantId = context.get("userTenantId");
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    try {
        ByteBuffer fileBytes = (ByteBuffer) context.get("processFile");
        String fileName = (String)context.get("_processFile_fileName");
        InputStream fileInputStream = new ByteArrayInputStream(fileBytes.array());
        String extension = FilenameUtils.getExtension(fileName);
        DeploymentBuilder deployment = repositoryService.createDeployment();
        if(StringUtils.isNotBlank(tenantId)){
            deployment = deployment.tenantId(tenantId);
        }
        if (extension.equals("zip") || extension.equals("bar")) {
            ZipInputStream zip = new ZipInputStream(fileInputStream);
            deployment.tenantId(tenantId).addZipInputStream(zip).deploy();
        } else if (extension.equals("png")) {
            deployment.tenantId(tenantId).addInputStream(fileName, fileInputStream).deploy();
        } else if (fileName.endsWith("bpmn20.xml")) {
            deployment.tenantId(tenantId).addInputStream(fileName, fileInputStream).deploy();
        } else if (extension.equals("bpmn")) {                /*
                 * bpmn扩展名特殊处理，转换为bpmn20.xml
				 */
            String baseName = FilenameUtils.getBaseName(fileName);
            deployment.tenantId(tenantId).addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
        } else {
            throw new RuntimeException("仅支持.zip .png .bpmn20.xml .bpmn结尾的文件格式");
        }
    } catch (Exception e) {
        Debug.logError(e, "流程部署错误", this.getClass().getName());
        throw new RuntimeException("流程部署错误");
    }
    successResult.put("data", UtilMisc.toMap("msg", "部署成功"));
    return successResult;
}