<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<script type="text/javascript">
    function ztreeDataFilter(treeId, parentNode, responseData){
        return responseData.data;
    }
    function onSelectInternalContact(id, node){
        if (node.address) {
            emailAddresses.addTag(node.name + "<" + node.address + ">", {unique: true});
        }else{
            showError(node.name + "未配置电子邮箱");
        }
    }
    function onSelectExternalContact(id, node){
        if (node.address) {
            emailAddresses.addTag(node.name + "<" + node.address + ">", {unique: true});
        }else{
            showError(node.name + "未配置电子邮箱");
        }
    }
    function onSelectGroupContact(id, node){
        if (node.address) {
            emailAddresses.addTag(node.name + "<" + node.address + ">", {unique: true});
        }else{
            showError(node.name + "未配置电子邮箱");
        }
    }

    function confirmAddresses(){
        <#if targetId?has_content>
            var target = $("#${targetId}");
            target.importTags(emailAddresses.val());
        </#if>
        <#if lookupEmailCallback?has_content>
            ${lookupEmailCallback}(emailAddresses.val());
        </#if>
        closeCurrentTab();
    }
</script>
<style type="text/css">
    .emailAddress-tags .tagsinput input{
        display: none;
    }
</style>
<div>
    <div style="margin-bottom: 5px">
        <a href="#" class="smallSubmit" onclick="confirmAddresses()">确定</a>
        <a href="#" class="smallSubmit" onclick="closeCurrentTab()">取消</a>
    </div>
    <div style="margin-bottom: 5px" class="emailAddress-tags">
        <input type="text" style="width:100%" id="emailAddresses">
    </div>
    <div>
        <div id="contactGroups">
            <ul>
                <li><a href="#internalContacts">内部</a></li>
                <li><a href="#exteralContacts">外部</a></li>
                <li><a href="#groupContacts">通讯组</a></li>
            </ul>
            <div>
                <div id="internalContacts">
                <@treeInline id="internalContactsTree" url="GetInternalContact" asyncUrl="GetInternalContact" dataFilter="ztreeDataFilter" onselect="onSelectInternalContact" defaultExpand=false edit=false/>
                </div>
                <div id="exteralContacts">
                <@treeInline id="externalContactsTree" url="GetExternalContact" asyncUrl="GetExternalContact" dataFilter="ztreeDataFilter" onselect="onSelectExternalContact" defaultExpand=false edit=false/>
                </div>
                <div id="groupContacts">
                <@treeInline id="groupContactsTree" url="GetGroupContact" asyncUrl="GetGroupContact" dataFilter="ztreeDataFilter" onselect="onSelectGroupContact" defaultExpand=false edit=false/>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var emailAddresses;
    $(function(){

        $.treeInline['internalContactsTree'].initTreeInline();
        $.treeInline['externalContactsTree'].initTreeInline();
        $.treeInline['groupContactsTree'].initTreeInline();
        YUI({
            base:'/images/lib/yui/'
        }).use('tabview', function (Y) {
            var tabview = new Y.TabView({srcNode:'#contactGroups'});
            tabview.render();
        });
        emailAddresses = $("#emailAddresses");
        emailAddresses.tagsInput({'width': '100%',
            'height': 'auto',
            'defaultText':'',
            delimiter: ';'});

    <#if targetId?has_content>
        emailAddresses.importTags($("#${targetId}").val());
    </#if>
    });
</script>
