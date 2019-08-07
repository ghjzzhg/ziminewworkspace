import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.widget.portal.PortalPageWorker

GenericValue userLogin = context.get("userLogin");
String partyId = userLogin.getString("partyId");

String returnUrl;
GenericValue party = from("Party").where("partyId", partyId).queryOne();
String partyStatus = party.getString("statusId");
if("admin".equals(party.getString("partyId"))){
    returnUrl = "ManageAccounts";
}else if("PARTY_UNIDENTIFIED".equals(partyStatus)){
    returnUrl = "qualification";
}else if("PARTY_IDENTIFIED".equals(partyStatus)){
    returnUrl = "ProviderCases";
}

GenericValue nowLogin = from("UserLogin").where("partyId",partyId).queryOne();
String enabled = nowLogin.get("enabled");
if(!enabled.equals("Y"))
{
    return "default";
}

String parentPortalPageId = '';
String portalPageId = '';

List<GenericValue> securityGroups = from("UserLoginSecurityGroup").where("userLoginId", userLogin.get("userLoginId")).queryList();
String defaultMenuId = "";
if(UtilValidate.isNotEmpty(securityGroups)){
    for (GenericValue securityGroup : securityGroups) {
        String groupId = securityGroup.getString("groupId");
        if(groupId.equals("ZXDOC_PROVIDER") || groupId.equals("ZXDOC_COMPANY")){
            returnUrl = "subAccounts";
            defaultMenuId = "SubAccounts";
        }else if(groupId.equals("ZXDOC_PROVIDER_PERSON")){
            parentPortalPageId = "ZxdocProvider";
            defaultMenuId = "MyCases";
            break;
        }else if(groupId.equals("ZXDOC_COMPANY_PERSON")){
            parentPortalPageId = "ZxdocCompany";
            defaultMenuId = "MyCases";
            break;
        }else if(groupId.equals("ZXDOC_COMPANY_PARTNER")){
            parentPortalPageId = "ZxdocCompany";
            defaultMenuId = "MyCases";
            break;
        }else if(groupId.equals("ZXDOC_PROVIDER_PARTNER")){
            parentPortalPageId = "ZxdocProvider";
            defaultMenuId = "MyCases";
            break;
        }
    }
}
request.setAttribute("defaultMenuId",defaultMenuId);
request.setAttribute("parentPortalPageId", parentPortalPageId);

List<GenericValue> portalPages = PortalPageWorker.getPortalPages(parentPortalPageId, context);
if (UtilValidate.isNotEmpty(portalPages)) {
    Locale locale = (Locale) context.get("locale");
    for (GenericValue portalPage : portalPages) {
        if (UtilValidate.isNotEmpty(portalPage.getString("portalPageName"))) {
            portalPageId = portalPage.get("portalPageId");
            request.setAttribute("portalPageId", portalPageId);
            break;
        }
    }
}

if(UtilValidate.isNotEmpty(returnUrl)){
    return returnUrl;
}
if(UtilValidate.isNotEmpty(portalPageId)){
    response.sendRedirect("showPortalPage?portalPageId=" + portalPageId + "&parentPortalPageId=" + StringUtils.stripToEmpty(parentPortalPageId));
}
return "success";

