package org.ofbiz.catalina.container;

import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;

/**
 * Created by galaxypan on 2017/10/6.
 */
public class FilterJars implements JarScanFilter {

    @Override
    public boolean check(final JarScanType jarScanType, final String jarName) {
        if (jarName.contains("discoverable")) {
            return true;
        } else {
            return false;
        }
    }
}