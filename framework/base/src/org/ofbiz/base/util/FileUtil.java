/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.util;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.location.ComponentLocationResolver;

/**
 * File Utilities
 *
 */
public class FileUtil {

    public static final String module = FileUtil.class.getName();
    private static final Map<String, String> mFileTypes = new HashMap<String, String>();
    // judge file type by file header content
    static {
        mFileTypes.put("ffd8ffe000104a464946", "jpg"); //JPEG (jpg)
        mFileTypes.put("89504e470d0a1a0a0000", "png"); //PNG (png)
        mFileTypes.put("47494638396126026f01", "gif"); //GIF (gif)
        mFileTypes.put("49492a00227105008037", "tif"); //TIFF (tif)
        mFileTypes.put("424d228c010000000000", "bmp"); //16色位图(bmp)
        mFileTypes.put("424d8240090000000000", "bmp"); //24位位图(bmp)
        mFileTypes.put("424d8e1b030000000000", "bmp"); //256色位图(bmp)
        mFileTypes.put("41433130313500000000", "dwg"); //CAD (dwg)
        mFileTypes.put("3c21444f435459504520", "html"); //HTML (html)
        mFileTypes.put("3c21646f637479706520", "htm"); //HTM (htm)
        mFileTypes.put("48544d4c207b0d0a0942", "css"); //css
        mFileTypes.put("696b2e71623d696b2e71", "js"); //js
        mFileTypes.put("7b5c727466315c616e73", "rtf"); //Rich Text Format (rtf)
        mFileTypes.put("38425053000100000000", "psd"); //Photoshop (psd)
        mFileTypes.put("46726f6d3a203d3f6762", "eml"); //Email [Outlook Express 6] (eml)
        mFileTypes.put("d0cf11e0a1b11ae10000", "doc"); //MS Excel 注意：word、msi 和 excel的文件头一样
        mFileTypes.put("d0cf11e0a1b11ae10000", "vsd"); //Visio 绘图
        mFileTypes.put("5374616E64617264204A", "mdb"); //MS Access (mdb)
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462d312e350d0a", "pdf"); //Adobe Acrobat (pdf)
        mFileTypes.put("2e524d46000000120001", "rmvb"); //rmvb/rm相同
        mFileTypes.put("464c5601050000000900", "flv"); //flv与f4v相同
        mFileTypes.put("00000020667479706d70", "mp4");
        mFileTypes.put("49443303000000002176", "mp3");
        mFileTypes.put("000001ba210001000180", "mpg"); //
        mFileTypes.put("3026b2758e66cf11a6d9", "wmv"); //wmv与asf相同
        mFileTypes.put("52494646e27807005741", "wav"); //Wave (wav)
        mFileTypes.put("52494646d07d60074156", "avi");
        mFileTypes.put("4d546864000000060001", "mid"); //MIDI (mid)
        mFileTypes.put("504b0304140000000800", "zip");
        mFileTypes.put("526172211a0700cf9073", "rar");
        mFileTypes.put("235468697320636f6e66", "ini");
        mFileTypes.put("504b03040a0000000000", "jar");
        mFileTypes.put("4d5a9000030000000400", "exe");//可执行文件
        mFileTypes.put("3c25402070616765206c", "jsp");//jsp文件
        mFileTypes.put("4d616e69666573742d56", "mf");//MF文件
        mFileTypes.put("3c3f786d6c2076657273", "xml");//xml文件
        mFileTypes.put("494e5345525420494e54", "sql");//xml文件
        mFileTypes.put("7061636b616765207765", "java");//java文件
        mFileTypes.put("406563686f206f66660d", "bat");//bat文件
        mFileTypes.put("1f8b0800000000000000", "gz");//gz文件
        mFileTypes.put("6c6f67346a2e726f6f74", "properties");//bat文件
        mFileTypes.put("cafebabe0000002e0041", "class");//bat文件
        mFileTypes.put("49545346030000006000", "chm");//bat文件
        mFileTypes.put("04000000010000001300", "mxp");//bat文件
        mFileTypes.put("504b0304140006000800", "docx");//docx文件
        mFileTypes.put("d0cf11e0a1b11ae10000", "wps");//WPS文字wps、表格et、演示dps都是一样的
        mFileTypes.put("6431303a637265617465", "torrent");


        mFileTypes.put("6D6F6F76", "mov"); //Quicktime (mov)
        mFileTypes.put("FF575043", "wpd"); //WordPerfect (wpd)
        mFileTypes.put("CFAD12FEC5FD746F", "dbx"); //Outlook Express (dbx)
        mFileTypes.put("2142444E", "pst"); //Outlook (pst)
        mFileTypes.put("AC9EBD8F", "qdf"); //Quicken (qdf)
        mFileTypes.put("E3828596", "pwl"); //Windows Password (pwl)
        mFileTypes.put("2E7261FD", "ram"); //Real Audio (ram)
        mFileTypes.put("null", null); //null
    }

    public static String getFileType(File root, String filePath) {
        String keySearch = getFileHeader(root, filePath);
        String fileSuffix = mFileTypes.get(keySearch);
        //补充 这里并不是所有的文件格式前10 byte（jpg）都一致，前五个byte一致即可
        if (UtilValidate.isEmpty(fileSuffix)) {
            Iterator<String> keyList = mFileTypes.keySet().iterator();
            String key, keySearchPrefix = keySearch.substring(0, 5);
            while (keyList.hasNext()) {
                key = keyList.next();
                if (key.contains(keySearchPrefix)) {
                    fileSuffix = mFileTypes.get(key);
                    break;
                }
            }
        }
        return fileSuffix;
    }

    private static String getFileHeader(File root, String filePath) {
        File file = getFile(root, filePath);
        if (!file.exists() || file.length() < 11) {
            return "null";
        }
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(file);
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public static File getFile(String path) {
        return getFile(null, path);
    }

    public static File getFile(File root, String path) {
        if (path.startsWith("component://")) {
            try {
                path = ComponentLocationResolver.getBaseLocation(path).toString();
            } catch (MalformedURLException e) {
                Debug.logError(e, module);
                return null;
            }
        }
        return new File(root, localizePath(path));
    }

    /**
     * Converts a file path to one that is compatible with the host operating system.
     *
     * @param path The file path to convert.
     * @return The converted file path.
     */
    public static String localizePath(String path) {
        String fileNameSeparator = ("\\".equals(File.separator) ? "\\" + File.separator : File.separator);
        return path.replaceAll("/+|\\\\+", fileNameSeparator);
    }

    public static void writeString(String fileName, String s) throws IOException {
        writeString(null, fileName, s);
    }

    public static void writeString(String path, String name, String s) throws IOException {
        Writer out = getBufferedWriter(path, name);

        try {
            out.write(s + System.getProperty("line.separator"));
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }
    }

    /**
     * Writes a file from a string with a specified encoding.
     *
     * @param path
     * @param name
     * @param encoding
     * @param s
     * @throws IOException
     */
    public static void writeString(String path, String name, String encoding, String s) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        try {
            FileUtils.writeStringToFile(new File(fileName), s, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
    }

    public static void writeString(String encoding, String s, File outFile) throws IOException {
        try {
            FileUtils.writeStringToFile(outFile, s, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
    }

    public static Writer getBufferedWriter(String path, String name) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        return new BufferedWriter(new FileWriter(fileName));
    }

    public static OutputStream getBufferedOutputStream(String path, String name) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        return new BufferedOutputStream(new FileOutputStream(fileName));
    }

    public static String getPatchedFileName(String path, String fileName) throws IOException {
        // make sure the export directory exists
        if (UtilValidate.isNotEmpty(path)) {
            path = path.replaceAll("\\\\", "/");
            File parentDir = new File(path);
            if (!parentDir.exists()) {
                if (!parentDir.mkdir()) {
                    throw new IOException("Cannot create directory for path: " + path);
                }
            }

            // build the filename with the path
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            fileName = path + fileName;
        }

        return fileName;
    }

    public static StringBuffer readTextFile(File file, boolean newline) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        StringBuffer buf = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
                if (newline) {
                    buf.append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return buf;
    }
    public static StringBuffer readTextFile(String fileName, boolean newline) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        return readTextFile(file, newline);
    }

    public static String readString(String encoding, File inFile) throws IOException {
        String readString = "";
        try {
            readString = FileUtils.readFileToString(inFile, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
        return readString;
    }

    public static void searchFiles(List<File> fileList, File path, FilenameFilter filter, boolean includeSubfolders) throws IOException {
        // Get filtered files in the current path
        File[] files = path.listFiles(filter);
        if (files == null) {
            return;
        }

        // Process each filtered entry
        for (int i = 0; i < files.length; i++) {
            // recurse if the entry is a directory
            if (files[i].isDirectory() && includeSubfolders && !files[i].getName().startsWith(".")) {
                searchFiles(fileList, files[i], filter, true);
            } else {
                // add the filtered file to the list
                fileList.add(files[i]);
            }
        }
    }

    public static List<File> findFiles(String fileExt, String basePath, String partialPath, String stringToFind) throws IOException {
        if (basePath == null) {
            basePath = System.getProperty("ofbiz.home");
        }

        Set<String> stringsToFindInPath = new HashSet<String>();
        Set<String> stringsToFindInFile = new HashSet<String>();

        if (partialPath != null) {
           stringsToFindInPath.add(partialPath);
        }
        if (stringToFind != null) {
           stringsToFindInFile.add(stringToFind);
        }

        List<File> fileList = new LinkedList<File>();
        FileUtil.searchFiles(fileList, new File(basePath), new SearchTextFilesFilter(fileExt, stringsToFindInPath, stringsToFindInFile), true);

        return fileList;
    }

    public static List<File> findXmlFiles(String basePath, String partialPath, String rootElementName, String xsdOrDtdName) throws IOException {
        if (basePath == null) {
            basePath = System.getProperty("ofbiz.home");
        }

        Set<String> stringsToFindInPath = new HashSet<String>();
        Set<String> stringsToFindInFile = new HashSet<String>();

        if (partialPath != null) stringsToFindInPath.add(partialPath);
        if (rootElementName != null) stringsToFindInFile.add("<" + rootElementName + " ");
        if (xsdOrDtdName != null) stringsToFindInFile.add(xsdOrDtdName);

        List<File> fileList = new LinkedList<File>();
        FileUtil.searchFiles(fileList, new File(basePath), new SearchTextFilesFilter("xml", stringsToFindInPath, stringsToFindInFile), true);
        return fileList;
    }

    public static class SearchTextFilesFilter implements FilenameFilter {
        String fileExtension;
        Set<String> stringsToFindInFile = new HashSet<String>();
        Set<String> stringsToFindInPath = new HashSet<String>();

        public SearchTextFilesFilter(String fileExtension, Set<String> stringsToFindInPath, Set<String> stringsToFindInFile) {
            this.fileExtension = fileExtension;
            if (stringsToFindInPath != null) {
                this.stringsToFindInPath.addAll(stringsToFindInPath);
            }
            if (stringsToFindInFile != null) {
                this.stringsToFindInFile.addAll(stringsToFindInFile);
            }
        }

        @Override
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if (file.getName().startsWith(".")) {
                return false;
            }
            if (file.isDirectory()) {
                return true;
            }

            boolean hasAllPathStrings = true;
            String fullPath = dir.getPath().replace('\\', '/');
            for (String pathString: stringsToFindInPath) {
                if (fullPath.indexOf(pathString) < 0) {
                    hasAllPathStrings = false;
                    break;
                }
            }

            if (hasAllPathStrings && name.endsWith("." + fileExtension)) {
                if (stringsToFindInFile.size() == 0) {
                    return true;
                }
                StringBuffer xmlFileBuffer = null;
                try {
                    xmlFileBuffer = FileUtil.readTextFile(file, true);
                } catch (FileNotFoundException e) {
                    Debug.logWarning("Error reading xml file [" + file + "] for file search: " + e.toString(), module);
                    return false;
                } catch (IOException e) {
                    Debug.logWarning("Error reading xml file [" + file + "] for file search: " + e.toString(), module);
                    return false;
                }
                if (UtilValidate.isNotEmpty(xmlFileBuffer)) {
                    boolean hasAllStrings = true;
                    for (String stringToFile: stringsToFindInFile) {
                        if (xmlFileBuffer.indexOf(stringToFile) < 0) {
                            hasAllStrings = false;
                            break;
                        }
                    }
                    return hasAllStrings;
                }
            } else {
                return false;
            }
            return false;
        }
    }

    /**
    *
    *
    * Search for the specified <code>searchString</code> in the given
    * {@link Reader}.
    *
    * @param reader A Reader in which the String will be searched.
    * @param searchString The String to search for
    * @return <code>TRUE</code> if the <code>searchString</code> is found;
    *         <code>FALSE</code> otherwise.
    * @throws IOException
    */
   public static boolean containsString(Reader reader, final String searchString) throws IOException {
       char[] buffer = new char[1024];
       int numCharsRead;
       int count = 0;
       while((numCharsRead = reader.read(buffer)) > 0) {
           for (int c = 0; c < numCharsRead; ++c) {
               if (buffer[c] == searchString.charAt(count)) count++;
               else count = 0;
               if (count == searchString.length()) return true;
           }
       }
       return false;
   }

   /**
    *
    *
    * Search for the specified <code>searchString</code> in the given
    * filename. If the specified file doesn't exist, <code>FALSE</code>
    * returns.
    *
    * @param fileName A full path to a file in which the String will be searched.
    * @param searchString The String to search for
    * @return <code>TRUE</code> if the <code>searchString</code> is found;
    *         <code>FALSE</code> otherwise.
    * @throws IOException
    */
   public static boolean containsString(final String fileName, final String searchString) throws IOException {
       File inFile = new File(fileName);
       if (inFile.exists()) {
           BufferedReader in = new BufferedReader(new FileReader(inFile));
           try {
               return containsString(in, searchString);
           } finally {
               if (in != null)in.close();
           }
       } else {
           return false;
       }
   }
   
   /**
   *
   *
   * Check if the specified <code>fileName</code> exists and is a file (not a directory)
   * If the specified file doesn't exist or is a directory <code>FALSE</code> returns.
   *
   * @param fileName A full path to a file in which the String will be searched.
   * @return <code>TRUE</code> if the <code>fileName</code> exists and is a file (not a directory)
   *         <code>FALSE</code> otherwise.
   */
   public static boolean isFile(String fileName) {
       File f = new File(fileName);
       return f.isFile();
   }
   
}
