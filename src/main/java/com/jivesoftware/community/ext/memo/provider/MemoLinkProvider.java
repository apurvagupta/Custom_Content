package com.jivesoftware.community.ext.memo.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jivesoftware.community.impl.BaseContentLinkProvider;

public class MemoLinkProvider extends BaseContentLinkProvider {
    public static final Pattern MEMO_LINK_PATTERN = Pattern.compile("/?(memos)/([\\w\\#]+)(\\^[\\w.]+)?$", Pattern.CASE_INSENSITIVE);

    public Matcher getAnchorHrefMatcher(String href) {
        Matcher matcher = MEMO_LINK_PATTERN.matcher(href);
        if(matcher.find()) {
            matcher.reset();
            return matcher;
        }
        
        return null;
    }

    public String getAnchorLinkFromAnchorHrefMatcher(Matcher urlMatcher) {
        return urlMatcher.group(2).contains("#") ? urlMatcher.group(2) : null;
    }

    public String getAttachmentNameFromAnchorHrefMatcher(Matcher urlMatcher) {
        return null;
    }

    public String getLinkCSS() {
        return "jive-link-memo";
    }

    public String getObjectIdFromAnchorHrefMatcher(Matcher urlMatcher) {
        String objectId = urlMatcher.group(2);
        return objectId.contains("#") ? objectId.substring(0, objectId.indexOf("#")) : objectId;
    }

    public String parseRelativeURL(String href, String baseUrl) {
        String part = "/memos/";
        String hrefPart;
        
        if(href.startsWith(baseUrl + part) || href.startsWith(part)) {
            hrefPart = part;
        } else {
            return null;
        }
        
        return hrefPart + href.substring(href.lastIndexOf("=") + 1);
    }

}
