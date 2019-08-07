/*
 * Copyright (c) Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
/* @author: Michele Orru' (michele.orru@integratingweb.com) */

/* Here we suppose that the applicationId defined for an application in the entity HomeWebApps
   is equal to the webapp name of the same application, defined in his own ofbiz-component.xml.

   An example.
   
   We define the following in opentaps/homeapps/HomeAppsSeedData.xml:
   <HomeWebApps applicationId="party" name="Parties and Users" description="Party Description."
     imageUrl="/opentaps_images/integratingweb/party.png" imageHoverUrl="/opentaps_images/integratingweb/partyHover.png" linkUrl="/partymgr/control/main" lastUpdatedStamp="2009-07-18 16:41:58.415"
     lastUpdatedTxStamp="2009-07-18 16:41:58.415" createdStamp="2009-07-18 16:12:28.395" createdTxStamp="2009-07-18 16:12:28.395"/>

    Then, in applications/party/ofbiz-component.xml:

    <webapp name="party"
        title="Party"
        server="default-server"
        location="webapp/partymgr"
        base-permission="OFBTOOLS,PARTYMGR"
        mount-point="/partymgr"/>

   As you can clearly see, the webapp name and the applicationId are the same.

   The security checks are made on the base-permission String array, appending to the values
   the default _VIEW crud operation. Instead, when base-permission is NONE (not defined), the application
   can be accessed without any privileges (ecommerce, for example).

   In this way, as discussed with Si, we don't have to maintain permission relations
   in the HomeWebApps entity.

*/
import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.component.ComponentConfig
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

// security
security = request.getAttribute("security");
context.put("security", security);

// external login key
extLogin = request.getAttribute("externalLoginKey");
if (extLogin != null) {
    context.put("externalKeyParam", "externalLoginKey=" + requestAttributes.get("externalLoginKey"));
}
org.ofbiz.base.util.Debug.log("userLogin : " + userLogin);

Map<String, String[]> webappsMap = FastMap.newInstance();
//get all webapps defined in all the ofbiz-components
List<ComponentConfig.WebappInfo> webapps = ComponentConfig.getAllWebappResourceInfos();
//create a map entry (name , permissions[]) for every webapp
for (ComponentConfig.WebappInfo webapp : webapps) {
    webappsMap.put(webapp.getName() , webapp.getBasePermission());
}

List apps = FastList.newInstance();

List configuredApps = from("WebApps").orderBy("sequenceNum").queryList();
if (UtilValidate.isNotEmpty(configuredApps)) {
    for (GenericValue webapp : configuredApps) {
        applicationId = webapp.getString("applicationId")
        String[] permissions = webappsMap.get(applicationId);
        if (userLogin != null) {
            boolean permitted = true;
            if (permissions != null) {
                //  if there are permissions for this application, then check if the user can view it
                for (int i = 0; i < permissions.length; i++) {
                    // if the application has basePermissions and user doesn't has VIEW/ADMIN permissions on them, don't get the app
                    if (!"NONE".equals(permissions[i]) && !security.hasPermission(permissions[i] + "_VIEW", userLogin) && !security.hasPermission(permissions[i] + "_ADMIN", userLogin)) {
                        permitted = false;
                        break;
                    }
                }
            }
            if (permitted) {
                apps.add(webapp);
            }
        } else {
            // if user is not authenticated
            if (permissions == null) {
                // if there are no permissions required for the application, or if it is an external link,
                apps.add(webapp);
            } else if (permissions.length > 0) {
                //  or, if the application is defined with permission of "NONE",  such as the ofbiz e-commerce store
                if ("NONE".equals(permissions[0])) {
                    //permissions[0] will always exists
                    apps.add(webapp);
                }
            }
        }
    }

}
context.put("apps", apps);
