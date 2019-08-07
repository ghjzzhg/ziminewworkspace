/*
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
 */

/*
 * This script is also referenced by the ecommerce's screens and
 * should not contain order component's specific code.
 */
import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.party.party.PartyHelper
import java.text.SimpleDateFormat
import javolution.util.FastMap
// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.

includeStaff = parameters.get("includeStaff");
dummyRoot = parameters.get("dummyRoot");
completedTree =  FastList.newInstance();
completedTreeContext =  FastList.newInstance();
staffData =  FastList.newInstance();
existParties =  FastList.newInstance();
subtopLists =  FastList.newInstance();
rootId = "Company";
root = [:];
root.put("id", rootId);
root.put("name", delegator.findByPrimaryKeyCache("PartyGroup", [partyId: "Company"]).get("groupName"));
root.put("code", rootId);
root.put("iconSkin", "pIcon01");
root.put("pId", "");
completedTreeContext.add(root);

if(dummyRoot){
    dummyRoot = [:];
    dummyRoot.put("id", "dummyRoot");
    root.put("pId", "dummyRoot");
    dummyRoot.put("code", "");
    dummyRoot.put("iconSkin", "pIcon00");
    dummyRoot.put("pId", "");
    dummyRoot.put("name", "组织部门");
    completedTreeContext.add(dummyRoot);
}

def addStaffNode(partyId){
    if(includeStaff){
        memberResult = runService("getDepartmentMembers", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "direct", true, "VIEW_SIZE", "100"));
        members = memberResult.get("data");
        if(members){
            for(member in members){
                staffData.add(["id": member.get("partyId"), code: member.get("partyId"), name: member.get("employeeName"), pId: partyId, isHidden: true]);
            }
        }
    }
}

addStaffNode(rootId);

def findInternalOrg(parentId){
//internalOrg list
    hasChild = false;
    partyRelationships = from("PartyRelationship").where("partyIdFrom", parentId, "partyRelationshipTypeId", "GROUP_ROLLUP").filterByDate().queryList();
    if (partyRelationships) {

        //root
        /*partyRoot = from("PartyGroup").where("partyId", partyId).queryOne();
        partyRootMap = FastMap.newInstance();
        partyRootMap.put("partyId", partyId);
        partyRootMap.put("groupName", partyRoot.getString("groupName"));*/

        //child
        for(partyRelationship in partyRelationships) {
            partyIdTo = partyRelationship.getString("partyIdTo");
            partyTo = from("Party").where("partyId", partyIdTo).cache().queryOne();
            if(partyTo.getString("partyTypeId").equals("PERSON")){
                continue;//只显示PartyGroup
            }
            hasChild = true;
            partyGroupMap = [:];

            partyGroupMap.put("id", partyIdTo);
            partyGroupMap.put("code", partyGroupMap.get("id"));
            partyGroupMap.put("pId", partyRelationship.getString("partyIdFrom"));
            partyGroupMap.put("name", PartyHelper.getPartyName(delegator, partyRelationship.getString("partyIdTo"), true));
            completedTreeContext.add(partyGroupMap);
            addStaffNode(partyIdTo);

            if(findInternalOrg(partyIdTo)){
                partyGroupMap.put("iconSkin", "pIcon01");
            }else{
                partyGroupMap.put("iconSkin", "icon01");
            }
//        subtopLists.addAll(partyRelationship.getString("partyIdTo"));
        }

//    partyRootMap.put("child", completedTreeContext);
//    completedTree.add(partyRootMap);

    }
    return hasChild;
}

findInternalOrg(rootId);
// The complete tree list for the category tree
Calendar calendar = Calendar.getInstance();//此时打印它获取的是系统当前时间
calendar.add(Calendar.DATE, -1);    //得到前一天
//Date date = calendar.getTime();
//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
String  yestedayDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
//String yestedayDate = df.format(date);
context.yestedayDate = yestedayDate;
context.completedTree = completedTree;
context.subtopLists = subtopLists;
context.completedTreeContext = completedTreeContext;
context.staffData = staffData;
//组织树获取json
if(includeStaff){
    request.setAttribute("data", [completedTreeContext, staffData])
}else{
    request.setAttribute("data", completedTreeContext)
}
