import org.ofbiz.content.content.UploadFileService
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

//��Ŀ¼·��������ָ������·�������� /var/www/attached/
String rootPath =  request.getSession().getServletContext().getRealPath("/");
//��Ŀ¼URL������ָ������·�������� http://www.yoursite.com/attached/
String rootUrl  = request.getContextPath()+"/";
//��ȡ�û��˺�
GenericValue userLogin = context.get("userLogin");
//ƴ�����·��
String savePath = UploadFileService.getSavePath()+"/"+userLogin.get("partyId") + "/";
String root ="/ckfinder/uploadSendFile/" + userLogin.get("partyId") + "/";
//ͼƬ��չ��
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

//������ʽ��name or size or type
String order = request.getParameter("order") != null ? request.getParameter("order").toLowerCase() : "name";

//������ʹ��..�ƶ�����һ��Ŀ¼
if (path.indexOf("..") >= 0) {
    out.println("Access is not allowed.");
    return;
}

//Ŀ¼�����ڻ���Ŀ¼
File currentPathFile = new File(currentPath);
if(!currentPathFile.isDirectory()){
    currentPathFile.mkdirs();
}

//����Ŀ¼ȡ���ļ���Ϣ
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
