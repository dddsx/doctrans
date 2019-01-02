package com.zhihuishu.doctrans.model;

public class WmfData {
    
    private String placeholder;
    
    private byte[] bytes;
    
    private Double width;
    
    private Double height;
    
    public WmfData(String placeholder, byte[] bytes, Double width, Double height) {
        this.placeholder = placeholder;
        this.bytes = bytes;
        this.width = width;
        this.height = height;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public Double getWidth() {
        return width;
    }
    
    public Double getHeight() {
        return height;
    }
}
