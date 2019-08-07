import eu.bitwalker.useragentutils.UserAgent
import org.ofbiz.base.util.UtilValidate

UserAgent ua = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
context.isXp = ua.operatingSystem == 'WINDOWS_XP';
context.isIE = ua.browser.toString().contains('IE');
float version = 0.0;
if(UtilValidate.isNotEmpty(ua.browserVersion)){
    version = Float.parseFloat(ua.browserVersion.majorVersion);
}
context.ieVersion = version