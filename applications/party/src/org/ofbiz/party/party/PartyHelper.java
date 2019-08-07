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

package org.ofbiz.party.party;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * PartyHelper
 */
public class PartyHelper {

    public static final String module = PartyHelper.class.getName();

    private PartyHelper() {}

    public static String getPartyName(GenericValue partyObject) {
        return getPartyName(partyObject, false);
    }

    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst) {
        GenericValue partyObject = null;
        try {
            partyObject = EntityQuery.use(delegator).from("PartyNameView").where(UtilMisc.toMap("partyId", partyId)).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", module);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }
    }

    public static String getPartyName(GenericValue partyObject, boolean lastNameFirst) {
        if (partyObject == null) {
            return "";
        }
        if ("PartyGroup".equals(partyObject.getEntityName()) || "Person".equals(partyObject.getEntityName())) {
            return formatPartyNameObject(partyObject, lastNameFirst);
        } else {
            String partyId = null;
            try {
                partyId = partyObject.getString("partyId");
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Party object does not contain a party ID", module);
            }

            if (partyId == null) {
                Debug.logWarning("No party ID found; cannot get name based on entity: " + partyObject.getEntityName(), module);
                return "";
            } else {
                return getPartyName(partyObject.getDelegator(), partyId, lastNameFirst);
            }
        }
    }

    public static String formatPartyNameObject(GenericValue partyValue, boolean lastNameFirst) {
        if (partyValue == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        ModelEntity modelEntity = partyValue.getModelEntity();
        if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
            if (lastNameFirst) {
                if (UtilFormatOut.checkNull(partyValue.getString("lastName")) != null) {
                    result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
                    if (partyValue.getString("firstName") != null) {
                        result.append(", ");
                    }
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("firstName")));
            } else {
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("firstName"), "", " "));
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("middleName"), "", " "));
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            }
        }
        if (modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
            result.append(partyValue.getString("groupName"));
        }
        return result.toString();
    }

    public static boolean partyHasRoleType(Delegator delegator, String partyId, String roleTypeId) {
        GenericValue partyObject = null;
        try {
            List list = new ArrayList();
            list.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));

            EntityCondition cond1 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId);
            EntityCondition cond2 = EntityCondition.makeCondition("partnerType", EntityOperator.EQUALS, roleTypeId);
            list.add(EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2), EntityOperator.OR));

            list.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"));

            partyObject = EntityQuery.use(delegator).from("PartyPartnerType").where(list).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error check partyHasRoleType", module);
        }
        return partyObject != null;
    }

    /**
     * 是否机构合伙人
     * @param delegator
     * @param partyId
     * @param roleTypeId
     * @return
     */
    public static boolean isPartner(Delegator delegator, String partyId, String roleTypeId) {
        GenericValue partyObject = null;
        try {
            partyObject = EntityQuery.use(delegator).from("PartyGroup").where(UtilMisc.toMap("partyId",partyId, "partnerType", roleTypeId)).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error check partyHasRoleType", module);
        }
        return partyObject == null;
    }
}
