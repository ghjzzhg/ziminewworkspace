import javolution.util.FastList
import javolution.util.FastMap

bumphNoticeSignInStatusList = FastList.newInstance();

for(int i=0;i<10;i++){
    bumphNoticeSignInStatusMap = FastMap.newInstance();
    bumphNoticeSignInStatusMap.put("q","人事部");
    bumphNoticeSignInStatusMap.put("w","张楚");
    bumphNoticeSignInStatusMap.put("e","技术总监");
    bumphNoticeSignInStatusMap.put("r","2015-5-28 14:10:34");
    bumphNoticeSignInStatusMap.put("t","准备签收");
    bumphNoticeSignInStatusMap.put("y","仍未浏览");
    bumphNoticeSignInStatusList.add(bumphNoticeSignInStatusMap)
}
context.bumphNoticeSignInStatusList = bumphNoticeSignInStatusList;


