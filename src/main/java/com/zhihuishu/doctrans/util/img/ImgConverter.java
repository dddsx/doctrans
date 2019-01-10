package com.zhihuishu.doctrans.util.img;

import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;

public interface ImgConverter {
    
    String EXT_WMF = ".wmf";

    String EXT_SVG = ".svg";

    String EXT_PNG = ".png";

    String FORMAT_WMF = "wmf";
    
    String FORMAT_SVG = "svg";
    
    String FORMAT_PNG = "png";
    
    String SYMBOL_POINT = ".";
    
    /**
     * 将图片转换为其它格式
     * @param in 源图片流
     * @param out 输出流
     */
    void convert(InputStream in, OutputStream out, ImgConfig config) throws Exception;
    
    static boolean isWMFFormat(String filename) {
        return StringUtils.endsWithIgnoreCase(filename, FORMAT_WMF);
    }
    
    class ImgConfig {
    
        private String targetFormat;
    
        private Integer width;
        
        private Integer height;
    
        private Integer quality;
    
        public ImgConfig() {
        
        }
    
        public ImgConfig(String targetFormat) {
            this.targetFormat = targetFormat;
        }
    
        public ImgConfig(String targetFormat, Integer width, Integer height) {
            this.targetFormat = targetFormat;
            this.width = width;
            this.height = height;
        }
    
        public ImgConfig(String targetFormat, Integer width, Integer height, Integer quality) {
            this.targetFormat = targetFormat;
            this.width = width;
            this.height = height;
            this.quality = quality;
        }
    
        public String getTargetFormat() {
            return targetFormat;
        }
    
        public void setTargetFormat(String targetFormat) {
            this.targetFormat = targetFormat;
        }
    
        public Integer getWidth() {
            return width;
        }
    
        public void setWidth(Integer width) {
            this.width = width;
        }
        
        public Integer getHeight() {
            return height;
        }
    
        public void setHeight(Integer height) {
            this.height = height;
        }
    
        public Integer getQuality() {
            return quality;
        }
    
        public void setQuality(Integer quality) {
            this.quality = quality;
        }
    }
}
