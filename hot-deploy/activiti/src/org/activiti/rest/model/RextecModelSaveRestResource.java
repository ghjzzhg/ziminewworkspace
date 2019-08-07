package org.activiti.rest.model;

import org.activiti.rest.editor.model.ModelSaveRestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

/**
 * Created by galaxypan on 2015/1/6.
 * activiti http method 为put，不知为何RequsetBody始终无法获取数据，故使用POST方法。对应的oryx.debug.js发出的请求方法要改为POST
 */

@RestController
public class RextecModelSaveRestResource{

    @Autowired
    private ModelSaveRestResource modelSaveRestResource;

    @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.POST)
    @ResponseBody
    public String saveModelRextec(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
        modelSaveRestResource.saveModel(modelId, values);
        return "{\"modelId\":\"" + modelId + "\"}";
    }
}
