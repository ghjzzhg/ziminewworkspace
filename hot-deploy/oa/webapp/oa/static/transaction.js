$.transaction = {
    addTransaction: function(param){
        var strTab = "增加事务"
        if(param=="edit"){
            strTab = "修改事务";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "TransactionAddAjax",data:{param:param},width: "800px", position:'top'});
    },
    addTransactionType: function(param){
        var strTab = "增加事务类别"
        if(param=="edit"){
            strTab = "修改事务类别";
        }
        displayInTab3("AddTransactionTypeTab", strTab, {requestUrl: "AddTransactionType",data:{param:param},width: "600px", position:'top'});
    },
    addTransactionProgress: function(param){
        var strTab = "增加事务进度"
        if(param=="edit"){
            strTab = "修改事务进度";
        }
        displayInTab3("AddTransactionProgressTab", strTab, {requestUrl: "AddTransactionProgress",data:{param:param},width: "600px", position:'top'});
    },
    transactionInfo:function(){
        displayInTab3("TransactionInfoTab", "事务详细信息及反馈", {requestUrl: "transactionInfo",width: "800px", position:'top'});
    },
    visitLog:function(strTab){
        closeCurrentTab2();
        displayInTab3("VisitLogTab", "事务："+strTab+"浏览日志", {requestUrl: "visitLogList",width: "800px", position:'top'});
    }
}