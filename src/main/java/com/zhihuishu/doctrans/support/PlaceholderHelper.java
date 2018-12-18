package com.zhihuishu.doctrans.support;

public class PlaceholderHelper {
    
    private static final String WMF_PLACEHOLDER_PREFIX = "${wmf:";
    
    private static final String MATHML_PLACEHOLDER_PREFIX = "${mathml:";
    
    private static final String COMMON_PLACEHOLDER_SUFFIX = "}";
    
    public static String createWMFPlaceholder(String rId) {
        return createPlaceholder(WMF_PLACEHOLDER_PREFIX, COMMON_PLACEHOLDER_SUFFIX, rId);
    }
    
    public static String createMathMLPlaceholder(Integer num) {
        return createPlaceholder(MATHML_PLACEHOLDER_PREFIX, COMMON_PLACEHOLDER_SUFFIX, num);
    }
    
    private static String createPlaceholder(String prefix, String suffix, Object obj) {
        if (prefix == null || suffix == null || obj == null) {
            throw new IllegalArgumentException("非法占位符参数");
        }
        return prefix + String.valueOf(obj) + suffix;
    }
    
}
