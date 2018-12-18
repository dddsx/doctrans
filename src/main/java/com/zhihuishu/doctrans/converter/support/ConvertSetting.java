package com.zhihuishu.doctrans.converter.support;

public class ConvertSetting {
    
    /** 取完整html或一段div */
    private boolean fragment = true;
    
    /** 提取公式 */
    private boolean extractEquation = true;
    
    public ConvertSetting() {
    
    }
    
    public boolean isFragment() {
        return fragment;
    }
    
    public void setFragment(boolean fragment) {
        this.fragment = fragment;
    }
    
    public boolean isExtractEquation() {
        return extractEquation;
    }
    
    public void setExtractEquation(boolean extractEquation) {
        this.extractEquation = extractEquation;
    }
}
