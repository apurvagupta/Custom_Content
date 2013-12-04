package com.jivesoftware.community.ext.memo.impl;

import static com.jivesoftware.community.ext.memo.test.matcher.QueryCacheKeyContainsMatcher.aQueryCacheKeyContaining;
import static com.jivesoftware.community.ext.memo.test.matcher.MemoBeanMatcher.*;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import com.jivesoftware.base.User;
import com.jivesoftware.community.Community;
import com.jivesoftware.community.Image;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.JiveIterator;
import com.jivesoftware.community.RenderCacheManager;
import com.jivesoftware.community.TagManager;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.cache.Cache;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.ext.memo.dao.MemoDao;
import com.jivesoftware.community.impl.MemoImageHelper;
import com.jivesoftware.community.impl.CachedPreparedStatement;
import com.jivesoftware.community.impl.EmptyJiveIterator;
import com.jivesoftware.community.impl.QueryCache;
import com.jivesoftware.community.impl.QueryCacheManager;
import com.jivesoftware.community.internal.ExtendedCommunityManager;

@RunWith(JMock.class)
public class MemoManagerImplTest extends AbstractAnnotationAwareTransactionalTests {
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private MemoManagerImpl manager = new TestMemoManagerImpl();
	private MemoDao dao = context.mock(MemoDao.class);
	private QueryCacheManager queryCacheManager = context.mock(QueryCacheManager.class);
	private Cache<Long, MemoBean> memoCache = context.mock(Cache.class);
	private RenderCacheManager renderCacheManager = context.mock(RenderCacheManager.class);
	private QueryCache queryCache = context.mock(QueryCache.class);
	private TagManager tagManager = context.mock(TagManager.class);
	private ExtendedCommunityManager communityManager = context.mock(ExtendedCommunityManager.class);
	private MemoConverter converter = context.mock(MemoConverter.class);
	private MemoImageHelper imageHelper = context.mock(MemoImageHelper.class);
	
	private Community rootCommunity = context.mock(Community.class);
	private Memo memo = context.mock(Memo.class);
	private JiveContainer container = context.mock(JiveContainer.class);
	private User user = context.mock(User.class);

	private long rootCommunityID = 1L;
	private long memoID = 1000L;
	private long userID = 2000L;
	private long containerID = 3000L;
	
	@Before
	public void doBefore() {
		manager.setMemoDao(dao);
		manager.setQueryCacheManager(queryCacheManager);
		manager.setMemoCache(memoCache);
		manager.setRenderCacheManager(renderCacheManager);
		manager.setTagManager(tagManager);
		manager.setCommunityManager(communityManager);
		manager.setMemoConverter(converter);
		manager.setImageHelper(imageHelper);
		
		context.checking(new Expectations() {{
			allowing(queryCacheManager).getQueryCache(); will(returnValue(queryCache));
			allowing(communityManager).getRootCommunity(); will(returnValue(rootCommunity));
			
			allowing(memo).getID(); will(returnValue(memoID));
			allowing(memo).getUser(); will(returnValue(user));
			allowing(memo).getJiveContainer(); will(returnValue(container));

			allowing(rootCommunity).getID(); will(returnValue(rootCommunityID));
			allowing(user).getID(); will(returnValue(userID));
			allowing(container).getID(); will(returnValue(containerID));
		}});
	}
	
	@Test
	public void getMemoCountForContainer() {
		final int count = 36;

		final CachedPreparedStatement ps = new CachedPreparedStatement();

		context.checking(new Expectations() {{
			one(dao).getMemoListCountSQL(with(any(MemoResultFilter.class))); will(returnValue(ps));
			one(queryCache).getCount(with(aQueryCacheKeyContaining(MemoObjectType.MEMO_TYPE_ID, containerID, ps)), with(any(Boolean.class))); will(returnValue(count));
		}});
		
		assertEquals(count, manager.getMemoCount(container));
	}
	
	@Test
	public void getMemoCountForFilter() {
		final int count = 36;
		final MemoResultFilter filter = new MemoResultFilter();
		final CachedPreparedStatement ps = new CachedPreparedStatement();
		
		context.checking(new Expectations() {{
			one(dao).getMemoListCountSQL(filter); will(returnValue(ps));
			one(queryCache).getCount(with(aQueryCacheKeyContaining(MemoObjectType.MEMO_TYPE_ID, -1, ps)), with(any(Boolean.class))); will(returnValue(count));
		}});
		
		assertEquals(count, manager.getMemoCount(filter));
	}
	
	@Test
	public void getMemoCountForFilterByUser() {
		final int count = 36;
		final MemoResultFilter filter = new MemoResultFilter();
		final CachedPreparedStatement ps = new CachedPreparedStatement();
		
		context.checking(new Expectations() {{
			one(dao).getMemoListCountSQL(filter); will(returnValue(ps));
			one(queryCache).getCount(with(aQueryCacheKeyContaining(MemoObjectType.MEMO_TYPE_ID, userID, ps)), with(any(Boolean.class))); will(returnValue(count));
		}});
		
		filter.setUser(user);

		assertEquals(count, manager.getMemoCount(filter));
	}
	
	@Test
	public void getMemoCountForFilterByContainer() {
		final int count = 36;
		final MemoResultFilter filter = new MemoResultFilter();
		final CachedPreparedStatement ps = new CachedPreparedStatement();
		
		context.checking(new Expectations() {{
			one(dao).getMemoListCountSQL(filter); will(returnValue(ps));
			one(queryCache).getCount(with(aQueryCacheKeyContaining(MemoObjectType.MEMO_TYPE_ID, containerID, ps)), with(any(Boolean.class))); will(returnValue(count));
		}});
		
		filter.setContainer(container);

		assertEquals(count, manager.getMemoCount(filter));
	}
	
	@Test
	public void getAllMemosCount() {
		final int count = 36;
		final CachedPreparedStatement ps = new CachedPreparedStatement();

		context.checking(new Expectations() {{
			one(dao).getAllMemosCountSQL(); will(returnValue(ps));
			one(queryCache).getCount(with(aQueryCacheKeyContaining(MemoObjectType.MEMO_TYPE_ID, -1, ps)), with(any(Boolean.class))); will(returnValue(count));
		}});

		assertEquals(count, manager.getAllMemosCount());
	}
	
	@Test
	public void getMemosFilter(){
		final int start = 0;
		final int numResults = 25;
		final long[] block = {1000L, 1001L, 1002L};
		final MemoResultFilter filter = new MemoResultFilter();
		final CachedPreparedStatement sql = new CachedPreparedStatement();
		
		context.checking(new Expectations() {{
			one(dao).getMemoListSQL(filter); will(returnValue(sql));
			one(queryCache).getBlock(sql, MemoObjectType.MEMO_TYPE_ID, -1, start, true); will(returnValue(block));
		}});
		
		filter.setStartIndex(0);
		filter.setNumResults(numResults);
		
		JiveIterator<Memo> iter = manager.getMemos(filter);
		
		assertNotNull(iter);
	}
	
	@Test
	public void getAllMemos() {
		final long[] block = {1000L, 1001L, 1002L};
		final CachedPreparedStatement sql = new CachedPreparedStatement();
		final CachedPreparedStatement countSql = new CachedPreparedStatement();
		
		context.checking(new Expectations() {{
			one(dao).getAllMemosSQL(); will(returnValue(sql));
			one(dao).getAllMemosCountSQL(); will(returnValue(countSql));
			one(queryCache).getBlock(sql, MemoObjectType.MEMO_TYPE_ID, -1, 0, true); will(returnValue(block));
			one(queryCache).getCount(with(aQueryCacheKeyContaining(MemoObjectType.MEMO_TYPE_ID, -1, countSql)), with(any(Boolean.class))); will(returnValue(block.length));
		}});
		
		JiveIterator<Memo> iter = manager.getAllMemos();
		
		assertNotNull(iter);
	}

	@Test
	public void deleteMemo() {
		context.checking(new Expectations() {{
			one(dao).deleteMemo(memoID);
			one(memoCache).remove(memoID);
			exactly(4).of(queryCache).remove(with(any(Integer.class)), with(any(Long.class)));
			one(renderCacheManager).clearContentCache(memo);
			one(tagManager).removeAllTags(memo);
		}});
		
		manager.doDeleteMemo(memo);
	}
	
	@Test
	public void saveMemo() {
		final MemoBean bean = new MemoBean();
		final JiveIterator<Image> images = EmptyJiveIterator.getInstance();
		bean.setID(memoID);
		
		context.checking(new Expectations() {{
			one(dao).saveMemo(bean); will(returnValue(memoID));
			one(dao).getMemo(memoID); will(returnValue(bean));
			one(memoCache).put(memoID, bean);
			one(converter).convert(bean); will(returnValue(memo));
			one(imageHelper).saveImages(memo, images);
			one(memoCache).remove(memoID);
			ignoring(queryCache);
			ignoring(renderCacheManager);
		}});
		
		assertSame(memo, manager.doSaveMemo(bean, images));
	}
	
	@Ignore
	public void updateMemo() {
		final Memo newMemo = context.mock(Memo.class, "new");
		final MemoBean bean = new MemoBean();
		bean.setID(memoID);
		bean.setModificationDate(new Date(0));
		final JiveIterator<Image> images = EmptyJiveIterator.getInstance();
		
		final String newTitle = "newTitle";
		final String newDescription = "newDescription";
		//Add your new properties here
		final Status newStatus = JiveContentObject.Status.PUBLISHED;
		
		final MemoBean newBean = new MemoBean();
		newBean.setID(memoID);

		newBean.setTitle(newTitle);
		newBean.setDescription(newDescription);	
		//Populate your new property values here	
		newBean.setStatusID(newStatus.intValue());
		
		final Sequence converterSequence = context.sequence("sequence-name");
		
		context.checking(new Expectations() {{
			allowing(newMemo).getID(); will(returnValue(memoID));
			allowing(newMemo).getUser(); will(returnValue(user));
			allowing(newMemo).getJiveContainer(); will(returnValue(container));
			allowing(memoCache).get(memoID); will(returnValue(bean));
			one(converter).convert(bean); will(returnValue(memo)); inSequence(converterSequence);
			
			allowing(newMemo).getTitle(); will(returnValue(newTitle));
			allowing(newMemo).getDescription(); will(returnValue(newDescription));
			//Add expectations for you properties here			
			allowing(newMemo).getStatus(); will(returnValue(newStatus));
			
			one(dao).updateMemo(with(aMemoBeanContaining(newTitle, newDescription, newStatus)));
			
			one(dao).getMemo(memoID); will(returnValue(newBean));
			one(memoCache).put(memoID, newBean);
			one(converter).convert(newBean); will(returnValue(newMemo)); inSequence(converterSequence);
			one(imageHelper).saveImages(newMemo, images);

			one(memoCache).remove(memoID);
			ignoring(queryCache);
			ignoring(renderCacheManager);
		}});
		
		assertSame(newMemo, manager.doUpdateMemo(newMemo, images));
	}
	
	@Test
	public void updateMemoStatus() {
		final Status newStatus = JiveContentObject.Status.ABUSE_HIDDEN;
		final MemoBean bean = new MemoBean();
		final MemoBean newBean = new MemoBean();
		newBean.setID(memoID);
		final Memo newMemo = context.mock(Memo.class, "new");
		
		context.checking(new Expectations() {{
			allowing(memoCache).get(memoID); will(returnValue(bean));
			one(dao).updateMemo(with(aMemoBeanContaining(newStatus)));
			one(dao).getMemo(memoID); will(returnValue(newBean));
			one(converter).convert(newBean); will(returnValue(newMemo));
			one(memoCache).put(memoID, newBean);
		}});
		
		assertSame(newMemo, manager.doStatusUpdate(memo, newStatus));
	}
	
	@Test
	public void loadObject() throws Exception {
		final long id = 1000L;
		final MemoBean bean = new MemoBean();
		bean.setID(id);
		
		context.checking(new Expectations() {{
			one(memoCache).get(id); will(returnValue(bean));
			one(converter).convert(bean); will(returnValue(memo));
		}});
		
		assertSame(memo, manager.loadObject(id, true));
	}
	
	@Test
	public void loadBeanNoCache() {
		final long id = 1000L;
		final MemoBean bean = new MemoBean();
		bean.setID(id);
		
		context.checking(new Expectations() {{
			one(dao).getMemo(id); will(returnValue(bean));
			one(memoCache).put(id, bean);
		}});
		
		assertSame(bean, manager.loadBean(id, false));
	}
	
	@Test
	public void loadBeanNotInCache() {
		final long id = 1000L;
		final MemoBean bean = new MemoBean();
		bean.setID(id);
		
		context.checking(new Expectations() {{
			one(memoCache).get(id); will(returnValue(null));
			one(dao).getMemo(id); will(returnValue(bean));
			one(memoCache).put(id, bean);
		}});
		
		assertSame(bean, manager.loadBean(id, true));
	}
	
	@Test
	public void loadBeanFromCache() {
		final long id = 1000L;
		final MemoBean bean = new MemoBean();
		bean.setID(id);
		
		context.checking(new Expectations() {{
			one(memoCache).get(id); will(returnValue(bean));
		}});
		
		assertSame(bean, manager.loadBean(id, true));

	}
	
	@Test
	public void areContainersSame() {
		final JiveContainer to = context.mock(JiveContainer.class, "to");
		final JiveContainer from = context.mock(JiveContainer.class, "from");

		context.checking(new Expectations() {{
			allowing(to).getObjectType(); will(returnValue(14));
			allowing(from).getObjectType(); will(returnValue(14));
			allowing(to).getID(); will(returnValue(1000L));
			allowing(from).getID(); will(returnValue(2000L));
		}});
		
		assertTrue(manager.areContainersSame(to, to));
		assertFalse(manager.areContainersSame(to, from));
	}

	
	class TestMemoManagerImpl extends MemoManagerImpl {
		protected void updateContainer(Memo memo) {
			//do nothing
		}
		
		protected boolean isContentUpdate(Memo before, Memo after) {
			return false;
		}
	}
}
