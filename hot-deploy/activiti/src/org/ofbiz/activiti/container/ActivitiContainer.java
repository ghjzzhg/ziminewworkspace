/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.activiti.container;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.parse.BpmnParseHandler;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.config.model.EntityConfig;
import org.ofbiz.entity.config.model.InlineJdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ActivitiContainer implements Container {

    public static final String module = ActivitiContainer.class.getName();

    String sql;
    protected String configFile;

    private String name;

    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name = name;
        this.configFile = configFile;
    }

    /**
     * start container
     */
    @Override
    public boolean start() throws ContainerException {
        Debug.logInfo("Start Activiti container", module);
        try {
            ActivitiSyncUtil.synToActiviti();
        } catch (Exception e) {
            Debug.logError(e, "Activiti初始化错误", module);
        }

        return true;
    }

    @Override
    public void stop() throws ContainerException {
    }

    @Override
    public String getName() {
        return name;
    }
}
