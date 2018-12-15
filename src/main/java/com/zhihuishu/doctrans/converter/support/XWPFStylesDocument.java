/**
 * Copyright (C) 2011-2015 The XDocReport Team <xdocreport@googlegroups.com>
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zhihuishu.doctrans.converter.support;

import fr.opensagres.poi.xwpf.converter.core.styles.TableInfo;
import fr.opensagres.poi.xwpf.converter.core.utils.DxaUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.util.*;

public class XWPFStylesDocument {
    
    public static final Object EMPTY_VALUE = new Object();
    
    private static final float DEFAULT_TAB_STOP_POINT = DxaUtil.dxa2points(720f);
    
    private final Map<String, CTStyle> stylesByStyleId;
    
    private CTStyle defaultParagraphStyle;
    
    private CTStyle defaultTableStyle;
    
    private final Map<String, Object> values;
    
    private CTStyle defaultCharacterStyle;
    
    private CTStyle defaultNumberingStyle;
    
    private Map<XWPFTable, TableInfo> tableInfos;
    
    private Float defaultTabStop;
    
    private CTSettings ctSettings;
    
    private List<ThemeDocument> themeDocuments;
    
    private CTStyles styles;
    
    public XWPFStylesDocument(XWPFDocument document) {
        this.styles = styles;
        this.stylesByStyleId = new HashMap<String, CTStyle>();
        this.values = new HashMap<String, Object>();
        this.themeDocuments = themeDocuments;
    }

}

