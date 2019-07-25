package com.zhihuishu.doctrans.model;

/**
 * wmf图片元数据
 */
public class WmfData {
    
    /** html中的占位符 */
    private String placeholder;
    
    /** 图片字节数组 */
    private byte[] bytes;
    
    /** 图片宽度, 可能为空 */
    private Double width;
    
    /** 图片高度, 可能为空 */
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
