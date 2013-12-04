package com.jivesoftware.community.ext.memo.test.matcher;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.ext.memo.MemoBean;

public class MemoBeanMatcher extends TypeSafeMatcher<MemoBean> {
	private String title;
	private String description;
	private Status status;
	
	@Factory
	public static Matcher<MemoBean> aMemoBeanContaining(String title, String description, Status status) {
		return new MemoBeanMatcher(title, description, status);
	}
	
	@Factory
	public static Matcher<MemoBean> aMemoBeanContaining(Status status) {
		return new MemoBeanMatcher(status);
	}
	
	public MemoBeanMatcher(String title, String description, Status status) {
		this.title = title;
		this.description = description;
		this.status = status;
	}
	
	public MemoBeanMatcher(Status status) {
		this(null, null, status);
	}
	
	@Override
	public boolean matchesSafely(MemoBean bean) {
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(title, bean.getTitle());
		builder.append(description, bean.getDescription());
		builder.append(status.intValue(), bean.getStatusID());
		
		return builder.isEquals();
	}
	
	@Override
	public void describeTo(Description desc) {
		desc.appendText("a MemoBean");
	}
}
