package com.zhihuishu.doctrans.model;

import org.w3c.dom.Node;

public class UnknownElement {
    
    private String nodeName;
    
    private Node node;
    
    private String describe;
    
    public String getNodeName() {
        return nodeName;
    }
    
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    
    public Node getNode() {
        return node;
    }
    
    public void setNode(Node node) {
        this.node = node;
    }
    
    public String getDescribe() {
        return describe;
    }
    
    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
