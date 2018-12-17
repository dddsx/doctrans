package com.zhihuishu.doctrans.support;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class PlaceholderHelper {
    
    private static final String WMF_PLACEHOLDER_PREFIX = "${wmf:";
    
    private static final String MATHML_PLACEHOLDER_PREFIX = "${mathml:";
    
    private static final String COMMON_PLACEHOLDER_SUFFIX = "}";
    
    public static String createWMFPlaceholder(String id) {
        return createPlaceholder(WMF_PLACEHOLDER_PREFIX, COMMON_PLACEHOLDER_SUFFIX, id);
    }
    
    public static String createMathMLPlaceholder(String id) {
        return createPlaceholder(MATHML_PLACEHOLDER_PREFIX, COMMON_PLACEHOLDER_SUFFIX, id);
    }
    
    private static String createPlaceholder(String prefix, String suffix, String str) {
        if (prefix == null || suffix == null || StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("非法占位符参数");
        }
        return prefix + str + suffix;
    }
    
}
