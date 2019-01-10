package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.model.WmfData;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class BetterXdocreportConveter extends XdocreportConverter {
    
    public BetterXdocreportConveter(InputStream inputStream, ConvertSetting setting) throws IOException {
        super(inputStream, setting);
    }
    
    /**
     * 将wmf批量上传至.net服务来转换
     */
    @Override
    protected Map<String, byte[]> convertOMathToPNG() {
        HttpClient httpClient = HttpClients.createDefault();
        
        
        for (Map.Entry<String, WmfData> wmfData : wmfDatas.entrySet()) {
        
        }
        return super.convertOMathToPNG();
    }
}
