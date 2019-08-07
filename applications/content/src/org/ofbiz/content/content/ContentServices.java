package org.ofbiz.content.content;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.util.*;
import org.ofbiz.content.data.DataFileDescription;
import org.ofbiz.content.data.FileManagerFactory;
import org.ofbiz.content.data.FileTypeManager;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ContentServices Class
 */
public class ContentServices {

    public static final String module = ContentServices.class.getName();
    public static final String resource = "ContentUiLabels";

    /**
     * findRelatedContent Finds the related
     */
    public static Map<String, Object> findRelatedContent(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> results = FastMap.newInstance();

        GenericValue currentContent = (GenericValue) context.get("currentContent");
        String fromDate = (String) context.get("fromDate");
        String thruDate = (String) context.get("thruDate");
        String toFrom = (String) context.get("toFrom");
        Locale locale = (Locale) context.get("locale");
        if (toFrom == null) {
            toFrom = "TO";
        } else {
            toFrom = toFrom.toUpperCase();
        }

        List<String> assocTypes = UtilGenerics.checkList(context.get("contentAssocTypeList"));
        List<String> targetOperations = UtilGenerics.checkList(context.get("targetOperationList"));
        List<String> contentTypes = UtilGenerics.checkList(context.get("contentTypeList"));
        List<GenericValue> contentList = null;
        
        try {
            contentList = ContentWorker.getAssociatedContent(currentContent, toFrom, assocTypes, contentTypes, fromDate, thruDate);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentAssocRetrievingError", UtilMisc.toMap("errorString", e.toString()), locale));
        }

        if (UtilValidate.isEmpty(targetOperations)) {
            results.put("contentList", contentList);
            return results;
        }

        Map<String, Object> serviceInMap = FastMap.newInstance();
        serviceInMap.put("userLogin", context.get("userLogin"));
        serviceInMap.put("targetOperationList", targetOperations);
        serviceInMap.put("entityOperation", context.get("entityOperation"));

        List<GenericValue> permittedList = FastList.newInstance();
        Map<String, Object> permResults = null;
        for (GenericValue content : contentList) {
            serviceInMap.put("currentContent", content);
            try {
                permResults = dispatcher.runSync("checkContentPermission", serviceInMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem checking permissions", "ContentServices");
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentPermissionNotGranted", locale));
            }

            String permissionStatus = (String) permResults.get("permissionStatus");
            if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
                permittedList.add(content);
            }

        }

        results.put("contentList", permittedList);
        return results;
    }
    /**
     * This is a generic service for traversing a Content tree, typical of a blog response tree. It calls the ContentWorker.traverse method.
     */
    public static Map<String, Object> findContentParents(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> results = FastMap.newInstance();
        List<Object> parentList = FastList.newInstance();
        results.put("parentList", parentList);
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String contentId = (String)context.get("contentId");
        String contentAssocTypeId = (String)context.get("contentAssocTypeId");
        String direction = (String)context.get("direction");
        if (UtilValidate.isEmpty(direction)) {
            direction="To";
        }
        Map<String, Object> traversMap = FastMap.newInstance();
        traversMap.put("contentId", contentId);
        traversMap.put("direction", direction);
        traversMap.put("contentAssocTypeId", contentAssocTypeId);
        try {
            Map<String, Object> thisResults = dispatcher.runSync("traverseContent", traversMap);
            String errorMsg = ServiceUtil.getErrorMessage(thisResults);
            if (UtilValidate.isNotEmpty(errorMsg)) {
                Debug.logError("Problem in traverseContent. " + errorMsg, module);
                return ServiceUtil.returnError(errorMsg);
            }
            Map<String, Object> nodeMap = UtilGenerics.checkMap(thisResults.get("nodeMap"));
            walkParentTree(nodeMap, parentList);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure(e.getMessage());
        }
        return results;
    }

    private static void walkParentTree(Map<String, Object> nodeMap, List<Object> parentList) {
        List<Map<String, Object>> kids = UtilGenerics.checkList(nodeMap.get("kids"));
        if (UtilValidate.isEmpty(kids)) {
            parentList.add(nodeMap.get("contentId"));
        } else {
            for (Map<String, Object> node : kids) {
                walkParentTree(node, parentList);
            }
        }
    }
    /**
     * This is a generic service for traversing a Content tree, typical of a blog response tree. It calls the ContentWorker.traverse method.
     */
    public static Map<String, Object> traverseContent(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> results = FastMap.newInstance();
        Locale locale = (Locale) context.get("locale");

        String contentId = (String) context.get("contentId");
        String direction = (String) context.get("direction");
        if (direction != null && direction.equalsIgnoreCase("From")) {
            direction = "From";
        } else {
            direction = "To";
        }

        if (contentId == null) {
            contentId = "PUBLISH_ROOT";
        }

        GenericValue content = null;
        try {
            content = EntityQuery.use(delegator).from("Content").where("contentId", contentId).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity Error:" + e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentNoContentFound", UtilMisc.toMap("contentId", contentId), locale));
        }

        String fromDateStr = (String) context.get("fromDateStr");
        String thruDateStr = (String) context.get("thruDateStr");
        Timestamp fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            fromDate = UtilDateTime.toTimestamp(fromDateStr);
        }

        Timestamp thruDate = null;
        if (UtilValidate.isNotEmpty(thruDateStr)) {
            thruDate = UtilDateTime.toTimestamp(thruDateStr);
        }

        Map<String, Object> whenMap = FastMap.newInstance();
        whenMap.put("followWhen", context.get("followWhen"));
        whenMap.put("pickWhen", context.get("pickWhen"));
        whenMap.put("returnBeforePickWhen", context.get("returnBeforePickWhen"));
        whenMap.put("returnAfterPickWhen", context.get("returnAfterPickWhen"));

        String startContentAssocTypeId = (String) context.get("contentAssocTypeId");
        if (startContentAssocTypeId != null) {
            startContentAssocTypeId = "PUBLISH";
        }

        Map<String, Object> nodeMap = FastMap.newInstance();
        List<GenericValue> pickList = FastList.newInstance();
        ContentWorker.traverse(delegator, content, fromDate, thruDate, whenMap, 0, nodeMap, startContentAssocTypeId, pickList, direction);

        results.put("nodeMap", nodeMap);
        results.put("pickList", pickList);
        return results;
    }

    /**
     * Create a Content service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> createContent(DispatchContext dctx, Map<String, ? extends Object> context) {
        /*
        context.put("entityOperation", "_CREATE");
        List targetOperationList = ContentWorker.prepTargetOperationList(context, "_CREATE");

        List contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList); // for checking permissions
        //context.put("skipPermissionCheck", null);
        */

        Map<String, Object> result = createContentMethod(dctx, context);
        return result;
    }

    /**
     * Create a Content method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> createContentMethod(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        context.put("entityOperation", "_CREATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_CREATE");
        if (Debug.infoOn()) Debug.logInfo("in createContentMethod, targetOperationList: " + targetOperationList, null);

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String contentId = (String) context.get("contentId");
        //String contentTypeId = (String) context.get("contentTypeId");

        if (UtilValidate.isEmpty(contentId)) {
            contentId = delegator.getNextSeqId("Content");
        }

        GenericValue content = delegator.makeValue("Content", UtilMisc.toMap("contentId", contentId));
        content.setNonPKFields(context);

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");

        // get first statusId  for content out of the statusItem table if not provided
        if (UtilValidate.isEmpty(context.get("statusId"))) {
            try {
                GenericValue statusItem = EntityQuery.use(delegator).from("StatusItem")
                        .where("statusTypeId", "CONTENT_STATUS")
                        .orderBy("sequenceId").queryFirst();
                if (statusItem != null) {
                    content.put("statusId",  statusItem.get("statusId"));
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        content.put("createdByUserLogin", userLoginId);
        content.put("lastModifiedByUserLogin", userLoginId);
        content.put("createdDate", nowTimestamp);
        content.put("lastModifiedDate", nowTimestamp);

        context.put("currentContent", content);
        if (Debug.infoOn()) Debug.logInfo("in createContentMethod, context: " + context, null);

        Map<String, Object> permResults = ContentWorker.callContentPermissionCheckResult(delegator, dispatcher, context);
        String permissionStatus = (String) permResults.get("permissionStatus");
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            try {
                content.create();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            } catch (Exception e2) {
                return ServiceUtil.returnError(e2.getMessage());
            }

            result.put("contentId", contentId);
        } else {
            String errorMsg = (String) permResults.get(ModelService.ERROR_MESSAGE);
            result.put(ModelService.ERROR_MESSAGE, errorMsg);
            return ServiceUtil.returnFailure(errorMsg);
        }

        context.remove("currentContent");
        return result;
    }

    /**
     * Create a ContentAssoc service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> createContentAssoc(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        context.put("entityOperation", "_CREATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_CREATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);
        context.put("skipPermissionCheck", null);

        Map<String, Object> result = null;
        try {
            result = createContentAssocMethod(dctx, context);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericEntityException e2) {
            return ServiceUtil.returnError(e2.getMessage());
        } catch (Exception e3) {
            return ServiceUtil.returnError(e3.getMessage());
        }
        return result;
    }

    /**
     * Create a ContentAssoc method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> createContentAssocMethod(DispatchContext dctx, Map<String, ? extends Object> rcontext) throws GenericServiceException, GenericEntityException {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_CREATE");
        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);
        Locale locale = (Locale) context.get("locale");

        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();

        // This section guesses how contentId should be used (From or To) if
        // only a contentIdFrom o contentIdTo is passed in
        String contentIdFrom = (String) context.get("contentIdFrom");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentId = (String) context.get("contentId");
        int contentIdCount = 0;
        if (UtilValidate.isNotEmpty(contentIdFrom))
            contentIdCount++;
        if (UtilValidate.isNotEmpty(contentIdTo))
            contentIdCount++;
        if (UtilValidate.isNotEmpty(contentId))
            contentIdCount++;
        if (contentIdCount < 2) {
            Debug.logError("Not 2 out of ContentId/To/From.", "ContentServices");
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentCreateContentAssocMethodError", locale));
        }

        if (UtilValidate.isNotEmpty(contentIdFrom)) {
            if (UtilValidate.isEmpty(contentIdTo))
                contentIdTo = contentId;
        }
        if (UtilValidate.isNotEmpty(contentIdTo)) {
            if (UtilValidate.isEmpty(contentIdFrom))
                contentIdFrom = contentId;
        }

        /*
        String deactivateExisting = (String) context.get("deactivateExisting");
        if (deactivateExisting != null && deactivateExisting.equalsIgnoreCase("true")) {
            Map andMap = FastMap.newInstance();
            andMap.put("contentIdTo", contentIdTo);
            andMap.put("contentAssocTypeId", context.get("contentAssocTypeId"));

            String mapKey = (String) context.get("mapKey");
            if (UtilValidate.isNotEmpty(mapKey)) {
                andMap.put("mapKey", mapKey);
            }
            if (Debug.infoOn()) Debug.logInfo("DEACTIVATING CONTENTASSOC andMap: " + andMap, null);

            List assocList = EntityQuery.use(delegator).from("ContentAssoc").where(andMap).queryList();
            Iterator iter = assocList.iterator();
            while (iter.hasNext()) {
                GenericValue val = (GenericValue) iter.next();
                if (Debug.infoOn()) Debug.logInfo("DEACTIVATING CONTENTASSOC val: " + val, null);

                val.set("thruDate", UtilDateTime.nowTimestamp());
                val.store();
            }
        }
        */

        GenericValue contentAssoc = delegator.makeValue("ContentAssoc", FastMap.newInstance());
        contentAssoc.put("contentId", contentIdFrom);
        contentAssoc.put("contentIdTo", contentIdTo);
        contentAssoc.put("contentAssocTypeId", context.get("contentAssocTypeId"));
        contentAssoc.put("contentAssocPredicateId", context.get("contentAssocPredicateIdFrom"));
        contentAssoc.put("dataSourceId", context.get("dataSourceId"));

        Timestamp fromDate = (Timestamp) context.get("fromDate");
        if (fromDate == null) {
            contentAssoc.put("fromDate", UtilDateTime.nowTimestamp());
        } else {
            contentAssoc.put("fromDate", fromDate);
        }

        Timestamp thruDate = (Timestamp) context.get("thruDate");
        if (thruDate == null) {
            contentAssoc.put("thruDate", null);
        } else {
            contentAssoc.put("thruDate", thruDate);
        }

        contentAssoc.put("sequenceNum", context.get("sequenceNum"));
        contentAssoc.put("mapKey", context.get("mapKey"));

        String upperCoordinateStr = (String) context.get("upperCoordinate");
        if (UtilValidate.isEmpty(upperCoordinateStr)) {
            contentAssoc.put("upperCoordinate", null);
        } else {
            contentAssoc.put("upperCoordinate", upperCoordinateStr);
        }

        String leftCoordinateStr = (String) context.get("leftCoordinate");
        if (UtilValidate.isEmpty(leftCoordinateStr)) {
            contentAssoc.put("leftCoordinate", null);
        } else {
            contentAssoc.put("leftCoordinate", leftCoordinateStr);
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");
        String createdByUserLogin = userLoginId;
        String lastModifiedByUserLogin = userLoginId;

        Timestamp createdDate = UtilDateTime.nowTimestamp();
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

        contentAssoc.put("createdByUserLogin", createdByUserLogin);
        contentAssoc.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
        contentAssoc.put("createdDate", createdDate);
        contentAssoc.put("lastModifiedDate", lastModifiedDate);

        Map<String, Object> serviceInMap = FastMap.newInstance();
        String permissionStatus = null;
        serviceInMap.put("userLogin", context.get("userLogin"));
        serviceInMap.put("targetOperationList", targetOperationList);
        serviceInMap.put("contentPurposeList", contentPurposeList);
        serviceInMap.put("entityOperation", context.get("entityOperation"));
        serviceInMap.put("contentAssocPredicateId", context.get("contentAssocPredicateId"));
        serviceInMap.put("contentIdTo", contentIdTo);
        serviceInMap.put("contentIdFrom", contentIdFrom);
        serviceInMap.put("statusId", context.get("statusId"));
        serviceInMap.put("privilegeEnumId", context.get("privilegeEnumId"));
        serviceInMap.put("roleTypeList", context.get("roleTypeList"));
        serviceInMap.put("displayFailCond", context.get("displayFailCond"));

        Map<String, Object> permResults = null;
        permResults = dispatcher.runSync("checkAssocPermission", serviceInMap);
        permissionStatus = (String) permResults.get("permissionStatus");

        if (permissionStatus != null && permissionStatus.equals("granted")) {
            contentAssoc.create();
        } else {
            String errorMsg = (String)permResults.get(ModelService.ERROR_MESSAGE);
            result.put(ModelService.ERROR_MESSAGE, errorMsg);
            return ServiceUtil.returnFailure(errorMsg);
        }

        result.put("contentIdTo", contentIdTo);
        result.put("contentIdFrom", contentIdFrom);
        result.put("fromDate", contentAssoc.get("fromDate"));
        result.put("contentAssocTypeId", contentAssoc.get("contentAssocTypeId"));
        //Debug.logInfo("result:" + result, module);
        //Debug.logInfo("contentAssoc:" + contentAssoc, module);

        return result;
    }

    /**
     * A service wrapper for the updateContentMethod method. Forces permissions to be checked.
     */
    public static Map<String, Object> updateContent(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        context.put("entityOperation", "_UPDATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_UPDATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);
        context.put("skipPermissionCheck", null);

        Map<String, Object> result = updateContentMethod(dctx, context);
        return result;
    }

    /**
     * Update a Content method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty of calling a service.
     * DEJ20060610: why is this being done? It's a bad design because the service call overhead is not very big, but not calling through the service engine breaks functionality possibilities like ECAs and such
     */
    public static Map<String, Object> updateContentMethod(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        
        context.put("entityOperation", "_UPDATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_UPDATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);

        GenericValue content = null;
        Locale locale = (Locale) context.get("locale");
        String contentId = (String) context.get("contentId");
        try {
            content = EntityQuery.use(delegator).from("Content").where("contentId", contentId).queryOne();
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentNoContentFound", UtilMisc.toMap("contentId", contentId), locale));
        }
        context.put("currentContent", content);

        Map<String, Object> permResults = ContentWorker.callContentPermissionCheckResult(delegator, dispatcher, context);
        String permissionStatus = (String) permResults.get("permissionStatus");
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = (String) userLogin.get("userLoginId");
            String lastModifiedByUserLogin = userLoginId;
            Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

            // update status first to see if allowed
            if (UtilValidate.isNotEmpty(context.get("statusId"))) {
                Map<String, Object> statusInMap = UtilMisc.<String, Object>toMap("contentId", context.get("contentId"), "statusId", context.get("statusId"), "userLogin", userLogin);
                try {
                   dispatcher.runSync("setContentStatus", statusInMap);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem updating content Status", "ContentServices");
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentStatusUpdateError", UtilMisc.toMap("errorString", e), locale));
                }
            }

            content.setNonPKFields(context);
            content.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
            content.put("lastModifiedDate", lastModifiedDate);
            try {
                content.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        } else {
            String errorMsg = ContentWorker.prepPermissionErrorMsg(permResults);
            return ServiceUtil.returnError(errorMsg);
        }

        return result;
    }

    /**
     * Update a ContentAssoc service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> updateContentAssoc(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        context.put("entityOperation", "_UPDATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_UPDATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);
        context.put("skipPermissionCheck", null);

        Map<String, Object> result = updateContentAssocMethod(dctx, context);
        return result;
    }

    /**
     * Update a ContentAssoc method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> updateContentAssocMethod(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = FastMap.newInstance();

        context.put("entityOperation", "_UPDATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_UPDATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);

        // This section guesses how contentId should be used (From or To) if
        // only a contentIdFrom o contentIdTo is passed in
        String contentIdFrom = (String) context.get("contentId");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentId = (String) context.get("contentId");
        String contentAssocTypeId = (String) context.get("contentAssocTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

        GenericValue contentAssoc = null;
        try {
            contentAssoc = EntityQuery.use(delegator).from("ContentAssoc").where("contentId", contentId, "contentIdTo", contentIdTo, "contentAssocTypeId", contentAssocTypeId, "fromDate", fromDate).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity Error:" + e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentAssocRetrievingError", UtilMisc.toMap("errorString", e.getMessage()), locale));
        }
        if (contentAssoc == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentAssocUpdateError", locale));
        }

        contentAssoc.put("contentAssocPredicateId", context.get("contentAssocPredicateId"));
        contentAssoc.put("dataSourceId", context.get("dataSourceId"));
        contentAssoc.set("thruDate", context.get("thruDate"));
        contentAssoc.set("sequenceNum", context.get("sequenceNum"));
        contentAssoc.put("mapKey", context.get("mapKey"));

        String upperCoordinateStr = (String) context.get("upperCoordinate");
        if (UtilValidate.isEmpty(upperCoordinateStr)) {
            contentAssoc.put("upperCoordinate", null);
        } else {
            contentAssoc.setString("upperCoordinate", upperCoordinateStr);
        }

        String leftCoordinateStr = (String) context.get("leftCoordinate");
        if (UtilValidate.isEmpty(leftCoordinateStr)) {
            contentAssoc.put("leftCoordinate", null);
        } else {
            contentAssoc.setString("leftCoordinate", leftCoordinateStr);
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");
        String lastModifiedByUserLogin = userLoginId;
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
        contentAssoc.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
        contentAssoc.put("lastModifiedDate", lastModifiedDate);

        String permissionStatus = null;
        Map<String, Object> serviceInMap = FastMap.newInstance();
        serviceInMap.put("userLogin", context.get("userLogin"));
        serviceInMap.put("targetOperationList", targetOperationList);
        serviceInMap.put("contentPurposeList", contentPurposeList);
        serviceInMap.put("entityOperation", context.get("entityOperation"));
        serviceInMap.put("contentIdTo", contentIdTo);
        serviceInMap.put("contentIdFrom", contentIdFrom);

        Map<String, Object> permResults = null;
        try {
            permResults = dispatcher.runSync("checkAssocPermission", serviceInMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem checking permissions", "ContentServices");
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentPermissionNotGranted", locale));
        }
        permissionStatus = (String) permResults.get("permissionStatus");

        if (permissionStatus != null && permissionStatus.equals("granted")) {
            try {
                contentAssoc.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        } else {
            String errorMsg = ContentWorker.prepPermissionErrorMsg(permResults);
            return ServiceUtil.returnError(errorMsg);
        }

        return result;
    }

    /**
     * Update a ContentAssoc service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> deactivateContentAssoc(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        context.put("entityOperation", "_UPDATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_UPDATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);
        context.put("skipPermissionCheck", null);

        Map<String, Object> result = deactivateContentAssocMethod(dctx, context);
        return result;
    }

    /**
     * Update a ContentAssoc method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map<String, Object> deactivateContentAssocMethod(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        Locale locale = (Locale) context.get("locale");
        context.put("entityOperation", "_UPDATE");
        List<String> targetOperationList = ContentWorker.prepTargetOperationList(context, "_UPDATE");

        List<String> contentPurposeList = ContentWorker.prepContentPurposeList(context);
        context.put("targetOperationList", targetOperationList);
        context.put("contentPurposeList", contentPurposeList);

        GenericValue pk = delegator.makeValue("ContentAssoc");
        pk.setAllFields(context, false, null, Boolean.TRUE);
        pk.setAllFields(context, false, "ca", Boolean.TRUE);
        //String contentIdFrom = (String) context.get("contentId");
        //String contentIdTo = (String) context.get("contentIdTo");
        //String contentId = (String) context.get("contentId");
        //String contentAssocTypeId = (String) context.get("contentAssocTypeId");
        //Timestamp fromDate = (Timestamp) context.get("fromDate");

        GenericValue contentAssoc = null;
        try {
            //contentAssoc = EntityQuery.use(delegator).from("ContentAssoc").where("contentId", contentId, "contentIdTo", contentIdTo, "contentAssocTypeId", contentAssocTypeId, "fromDate", fromDate).queryOne();
            contentAssoc = EntityQuery.use(delegator).from("ContentAssoc").where(pk).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity Error:" + e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentAssocRetrievingError", UtilMisc.toMap("errorString", e.getMessage()), locale));
        }

        if (contentAssoc == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentAssocDeactivatingError", locale));
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");
        String lastModifiedByUserLogin = userLoginId;
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
        contentAssoc.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
        contentAssoc.put("lastModifiedDate", lastModifiedDate);
        contentAssoc.put("thruDate", UtilDateTime.nowTimestamp());

        String permissionStatus = null;
        Map<String, Object> serviceInMap = FastMap.newInstance();
        serviceInMap.put("userLogin", context.get("userLogin"));
        serviceInMap.put("targetOperationList", targetOperationList);
        serviceInMap.put("contentPurposeList", contentPurposeList);
        serviceInMap.put("entityOperation", context.get("entityOperation"));
        serviceInMap.put("contentIdTo", contentAssoc.get("contentIdTo"));
        serviceInMap.put("contentIdFrom", contentAssoc.get("contentId"));

        Map<String, Object> permResults = null;
        try {
            permResults = dispatcher.runSync("checkAssocPermission", serviceInMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem checking permissions", "ContentServices");
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentPermissionNotGranted", locale));
        }
        permissionStatus = (String) permResults.get("permissionStatus");

        if (permissionStatus != null && permissionStatus.equals("granted")) {
            try {
                contentAssoc.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        } else {
            String errorMsg = ContentWorker.prepPermissionErrorMsg(permResults);
            return ServiceUtil.returnError(errorMsg);
        }

        return result;
    }

    /**
     * Deactivates any active ContentAssoc (except the current one) that is associated with the passed in template/layout contentId and mapKey.
     */
    public static Map<String, Object> deactivateAssocs(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String contentIdTo = (String) context.get("contentIdTo");
        String mapKey = (String) context.get("mapKey");
        String contentAssocTypeId = (String) context.get("contentAssocTypeId");
        String activeContentId = (String) context.get("activeContentId");
        String contentId = (String) context.get("contentId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Locale locale = (Locale) context.get("locale");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        String sequenceNum = null;
        Map<String, Object> results = FastMap.newInstance();

        try {
            GenericValue activeAssoc = null;
            if (fromDate != null) {
                activeAssoc = EntityQuery.use(delegator).from("ContentAssoc").where("contentId", activeContentId, "contentIdTo", contentIdTo, "fromDate", fromDate, "contentAssocTypeId", contentAssocTypeId).queryOne();
                if (activeAssoc == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentAssocNotFound", UtilMisc.toMap("activeContentId", activeContentId, "contentIdTo", contentIdTo, "contentAssocTypeId", contentAssocTypeId, "fromDate", fromDate), locale));
                }
                sequenceNum = (String) activeAssoc.get("sequenceNum");
            }

            List<EntityCondition> exprList = FastList.newInstance();
            exprList.add(EntityCondition.makeCondition("mapKey", EntityOperator.EQUALS, mapKey));
            if (sequenceNum != null) {
                exprList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));
            }
            exprList.add(EntityCondition.makeCondition("mapKey", EntityOperator.EQUALS, mapKey));
            exprList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
            exprList.add(EntityCondition.makeCondition("contentIdTo", EntityOperator.EQUALS, contentIdTo));
            exprList.add(EntityCondition.makeCondition("contentAssocTypeId", EntityOperator.EQUALS, contentAssocTypeId));

            if (UtilValidate.isNotEmpty(activeContentId)) {
                exprList.add(EntityCondition.makeCondition("contentId", EntityOperator.NOT_EQUAL, activeContentId));
            }
            if (UtilValidate.isNotEmpty(contentId)) {
                exprList.add(EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, contentId));
            }

            EntityConditionList<EntityCondition> assocExprList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
            List<GenericValue> relatedAssocs = EntityQuery.use(delegator).from("ContentAssoc")
                    .where(assocExprList)
                    .orderBy("fromDate").filterByDate().queryList();
            //if (Debug.infoOn()) Debug.logInfo("in deactivateAssocs, relatedAssocs:" + relatedAssocs, module);

            for (GenericValue val : relatedAssocs) {
                val.set("thruDate", nowTimestamp);
                val.store();
                //if (Debug.infoOn()) Debug.logInfo("in deactivateAssocs, val:" + val, module);
            }
            results.put("deactivatedList", relatedAssocs);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        return results;
    }

    /**
     * Get and render subcontent associated with template id and mapkey. If subContentId is supplied, that content will be rendered without searching for other
     * matching content.
     */
    public static Map<String, Object> renderSubContentAsText(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> results = FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        Map<String,Object> templateContext = UtilGenerics.checkMap(context.get("templateContext"));
        String contentId = (String) context.get("contentId");
        // Timestamp fromDate = (Timestamp) context.get("fromDate");
        // GenericValue userLogin = (GenericValue) context.get("userLogin");

        if (templateContext != null && UtilValidate.isEmpty(contentId)) {
            contentId = (String) templateContext.get("contentId");
        }
        String mapKey = (String) context.get("mapKey");
        if (templateContext != null && UtilValidate.isEmpty(mapKey)) {
            mapKey = (String) templateContext.get("mapKey");
        }
        //String subContentId = (String) context.get("subContentId");
        //if (templateContext != null && UtilValidate.isEmpty(subContentId)) {
            //subContentId = (String) templateContext.get("subContentId");
        //}
        String mimeTypeId = (String) context.get("mimeTypeId");
        if (templateContext != null && UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) templateContext.get("mimeTypeId");
        }
        Locale locale = (Locale) context.get("locale");
        if (templateContext != null && locale == null) {
            locale = (Locale) templateContext.get("locale");
        }
        GenericValue subContentDataResourceView = (GenericValue) context.get("subContentDataResourceView");
        if (templateContext != null && subContentDataResourceView == null) {
            subContentDataResourceView = (GenericValue) templateContext.get("subContentDataResourceView");
        }

        Writer out = (Writer) context.get("outWriter");
        Writer outWriter = new StringWriter();

        if (templateContext == null) {
            templateContext = FastMap.newInstance();
        }

        try {
            ContentWorker.renderSubContentAsText(dispatcher, delegator, contentId, outWriter, mapKey, templateContext, locale, mimeTypeId, true);
            out.write(outWriter.toString());
            results.put("textData", outWriter.toString());
        } catch (GeneralException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        } catch (IOException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        }

        return results;

    }

    /**
     * Get and render subcontent associated with template id and mapkey. If subContentId is supplied, that content will be rendered without searching for other
     * matching content.
     */
    public static Map<String, Object> renderContentAsText(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Writer out = (Writer) context.get("outWriter");

        Map<String,Object> templateContext = UtilGenerics.checkMap(context.get("templateContext"));
        //GenericValue userLogin = (GenericValue)context.get("userLogin");
        String contentId = (String) context.get("contentId");
        if (templateContext != null && UtilValidate.isEmpty(contentId)) {
            contentId = (String) templateContext.get("contentId");
        }
        String mimeTypeId = (String) context.get("mimeTypeId");
        if (templateContext != null && UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) templateContext.get("mimeTypeId");
        }
        Locale locale = (Locale) context.get("locale");
        if (templateContext != null && locale == null) {
            locale = (Locale) templateContext.get("locale");
        }

        if (templateContext == null) {
            templateContext = FastMap.newInstance();
        }

        Writer outWriter = new StringWriter();
        GenericValue view = (GenericValue)context.get("subContentDataResourceView");
        if (view != null && view.containsKey("contentId")) {
            contentId = view.getString("contentId");
        }

        try {
            ContentWorker.renderContentAsText(dispatcher, delegator, contentId, outWriter, templateContext, locale, mimeTypeId, null, null, true);
            if (out != null) out.write(outWriter.toString());
            results.put("textData", outWriter.toString());
        } catch (GeneralException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        } catch (IOException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        }
        return results;
    }

    public static Map<String, Object> linkContentToPubPt(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> results = FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        String contentId = (String) context.get("contentId");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentAssocTypeId = (String) context.get("contentAssocTypeId");
        String statusId = (String) context.get("statusId");
        String privilegeEnumId = (String) context.get("privilegeEnumId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        if (Debug.infoOn()) Debug.logInfo("in publishContent, statusId:" + statusId, module);
        if (Debug.infoOn()) Debug.logInfo("in publishContent, userLogin:" + userLogin, module);

        Map<String, Object> mapIn = FastMap.newInstance();
        mapIn.put("contentId", contentId);
        mapIn.put("contentIdTo", contentIdTo);
        mapIn.put("contentAssocTypeId", contentAssocTypeId);
        String publish = (String) context.get("publish");

        try {
            boolean isPublished = false;
            GenericValue contentAssocViewFrom = ContentWorker.getContentAssocViewFrom(delegator, contentIdTo, contentId, contentAssocTypeId, statusId, privilegeEnumId);
            if (contentAssocViewFrom != null)
                isPublished = true;
            if (Debug.infoOn()) Debug.logInfo("in publishContent, contentId:" + contentId + " contentIdTo:" + contentIdTo + " contentAssocTypeId:" + contentAssocTypeId + " publish:" + publish + " isPublished:" + isPublished, module);
            if (UtilValidate.isNotEmpty(publish) && publish.equalsIgnoreCase("Y")) {
                GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId", contentId).queryOne();
                String contentStatusId = (String) content.get("statusId");
                String contentPrivilegeEnumId = (String) content.get("privilegeEnumId");

                if (Debug.infoOn()) Debug.logInfo("in publishContent, statusId:" + statusId + " contentStatusId:" + contentStatusId + " privilegeEnumId:" + privilegeEnumId + " contentPrivilegeEnumId:" + contentPrivilegeEnumId, module);
                // Don't do anything if link was already there
                if (!isPublished) {
                    //Map thisResults = dispatcher.runSync("deactivateAssocs", mapIn);
                    //String errorMsg = ServiceUtil.getErrorMessage(thisResults);
                    //if (UtilValidate.isNotEmpty(errorMsg)) {
                    //Debug.logError("Problem running deactivateAssocs. " + errorMsg, "ContentServices");
                    //return ServiceUtil.returnError(errorMsg);
                    //}
                    content.put("privilegeEnumId", privilegeEnumId);
                    content.put("statusId", statusId);
                    content.store();

                    mapIn = FastMap.newInstance();
                    mapIn.put("contentId", contentId);
                    mapIn.put("contentIdTo", contentIdTo);
                    mapIn.put("contentAssocTypeId", contentAssocTypeId);
                    mapIn.put("mapKey", context.get("mapKey"));
                    mapIn.put("fromDate", UtilDateTime.nowTimestamp());
                    mapIn.put("createdDate", UtilDateTime.nowTimestamp());
                    mapIn.put("lastModifiedDate", UtilDateTime.nowTimestamp());
                    mapIn.put("createdByUserLogin", userLogin.get("userLoginId"));
                    mapIn.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
                    delegator.create("ContentAssoc", mapIn);
                }
            } else {
                // Only deactive if currently published
                if (isPublished) {
                    Map<String, Object> thisResults = dispatcher.runSync("deactivateAssocs", mapIn);
                    String errorMsg = ServiceUtil.getErrorMessage(thisResults);
                    if (UtilValidate.isNotEmpty(errorMsg)) {
                        Debug.logError("Problem running deactivateAssocs. " + errorMsg, "ContentServices");
                        return ServiceUtil.returnError(errorMsg);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting existing content", "ContentServices");
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem running deactivateAssocs", "ContentServices");
            return ServiceUtil.returnError(e.getMessage());
        }

        return results;
    }

    public static Map<String, Object> publishContent(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException{
        Map<String, Object> result = FastMap.newInstance();
        GenericValue content = (GenericValue)context.get("content");
        
        try {
            content.put("statusId", "CTNT_PUBLISHED");
            content.store();
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> getPrefixedMembers(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException{
        Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> mapIn = UtilGenerics.checkMap(context.get("mapIn"));
        String prefix = (String)context.get("prefix");
        Map<String, Object> mapOut = FastMap.newInstance();
        result.put("mapOut", mapOut);
        if (mapIn != null) {
            Set<Map.Entry<String, Object>> entrySet = mapIn.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                if (key.startsWith(prefix)) {
                    // String keyBase = key.substring(prefix.length());
                    Object value = entry.getValue();
                    mapOut.put(key, value);
                }
            }
        }
        return result;
    }

    public static Map<String, Object> splitString(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException{
        Map<String, Object> result = FastMap.newInstance();
        List<String> outputList = FastList.newInstance();
        String delimiter = UtilFormatOut.checkEmpty((String)context.get("delimiter"), "|");
        String inputString = (String)context.get("inputString");
        if (UtilValidate.isNotEmpty(inputString)) {
            outputList = StringUtil.split(inputString, delimiter);
        }
        result.put("outputList", outputList);
        return result;
    }

    public static Map<String, Object> joinString(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException{
        Map<String, Object> result = FastMap.newInstance();
        String outputString = null;
        String delimiter = UtilFormatOut.checkEmpty((String)context.get("delimiter"), "|");
        List<String> inputList = UtilGenerics.checkList(context.get("inputList"));
        if (inputList != null) {
            outputString = StringUtil.join(inputList, delimiter);
        }
        result.put("outputString", outputString);
        return result;
    }

    public static Map<String, Object> urlEncodeArgs(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericServiceException{
        Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> mapFiltered = FastMap.newInstance();
        Map<String, Object> mapIn = UtilGenerics.checkMap(context.get("mapIn"));
        if (mapIn != null) {
            Set<Map.Entry<String, Object>> entrySet = mapIn.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    if (UtilValidate.isNotEmpty(value)) {
                        mapFiltered.put(key, value);
                    }
                } else if (value != null) {
                    mapFiltered.put(key, value);
                }
            }
            String outputString = UtilHttp.urlEncodeArgs(mapFiltered);
            result.put("outputString", outputString);
        }
        return result;
    }

    public static String fineUpload(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map<String, String> result = FastMap.newInstance();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            String fileId = request.getParameter("fileId");
            String storeType =  UtilProperties.getPropertyValue("content.properties", "content.store.type");
            if(UtilValidate.isNotEmpty(fileId)){
                GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId", fileId)).queryOne();
                if(UtilValidate.isNotEmpty(genericValue)){
                    storeType = genericValue.getString("dataResourceTypeId");
                }
            }
            FileTypeManager fileManager = FileManagerFactory.getFileManager(storeType, delegator);
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            if(userLogin == null){//changed by galaxypan@2017-09-03:注册时上传，此时无登录用户
                userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").queryOne();
            }
            List items = upload.parseRequest(request);
            Iterator itr = items.iterator();
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {
                    Debug.logVerbose("表单参数名:" + item.getFieldName()
                            + "，表单参数值:" + item.getString("UTF-8"), module);
                } else {
                    String itemFileName = item.getName();
                    if (UtilValidate.isNotEmpty(itemFileName)) {
                        Debug.logVerbose("上传文件的大小:" + item.getSize(), module);
                        Debug.logVerbose("上传文件的类型:" + item.getContentType(), module);
                        // item.getName()返回上传文件在客户端的完整路径名称
                        Debug.logVerbose("上传文件的名称:" + itemFileName, module);
                        //changed by galaxypan@2017-09-03:文件名太长的自动截取
                        if(itemFileName.length() > 50){
                            itemFileName = FilenameUtils.getBaseName(itemFileName).substring(0, 40) + "." + FilenameUtils.getExtension(itemFileName);
                        }
                        String overwrite = request.getParameter("overwrite");
                        DataFileDescription fileDescription = new DataFileDescription(itemFileName, item.getSize(), item.getContentType(), item.get(), request.getParameter("fileId"), UtilValidate.isEmpty(overwrite) ? true : Boolean.parseBoolean(overwrite));
                        String seqId = fileManager.storeFile(fileDescription, userLogin);
                        result.put("name", itemFileName);
                        result.put("path", seqId);
                        String fileName = itemFileName;
                        if(fileName.indexOf(".jpg") > 0 || fileName.indexOf(".jpeg") > 0 || fileName.indexOf(".gif") > 0 || fileName.indexOf(".png") > 0 || fileName.indexOf(".bmp") > 0){
                            request.setAttribute("url","showPhoto?fileId="+seqId);
                        }else{
                            request.setAttribute("url","javascript:viewPdfInLayer(" + seqId + ")");
                        }
                        request.setAttribute("fileId", seqId);
                        request.setAttribute("fileName", fileName);
                        request.setAttribute("error", "0");
                        request.setAttribute("upload.message", "上传文件成功！");
                        request.setAttribute("status", true);
                        request.setAttribute("fileType", FilenameUtils.getExtension(fileName));
                    } else {
                        request.setAttribute("upload.message", "没有选择上传文件！");
                        request.setAttribute("status", false);
                        request.setAttribute("url","");
                        request.setAttribute("error", "1");
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("status", false);
            request.setAttribute("upload.message", "上传文件失败！");
        }
        request.setAttribute("data", result);
        return "success";
    }

    public static String removeUpload(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map<String, Object> result = FastMap.newInstance();
        String fileId = request.getParameter("fileId");
        if(UtilValidate.isNotEmpty(fileId)){
            result.put("fileId", fileId);
            try {
                GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId", fileId)).queryOne();
                if(dataResource != null){
                    FileTypeManager fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
                    fileManager.delFile(fileId);
                }else{
                    result.put("message", "文件已删除");
                }
                result.put("message", "文件删除成功！");
                result.put("status", true);
            } catch (Exception e) {
                Debug.logError(e, module);
                result.put("status", false);
                result.put("message", "文件删除失败！");
            }
        }
        request.setAttribute("data", result);
        return "success";
    }
    
    public static String saveShareFilePrivate(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map<String, Object> result = FastMap.newInstance();
        String fileId = request.getParameter("fileId");
        String sharedPartyIds = request.getParameter("sharedPartyIds");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        result.put("fileId", fileId);
        result.put("sharedPartyIds", sharedPartyIds);
        try {
        	List<GenericValue> folderInfoList = EntityQuery.use(delegator).from("TblFileOwnership").where(UtilMisc.toMap("partyId", userLogin.getString("partyId"),"fileId",fileId)).queryList();
			if(UtilValidate.isNotEmpty(folderInfoList)){
				GenericValue folderInfo = folderInfoList.get(0);
	        	for(String partyId : sharedPartyIds.split(",")){
	        		saveTblDirectoryStructure(fileId,folderInfo.getString("folderId"),partyId,userLogin,delegator);
	        	}
			}
            result.put("message", "分享成功！");
            result.put("status", true);
        } catch (Exception e) {
            Debug.logError(e, module);
            result.put("message", "分享失败！");
            result.put("status", false);
        }
        request.setAttribute("data", result);
        return "success";
    }
	public static void saveTblDirectoryStructure(String fileId,String folderId,String partyId,GenericValue userLogin,GenericDelegator delegator){
    	Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateTime = format.format(date);
		System.out.println("fileId:"+fileId);
		System.out.println("folderId:"+folderId);
		System.out.println("partyId:"+partyId);
		try{
			List<GenericValue> tblFileOwnershipListFlag = EntityQuery.use(delegator).from("TblFileOwnership").where(UtilMisc.toMap("fileId", fileId,"folderId", folderId,"partyId", partyId)).queryList();
			if(fileId!=null && UtilValidate.isEmpty(tblFileOwnershipListFlag)){
				//如果是文件夹下有文件放置到TblFileOwnership表中
				List<GenericValue> tblFileOwnershipList = EntityQuery.use(delegator).from("TblFileOwnership").where(UtilMisc.toMap("fileId", fileId,"folderId", folderId,"partyId", userLogin.getString("partyId"))).queryList();
				if(UtilValidate.isNotEmpty(tblFileOwnershipList)){
					GenericValue tblFileOwnership = tblFileOwnershipList.get(0);
					Map<String, Object> fileMap = new HashMap<>();
					String id = delegator.getNextSeqId("TblFileOwnership");
					fileMap.put("id", id);
					fileMap.put("fileId", tblFileOwnership.getString("fileId"));
					fileMap.put("folderId", tblFileOwnership.getString("folderId"));
					fileMap.put("partyId", partyId);
					fileMap.put("fileType", "2");
					fileMap.put("filePermissions", tblFileOwnership.getString("filePermissions"));
					fileMap.put("createPartyId", userLogin.get("partyId"));
					fileMap.put("folderStructure", tblFileOwnership.getString("folderStructure"));
					fileMap.put("createFileTime", timestamp);
					fileMap.put("fileVersion", dateTime);
					GenericValue file = delegator.makeValue("TblFileOwnership",fileMap);
					file.create();
				}
			}
		} catch (Exception e) {
            Debug.logError(e, module);
        }
    }
}
