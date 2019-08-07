$.partner = {
    addPartner: function (flag) {
        var options = {
            beforeSubmit: function () {
                var validation = $('#partnerForm').validationEngine('validate');
                return validation;
            },
            dataType: "json",
            success: function (data) {
                showInfo("客户创建成功!");
                closeCurrentTab();
                if(flag != "returnPage"){
                    displayInside('ListPartners');
                }
            },
            error: function (xhr, status, errMsg) {
                // showError(xhr.responseJSON._ERROR_MESSAGE_);
            },
            url: 'addPartner', // override for form's 'action' attribute
            type: 'post',        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#partnerForm").ajaxSubmit(options);
    },
    searchPartner: function () {
        templateTable.ajax.reload();
    },
    deletePartner: function (partyId,id) {
        if (confirm("确定删除该客户吗?")) {
            $.ajax({
                type: 'post',
                async: false,
                url: 'deletePartner',
                dataType: 'json',
                data: {partyId: partyId, id: id},
                success: function () {
                    showInfo("客户删除成功!");
                    displayInside("ListPartners");
                }
            })
        }
    },
    editPartner: function (partyId,id) {
        displayInLayer('修改客户', {
            requestUrl: 'NewPartner',data:{partyId:partyId,id:id},height: "70%", width: 800, end: function () {
                templateTable.ajax.reload();
            }
        })
    },
    showPartner: function (partyId,id) {
        displayInLayer('客户详情', {
            requestUrl: 'ViewPartner', data:{partyId:partyId,id:id},height: "60%", width: "800px", end: function () {
                templateTable.ajax.reload();
            }
        })
    },
    savePartner: function () {
        var options = {
            beforeSubmit: function () {
                var validation = $('#partnerForm').validationEngine('validate');
                return validation;
            },
            dataType: "json",
            success: function (data) {
                showInfo("客户修改成功!");
                closeCurrentTab();
            },
            error: function (xhr, status, errMsg) {
                showError(xhr.responseJSON._ERROR_MESSAGE_);
            },
            url: 'savePartner', // override for form's 'action' attribute
            type: 'post',        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#partnerForm").ajaxSubmit(options);
    },
}