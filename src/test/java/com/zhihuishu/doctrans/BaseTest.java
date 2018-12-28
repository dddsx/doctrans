package com.zhihuishu.doctrans;

import java.io.File;

public class BaseTest {
    
    protected File rootFile = new File(BaseTest.class.getClassLoader().getResource("").getFile());
    
}
