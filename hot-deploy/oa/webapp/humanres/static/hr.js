
$.organization = {
    addSubOrg: function(parentId){
        displayInTab3("SubOrganizationTab", "下级组织", {requestUrl: "CreateSubOrganization", data:{parentPartyId: parentId}, width: "500px"});
        /*$.ajax({
         type: 'GET',
         url: "CreateSubOrganization?parentPartyId=" + parentId,
         async: true,
         dataType: 'html',
         success: function (content) {
         displayInTab("SubOrganizationTab", "下级组织", content, {width:"500px"});
         }
         });*/
    },
    editOrg: function(parentId, partyId){
        displayInTab3("OrganizationTab", "组织信息", {
            requestUrl: "AjaxEditOrganization",
            width: "400px",
            height: 300,
            position: "center",
            data:{parentPartyId: parentId, partyId:partyId}
        });
       /* $.ajax({
            type: 'GET',
            url: "AjaxEditOrganization?partyId=" + partyId,
            async: true,
            dataType: 'html',
            success: function (content) {
                displayInTab3("OrganizationTab", "组织信息", content, {});
            }
        });*/
    },
    removeSubOrg: function(parentId, partyId){
        $.ajax({
            type: 'POST',
            url: "removeSubOrg",
            async: true,
            dataType: 'json',
            data:{partyIdFrom: parentId, partyId: partyId},
            success: function (content) {
                showInfo("成功解除上下级关系");
                $.organization.refreshSubOrgs({data:{parentPartyId: parentId}});
            }
        });
    },
    updateOrgName: function(partyId, name){
        $.ajax({
            type: 'POST',
            url: "updateOrgName",
            async: true,
            dataType: 'json',
            data:{partyId: partyId, name: name},
            success: function (content) {
                showInfo("更新成功");
                $.organization.refreshSubOrgs({data:{parentPartyId: partyId}});
            }
        });
    },
    saveOrganization: function(callback){
        var options = {
            beforeSubmit: function () {
                //return $("#EditOrganization").validationEngine("validate");
                return true;
            }, // pre-submit callback
            dataType: "json",
			//data: {manager:$('input[name='manager']').val()},
            success: function (data) {
                showInfo(data.data.msg);
                if(callback){
                    var callbackObj = typeof(callback) == 'function' ? callback : eval(callback);
                    if (typeof(callbackObj) == 'function') {
                        callbackObj.call(window, data);
                    }
                }
            },
            url: "saveOrganization", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#EditOrganization").ajaxSubmit(options);
    },
    saveOrganization1: function(callback){
        var options = {
            beforeSubmit: function () {
                return $("#EditOrganization1").validationEngine("validate");
            },
			data: {manager:$("input[name='managerForEdit']").val()},
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                if(callback){
                    var callbackObj = typeof(callback) == 'function' ? callback : eval(callback);
                    if (typeof(callbackObj) == 'function') {
                        callbackObj.call(window, data);
                    }
                }
            },
            url: "saveOrganization",
            type: 'post'
        };
        $("#EditOrganization1").ajaxSubmit(options);
    },
    saveSubOrganization: function(callback){
        var options = {
            beforeSubmit: function () {
                //return $("#EditOrganization").validationEngine("validate");
                return true;
            }, // pre-submit callback
            dataType: "json",
			data: {manager:$("input[name='managerForSub']").val()},
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                if(callback){
                    var callbackObj = typeof(callback) == 'function' ? callback : eval(callback);
                    if (typeof(callbackObj) == 'function') {
                        callbackObj.call(window, data);
                    }
                }
            },

            url: "saveOrganization", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#EditSubOrganization").ajaxSubmit(options);
    },
    refreshSubOrgs: function(param){
        //刷新组织树
        var orgTree = $.treeInline['category'];
        if(orgTree){
            var selectedNodes = orgTree.tree.getSelectedNodes()[0];
            orgTree.initTreeInline();
        }

        $.ajax({
            type: 'GET',
            url: "ListSubOrgs?partyId=" + param.data.parentPartyId,
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#ListSubOrgs").html(content);
            }
        });
    },
    editTitles: function(){
        displayInTab3("EditTitlesTab", "职级管理", {requestUrl: "EditTitles" ,width:700});
    },
    refreshOccupationChart: function(){
        displayInTab3("EditTitlesTab", "职级管理", {requestUrl: "EditTitles" ,width:700});
    },
    refreshOccupationDetail: function(partyId){
        $.ajax({
            type: 'GET',
            url: "GetOccupationDetail?partyId=" + partyId,
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#occupationInfo").html(content);
            }
        });
    },
    listLowerOccupations: function(positionId){
        $.ajax({
            type: 'GET',
            url: "ListLowerOccupations?positionId=" + positionId,
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#lowerOccupationsContainer").html(content);
            }
        });
    },
    listOccupationMembers: function(positionId){
        $.ajax({
            type: 'GET',
            url: "ListOccupationMembers?positionId=" + positionId,
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#occupationMembersContainer").html(content);
            }
        });
    },
    addOccupation: function(){
        var options = {
            beforeSubmit: function () {
                return $("#AddOccupation").validationEngine("validate");
                //return true;
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                refreshOccupationChart(data.data.partyId);
            },

            url: "addOccupation", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#AddOccupation").ajaxSubmit(options);
    },
    createOccupation: function(partyId){
        displayInTab3("CreateOccupationTab", "新增岗位", {requestUrl: "AddOccupation" ,width:700, data: {partyId: partyId}});
    },
    editOccupation: function(positionId){
        displayInTab3("EditOccupationTab", "修改岗位信息", {requestUrl: "EditOccupation" ,width:700, data: {positionId: positionId}});
    },
    saveOccupation: function(){
        var options = {
            beforeSubmit: function () {
                //return $("#EditOrganization").validationEngine("validate");
                return true;
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                refreshOccupationChart(data.data.partyId);
            },

            url: "editOccupation", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#EditOccupation").ajaxSubmit(options);
    },
    selectOccupationMaster: function(){
        displayInTab3("LookupOccupationTab", "职级管理", {requestUrl: "EditTitles" ,width:700, data: {onSelectOccupation: '$.organization.onSelectOccupation'}});
    },
    onSelectOccupation: function(data){

    },
    addOccupationMember: function(positionId){
        displayInTab3("AddOccupationMemberTab", "添加岗位成员", {requestUrl: "AddOccupationMember" ,width:700, data: {positionId: positionId}});
    },
    saveOccupationMember: function(){
        var options = {
            beforeSubmit: function () {
                //return $("#EditOrganization").validationEngine("validate");
                return true;
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                refreshOccupationChart(data.data.partyId);
            },

            url: "saveOccupationMember", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#AddOccupationMember").ajaxSubmit(options);
    },
    updateEnumSeq: function(value){
        $("#AddTitle input[name=sequenceId]").val(value);
    },
    deleteTitle: function(id){
        var options = {
            beforeSubmit: function () {
                //return $("#EditOrganization").validationEngine("validate");
                return true;
            }, // pre-submit callback
            dataType: "html",
            success: function (content) {
                $("#EditTitlesWrapper").replaceWith(content);
            },
            data:{enumId: id},
            url: "deleteTitle", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#AddTitle").ajaxSubmit(options);
    }

};
$.recordManagement = {
    viewDepartureEmployeeInformation: function(partyId){
        displayInTab3("viewDepartureEmployeeInformationTab", "查看离职员工信息", {requestUrl: "ViewEmployeeRecord" ,data:{partyId:partyId},width:800});
    },
    createFamilySituation:function(partyId){
        displayInTab3("FamilySituationTab", "添加家庭状况", {requestUrl: "createFamilySituation",data:{partyId:partyId}, width: "800px"});
    },
    createEducationSituation:function(partyId){
        displayInTab3("EducationSituationTab", "添加教育经历", {requestUrl: "EducationSituationCreate",data:{partyId:partyId}, width: "800px"});
    },
    editEducationSituation:function(partyId,educationId){
        displayInTab3("EducationSituationTab", "修改教育经历", {requestUrl: "EducationSituationCreate",data:{partyId:partyId,educationId:educationId}, width: "800px"});
    },
    createWorkExperience:function(partyId){
        displayInTab3("createWorkExperience", "添加工作经历", {requestUrl: "WorkSituationCreate",data:{partyId:partyId}, width: "800px"});
    },
    editWorkExperience:function(partyId,workId){
        displayInTab3("editWorkExperience", "修改工作经历", {requestUrl: "WorkSituationCreate",data:{partyId:partyId,workId:workId},width: "800px"});
    },
    editPostChange:function(partyId,postId,url){
        displayInTab3("createPostChange", "修改调岗记录", {requestUrl: "TransactionRecordCreate",data:{partyId:partyId,postId:postId,url:url}, width: "800px"});
    },
    createPostChange:function(partyId,url){
        displayInTab3("createPostChange", "添加调岗记录", {requestUrl: "TransactionRecordCreate",data:{partyId:partyId,url:url}, width: "800px"});
    },
    editDeparture:function(partyId,isEdit,type){
        displayInTab3("editDepartureTab", "修改离职记录", {requestUrl: "editDeparture",data:{partyId:partyId,isEdit:isEdit,type:type},width: "800px"});
    },
    editDepartures:function(departureId){
        displayInTab3("editDepartureTab", "修改离职记录", {requestUrl: "editDepartures",data:{departureId:departureId},width: "800px"});
    },
    createDeparture:function(){
        displayInTab3("editDepartureTab", "添加离职记录", {requestUrl: "editDeparture",width: "800px"});
    },
    ContractRecordCreate:function(partyId,url,type){
        displayInTab3("ContractRecordCreateTab", "添加合同", {requestUrl: "ContractRecordCreate",data:{partyId:partyId,url:url,type:type}, width: "800px"});
    },
    editContract:function(contractId,url,type){
        displayInTab3("ContractRecordCreateTab", "修改合同", {requestUrl: "editContract",data:{contractId:contractId,url:url,type:type}, width: "800px"});
    },
    createReword:function(partyId,url){
        displayInTab3("RewardsAndPunishmentRecordCreateTab", "添加奖惩记录", {requestUrl: "RewardsAndPunishmentRecordCreate",data:{partyId:partyId,url:url}, width: "800px"});
    },
    editReword:function(rewordId,partyId,url){
        displayInTab3("RewardsAndPunishmentRecordCreateTab", "修改奖惩记录", {requestUrl: "RewardsAndPunishmentRecordCreate",data:{partyId:partyId,rewordId:rewordId,url:url}, width: "800px"});
    },
    changeTrainType:function(id,name){
        $("#RewardTrainForSearch_type").append('<option value="'+id+'">'+name+'</option>');
    },
    SalaryManagementCreate:function(partyId,id){
        var flag = false;
        $.ajax({
            type: 'POST',
            url: "FindContract",
            async: true,
            dataType: 'json',
            data:{partyId:partyId},
            success: function (data) {
                if(data.data=="1"){
                    var info = "";
                    if(null != id && '' != id){
                        info = "修改薪资记录"
                    }else{
                        info = "添加薪资记录";
                        id = "";
                    }
                    displayInTab3("SalaryManagementCreateTab", info, {requestUrl: "SalaryManagementCreate"  ,data:{partyId:partyId,salaryId:id},height:"500",width: "600px",position:"center"});
                }else{
                    showInfo("请创建员工的劳动合同后在增加薪资条目!");
                    return ;
                }
            }
        });
    },
    createTrain:function(partyId,url){
        displayInTab3("createTrain", "添加培训记录", {requestUrl: "TrainManagementCreate",data:{partyId:partyId,url:url}, width: "800px"});
    },
    editTrain:function(trainId,partyId,url){
        displayInTab3("createTrain", "修改培训记录", {requestUrl: "TrainManagementCreate",data:{partyId:partyId,trainId:trainId,url:url}, width: "800px"});
    },
    editFamilySituation:function(familyId,partyId){
        displayInTab3("FamilySituationTab", "修改家庭状况", {requestUrl: "editFamilySituation",data:{partyId: partyId,familyId:familyId}, width: "800px"});
    },
    CreateEmployee:function(partyId){
        displayInTab3("employeeTab", "添加员工", {requestUrl: "CreateEmployee",data:{partyId:partyId}, width: "800px"});
    },
    editEmployee:function(partyId){
        displayInTab3("employeeTab", "修改员工", {requestUrl: "CreateEmployee",data:{partyId:partyId},width: "800px"});
    },
    editEmployees:function(partyId){
        displayInTab3("employeeTab", "修改员工", {requestUrl: "CreateEmployees",data:{partyId:partyId},width: "800px"});
    },
    createUserLogin:function(partyId){
        displayInTab3("userLoginTab", "添加账户", {requestUrl: "HrCreateUserLogin",data:{partyId:partyId},width: "800px"});
    },
    editUserLogin:function(userLoginId){
        displayInTab3("userLoginTab", "编辑账户", {requestUrl: "HrEditUserLogin",data:{userLoginId:userLoginId},width: "800px"});
    },
    saveUserLogin:function(){
        var options = {
            beforeSubmit:function(){
                return $("#createUserLogin").validationEngine("validate");
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab();
                $.recordManagement.searchEmployee();
            },
            url:"hrCreateUserLogin",
            type:"POST"
        };
        $("#createUserLogin").ajaxSubmit(options);
    },
    updateUserLogin:function(){
        var options = {
            beforeSubmit:function(){
                return $("#editUserPassword").validationEngine("validate");
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab()
            },
            url:"hrUpdateUserLogin",
            type:"POST"
        };
        $("#editUserPassword").ajaxSubmit(options);
    },
    RemoveEmployee:function(partyId,fullName){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removeEmployee",
            async: false,
            dataType: 'json',
            data:{partyId:partyId,fullName:fullName},
            success: function (data) {
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "ListEmployeeRecord",
                    async: false,
                    dataType: 'html',
                    success: function (content) {
                        document.getElementById('screenlet_2_col').innerHTML = " ";
                        $("#screenlet_2_col").html(content);
                    }
                });
            }
        });
        return true;
    },
    searchPostChange:function(){
        var options = {
            beforeSubmit:function(){
                return true;
            },
            async:false,
            data:{isSearch:"true"},
            dataType:"html",
            success:function(content){
              $("#staffInformation_col").html(content);
            },
            url:"searchPostChange",
            type:"POST"
        };
        $("#TransferRecordForSearch").ajaxSubmit(options);
    },
    compareDate:function(){
        var flag = true;
        var Str="";
        var startDate=$('input[name="startDate"]').val()
        var endDate=$('input[name="endDate"]').val()
        if(startDate!=""&&endDate!=""){
            var arr= startDate.split("-");
            var startTime=new Date(arr[0],arr[1]-1,arr[2]);
            var startDates=startTime.getTime();
            var arr1= endDate.split("-");
            var endTime=new Date(arr1[0],arr1[1]-1,arr1[2]);
            var endDates=endTime.getTime();
            if(startDates>=endDates){
                Str="结束时间必须在开始时间之后！";
                flag = false;
            }else{
                flag = true;
            }
        }
        if(!flag){
            showInfo(Str);
            return false;
        }
        return true;
    },
    searchDeparture:function(){
        var flag = $.recordManagement.compareDate();
        if(flag){
            var options = {
                beforeSubmit:function(){
                    return true;
                },
                async:false,
                data:{isSearch:"true"},
                dataType:"html",
                success:function(content){
                    $("#staffDepartureInformation").html(content);
                },
                url:"ListDepartureManagement",
                type:"POST"
            };
            $("#DepartureManagementForSearch").ajaxSubmit(options);
        }
    },
    searchContract:function(){
        var flag = $.recordManagement.compareDate();
        if(flag){
            var options = {
                beforeSubmit:function(){
                    return true;
                },
                async: true,
                data:{isSearch:"true"},
                dataType:"html",
                success:function(content){
                    $("#staffInformation1").html(content);
                },
                url:"searchContract",
                type:"POST"
            };
            $("#ContractForEmployeeForSearch1").ajaxSubmit(options);
        }
    },
    searchReword:function(){
        var flag = $.recordManagement.compareDate();
        if(flag){
            var options = {
                beforeSubmit:function(){
                    return true;
                },
                async:false,
                data:{isSearch:"true"},
                dataType:"html",
                success:function(content){
                    $("#staffInformation").html(content);
                },
                url:"rewordList",
                type:"POST"
            };
            $("#RewardAndPunishmentForSearch").ajaxSubmit(options);
        }
    },
    searchTrain:function(){
        var flag = $.recordManagement.compareDate();
        if(flag) {
            var options = {
                beforeSubmit: function () {
                    return true;
                },
                async: false,
                data: {isSearch: "true"},
                dataType: "html",
                success: function (content) {
                    $("#staffInformation").html(content);
                },
                url: "trainList",
                type: "POST"
            };
            $("#RewardTrainForSearch").ajaxSubmit(options);
        }
    },
    searchEmployee:function(){
        var flag = $.recordManagement.compareDate();
        if(flag){
            var options = {
                beforeSubmit:function(){
                    return true;
                },
                async:false,
                data:{isSearch:"true"},
                dataType:"html",
                success:function(content){
                    $("#screenlet_3_col").html(content);
                },
                url:"ListEmployeeRecord",
                type:"POST"
            };
            $("#ViewEmployeeRecordForSearch").ajaxSubmit(options);
        }
    },
    saveFamilySituation:function(){
      var options = {
          beforeSubmit:function(){
              return $("#FamilySituationCreate").validationEngine("validate");
          },
          async:false,
          dataType:"json",
          success:function(data){
              closeCurrentTab()
              showInfo(data.data.msg);
              $.ajax({
                  type: 'GET',
                  url: "FamilySituationList",
                  data:{partyId:data.data.partyId},
                  async: true,
                  dataType: 'html',
                  success: function (content) {
                      $("#staffInformation_col").html(content);
                  }
              });
          },
          url:"saveFamilySituation",
          type:"POST"
      };
        $("#FamilySituationCreate").ajaxSubmit(options);
    },
    saveWorkExperience:function(){
        var options = {
            beforeSubmit:function(){
                return $('#WorkSituationCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab()
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "WorkExperienceList",
                    data:{partyId:data.data.partyId},
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#staffInformation_col").html(content);
                    }
                });
            },
            url:"saveWorkExperience",
            type:"POST"
        };
        $("#WorkSituationCreate").ajaxSubmit(options);
    },
    saveEducational:function(){
        var options = {
            beforeSubmit:function(){
                return $('#EducationSituationCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab()
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "EducationExperienceList",
                    data:{partyId:data.data.partyId},
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#staffInformation_col").html(content);
                    }
                });
            },
            url:"saveEducational",
            type:"POST"
        };
        $("#EducationSituationCreate").ajaxSubmit(options);
    },
    removeEducation:function(educationId,partyId){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removeEducation",
            async: false,
            dataType: 'json',
            data:{educationId:educationId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "EducationExperienceList",
                    data:{partyId:partyId},
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#staffInformation_col").html(content);
                    }
                });
            }
        });
        return true;
    },
    saveTrain:function(url){
        var options = {
            beforeSubmit:function(){
                return $('#TrainManagementCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab()
                showInfo(data.data.msg);
                var partyId = null;
                if(url=="TrainManagementList"){
                    partyId = data.data.partyId;
                    $.ajax({
                        type: 'GET',
                        url: url,
                        data:{partyId:partyId},
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            $("#staffInformation_col").html(content);
                        }
                    });
                }else{
                    $.recordManagement.searchTrain();
                }
            },
            url:"saveTrain",
            type:"POST"
        };
        $("#TrainManagementCreate").ajaxSubmit(options);
    },
    removeTrain:function(trainId,url,partyId){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removeTrain",
            async: false,
            dataType: 'json',
            data:{trainId:trainId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                if(url=="trainList"){
                    $.recordManagement.searchTrain();
                }else{
                    $.ajax({
                        type: 'GET',
                        url: url,
                        data:{partyId:partyId},
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            $("#staffInformation_col").html(content);
                        }
                    });
                }
            }
        });
        return true;
    },
    savePostChange:function(url){
        var options = {
            beforeSubmit:function(){
                return $('#TransactionRecordCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab()
                showInfo(data.data.msg);
                var partyId = null;
                if(url=="TransactionRecordList"){
                    partyId = data.data.partyId;
					$.recordManagement.searchPostChange(url,partyId);
                }else{
					$.recordManagement.searchPostChanges();
				}
            },
            url:"savePostChange",
            type:"POST"
        };
        $("#TransactionRecordCreate").ajaxSubmit(options);
    },
    removePostChange:function(partyId,postId,url){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removePostChange",
            async: false,
            dataType: 'json',
            data:{postId:postId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                var partyId = data.data.partyId;
                $.recordManagement.searchPostChange(url,partyId);
            }
        });
        return true;
    },
    searchPostChange:function(url,partyId){
        $.ajax({
            type: 'GET',
            url: url,
            data:{partyId:partyId},
            async: true,
            dataType: 'html',
            success: function (content) {
				if(url=="TransactionRecordList"){
					$("#staffInformation_col").html(content);
				}else{
					$("#ListTransferRecord_col").html(content);
				}
            }
        });
    },
    searchPostChanges:function(){
        var flag = $.recordManagement.compareDate();
        if(flag){
            var options = {
                beforeSubmit:function(){
                    return true;
                },
                async:false,
                dataType:"html",
                success:function(content){
                    $("#ListTransferRecord_col").html(content);
                },
                url:"postChangeList",
                type:"POST"
            };
            $("#TransferRecordForSearch").ajaxSubmit(options);
        }
    },
    saveContract:function(url,type){
        var options = {
            beforeSubmit:function(){
                return $('#ContractRecordCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                if(data.data.message=="DEPARTURE"){
                    showError("该员工已经离职，只能创建劳动合同");
                } else {
                    closeCurrentTab()
                    showInfo(data.data.message);
                    if("1" == type){
                        $.recordManagement.searchContract();
                    }else{
                        $.recordManagement.employeeRecordSituation('ContractRecordList',data.data.partyId);
                    }
                }
            },
            url:"saveContract",
            type:"POST"
        };
        $("#ContractRecordCreate").ajaxSubmit(options);
    },
    removeContract:function(contractId,partyId){
        if (!confirm("确定作废该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "cancelContract",
            async: false,
            dataType: 'json',
            data:{contractId:contractId},
            success: function (content) {
                showInfo("作废成功");
                $.recordManagement.employeeRecordSituation('ContractRecordList',partyId);
            }
        });
        return true;
    },
    cancelContract:function(contractId,url){
        if (confirm("确定作废该项吗?")) {
			b=document.getElementById('#cancel');
			b.disabled=true;
			$.ajax({
				type: 'POST',
				url: "cancelContract",
				async: false,
				dataType: 'json',
				data:{contractId:contractId},
				success: function (content) {
				    showInfo("作废成功");
					$.recordManagement.searchContract();
				}
			});
		}
    },
    saveDeparture:function(url){
        var options = {
            beforeSubmit:function(){
                return $('#DepartureRecordCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                if(data.data.msg == "DEPARTURE"){
                    showError("该员工已经离职，不能创建离职记录");
                } else {
                    closeCurrentTab()
                    showInfo(data.data.msg);
                    var partyId = null;
                    if(url=="DepartureManagement"){
                        partyId = data.data.partyId;
                        $.ajax({
                            type: 'GET',
                            url: url,
                            data:{partyId:partyId},
                            async: true,
                            dataType: 'html',
                            success: function (content) {
                                $("#staffInformation_col").html(content);
                            }
                        });
                    }
                    if(url == "ListDepartureManagement"){
                        $.recordManagement.searchDeparture();
                    }
                }
            },
            url:"saveDeparture",
            type:"POST"
        };
        $("#DepartureRecordCreate").ajaxSubmit(options);
    },
    removeDeparture:function(partyId,departureId){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removeDeparture",
            async: false,
            dataType: 'json',
            data:{departureId:departureId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "DepartureManagement",
                    data:{partyId:data.data.partyId},
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#staffInformation_col").html(content);
                    }
                });
            }
        });
        return true;
    },
    saveReword:function(url){
        var options = {
            beforeSubmit:function(){
                return $('#RewardsAndPunishmentRecordCreate').validationEngine('validate');
            },
            async:false,
            dataType:"json",
            success:function(data){
                closeCurrentTab()
                showInfo(data.data.msg);
                var partyId = null;
                if(url=="RewardsAndPunishmentRecordList"){
                    partyId = data.data.partyId;
                    $.ajax({
                        type: 'GET',
                        url: url,
                        data:{partyId:partyId},
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            $("#staffInformation_col").html(content);
                        }
                    });
                }else{
                    $.recordManagement.searchReword();
                }
            },
            url:"saveReword",
            type:"POST"
        };
        $("#RewardsAndPunishmentRecordCreate").ajaxSubmit(options);
    },
    removeReword:function(rewordId,partyId,url){
        if (!confirm("确定删除该项吗?")) {
            return false;
        }
        $.ajax({
            type: 'POST',
            url: "removeReword",
            async: false,
            dataType: 'json',
            data:{rewordId:rewordId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                if(url=="RewardsAndPunishmentRecordList") {
                    var partyId = data.data.partyId;
                    $.ajax({
                        type: 'GET',
                        url: url,
                        data: {partyId: partyId},
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            $("#staffInformation_col").html(content);
                        }
                    });
                }else{
                    $.recordManagement.searchReword();
                }
            }
        });
        return true;
    },
    removeWorkExperience:function(partyId,workId){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removeWorkExperience",
            async: false,
            dataType: 'json',
            data:{workId:workId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "WorkExperienceList",
                    data:{partyId:data.data.partyId},
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#staffInformation_col").html(content);
                    }
                });
            }
        });
        return true;
    },
    removeFamilySituation:function(familyId,partyId){
        if (!confirm("确定删除该项吗?")) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: "removeFamilySituation",
            async: false,
            dataType: 'json',
            data:{familyId:familyId,partyId:partyId},
            success: function (data) {
                showInfo(data.data.msg);
                $.ajax({
                    type: 'GET',
                    url: "FamilySituationList",
                    data:{partyId:data.data.partyId},
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#staffInformation_col").html(content);
                    }
                });
            }
        });
        return true;
    },
    saveSalary:function(){
        var urlValue = "";
        var startTime = $.trim($("#startTime").val());
        var inputId = $.trim($("#loginId").val());
        var salaryText = $.trim($("#salaryText").val());
        var adjustmentTime = $.trim($("#adjustmentTime").val());
        var party = $.trim($("#partyId").val());
        var falg = true;
        var ckfalg = true;
        $('input[name="subBox"]').each(function () {
            var allid = $(this).attr("id");
            var id = allid.substr(allid.indexOf("_") + 1, allid.length);
            var valueId =$("#amout_"+id).val();
            var status = "";
            if($("#"+allid).attr("checked")=="checked"){
                ckfalg = false;
                if('' == valueId){
                    falg = false;
                }
                status = "1";
            }else{
                status = "0";
            }
            urlValue = urlValue + id + ":" + valueId + "-" + status + ",";
        });
        if(ckfalg){
            showInfo("请至少勾选一个薪资条目！");
            return;
        }
        if(!falg){
            showInfo("选中项请输入薪资！");
            return;
        }
        urlValue = urlValue.substr(0,urlValue.length - 1);
        var salaryId = $("#salaryId").val();
        if('' == startTime){
            showInfo("请选择生效日期");
            return;
        }else if('' == adjustmentTime){
            showInfo("请选择调整日期");
            return;
        }
        /*if('' != startTime && '' != endTime){
            var d1 = new Date(startTime.replace(/\-/g, "\/"));
            var d2 = new Date(endTime.replace(/\-/g, "\/"));
            if(d1 >= d2)
            {
                showInfo("开始时间不能大于等于结束时间！");
                return;
            }
        }*/
        $.ajax({
            type: 'POST',
            url: "saveSalary",
            async: false,
            dataType: 'json',
            data:{salaryId:salaryId,urlValue:urlValue,startTime:startTime,inputId:inputId,remarks:salaryText,adjustmentTime:adjustmentTime,partyId:party},
            success: function (data) {
                showInfo(data.data.message);
                $.recordManagement.employeeRecordSituation('SalaryManagementList',party);
                closeCurrentTab2();
            }
        });
    },
    removeSalaryInfo:function(id,partyId){
        if(confirm("是否确定删除？")){
            $.ajax({
                type: 'POST',
                url: "removeSalaryById",
                async: false,
                dataType: 'json',
                data:{salaryId:id},
                success: function (data) {
					showInfo(data.data.message);
                    $.recordManagement.employeeRecordSituation('SalaryManagementList',partyId);
                }
            });
        }
    },
    saveEmployee:function(){
		if($('#CreateEmployee').validationEngine('validate')){
			var num = $("#CreateEmployee_cardId").val();
            var partyId = $("#CreateEmployee_partyId").val();
            if(num != ''){
                $.ajax({
                    type: 'post',
                    url: "searchCardId",
                    data:{num:num,partyId:partyId},
                    async: true,
                    dataType: 'json',
                    success: function (data) {
                         if(data.flag != '2'){
                             showInfo("身份证号已被使用，请重新填写！");
                         }else{
                             var options = {
                                beforeSubmit: function () {
                                    return true;
                                }, // pre-submit callback
                                async: false,
                                dataType: "json",
                                success: function (data) {
                                    closeCurrentTab();
                                    showInfo(data.data.msg);
                                    var employeeSaveCallback = window['afterSaveEmployee'];
                                    if(employeeSaveCallback){
                                        employeeSaveCallback.apply(window, [partyId]);
                                    }else{
                                        $.ajax({
                                            type: 'GET',
                                            url: "ListEmployeeRecord",
                                            async: false,
                                            dataType: 'html',
                                            success: function (content) {
                                                $("#screenlet_3_col").html(content);
                                            }
                                        });
                                    }
                                },

                                url: "saveEmployee", // override for form's 'action' attribute
                                type: 'post'        // 'get' or 'post', override for form's 'method' attribute
                            };
                            $("#CreateEmployee").ajaxSubmit(options);
                         }
                    }
                });
            }
		}
    },
    saveEmployees:function(){
        var Id = $("#departmentId").val();
		if($('#CreateEmployee').validationEngine('validate')){
			var num = $("#CreateEmployee_cardId").val();
            var partyId = $("input[name='partyId']").val();
                if(num != ''){
                    $.ajax({
                        type: 'post',
                        url: "searchCardId",
                        data:{num:num,partyId:partyId},
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                         if(data.flag != '2'){
							 showInfo("身份证号已被使用，请重新填写！");
						 }else{
							 var options = {
							beforeSubmit: function () {
								return true;
							}, // pre-submit callback
							async: false,
							dataType: "json",
							success: function (data) {
								closeCurrentTab();
								showInfo(data.data.msg);
								$.ajax({
									type: 'post',
									url: "ListRecordForDepartment",
									data:{partyId:Id},
									async: true,
									dataType: 'html',
									success: function (content) {
										$("#ListSubOrgs").html(content);
									}
								});
								$.ajax({
									type: 'GET',
									url: "EmployeeDetail",
									data:{partyId:data.data.partyId},
									async: true,
									dataType: 'html',
									success: function (content) {
										$("#staffInformation_col").html(content);
									}
								});
							},
							url: "saveEmployee", // override for form's 'action' attribute
							type: 'post'        // 'get' or 'post', override for form's 'method' attribute
						};
						$("#CreateEmployee").ajaxSubmit(options);
						 }   
                        }
                    });
                }
		}
    },
    ViewEmployee: function (partyId) {
        $.ajax({
            type: 'GET',
            url: "ViewEmployeeRecord",
            data:{partyId:partyId},
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#testCalendar").html(content);
            }
        });
    },
    employeeRecordSituation:function(url,partyId){
        $.ajax({
            type: 'post',
            url: url,
            data:{partyId:partyId},
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#staffInformation_col").html(content);
            }
        });
    },
    employeeRecordForMenu:function(url){
        $.ajax({
            type: 'GET',
            url: url,
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#column-container").html(content);
            }
        });
    },
    manageContractType: function () {
        displayInTab3("manageCategoryTab", "合同类型维护",{requestUrl: "EditEnums", data:{enumTypeId: "CONTRACT_TYPE"}, width: "600px"});
    }
}

$.perfExam = {
    addPerfExam: function(){
        displayInTab3("CreatePerfExamTab", "员工考评", {requestUrl: "CreatePerfExam", width: "800px"});
    },
    showPerfExam: function(examId,type){
        if(examId==""){
            showInfo("该员工还没有考核记录！");
            return false;
        }
        displayInTab3("CreatePerfExamTab",type, {requestUrl: "showPerfExam", width: "800px", data:{examId: examId}});
    },
    editPerfExam: function(examId,planId,type,evaluateYear,evaluateMonth){
        var type1;
        if(type=='PERF_EXAM_TYPE_1'){
            type1='考评';
        }
        if(type=='PERF_EXAM_TYPE_2'){
            type1='初审';
        }
        if(type=='PERF_EXAM_TYPE_3'){
            type1='终审';
        }
        if(type=='PERF_EXAM_TYPE_4'){
            type1='归档';
        }
        displayInTab3("CreatePerfExamTab",type1, {requestUrl: "CreatePerfExam", width: "800px", data:{examId: examId,planId:planId,type:type,evaluateYear:evaluateYear,evaluateMonth:evaluateMonth}});
    },
    changeEvaluateMonth:function(obj){
        var year = obj.value;
        $.ajax({
            type: 'POST',
            url: "changeEvaluateMonth",
            async: true,
            dataType: 'json',
            data:{year:year},
            success: function (data) {
                var value, optStr,list=data.data.list,str="";
                if(list.length>0){
                    for(var n=0;n<list.length;n++){
                        value=list[n].value;
                        optStr=list[n].label;
                        str+="<option value='"+value+"'>"+optStr+"</option>";
                        $("#PerfExamFindOptions_evaluateMonth").html(str);
                    }
                }else{
                    $("#PerfExamFindOptions_evaluateMonth").html(str);
                }
            }
        });
    },
    rejectPerfExam: function(id,state){
        $('textarea[name="remark"]').val(remark.html());
         if (!confirm("是否确定驳回，一旦驳回将不保存评分！")) {
            return;
         }
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "json",
            data:{state:state},
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.perfExam.searchPerfExam();
            },
            url: "createPerfExam",
            type: 'post',
        };
        $("#perfExamEditForm").ajaxSubmit(options);
    },
    archivePerfExam: function(examId,type){
        if (!confirm("确定归档?，一旦归档将不能修改删除")) {
            return;
        }
        $.ajax({
            type: 'post',
            url: "createPerfExam",
            async: true,
            dataType: 'json',
            data:{examId:examId,type:type},
            success: function (data) {
                showInfo(data.data.msg);
                $.perfExam.searchPerfExam();
            }
        });
    },
    savePerfExam: function(id,state){
        $('textarea[name="remark"]').val(remark.html());
        if($("#totalScore").val()==''){
            showInfo("请正确评分!")
            return false;
        }
        var options = {
            beforeSubmit: function () {
                return $("#perfExamEditForm").validationEngine("validate");
            },
            dataType: "json",
            data:{state:state},
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.perfExam.searchPerfExam();
            },

            url: "createPerfExam",
            type: 'post',
        };
        $("#perfExamEditForm").ajaxSubmit(options);
    },
    savePerfExams: function(id,state){
        $('textarea[name="previousAdvice"]').val(previousAdvice.html());
        $('textarea[name="nextAdvice"]').val(nextAdvice.html());
        if($("#totalScore").val()==''){
            showInfo("请正确评分!")
            return false;
        }
        if($('textarea[name="previousAdvice"]').val()==''){
            showInfo("前期工作计划及建设性建议必填!")
            return false;
        }
        if($('textarea[name="nextAdvice"]').val()==''){
            showInfo("下期工作计划及建设性建议必填!")
            return false;
        }
        var options = {
            beforeSubmit: function () {
                return $("#perfExamEditForm").validationEngine("validate");
            },
            dataType: "json",
            data:{state:state},
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.perfExam.searchPerfExam();
            },

            url: "createPerfExam",
            type: 'post',
        };
        $("#perfExamEditForm").ajaxSubmit(options);
    },
    searchPerfExam:function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (context) {
                $("#perfExamStats").html(context);
            },
            url: "searchPerfExam",
            type: 'post'
        };
        $("#PerfExamFindOptions").ajaxSubmit(options);
    },
    deletePerfExam: function(examId,type){
        if (!confirm("确定删除相应评分信息吗?")) {
            return;
        }
        $.ajax({
            type: 'post',
            url: "deletePerfExam",
            async: true,
            dataType: 'json',
            data:{examId:examId,type:type},
            success: function (data) {
                showInfo(data.msg);
                $.perfExam.searchPerfExam();
            }
        });
    },
    getPerfExamItems: function(type){
        if(type){
            $.ajax({
                type: 'POST',
                url: "GetPerfExamItems?type=" + type,
                async: true,
                dataType: 'html',
                success: function (content) {
                    $("#perfExamItemsContainer").html(content);
                    /*sumTotalValue();*/
                }
            });
        }else{
            $("#perfExamItemsContainer").empty();
        }
    },
    refreshPerfExamItem: function(){
        var selectedType = $.treeInline["perfExamItemTypes"].tree.getSelectedNodes();
        var nodeId = "";
        if(selectedType.length){
            nodeId = selectedType[0].id
        }
        onPerfExamItemType(nodeId);
    },
    addPerfExamItem: function(parentType, type){
        displayInTab3("EditPerfExamItemTab", "考评项目", {requestUrl: "EditPerfExamItem", data: {parentType: (parentType ? parentType : null), type : (type ? type : null)}, width: "800px"});
    },
    editPerfExamItem: function(id, parentType, type){
        displayInTab3("EditPerfExamItemTab", "考评项目", {requestUrl: "EditPerfExamItem", data: {id: id}, width: "800px"});
    },
    deletePerfExamItem: function(id){
        if(confirm("是否确认作废？")) {
            $.ajax({
                type: 'POST',
                url: "deletePerfExamItem",
                async: true,
                data:{itemId: id},
                dataType: 'json',
                success: function (data) {
                    $.perfExam.refreshPerfExamItem();
                    showInfo(data.data);
                }
            });
        }
    },
    savePerfExamItem: function(id){
        var options = {
            beforeSubmit: function () {
                return $("#EditPerfExamItem").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                $.perfExam.refreshPerfExamItem();
                closeCurrentTab();
            },

            url: (id ? "updatePerfExamItem" : "createPerfExamItem"),
            type: 'post'
        };
        $("#EditPerfExamItem").ajaxSubmit(options);
    },
    addPerfExamPerson: function(department){
        displayInTab3("EditPerfExamPersonTab", "考核计划", {requestUrl: "EditPerfExamPerson", data: {department: (department ? department : null)}, width: "800px"});
    },
    editPerfExamPerson: function(id){
        displayInTab3("EditPerfExamPersonTab", "考核计划", {requestUrl: "EditPerfExamPerson", data: {id: id}, width: "800px"});
    },
    deletePerfExamPerson: function(id){
        $.ajax({
            type: 'POST',
            url: "deletePerfExamPerson",
            async: true,
            data:{planId: id},
            dataType: 'json',
            success: function (data) {
                $.perfExam.refreshPerfExamPerson();
                showInfo(data.msg);
            }
        });
    },
    savePerfExamPerson: function(){
        var id =$("#CreatePerfExamPerson_planId").val();
        var options = {
            beforeSubmit: function () {
                return $("#CreatePerfExamPerson").validationEngine("validate");
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                $.perfExam.refreshPerfExamPerson();
                closeCurrentTab();
            },

            url: (id ? "updatePerfExamPerson" : "createPerfExamPerson"),
            type: 'post'
        };
        $("#CreatePerfExamPerson").ajaxSubmit(options);
    },
    refreshPerfExamPerson: function(){
        var selectedType = $.treeInline["category"].tree.getSelectedNodes();
        var nodeId = "";
        if(selectedType.length){
            nodeId = selectedType[0].id
        }
        onSelectOrg(nodeId);
    }
}

$.salary = {
    editPersonSalaryItems: function(id){
        displayInTab3("EditPersonSalaryItemsTab", "员工薪资", {requestUrl: "PersonSalaryItems", width: "800px"});
    },
    //submitPersonSalaryItems: function(sendId){
    //    displayInTab3("SubmitPersonSalaryItemsTab", "员工薪资发放", {requestUrl: "SubmitPersonSalary",data:{sendId:sendId}, width: "800px"});
    //},
    submitPersonSalaryItemsFor: function(partyId,year,month,sendId,status,paySalary){
        $.ajax({
            type:'POST',
            url:"SubmitPersonSalary",//跳转的action
            data:{partyId:partyId,year:year,month:month,flag:"no",paySalary:paySalary,sendId:sendId,status:status},//参数，用链接的形式发送
            async:false,//是否发送异步请求
            dataType:'json',//返回的类型，有html，xml等
            success:function (data) {//成功之后会做的事情
                var message = data.data.message;
                send = data.data.sendId;
                if((null != send && send != "") && ("" == sendId || null == sendId)){
                    sendId = send;
                }
                if(message != "success"){
                    showInfo(data.data.message);
                }else{
                    displayInTab3("SubmitPersonSalaryItemsTab", "员工薪资发放", {requestUrl: "SubmitPersonSalaryInfo",data:{partyId:partyId,year:year,month:month,flag:"history",sendId:sendId,status:status,paySalary:paySalary}, width: "800px"});

                }
            }
        })

    },
	changeType: function(){
		var value = document.getElementById("AddAttendance_type").value;
		if(value == "ABSENTEEISM"){
			//$("#AddAttendance_timeRangeStart_title").hide();
			$("input[name='timeRangeStart']").attr({"disabled":true});
			$("input[name='timeRangeEnd']").attr({"disabled":true});
		}else{
			$("input[name='timeRangeStart']").attr({"disabled":false});
			$("input[name='timeRangeEnd']").attr({"disabled":false});
		}
	},
    approvePersonSalaryItems: function(sendId){
        displayInTab3("ApprovePersonSalaryItemsTab", "员工薪资审核", {requestUrl: "ApprovePersonSalary",data:{sendId:sendId}, width: "800px"});
    },
    payPersonSalaryItems: function(sendId){
        displayInTab3("PayPersonSalaryItemsTab", "员工薪资发放", {requestUrl: "PayPersonSalary",data:{sendId:sendId}, width: "800px"});
    },
    viewPersonSalaryItems: function(sendId){
        displayInTab3("ViewPersonSalaryItemsTab", "员工薪资", {requestUrl: "ViewPersonSalary",data:{sendId:sendId}, width: "800px"});
    },
    addTemplateManagement: function(mouldId){
        var strTab = "新增工资条模板";
        if(mouldId!=null){
            strTab = "修改工资条模板";
        }
        displayInTab3("addTemplateManagement", strTab, {requestUrl: "addTemplateManagement", data:{mouldId:mouldId}, width: "800px"});
    },
    showTemplateManagement: function(){	
        $.ajax({
            type: 'POST',
            url: "showTemplateManagement",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#showTemplateManagement").html(content);
            }
        });
    },
    addSubmitSalaryItem:function(){
        var idForAdd = $("#AddSubmitSalaryItem_basedOnSalary").val();
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: 'html',
            data:{idForAdd:idForAdd},
            success: function (content) {
                document.getElementById('salaryDetail').innerHTML = " ";
                $("#salaryDetail").html(content);
            },
            url:"addSubmitSalaryItem",
            type: 'post'
        };
        $("#SubmitPersonSalaryForm").ajaxSubmit(options);
    },
    searchByYear:function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: 'html',
            success: function (content) {
                document.getElementById('column-container').innerHTML = " ";
                $("#column-container").html(content);
            },
            url:"SalaryPayOff",
            type: 'post'
        };
        $("#SalaryPayOffFindOptions").ajaxSubmit(options);
    },
    saveAttendance:function(){
		var deductType = document.getElementById("AddAttendance_deductType").value;
		var deductValue = document.getElementById("AddAttendance_value").value;
		if(deductType == "PERCENT" && deductValue > 100){
			showInfo("百分比不得大于100！");
			return false;
		}
        var options = {
            beforeSubmit: function () {
                return $("#AddAttendance").validationEngine("validate");
            },
            dataType: 'json',
            success: function (data) {
                showInfo(data.data.msg);
                javascript:showSubMenuAjax('SalaryOnAttendance')
            },
            url:"saveAttendance",
            type: 'post'
        };
        $("#AddAttendance").ajaxSubmit(options);
    },
    saveSalaryTemplate: function(){
        $("#mouldContent").val(mouldContent.html());
        var options = {
            beforeSubmit: function () {
                return $('#addSalaryBillMouldForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
				showInfo(data.data.msg);
                closeCurrentTab();
				$.ajax({
					type: 'POST',
					url: "SalaryNotice",
					async: true,
					dataType: 'html',
					success: function (content) {
						$("#column-container").html(content);
					}
				});
            },
            url: "saveSalaryBillMould",
            type: 'post'
        };
        $("#addSalaryBillMouldForm").ajaxSubmit(options);
	},
    deleteSalaryTemplate:function(mouldId){
        $.ajax({
            type: 'POST',
            url: "deleteSalaryTemplate",
            async: true,
            data:{mouldId: mouldId},
            dataType: 'json',
            success: function (data) {
                showInfo(data.returnValue);
                $.salary.showTemplateManagement();
            }
        });
    },
    changeTemplateState: function(mouldId){
        $.ajax({
            type: 'POST',
            url: "changeTemplateState",
            async: true,
            data:{mouldId: mouldId},
            dataType: 'json',
            success: function (data) {
                showInfo(data.returnValue);
				$.ajax({
					type: 'POST',
					url: "SalaryNotice",
					async: true,
					dataType: 'html',
					success: function (content) {
						$("#column-container").html(content);
					}
				});
            }
        });
	},
    saveSalaryEntry: function(){
		var relativeEntry = document.getElementById("EditSalaryItem_relativeEntry").value;
		var amount = document.getElementById("EditSalaryItem_amount").value;
		if(relativeEntry != "" && amount > 100){
			showInfo("百分比不得大于100！");
			return false;
		}
        var options = {
            beforeSubmit: function () {
                return $('#EditSalaryItem').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                if(data.data.msg == "FAILED"){
                    showError("相对条目顶层出现死循环");
                } else {
                    $.salary.refreshList();
                    showInfo(data.data.msg)
                }
            },
            url:"saveSalaryEntry",
            type: 'post'
        };
        //$("#AddAttendance").ajaxSubmit(options);
        $("#EditSalaryItem").ajaxSubmit(options);
    },
    saveSendDetail: function(type,status){
		var entryInfo = "";
		//var flag = false;
        var submitperson =  $('#submitpersonForm').validationEngine('validate');
        if(submitperson && (type == 'SEND_TYPE_NOTEXAMINE' || type == "")){
            $("input[name^='amount_']").each(function () {
                var id = $(this).attr("name");
                var entryId = id.substring(id.indexOf("_") + 1, id.indexOf(":"));
                var sendEntryId = id.substring(id.indexOf(":") + 1, id.length);
                var entryValue = $(this).val();
                if(null != entryValue && "" != entryValue){
                    entryInfo = entryInfo + entryId + ":" + $(this).val() + "-" + sendEntryId + ",";
                }
            });
            entryInfo = entryInfo.substr(0,entryInfo.length-1);
            var partyId = $("#partyId").val();
            var year = $("#year").val();
            var month = $("#month").val();
            var sendId = $("#sendId").val();
            var salaryType = $("#salaryType").val();
            $.ajax({
                type: 'POST',
                url: 'saveSendDetail',
                async: true,
                dataType: 'json',
                data:{entryInfo:entryInfo,status:"SEND_TYPE_NOTSEND",partyId:partyId,year:year,month:month,sendId:sendId},
                success: function (data) {
                    if(type == 'SEND_TYPE_NOTEXAMINE'){
                        sendId = data.data.sendId;
                        $.salary.submitSendDetail('SEND_TYPE_NOTEXAMINE',sendId);
                    } else if (status == 'SEND_TYPE_DISAPPROVE'){
                        showSubMenuAjax('SalaryPayOff');
                    }else{
                        closeCurrentTab();
                    }
                    searchSalaryPayOffList();

                }
            });
        }
    },
    excelLeadOut:function(year){
        var excelMonth = $("#excelMonth").val();
        var form = $("<form>");   //定义一个form表单
        form.attr('style', 'display:none');   //在form表单中添加查询参数
        form.attr('target', '');
        form.attr('method', 'post');
        form.attr('action', "excelLeadOut");
        var input1 = $('<input>');
        input1.attr('type', 'hidden');
        input1.attr('name', 'month');
        input1.attr('value', excelMonth);
        var input2 = $('<input>');
        input2.attr('type', 'hidden');
        input2.attr('name', 'year');
        input2.attr('value', year);
        $('body').append(form);  //将表单放置在web中
        form.append(input1);   //将查询参数控件提交到表单上
        form.append(input2);
        form.submit();
    },
    printSalaryBill: function(year){
		var flag = false;
        var num = 0;
        $("input[name^='month_']").each(function (){
            if ($(this).attr("checked")) {
                num++;
                var value = $(this).val();
                var valuestr = value.substr(0, value.lastIndexOf(","));
                var type = valuestr.substr(valuestr.lastIndexOf(",") + 1, valuestr.length);
                if (type != "SEND_TYPE_SEND") {
                    flag = true;
                    return;
                }
            }
        });
        if(num == 0){
            showInfo("请选择状态为已发的员工");
            return;
        }
        if(flag){
            showInfo("请选择状为已发的员工发送工资条");
            return;
        }
		var sendIdList = "";
		$("input[name^='month_']").each(function (){
            if ($(this).attr("checked")) {
                var valueInfo = $(this).val();
                var partyId = valueInfo.substr(0, valueInfo.indexOf(","));
                var partyValue = valueInfo.substr(valueInfo.indexOf(",") + 1, valueInfo.length);
                var year = partyValue.substr(0, partyValue.indexOf(","));
                var yearValue = partyValue.substr(partyValue.indexOf(",") + 1, partyValue.length);
                var month = yearValue.substr(0, yearValue.indexOf(","));
                var monthValue = yearValue.substr(yearValue.indexOf(",") + 1, yearValue.length);
                var sendId = monthValue.substr(0, monthValue.indexOf(","));
				sendIdList = sendIdList + sendId + ",";
            }
        });
        displayInTab3("printSalaryBillTab", "工资条打印", {requestUrl: "printSalaryBill",data:{sendIdList:sendIdList},width: "800px"});
    },
    submitSendDetail: function(status,sendId){
		 $.ajax({
            type: 'POST',
            url: 'saveSendDetail',
            async: true,
            dataType: 'json',
            data:{status:status,sendId:sendId},
            success: function (data) {
				closeCurrentTab();
				showSubMenuAjax('SalaryPayOff');
            }
        });
    },
    refreshList:function () {
        $.ajax({
            type: 'GET',
            url: "SalaryItems",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#column-container").html(content);
            }
        });
    }
}

$.hrMacro = {
    emptyScopes : function(id, name, entityName){
        $("#" + id).html("");
        $("input[name^=" + name + "_]").val("");
		var entityNameId = name + "_entity_name";
		//alert(entityNameId);
		//alert(entityName);
        document.getElementById(entityNameId).value = entityName;
        $("input[name^=" + name + "_][class]").trigger("blur");
        return false;
    },
    openDataScope : function(param){
        var width = Math.floor($(".contentarea").width() * 0.9);
        if(width > 1000){
            width = 1000;
        }
        var id = param.id, name = param.name, appCtx = param.appCtx, dept = param.dept, level = param.level, position = param.position, user = param.user, like = param.like;
        displayInTab3('','选择范围', {
            requestUrl: appCtx ? appCtx + '/LookupDataScope' : 'LookupDataScope', width: width, data: {
                lookupId: id,
                lookupName: name,
                deptOnly: $("#" + name + "_dept_only").val(),
                deptLike: $("#" + name + "_dept_like").val(),
                levelOnly: $("#" + name + "_level_only").val(),
                levelLike: $("#" + name + "_level_like").val(),
                positionOnly: $("#" + name + "_position_only").val(),
                positionLike: $("#" + name + "_position_like").val(),
                userValue: $("#" + name + "_user").val(),
                dept: dept,
                level: level,
                position: position,
                like: like,
                user: user
            }
        });
    },
    getDataScope : function(param){
        var name = param.name;
       return {
           entityName: $("#" + name + "_entity_name").val(),
           deptOnly: $("#" + name + "_dept_only").val(),
           deptLike: $("#" + name + "_dept_like").val(),
           levelOnly: $("#" + name + "_level_only").val(),
           levelLike: $("#" + name + "_level_like").val(),
           positionOnly: $("#" + name + "_position_only").val(),
           positionLike: $("#" + name + "_position_like").val(),
           userValue: $("#" + name + "_user").val(),
           dataId: $("#" + name + "_data_id").val()
       }
    },
    isEmptyScope : function(param){
        var name = param.name;
       return !$("#" + name + "_dept_only").val() && !$("#" + name + "_dept_like").val() && !$("#" + name + "_level_only").val() && !$("#" + name + "_level_like").val() && !$("#" + name + "_position_only").val() && !$("#" + name + "_position_like").val() && !$("#" + name + "_user").val();
    }
}
