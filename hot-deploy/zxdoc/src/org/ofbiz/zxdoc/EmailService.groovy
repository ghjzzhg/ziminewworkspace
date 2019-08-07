package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

/**
 * Created by Administrator on 2016/12/14.
 */
Map success = ServiceUtil.returnSuccess();
List<GenericValue> emailInfo = EntityQuery.use(delegator).from("EmailTemplateSetting").where(EntityCondition.makeCondition("description",EntityOperator.NOT_EQUAL,null)).queryList();
context.emailInfo = emailInfo;