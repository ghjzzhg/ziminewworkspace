assetsId = parameters.get("assetsId");
//查询资产
result = from("FixedAssets").where("assetsId", assetsId).queryOne();
context.fixedAssets = result;
