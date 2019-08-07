<#if partyId?has_content>
    <#assign staffId = partyId>
</#if>

<script type="text/javascript">
    var staff = $("#AddCheckingInForm").find("input[name='staff']").val('${staffId}');
</script>