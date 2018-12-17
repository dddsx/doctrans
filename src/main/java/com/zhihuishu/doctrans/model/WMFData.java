package com.zhihuishu.doctrans.model;

public class WMFData {
    
    private String rId;
    
    private byte[] data;
    
    private String style;
    
    public WMFData(String rId, byte[] data, String style) {
        this.rId = rId;
        this.data = data;
        this.style = style;
    }
    
    public String getrId() {
        return rId;
    }
    
    public void setrId(String rId) {
        this.rId = rId;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
}
