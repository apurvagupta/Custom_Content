package com.jivesoftware.community.ext.memo.provider;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.JiveObjectURLFactory;
import com.jivesoftware.community.LinkProvider;
import com.jivesoftware.community.ParamMapObjectFactory;
import com.jivesoftware.community.TypeUIProvider;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.objecttype.ContainerContentInfoProvider;
import com.jivesoftware.community.util.IconGenerator;
import com.jivesoftware.util.LocaleUtils;

public class MemoTypeUIProvider implements TypeUIProvider {

    private IconGenerator iconGenerator;
    private JiveObjectURLFactory jiveObjectUrlFactory;
    private LinkProvider linkProvider;
    private List<String> macroNames;
    private Map<String, ParamMapObjectFactory> paramMapObjectFactories;
    private ContainerContentInfoProvider containerContentInfoProvider;
    
    public String getContentTypeFeatureName() {
        return getLocalizedString("global.memos");
    }

    public String getContentTypeFeatureName(Locale locale) {
        return getLocalizedString("global.memos", locale);
    }

    public String getContentTypeFormCSS() {
        return "jive-icon-big jive-icon-memo-big";
    }

    public String getContentTypeName() {
        return getLocalizedString("global.memo");
    }

    public String getContentTypeName(Locale locale) {
        return getLocalizedString("global.memo", locale);
    }

    public IconGenerator getIconGenerator() {
        return iconGenerator;
    }

    public JiveObjectURLFactory getJiveObjectURLFactory() {
        return jiveObjectUrlFactory;
    }

    public String getLinkMediumCSS() {
        return "jive-icon-med jive-icon-memo-med";
    }

    public LinkProvider getLinkProvider() {
        return linkProvider;
    }

    public List<String> getMacroNames() {
        return macroNames;
    }

    public Map<String, ParamMapObjectFactory> getParamMapObjectFactories() {
        return paramMapObjectFactories;
    }

    protected String getLocalizedString(String key) {
        return LocaleUtils.getLocalizedString(key);
    }
    
    protected String getLocalizedString(String key, Locale locale) {
        return LocaleUtils.getLocalizedString(key, locale);
    }
    
    public void setParamMapObjectFactories(Map<String, ParamMapObjectFactory> paramMapObjectFactories) {
        this.paramMapObjectFactories = paramMapObjectFactories;
    }

    public void setIconGenerator(IconGenerator iconGenerator) {
        this.iconGenerator = iconGenerator;
    }

    public void setJiveObjectURLFactory(JiveObjectURLFactory jiveObjectUrlFactory) {
        this.jiveObjectUrlFactory = jiveObjectUrlFactory;
    }

    public void setLinkProvider(LinkProvider linkProvider) {
        this.linkProvider = linkProvider;
    }

    public void setMacroNames(List<String> macroNames) {
        this.macroNames = macroNames;
    }
    
    public void setMemoContainerContentInfoProvider(ContainerContentInfoProvider containerContentInfoProvider) {
        this.containerContentInfoProvider = containerContentInfoProvider;
    }
   
    @Override
    public ContainerContentInfoProvider getContainerContentInfoProvider(int containerType) {
        return containerContentInfoProvider;
    }

    @Override
    public String getObjectName(JiveObject jiveObject) {
        return ((Memo)jiveObject).getSubject();
    }
}
