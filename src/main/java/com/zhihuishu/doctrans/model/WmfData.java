package com.zhihuishu.doctrans.model;

public class WmfData {
    
    private String placeholder;
    
    private byte[] bytes;
    
    private String style;
    
    public WmfData(String placeholder, byte[] bytes, String style) {
        this.placeholder = placeholder;
        this.bytes = bytes;
        this.style = style;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
}
