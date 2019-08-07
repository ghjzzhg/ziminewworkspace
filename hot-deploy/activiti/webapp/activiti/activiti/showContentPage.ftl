<#include "component://widget/templates/chooser.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#assign type = parameters.type>
<#assign name = parameters.name>
<#assign strTab = parameters.strTab>
<#if parameters.control?has_content && parameters.control == "chooser">
<@chooser chooserType="${type}" name="${name}" strTab="${strTab}" importability=true fp="fp_"/>
<#elseif  parameters.control?has_content && parameters.control == "date">
<@htmlTemplate.renderDateTimeField name="fp_${name}" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
value="" size="25" maxlength="20" id="fp_${name}" dateType="dateFmt:'yyyy-MM-dd'" shortDateInput=true timeDropdownParamName=""
defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
</#if>