package com.jivesoftware.community.ext.memo.provider;

import java.util.Map;

import com.jivesoftware.community.JiveContext;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.ParamMapObjectFactory;
import com.jivesoftware.community.ext.memo.MemoManager;

public class MemoParamMapObjectFactory implements ParamMapObjectFactory {

    private MemoManager memoManager;
    
    public JiveObject loadObject(JiveContext jiveContext, Map<String, Object> params) throws NotFoundException {
        String memoID = (String) params.get("memo");
        if(memoID == null) {
            return null;
        }
        
        try {
            return memoManager.getMemo(Long.valueOf(memoID));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

    
}
