import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

//赠送对象
String partyIdTo =parameters.get("partyIdTo");
GenericValue party = EntityQuery.use(delegator).from("Person").where("partyId", partyIdTo).queryOne();
if (party != null) {
    context.partyName = party.getString("fullName");
}

int userScoreNo = 0;
//查找用户积分
GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", userLogin.getString("partyId"))).queryOne();
if(userScore != null){
    userScoreNo = Integer.parseInt(userScore.get("scoreAvailable").toString());
}

context.maxScore = userScoreNo;