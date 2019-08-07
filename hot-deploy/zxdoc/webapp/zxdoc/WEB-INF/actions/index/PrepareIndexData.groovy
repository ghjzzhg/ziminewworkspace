import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

String _ERROR_MESSAGE_ = parameters._ERROR_MESSAGE_;
List<GenericValue> allCates = from("Enumeration").where(EntityCondition.makeCondition("enumTypeId", EntityOperator.LIKE, "POLICY_CATEGORY%")).orderBy("sequenceId").queryList();
List<GenericValue> topCategories = [];
List<GenericValue> subCategories = [];
for (GenericValue category : allCates) {
    String enumTypeId = category.getString("enumTypeId")
    if(enumTypeId.equals("POLICY_CATEGORY")){
        topCategories.add(category);
    }else{
        /*List<GenericValue> data = subCategories.get(enumTypeId);
        if(data == null){
            data = new ArrayList<>();
            subCategories.put(enumTypeId, data);
        }*/
        subCategories.add(category);
    }
}
context.topCategories = topCategories;
context.subCategories = subCategories;

List<GenericValue> files = from("TblPolicyFiles").queryList();
Map<String, List<GenericValue>> filesMap = files.groupBy {x -> x.getString("category")};
context.policyFiles = filesMap;
context.error = _ERROR_MESSAGE_;

//1.注册企业（未被禁用的企业类型主账户数目）
int companyCounts = EntityQuery.use(delegator).from("BasicGroupInfo").where("roleTypeId","CASE_ROLE_OWNER").queryCount();
context.company = companyCounts;

//2.服务机构（未被禁用，且账户类型不为企业的公司）
int serverCounts = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.NOT_EQUAL,"CASE_ROLE_OWNER")).queryCount();
context.server = serverCounts;

//3.完成项目（即已经归档的case）
int caseCounts = EntityQuery.use(delegator).from("TblCase").where("status","CASE_STATUS_ARCHIVED").queryCount();
context.caseCounts = caseCounts;

//4.资料分享
int libraryCounts = EntityQuery.use(delegator).from("TblLibrary").where("status","Y").queryCount();
context.library = libraryCounts;