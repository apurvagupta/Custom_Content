package com.jivesoftware.community.ext.memo.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveObject;
import com.jivesoftware.community.action.ContentActionSupport;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoManager;
import com.jivesoftware.community.ext.memo.impl.MemoConverter;
import com.jivesoftware.community.ext.memo.impl.MemoImpl;
import com.jivesoftware.community.moderation.JiveObjectModerator;
import com.jivesoftware.community.tags.TagActionUtil;
import com.jivesoftware.community.util.AttachmentPermHelper;
import com.jivesoftware.community.util.concurrent.LockUtil;
import com.jivesoftware.community.validation.InvalidPropertyException;
import com.opensymphony.xwork2.ActionContext;

public abstract class EditMemoActionSupport extends ContentActionSupport {
    protected static final String SESSION_MEMO_KEY = "jive.memo.message";
    public static final String SUCCESS_MODERATION = "success-moderation";

    protected String title;
    protected String description;
    
    protected String tags;
    protected Set<String> validatedTags = Collections.emptySet();
    protected Iterable<String> popularTags;
    
    protected MemoConverter memoConverter;
    private boolean postedFromGUIEditor;
    protected Memo newMemo;
    
    protected Memo memo;
    
    protected MemoManager memoManager;
    private JiveObjectModerator jiveObjectModerator;
    protected TagActionUtil tagActionUtil;
    
    public void validate() {
        //TODO: validate input fields here
        
        if (!StringUtils.isBlank(tags)) {
            try {
                validatedTags = tagActionUtil.getValidTags(tags);
            }
            catch (InvalidPropertyException ipe) {
                switch (ipe.getError()) {
                    case length:
                        addFieldError("tags",
                                getText("doc.create.error.tag.length", new String[]{ipe.getProperty().toString()}));
                        break;
                    default:
                        addFieldError("tags",
                                getText("doc.create.error.tag.unknown", new String[]{ipe.getProperty().toString()}));
                        break;
                }
            }
        }
    }

    public String doImagePicker() {
        if (memo == null) {
            try {
                if (getSessionKey() != null && getSession().get(getSessionKey()) != null) {
                    memo = (Memo) getSession().get(getSessionKey());
                } else {
                    memo = getTemporaryMemo();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                addActionError(e.getMessage());
            }
        }
                            
        getSession().put(getSessionKey(), memo);
                            
        return "image-picker";
    }
                            
    public String getSessionKey() {
        try {
            if (memo == null) {
                memo = getTemporaryMemo();
            }
                            
            if (memo.getID() < 1) {
                return getMemoSessionKey("");
            }
                            
            return getMemoSessionKey("" + memo.getID());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
                            
        return null;
    }
                            
    protected Memo getTemporaryMemo() throws Exception {
        if (memo != null && getSession().containsKey(getMemoSessionKey("" + memo.getID()))) {
            return (Memo) getSession().get(getMemoSessionKey("" + memo.getID()));
        } else {
            return createTempMemo();
        }
    }
                            
    private Memo createTempMemo() {
        MemoBean bean = new MemoBean();
        bean.setID(-1);
        bean.setUserID(this.getUser().getID());
        bean.setContainerID(getContainer().getID());
        bean.setContainerType(getContainer().getObjectType());
                            
        return memoConverter.convert(bean);
    }
                            
    public boolean hasPermissionsToUploadImages() {
        return AttachmentPermHelper.getCanCreateImageAttachment(getContainer());
    }
                            
    protected String getMemoSessionKey(String id) {
        return LockUtil.intern(SESSION_MEMO_KEY + id);
    }
                            
    protected void cleanSession() {
        ArrayList<String> removeables = new ArrayList<String>();
        Set keys = ActionContext.getContext().getSession().keySet();
        for (Object key1 : keys) {
            String key = (String) key1;
            if (key.startsWith(SESSION_MEMO_KEY)) {
                removeables.add(key);
            }
        }

        for (String removeable : removeables) {
            getSession().remove(removeable);
        }
    }

    public String getLanguage() {
        return this.getLocale().getLanguage();
    }

    public void setNewMemo(Memo memo) {
        this.newMemo = memo;
    }

    public boolean isPostedFromGUIEditor() {
        return postedFromGUIEditor;
    }

    public void setPostedFromGUIEditor(boolean postedFromGUIEditor) {
        this.postedFromGUIEditor = postedFromGUIEditor;
    }

    public void setMemoConverter(MemoConverter memoConverter) {
        this.memoConverter = memoConverter;
    }

    public Memo getMemo() {
        return memo;
    }

    public void setMemo(Memo memo) {
        this.memo = memo;
    }

    public Iterable<String> getPopularTags() {
        if (popularTags == null) {
            popularTags = tagActionUtil.getPopularTags(getContainer(), 25);
        }
        return popularTags;
    }
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
       this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
       this.description = description;
    }
    
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public List<Long> getObjectTagSetIDs(JiveObject object) {
        return getTagSetActionHelper().getObjectTagSetIDs(object);
    }
    public void setMemoManager(MemoManager memoManager) {
        this.memoManager = memoManager;
    }

    public void setTagActionUtil(TagActionUtil tagActionUtil) {
        this.tagActionUtil = tagActionUtil;
    }

    public boolean isMemoModerated() {
        boolean moderationOn = jiveObjectModerator.isModerationEnabled(getContainer(), new MemoImpl(), getUser());
        // Check to see if an interceptor has toggled the status to awaiting moderation
        boolean alreadyAwaitingModeration = getMemo() != null && getMemo().getStatus() == JiveContentObject.Status
                .AWAITING_MODERATION;

        return moderationOn || alreadyAwaitingModeration;
    }

    @Override
    public String input() {
        if (getJiveObjectModerator().isModerationEnabled(getContainer(), new MemoImpl(), getUser())) {
            addActionMessage(getText("Please note, your memo will need to be approved by a moderator before it is posted."));
        }

        return super.input();
    }

    protected JiveObjectModerator getJiveObjectModerator() {
        return jiveObjectModerator;
    }

    public void setJiveObjectModerator(JiveObjectModerator jiveObjectModerator) {
        this.jiveObjectModerator = jiveObjectModerator;
    }

    public abstract JiveContainer getContainer();
    public abstract boolean isEdit();
}
