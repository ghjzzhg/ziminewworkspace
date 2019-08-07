$.vehicleManagement = {
    vehicleOrderView:function(orderId){
        displayInTab3("vehicleOrderViewTab", "车辆预约查看",{requestUrl: "VehicleOrderView",data:{orderId:orderId},width: "900px"});
    },
    auditVehicleOrder: function(orderId){
        displayInTab3("auditVehicleOrderViewTab", "车辆预约审核",{requestUrl: "AuditVehicleOrder",data:{orderId:orderId},width: "900px"});
    },
    arrangeVehicleOrder: function(orderId){
		displayInTab3("arrangeVehicleOrderViewTab", "车辆预约安排",{requestUrl: "ArrangeVehicleOrder",data:{orderId:orderId},width: "900px"});
    },
    listVehicle: function(){
                        displayInTab3("listVehicleTab", "车辆列表", {requestUrl: "ListVehicle" ,width:700});
    },
    saveAuditVehicleOrder: function(state){
		//alert(state);
        $('textarea[name="reviewRemarks"]').val(reviewRemarks.html());
        var options = {
            beforeSubmit: function () {
                return $("#AuditVehicleOrder").validationEngine('validate');
            },
            dataType: "json",
            data:{reviewState: state},
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
				$.vehicleManagement.refreshFindVehicleManagement();
            },
            url: "saveAuditVehicleOrder", 
            type: 'post'
        };
        $("#AuditVehicleOrder").ajaxSubmit(options);
    },
    saveArrangeVehicleOrder: function(state){
        $('textarea[name="arrangeRemarks"]').val(arrangeRemarks.html());
        var options = {
            beforeSubmit: function () {
                return $("#ArrangeVehicleOrder").validationEngine('validate');
            },
            dataType: "json",
            data:{reviewState: state},
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
				$.vehicleManagement.refreshFindVehicleManagement();
            },
            url: "saveArrangeVehicleOrder", 
            type: 'post'
        };
        $("#ArrangeVehicleOrder").ajaxSubmit(options);
    },
    saveVehicle: function(){
        $("textarea[name='remarks']").val(remarks.html());
        var options = {
            beforeSubmit: function () {
                var validation = $('#createVehicleForm').validationEngine('validate');
                return validation;
            },
            async:false,
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg)
                //$.vehicleManagement.refreshVehicleList();
                $.vehicleManagement.refreshOrderVehicleList();
            },
            url: "saveVehicle", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#createVehicleForm").ajaxSubmit(options);
    },
    editVehicleFor: function(){
        var options = {
            beforeSubmit: function () {
                var validation = $('#editVehicleForm').validationEngine('validate');
                return validation;
            },
            async:false,
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg)
                $.vehicleManagement.refreshVehicleList();
                $.vehicleManagement.refreshOrderVehicleList();
               /* $.vehicleManagement.refreshFindVehicleManagement();*/
            },
            url: "saveVehicle", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#editVehicleForm").ajaxSubmit(options);
    },
    saveOrderVehicle: function(){
        //var startDate=$("#orderVehicleForm_startDate_i18n").val();
        //var endDate=$("#orderVehicleForm_endDate_i18n").val();
        //if(startDate>=endDate){
        //    showInfo("结束时间必须大于开始时间")
        //    return false;
        //}
        var options = {
            beforeSubmit: function () {
                return $("#orderVehicleForm").validationEngine('validate');
            },
            async:true,
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                if(data.data.msg=="所选时间与其他预约有冲突，请重新预约!"){
                    showInfo("请修改预约时间!");
                }else{
                    closeCurrentTab();
                    $.vehicleManagement.refreshOrderVehicleList();
                    $.vehicleManagement.refreshFindVehicleManagement();
                }
                //$.vehicleManagement.refreshVehicleList();


            },
            url: "saveOrderVehicle", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#orderVehicleForm").ajaxSubmit(options);
    },
    saveOrderVehicleForReview: function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            async:true,
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg)
                $.vehicleManagement.refreshVehicleOccupyList();
            },
            url: "saveOrderVehicle", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#VehicleOrderView").ajaxSubmit(options);
    },
    refreshVehicleList:function(){
        $.ajax({
            type: 'GET',
            url: "ListVehicle",
            async: true,
            dataType: 'html',
            success: function (content) {
                document.getElementById('listVehicleTab').innerHTML = " ";
                $("#listVehicleTab").html($(content));
            }
        });
    },
    refreshVehicleOccupyList:function(){
        $.ajax({
            type: 'GET',
            url: "vehicleOccupy",
            async: true,
            dataType: 'html',
            success: function (content) {
                document.getElementById('screenlet_1_col').innerHTML = " ";
                $("#screenlet_1_col").html($(content));
            }
        });
    },
    refreshOrderVehicleList:function(){
        $.ajax({
            type: 'GET',
            url: "vehicleOrderList",
            async: true,
            dataType: 'html',
            success: function (content) {
                document.getElementById('screenlet_1_col').innerHTML = " ";
                $("#screenlet_1_col").html($(content));
            }
        });
    },
    /**
     * 整体刷新
     */
    refreshFindVehicleManagement:function(){
        $.ajax({
            type: 'GET',
            url: "FindVehicleManagement",
            async: true,
            dataType: 'html',
            success: function (content) {
                document.getElementById('column-container').innerHTML = " ";
                $("#column-container").html($(content));
            }
        });
    },
    saveVehicleCost: function(){
        var options = {
            beforeSubmit: function () {
                var validation = $('#createVehicleForCost').validationEngine('validate');
                return validation;
            },
            async:true,
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg)
                $.ajax({
                    type: 'POST',
                    url: "vehicleForCost",
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        document.getElementById('vehicleForCostTab').innerHTML = " ";
                        $("#vehicleForCostTab").html($(content));
                    }
                });
            },
            url: "saveVehicleCost", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#createVehicleForCost").ajaxSubmit(options);
    },

    deleteVehicle:function(vehicleId){
        $.ajax({
            type: 'POST',
            url: "deleteVehicle",
            async: true,
            dataType: 'json',
            data:{vehicleId: vehicleId},
            success: function (data) {
                showInfo(data.data.msg)
                $.vehicleManagement.refreshOrderVehicleList();
                $.vehicleManagement.refreshVehicleList();
                $.vehicleManagement.refreshFindVehicleManagement();
            }
        });
    },
    searchVehicleSituationByDate:function(){
        var date = $("#searchDate").val();
        $.ajax({
            type: 'GET',
            url: "searchVehicleSituationByDate",
            async: true,
            dataType: 'html',
            data:{date: date},
            success: function (content) {
                //$("#screenlet_1_col").html($(content));vehicleOccupySituationForOrder
                //$.vehicleManagement.refreshOrderVehicleList();
                document.getElementById('screenlet_1_col').innerHTML = " ";
                $("#screenlet_1_col").html($(content));
            }
        });
    },
    searchVehicleSituationForOrderByDate:function(){
        var date = $("#dateForOrderSearch").val();
        $.ajax({
            type: 'GET',
            url: "vehicleOccupySituationForOrder",
            async: true,
            dataType: 'html',
            data:{date: date},
            success: function (content) {
                $("#vehicleOccupySituationForOrder").html($(content));
            }
        });
    },
    searchVehicleSituationForCostByDate:function(){
        var date = $("#dateForCostSearch").val();
        $.ajax({
            type: 'GET',
            url: "searchVehicleSituationForCostByDate",
            async: true,
            dataType: 'html',
            data:{date: date},
            success: function (content) {
                $("#vehicleForCost").html($(content));
            }
        });
    },
    orderVehicle:function(){
		displayInTab3("orderVehicleTbl", "预约车辆", {requestUrl: "OrderVehicle" ,width:900,height:600,position:"center"});
    },
    vehicleForCost:function(){
		displayInTab3("vehicleForCostTab", "费用管理",{requestUrl: "vehicleForCost" ,height:600,width:900,position:"center"});
    },

    editVehicle: function(vehicleId){
		displayInTab3("vehicleTab", "修该车辆信息", {requestUrl: "AjaxEditVehicle" ,data:{vehicleId:vehicleId},width:800});
    },
    createVehicle:function(){
		displayInTab3("vehicleTab", "新增车辆",{requestUrl: "CreateVehicle",width:800});
    },
    //查看车辆当月费用详情
    showVehicle:function(vehicleId,year,month){
        displayInTab3("vehicleTab", "车辆费用详情",{requestUrl: "ShowVehicle",data:{vehicleId:vehicleId,year:year,month:month},width:700});
    },
    //根据费用类型来查询当月车辆费用
    searchVehicleByCostType:function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            async:true,
            dataType: "html",
            success: function (content) {
                $("#vehicleForDetailsScreens").html(content);
            },
            url: "vehicleForDetailsScreens", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#searchShowVehicle").ajaxSubmit(options);
    },
    //删除费用
    deleteVehicleByCostId:function(id){
        if(confirm("是否确认删除？")) {
            $.ajax({
                type: 'post',
                url: "deleteVehicleByCostId",
                data:{costId:id},
                async: true,
                dataType: 'json',
                success: function (data) {
                    showInfo(data.data.msg);
                    $.vehicleManagement.searchVehicleByCostType();
                    $.vehicleManagement.refreshOrderVehicleList();
                    $.vehicleManagement.refreshVehicleList();
                    $.ajax({
                        type: 'POST',
                        url: "vehicleForCost",
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            document.getElementById('vehicleForCostTab').innerHTML = " ";
                            $("#vehicleForCostTab").html($(content));
                        }
                    });
                }
            });
        }
    },
    //保存修改的费用
    saveVehicleByCostId:function(id){
            var cost = $("#cost_"+id).val();
            var remarks = $("#remarks_"+id).val();
            $.ajax({
                type: 'post',
                url: "saveVehicleByCostId",
                data:{costId:id,cost:cost,remarks:remarks},
                async: true,
                dataType: 'json',
                success: function (data) {
                    showInfo(data.data.msg);
                    $.vehicleManagement.searchVehicleByCostType();
                    $.vehicleManagement.refreshOrderVehicleList();
                    $.vehicleManagement.refreshVehicleList();
                    $.ajax({
                        type: 'POST',
                        url: "vehicleForCost",
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            document.getElementById('vehicleForCostTab').innerHTML = " ";
                            $("#vehicleForCostTab").html($(content));
                        }
                    });
                }
            });
    }
}

$.UnitContract = {
    createContract: function(){
        displayInTab3("createContractTab", "添加单位合同",{requestUrl: "CreateUnitContract", width: "800px"});
    },
    editUnitContract: function(id){
        displayInTab3("createContractTab", "添加单位合同",{requestUrl: "editUnitContract",data:{unitContractId:id}, width: "800px"});
    },
    saveUnitContract:function(id){
        $("textarea[name='abstract']").val(abstract.html());
        $("textarea[name='remark']").val(remark.html());
        var options = {
            beforeSubmit: function () {
                return $('#unitContractCreate').validationEngine('validate');
            },
            dataType: "json",
            data:{unitContractId:id},
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.UnitContract.searchContract();
            },
            url: "saveUnitContract",
            type: 'post'
        };
        $("#unitContractCreate").ajaxSubmit(options);
    },
    deleteUnitContract : function(id){
        $.ajax({
            type: 'POST',
            url: "deleteUnitContract",
            async: true,
            dataType: 'json',
            data:{unitContractId: id},
            success: function (data) {
                showInfo(data.data.msg);
                $.UnitContract.searchContract();
            }
        });
    },
    searchContract : function(){
        var options = {
            type: 'post',
            url: "searchUnitContract",
            dataType: 'html',
            success: function (data) {
                $("#UnitContractListId").html(data);
            }
        };
        $("#SearchUnitContract").ajaxSubmit(options);
    }
}
$.resourceManagement = {
    listResource: function(){
                displayInTab3("listResourceTab", "资源列表",{requestUrl: "ListResource", width: "700px"});
    },
    saveResource: function(){
        $("textarea[name='resourceExplain']").val(resourceExplain.html());
        var options = {
            beforeSubmit: function () {
                return $('#createResourceForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg)
                $.resourceManagement.refreshResourceList();
                $.resourceManagement.refreshResourceOccupySituation();
            },
            url: "saveResource", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#createResourceForm").ajaxSubmit(options);
    },
    searchResourceSituationByDate:function(){
        var date = $("#searchDate").val();
        $.ajax({
            type: 'GET',
            url: "searchResourceSituationByDate",
            async: true,
            dataType: 'html',
            data:{date: date},
            success: function (content) {
                $("#resourceOccupy-situation").html($(content));
            }
        });
    },
    searchResourceSituationByDateForOrder:function(){
        var date = $("#searchDateForOrder").val();
        $.ajax({
            type: 'GET',
            url: "searchResourceSituationByDateForOrder",
            async: true,
            dataType: 'html',
            data:{date: date},
            success: function (content) {
                $("#forOrder").html($(content));
            }
        });
    },
    saveOrderResource: function(){
        var startDate=$("#orderResourceForm_startDate_i18n").val();
        var endDate=$("#orderResourceForm_endDate_i18n").val();
        if(startDate>=endDate){
            showInfo("结束时间必须大于开始时间")
            return false;
        }
        var options = {
            beforeSubmit: function () {
                return $("#orderResourceForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                if(data.data.msg=="所选时间与其他预约有冲突，请重新预约!"){
                    showInfo("请修改预约时间!");
                }else{
                    closeCurrentTab();
                    $.resourceManagement.refreshResourceOccupySituation();
                    $.resourceManagement.refreshPendingAudit();
                }

            },
            url: "saveOrderResource", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#orderResourceForm").ajaxSubmit(options);
    },
    saveAuditResourceOrder: function(state){
		//alert(state);
        $('textarea[name="auditRemarks"]').val(auditRemarks.html());
        var options = {
            beforeSubmit: function () {
                return $("#AuditResourceOrder").validationEngine('validate');
            },
            dataType: "json",
            data:{reviewState: state},
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.resourceManagement.refreshResourceOccupySituation();
                $.resourceManagement.refreshPendingAudit();
                $.resourceManagement.refreshPendingArrange();
                $.resourceManagement.refreshTodayUseSituation();
            },
            url: "saveAuditResourceOrder", 
            type: 'post'
        };
        $("#AuditResourceOrder").ajaxSubmit(options);
    },
    saveArrangeResourceOrder: function(state){
        $('textarea[name="arrangeRemarks"]').val(arrangeRemarks.html());
        var options = {
            beforeSubmit: function () {
                return $("#ArrangeResourceOrder").validationEngine('validate');
            },
            dataType: "json",
            data:{reviewState: state},
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.resourceManagement.refreshResourceOccupySituation();
                $.resourceManagement.refreshPendingAudit();
                $.resourceManagement.refreshPendingArrange();
                $.resourceManagement.refreshTodayUseSituation();
            },
            url: "saveArrangeResourceOrder", 
            type: 'post'
        };
        $("#ArrangeResourceOrder").ajaxSubmit(options);
    },
    deleteResource:function(resourceId){
        $.ajax({
            type: 'POST',
            url: "deleteResource",
            async: true,
            dataType: 'json',
            data:{resourceId: resourceId},
            success: function (data) {
                showInfo(data.data.msg);
                $.resourceManagement.refreshResourceList();
                $.resourceManagement.refreshResourceOccupySituation();
            }
        });
    },
    refreshResourceList:function(){
        $.ajax({
            type: 'GET',
            url: "ListResource",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#listResourceTab").html($(content));
            }
        });
    },
    refreshResourceOccupySituation:function(){
        $.ajax({
            type: 'GET',
            url: "resourceOrderList",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#resourceOccupySituation_col").html($(content));
            }
        });
    },
    refreshPendingAudit:function(){
        $.ajax({
            type: 'GET',
            url: "pendingAudit",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#pendingAudit_col").html(content);
            }
        });
    },
    refreshPendingArrange:function(){
        $.ajax({
            type: 'GET',
            url: "pendingArrange",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#pendingArranged_col").html(content);
            }
        });
    },
    refreshTodayUseSituation:function(){
        $.ajax({
            type: 'GET',
            url: "todayUseSituation",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#todayUseSituation_col").html(content);
            }
        });
    },
    auditResourceOrder: function(orderId){
                displayInTab3("auditResourceOrderViewTab", "资源预约审核",{requestUrl: "AuditResourceOrder",data:{orderId:orderId},width: "900px"});
    },
    arrangeResourceOrder: function(orderId){
                displayInTab3("arrangeResourceOrderViewTab", "资源预约安排",{requestUrl: "ArrangeResourceOrder",data:{orderId:orderId},width: "900px"});
    },
    orderResource:function(){
                displayInTab3("orderResource", "预约资源",{requestUrl: "OrderResource", width: "900px"});
    },
    editResource: function(resourceId){
                displayInTab3("resourceTab", "修该资源信息",{requestUrl: "AjaxEditResource",data:{resourceId:resourceId}, width: "800px"});
    },
    resourceOrderView:function(orderId){
                displayInTab3("resourceOrderViewTab", "资源预约查看",{requestUrl: "ResourceOrderView",data:{orderId:orderId},width: "900px"});
    },
    createResource:function(){
                displayInTab3("resourceTab", "新增资源", {requestUrl: "CreateResource",width: "800px"});
    }
}
$.fixedAssets = {
    saveFixedAssets: function(){
      var options = {
            beforeSubmit: function () {
                return $("#FixedAssetsForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                $('input[name="useDepartment"]').val("");
                $.fixedAssets.searchFixedAssets();
            },
            url: "saveFixedAssets", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#FixedAssetsForm").ajaxSubmit(options);
    },
    changeAssetType:function(id,name){
        showInfo(name);
        $("#FixedAssetsForSearch_assetType").append("<option value="+id+">"+name+"</option>")
    },
    borrowRegister:function(fixedAssetsId){
        displayInTab3("BorrowRegisterTab", "借用预登记", {
            requestUrl: "borrowRegister",
            data: {fixedAssetsId: fixedAssetsId},
            width: "800px"
        });
    },
    borrowAssetsConfirm:function(fixedAssetsId){
        displayInTab3("BorrowAssetsConfirmTab", "借用确认", {
            requestUrl: "borrowAssetsConfirm",
            data: {fixedAssetsId: fixedAssetsId},
            width: "800px"
        });
    },
    returnAssets:function(fixedAssetsId){
        displayInTab3("ReturnAssetsTab", "归还确认", {
            requestUrl: "returnAssets",
            data: {fixedAssetsId: fixedAssetsId},
            width: "800px",
        });
    },
    saveBorrowRegister: function(id){
        var options = {
            beforeSubmit: function () {
                return $("#borrowRegister").validationEngine('validate');
            },
            dataType: "json",
            data:{applyPerson: id},
            success: function (data) {
                closeCurrentTab();
                $.fixedAssets.searchAssetsBorrow();
                $.fixedAssets.passTask;
            },
            url: "borrowRegisterConfirm", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#borrowRegister").ajaxSubmit(options);
    },
    passTask:function(){
        $.ajax({
            type:'POST',
            url:"/workflow/control/passTask",
            async:true,
            data:formData,
            dataType:'json',
            success:function (data) {
            }
        });
    },
    saveborrowAssetsConfirm: function(){
        var options = {
            beforeSubmit: function () {
                return $("#borrowRegisterConfirm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                $.fixedAssets.searchAssetsBorrow();
            },
            url: "saveBorrowAssetsConfirm", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#borrowRegisterConfirm").ajaxSubmit(options);
    },
    rejectBorrowAssetsConfirm : function(){
        var options = {
            beforeSubmit: function () {
                return $("#borrowRegisterConfirm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                $.fixedAssets.searchAssetsBorrow();
            },
            url: "rejectBorrowAssetsConfirm", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#borrowRegisterConfirm").ajaxSubmit(options);
    },
    saveReturnAssets: function(id){
        var options = {
            beforeSubmit: function () {
                return $("#ReturnAssetsForm").validationEngine('validate');
            },
            dataType: "json",
            data:{assetReturnPerson: id},
            success: function (data) {
                closeCurrentTab();
                $.fixedAssets.searchAssetsBorrow();
            },
            url: "saveReturnAssets", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#ReturnAssetsForm").ajaxSubmit(options);
    },
    searchAssetsBorrow: function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (data) {
                $("#ListFixedAssetsBorrow").html(data);
            },
            url: "searchAssetsBorrow", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#searchFixedAssetsBorrow").ajaxSubmit(options);
    },
    removeAssets: function(assetsId){
        $.ajax({
            type: 'POST',
            url: "removeAssets",
            async: true,
            dataType: 'json',
            data:{assetsId: assetsId},
            success: function (content) {
                $.fixedAssets.refreshAssets();
            }
        });
    },
    editAssets: function(assetsId){
        displayInTab3("AssetsTab", "修改固定资产", {requestUrl: "AjaxEditAssets",data:assetsId,width: "700"});
    },
    borrowInfo:function(fixedAssetsId){
        displayInTab3("SubOrganizationTab", "资产借用单详情", {requestUrl: "borrowInfo", data:{fixedAssetsId:fixedAssetsId}, width: "900px"});
    },
    showFixedAssetsInfoForm:function(fixedAssetsId){
        displayInTab3("SubOrganizationTab", "固定资产", {requestUrl: "FixedAssetsInfoForm", data:{fixedAssetsId: fixedAssetsId}, width: "800px",height:600});
    },
    addAssetPartys:function(fixedAssetsId,assetsPartsId){
        displayInTab3("AddAssetPartysTab", "增加固定资产明细", {requestUrl: "addAssetPartys", data:{fixedAssetsId: fixedAssetsId,assetsPartsId:assetsPartsId}, width: "800px"});
    },
    addUserInfo:function(fixedAssetsId,fixedAssetsCode,assetsUseInfoId){
        displayInTab3("AddAssetPartysTab", "增加使用记录", {requestUrl: "addUseInfo", data:{fixedAssetsId: fixedAssetsId,fixedAssetsCode:fixedAssetsCode,assetsUseInfoId:assetsUseInfoId}, width: "800px"});
    },
    addRepair:function(fixedAssetsId,fixedAssetsCode,assetsRepairId){
        displayInTab3("AddRepairTab", "增加检修记录", {requestUrl: "addRepair", data:{fixedAssetsId: fixedAssetsId,fixedAssetsCode:fixedAssetsCode,assetsRepairId:assetsRepairId}, width: "800px"});
    },
    addFixedAssets:function(fixedAssetsId){
        var strTab = "固定资产增加"
        if(fixedAssetsId != null){
            strTab = "固定资产修改";
        }
        displayInTab3("SubOrganizationTab", strTab, {requestUrl: "AddFixedAssets", data:{fixedAssetsId:fixedAssetsId}, width: "800px"});
    },
    searchFixedAssets:function(){
        var options = {
            type: 'post',
            url: "searchFixedAssets",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#ListFixedAssetsOrg").html(content);
            }
        };
        $("#searchFixedAssetsForm").ajaxSubmit(options);
    },
    saveAssetsPartys:function(){
        var options = {
            beforeSubmit: function () {
                return $("#AssetPartysForm").validationEngine('validate');
            },
            type: 'post',
            url: "saveAssetsPartys",
            async: true,
            dataType: 'html',
            success: function (content) {
                closeCurrentTab2();
                $("#AssetsPartys").html(content);
            }
        };
        $("#AssetPartysForm").ajaxSubmit(options);
    },
    deleteParts:function(fixedAssetsId,assetsPartsId){
        $.ajax({
            type: 'post',
            url: "deleteParts",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#AssetsPartys").html(content);
            },
            data:{fixedAssetsId:fixedAssetsId,assetsPartsId:assetsPartsId}
        });
    },
    deleteUseInfo:function(fixedAssetsId,assetsUseInfoId){
        $.ajax({
            type: 'post',
            url: "deleteUseInfo",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#AassetsUse").html(content);
            },
            data:{fixedAssetsId:fixedAssetsId,assetsUseInfoId:assetsUseInfoId}
        });
    },
    deleteRepair:function(fixedAssetsId,assetsRepairId){
        $.ajax({
            type: 'post',
            url: "deleteRepair",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#AssetsRepair").html(content);
            },
            data:{fixedAssetsId:fixedAssetsId,assetsRepairId:assetsRepairId}
        });
    },
    deleteFixedsAssets:function(fixedAssetsId){
        $.ajax({
            type: 'post',
            url: "deleteFixedsAssets",
            async: true,
            dataType: 'json',
            success: function (data) {
                showInfo(data.msg);
                $.fixedAssets.searchFixedAssets();
            },
            data:{fixedAssetsId:fixedAssetsId}
        });
    },
    saveUseInfo:function(){
        var options = {
            beforeSubmit: function () {
                return $("#AssetsUseInfoForm").validationEngine('validate');
            },
            type: 'post',
            url: "saveUseInfo",
            async: true,
            dataType: 'html',
            success: function (content) {
                closeCurrentTab2();
                $("#AassetsUse").html(content);
            }
        };
        $("#AssetsUseInfoForm").ajaxSubmit(options);
    },
    saveAssetsRepair:function(){
        var options = {
            beforeSubmit: function () {
                return $("#AssetsRepairForm").validationEngine('validate');
            },
            type: 'post',
            url: "saveAssetsRepair",
            async: true,
            dataType: 'html',
            success: function (content) {
                closeCurrentTab2();
                $("#AssetsRepair").html(content);
            }
        };
        $("#AssetsRepairForm").ajaxSubmit(options);
    },
    findFixedAssets: function(){
        var options = {
            type: 'GET',
            url: "ListAssets",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#search-results").html(content);
            }
        };
        $("#FindFixedAssets").ajaxSubmit(options);
    },
    refreshAssets: function(){
        $.ajax({
            type: 'GET',
            url: "ListAssets",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#search-results").html(content);
            }
        });
    }
}

$.FileData = {
    searchFileType:function(){
        var Id = $("#Id").attr("value");
        $.ajax({
            type: 'GET',
            url: "ListFileType",
            async: true,
            dataType: 'html',
            data:{Id:Id},
            success: function (content) {
                $("#ListFileType").html(content);
            }
        });
    },
    addFileType: function(strTab){
        var verifyId=$("#verifyId").attr("value");
        var parentId=$("#parentId").attr("value");
        var typeName=$("#typeName").attr("value");
        if(verifyId!="1000"){
            showInfo("该文档类别不允许有下级类别，请重新选择！");
            return false;
        }
        displayInTab3("addFileTypeTab", strTab, {
            requestUrl: "addFileType",
            width: "400px",
            height: 300,
            position: "center",
            data:{typeName:typeName,parentId:parentId}
        });
    } ,
    saveFileType:function(){
        var options = {
            beforeSubmit: function () {
                return $("#addFileTypeForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.FileData.searchFileType();
                $.FileData.refreshTree();
                $.FileData.searchFileData();
            },
            url: "saveFileType",
            type: 'post'
        };
        $("#addFileTypeForm").ajaxSubmit(options);
    },
    refreshTree: function(){
        $.treeInline['fileDataItemTypes'].initTreeInline();
    },
    deleteFileType: function(fileTypeId){
        if (confirm("该类别下可能有档案记录，请谨慎删除！确定删除该项吗?")) {
            $.ajax({
                type: 'GET',
                url: "deleteFileType",
                async: true,
                dataType: 'json',
                data:{fileTypeId:fileTypeId},
                success: function (data) {
                    showInfo(data.msg);
                    $.FileData.searchFileType();
                    $.FileData.refreshTree();
                }
            });
        }
    },
    modifyFileType: function(id,strTab){
        displayInTab3("addFileTypeTab", strTab, {
            requestUrl: "modifyFileType",
            width: "400px",
            height: 300,
            position: "center",
            data:{fileTypeId:id}
        });
    },
    auditFileData: function(id,strTab){
        displayInTab3("auditFileData", strTab, {
            requestUrl: "auditFileData",
            height: 600,
            position: "center",
            data:{fileDataId:id}
        });
    },
    showFileData: function(id,strTab){
        displayInTab3("showFileData", strTab, {
            requestUrl: "showFileData",
            width: "600px",
            height: 500,
            position: "center",
            data:{fileDataId:id}
        });
    },
    modifyFileData: function(id,strTab){
        displayInTab3("createFileDataTab", strTab, {
            requestUrl: "modifyFileData",
            width: "1000px",
            height: 600,
            position: "center",
            data:{fileDataId:id}
        });
    } ,
    showFileDataVersion : function(fileDataId,oldFileDataId){
      displayInTab3("createFileDataTab","历史版本",{
          requestUrl:"showFileDataVersion",
          width:"800px",
          height:600,
          position:"center",
          data:{fileDataId:fileDataId,oldFileDataId:oldFileDataId}
      });
    },
    createFileData: function(strTab){
        var parentTypeName = $("#parentTName").attr("value");
        var sonTypeName = $("#sonTName").attr("value");
        var fileDataSonId = $("#fileDataSonId").attr("value");
        var fileDataParentId = $("#fileDataParentId").attr("value");
       /* if(parentTypeName==""||sonTypeName==""){
            showInfo("请在左边选择文档类别和文档子类别！");
            return false;
        }*/
        displayInTab3("createFileDataTab", strTab, {
            requestUrl: "CreateFileData",
            width: "1000px",
            height: 600,
            position: "center",
            data:{sonTypeName:sonTypeName,parentTypeName:parentTypeName,fileDataSonId:fileDataSonId,fileDataParentId:fileDataParentId}
        });
    } ,
    saveFileData:function(){
        $('textarea[name="documentContent"]').val(documentContent.html());
        $('textarea[name="remarks"]').val(remarks.html());
        $('textarea[name="reason"]').val(reason.html());
        var options = {
            beforeSubmit: function () {
                return $("#fileDataCreateForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.FileData.searchFileData();
            },
            url: "saveFileData",
            type: 'post'
        };
        $("#fileDataCreateForm").ajaxSubmit(options);
    },
    auditSuccess:function(){
        $('textarea[name="auditContent"]').val(auditContent.html());
        $('input[name="status"]').val("审核通过");
        var options = {
            beforeSubmit: function () {
                return $("#fileDataAuditForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.FileData.searchFileData();
            },
            url: "saveAudit",
            type: 'post'
        };
        $("#fileDataAuditForm").ajaxSubmit(options);
    },
    auditFail:function(){
        $('textarea[name="auditContent"]').val(auditContent.html());
        $('input[name="status"]').val("驳回文档");
        var options = {
            beforeSubmit: function () {
                return $("#fileDataAuditForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.FileData.searchFileData();
            },
            url: "saveAudit",
            type: 'post'
        };
        $("#fileDataAuditForm").ajaxSubmit(options);
    },
    searchFileData:function(){
        var parentTypeName =$("#fileDataParentId").attr("value");
        var sonTypeName = $("#fileDataSonId").attr("value");
        $("input[name='parentTypeName']").val(parentTypeName);
        $("input[name='sonTypeName']").val(sonTypeName);
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (context) {
                $("#fileDataList").html(context);
            },
            url: "searchFileData",
            type: 'post'
        };
        $("#FindFileData").ajaxSubmit(options);
    },
    deleteFileData: function(fileDataId){
        if (confirm("确定删除该项档案记录吗?")) {
            $.ajax({
                type: 'GET',
                url: "deleteFileData",
                async: true,
                dataType: 'json',
                data:{fileDataId:fileDataId},
                success: function (data) {
                    showInfo(data.msg);
                    $.FileData.searchFileData();
                }
            });
        }
    }/*,
    editFileData: function(type){
        displayInTab3("createFileDataTab", "发布档案",{requestUrl: "CreateFileData",data:{type:type},width: "700px"});
    } ,
    maintainType: function(){
        displayInTab3("maintainTypeTab", "维护档案类别",{requestUrl: "MaintainType", width: "600px"});
    },
    createMaintainType: function(){
        displayInTab3("createMaintainTypeTab", "添加档案类别",{requestUrl: "CreateMaintainType", width: "600px"});
    },
    editMaintainType: function(parentTypeName,typeName){
        displayInTab3("editMaintainTypeTab", "编辑档案类别",{requestUrl: "EditMaintainType",data:{typeName:typeName,parentTypeName:parentTypeName},width: "600px"});
    }*/
}

function showNoticeHeadManagement(){
    displayInTab3("NoticeHeadManagement", "文档抬头单位管理", {
		requestUrl: "EditEnums", 
		data:{enumTypeId: "NOTICE_HEAD"}, 
		width: "600px", 
		close: function(){
			$.ajax({
				type: 'POST',
				url: "findEnums",
				async: true,
				data:{enumTypeId: "NOTICE_HEAD"},
				success: function (data) {
					$("#BumphNotice_noticeHead option").remove();
					for(var i = 0; i < data.data.size; i++){
						$("#BumphNotice_noticeHead").append("<option value='" + data.data.listEnums[i].enumId + "'>" + data.data.listEnums[i].description + "</option>");
					}
				}
			});
		}
	});
}

function noticeTypeManagement(){
    displayInTab3("noticeTypeManagement", "文档类型管理", {
		requestUrl: "EditEnums", 
		data:{enumTypeId: "NOTICE_TYPE"}, 
		width: "600px",
		close: function(){
			$.ajax({
				type: 'POST',
				url: "findEnums",
				async: true,
				data:{enumTypeId: "NOTICE_TYPE"},
				success: function (data) {
					$("#BumphNotice_noticeNumber option").remove();
					for(var i = 0; i < data.data.size; i++){
						$("#BumphNotice_noticeNumber").append("<option value='" + data.data.listEnums[i].enumId + "'>" + data.data.listEnums[i].description + "</option>");
					}
					$("#BumphNoticeSearchForm_noticeType option").remove();
					$("#BumphNoticeSearchForm_noticeType").append("<option value=''>--请选择--</option>");
					for(var i = 0; i < data.data.size; i++){
						$("#BumphNoticeSearchForm_noticeType").append("<option value='" + data.data.listEnums[i].enumId + "'>" + data.data.listEnums[i].description + "</option>");
					}
				}
			});
		}
	});
}

function useTemplateManagement(noticeTemplateId,noticeId){
    displayInTab3("UseTemplateManagement", "公文模板管理", {requestUrl: "UseTemplateManagement", data:{noticeTemplateId:noticeTemplateId, noticeId:noticeId}, width: "720px"});
}

$.bumphNotice = {
    addBumphNotice: function(id,strTab){
        displayInTab3("AddBumphNoticeTab", strTab, {
            requestUrl: "AddBumphNoticeAjax",
            width: "800px",
            height: 600,
            position: "center",
            data:{noticeId:id}
        });
    },
    addTransactionType: function(param){
        var strTab = "增加事务类别"
        if(param=="edit"){
            strTab = "修改事务类别";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "AddTransactionType",data:{param:param},width: "600px", position:"center"});
    },
    addTransactionProgress: function(param){
        var strTab = "增加事务进度"
        if(param=="edit"){
            strTab = "修改事务进度";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "AddTransactionProgress",data:{param:param},width: "600px", position:"center"});
    },
    showBumphNotice: function(){
        displayInTab3("AddTransactionTab", "签收记录", {requestUrl: "showBumphNotice",width: "800px",height:600,position:"center"});
    },

    BumphNoticeSignIn: function(noticeId){
        displayInTab3("AddTransactionTab", "签收记录", {
            requestUrl: "showBumphNotice",
            width: "800px",
            height:200,
            position: "center",
            data:{noticeId:noticeId}
        });
    },

    showBumphNoticeInfo: function(noticeId){
        displayInTab3("AddTransactionTab", "公文/通知明细", {
            requestUrl: "showBumphNoticeInfo",
            width: "800px",
            height:600,
            position: "center",
            data:{noticeId:noticeId}
        });
    },
	
	changeNoticeTemplate:function(noticeTemplateId, noticeId){
		$.ajax({
			type: 'POST',
			url: "changeNoticeTemplate",
			async: true,
			data:{noticeTemplateId:noticeTemplateId, noticeId:noticeId},
			success: function (data) {
				KindEditor.remove('textarea[name="content"]');
				$('textarea[name="content"]').val(data.data.noticeTemplate);
				template = KindEditor.create('textarea[name="content"]', {
					allowFileManager: true
				});
				
			}
		});
	},

    showTemplateInfo: function(noticeTemplateId,noticeId){
		$.ajax({
			type: 'POST',
			url: "UseTemplateManagement",
			async: true,
			data:{noticeTemplateId:noticeTemplateId, noticeId:noticeId},
			success: function (content) {
				//alert(content);
				document.getElementById('noticeTemplateList').innerHTML = " ";
				document.getElementById('editNoticeTemplate').innerHTML = " ";
				$("#noticeTemplateList").html($(content));
			}
		});
    },
	
    deleteNoticeTemplate:function(noticeTemplateId,noticeId){
		if(noticeId == ""){
			noticeId = null;
		}
        $.ajax({
            type: 'GET',
            url: "deleteNoticeTemplate",
            async: true,
            dataType: 'json',
            data:{noticeTemplateId:noticeTemplateId, noticeId:noticeId},
			success: function (data) {
				var noticeTemplateId = null;
                $.ajax({
                    type: 'POST',
                    url: "UseTemplateManagement",
                    async: true,
					data:{noticeTemplateId:noticeTemplateId, noticeId:noticeId},
                    success: function (content) {
                        document.getElementById('noticeTemplateList').innerHTML = " ";
                        document.getElementById('editNoticeTemplate').innerHTML = " ";
                        $("#noticeTemplateList").html($(content));
                    }
                });
				$("#BumphNotice_useTemplate option[value='" + data.data.noticeTemplateId + "']").remove();
                showInfo(data.data.msg);
			}
        });
	
	},
	
    saveNoticeTemplate:function(templateTextarea,noticeId){
		if(noticeId == ""){
			noticeId = null;
		}
        $("#noticeTemplate").val(template.html());
        var options = {
            beforeSubmit: function () {
				return $('#templateForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
				var noticeTemplateId = null;
                $.ajax({
                    type: 'POST',
                    url: "UseTemplateManagement",
                    async: true,
					data:{noticeTemplateId:noticeTemplateId, noticeId:noticeId},
                    success: function (content) {
                        document.getElementById('noticeTemplateList').innerHTML = " ";
                        document.getElementById('editNoticeTemplate').innerHTML = " ";
                        $("#noticeTemplateList").html($(content));
                    }
                });
				if(data.data.msg == "添加成功"){
					$("#BumphNotice_useTemplate").append("<option value='" + data.data.noticeTemplateId + "'>" + data.data.noticeTemplateName + "</option>");
				}
				if(data.data.msg == "更新成功"){
					$("#BumphNotice_useTemplate option[value='" + data.data.noticeTemplateId + "']").remove();
					$("#BumphNotice_useTemplate").append("<option value='" + data.data.noticeTemplateId + "'>" + data.data.noticeTemplateName + "</option>");
				}
                showInfo(data.data.msg);
            },
            url: "saveNoticeTemplate",
            type: 'post'
        };
        $("#templateForm").ajaxSubmit(options);
    },
	
    deleteNotice: function(noticeId){
        $.ajax({
            type: 'GET',
            url: "deleteNotice",
            async: true,
            dataType: 'json',
            data:{noticeId:noticeId},
            success: function (data) {
                showInfo(data.msg);
                $.bumphNotice.searchNotice();
            }
        });
    },
	
    saveNotice:function(template){
        $("textarea[name='content']").val(template.html());
        var options = {
            beforeSubmit: function () {
				return $('#noticeForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.msg);
                closeCurrentTab();
                $.bumphNotice.searchNotice();
            },
            url: "saveNotice",
            type: 'post'
        };
        $("#noticeForm").ajaxSubmit(options);
    },
    searchNotice:function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (context) {
                $("#BumphNoticeList").html(context);
            },
            url: "searchNotice",
            type: 'post'
        };
        $("#BumphNoticeSearchForm").ajaxSubmit(options);
    },
    searchSignInRecord:function(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (context) {
                $("#BumphNoticeSignInList").html(context);
            },
            url: "searchSignInRecord",
            type: 'post',
        };
        $("#BumphNoticeSignInSearchForm").ajaxSubmit(options);
    }
}
$.feedback = {
    findFeedback:function(workPlanId, childWorkPlanId, feedbackPersonId, selectorId){
        $.ajax({
            type: 'POST',
            url: "findFeedback",
            async: true,
            data: {workPlanId: workPlanId, childWorkPlanId: childWorkPlanId, partyId: feedbackPersonId,refreshId:selectorId},
            dataType: 'html',
            success: function (content) {
                $(selectorId).html(content);
            }
        });
    },
    getFeedback:function(feedbackMiddleId, childFeedbackId, feedbackPerson, type,selectorId){
        $.ajax({
            type: 'POST',
            url: "getFeedback",
            async: true,
            data: {feedbackMiddleId: feedbackMiddleId, childFeedbackId: childFeedbackId, feedbackPerson: feedbackPerson,feedbackMiddleType:type,refreshId:selectorId},
            dataType: 'html',
            success: function (content) {
                $(selectorId).html(content);
            }
        });
    }
}

$.proposal = {
    proposalEdit:function(strTab){
        displayInTab3("proposalEditTab", strTab, {requestUrl: "ProposalEdit",width: "800px",height:600, position:"center"});
    },
    editProposal : function(id){
        displayInTab3("proposalEditTab", "更新提案", {requestUrl: "editProposal",data:{proposalId:id},width: "800px",height:600, position:"center"});
    },
    deleteProposal : function(id){
        $.ajax({
            type: 'POST',
            url: "deleteProposal",
            async: true,
            dataType: 'json',
            data:{proposalId: id},
            success: function (data) {
                showInfo(data.data.msg);
                $.proposal.searchProposalList();
            }
        });
    },
    saveProposal : function(){
        $("textarea[name='proposalContent']").val(template.html());
        var options = {
            beforeSubmit: function () {
                return $('#proposalForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.proposal.searchProposalList();
            },
            url: "saveProposal",
            type: 'post'
        };
        $("#proposalForm").ajaxSubmit(options);
    },
    searchProposalList : function(){
        var options = {
            type: 'post',
            url: "searchProposalList",
            dataType: 'html',
            success: function (data) {
                $("#ListProposal").html(data);
            }
        };
        $("#ProposalSearchForm").ajaxSubmit(options);
    }
}

$.meetingNotice = {
    summaryRelease: function (id,type) {
        var strTab = "发布会议纪要"
        var data = new Object();
        if(type=="create"){
            data.meetingNoticeId = id;
        }else if(type=="edit"){
            data.summaryId = id;
            strTab = "修改会议纪要";
        }
        displayInTab3("summaryReleaseTab", strTab, {
            requestUrl: "summaryRelease",
            width: "800px",
            height: 600,
            position: "center",
            data:data
        });
    },
    showMeetingNoticeInfo: function (meetingNoticeId) {
        displayInTab3("MeetingSummaryInfoTab", "会议通知", {
            requestUrl: "meetingNoticeInfo",
            width: "800px",
            height: 600,
            data:{meetingNoticeId:meetingNoticeId},
            position: 'center'
        });
    },
    showMeetingInfo: function (meetingNoticeId,summaryId) {
        if(summaryId!=null&&summaryId!=''){
            displayInTab3("MeetingSummaryInfoTab", "会议纪要明细", {
                requestUrl: "showMeetingSummaryInfo",
                width: "800px",
                height: 600,
                position: "center",
                data:{summaryId:summaryId}
            });
        }else{
            $.meetingNotice.showMeetingNoticeInfo(meetingNoticeId);
        }
    },
    addMeetingNotice: function (id) {
        var strTab = "发布会议通知";
        if(id!=null){
            strTab = "修改会议通知";
        }
        displayInTab3("summaryReleaseTab", strTab, {
            requestUrl: "MeetingNoticeRelease",
            width: "800px",
            height: 600,
            position: "center",
            data:{meetingNoticeId:id}
        });
    },
	isStartEndDate: function(startDate,endDate){
		if(startDate.length>0&&endDate.length>0){ 
			var startDateTemp = startDate.split(" ");   
			var endDateTemp = endDate.split(" ");   
			var arrStartDate = startDateTemp[0].split("-");   
			var arrEndDate = endDateTemp[0].split("-");   
			var arrStartTime = startDateTemp[1].split(":");   
			var arrEndTime = endDateTemp[1].split(":");   
			var allStartDate = new Date(arrStartDate[0],arrStartDate[1],arrStartDate[2],arrStartTime[0],arrStartTime[1],arrStartTime[2]);   
			var allEndDate = new Date(arrEndDate[0],arrEndDate[1],arrEndDate[2],arrEndTime[0],arrEndTime[1],arrEndTime[2]);   
			if(allStartDate.getTime() > allEndDate.getTime()){
				showInfo("开始时间不能大于结束时间！");
				return false; 
			} else if(allStartDate.getTime() == allEndDate.getTime()){
				showInfo("开始时间不能等于结束时间！");
				return false; 
			} else{
				return true; 
			}
		}else{
			return false; 
		}
	},
    saveMeetingNotice: function (template) {
		/*var startDate = $("#meetingNotice_meetingStartTime").val();
		var endDate = $("#meetingNotice_meetingEndTime").val();
		if(!$.meetingNotice.isStartEndDate(startDate,endDate)){
			return;
		}*/
        $("textarea[name='content']").val(template.html());
        var options = {
            beforeSubmit: function () {
				return $('#meetingNoticeForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.msg);
                closeCurrentTab();
                $.meetingNotice.searchMeetingNotice();
                $.meetingNotice.searchMeetingSummary();
            },
            url: "saveMeetingNotice",
            type:"POST"
        };
        $("#meetingNoticeForm").ajaxSubmit(options);
    },
    saveMeetingSummary: function (template) {
		//var startDate = $("#meetingSummary_meetingTime").val();
		//var endDate = $("#meetingEndTime").val();
		//if(!$.meetingNotice.isStartEndDate(startDate,endDate)){
		//	return;
		//}
        $("textarea[name='summaryContent']").val(template.html());
        var options = {
            beforeSubmit: function () {
				$("input[name = title]").removeAttr("class","validate[required]");
				$("input[name = startTime]").removeAttr("class","validate[required,past[completeTime:yyyy-MM-dd]]");
				$("input[name = completeTime]").removeAttr("class","validate[required,future[startTime:yyyy-MM-dd]]");
				$("input[name = executor]").removeAttr("class","validate[required]");
				return $('#meetingNoticeSummaryForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                if(data.data.type == 'create'){
                    $.meetingNotice.searchMeetingNotice();
                }else{
                    $.meetingNotice.searchMeetingSummary();
                }
                closeCurrentTab();
            },
            url: "saveMeetingNoticeSummary",
            type:"POST",
        };
        $("#meetingNoticeSummaryForm").ajaxSubmit(options);
    },
    searchMeetingSummary: function () {
		var startDate = $("#MeetingSummarySearchForm_startTime").val();
		var endDate = $("#MeetingSummarySearchForm_endTime").val();
        if(startDate==null||startDate==''){
            if(endDate!=null && endDate!=''){
                showInfo("开始时间和结束时间要都选，才能使用时间查询")
                return false;
            }
        }else{
            if(endDate==null||endDate==''){
                showInfo("开始时间和结束时间要都选，才能使用时间查询")
                return false;
            }
        }
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (content) {
                $("#MeetingSummaryList").html(content);
            },
            url: "searchMeetingSummary",
            type:"POST",
        };
        $("#MeetingSummarySearchForm").ajaxSubmit(options);
    },
    searchMeetingNotice: function () {
		var startDate = $("#MeetingNoticeSearchForm_startTime").val();
		var endDate = $("#MeetingNoticeSearchForm_endTime").val();
        if(startDate==null||startDate==''){
            if(endDate!=null && endDate!=''){
                showInfo("开始时间和结束时间要都选，才能使用时间查询")
                return false;
            }
        }else{
            if(endDate==null||endDate==''){
                showInfo("开始时间和结束时间要都选，才能使用时间查询")
                return false;
            }
        }
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (content) {
                $("#MeetingNoticeList").html(content);
            },
            url: "searchMeetingNotice",
            type:"POST"
        };
        $("#MeetingNoticeSearchForm").ajaxSubmit(options);
    },
    deleteSummary: function (id) {
        $.ajax({
            type: 'POST',
            url: "deleteMeetingNoticeSummary",
            async: true,
            dataType: 'json',
            data:{summaryId:id},
            success: function (data) {
                showInfo(data.msg);
                $.meetingNotice.searchMeetingSummary();
            }
        });
    },
    deleteMeetingNotice: function (id) {
        $.ajax({
            type: 'POST',
            url: "deleteMeetingNotice",
            async: true,
            dataType: 'json',
            data:{meetingNoticeId:id},
            success: function (data) {
                showInfo(data.msg);
                $.meetingNotice.searchMeetingSummary();
            }
        });
    }
}

$.memo = {
    addMemo:function(strTab){
        displayInTab3("AddMemo", strTab, {requestUrl: "addMemo",width: "800px",height:"500px",position:"center"});
    },
    showMemoInfo:function(){
        displayInTab3("AddMemo", "个人日记＆备忘录浏览", {requestUrl: "showMemoInfo",width: "800px",position:"center"});
    }
}
$.liaison = {
    showSendContactList:function(){
        displayInTab3("showSendContactList", "发送联络单", {requestUrl: "showSendContactList", data:{}, width: "900px",height:600,position:'center'});
    },
    showLiaisonInfo:function(id){
        displayInTab3("AddMemo", "审核工作联络单", {requestUrl: "showLiaisonInfo",data:{contactListId:id},width: "800px",height:600,position:"center"});
    },
    showLiaisonInfos:function(id){
        displayInTab3("AddMemo", "审核工作联络单", {requestUrl: "showLiaisonInfos",data:{contactListId:id},width: "800px",height:600,position:"center"});
    },
    showLiaison:function(){
        displayInTab3("AddMemo", "工作联络单", {requestUrl: "showLiaison",width: "800px",height:600,position:"center"});
    },
    /**
     * 刷新审核工作联络单界面
     */
    refreshShowLiaisonInfo:function(id){
        $.ajax({
            type: 'post',
            url: "showLiaisonInfo",
            data:{contactListId:id},
            async: true,
            dataType: 'html',
            success: function (content) {
                document.getElementById('LiaisonInfo').innerHTML = " ";
                $("#LiaisonInfo").html($(content));
            }
        });
    },

    /**
     * 联络单状态双击事件
     * @param obj
     * @param id
     * @param reviewTheStatusId
     */
    onClickStatus: function(obj,id,reviewTheStatusId){
        $.ajax({
            type: 'post',
            url: "getLiaisonStatusList",
            data: {
                enumTypeId: "LIAISON_STATUS"
            },
            async: true,
            dataType: 'json',
            success: function (data) {
                var selectStr = '<select name="reviewTheStatus" onchange="$.liaison.toChangeStatus($(this))" class="'+id+'">';
                var liaisonStatusList = data.LiaisonStatusList;
                for(var i=0; i < liaisonStatusList.length; i++){
                    if(liaisonStatusList[i].enumId == reviewTheStatusId && liaisonStatusList[i].enumId != "LIAISON_STATUS_ONE"){
                        selectStr += '<option value="'+liaisonStatusList[i].enumId+'" selected>'+ liaisonStatusList[i].description+'</option>';
                    }else if((liaisonStatusList[i].enumId != "LIAISON_STATUS_ONE")){
                        selectStr += '<option value="'+liaisonStatusList[i].enumId+'">'+ liaisonStatusList[i].description+'</option>';
                    }
                }
                $(obj).html(selectStr);
            }
        });
    },
    /**
     * 修改联络单的状态
     * @param obj
     */
    toChangeStatus:function(obj){
        var contactListId = $(obj).attr("class");
        var value = $(obj).val();
        var confirmString="是否确认将联络单状态更改为："+$(obj).find("option:selected").text();
        if(confirm(confirmString)) {
            $.ajax({
                type: 'post',
                url: "changeLiaisonStatus",
                data: {
                    contactListId: contactListId,
                    reviewTheStatus: value
                },
                async: true,
                dataType: 'json',
                success: function (data) {
                    showInfo(data.data.msg);
                    $(obj).parent().parent().html('<a href="#nowhere" ondblclick="$.liaison.onClickStatus($(this),\'' + contactListId + '\',\'' + data.data.statusId + '\')" title="状态">' + data.data.statusName + '</a>');
                }
            });
        }
    },
    /**
     *审核联络单
     * @param id
     * @param workSheetAuditInformationId
     * @param param
     */
    approved:function(id,workSheetAuditInformationId,partyId,param){
        var auditOpinion = $("#auditOpinion_"+partyId).val();
        if(auditOpinion==""){
            showInfo("审核意见不能为空")
            return false;
        }
        var confirmString="";
        if(param=='PERSON_TWO'){
            confirmString="是否确认通过？";
        }else if(param=='PERSON_THREE'){
            confirmString="是否确认驳回？";
        }
        if(confirm(confirmString)) {
            $.ajax({
                type: 'post',
                url: "approvedLiaison",
                data: {
                    contactListId: id,
                    workSheetAuditInforId: workSheetAuditInformationId,
                    reviewTheStatus: param,
                    content: auditOpinion,
                    partyId:partyId
                },
                async: true,
                dataType: 'html',
                success: function (data) {
                    closeCurrentTab();
                    $("#auditInformationTableList_td").html(data);
                    $.liaison.searchLiaison();
                    //$.liaison.refreshShowLiaisonInfo(id);
                }
            });
        }
    },
    /**
     * 签收联络单
     * @param id
     * @param workSheetSignForInformationId
     * @param param
     */
    signed:function(id,fullName,workSheetSignForInformationId,param){
        var auditOpinion = $("#auditOpinion_"+workSheetSignForInformationId).val();
        if(auditOpinion==""){
            showInfo("签收意见不能为空")
            return false;
        }
        if(confirm("是否确认签收？")) {
            $.ajax({
                type: 'post',
                url: "signedLiaison",
                data: {
                    contactListId: id,
                    workSheetSignForId: workSheetSignForInformationId,
                    reviewTheStatus: param,
                    fullName:fullName,
                    content: auditOpinion
                },
                async: true,
                dataType: 'html',
                success: function (data) {
                    $("#SignForTableList_div").html(data);
                    $.liaison.searchLiaison();
                    $.liaison.refreshShowLiaisonInfo(id);
                }
            });
        }
    },
    /**
     * 回复联络单
     * @param id
     */
    replyInformation:function(id,content){
        $.ajax({
            type: 'post',
            url: "replyInformationLiaison",
            data:{contactListId:id,content:content},
            async: true,
            dataType: 'html',
            success: function (data) {
                $("#ReplyInformationList_div").html(data);
                showInfo($("#ReplyInformationList_msg").val());
                $.liaison.searchLiaison();
                content1.html("");
            }
        });
    },
    /**
     * 条件查询联络单
     */
    searchLiaison:function(){
        var flag = true;
        var Str="";
        var startDate=$('input[name="responseTimeStart"]').val()
        var endDate=$('input[name="responseTimeEnd"]').val()
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
            return ;
        }
        var options = {
            beforeSubmit: function () {

            },
            dataType: "html",
            success: function (data) {
                $("#LiaisonCheckList").html(data);
            },
            url: "searchLiaison",
            type: 'post',
        };
        $("#LiaisonCheckSearchForm").ajaxSubmit(options);
    },

    /**
     * 删除联系单
     * @param id
     */
    deleteLiaison:function(id){
        if(confirm("是否确认作废？")) {
            $.ajax({
                type: 'post',
                url: "deleteLiaison",
                data:{contactListId:id},
                async: true,
                dataType: 'json',
                success: function (data) {
                   showInfo(data.data);
                    $.liaison.searchLiaison();
                }
            });
        }
    }

}
$.workPlan = {
    addWorkReport:function(strTab,id){
        displayInTab3("addWorkReportTab", strTab, {requestUrl: "addWorkReport",data:{workReportId:id,viewType:'edit'},width: "800px",height:500,position:"center"});
    },
    addJobs:function(id,partyId,personWorkId,planPerson,projectLeader,startTime,completeTime){
        displayInTab3("addJobsTab", "任务分配", {
            requestUrl: "addJobs",
            data: {
                workPlanId: id,
                partyId: partyId,
                personWorkId: personWorkId,
                planPerson: planPerson,
                projectLeader: projectLeader,
                startTime:startTime,
                completeTime:completeTime
            },
            width: "800px",
            position: "center"
        });
    },
    feedbackWorkPlan:function(workPlanId,partyId,childWorkPlanId) {
        displayInTab3("FeedbackWorkPlanTab", "反馈工作计划", {
            requestUrl: "feedbackWorkPlan",
            data: {workPlanId:workPlanId,partyId:partyId,childWorkPlanId:childWorkPlanId},
            width: "800px",
            height: "500",
            position: 'center'
        });
    },
    delWorkReport:function(id){
        if(confirm("是否确认删除？")){
            $.ajax({
                type: 'post',
                url: "delWorkReport",
                data:{workReportId:id},
                async: true,
                dataType: 'json',
                success: function (content) {
                    showInfo("删除成功");
                    $.ajax({
                        type: 'post',
                        url: "searchWorkReportList",
                        async: true,
                        dataType: 'html',
                        success: function (content) {
                            $("#workReportList").html(content);
                        }
                    });
                }
            });
        }
    },
    searchWorkReportList:function(){
        $.ajax({
            type: 'post',
            url: "searchWorkReportList",
            data:{num:$("#num").val(),name:$("#name").val(),party:$("input[name='party']").val(),status:$("#status").val(),type:$("#type").val(),leader:$("input[name='leader").val(),executor:$("input[name='executor").val(),process:$("#process").val()},
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#workReportList").html(content);
            }
        });
    },
/*    feedbackWorkPlan:function(type){
        displayInTab3("FeedbackWorkPlanTab", "反馈工作计划", {
            requestUrl: "feedbackWorkPlan",
            data: {feedbackType: type},
            width: "800px",
            height: "500",
            position: 'center'
        });
    },*/
    commitReport:function(id){
        displayInTab3("commitReportTab", "报告详细信息及反馈", {
            requestUrl: "commitReport",
            data:{workReportId:id,viewType:'edit'},
            width: "800px",
            height: "500",
            position: "center"
        });
    },
    browseReport:function(id){
        displayInTab3("commitReportTab", "工作报告浏览", {
            requestUrl: "browseReport",
            data:{workReportId:id,viewType:'show'},
            width: "800px",
            height: "500",
            position: "center"
        });
    },
    workPlanCreate:function(workPlanStatus){
        var flag = true;
        var Str="";
        var startTime= $("#WorkPlan_startTime").val();
        var completeTime= $("#WorkPlan_completeTime").val();
        var arr=startTime.split("-");
        var startdate=new Date(arr[0],arr[1]-1,arr[2]);
        var startTimes=startdate.getTime();
        var arr1=completeTime.split("-");
        var completedate=new Date(arr1[0],arr1[1]-1,arr1[2]);
        var completeTimes=completedate.getTime();
        $("[id^='startTime_']").each(function () {
            var time = this.value;
            var arrs=time.split("-");
            var startDate1=new Date(arrs[0],arrs[1]-1,arrs[2]);
            var startTimes1=startDate1.getTime();
            var endTime;
            if(workPlanStatus!="1"){
                var a = $(this).parent("span").siblings("span").find("input").val();
                var arrs1=a.split("-");
                var endDate=new Date(arrs1[0],arrs1[1]-1,arrs1[2]);
                endTime=endDate.getTime();
            }else{
                var a = $(this).closest("td").find("input[name^=endTime]").val();
                var arrs1=a.split("-");
                var endDate=new Date(arrs1[0],arrs1[1]-1,arrs1[2]);
                endTime=endDate.getTime();
            }

            if(startTimes1<startTimes||startTimes1>completeTimes||startTimes1>endTime){
                Str=Str+"执行人开始日期必须在项目时间范围内,并且必须小于其执行人结束日期!";
                flag = false;
				return;
            }
        });
		if(!flag){
            showInfo(Str);
            return;
        }
        $("[id^='endTime_']").each(function () {
            var time = this.value;
            var arrs=time.split("-");
            var endDate1=new Date(arrs[0],arrs[1]-1,arrs[2]);
            var endTime1=endDate1.getTime();
            var startTime;
            if(workPlanStatus!="1"){
                var a = $(this).parent("span").siblings("span").find("input").val();
                var arrs1=a.split("-");
                var startDate=new Date(arrs1[0],arrs1[1]-1,arrs1[2]);
                startTime=startDate.getTime();
            }else{
                var a = $(this).closest("td").find("input[name^=startTime]").val();
                var arrs1=a.split("-");
                var startDate=new Date(arrs1[0],arrs1[1]-1,arrs1[2]);
                startTime=startDate.getTime();
            }
            if((endTime1<startTimes)||(endTime1>completeTimes)||(startTime>endTime1)){
                Str=Str+"执行人结束日期必须在项目时间范围内,并且必须大于其执行人开始日期!";
                flag = false;
				return;
            }
        });
		if(!flag){
            showInfo(Str);
            return;
        }
        $("[id^='milestoneTime_o_']").each(function () {
            var time = this.value;
            var arrs=time.split("-");
            var milestoneDate=new Date(arrs[0],arrs[1]-1,arrs[2]);
            var milestoneTime=milestoneDate.getTime();
            if(milestoneTime<startTimes||milestoneTime>completeTimes){
                Str=Str+"里程碑时间必须在项目时间范围内!";
                flag = false;
				return;
            }
        });
        if(!flag){
            showInfo(Str);
            return;
        }
        var options = {
            beforeSubmit: function () {
                var value = $('#WorkPlanForm').validationEngine('validate');
                return value;
            },
            dataType: "json",
            success: function (data) {
                showInfo(data._EVENT_MESSAGE_);
                closeCurrentTab();
                $.workPlan.searchWorPlan();
            },
            url: "saveWorkPlanCreate",
            type: 'post',
            data:{workPlanStatus:workPlanStatus}
        };
        $("#WorkPlanForm").ajaxSubmit(options);
    },
    deleteWorkPlan:function(workPlanId){
        $.ajax({
            type: 'post',
            url: "deleteWorkPlan",
            data:{"workPlanId":workPlanId},
            async: true,
            dataType: 'json',
            success: function (data) {
                showInfo(data.msg);
                closeCurrentTab();
                closeCurrentTab();
                $.workPlan.searchWorPlan();
            }
        });
    },
    searchWorPlan:function(id){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            success: function (context) {
                $("#workPlanSearchResult").html(context);
            },
            url: "searchWorkPlan",
            type: 'post',
            data:{selfWorkPlan:id}
        };
        $("#WorkPlanSearch").ajaxSubmit(options);
    },
    /*
    * @param workPlanId(工作计划id)
    * @param partyId(子计划跟进人id)
    * @param childWorkPlanId(子计划id)
    * @param curPartyId(子计划所属人的id)
    *
    * */
    addChildWorkPlan:function(workPlanId,partyId,childWorkPlanId,curPartyId) {
        var strTab = "添加子计划";
        if(childWorkPlanId!=null){
            strTab = "修改子计划";
        }
        displayInTab3("ChildWorkPlanTab", strTab, {
            requestUrl: "addChildWorkPlan",
            data:{workPlanId:workPlanId,partyId:partyId,childWorkPlanId:childWorkPlanId,curPartyId:curPartyId},
            width: "800px",
            height:"500",
            position: "center"
        });
    },
    editWorkPlan:function(workPlanId){
        displayInTab3("EditWorkPlanTab", "修改工作计划", {
            requestUrl: "editWorkPlan",
            data:{workPlanId:workPlanId},
            width: "800px",
            height:"500",
            position: "center"
        });
    },
    copyWorkPlan:function(workPlanId){
        closeCurrentTab();
        displayInTab3("CopyWorkPlanTab", "复制工作计划", {
            requestUrl: "copyWorkPlan",
            data:{workPlanId:workPlanId},
            width: "800px",
            height:"500",
            position: "center"
        });
    },
    editPerformance:function(workPlanId){
        $.ajax({
            type: 'post',
            url: "editPerformance",
            data: {"workPlanId": workPlanId},
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#feedbackList").html(content);
            }
        });
    },
    toGrade:function(workPlanId,partyId){
        displayInTab3("ToGradeTab","绩效评定", {requestUrl: 'toGrade',data:{workPlanId:workPlanId,partyId:partyId},width: "800px",height:600,position:'center'});
    },
    saveGrade:function(workPlanId){
        $('textarea[name="performanceRemark"]').val(templateRemark.html());
        var options = {
            beforeSubmit:function () {
                return true;
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.msg);
                $.workPlan.editPerformance(workPlanId);
                $.workPlan.searchWorPlan();
                closeCurrentTab();
            },
            url:"saveGrade",
            type:'post'
        };
        $("#workPlanGradeForm").ajaxSubmit(options);
    },
    deleteSelected:function(){
        var options = {
            beforeSubmit:function () {
                return true;
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.data);
            },
            url:"deleteFeedback",
            type:'post'
        };
        $("#deleteFeedbackForm").ajaxSubmit(options);
    }
}

$.transaction = {
    addTransaction: function(param){
        var strTab = "增加事务"
        if(param=="edit"){
            strTab = "修改事务";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "TransactionAddAjax",data:{param:param},width: "800px",position:"center"});
    },
    addTransactionType: function(param){
        var strTab = "增加事务类别"
        if(param=="edit"){
            strTab = "修改事务类别";
        }
        displayInTab3("AddTransactionTypeTab", strTab, {requestUrl: "AddTransactionType",data:{param:param},width: "600px", position:"center"});
    },
    addTransactionProgress: function(param){
        var strTab = "增加事务进度"
        if(param=="edit"){
            strTab = "修改事务进度";
        }
        displayInTab3("AddTransactionProgressTab", strTab, {requestUrl: "AddTransactionProgress",data:{param:param},width: "600px", position:"center"});
    },
    transactionInfo:function(){
        displayInTab3("TransactionInfoTab", "事务详细信息及反馈", {requestUrl: "transactionInfo",width: "800px", height:600,position:"center"});
    },
    visitLog:function(strTab){
        closeCurrentTab2();
        displayInTab3("VisitLogTab", "事务："+strTab+"浏览日志", {requestUrl: "visitLogList",width: "800px", position:'top'});
    }
}
$.workLog={
    saveAttentionSubordinate:function(partyId,ifAttentionButton){
        $.ajax({
            type: 'POST',
            url: "saveAttentionSubordinate",
            async: true,
            data:{partyId: partyId,ifAttentionButton:ifAttentionButton},
            dataType: 'json',
            success: function (content) {
               showInfo(content.data)
                $.ajax({
                           type: 'POST',
                           url: "subordinates",
                           async: true,
                           dataType: 'html',
                           success: function (content) {
                            document.getElementById('subordinatesTab').innerHTML = " ";
                                            $("#subordinatesTab").html($(content));
                           }
                       });
            }
        });
    },
    saveSchedule:function(){
        var startHour = $("select[name='scheduleStartDatetime_hour']").val();
        var startMinute = $("select[name='scheduleStartDatetime_minute']").val();
        var endHour = $("select[name='scheduleEndDatetime_hour']").val();
        var endMinute = $("select[name='scheduleEndDatetime_minute']").val();
        if(startHour==endHour){
            if(startMinute>=endMinute){
                showInfo("开始时间必须大于结束时间!");
               return false;
            }
        }
        if(startHour>endHour){
            showInfo("开始时间必须大于结束时间!");
            return false;
        }
        var options = {
            beforeSubmit:function () {
                return $("#ScheduleForm").validationEngine('validate');
            },
            async: true,
            dataType:"html",
            success:function (data) {
                $("#scheduleList").html(data);
                $("#testCalendar").fullCalendar('refetchEvents');
                showInfo($("#workLog_msg").val());
                if($("#workLog_msg").val()!="日程时间重叠，请调整日程时间"){
                    closeCurrentTab2();
                }
            },
            url:"saveWorkLogSchedule",
            type:'post'
        };
        $("#ScheduleForm").ajaxSubmit(options);
    },
    showWorkLog:function(workLogId,workLogDate,type){
        displayInTab3("ShowWorkLogInfo", "日程安排", {
            requestUrl: "ShowWorkLogInfo",
            data: {workLogId: workLogId,workLogDate:workLogDate.substr(0,10),type:type},
            width: "800px",
            position: "center"
        });
    }
}
$.osManager={
    showCreateForm:function(){
        displayInTab3("SubOrganizationTab", "易耗品", {requestUrl: "EditOsManager", data:{}, width: "800px"});
    },
    deleteOsManager:function(id){
        $.ajax({
            type: 'POST',
            url: "deleteOsManager",
            async: true,
            data:{osManagementId: id},
            dataType: 'html',
            success: function (content) {
                $("#ListSubOrgs").html(content);
            }
        });
    },
    createOsManager:function(){
        $("textarea[name='remark']").val(template.html());
        var options = {
            beforeSubmit:function(){
                return $('#EditOsManager').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.osManager.searchOsManager();
            },
            url:"saveOsManager",
            type:'post'
        };
        $("#EditOsManager").ajaxSubmit(options);
    },
    showUpdateForm:function(id){
        displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{osManagementId: id}, width: "800px"});
    },
    showInventoryInfoForm:function(id){
        displayInTab3("SubOrganizationTab", "仓库信息", {requestUrl: "EditInventoryInfo", data:{inventoryInfoId: id}, width: "800px"});
    },
    searchOsManager:function(){
    var options = {
        dataType:"html",
        success:function (content) {
            $("#ListOsManager").html(content);
        },
        url:"ListOsManager",
        type:'post'
    };
    $("#SearchOsManager").ajaxSubmit(options);
}

}

$.workflow = {
    apply: function(id){
        displayInTab3("WorkflowApplyTab", "流程申请", {requestUrl: "ApplyWorkflow", data:{id: id}, width: "900px"});
    },
    approve: function(id){
        displayInTab3("WorkflowApproveTab", "流程审批", {requestUrl: "ApproveWorkflow", data:{id: id}, width: "900px"});
    }
}


$.checkingIn = {
    updateWorkNum:function(param){
        var strTab = "增加班次信息";
        if(param != null && param != ''){
            strTab = "修改班次信息";
        }
        displayInTab3("OrganizationTab", strTab, {requestUrl: "EditListOfWork?listOfWorkId="+param, data:{}, width: "800px"});
    },
    saveListOfWork:function(){
        var options = {
            beforeSubmit:function(){
                return $('#ListOfWorkAddForm').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                closeCurrentTab();
                showInfo(data.returnValue);
                $.checkingIn.searchListOfWork();
            },
            async: true,
            url:"saveListOfWork",
            type:'post'
        };
        $("#ListOfWorkAddForm").ajaxSubmit(options);
    },
    searchListOfWork:function(){
        var options = {
            dataType:"html",
            success:function (content) {
                $("#ListOfWorkList").html(content);
            },
            async: true,
            url:"searchListOfWork",
            type:'post'
        };
        $("#SearchListOfWorkForm").ajaxSubmit(options);

    },
    deleteListOfWork:function(id){
        $.ajax({
            type: 'POST',
            url: "deleteListOfWork",
            async: true,
            data:{listOfWorkId: id},
            dataType: 'json',
            success: function (data) {
                showInfo(data.returnValue);
                $.checkingIn.searchListOfWork();
            }
        });
    },
    updateListOfWorkByWeek:function(id){
        var strTab = "增加班制信息";
        if(id != null && id != ''){
            strTab = "修改班制信息";
        }
        displayInTab3("OrganizationTab", strTab, {requestUrl: "EditListOfWorkByWeek", data:{listOfWorkByWeekId:id}, width:900});
    },
    saveListOfWorkByWeek:function(){
        var flag = false;
        $("[name^='work_']").each(function () {
            var path = this.value;
            if (path!=null && path!='') {
                flag = true;
                return;
            }
        });
        if(!flag){
            showInfo("请至少选择一个班次！");
            return;
        }
        var options = {
            beforeSubmit:function(){
                return $('#ListOfWorkByWeekForm').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.returnValue);
                closeCurrentTab();
                $.checkingIn.searchListOfWorkByWeek();
            },
            async: true,
            url:"saveListOfWorkByWeek",
            type:'post'
        };
        $("#ListOfWorkByWeekForm").ajaxSubmit(options);
    },
    searchListOfWorkByWeek:function(){
        var options = {
            dataType:"html",
            success:function (content) {
                $("#ListOfWorkByWeekList").html(content);
            },
            async: true,
            url:"searchListOfWorkByWeek",
            type:'post'
        };
        $("#searchLisOfWorkByWeekForm").ajaxSubmit(options);
    },
    deleteListOfWorkByWeek:function(id){
        $.ajax({
            type: 'POST',
            url: "deleteListOfWorkByWeek",
            async: true,
            data:{listOfWorkByWeekId: id},
            dataType: 'json',
            success: function (data) {
                showInfo(data.returnValue);
                $.checkingIn.searchListOfWorkByWeek();
            }
        });
    },
    editWorkSchedule: function (id) {
        displayInTab3("EditWorkScheduleTab", "新增排班信息", {requestUrl: "EditWorkSchedule", data: {}, width: "800px"});
    },
    saveWorkSchedule:function(){
		var type = $("#WorkScheduleAddForm_belongTo").val();
        var options = {
            beforeSubmit: function () {
				if(type == "WST_PERSONAL"){
					$("input[name = staff]").attr("class","validate[required]");
				}
				if(type == "WST_DEPARTMENT"){
					$("input[name = department]").attr("class","validate[required]");
				}
                return $('#WorkScheduleAddForm').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.returnValue);
				if(data.returnValue != "周期排班时间重叠，请调整时间后保存"){
					closeCurrentTab();
					$.checkingIn.searchScheduleOfWork();
				}
            },
            async: true,
            url:"saveWorkSchedule",
            type:'post'
        };
        $("#WorkScheduleAddForm").ajaxSubmit(options);
    },
    showCalendar:function(id,dataValue,date){
        var strTab = "新增个人排班";
        if(date != null && date != ''){
            strTab = "修改个人排班";
        }
        //closeCurrentTab();
        displayInTab3("PersonScheduleTab", strTab, {requestUrl: "showCalendar", data:{personalWorkScheduleId:dataValue,staffId:id,date:date}, width: "800px"});
    },
    searchOvertime:function(){
        var options = {
            dataType:"html",
            success:function (content) {
                $("#screenlet_2_col").html(content);
            },
            async: true,
            url:"searchOvertime",
            type:'post'
        };
        $("#OvertimeStatisticsForm").ajaxSubmit(options);
    },
    addOvertime:function(id){
        var strTab = "新增加班";
        if(id != null && '' != id){
            strTab = "修改加班";
        }
        displayInTab3("overtimeTab", strTab, {requestUrl: "AddOvertime", data:{overtimeId:id}, width: "800px"});
    },
    auditorOvertime:function(id){
        displayInTab3("overtimeTab1", "加班审核", {requestUrl: "auditorOvertime", data:{overtimeId:id}, width: "800px"});
    },
    saveAuditOvertime: function(state){
        var options = {
            beforeSubmit: function () {
                return $("#auditOvertimeForm").validationEngine('validate');
            },
            dataType: "json",
            data:{overtimeState: state},
            success: function (data) {
                showInfo(data.data.msg);
                closeCurrentTab();
                $.checkingIn.searchOvertime();
            },
            url: "saveAuditOvertime", 
            type: 'post'
        };
        $("#auditOvertimeForm").ajaxSubmit(options);
    },
    saveOvertime:function(){
		var type = $("#OvertimeForm_overtimeFrom").val();
		var type1 = $("#OvertimeForm_overtimeFrom").html();
        var options = {
            beforeSubmit: function () {
				if(type == 1 || type1 == "个人"){
					$("input[name = staff]").attr("class","validate[required]");
				}
				if(type == 2 || type1 == "组织"){
					$("input[name = department]").attr("class","validate[required]");
				}
                return $('#AddHolidayInfoForm').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.returnValue);
                closeCurrentTab();
                $.checkingIn.searchOvertime();
            },
            async: true,
            url:"saveOvertime",
            type:'post'
        };
        $("#OvertimeForm").ajaxSubmit(options);
    },
    deleteOvertime:function(id){
        if(confirm("确定要删除加班吗？")){
            $.ajax({
                type: 'POST',
                url: "deleteOvertime",
                async: true,
                data:{overtimeId: id},
                dataType: 'json',
                success: function (data) {
                    showInfo(data.returnValue);
                    $.checkingIn.searchOvertime();
                }
            });
        }
    },
    addHolidayInfo:function(id){
        var strTab = "新增假期信息";
        if(id != null && '' != id){
            strTab = "修改假期信息";
        }
        displayInTab3("OrganizationTab", strTab, {requestUrl: "AddHolidayInfo", data:{holidayId:id}, width: "800px"});
    },
    saveHoliday:function(){
		var type = $("#AddHolidayInfoForm_holidayFrom").val();
		var type1 = $("#AddHolidayInfoForm_holidayFrom").html();
        var options = {
            beforeSubmit: function () {
				if(type == 1 || type1 == "个人"){
					$("input[name = staff]").attr("class","validate[required]");
				}
				if(type == 2 || type1 == "组织"){
					$("input[name = department]").attr("class","validate[required]");
				}
                return $('#AddHolidayInfoForm').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.returnValue);
                closeCurrentTab();
                $.checkingIn.searchHoliday();
            },
            async: true,
            url:"saveHoliday",
            type:'post'
        };
        $("#AddHolidayInfoForm").ajaxSubmit(options);
    },
    searchHoliday:function(){
        var options = {
            dataType:"html",
            success:function (content) {
                $("#ListHolidayInfo").html(content);
            },
            async: true,
            url:"searchHoliday",
            type:'post'
        };
        $("#HolidayInfoForm").ajaxSubmit(options);
    },
    lookHolidayInfo:function(holidayId){
        displayInTab3("LookHolidayInfoTab", "查看假期信息", {requestUrl: "LookHolidayInfo", data:{holidayId:holidayId}, width: "800px"});
    },
    deleteHoliday:function(holidayId){
        if(confirm("确定要删除假期吗？")){
            $.ajax({
                type: 'POST',
                url: "deleteHoliday",
                async: true,
                data:{holidayId: holidayId},
                dataType: 'json',
                success: function (data) {
                    showInfo(data.returnValue);
                    $.checkingIn.searchHoliday();
                }
            });
        }
    },
    searchScheduleOfWork:function(){
        var options = {
            dataType:"html",
            success:function (content) {
                $("#WorkScheduleListSubOrgs").html(content);
            },
            async: true,
            url:"searchScheduleOfWork",
            type:'post'
        };
        $("#SearchScheduleOfWorkForm").ajaxSubmit(options);
    },
    saveCheckingIn:function(){
        /*var  a= $("#listOfWorkId").val();
        if(a==null){
            showInfo("请先增加该员工相应班次")
        }*/
        var options = {
            beforeSubmit:function(){
                return $('#AddCheckingInForm').validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
				if(data.returnValue != null || data.returnValue != ""){
					showInfo(data.returnValue);
				}
                closeCurrentTab();
                $.checkingIn.searchCheckingIn();
            },
            async: true,
            url:"saveCheckingIn",
            type:'post'
        };
        $("#AddCheckingInForm").ajaxSubmit(options);
    },
    searcheCheckingInStatistics:function(){
        var options = {
            dataType:"html",
            success:function (content) {
                $("#ListHoliddayInfo").html(content);
            },
            async: true,
            url:"searcheCheckingInStatistics",
            type:'post'
        };
        $("#CheckingStatisticsForm").ajaxSubmit(options);
    },
    showPerson:function(){
        displayInTab3("OrganizationTab", "选择员工", {requestUrl: "ShowPerson", data:{}, width: "800px",position:'center'});
    },
    addChekingInTab:function(data){
        displayInTab3("AddCheckingInTab", "新增考勤信息", {requestUrl: "AddCheckingIn", data:data, width: "800px"});
    },
    selectedStaffChange:function(partyId) {
        var staff = $("#AddCheckingInForm").find("input[name='staff']").val(partyId);
        var checkingInDate = $("#AddCheckingInForm").find("input[name='checkingInDate']").val();
        var data = new Object();
        data.staffId = partyId;
        data.date = checkingInDate;
        $.ajax({
            type: 'POST',
            url: "AddCheckingIn",
            async: true,
            data:data,
            dataType: 'html',
            success: function (content) {
               $("#CheckingInAdd").html(content);
            }
        });
        closeCurrentTab();
    },
    showStaffCheckingIn:function(ids,date,type,title,abnormalCause,checkingInType){
        $.ajax({
            type: 'POST',
            url: "WorkerCheckingIn",
            async: true,
            data:{staffIds:ids,date:date,type:type,title:title,abnormalCause:abnormalCause,checkingInType:checkingInType},
            dataType: 'html',
            success: function (content) {
                $("#WorkerCheckingInTag").html(content);
            }
        });
    },
    searchCheckingIn:function(){
        var startDate=$("input[name='startDate']").val();
        var endDate=$("input[name='endDate']").val();
        if(startDate==null||startDate==''){
            if(endDate!=null && endDate!=''){
                showInfo("开始时间和结束时间要都选，才能使用时间查询")
                return false;
            }
        }else{
            if(endDate==null||endDate==''){
                showInfo("开始时间和结束时间要都选，才能使用时间查询")
                return false;
            }
        }
        var options = {
            dataType:"html",
            success:function (content) {
                $("#ListCheckingInSubOrgs").html(content);
            },
            async: true,
            url:"SearchCheckingInInfoForm",
            type:'post'
        };
        $("#SearchCheckingInInfoForm").ajaxSubmit(options);
    },
    deleteCheckingIn:function(checkingInDate,staff,listOfWorkId,type){
        if(confirm("确定要删除吗？")){
            $.ajax({
                dataType:"json",
                success:function (data) {
                    showInfo(data.returnValue);
                    $.checkingIn.searchCheckingIn();
                },
                async: true,
                url:"deleteCheckingIn",
                type:'post',
                data:{checkingInDate:checkingInDate,staff:staff,listOfWorkId:listOfWorkId,type:type}
            });
        }
    },
    updateAutoWorkSchedule:function(id,departmentIds,listOfWorkIds){
        displayInTab3("SubOrganizationTab", "修改自动排班", {requestUrl: "EditOsManager", data:{}, width: "800px"});
    },
    editWorkSchedule:function(id,type) {
        var data = new Object();
        if(id != null && id != ''){
            data.workCycleScheduleId = id;
            data.type = type;
        }
        displayInTab3("EditWorkScheduleTab", "新增排班信息", {requestUrl: "EditWorkSchedule", data: data, width: "800px"});
    },
    autoScheduling:function(id,departmentIds,listOfWorkIds,startDate,endDate){
        var data = new Object();
        if(id != null || id != ''){
            data.autoWorkScheduleId = id;
            data.departmentIds = departmentIds;
            data.listOfWorkIds = listOfWorkIds;
            data.startDate = startDate;
            data.endDate = endDate;
        }
        displayInTab3("OrganizationTab", "新增自动排班", {requestUrl: "AutoScheduling", data: data, width: "800px"});
    },
    deleteautoSchedule:function(id){
        if(confirm("确定要删除吗？")){
            $.ajax({
                dataType:"json",
                success:function (data) {
                    showInfo(data.returnValue);
                    $.checkingIn.searchScheduleOfWork();
                },
                async: true,
                url:"deleteautoSchedule",
                type:'post',
                data:{autoWorkScheduleId:id}
            });
        }
    },
    deleteWorkCycleSchedule:function(id){
        if(confirm("确定要删除吗？")){
            $.ajax({
                dataType:"json",
                success:function (data) {
                    showInfo(data._EVENT_MESSAGE_);
                    $.checkingIn.searchScheduleOfWork();
                },
                async: true,
                url:"deleteWorkCycleSchedule",
                type:'post',
                data:{workCycleScheduleId:id}
            });
        }
    },
    deletePersonalWorkSchedule:function(id){
        if(confirm("确定要删除吗？")){
            $.ajax({
                dataType:"json",
                success:function (data) {
					console.log(data.returnValue);
                    showInfo(data.returnValue);
                    $.checkingIn.searchScheduleOfWork();
                },
                async: true,
                url:"deletePersonalWorkSchedule",
                type:'post',
                data:{personalWorkScheduleId:id}
            });
        }
    },
    staffChekingInCalendar:function(){
        displayInTab3("StaffChekingInCalendarTab", "签到日历", {requestUrl: "staffChekingInCalendar", data: {}, width: "800px",position:'center'});
    },
    showWorkRegime: function (listOfWorkByWeekId) {
        displayInTab3("OrganizationTab", "查看班制信息", {requestUrl: "WorkRegimeShow", data: {listOfWorkByWeekId:listOfWorkByWeekId}, width: "800px"});
    }
}

$.workContactList = {

    saveWorkContactList:function(){
        var a = $("input[name='ifApprove']").attr("checked");
        if(a=="checked"){
            var auditorPerson = $.trim($("input[name=auditorPerson]").attr("value"));
            if(auditorPerson == ""){
                showInfo("审核人不能为空！");
                return false;
            }
        }
        var b = $("input[name='ifResponse']").attr("checked");
        if(b=="checked"){
            var responseTime = $.trim($("input[name=responseTime_i18n]").attr("value"));
            if(responseTime == ""){
                showInfo("回复时间不能为空！");
                return false;
            }
        }
        $('textarea[name="content"]').val(templateForWorkContactList.html());
        var options = {
            beforeSubmit: function () {
                return $("#perfExamEditForm").validationEngine('validate');
            },
            dataType:"json",
            success:function (data) {
                showInfo(data.data.msg)
                closeCurrentTab2();
                $.ajax({
                    type: 'POST',
                    url: "searchLiaison",
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        document.getElementById('LiaisonCheckList').innerHTML = " ";
                        $("#LiaisonCheckList").html($(content));
                    }
                });
            },
            url:"saveWorkContactList",
            type:'post'
        };
        $("#perfExamEditForm").ajaxSubmit(options);
    }
}

$.typeManagement = {

    saveTypeManagement:function(){
        var name=$.trim($("input[name=typeManagement]").attr("value"));
        if(name==""){
            showInfo("添加类型不能为空！");
            return false;
        }
        var options = {
            beforeSubmit:function () {
                return true;
            },
            dataType:"json",
            success:function (data) {
                $.ajax({
                    type: 'POST',
                    url: "TypeManagement",
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        document.getElementById('TypeManagement').innerHTML = " ";
                        $("#TypeManagement").html($(content));
                    }
                });
                /*var */
                $("#contactListType").append("<option value='" + data.data.Id + "'>" + data.data.typeManagement + "</option>");
                /*$.ajax({
                    type: 'POST',
                    url: "showSendContactList",
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        document.getElementById('showSendContactList').innerHTML = " ";
                        $("#showSendContactList").html($(content));
                    }
                });*/
                showInfo(data.data.msg);
            },
            url:"AddTypeManagement",
            type:'post'
        };
        $("#AddTypeManagement").ajaxSubmit(options);
    }
}




$.file = {
    sendFiles:function(){
        var personIds =  $("input[name='testStaffSelect']").val();
        var fileIdList = $("#fileListH").val();
        var type = $("#type").val();
        var photoId = $("#attachment_photographic_value").val();
        if(null != photoId && "" != photoId){
            if(null != fileIdList && "" != fileIdList){
                fileIdList = fileIdList + "," + photoId;
            }else{
                fileIdList = photoId;
            }
        }
        if(null == personIds || personIds.length <= 0){
            alert("请选择接收人！");
            return;
        }
        if(null == fileIdList || fileIdList.length <= 0){
            alert("请选择文件！");
            return;
        }
        $.ajax({
            type: 'POST',
            url: "sendFileList",
            async: true,
            data:{personIds: personIds,fileIdList:fileIdList,type:type},
            dataType: 'json',
            success: function (data) {
                showInfo("发送成功");
                closeCurrentTab2();
            }
        });
    }
}



function showTypeManagement(){
    displayInTab3("TypeManagement", "类型管理", {requestUrl: "TypeManagement", data:{}, width: "500px"});
}


function showUpdateForm(id){
    displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{osManagementId: id}, width: "800px"});
}

function createOsManager() {
    var options = {
        dataType:"json",
        success:function (data) {
            closeCurrentTab();
            showInfo(data.data.msg);
        },
        url:"saveOsManager",
        type:'post'
    };
    $("#EditOsManager").ajaxSubmit(options);
}

function deleteOsManager(id) {
    $.ajax({
        type: 'POST',
        url: "deleteOsManager",
        async: true,
        data:{osManagementId: id},
        dataType: 'html',
        success: function (content) {
            $("#ListSubOrgs").html(content);
            /*
             //showInfo(data.data.msg);
             alert(data.data.msg);*/
        }
    });
}

$.inventory ={
    createWarehouseInfo:function(){
        displayInTab3("SubOrganizationTab", "增加仓库信息", {requestUrl: "createWarehouseInfo", data:{}, width: "800px"});
    },
    deleteInventoryInfo : function (id){
        $.ajax({
            type: 'post',
            url: "deleteInventoryInfo",
            async: true,
            data:{warehouseId: id},
            dataType: 'json',
            success:function (data) {
                console.log(data);
                showInfo(data.msg)
                $.inventory.searchWarehouseInfo();
            },
        });
    },
    editWarehouseInfo:function(id){
        displayInTab3("SubOrganizationTab", "修改仓库信息", {requestUrl: "EditWarehouseInfo", data:{warehouseId: id}, width: "800px"});
    },
    saveWarehouseInfo : function (id){
        var options = {
            beforeSubmit: function () {
                return $("#warehouseInfo").validationEngine('validate');
            },
            dataType: "json",
            data:{warehouseId:id},
            success:function (data) {
                showInfo(data._EVENT_MESSAGE_);
                closeCurrentTab2();
                $.inventory.searchWarehouseInfo();
            },
            url: "saveWarehouseInfo",
            type: 'post'
        };
        $("#warehouseInfo").ajaxSubmit(options);
    },

    searchWarehouseInfo : function(){
        var options = {
            beforeSubmit: function () {
                return $("#SearchInventoryInfo").validationEngine('validate');
            },
            dataType: "html",
            success:function (data) {
                $("#inventoryList").html(data);
            },
            url: "searchWarehouseInfo",
            type: 'post'
        };
        $("#SearchInventoryInfo").ajaxSubmit(options);
    },

    createProductType : function (){
        displayInTab3("SubOrganizationTab", "增加货品类别", {requestUrl: "createProductType", data:{}, width: "800px"});
    },
    editProductTypeInfo:function(id){
        displayInTab3("SubOrganizationTab", "修改货品类别信息", {requestUrl: "EditProductTypeInfo", data:{productTypeId: id}, width: "800px"});
    },
    saveProductType : function(){
        $("textarea[name='note']").val(template.html());
        var max = $("input[name='MaximumSafety']").val();
        var min = $("input[name='MinimumSafety']").val();
        if ((max-min)<0) {
            showError("最大上限需要大于最小下限");
        } else {
            var options = {
                beforeSubmit: function () {
                    return $("#productType").validationEngine('validate');
                },
                dataType: "json",
                success:function (data) {
                    showInfo(data.data.msg)
                    closeCurrentTab2();
                    $.inventory.searchProductTypeInfo();
                },
                url: "saveProductType",
                type: 'post'
            };
            $("#productType").ajaxSubmit(options);
        }
    },
	deleteProductTypeInfo : function (id){
        $.ajax({
            type: 'post',
            url: "deleteProductTypeInfo",
            async: true,
            data:{productTypeId: id},
            dataType: 'json',
            success:function (data) {
                showInfo(data.msg)
                $.inventory.searchProductTypeInfo();
            },
        });
    },
    searchProductTypeInfo : function(){
        var options = {
            beforeSubmit: function () {
                return $("#SearchProductTypeInfo").validationEngine('validate');
            },
            dataType: "html",
            success:function (data) {
                $("#ProductType").html(data);
            },
            url: "ProductTypeList",
            type: 'post'
        };
        $("#SearchProductTypeInfo").ajaxSubmit(options);
    },

    showProductTypeForm:function (id){
    var strTab = "修改货品类别";
    if(id == ""){
        strTab = "增加货品类别";
    }
    displayInTab3("SubOrganizationTab", strTab, {requestUrl: "EditProductType", data:{productCode: id}, width: "800px"});
    },
    addLocation :function(){
        var rows = $("#addLocationTable tr").length;
        var htmlStr = "<tr id='tr_"+rows+"'>" +
            "<td><input type='text' name='locationName'>" +
            "<input type='hidden' name='locationId' value=''> </td>"+
            "<td><a name='removeTr' href='#' onclick='removeTr("+rows+")' title='移除' class='icon-trash'></td>"+
            "</tr>";
        var $tr = $("#addLocationTable tr:last");
        $tr.after(htmlStr);
    }
}
$.customer ={
    showCustomerInfoForm :function (id){
        displayInTab3("SubOrganizationTab", "维护客户信息", {requestUrl: "EditCustomerInfo", data:{customerInfoId:id}, width: "800px"});
    },
    editCustomerInfoForm :function (id){
        displayInTab3("SubOrganizationTab", "修改客户信息", {requestUrl: "EditCustomerInfo", data:{customerInfoId:id}, width: "800px"});
    },
    showCustomerInfo:function (id){
        displayInTab3("SubOrganizationTab", "客户信息详情", {requestUrl: "ShowCustomerInfo", data:{customerInfoId:id}, width: "800px"});
    },
    saveCustomer : function(){
        $("textarea[name='note']").val(template.html());
        var options = {
            beforeSubmit: function () {
                return $("#EditCustomer").validationEngine('validate');
            },
            dataType: "json",
            success:function (data) {
                showInfo(data.data.msg)
                closeCurrentTab2();
                $.customer.searchCustomerInfo();
            },
            url: "saveCustomer",
            type: 'post'
        };
        $("#EditCustomer").ajaxSubmit(options);

    },
   deleteCustomerInfo : function (id){
        $.ajax({
            type: 'post',
            url: "deleteCustomerInfo",
            async: true,
            data:{customerInfoId: id},
            dataType: 'json',
            success:function (data) {
                showInfo(data.data.msg)
                $.customer.searchCustomerInfo();
            },
        });
    },
    searchCustomerInfo : function(){
        var options = {
            dataType: "html",
            success:function (data) {
                $("#ListCustomerInfo").html(data);
            },
            url: "searchCustomerInfo",
            type: 'post'
        };
        $("#SearchCustomerInfo").ajaxSubmit(options);
    },
}
$.InventoryManagement = {
    searchInventoryList : function(){
        var options = {
            dataType: "html",
            success:function (data) {
                $("#InventoryListResult").html(data);
            },
            url: "searchInventoryList",
            type: 'post'
        };
        $("#SearchInventory").ajaxSubmit(options);
    },
    searchReveiveInventoryList : function(){
        var options = {
            dataType: "html",
            success:function (data) {
                $("#InventoryList2").html(data);
            },
            url: "InventoryListInfo",
            type: 'post'
        };
        $("#SearchInventory").ajaxSubmit(options);
    },
    inputInventory : function(id){
        displayInTab3("SubOrganizationTab", "货品入库", {requestUrl: "EditInputInventory", data:{osManagementId:id}, width: "800px"});
    },
    outInventory : function(id){
        displayInTab3("SubOrganizationTab", "货品出库", {requestUrl: "EditOutInventory", data:{osManagementId:id}, width: "800px"});
    },
    addReceiveForm : function(){
        displayInTab3("SubOrganizationTab", "增加新的领用单", {requestUrl: "OutInventoryMany", data:{}, width: "800px"});
    },
    saveReceive : function(){
        var options = {
            beforeSubmit: function () {
                return $("#EditReviewList").validationEngine('validate');
            },
            dataType:"json",
            success:function(data){
                showInfo(data._EVENT_MESSAGE_)
                closeCurrentTab2();
                $.InventoryManagement.searchUncheckReveice()
            },
            url:"saveReceive",
            type:'post'
        };
        $("#EditReviewList").ajaxSubmit(options);
    },
    saveInputInventory : function () {
        var options = {
            beforeSubmit: function () {
                return $("#EditInputInventory").validationEngine('validate');
            },
            dataType:"json",
            success:function(data){
                showInfo(data.data.msg);
                closeCurrentTab2();
                $.InventoryManagement.searchOutInventory()
            },
            url:"saveInputInventory",
            type:'post'
        };
        $("#EditInputInventory").ajaxSubmit(options);
    },
    saveOutInventory : function () {
        var repertoryAmount = $("input[name='repertoryAmount']").val();
        var outInventoryAmount = $("input[name='outInventoryAmount']").val();
        if(repertoryAmount<outInventoryAmount){
            showInfo("出库数量不能大于库存量");
            return;
        }

        var options = {
            beforeSubmit: function () {
                return $("#EditOutInventory").validationEngine('validate');
            },
            dataType:"json",
            success:function(data){
                showInfo(data.data.msg);
                closeCurrentTab2();
                $.InventoryManagement.searchOutInventory()
            },
            url:"saveOutInventory",
            type:'post'
        };
        $("#EditOutInventory").ajaxSubmit(options);
    },
    searchOutInventory : function(){
        var options = {
          dataType:"html",
            success:function(data){
                $("#InventoryList").html(data);
            },
            url:"searchInventoryList",
            type:'post'
        };
        $("#SearchInventory").ajaxSubmit(options);
    },
    searchUncheckReveice : function(){
        var options = {
            dataType:"html",
            success:function(data){
                $("#ListReceiveResult").html(data);
            },
            url:"SearchListReceive",
            type:'post'
        };
        $("#SearchReviewList").ajaxSubmit(options);
    },
    searchCheckedInventory : function(){
        var options = {
            dataType:"html",
            success:function(data){
                $("#ListReceiveResult").html(data);
            },
            url:"SearchListReceive",
            type:'post'
        };
        $("#SearchReviewLiat").ajaxSubmit(options);
    },
	moveValue : function(id ,value1,value2,value3,value4,value5,value6,value7){
        var flag = true;
	    $("input[name='osManagementId']").each(function(){
            var osManagementId = $(this).val();
            if(osManagementId == value7){
                showInfo("改货品已经被选中,请选择其他货品");
                flag= false;
                return;
            }
        });
        if(!flag){
            return;
        }
		$("#warehouseName_"+id).val(value1);
		$("#standardName_"+id).html(value2);
		$("#typeName_"+id).html(value3);
		$("#unitName_"+id).html(value4);
		$("#canUseAmount_"+id).html(value6);
		$("#repertoryAmount_"+id).html(value5);
		$("#osManagement_"+id).val(value7);
		closeCurrentTab2();
	},
    checkReceive:function(id){
        displayInTab3("SubOrganizationTab", "审核", {requestUrl: "checkReceive", data:{receiveId:id}, width: "800px"});
    },
    seeReceive:function(id){
        displayInTab3("SubOrganizationTab", "审核", {requestUrl: "seeReceive", data:{receiveId:id}, width: "800px"});
    },
    checkAllow:function(id){
        $("textarea[name='checkNote']").val(template.html());
        var options = {
            dataType:"json",
            data:{receiveId:id , checkNote:$("textarea[name='checkNote']").val()},
            success:function(data){
                showInfo(data.data.msg);
                closeCurrentTab2();
                $.InventoryManagement.searchUncheckReveice();
            },
            url:"checkAllow",
            type:'post'
        };
        $("#CheckReceive").ajaxSubmit(options);
    },
    checkRefuse:function(id){
        $("textarea[name='checkNote']").val(template.html());
        var options = {
            dataType:"json",
            data:{receiveId:id, checkNote:$("textarea[name='checkNote']").val()},
            success:function(data){
                showInfo(data.data.msg);
                closeCurrentTab2();
            },
            url:"checkRefuse",
            type:'post'
        };
        $("#CheckReceive").ajaxSubmit(options);
    },
    delivery : function(id){
        displayInTab3("SubOrganizationTab", "发货", {requestUrl: "delivery", data:{receiveId:id}, width: "800px"});
    },
    saveDelivery : function(id){
        var options = {
            beforeSubmit: function () {
                return $("#deliveryReceive").validationEngine('validate');
            },
            dataType:"json",
            success:function(data){
                showInfo(data._EVENT_MESSAGE_)
                closeCurrentTab2();
                $.InventoryManagement.searchOutInventory()
            },
            url:"saveDelivery",
            type:'post'
        };
        $("#deliveryReceive").ajaxSubmit(options);
    }
}
/*function showInputOrOutInventoryForm(id,url){
    var strTab = "入库"
    if("EditOutInventory"==url){
        strTab = "出库"
    }
    displayInTab3("SubOrganizationTab", strTab, {requestUrl: url, data:{productCode: id}, width: "800px"});

}*/

/*function searchOutInventory(){
    var options = {
        dataType:"html",
        success:function (content) {
            $("#ListSubOrgs").html($(content));
        },
        url:"searchOutInventory",
        type:'post'
    };
    $("#EditOutInventoryMany").ajaxSubmit(options);
}


function showReceiveInfo(id){
    displayInTab3("SubOrganizationTab", "领用单明细信息", {requestUrl: "receiveInfo", data:{receiveNum: id}, width: "800px"});

}





function borrowRegisterConfirm(){
    displayInTab3("SubOrganizationTab", "借用预登记", {requestUrl: "borrowRegisterConfirm", data:{}, width: "1000px"});

}

function borrowInfo(){
    displayInTab3("SubOrganizationTab", "资产借用单详情", {requestUrl: "borrowInfo", data:{}, width: "800px"});

}

function addOutInventoryMany(){
    $.ajax({
        type: 'POST',
        url: "AddOutInventoryMany",
        async: true,
        dataType: 'html',
        success: function (content) {
            $("#ListSubOrgs").html($(content));
        }
    });
}


function addReceiveForm(){
    displayInTab3("SubOrganizationTab", "增加新的领用单", {requestUrl: "OutInventoryMany", data:{}, width: "800px"});
}

function sendOut(){
    displayInTab3("SubOrganizationTab", "领用单明细信息", {requestUrl: "sendOut", data:{}, width: "800px"});
}
*/
