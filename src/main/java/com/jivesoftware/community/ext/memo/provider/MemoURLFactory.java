package com.jivesoftware.community.ext.memo.provider;

import com.jivesoftware.community.Attachment;
import com.jivesoftware.community.AttachmentContentResource;
import com.jivesoftware.community.Comment;
import com.jivesoftware.community.CommentContentResource;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveGlobals;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.JiveObjectURLFactory;

public class MemoURLFactory implements JiveObjectURLFactory {

    public String createAttachmentContentResourceURL(AttachmentContentResource attachmentTarget, Attachment attachment) {
        return "";
    }

    public String createCommentContentResourceURL(CommentContentResource commentTarget, Comment comment, boolean absolute) {
        StringBuilder url = new StringBuilder(this.createURL(commentTarget, absolute));
        
        url.append("#comment-");
        url.append(comment.getID());
        
        return url.toString();
    }

    public String createEditFormURL(JiveObject jiveObject) {
        StringBuilder url = new StringBuilder(this.createURL(jiveObject, false));
        url.append("/edit");
        
        return url.toString();
    }

    public String createImageURL(JiveContentObject imageTarget, Image image) {
        return "";
    }

    public String createURL(JiveObject memo, boolean absolute) {
        StringBuilder url = new StringBuilder(32);
        
        if(absolute) {
            url.append(JiveGlobals.getDefaultBaseURL());
        }
        
        url.append("/memos/").append(memo.getID());
        return url.toString();
    }
    
    @Override
    public String createDownloadURL(JiveObject jiveObject, boolean absolute) {
        return "";
    }
}
