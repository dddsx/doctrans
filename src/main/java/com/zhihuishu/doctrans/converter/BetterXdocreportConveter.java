package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.http.WMFConvertRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class BetterXdocreportConveter extends XdocreportConverter {
    
    public BetterXdocreportConveter(InputStream inputStream, ConvertSetting setting) throws Exception {
        super(inputStream, setting);
    }
    
    /**
     * 将wmf批量上传至.net服务来转换
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, byte[]> convertWMFToPNG() {
        Map<String, byte[]> pngDatas = Collections.EMPTY_MAP;
        if (wmfDatas == null || wmfDatas.size() == 0) {
            return pngDatas;
        }
        
        try {
            pngDatas = WMFConvertRequest.uploadAndConvert(wmfDatas);
        } catch (IOException e) {
            logger.error("wmf转换服务响应异常", e);
        } catch (Throwable e) {
            logger.error("wmf转换服务出现未知异常", e);
        }
        return pngDatas;
    }
}
