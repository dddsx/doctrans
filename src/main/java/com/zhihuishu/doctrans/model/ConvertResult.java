package com.zhihuishu.doctrans.model;

import java.util.List;

public class ConvertResult {
    
    private String html;
    
    private List<UnknownElement> unknownElements;
    
    private Boolean successful = false;
    
    private String errorMsg;
    
    public String getHtml() {
        return html;
    }
    
    public void setHtml(String html) {
        this.html = html;
    }
    
    public List<UnknownElement> getUnknownElements() {
        return unknownElements;
    }
    
    public void setUnknownElements(List<UnknownElement> unknownElements) {
        this.unknownElements = unknownElements;
    }
    
    public Boolean getSuccessful() {
        return successful;
    }
    
    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
