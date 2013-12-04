package com.jivesoftware.community.ext.memo.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.jivesoftware.community.impl.CachedPreparedStatement;
import com.jivesoftware.community.impl.querycache.QueryCacheKey;

public class QueryCacheKeyContainsMatcher extends TypeSafeMatcher<QueryCacheKey> {
	private int objectType;
	private long objectID;
	private CachedPreparedStatement sql;
	
	
	@Factory
	public static Matcher<QueryCacheKey> aQueryCacheKeyContaining(int objectType, long objectID, CachedPreparedStatement sql) {
		return new QueryCacheKeyContainsMatcher(objectType, objectID, sql);
	}
	
	public QueryCacheKeyContainsMatcher(int objectType, long objectID, CachedPreparedStatement sql) {
		this.objectType = objectType;
		this.objectID = objectID;
		this.sql = sql;
	}
	
	@Override
	public boolean matchesSafely(QueryCacheKey key) {
		return key.getObjectType() == objectType && key.getObjectID() == objectID && key.getSQL().equals(sql);
	}

	@Override
	public void describeTo(Description desc) {
		desc.appendText("a QueryCacheKey for objectType " + objectType + " and objectID " + objectID);
	}

}
