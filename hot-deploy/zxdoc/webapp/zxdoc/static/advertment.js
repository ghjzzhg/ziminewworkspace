/**
 * Created by Administrator on 2016/10/13.
 */
$.Advertment= {
    deleteAdvertment: function (AdvertId) {
        if (confirm("确定删除该项吗?")) {
            $.ajax({
                type: 'POST',
                url: "deleteAdvert",
                async: false,
                dataType: 'json',
                data: {AdvertId: AdvertId},
                success: function (data) {
                    showInfo("删除成功");
                    oTable.ajax.reload();
                }
            });
        }
    },
    editAdvertment: function (AdvertId) {
        displayInLayer('修改', {
            requestUrl: 'EditAdvert?AdvertId=' + AdvertId, height: '80%', width: 700, end: function () {
                oTable.ajax.reload();
            }
        })
    },
    updateAdvertmentSatus: function (AdvertId, status) {
        var msg = "禁用";
        if(status == "0"){
            msg = "启用"
        }
        if (confirm("确定" + msg + "该项吗？")) {
            $.ajax({
                type: 'POST',
                url: "updateAdvertmentSatus",
                async: false,
                dataType: 'json',
                data: {AdvertId: AdvertId, status: status},
                success: function (data) {
                    showInfo(msg + "成功");
                    oTable.ajax.reload();
                }
            });
        }
    }
}
