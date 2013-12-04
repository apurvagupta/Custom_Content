package com.jivesoftware.community.ext.memo.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.ResultSet;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.community.ext.memo.MemoBean;

@RunWith(JMock.class)
public class MemoBeanRowMapperTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoBeanRowMapper mapper = new MemoBeanRowMapper();
	
	@Ignore
	public void mapRow() throws Exception {
		final long id = 1000L;
		final long containerId = 2000L;
		final int containerType = 14;
		final long userId = 3000L;
		final int statusId = 1;
		final long creationDate = System.currentTimeMillis();
		final long modificationDate = System.currentTimeMillis();
		final String title = "title";
		final String description = "description";
		//Add your property values here
		
		final ResultSet rs = context.mock(ResultSet.class);
		
		context.checking(new Expectations() {{
			allowing(rs).getLong(1); will(returnValue(id));
			allowing(rs).getLong(2); will(returnValue(containerId));
			allowing(rs).getInt(3); will(returnValue(containerType));
			allowing(rs).getLong(4); will(returnValue(userId));
			allowing(rs).getInt(5); will(returnValue(statusId));
			allowing(rs).getLong(6); will(returnValue(creationDate));
			allowing(rs).getLong(7); will(returnValue(modificationDate));
			allowing(rs).getString(8); will(returnValue(title));
			allowing(rs).getString(9); will(returnValue(description));
			//Add expectations for your properties here
		}});
		
		MemoBean bean = mapper.mapRow(rs, 1);
		assertNotNull(bean);
		assertEquals(id, bean.getID());
		assertEquals(containerId, bean.getContainerID());
		assertEquals(containerType, bean.getContainerType());
		assertEquals(userId, bean.getUserID());
		assertEquals(statusId, bean.getStatusID());
		assertEquals(creationDate, bean.getCreationDate().getTime());
		assertEquals(modificationDate, bean.getModificationDate().getTime());
		assertEquals(title, bean.getTitle());
		assertEquals(description, bean.getDescription());
		//Add assertions for your properties here
	}	
	
	@Test
	//delete this test when you remove the @Ignore annotation from the mapRow() method
	public void test() {
	}	
}
