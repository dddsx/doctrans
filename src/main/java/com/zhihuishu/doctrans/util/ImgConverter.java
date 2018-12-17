package com.zhihuishu.doctrans.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;

public interface ImgConverter {
    
    String EXT_WMF = ".wmf";

    String EXT_SVG = ".svg";

    String EXT_PNG = ".png";

    String FORMAT_WMF = "wmf";
    
    String FORMAT_SVG = "svg";
    
    String FORMAT_PNG = "png";

    /**
     * 将图片转换为其它格式
     * @param source 源文件
     * @param target 输出文件
     */
    void convert(File source, File target, ImgConfig config);
    
    static boolean isWMFFormat(String filename) {
        return StringUtils.endsWithIgnoreCase(filename, FORMAT_WMF);
    }
    
    class ImgConfig {
        
        private String format;
    
        private Double height;
    
        private Double width;
        
        private Integer quality;
    
        public ImgConfig() {
        
        }
        
        public ImgConfig(String format) {
            this.format = format;
        }
        
        public ImgConfig(String format, Double height, Double width, Integer quality) {
            this.format = format;
            this.height = height;
            this.width = width;
            this.quality = quality;
        }
    
        public String getFormat() {
            return format;
        }
    
        public void setFormat(String format) {
            this.format = format;
        }
    
        public Double getHeight() {
            return height;
        }
    
        public void setHeight(Double height) {
            this.height = height;
        }
    
        public Double getWidth() {
            return width;
        }
    
        public void setWidth(Double width) {
            this.width = width;
        }
    
        public Integer getQuality() {
            return quality;
        }
    
        public void setQuality(Integer quality) {
            this.quality = quality;
        }
    }
}
