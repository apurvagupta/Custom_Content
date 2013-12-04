package com.jivesoftware.community.ext.memo;

import com.jivesoftware.community.CommentableTypeInfoProvider;
import com.jivesoftware.community.ContainableTypeInfoProvider;
import com.jivesoftware.community.FilteredContentProvider;
import com.jivesoftware.community.RecentActivityInfoProvider;
import com.jivesoftware.community.RecentContentInfoProvider;
import com.jivesoftware.community.RecentHistoryProvider;
import com.jivesoftware.community.TaggableTypeInfoProvider;
import com.jivesoftware.community.TypeUIProvider;
import com.jivesoftware.community.UserBarProvider;
import com.jivesoftware.community.WatchInfoProvider;
import com.jivesoftware.community.favorites.type.FavoritableType;
import com.jivesoftware.community.favorites.type.FavoriteInfoProvider;
import com.jivesoftware.community.moderation.ModeratableType;
import com.jivesoftware.community.moderation.ModerationStrategy;
import com.jivesoftware.community.moderation.ModerationUIProvider;
import com.jivesoftware.community.objecttype.CommentableType;
import com.jivesoftware.community.objecttype.ContainableType;
import com.jivesoftware.community.objecttype.ContainableTypeManager;
import com.jivesoftware.community.objecttype.ContentNotificationProvider;
import com.jivesoftware.community.objecttype.ContentObjectType;
import com.jivesoftware.community.objecttype.ContentObjectTypeInfoProvider;
import com.jivesoftware.community.objecttype.EntitlementCheckableType;
import com.jivesoftware.community.objecttype.EntitlementCheckProvider;
import com.jivesoftware.community.objecttype.FeaturedContentEnabledType;
import com.jivesoftware.community.objecttype.FilteredIndexableType;
import com.jivesoftware.community.objecttype.JiveObjectFactory;
import com.jivesoftware.community.objecttype.MoveContentProvider;
import com.jivesoftware.community.objecttype.MoveableContentType;
import com.jivesoftware.community.objecttype.NotificationEnabledType;
import com.jivesoftware.community.objecttype.RecentActivityEnabledType;
import com.jivesoftware.community.objecttype.RecentContentEnabledType;
import com.jivesoftware.community.objecttype.RecentHistoryEnabledType;
import com.jivesoftware.community.objecttype.TaggableType;
import com.jivesoftware.community.objecttype.ViewCountSupport;
import com.jivesoftware.community.objecttype.ViewCountSupportedType;
import com.jivesoftware.community.objecttype.WatchableType;
import com.jivesoftware.community.search.IndexInfoProvider;
import com.jivesoftware.community.tags.type.CommunityTaggableType;
import com.jivesoftware.community.tags.type.GenericTaggableTypeInfoProvider;

public class MemoObjectType implements ContentObjectType, ContainableType, NotificationEnabledType<Memo>, CommunityTaggableType, TaggableType, RecentContentEnabledType, FilteredIndexableType, ModeratableType, RecentActivityEnabledType, RecentHistoryEnabledType, CommentableType, FavoritableType, WatchableType, EntitlementCheckableType<Memo>, FeaturedContentEnabledType, MoveableContentType, ViewCountSupportedType {
    private static final String MEMO_TYPE_CODE = "memo";
    public static final int MEMO_TYPE_ID = Math.abs(MEMO_TYPE_CODE.hashCode());
    
    public static final String MEMOS_ENABLED_PROP = "memos.enabled";

    private JiveObjectFactory<Memo> jiveObjectFactory;

    private TypeUIProvider typeUiProvider;
    private UserBarProvider userBarProvider;
    private ContentObjectTypeInfoProvider contentObjectTypeInfoProvider;

    private ContainableTypeInfoProvider containableTypeInfoProvider;
    private ContainableTypeManager containableTypeManager;
    private ContentNotificationProvider<Memo> contentNotificationProvider;
    private FilteredContentProvider filteredContentProvider;
    private GenericTaggableTypeInfoProvider genericTaggableTypeInfoProvider;
    private TaggableTypeInfoProvider taggableTypeInfoProvider;
    private RecentContentInfoProvider recentContentInfoProvider;
    private IndexInfoProvider indexInfoProvider;
    private ModerationStrategy moderationStrategy;
    private ModerationUIProvider moderationUIProvider;
    private RecentActivityInfoProvider recentActivityInfoProvider;
    private RecentHistoryProvider recentHistoryProvider;
    private CommentableTypeInfoProvider commentableTypeInfoProvider;
    private FavoriteInfoProvider<Memo, MemoObjectType> favoriteInfoProvider;
    private WatchInfoProvider watchInfoProvider;
    private EntitlementCheckProvider<Memo> entitlementCheckProvider;
    private MoveContentProvider<Memo> moveContentProvider;
    private ViewCountSupport viewCountSupport;
    
    public String getCode() {
        return MEMO_TYPE_CODE;
    }

    public int getID() {
        return MEMO_TYPE_ID;
    }

    public JiveObjectFactory<Memo> getObjectFactory() {
        return jiveObjectFactory;
    }

    public boolean isEnabled() {
        return true;
    }

    public TypeUIProvider getTypeUIProvider() {
        return typeUiProvider;
    }

    public void setTypeUiProvider(TypeUIProvider typeUiProvider) {
        this.typeUiProvider = typeUiProvider;
    }

    public UserBarProvider getUserBarProvider() {
        return userBarProvider;
    }

    public void setUserBarProvider(UserBarProvider userBarProvider) {
        this.userBarProvider = userBarProvider;
    }

    public void setJiveObjectFactory(JiveObjectFactory<Memo> jiveObjectFactory) {
        this.jiveObjectFactory = jiveObjectFactory;
    }

    public ContentObjectTypeInfoProvider getContentObjectTypeInfoProvider() {
        return contentObjectTypeInfoProvider;
    }

    public void setContentObjectTypeInfoProvider(ContentObjectTypeInfoProvider contentObjectTypeInfoProvider) {
        this.contentObjectTypeInfoProvider = contentObjectTypeInfoProvider;
    }

    public ContainableTypeInfoProvider getContainableTypeInfoProvider() {
        return containableTypeInfoProvider;
    }

    public void setContainableTypeInfoProvider(ContainableTypeInfoProvider containableTypeInfoProvider) {
        this.containableTypeInfoProvider = containableTypeInfoProvider;
    }

    public ContainableTypeManager getContainableTypeManager() {
        return containableTypeManager;
    }

    public void setContainableTypeManager(ContainableTypeManager containableTypeManager) {
        this.containableTypeManager = containableTypeManager;
    }
	
    public ContentNotificationProvider<Memo> getContentNotificationProvider() {
        return contentNotificationProvider;
    }

    public void setContentNotificationProvider(ContentNotificationProvider<Memo> contentNotificationProvider) {
        this.contentNotificationProvider = contentNotificationProvider;
    }
	
    public FilteredContentProvider getFilteredContentProvider() {
        return filteredContentProvider;
    }

    public void setFilteredContentProvider(FilteredContentProvider filteredContentProvider) {
        this.filteredContentProvider = filteredContentProvider;
    }
	
    public GenericTaggableTypeInfoProvider getGenericTaggableTypeInfoProvider() {
        return genericTaggableTypeInfoProvider;
    }

    public void setGenericTaggableTypeInfoProvider(GenericTaggableTypeInfoProvider genericTaggableTypeInfoProvider) {
        this.genericTaggableTypeInfoProvider = genericTaggableTypeInfoProvider;
    }

    public TaggableTypeInfoProvider getTaggableTypeInfoProvider() {
        return taggableTypeInfoProvider;
    }

    public void setTaggableTypeInfoProvider(TaggableTypeInfoProvider taggableTypeInfoProvider) {
        this.taggableTypeInfoProvider = taggableTypeInfoProvider;
    }
	
    public RecentContentInfoProvider getRecentContentInfoProvider() {
        return recentContentInfoProvider;
    }

    public void setRecentContentInfoProvider(RecentContentInfoProvider recentContentInfoProvider) {
        this.recentContentInfoProvider = recentContentInfoProvider;
    }
	
    public IndexInfoProvider getIndexInfoProvider() {
        return indexInfoProvider;
    }

    public void setIndexInfoProvider(IndexInfoProvider indexInfoProvider) {
        this.indexInfoProvider = indexInfoProvider;
    }
	
    public ModerationStrategy getModerationStrategy() {
        return moderationStrategy;
    }

    public void setModerationStrategy(ModerationStrategy moderationStrategy) {
        this.moderationStrategy = moderationStrategy;
    }

    public ModerationUIProvider getModerationUIProvider() {
        return moderationUIProvider;
    }

    public void setModerationUIProvider(ModerationUIProvider moderationUIProvider) {
        this.moderationUIProvider = moderationUIProvider;
    }
	
    public RecentActivityInfoProvider getRecentActivityInfoProvider() {
        return recentActivityInfoProvider;
    }

    public void setRecentActivityInfoProvider(RecentActivityInfoProvider recentActivityInfoProvider) {
        this.recentActivityInfoProvider = recentActivityInfoProvider;
    }
	
    public RecentHistoryProvider getRecentHistoryProvider() {
        return recentHistoryProvider;
    }

    public void setRecentHistoryProvider(RecentHistoryProvider recentHistoryProvider) {
        this.recentHistoryProvider = recentHistoryProvider;
    }
	
    public CommentableTypeInfoProvider getCommentableTypeInfoProvider() {
        return commentableTypeInfoProvider;
    }

    public void setCommentableTypeInfoProvider(CommentableTypeInfoProvider commentableTypeInfoProvider) {
        this.commentableTypeInfoProvider = commentableTypeInfoProvider;
    }
	
    public FavoriteInfoProvider<Memo, MemoObjectType> getFavoriteInfoProvider() {
        return favoriteInfoProvider;
    }

    public void setFavoriteInfoProvider(FavoriteInfoProvider<Memo, MemoObjectType> favoriteInfoProvider) {
        this.favoriteInfoProvider = favoriteInfoProvider;
    }

    public boolean isFavoriteViewable() {
        return false;
    }
	
    public WatchInfoProvider getWatchInfoProvider() {
        return watchInfoProvider;
    }

    public void setWatchInfoProvider(WatchInfoProvider watchInfoProvider) {
        this.watchInfoProvider = watchInfoProvider;
    }
	
	public MoveContentProvider<Memo> getMoveContentProvider() {
	    return moveContentProvider;
	}
	
	public void setMoveContentProvider(MoveContentProvider<Memo> moveContentProvider) {
	    this.moveContentProvider = moveContentProvider;
	}
	
	public ViewCountSupport getViewCountSupport() {
	    return viewCountSupport;
	}
	
	public void setViewCountSupport(ViewCountSupport viewCountSupport) {
	    this.viewCountSupport = viewCountSupport;
	}
	
	public EntitlementCheckProvider<Memo> getEntitlementCheckProvider() {
	    return entitlementCheckProvider;
	}
	
	public void setEntitlementCheckProvider(EntitlementCheckProvider<Memo> entitlementCheckProvider) {
	    this.entitlementCheckProvider = entitlementCheckProvider;
	}
    
}
