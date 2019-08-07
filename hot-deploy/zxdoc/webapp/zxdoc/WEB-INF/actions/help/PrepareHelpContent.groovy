import org.apache.commons.lang.math.NumberUtils
import org.ofbiz.base.util.UtilValidate

String fileIds = parameters.fileIds;
List<String> fileIdResultArray = new ArrayList<>();
if(UtilValidate.isNotEmpty(fileIds)){
    String[] fileIdArray = fileIds.split(",");
    for (String fileId : fileIdArray) {
        if(fileId.contains("..")){
            String[] range = fileId.split("\\.\\.");
            String min = range[0];
            String max = range[1];
            if(NumberUtils.isDigits(min) && NumberUtils.isDigits(max)){
                int minIndex = Integer.parseInt(min);
                int maxIndex = Integer.parseInt(max);
                for(int i = minIndex; i <= maxIndex; i ++){
                    fileIdResultArray.add(i);
                }
            }
        }else{
            fileIdResultArray.add(fileId);
        }
    }
}
context.files = fileIdResultArray;