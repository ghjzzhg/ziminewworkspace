$.scoreManage = {
    addScoreRule: function () {
        var options = {
            type: 'post',
            async: false,
            dataType: 'json',
            url: "addScoreRule",
            beforeSubmit: function () {
                return $("#scoreRule").validationEngine("validate");
            },
            success: function () {
                showInfo("积分策略新建成功！");
                closeCurrentTab();
            },
        };
        $("#scoreRule").ajaxSubmit(options);
    },
    editScoreRule: function (id) {
        displayInLayer("修改积分策略", {
            requestUrl: 'EditScoreRule?scoreRuleId=' + id, height: 450, width: 300, end: function () {
                templateTable.ajax.reload();
            }
        })
    },
    saveScoreRule: function () {
        var options = {
            type: 'post',
            async: false,
            dataType: 'json',
            url: "saveRule",
            beforeSubmit: function () {
                return $("#scoreRule").validationEngine("validate");
            },
            success: function () {
                showInfo("修改成功！");
                closeCurrentTab();
            },
        };
        $("#scoreRule").ajaxSubmit(options);
    },
    removeScoreRule: function (scoreRuleId) {
        if(confirm("确认是否删除积分策略?")) {
            $.ajax({
                type: 'post',
                url: 'removeScoreRule',
                async: false,
                dataType: 'json',
                data: {scoreRuleId: scoreRuleId},
                success: function () {
                    showInfo("删除成功！");
                    displayInside("ScoreRules");
                }
            })
        }
    },
    editUserScores: function (id) {
        displayInLayer("修改用户积分", {
            requestUrl: 'EditUserScore?userLoginId=' + id, width:360, height:500, end: function () {
                templateTable.ajax.reload();
            }
        })
    },
    saveUserScore: function () {
        var options = {
            beforeSubmit: function () {
                return $("#editUserScore").validationEngine("validate");
            },
            type: 'post',
            async: false,
            dataType: 'json',
            url: 'saveUserScore',
            success: function () {
                showInfo("保存成功！");
                closeCurrentTab();
            },
        };
        $("#editUserScore").ajaxSubmit(options);
    },
    userScoreHistory:function(id){
        displayInLayer2("积分记录", {
            requestUrl: 'UserScoreHistory?userLoginId=' + id, width:600, height:600
        })
    },
    searchUserScore:function(){
        templateTable.ajax.reload();
    },
    searchScoreHistory:function(){
        templateTable.ajax.reload();
    },
    deliverScore: function () {
        var options = {
            beforeSubmit: function () {
                return $("#editUserScore").validationEngine("validate");
            },
            type: 'post',
            async: false,
            dataType: 'json',
            url: 'deliverScore',
            success: function () {
                showInfo("赠送成功！");
                closeCurrentTab();
            },
        };
        $("#editUserScore").ajaxSubmit(options);
    }
}