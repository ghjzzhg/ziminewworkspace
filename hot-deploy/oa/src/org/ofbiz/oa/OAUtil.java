package org.ofbiz.oa;

import javolution.util.FastList;
import javolution.util.FastSet;
import org.ofbiz.base.util.Collections3;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import java.util.List;
import java.util.Set;

/**
 * Created by galaxypan on 2015/6/27.
 */
public class OAUtil {

    public static Set<String> getScopedValuesByParty(GenericDelegator delegator, String entityName, String dataAttr, String partyId) throws GenericEntityException {
        Set<String> dataIds = FastSet.newInstance();
        List<GenericValue> values = FastList.newInstance();
        List<EntityExpr> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition("staffPartyId", EntityOperator.EQUALS, partyId));
        conditions.add(EntityCondition.makeCondition("entityName", EntityOperator.EQUALS, entityName));
        if(UtilValidate.isNotEmpty(dataAttr)){
            conditions.add(EntityCondition.makeCondition("dataAttr", EntityOperator.EQUALS, dataAttr));
        }
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopeDeptOnly").where(conditions).distinct().queryList());
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopeDeptLike").where(conditions).distinct().queryList());
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopeDeptLikeSelf").where(conditions).distinct().queryList());
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopePositionOnly").where(conditions).distinct().queryList());
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopePositionLike").where(conditions).distinct().queryList());
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopePositionLikeSelf").where(conditions).distinct().queryList());
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopeLevelOnly").where(conditions).distinct().queryList());

        GenericValue staff = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", partyId), true);
        String position = staff.getString("position");
        if(UtilValidate.isNotEmpty(position)){
            List<EntityExpr> levelLikeConditions = FastList.newInstance();
            levelLikeConditions.add(EntityCondition.makeCondition("entityName", EntityOperator.EQUALS, entityName));
            if(UtilValidate.isNotEmpty(dataAttr)){
                levelLikeConditions.add(EntityCondition.makeCondition("dataAttr", EntityOperator.EQUALS, dataAttr));
            }
            levelLikeConditions.add(EntityCondition.makeCondition("level", EntityOperator.LESS_THAN_EQUAL_TO, position));

            values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopeLevelLike").where(levelLikeConditions).distinct().queryList());
        }
        values.addAll(EntityQuery.use(delegator).select("dataId").from("DataScopeUser").where(conditions).distinct().queryList());

        if(UtilValidate.isNotEmpty(values)){
            for (GenericValue genericValue : values) {
                dataIds.add(genericValue.getString("dataId"));
            }
        }
        return dataIds;
    }

    public static String formatUserLoginBtns(List<GenericValue> userLogins){
        StringBuilder sb = new StringBuilder();
        if(Collections3.isNotEmpty(userLogins)){
            for (GenericValue login : userLogins) {
                sb.append("<div");
                if("N".equalsIgnoreCase(login.getString("enabled"))){
                    sb.append(" class='disabled'");
                }
                sb.append("><a href='#nowhere' class='hyperLinkStyle' onclick=\"$.recordManagement.editUserLogin('").append(login.getString("userLoginId")).append("')\">").append(login.getString("userLoginId")).append("</a></div>");
            }
        }
        return sb.toString();
    }
}
