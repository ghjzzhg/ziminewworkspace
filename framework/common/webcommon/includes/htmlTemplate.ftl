<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#include "component://widget/templates/htmlFormMacroLibrary.ftl"/>

<#macro lookupField title="" className="" alert="" name="" value="" size="20" maxlength="20" id="" event="" action="" readonly="" autocomplete="" descriptionFieldName="" formName="" fieldFormName="" targetParameterIter="" imgSrc="" ajaxUrl="" ajaxEnabled="" presentation="layer" width="" height="" position="" fadeBackground="true" clearText="" showDescription="" initiallyCollapsed="">
    <#if (!ajaxEnabled?has_content)>
        <#assign javascriptEnabled = Static["org.ofbiz.base.util.UtilHttp"].isJavaScriptEnabled(request) />
        <#if (javascriptEnabled)>
            <#local ajaxEnabled = true>
        </#if>
    </#if>
    <#if (!id?has_content)>
        <#local id = Static["org.ofbiz.base.util.UtilHttp"].getNextUniqueId(request) />
    </#if>
    <#if "true" == readonly>
        <#local readonly = true/>
    <#else>
        <#local readonly = false />
    </#if>
    <#if userPreferences.VISUAL_THEME == "BIZZNESS_TIME">
        <#local position = "center" />
    </#if>
    <@renderLookupField name formName fieldFormName className alert value size maxlength id event action readonly autocomplete descriptionFieldName targetParameterIter imgSrc ajaxUrl ajaxEnabled presentation width height position fadeBackground clearText showDescription initiallyCollapsed "main" title/>
</#macro>

<#macro nextPrev commonUrl="" ajaxEnabled=false javaScriptEnabled=false paginateStyle="nav-pager" paginateFirstStyle="nav-first" viewIndex=0 highIndex=0 listSize=0 viewSize=1 ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying="" paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" >
    <#local javaScriptEnabled = javaScriptEnabled />
    <#if (!javaScriptEnabled)>
        <#local javaScriptEnabled = Static["org.ofbiz.base.util.UtilHttp"].isJavaScriptEnabled(request) />
    </#if>
    <#if (commonUrl?has_content)>
        <#if (!firstUrl?has_content)>
            <#local firstUrl=commonUrl+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexFirst}"/>
        </#if>
        <#if (!previousUrl?has_content)>
             <#local previousUrl=commonUrl+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexPrevious}"/>
        </#if>
        <#if (!nextUrl?has_content)>
            <#local nextUrl=commonUrl+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexNext}"/>
        </#if>
        <#if (!lastUrl?has_content)>
            <#local lastUrl=commonUrl+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexLast}"/>
        </#if>
        <#if (!selectUrl?has_content)>
            <#local selectUrl=commonUrl+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX="/>
        </#if>
        <#if (!selectSizeUrl?has_content)>
            <#local selectSizeUrl=commonUrl+"VIEW_SIZE='+this.value+'&amp;VIEW_INDEX=0"/>
        </#if>
    </#if>
    <@renderNextPrev paginateStyle paginateFirstStyle viewIndex highIndex listSize viewSize ajaxEnabled javaScriptEnabled ajaxFirstUrl firstUrl uiLabelMap.PaginationFirst paginatePreviousStyle ajaxPreviousUrl previousUrl uiLabelMap.PaginationPrevious uiLabelMap.CommonPage ajaxSelectUrl selectUrl ajaxSelectSizeUrl selectSizeUrl commonDisplaying paginateNextStyle ajaxNextUrl nextUrl uiLabelMap.PaginationNext paginateLastStyle ajaxLastUrl lastUrl uiLabelMap.PaginationLast uiLabelMap.CommonItemsPerPage/>
</#macro>

<#macro nextPrevAjax  targetId commonUrl="" param="" ajaxEnabled=true javaScriptEnabled=true paginateStyle="nav-pager" paginateFirstStyle="nav-first" viewIndex=0 highIndex=0 listSize=0 viewSize=1 ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying="" paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" >
    <#local javaScriptEnabled = javaScriptEnabled />
    <#if (!javaScriptEnabled)>
        <#local javaScriptEnabled = Static["org.ofbiz.base.util.UtilHttp"].isJavaScriptEnabled(request) />
    </#if>
    <#if (commonUrl?has_content)>
        <#local targetParam = param/>
        <#if targetParam?has_content>
            <#local targetParam = targetParam + "&amp;"/>
        </#if>
        <#if (!ajaxFirstUrl?has_content)>
            <#local ajaxFirstUrl=targetId + "," + commonUrl + "," + targetParam +"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexFirst}"/>
        </#if>
        <#if (!ajaxPreviousUrl?has_content)>
             <#local ajaxPreviousUrl=targetId + "," + commonUrl + "," + targetParam+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexPrevious}"/>
        </#if>
        <#if (!ajaxNextUrl?has_content)>
            <#local ajaxNextUrl=targetId + "," + commonUrl + "," + targetParam+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexNext}"/>
        </#if>
        <#if (!ajaxLastUrl?has_content)>
            <#local ajaxLastUrl=targetId + "," + commonUrl + "," + targetParam+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndexLast}"/>
        </#if>
        <#if (!ajaxSelectUrl?has_content)>
            <#local ajaxSelectUrl=targetId + "," + commonUrl + "," + targetParam+"VIEW_SIZE=${viewSize}&amp;VIEW_INDEX="/>
        </#if>
        <#if (!ajaxSelectSizeUrl?has_content)>
            <#local ajaxSelectSizeUrl=targetId + "," + commonUrl + "," + targetParam+"VIEW_SIZE='+this.value+'&amp;VIEW_INDEX=0"/>
        </#if>
    </#if>
    <@renderNextPrev paginateStyle paginateFirstStyle viewIndex highIndex listSize viewSize ajaxEnabled javaScriptEnabled ajaxFirstUrl firstUrl uiLabelMap.PaginationFirst paginatePreviousStyle ajaxPreviousUrl previousUrl uiLabelMap.PaginationPrevious uiLabelMap.CommonPage ajaxSelectUrl selectUrl ajaxSelectSizeUrl selectSizeUrl commonDisplaying paginateNextStyle ajaxNextUrl nextUrl uiLabelMap.PaginationNext paginateLastStyle ajaxLastUrl lastUrl uiLabelMap.PaginationLast uiLabelMap.CommonItemsPerPage/>
</#macro>
<#macro sortFieldAjax targetId currentSort sortField headerName="" commonUrl="" param="">
    <#local targetParam = param/>
    <#if targetParam?has_content>
        <#local targetParam = targetParam + "&amp;"/>
    </#if>
    <#local sortClass="sort-order"/>
    <#local sorted=false/>
    <#if currentSort?has_content && (currentSort==sortField || currentSort == "-" + sortField)>
        <#local sorted=true/>
    </#if>
    <#if sorted>
        <#if currentSort?starts_with("-")>
            <#local sortField = currentSort[1..]/>
            <#local sortClass="sort-order-desc"/>
        <#else>
            <#local sortField = "-" + currentSort/>
            <#local sortClass="sort-order-asc"/>
        </#if>
    </#if>
    <#local sortUrl=targetId + "," + commonUrl + "," + targetParam + "sortField=" + sortField/>
    <a class="${sortClass}" href="javascript:ajaxUpdateAreas('${sortUrl}')">${headerName}</a>
</#macro>
