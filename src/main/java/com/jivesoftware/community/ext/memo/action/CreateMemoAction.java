package com.jivesoftware.community.ext.memo.action;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jivesoftware.base.User;
import com.jivesoftware.base.UserNotFoundException;
import com.jivesoftware.community.BanException;
import com.jivesoftware.community.BanManager;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.RejectedException;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.util.DateUtils;
import com.jivesoftware.community.web.JiveResourceResolver;

public class CreateMemoAction extends EditMemoActionSupport {
	private Logger log = Logger.getLogger(CreateMemoAction.class);
    
    private JiveContainer container;

    private String cancelURL;

    private long memoId;

    @Override
    public String execute() {
        MemoBean bean = new MemoBean();
        
        bean.setTitle(this.getTitle());
        bean.setDescription(this.getDescription());
        bean.setContainerID(this.getContainer().getID());
        bean.setContainerType(this.getContainer().getObjectType());
        bean.setUserID(this.getUser().getID());
        bean.setCreationDate(new Date());
        bean.setModificationDate(new Date());
        bean.setStatusID(Status.PUBLISHED.intValue());

        try {
            JiveIterator<Image> images = EmptyJiveIterator.getInstance();
            if(getSessionKey() != null && getSession().get(getSessionKey()) != null) {
                Memo temp = (Memo) getSession().get(getSessionKey());
                images = temp.getImages();
            }
            
            memo = memoManager.saveMemo(bean , images);
        } catch (RejectedException mje) {
            Throwable nested = mje.getCause();
            if (nested instanceof BanException) {
                BanException be = (BanException) nested;
                ArrayList<String> params = new ArrayList<String>(2);

                if (be.getBan().getBanType() == BanManager.TYPE_BAN_USER) {
                    // load the banned user
                    User bannedUser = null;
                    try {
                        bannedUser = getJiveContext().getUserManager().getUser(be.getBan().getBannedUserID());
                    } catch (UserNotFoundException unfe) {
                        log.error("Failed to load user with ID: " + be.getBan().getBannedUserID(), unfe);
                    }
                    params.add(bannedUser.getUsername());
                    if (be.getBan().getExpirationDate() != null) {
                        DateUtils dateUtils = new DateUtils(request, getUser());
                        params.add(dateUtils.getMediumFormatDate(be.getBan().getExpirationDate()));
                        addActionError(getText("post.banned_user.temporary.text", params));
                    } else {
                        addActionError(getText("post.banned_user.permanent.text", params));
                    }
                } else {
                    params.add(request.getRemoteAddr());
                    if (be.getBan().getExpirationDate() != null) {
                        DateUtils dateUtils = new DateUtils(request, getUser());
                        params.add(dateUtils.getMediumFormatDate(be.getBan().getExpirationDate()));
                        addActionError(getText("post.banned_ip.temporary.text", params));
                    } else {
                        addActionError(getText("post.banned_ip.permanent.text", params));
                    }
                }
            } else {
                addActionError(mje.getMessage());
            }
        }

        memoId = memo.getID();
        
        if (memoId > 0) {
            tagActionUtil.saveTags(memo, validatedTags);
            getTagSetActionHelper().setTagSets(memo, getContentTagSets());
        }
        
        this.addActionMessage("Your memo was successfully added");

        this.cleanSession();
        if (isMemoModerated()) {
            return SUCCESS_MODERATION;
        }

        return SUCCESS;
    }
    
    public String cancel() {
        this.cleanSession();
        
        return CANCEL;
    }
    protected String createCancelURL() {
        return JiveResourceResolver.getJiveObjectURL(getContainer()) + "?view=memo";
    }
    
    public String getCancelURL() {
        if (cancelURL == null) {
            cancelURL = createCancelURL();
        }
        return cancelURL;
    }
    
    public long getMemoId() {
        return memoId;
    }
    
    public int getMemoType() {
        return new MemoObjectType().getID();
    }

    public boolean isEdit() {
        return false;
    }
    
    public JiveContainer getContainer() {
        return container;
    }

    public void setContainer(JiveContainer container) {
        this.container = container;
    }   
}
