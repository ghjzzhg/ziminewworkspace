import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by gf on 2016/10/12.
 * 广告设置
 */
public Map<String,Object> createAdvert(){
    /*由ServiceUtil类的returnSuccess()方法返回一个Map对象*/
    Map<String,Object> success = ServiceUtil.returnSuccess();
    //创建广告
    /*主键*/
    String AdvertId = delegator.getNextSeqId("TblAdvert");
    /* 广告标题*/
    String ruleName = context.get("ruleName");
    /*起始时间*/
    Date maxTimes = context.get("maxTimes");
    /*终止时间*/
    Date score = context.get("score");
    /*删除标记*/
    String hasdelete = "1";
    /*投放范围*/
    List<String> ranges = context.get("range");
    String rangeData = "";
    //创建投放范围
    for (String range : ranges) {
        rangeData = range + ",";
    }
    /*广告内容*/
    String ticketContent = context.get("ticketContent");
    GenericValue Advert = delegator.makeValue("TblAdvert");

    if(score.getTime() >= maxTimes.getTime()) {
        Advert.setString("AdvertId", AdvertId);
        Advert.setString("ruleName", ruleName);
        Advert.setString("hasdelete", hasdelete);
        Advert.set("maxTimes", new java.sql.Date(maxTimes.getTime()));
        Advert.set("score", new java.sql.Date(score.getTime()));
        Advert.setString("ranges", rangeData);
        Advert.setString("ranges", rangeData);
        Advert.setString("lockAdvert", "1");
        Advert.setString("description", ticketContent);
        delegator.create(Advert);

        success.put("data", "发布成功");
    }else{
        success.put("data", "终止时间不能小于开始时间");
    }
    return success;
}

public Map<String,Object> deleteAdvert()
{
    Map<String,Object> success = ServiceUtil.returnSuccess();
    String AdvertId = context.get("AdvertId");
    Map planetValues = UtilMisc.toMap("AdvertId",AdvertId);
    GenericValue Advert = delegator.findByPrimaryKey("TblAdvert",planetValues);
    Advert.put("hasdelete","0");
    Advert.store();
    return success;
}

public Map<String,Object> saveAdvert()
{
    Map<String,Object> success = ServiceUtil.returnSuccess();
    String AdvertId = context.get("AdvertId");
    /* 广告标题*/
    String ruleName = context.get("ruleName");
    /*起始时间*/
    Date maxTimes = context.get("maxTimes");
    /*终止时间*/
    Date score = context.get("score");
    /*广告内容*/
    String ticketContent = context.get("ticketContent");
    /*投放范围*/
    List<String> ranges = context.get("range");
    String rangeData = "";
    //创建投放范围
    for (String range : ranges) {
        rangeData = range + ",";
    }
    Map planetValues = UtilMisc.toMap("AdvertId",AdvertId);
    GenericValue Advert = delegator.findByPrimaryKey("TblAdvert",planetValues);
    Advert.put("ruleName",ruleName);
    Advert.put("maxTimes",maxTimes);
    Advert.put("score",score);
    Advert.put("ranges",rangeData);
    Advert.put("description", ticketContent);
    Advert.store();
    return success;
}

public Map<String, Object> updateAdvertmentSatus(){
    Map<String,Object> success = ServiceUtil.returnSuccess();

    Map planetValues = UtilMisc.toMap("AdvertId",context.get("AdvertId"));
    GenericValue advert = delegator.findByPrimaryKey("TblAdvert",planetValues);
    if(UtilValidate.isNotEmpty(advert)){
        String status = context.get("status");
        if(status.equals("0")){
            List list = new ArrayList();
            list.add(EntityCondition.makeCondition("lockAdvert", EntityOperator.NOT_EQUAL, "1"));
            list.add(EntityCondition.makeCondition("hasdelete", EntityOperator.EQUALS, "1"));
            List<GenericValue> unLockAdvertList = EntityQuery.use(delegator).select().from("TblAdvert").where(list).queryList();
            for(GenericValue genericValue : unLockAdvertList){
                genericValue.put("lockAdvert", "1");
            }
            delegator.storeAll(unLockAdvertList);
        }
        advert.put("lockAdvert", status);
        advert.store();
    }
    return success;
}