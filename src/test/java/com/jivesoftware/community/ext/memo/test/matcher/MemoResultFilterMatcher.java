package com.jivesoftware.community.ext.memo.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.jivesoftware.base.User;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.ext.memo.MemoResultFilter;

public class MemoResultFilterMatcher extends TypeSafeMatcher<MemoResultFilter> {
	private User user;
	private JiveContainer container;
	private boolean recursive;
	
	@Factory
	public static Matcher<MemoResultFilter> aMemoResultFilterContaining(User user) {
		return new MemoResultFilterMatcher(user);
	}
	
	@Factory
	public static Matcher<MemoResultFilter> aMemoResultFilterContaining(JiveContainer container, boolean recursive) {
		return new MemoResultFilterMatcher(container, recursive);
	}
	
	public MemoResultFilterMatcher(User user) {
		this.user = user;
	}
	
	public MemoResultFilterMatcher(JiveContainer container, boolean recursive) {
		this.container = container;
		this.recursive = recursive;
	}
	
	public boolean matchesSafely(MemoResultFilter filter) {
		if(filter == null) {
			return false;
		}
		
		if(user != null) {
			if(filter.getUser() == null) {
				return false;
			} else if(user.getID() != filter.getUser().getID()) {
				return false;
			}
		}
		
		if(container != null) {
			if(filter.getContainer() == null) {
				return false;
			} else if(container.getID() != filter.getContainer().getID()) {
				return false;
			} else if(container.getObjectType() != filter.getContainer().getObjectType()) {
				return false;
			} else if(recursive != filter.isRecursive()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void describeTo(Description desc) {
		desc.appendText("a MemoResultFilter for user " + user);
	}
}
