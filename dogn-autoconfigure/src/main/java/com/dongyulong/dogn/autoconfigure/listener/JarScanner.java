package com.dongyulong.dogn.autoconfigure.listener;

import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * jar 包扫描器
 *
 * @author zhangyunan
 */
public class JarScanner {

    private final static String SCAN_PACKAGE = "com.didapinche";

    /**
     * Do scan all jars set.
     *
     * @return the set
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public Set<String> doScanAllJars() throws IOException {
        Set<String> jarPackages = new LinkedHashSet<>();
        String basePackageString = SCAN_PACKAGE;
        try {
            basePackageString = SystemPropertyUtils.getProperty("dida.server.common.jarscan.basepackages", "com.didapinche");
        } catch (Exception e) {
            //TOOD
        }
        String[] basePackages = basePackageString.split(",");
        for (String packageName : basePackages) {
            if (StringUtils.isBlank(packageName)) {
                continue;
            }
            // 如果最后一个字符是“.”，则去掉
            if (packageName.endsWith(".")) {
                packageName = packageName.substring(0, packageName.lastIndexOf('.'));
            }
            // 将包名中的“.”换成系统文件夹的“/”
            String basePackageFilePath = packageName.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(basePackageFilePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("jar".equals(protocol)) {
                    String jarFullName = ((JarURLConnection) resource.openConnection()).getJarFile().getName();
                    if (!jarFullName.endsWith(".jar")) {
                        continue;
                    }
                    String jarName = jarFullName.substring(jarFullName.lastIndexOf("/") + 1);
                    jarPackages.add(jarName);
                }
            }
        }
        return jarPackages;
    }
}
