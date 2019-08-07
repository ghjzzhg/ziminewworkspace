package org.ofbiz.oa;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class ListOfWorkUtil{
    private static GenericValue listOfWork = null;
    private static String listOfWorkId = null;
    public static String getListOfWorkName(Delegator delegator,String id){
        initListOfWorkName(delegator,id);
        return (String) listOfWork.get("listOfWorkName");
    }

    public static String getListOfWorkType(Delegator delegator,String id){
        initListOfWorkName(delegator,id);
        String type = (String) listOfWork.get("listOfWorkType");
        GenericValue enumeration = null;
        try {
            enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", type),false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return (String) enumeration.get("description");
    }

    public static void initListOfWorkName(Delegator delegator,String id){
        if(listOfWorkId == null || !id.equals(listOfWorkId)){
            listOfWorkId = id;
            try {
                listOfWork = delegator.findOne("TblListOfWork", UtilMisc.toMap("listOfWorkId", id),false);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
    }
}


