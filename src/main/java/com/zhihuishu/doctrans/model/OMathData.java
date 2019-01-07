package com.zhihuishu.doctrans.model;

import org.w3c.dom.Node;

/**
 * omath元数据
 */
public class OMathData {
    
    /** html中的占位符 */
    private String placeholder;
    
    /** xml数据 */
    private String xmlContent;
    
    /** xml node */
    private Node node;
    
    public OMathData(String placeholder, String xmlContent, Node node) {
        this.placeholder = placeholder;
        this.xmlContent = xmlContent;
        this.node = node;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getXmlContent() {
        return xmlContent;
    }
    
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }
    
    public Node getNode() {
        return node;
    }
    
    public void setNode(Node node) {
        this.node = node;
    }
}
