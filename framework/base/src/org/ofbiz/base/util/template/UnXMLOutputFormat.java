package org.ofbiz.base.util.template;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.TemplateXMLOutputModel;
import freemarker.core.XMLOutputFormat;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import org.ofbiz.base.util.UtilCodec;

import java.io.IOException;
import java.io.Writer;

/**
 * 反编码xml
 *
 * Created by galaxypan on 16.9.8.
 */
public class UnXMLOutputFormat extends CommonMarkupOutputFormat<TemplateUNXMLOutputModel> {
    public static final UnXMLOutputFormat INSTANCE = new UnXMLOutputFormat();

    private UnXMLOutputFormat() {
    }

    public String getName() {
        return "UNXML";
    }

    public String getMimeType() {
        return "application/xml";
    }

    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        textToEsc = UtilCodec.getDecoder("html").decode(textToEsc);
        out.write(textToEsc);
    }

    public String escapePlainText(String plainTextContent) {
        return UtilCodec.getDecoder("html").decode(plainTextContent);
    }

    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("unxml");
    }

    protected TemplateUNXMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateUNXMLOutputModel(plainTextContent, markupContent);
    }
}