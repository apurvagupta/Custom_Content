package com.jivesoftware.community.ext.memo.macro;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.lifecycle.JiveApplication;
import com.jivesoftware.community.renderer.BaseMacro;
import com.jivesoftware.community.renderer.RenderContext;
import com.jivesoftware.community.renderer.filter.wiki.link.Link;
import com.jivesoftware.community.renderer.filter.wiki.link.LinkRenderer;
import com.jivesoftware.community.renderer.filter.wiki.link.LinkType;
import com.jivesoftware.community.renderer.impl.v2.JAXPUtils;
import com.jivesoftware.community.renderer.macro.MacroParameter;
import com.jivesoftware.community.web.JiveResourceResolver;

public class MemoMacro extends BaseMacro {
    private static final Logger log = Logger.getLogger(MemoMacro.class);
    
    public static final String TAG_NAME = "memo";
    protected static final String TITLE = "title";
    protected static final String ID = "id";
    protected static final String DEFAULTATTR = "__default_attr";

    private static final boolean enabledByDefault = true;
    private static final String[] DEFAULT_HELP = new String[] { "Text Decoration", "Memo Link", "Documentation not present" };

    private static Map<String, String[]> documentation = new HashMap<String, String[]>();

    private Map<String, String> parameters = new HashMap<String, String>();
    private String url = "/memo";

    public String getName() {
        return TAG_NAME;
    }

    public boolean isShowInRTE() {
        return false;
    }

    public List<MacroParameter<?>> getAllowedParameters() {
        LinkedList<MacroParameter<?>> l = new LinkedList<MacroParameter<?>>();
        l.add(new MacroParameter<String>(ID, ""));
        l.add(new MacroParameter<String>(DEFAULTATTR, ""));
        l.add(new MacroParameter<String>(TITLE, ""));
        return l;
    }

    protected boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public String getShortMacroName() {
        return TAG_NAME;
    }

    public boolean isSingleTagMacro() {
        return true;
    }

    public void execute(Element element, RenderContext renderContext) {
        String id = parameters.get("id");
        Memo memo = null;
        if (parameters.get("id") == null) {
            id = getDefaultParameterValue(element);
        }
        long memoID = -1;
        try {
            memoID = Long.parseLong(id);
            memo = getMemoManager().getMemo(memoID);
        } catch (NumberFormatException e) {
            log.debug(e);
        } catch (UnauthorizedException e) {
            handleError(e, element, "error.unauth.document", id);
            return;
        }

        try {
            if (memo != null) {
                Link link = createLink(memo);
                Element newElement = LinkRenderer.render(link, element.getOwnerDocument(), renderContext, false);
                JAXPUtils.replace(element, newElement);
            }
        } catch (UnauthorizedException e) {
            handleError(e, element, "error.unauth.document", id);
        }
    }

    private Link createLink(Memo memo) {
        Link link = new Link();
        String title = parameters.get(TITLE);
        if (parameters.get("title") == null) {
            link.setLinkText(memo.getSubject());
        } else {
            link.setLinkText(title);
        }
        link.setAltText(memo.getSubject());
        link.setLinkType(LinkType.JIVE_OBJECT_LINK);
        link.setObjectTypeID(new MemoObjectType().getID());
        link.setObjectID(memo.getID());
        link.setUrl(JiveResourceResolver.getJiveObjectURL(memo, true));
        return link;
    }

    protected Map<String, String[]> getDocumentationMap() {
        return documentation;
    }

    protected String[] getDefaultHelp() {
        return DEFAULT_HELP;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private MemoManager getMemoManager() {
        return (MemoManager) JiveApplication.getEffectiveContext().getSpringBean("memoManager");
    }
}
