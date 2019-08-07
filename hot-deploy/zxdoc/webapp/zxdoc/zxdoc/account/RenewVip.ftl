<script type="text/javascript">
     function chooseThis(id)
     {
        if(id==1)
        {
            $("#time1").attr("class","btn btn-success");
            $("#time2").attr("class","btn btn-info");
            $("#time3").attr("class","btn btn-info");
            $("#time4").attr("class","btn btn-info");
            $("#time5").attr("class","btn btn-info");
            $("#times").val(1);
        }else if(id==2){
            $("#time2").attr("class","btn btn-success");
            $("#time1").attr("class","btn btn-info");
            $("#time3").attr("class","btn btn-info");
            $("#time4").attr("class","btn btn-info");
            $("#time5").attr("class","btn btn-info");
            $("#times").val(3);
        }else if(id==3){
            $("#time3").attr("class","btn btn-success");
            $("#time1").attr("class","btn btn-info");
            $("#time2").attr("class","btn btn-info");
            $("#time4").attr("class","btn btn-info");
            $("#time5").attr("class","btn btn-info");
            $("#times").val(6);
        }else if(id==4){
            $("#time4").attr("class","btn btn-success");
            $("#time1").attr("class","btn btn-info");
            $("#time3").attr("class","btn btn-info");
            $("#time2").attr("class","btn btn-info");
            $("#time5").attr("class","btn btn-info");
            $("#times").val(12);
        }else if(id==5){
            $("#time5").attr("class","btn btn-success");
            $("#time1").attr("class","btn btn-info");
            $("#time3").attr("class","btn btn-info");
            $("#time4").attr("class","btn btn-info");
            $("#time2").attr("class","btn btn-info");
            $("#times").val(36);
        }
     }

     function renew() {
         var options = {
             beforeSubmit: function () {
                 return $("#accountForm").validationEngine("validate");
             },
             dataType: "json",
             success: function (data) {
                 showInfo("续费成功");
                 closeCurrentTab();
             },
             url: "renewAccount",
             type: 'post'
         };
         $("#accountForm").ajaxSubmit(options);
     }
</script>

<div class="portlet light">
    <div class="portlet-body">
        <form id="accountForm" name="accountForm">
            <div class="" style="">
                <input type="hidden" <#if parameters.partyId??>value="${parameters.partyId?default('')}"</#if>  name="partyId">
                <#if parameters.userLogins??>
                    <#list parameters.userLogins as userLogin>
                                <span class="label badge-success" contenteditable="true">${userLogin?default('')}</span>
                    </#list>
                </#if>
            </div>
            <div style="margin-top: 20px">
                <label class="control-label">续费时长<span class="required"> * </span></label>
                <div class="view">
                    <button class="btn btn-success" type="button" contenteditable="true" style="margin-top: 10px;" onclick="chooseThis(1)" id="time1">一个月</button>
                    <button class="btn btn-info" type="button" contenteditable="true" style="margin-top: 10px;" onclick="chooseThis(2)" id="time2">三个月</button>
                    <button class="btn btn-info" type="button" contenteditable="true" style="margin-top: 10px;" onclick="chooseThis(3)" id="time3">六个月</button>
                    <button class="btn btn-info" type="button" contenteditable="true" style="margin-top: 10px;" onclick="chooseThis(4)" id="time4">一年</button>
                    <button class="btn btn-info" type="button" contenteditable="true" style="margin-top: 10px;" onclick="chooseThis(5)" id="time5">三年</button>
                </div>
                <input type="hidden" value="1" id="times" name="times">
            </div>
            <div class="" style=" margin-top: 20px">
                <a href="javascript:renew();" class="btn green"> 续费 </a>
            </div>
        </form>
    </div>
</div>