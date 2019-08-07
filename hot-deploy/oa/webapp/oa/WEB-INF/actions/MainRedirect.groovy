import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.widget.portal.PortalPageWorker

parentPortalPageId = 'OA';

List<GenericValue> portalPages = PortalPageWorker.getPortalPages(parentPortalPageId, context);
if (UtilValidate.isNotEmpty(portalPages)) {
    Locale locale = (Locale) context.get("locale");
    for (GenericValue portalPage : portalPages) {
        if (UtilValidate.isNotEmpty(portalPage.getString("portalPageName"))) {
            response.sendRedirect("showPortalPage?portalPageId=" + portalPage.get("portalPageId") + "&parentPortalPageId=" + StringUtils.stripToEmpty(portalPage.get("parentPortalPageId")));
            break;
        }
    }
}