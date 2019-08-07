/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.oa;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.DatabaseUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.TransactionFactoryLoader;
import org.ofbiz.entity.util.EntityDataLoader;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceDispatcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;


/**
 * 获取易飞erp数据.
 */
public class YiFeiInterfaceContainer implements Container {

    public static final String module = YiFeiInterfaceContainer.class.getName();

    private String name;

    public YiFeiInterfaceContainer() {
        super();
    }

    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name = name;
    }

    /**
     * @see Container#start()
     */
    @Override
    public boolean start() throws ContainerException {
        GenericHelperInfo helperInfo = new GenericHelperInfo("", "localmssql");
        Connection connection = null;
        try {
            connection = TransactionFactoryLoader.getInstance().getConnection(helperInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT PURMA.MA001 FROM Leader..PURMA AS PURMA\n" +
                    "LEFT JOIN Leader..CMSMR AS A ON A.MR001=4 AND A.MR002=PURMA.MA006\n" +
                    "LEFT JOIN Leader..CMSMR AS B ON B.MR001=3 AND B.MR002=PURMA.MA007\n" +
                    "LEFT JOIN Leader..CMSMR AS D ON D.MR001=9 AND D.MR002=PURMA.MA004\n" +
                    "LEFT JOIN Leader..ACTMA AS C ON C.MA001=PURMA.MA041\n" +
                    "LEFT JOIN Leader..ACTMA AS E ON E.MA001=PURMA.MA043\n" +
                    "LEFT JOIN Leader..ACTMA AS G ON G.MA001=PURMA.MA042\n" +
                    "LEFT JOIN Leader..CMSMF AS CMSMF ON CMSMF.MF001=PURMA.MA021\n" +
                    "LEFT JOIN Leader..CMSMV AS F ON F.MV001=PURMA.MA047\n" +
                    "LEFT JOIN DSCSYS..CMSMO AS CMSMO ON MO001=PURMA.MA027\n" +
                    "LEFT JOIN Leader..CMSNA AS CMSNA ON NA001=1 AND NA002=MA055\n" +
                    "LEFT JOIN Leader..ACTMA AS H ON H.MA001=PURMA.MA068\n" +
                    "LEFT JOIN Leader..CMSNJ AS CMSNJ ON CMSNJ.NJ001=PURMA.MA024\n" +
                    "LEFT JOIN Leader..CMSME AS CMSME ON CMSME.ME001=PURMA.MA070 order by PURMA.MA001");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Debug.logInfo("################" + resultSet.getString(1), module);
            }
        } catch (SQLException e) {
            Debug.logError(e, "SQLSERVER数据库错误", module);
        } catch (GenericEntityException e) {
            Debug.logError(e, "SQLSERVER数据库错误", module);
        }finally{
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    /**
     * @see Container#stop()
     */
    @Override
    public void stop() throws ContainerException {
    }

    @Override
    public String getName() {
        return name;
    }
}
