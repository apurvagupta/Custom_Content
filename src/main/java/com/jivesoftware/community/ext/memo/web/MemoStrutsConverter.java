package com.jivesoftware.community.ext.memo.web;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.community.action.JiveActionSupport;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.web.struts.JiveConversionErrorInterceptor;
import com.jivesoftware.community.web.struts.converter.JiveObjectTypeConverter;

public class MemoStrutsConverter extends JiveObjectTypeConverter {
    private static final Logger log = Logger.getLogger(MemoStrutsConverter.class);
    private MemoManager memoManager;

    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
    try {
        long memoID = NumberUtils.toLong(values[0], -1);
        if (memoID > 0) {
        Memo memo = memoManager.getMemo(memoID);

        if (memo == null) {
            log.error("Could not convert to Memo for parameter: " + values[0], new Exception());
            context.put(JiveConversionErrorInterceptor.JIVE_CONVERSION_ERROR_KEY, JiveActionSupport.NOTFOUND);
        } else {
            return memo;
        }
        }
    } catch (UnauthorizedException e) {
        log.error("Could not convert to Memo for parameter: " + values[0], e);
        setError(context, JiveActionSupport.UNAUTHORIZED);
    } catch (Exception e) {
        log.error("Could not convert to Memo for parameter: " + values[0], e);
        setError(context, JiveActionSupport.ERROR);
    }

    return null;
    }

    @Override
    public String convertToString(Map context, Object o) {
        return String.valueOf(((Memo) o).getID());
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

}
