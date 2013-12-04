package com.jivesoftware.community.ext.memo.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.jivesoftware.community.web.struts.mapping.URLMapping;

public class MemoURLMapping implements URLMapping {

    @SuppressWarnings("unchecked")
    public void process(String uri, ActionMapping mapping) {
        String[] elements = uri.split("/");
        Map params = mapping.getParams();
        if (null == params) {
            params = new HashMap();
        }

        if (elements.length > 3) {
            mapping.setName("edit-memo");
            mapping.setMethod("input");
            params.put("memo", elements[2]);
        } else if (elements.length > 2) {
            mapping.setName("view-memo");
            params.put("memo", elements[2]);
        } else {
            mapping.setName("main-memos");
        }

        mapping.setNamespace("");
        mapping.setParams(params);
    }

}
