package org.ofbiz.base.util.template;

import freemarker.core.CommonTemplateMarkupOutputModel;
import freemarker.core.TemplateXMLOutputModel;
import freemarker.core.XMLOutputFormat;

/**
 * Created by galaxypan on 16.9.8.
 */
public class TemplateUNXMLOutputModel extends CommonTemplateMarkupOutputModel<TemplateUNXMLOutputModel> {
    TemplateUNXMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    public UnXMLOutputFormat getOutputFormat() {
        return UnXMLOutputFormat.INSTANCE;
    }
}
