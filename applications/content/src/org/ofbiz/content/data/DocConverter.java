package org.ofbiz.content.data;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;

/**
 * 类的描述信息
 *
 * @author galaxypan
 * @version 1.0.0
 */
public class DocConverter {
    private Logger logger = LoggerFactory.getLogger(DocConverter.class);
    private static int environment = 1;// 环境 1：windows 2:linux
    private static String swftools_path = "";
    private int openoffice_port = 8100;
    private File docFile;
    private InputStream docFileStream;
    private File pdfFile;
    private File swfFile;

    private static final String WORD = "doc";
    private static final String WORDX = "docx";

    private static final String EXCEL = "xls";
    private static final String EXCELX = "xlsx";

    private static final String PPT = "ppt";
    private static final String PPTX = "pptx";

    private static final String PDF = "pdf";

    private boolean omitConvert = false;
    private boolean toSwf = false;
    //转换生成的文件存放位置
    private String generateFileFolder;

    private static DocConverter docConverter;
    private OpenOfficeConnection connection;
    private OpenOfficeDocumentConverter converter;
    private DefaultDocumentFormatRegistry defaultDocumentFormatRegistry;

    public OpenOfficeConnection getConnection() {
        return connection;
    }

    public static DocConverter getInstance(){
        if(docConverter == null){
            synchronized (DocConverter.class){
                if(docConverter == null){
                    docConverter = new DocConverter();
                }
            }
        }
        if(!docConverter.getConnection().isConnected()){
            synchronized (DocConverter.class){
                if(!docConverter.getConnection().isConnected()){
                    try {
                        docConverter.getConnection().connect();
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return docConverter;
    }

    private DocConverter(){
        environment = Integer.parseInt(UtilProperties.getPropertyValue("content.properties", "doc_convert_env", "2"));
        openoffice_port = Integer.parseInt(UtilProperties.getPropertyValue("content.properties", "openoffice_port", "8100"));
        swftools_path = UtilProperties.getPropertyValue("content.properties", "swftools_path");
        generateFileFolder = UtilProperties.getPropertyValue("content.properties", "dataResourcePath");
        this.connection = new SocketOpenOfficeConnection(openoffice_port);
        this.defaultDocumentFormatRegistry =  new DefaultDocumentFormatRegistry();
        try {
            this.connection.connect();
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        this.converter = new OpenOfficeDocumentConverter(connection, defaultDocumentFormatRegistry);
    }

    public DocConverter(File file) {
        this(file, false);
    }

    public DocConverter(String fileName, InputStream fileInputStream) {
        ini(null, fileInputStream, fileName);
    }

    public DocConverter(File file, boolean toSwf) {
        this.toSwf = toSwf;
        ini(file, null, null);
    }

    public DocConverter(File file, boolean overwrite, boolean toSwf) {
        this(file, toSwf);
        omitConvert = !overwrite;
    }

    /**
     * 初始化
     *
     * @param file
     */
    private void ini(File file, InputStream fileInputStream, String fileInputName) {

        String name = file != null ? file.getName() : fileInputName;
//        int i = name.lastIndexOf(".");
        String extension = FilenameUtils.getExtension(name);
        String fileName = FilenameUtils.getBaseName(name);
        String parentFolder = UtilValidate.isEmpty(generateFileFolder) && file != null ? file.getParent() : generateFileFolder;
        if(toSwf){
            swfFile = new File(parentFolder, fileName.replace("{}", "-----") + ".swf");
        }
        /*if(swfFile.exists()){//如果已经存在不需要重复转换
            omitConvert = true;
        }*/
        if(PDF.equalsIgnoreCase(extension) && !omitConvert){
            pdfFile = new File(parentFolder, fileName + ".pdf.pdf");
            try {
                FileUtils.copyFile(file, pdfFile);
            } catch (IOException e) {
                throw new RuntimeException("转换错误", e);
            }
        }else{
            docFile = file;
            docFileStream = fileInputStream;
            pdfFile = new File(parentFolder, name + ".pdf");
        }
    }

    /**
     * 初始化
     *
     * @param file
     */
    private File getPdfFile(File file) {

        String name = file.getName();
//        int i = name.lastIndexOf(".");
        String extension = FilenameUtils.getExtension(name);
        String fileName = FilenameUtils.getBaseName(name);
        String parentFolder = UtilValidate.isEmpty(generateFileFolder) && file != null ? file.getParent() : generateFileFolder;
        /*if(swfFile.exists()){//如果已经存在不需要重复转换
            omitConvert = true;
        }*/
        return new File(parentFolder, name + ".pdf");
    }

    /**
     * 初始化
     *
     * @param file
     */
    private File getPdfFile(String fileName) {
        return new File(generateFileFolder, fileName + ".pdf");
    }

    /**
     * 转为PDF
     * 需安装open office
     * cd C:\Program Files\OpenOffice.org 4\program
     * soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
     */
    public String doc2pdf(FileInputStream docFileStream, String fileName) throws Exception {
        if (docFileStream != null && fileName != null) {
            File pdfFile = getPdfFile(fileName);
            if (!pdfFile.exists()) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(pdfFile);
                    converter.convert(docFileStream, defaultDocumentFormatRegistry.getFormatByFileExtension(FilenameUtils.getExtension(fileName)), out, defaultDocumentFormatRegistry.getFormatByFileExtension("pdf"));
                    logger.debug("****pdf转换成功，PDF输出：" + pdfFile.getPath() + "****");
                }  catch (com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException e) {
                    throw new RuntimeException("swf转换器异常，读取转换文件失败", e);
                } catch (Exception e) {
                    throw new RuntimeException("swf转换器异常，openoffice服务未启动", e);
                }finally {
                    // close the connection
                    connection.disconnect();
                    if(out != null){
                        out.close();
                    }
                    if(docFileStream != null){
                        docFileStream.close();
                    }
                }
            } else {
                logger.debug("****已经转换为pdf，不需要再进行转化****");
            }
            return pdfFile.getAbsolutePath();
        } else {
            logger.debug("****swf转换器异常，需要转换的文档不存在，无法转换****");
        }
        return null;
    }

    /**
     * 转为PDF
     * 需安装open office
     * cd C:\Program Files\OpenOffice.org 4\program
     * soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
     */
    public void doc2pdf(File docFile) throws Exception {
        if ((docFile != null && docFile.exists())) {
            File pdfFile = getPdfFile(docFile);
            if (!pdfFile.exists()) {
                FileOutputStream out = null;
                try {
                    converter.convert(docFile, pdfFile);
                    logger.debug("****pdf转换成功，PDF输出：" + pdfFile.getPath() + "****");
                } catch (com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException e) {
                    throw new RuntimeException("swf转换器异常，读取转换文件失败", e);
                } catch (Exception e) {
                    throw new RuntimeException("swf转换器异常，openoffice服务未启动", e);
                }finally {
                    // close the connection
                    connection.disconnect();
                    if(out != null){
                        out.close();
                    }
                }
            } else {
                logger.debug("****已经转换为pdf，不需要再进行转化****");
            }
        } else {
            logger.debug("****swf转换器异常，需要转换的文档不存在，无法转换****");
        }
    }

    /**
     * 转换成 swf
     */
    private void pdf2swf() throws Exception {
        Runtime r = Runtime.getRuntime();
        /*if (!swfFile.exists()) {*/
            if (pdfFile.exists()) {
                if (environment == 1) {// windows环境处理
                    try {
                        Process p = r.exec(swftools_path + " " + pdfFile.getPath() + " -o " + swfFile.getPath() + " -T 9");
                        logger.debug(loadStream(p.getInputStream()));
                        logger.error(loadStream(p.getErrorStream()));
                        logger.debug(loadStream(p.getInputStream()));
                        logger.debug("****swf转换成功，文件输出："
                                + swfFile.getPath() + "****");
                        if (pdfFile.exists()) {
                            pdfFile.delete();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }
                } else if (environment == 2) {// linux环境处理
                    try {
                        Process p = r.exec("pdf2swf " + pdfFile.getPath()
                                + " -o " + swfFile.getPath() + " -T 9");
                        logger.debug(loadStream(p.getInputStream()));
                        logger.error(loadStream(p.getErrorStream()));
                        logger.debug("****swf转换成功，文件输出："
                                + swfFile.getPath() + "****");
                        if (pdfFile.exists()) {
                            pdfFile.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            } else {
                logger.error("****pdf不存在,无法转换****");
            }
        /*} else {
            logger.debug("****swf已经存在不需要转换****");
        }*/
    }

    static String loadStream(InputStream in) throws IOException {

        int ptr = 0;
        in = new BufferedInputStream(in);
        StringBuilder buffer = new StringBuilder();

        while ((ptr = in.read()) != -1) {
            buffer.append((char) ptr);
        }

        return buffer.toString();
    }

    /**
     * 转换主方法
     *//*
    public File convert() {
        if(toSwf && swfFile.exists() && omitConvert){
            return swfFile;
        }

        try {
            doc2pdf();
            if(toSwf){
                pdf2swf();
            }
        } catch (Exception e) {
            throw new RuntimeException("转换出错", e);
        }

        if (toSwf && swfFile.exists()) {
            return swfFile;
        } else {
            throw new RuntimeException("无法加载文档");
        }
    }

    *//**
     * 返回文件路径
     *
     *//*
    public String getswfPath() {
        if (swfFile.exists()) {
            String tempString = swfFile.getPath();
            tempString = tempString.replaceAll("\\\\", "/");
            return tempString;
        } else {
            return "";
        }

    }*/

}
