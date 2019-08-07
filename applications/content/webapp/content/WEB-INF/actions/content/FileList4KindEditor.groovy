import org.ofbiz.content.content.UploadFileService
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

//根目录路径，可以指定绝对路径，比如 /var/www/attached/
String rootPath =  request.getSession().getServletContext().getRealPath("/");
//根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/
String rootUrl  = request.getContextPath()+"/";
//获取用户账号
GenericValue userLogin = context.get("userLogin");
//拼接相对路径
String savePath = UploadFileService.getSavePath()+"/"+userLogin.get("partyId") + "/";
String root ="/ckfinder/uploadSendFile/" + userLogin.get("partyId") + "/";
//图片扩展名
String[] fileTypes = ["gif","jpg", "jpeg", "png", "bmp"] as String[];

String path = request.getParameter("path") != null ? request.getParameter("path") : "";
//String currentPath = rootPath + path;
String currentPath = savePath;
//String currentUrl = rootUrl + path;
String currentUrl = root;
String currentDirPath = path;
String moveupDirPath = "";
if (!"".equals(path)) {
    String str = currentDirPath.substring(0, currentDirPath.length() - 1);
    moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
}

//排序形式，name or size or type
String order = request.getParameter("order") != null ? request.getParameter("order").toLowerCase() : "name";

//不允许使用..移动到上一级目录
if (path.indexOf("..") >= 0) {
    out.println("Access is not allowed.");
    return;
}

//目录不存在或不是目录
File currentPathFile = new File(currentPath);
if(!currentPathFile.isDirectory()){
    currentPathFile.mkdirs();
}

//遍历目录取的文件信息
List<Hashtable> fileList = new ArrayList<Hashtable>();
if(currentPathFile.listFiles() != null) {
    for (File file : currentPathFile.listFiles()) {
        Hashtable<String, Object> hash = new Hashtable<String, Object>();
        String fileName = file.getName();
        if(file.isDirectory()) {
            hash.put("is_dir", true);
            hash.put("has_file", (file.listFiles() != null));
            hash.put("filesize", 0L);
            hash.put("is_photo", false);
            hash.put("filetype", "");
        } else if(file.isFile()){
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            hash.put("is_dir", false);
            hash.put("has_file", false);
            hash.put("filesize", file.length());
            hash.put("is_photo", Arrays.<String>asList(fileTypes).contains(fileExt));
            hash.put("filetype", fileExt);
        }
        hash.put("filename", fileName);
        hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
        fileList.add(hash);
    }
}

/*fileList = [[is_dir:false, has_file: false, filesize: 10, is_photo: true, filetype: 'png', filename: 'test'],
            [is_dir:false, has_file: false, filesize: 10, is_photo: false, filetype: 'txt', filename: 'test'],
            [is_dir:false, has_file: false, filesize: 10, is_photo: false, filetype: '', filename: 'test'],
            [is_dir:false, has_file: false, filesize: 10, is_photo: true, filetype: 'png', filename: 'test'],
            [is_dir:true, has_file: false, filesize: 10, is_photo: true, filetype: 'png', filename: 'test']]*/

request.setAttribute("moveup_dir_path", moveupDirPath);
request.setAttribute("current_dir_path", currentDirPath);
request.setAttribute("current_url", currentUrl);
request.setAttribute("total_count", fileList.size());
request.setAttribute("file_list", fileList);
