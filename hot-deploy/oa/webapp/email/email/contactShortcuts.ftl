<script type="text/javascript">
    $(function(){
        YUI({
            base:'/images/lib/yui/'
        }).use('tabview', function (Y) {
            var tabview = new Y.TabView({srcNode:'#contactShortcuts'});
            tabview.render();
        });
    });
</script>
<div id="contactShortcuts">
    <ul>
        <li><a href="#recentlyContacts">最近</a></li>
        <li><a href="#preferedContacts">常用</a></li>
    </ul>
    <div>
        <div id="recentlyContacts">
            <#if (recentlyContacts?has_content)>
                <#list recentlyContacts as contact>
                    <div><a href="#">${contact.name}</a></div>
                </#list>
            </#if>
        </div>
        <div id="preferedContacts">
        <#if (preferedContacts?has_content)>
            <#list preferedContacts as contact>
                <div><a href="#">${contact.name}</a></div>
            </#list>
        </#if>
        </div>
    </div>
</div>