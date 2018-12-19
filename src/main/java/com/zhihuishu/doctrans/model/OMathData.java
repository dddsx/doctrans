package com.zhihuishu.doctrans.model;

import org.w3c.dom.Node;

public class OMathData {
    
    private String id;
    
    private String xmlContent;
    
    private Node node;
    
    public OMathData(String id, String xmlContent, Node node) {
        this.id = id;
        this.xmlContent = xmlContent;
        this.node = node;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
