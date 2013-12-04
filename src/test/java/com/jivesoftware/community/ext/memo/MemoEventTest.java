package com.jivesoftware.community.ext.memo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.jivesoftware.base.event.ContentEvent.ModificationType;
import com.jivesoftware.community.ext.memo.MemoEvent.Type;

public class MemoEventTest {
	private MemoEvent event;
	
	@Before
	public void doBefore() {
		
	}
	
	@Test
	public void getContentModificationTypeCreated() {
		event = new MemoEvent(Type.CREATED, null);
		assertSame(ModificationType.Create, event.getContentModificationType());
	}
	
	@Test
	public void getContentModificationTypeDeleted() {
		event = new MemoEvent(Type.DELETED, null);
		assertSame(ModificationType.Deleted, event.getContentModificationType());
	}
	
	@Test
	public void getContentModificationTypeModified() {
		event = new MemoEvent(Type.MODIFIED, null);
		assertSame(ModificationType.Modify, event.getContentModificationType());
	}
	
	@Test
	public void getContentModificationTypeViewed() {
		event = new MemoEvent(Type.VIEWED, null);
		assertSame(ModificationType.View, event.getContentModificationType());
	}
	
	@Test
	public void getContentModificationTypeRated() {
		event = new MemoEvent(Type.RATED, null);
		assertSame(ModificationType.Rate, event.getContentModificationType());
	}
	
	@Test
	public void getContentModificationTypeMoved() {
		event = new MemoEvent(Type.MOVED, null);
		assertSame(ModificationType.Move, event.getContentModificationType());
	}
	
	@Test
	public void getContentModificationTypeModerated() {
		event = new MemoEvent(Type.MODERATED, null);
		assertSame(ModificationType.Moderate, event.getContentModificationType());
	}
}
