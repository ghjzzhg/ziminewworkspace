package org.ofbiz.oa

import javolution.util.FastMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.Delegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

import javax.swing.text.html.parser.Entity

/**
 * Created by galaxypan on 2015/5/21.
 */
productTypeList = EntityQuery.use(delegator).select().from("TblProductType").where().queryList();
context.productTypeList = productTypeList;





