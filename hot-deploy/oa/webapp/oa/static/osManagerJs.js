function searchOsManager() {
        var options = {
            dataType:"html",
            success:function (content) {
                $("#ListSubOrgs").html(content);
            },
            url:"ListOsManager",
            type:'post'
        };
        $("#SearchOsManager").ajaxSubmit(options);
}

function showCreateForm(){
    displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{}, width: "800px"});
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


function showInventoryInfoForm(id){
    displayInTab3("SubOrganizationTab", "仓库信息", {requestUrl: "EditInventoryInfo", data:{inventoryInfoId: id}, width: "800px"});
}

function deleteInventoryInfo(id){
    alert("aa");
    $.ajax({
        type: 'POST',
        url: "deleteInventoryInfo",
        async: true,
        data:{inventoryInfoId: id},
        dataType: 'html',
        success: function (content) {
            $("#content-main-section").html($(content));
        }
    });
}


function showCustomerInfoForm(id){
    displayInTab3("SubOrganizationTab", "维护客户信息", {requestUrl: "EditCustomerInfo", data:{customerInfoId: id}, width: "800px"});

}

function showProductTypeForm(id){
    var strTab = "修改货品类别";
    if(id == ""){
        strTab = "增加货品类别";
    }
    displayInTab3("SubOrganizationTab", strTab, {requestUrl: "EditProductType", data:{productCode: id}, width: "800px"});

}

function showInputOrOutInventoryForm(id,url){
    var strTab = "入库"
    if("EditOutInventory"==url){
        strTab = "出库"
    }
    displayInTab3("SubOrganizationTab", strTab, {requestUrl: url, data:{productCode: id}, width: "800px"});

}

function searchOutInventory(){
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


function showFixedAssetsInfoForm(id){
    displayInTab3("SubOrganizationTab", "固定资产", {requestUrl: "FixedAssetsInfoForm", data:{productCode: id}, width: "800px"});

}

function borrowRegister(){
    displayInTab3("SubOrganizationTab", "借用预登记", {requestUrl: "borrowRegister", data:{}, width: "800px"});

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



function addFixedAssets(){
    displayInTab3("SubOrganizationTab", "固定资产增加", {requestUrl: "AddFixedAssets", data:{}, width: "800px"});
}

