package com.jivesoftware.community.ext.memo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.struts2.config.Results;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jivesoftware.base.AuthToken;
import com.jivesoftware.community.NotFoundException;
import com.jivesoftware.community.impl.BlockObjectFactory;
import com.jivesoftware.community.impl.DummyAuthToken;
import com.jivesoftware.community.impl.ObjectFactory;
import com.jivesoftware.community.proxy.IteratorProxy;

@RunWith(JMock.class)
public class MemoObjectFactoryTest {
	private Mockery context = new JUnit4Mockery();
	
	private MemoObjectFactory factory = new MemoObjectFactory();
	
	private ObjectFactory<Memo> objectFactory = context.mock(ObjectFactory.class, "objectFactory");
	private ObjectFactory<Memo> proxiedObjectFactory = context.mock(ObjectFactory.class, "proxiedObjectFactory");
	private IteratorProxy.ProxyFactory<Memo> proxyFactory = context.mock(IteratorProxy.ProxyFactory.class);
	
	@Before
	public void doBefore() {
		factory.setObjectFactory(objectFactory);
		factory.setProxiedObjectFactory(proxiedObjectFactory);
		factory.setProxyFactory(proxyFactory);
	}
	
	@Test
	public void createProxy() {
		final Memo memo = context.mock(Memo.class, "object");
		final Memo memoProxy = context.mock(Memo.class, "proxy");
		final AuthToken authToken = new DummyAuthToken(0L);
		
		context.checking(new Expectations() {{
			one(proxyFactory).createProxy(memo, authToken); will(returnValue(memoProxy));
		}});
		
		assertSame(memoProxy, factory.createProxy(memo, authToken));
	}
	
	@Test
	public void loadObjectLong() throws Exception {
		final Memo memo = context.mock(Memo.class);
		final long id = 1000L;
		
		context.checking(new Expectations() {{
			one(objectFactory).loadObject(id); will(returnValue(memo));
		}});
		
		assertSame(memo, factory.loadObject(id));
	}
	
	@Test (expected=NotFoundException.class) 
	public void loadObjectLongNotFound() throws Exception {
		final long id = 1000L;
		
		context.checking(new Expectations() {{
			one(objectFactory).loadObject(id); will(throwException(new NotFoundException()));
		}});
		
		factory.loadObject(id);
	}
	
	@Test
	public void loadObjectString() throws Exception {
		final Memo memo = context.mock(Memo.class);
		final String id = "1000";
		
		context.checking(new Expectations() {{
			one(objectFactory).loadObject(id); will(returnValue(memo));
		}});
		
		assertSame(memo, factory.loadObject(id));
	}
	
	@Test (expected=NotFoundException.class) 
	public void loadObjectStringNotFound() throws Exception {
		final String id = "1000";
		
		context.checking(new Expectations() {{
			one(objectFactory).loadObject(id); will(throwException(new NotFoundException()));
		}});
		
		factory.loadObject(id);
	}
	
	@Test
	public void loadProxyObjectLong() throws Exception {
		final Memo memo = context.mock(Memo.class);
		final long id = 1000L;
		
		context.checking(new Expectations() {{
			one(proxiedObjectFactory).loadObject(id); will(returnValue(memo));
		}});
		
		assertSame(memo, factory.loadProxyObject(id));
	}
	
	@Test (expected=NotFoundException.class) 
	public void loadProxyObjectLongNotFound() throws Exception {
		final long id = 1000L;
		
		context.checking(new Expectations() {{
			one(proxiedObjectFactory).loadObject(id); will(throwException(new NotFoundException()));
		}});
		
		factory.loadProxyObject(id);
	}
	
	@Test
	public void loadProxyObjectString() throws Exception {
		final Memo memo = context.mock(Memo.class);
		final String id = "1000";
		
		context.checking(new Expectations() {{
			one(proxiedObjectFactory).loadObject(id); will(returnValue(memo));
		}});
		
		assertSame(memo, factory.loadProxyObject(id));
	}
	
	@Test (expected=NotFoundException.class) 
	public void loadProxyObjectStringNotFound() throws Exception {
		final String id = "1000";
		
		context.checking(new Expectations() {{
			one(proxiedObjectFactory).loadObject(id); will(throwException(new NotFoundException()));
		}});
		
		factory.loadProxyObject(id);
	}
	
	@Test 
	public void loadObjectsFromBlockObjectFactory() {
		final BlockObjectFactory blockObjectFactory = context.mock(BlockObjectFactory.class);
		
		final List<Long> ids = Arrays.asList(1000L, 2000L, 3000L);
		final List<Memo> memos = new ArrayList<Memo>();
		memos.add(context.mock(Memo.class, "br1000"));
		memos.add(context.mock(Memo.class, "br2000"));
		memos.add(context.mock(Memo.class, "br3000"));
		
		factory.setObjectFactory(blockObjectFactory);
		
		context.checking(new Expectations() {{
			one(blockObjectFactory).loadObjects(ids); will(returnValue(memos));
		}});
		
		assertSame(memos, factory.loadObjects(ids));
	}
	
	@Test
	public void loadObjects() throws Exception {
		final long br1000Id = 1000L;
		final long br2000Id = 2000L;
		final long br3000Id = 3000L;
	
		final Memo br1000 = context.mock(Memo.class, "br1000");
		final Memo br2000 = context.mock(Memo.class, "br2000");
		final Memo br3000 = context.mock(Memo.class, "br3000");
		
		final List<Long> ids = Arrays.asList(br1000Id, br2000Id, br3000Id);
		final List<Memo> memos = Arrays.asList(br1000, br2000, br3000);
		
		context.checking(new Expectations() {{
			one(objectFactory).loadObject(br1000Id); will(returnValue(br1000));
			one(objectFactory).loadObject(br2000Id); will(returnValue(br2000));
			one(objectFactory).loadObject(br3000Id); will(returnValue(br3000));
		}});
		
		List<Memo> result = factory.loadObjects(ids);
		
		assertNotNull(result);
		assertEquals(memos.size(), result.size());
		
		for(Memo br : memos) {
			assertTrue(result.contains(br));
		}
	}
}
