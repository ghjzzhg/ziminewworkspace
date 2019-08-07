package org.ofbiz.zxdoc

import org.ofbiz.base.util.FileUtil
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.widget.model.HtmlWidget
import org.ofbiz.widget.model.ModelScreen
import org.ofbiz.widget.model.ModelScreenWidget
import org.ofbiz.widget.model.ModelWidget
import org.ofbiz.widget.model.ScreenFactory
import org.ofbiz.widget.renderer.ScreenRenderer
import org.w3c.dom.Element

/**
 * Created by Administrator on 2016/12/14.
 */
String emailId = parameters.emailId;
GenericValue emailInfo = EntityQuery.use(delegator).from("EmailTemplateSetting").where("emailTemplateSettingId",emailId).queryOne();
String path = emailInfo.get("bodyScreenLocation");
ScreenFactory screen = new ScreenFactory();
String screenName = screen.getScreenNameFromCombined(path);
String resourceName = screen.getResourceNameFromCombined(path);
ModelScreen modelScreen = screen.getScreenFromLocation(resourceName,screenName);
ModelScreenWidget.Section section = modelScreen.section;
List<ModelScreenWidget> subWidgets = section.subWidgets;
ModelScreenWidget.PlatformSpecific subWidget = subWidgets.get(0);
Map<String, ModelScreenWidget> tsubWidgets = subWidget.subWidgets;
HtmlWidget htmlWidget = tsubWidgets.get("html");
List<ModelScreenWidget> htmlList = htmlWidget.subWidgets;
HtmlWidget.HtmlTemplate htmlscreen = htmlList.get(0);
String location = htmlscreen.getLocation(new HashMap());
File file = FileUtil.getFile(location);
String html = FileUtil.readTextFile(file,true);
context.html = html;
context.emailId = emailId;