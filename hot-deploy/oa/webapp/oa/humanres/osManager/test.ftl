<div id="findEmployee" class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.CommonFind} ${uiLabelMap.HumanResEmployee}</li>
            <li>
                <a href="">${uiLabelMap.CommonShowLookupFields}</a>
            </li>
                <li>
                    <a href="">${uiLabelMap.CommonHideFields}</a>
                </li>
            <li><a href="javascript:document.lookupparty.submit();" class="icos-plus"></a></li>
        </ul>
        <br class="clear"/>
    </div>

    <div class="screenlet-body">
        <form method="post" name="lookupparty" action="" class="basic-form">
            <table cellspacing="0">
                <tr>
                    <td class="label">aaa</td>
                    <td><input type="radio" name="extInfo" value="N" onclick="javascript:refreshInfo();"
                               <#if extInfo == "N">checked="checked"</#if>/>${uiLabelMap.CommonNone}&nbsp;
                        <input type="radio" name="extInfo" value="P" onclick="javascript:refreshInfo();"
                               <#if extInfo == "P">checked="checked"</#if>/>${uiLabelMap.PartyPostal}&nbsp;
                        <input type="radio" name="extInfo" value="T" onclick="javascript:refreshInfo();"
                               <#if extInfo == "T">checked="checked"</#if>/>${uiLabelMap.PartyTelecom}&nbsp;
                        <input type="radio" name="extInfo" value="O" onclick="javascript:refreshInfo();"
                               <#if extInfo == "O">checked="checked"</#if>/>${uiLabelMap.CommonOther}&nbsp;
                    </td>
                </tr>
                <tr>
                    <td class='label'>${uiLabelMap.PartyPartyId}</td>
                    <td width="20%">
                        <@htmlTemplate.lookupField value='${requestParameters.partyId!}' formName="lookupparty" name="partyId" id="partyId" fieldFormName="LookupPerson"/>
                    </td>
                    <td class="label">${uiLabelMap.PartyUserLogin}</td>
                    <td width="20%"><input type="text" name="userLoginId" value="${parameters.userLoginId!}"/></td>
                    <td class="label">${uiLabelMap.PartyLastName}</td>
                    <td><input type="text" name="lastName" value="${parameters.lastName!}"/></td>
                    <td class="label">${uiLabelMap.PartyFirstName}</td>
                    <td><input type="text" width="20%" name="firstName" value="${parameters.firstName!}"/></td>
                </tr>

                <tr>
                    <td class='label'>qq</td>
                    <td width="20%">
                    <@htmlTemplate.lookupField value='${requestParameters.partyId!}' formName="lookupparty" name="partyId" id="partyId" fieldFormName="LookupPerson"/>
                    </td>
                    <td class="label">${uiLabelMap.PartyUserLogin}</td>
                    <td width="20%"><input type="text" name="userLoginId" value="${parameters.userLoginId!}"/></td>
                    <td class="label">${uiLabelMap.PartyLastName}</td>
                    <td><input type="text" name="lastName" value="${parameters.lastName!}"/></td>
                    <td class="label">${uiLabelMap.PartyFirstName}</td>
                    <td><input type="text" width="20%" name="firstName" value="${parameters.firstName!}"/>
                        <button type="button" class="ui-datepicker-trigger"></button>
                    </td>
                </tr>

                <tr>

                </tr>
                <tr>
                    <td colspan="3">
                        <hr/>
                    </td>
                </tr>
                <tr align="left">
                    <td>&nbsp;</td>
                    <td><input type="submit" value="${uiLabelMap.PartyLookupParty}"
                               onclick="javascript:document.lookupparty.submit();"/>
                        <a href="<@ofbizUrl>findEmployees?roleTypeId=EMPLOYEE&amp;hideFields=Y&amp;lookupFlag=Y</@ofbizUrl>"
                           class="smallSubmit">${uiLabelMap.CommonShowAllRecords}</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>



<span class="view-calendar">
    <input type="text" name="b_i18n" title="年-月-日 时:分:秒" size="25" maxlength="30" id="MineClassesSearchForm_b_i18n">
    <input type="text" name="b" style="height:1px;width:1px;border:none;background-color:transparent" title="年-月-日 时:分:秒" size="25" maxlength="30" id="MineClassesSearchForm_b" class="hasDatepicker">
    <button type="button" class="ui-datepicker-trigger"></button>
    <script type="text/javascript">
    if (Date.CultureInfo != undefined) {
        var initDate = "";
        if (initDate != "") {
            var dateFormat = Date.CultureInfo.formatPatterns.shortDate + " " + Date.CultureInfo.formatPatterns.longTime;
            if (initDate.indexOf('.') != -1) {
                initDate = initDate.substring(0, initDate.indexOf('.'));
            }
            jQuery("#MineClassesSearchForm_b").val(initDate);
            var ofbizTime = "yyyy-MM-dd HH:mm:ss";
            var dateObj = Date.parseExact(initDate, ofbizTime);
            var formatedObj = dateObj.toString(dateFormat);
            jQuery("#MineClassesSearchForm_b_i18n").val(formatedObj);
        }

        jQuery("#MineClassesSearchForm_b").change(function() {
            var ofbizTime = "yyyy-MM-dd HH:mm:ss";
            var newValue = ""
            if (this.value != "") {
                var dateObj = Date.parseExact(this.value, ofbizTime);
                var dateFormat = Date.CultureInfo.formatPatterns.shortDate + " " + Date.CultureInfo.formatPatterns.longTime;
                newValue = dateObj.toString(dateFormat);
            }
            jQuery("#MineClassesSearchForm_b_i18n").val(newValue);
        });
        jQuery("#MineClassesSearchForm_b_i18n").change(function() {
            var dateFormat = Date.CultureInfo.formatPatterns.shortDate + " " + Date.CultureInfo.formatPatterns.longTime,
                    newValue = "",
                    dateObj = Date.parseExact(this.value, dateFormat),
                    ofbizTime;
            if (this.value != "" && dateObj !== null) {
                ofbizTime = "yyyy-MM-dd HH:mm:ss";
                newValue = dateObj.toString(ofbizTime);
            }
            else { // invalid input
                jQuery("#MineClassesSearchForm_b_i18n").val("");
            }
            jQuery("#MineClassesSearchForm_b").val(newValue);
        });
    } else {
        jQuery("#MineClassesSearchForm_b").change(function() {
            jQuery("#MineClassesSearchForm_b_i18n").val(this.value);
        });
        jQuery("#MineClassesSearchForm_b_i18n").change(function() {
            jQuery("#MineClassesSearchForm_b").val(this.value);
        });
    }

    jQuery("#MineClassesSearchForm_b").datetimepicker({
        showSecond: true,
        timeFormat: 'HH:mm:ss',
        stepHour: 1,
        stepMinute: 1,
        stepSecond: 1,
        showOn: 'button',
        buttonImage: '',
        buttonText: '',
        buttonImageOnly: false,
        dateFormat: 'yy-mm-dd'
    })

    ;
</script>
    <input type="hidden" name="" value="Timestamp">
  </span>