package com.jivesoftware.community.ext.memo.dao;

import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.jivesoftware.base.User;
import com.jivesoftware.base.database.EntityDescriptorRowMapper;
import com.jivesoftware.base.database.LongRowMapper;
import com.jivesoftware.base.database.dao.DAOException;
import com.jivesoftware.base.database.dao.JiveJdbcDaoSupport;
import com.jivesoftware.base.database.dao.SequenceProvider;
import com.jivesoftware.community.ContentTag;
import com.jivesoftware.community.EntityDescriptor;
import com.jivesoftware.community.JiveConstants;
import com.jivesoftware.community.JiveContainer;
import com.jivesoftware.community.JiveContentObject;
import com.jivesoftware.community.ResultFilter;
import com.jivesoftware.community.TagSet;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.ext.memo.Memo;
import com.jivesoftware.community.ext.memo.MemoBean;
import com.jivesoftware.community.ext.memo.MemoObjectType;
import com.jivesoftware.community.ext.memo.MemoResultFilter;
import com.jivesoftware.community.impl.CachedPreparedStatement;
import com.jivesoftware.community.lifecycle.JiveApplication;

public class MemoDaoImpl extends JiveJdbcDaoSupport implements MemoDao {
    private static final Logger log = Logger.getLogger(MemoDaoImpl.class);
    
    private static final String SELECT_SQL = ""
        + " SELECT memoID, containerID, containerType, userID, status, creationDate, modificationDate, title, description "
        + "   FROM jiveMemo " + "  WHERE memoID = ? ";

    private static final String SELECT_ALL_MEMO_IDS_FOR_CONTAINER_SQL = ""
        + " SELECT memoID FROM jiveMemo WHERE containerID = ? AND containerType = ?";
    
    private static final String SELECT_ALL_MEMO_IDS_FOR_USER_SQL = ""
        + " SELECT memoID FROM jiveMemo WHERE userId = ?";
    
    private static final String INSERT_SQL = ""
        + " INSERT INTO jiveMemo ( "
        + "        memoID, containerID, containerType, userID, status, creationDate, modificationDate, title, description "
        + " ) VALUES ( "
        + "        :ID, :containerID, :containerType, :userID, :statusID, :creationDate, :modificationDate, :title, :description "
        + " ); ";
    

    
    private static final String UPDATE_SQL = "" 
        + " UPDATE jiveMemo " 
        + "    SET containerID = :containerID, containerType = :containerType, userID = :userID, status = :statusID, creationDate = :creationDate, modificationDate = :modificationDate, title = :title, description = :description "
        + " WHERE memoID = :ID ";
        

    
    private static final String DELETE_SQL = "" + " DELETE FROM jiveMemo WHERE memoID = ? ";
    
    private static final String MOVE_SQL = ""
        + " UPDATE jiveMemo SET containerID = ?, containerType = ? WHERE memoID =? AND containerID = ? and containerType = ?";
    
    private static final String FREQUENT_MEMO_CONTAINERS = "SELECT containerType, containerID from jiveMemo"
        + " WHERE userID = ? GROUP BY containerType, containerID ORDER BY MAX(creationDate) DESC ";
    
    private static final String SELECT = "SELECT";
    private static final String COUNT = " count(*)";
    private static final String MEMO_FIELDS = " DISTINCT memoId, modificationDate";
    private static final String FROM = " FROM";
    private static final String JIVE_MEMO = " jiveMemo ";
    private static final String WHERE = " WHERE 1=1";
    private static final String CONTAINER = "containerId = ? and containerType = ?";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String USER = "userId = ?";
    private static final String STATUS_IS = "status = ?";
    
    private static final String JIVE_OBJECT_TAG = " jiveObjectTag ot";
    private static final String JIVE_OBJECT_TAG_SET = " jiveObjectTagSet ots";
    
    private static final String TAGS_SUBQUERY = ""
        + "memoID IN (SELECT objectID FROM jiveObjectTag WHERE objectType = " + MemoObjectType.MEMO_TYPE_ID
        + " AND tagID IN ({0})"
        + " GROUP BY objectID HAVING COUNT(*) >= {1})";
    private static final String TAGS_JOIN = "ot.objectID = memoID and ot.objectType = " + MemoObjectType.MEMO_TYPE_ID
        + " AND ot.tagID in ({0})";     
    private static final String TAG_SETS_JOIN = "memoID = ots.objectID AND ots.objectType = " + MemoObjectType.MEMO_TYPE_ID;
    private static final String TAG_SETS_SINGLE = "ots.tagSetID = ?";
    private static final String TAG_SETS_MULTIPLE = "ots.tagSetID in ({0})";
    private static final String ORDER_BY = " order by ";
    private static final String MODIFICATION_DATE = "modificationDate";
    private static final String DESCENDING = " desc";
    
    private ParameterizedRowMapper<MemoBean> rowMapper;
    private SequenceProvider sequenceProvider;
    
    public CachedPreparedStatement getMemoListSQL(MemoResultFilter resultFilter) {
        return this.getMemoSQL(resultFilter, false);
    }
    
    public CachedPreparedStatement getMemoListCountSQL(MemoResultFilter resultFilter) {
        return this.getMemoSQL(resultFilter, true);
    }
    
    protected CachedPreparedStatement getMemoSQL(MemoResultFilter resultFilter, boolean countQuery) {
        JiveContainer container = resultFilter.getContainer();
        boolean isRoot = container == null
                || (container.getObjectType() == JiveConstants.COMMUNITY && container.getID() == JiveApplication.getContext().getCommunityManager().getRootCommunity().getID());
    
        boolean forContainer = resultFilter.getContainer() != null;
        boolean forUser = resultFilter.getUser() != null;
        List<JiveContentObject.Status> statusMatches = resultFilter.getStatus();

    
        StringBuilder sql = new StringBuilder(SELECT);
        sql.append(countQuery ? COUNT : MEMO_FIELDS);
        sql.append(FROM);
        
        sql.append(JIVE_MEMO);
        
        if (resultFilter.getTagSets().size() > 0) {
            sql.append(",").append(JIVE_OBJECT_TAG_SET);
        }

        if (resultFilter.getTags().size() > 0 && !resultFilter.isAllTagsRequired()) {
            sql.append(",").append(JIVE_OBJECT_TAG);
        }
        sql.append(WHERE);

        if (forContainer && !isRoot) {
            sql.append(AND);
            sql.append(CONTAINER);
        }
    
        if (forUser) {
            sql.append(AND);
            sql.append(USER);
        }
    
        if(statusMatches != null && !statusMatches.isEmpty()) {
            sql.append(AND);
            
            sql.append("(");
            
            for(int i = 0; i < statusMatches.size(); i++) {
                sql.append(STATUS_IS);
                
                if(i < statusMatches.size() - 1) {
                    sql.append(OR);
                }
            }

            sql.append(")");
        }
        
        if (resultFilter.getTagSets().size() > 0) {
            sql.append(AND);
            sql.append(TAG_SETS_JOIN);
            sql.append(AND);

            if(resultFilter.getTagSets().size() == 1) {
                sql.append(TAG_SETS_SINGLE);
            } else {
                StringBuffer tagSetParams = new StringBuffer();
                String sep = "";
                for(TagSet set : resultFilter.getTagSets()) {
                    tagSetParams.append(sep).append("?");
                    sep = ",";
                }
                sql.append(MessageFormat.format(TAG_SETS_MULTIPLE, tagSetParams));
            }
        }

        if (resultFilter.getTags().size() > 0) {
            StringBuffer tags = new StringBuffer();
            String sep = "";
            for(ContentTag tag : resultFilter.getTags()) {
                tags.append(sep).append("?");
                sep = ",";
            }

            sql.append(AND);
            if (resultFilter.isAllTagsRequired()) {
                sql.append(MessageFormat.format(TAGS_SUBQUERY, tags.toString(), resultFilter.getTags().size()));
            } else {
                sql.append(MessageFormat.format(TAGS_JOIN, tags.toString()));
            }
        }
    
        // order by
        if (!countQuery) {
            sql.append(ORDER_BY).append(MODIFICATION_DATE).append(resultFilter.getSortOrder() == ResultFilter.DESCENDING ? DESCENDING : "");
        }
    
        CachedPreparedStatement ps = new CachedPreparedStatement(sql.toString());
    
        if (forContainer && !isRoot) {
            ps.addLong(resultFilter.getContainer().getID());
            ps.addInt(resultFilter.getContainer().getObjectType());
        }
    
        if (forUser) {
            ps.addLong(resultFilter.getUser().getID());
        }
        
        if(statusMatches != null && !statusMatches.isEmpty()) {
            for(Status status : statusMatches) {
                ps.addInt(status.intValue());
            }
        }
        
        if(resultFilter.getTagSets() != null) {
            for(TagSet tagSet : resultFilter.getTagSets()) {
                ps.addLong(tagSet.getID());
            }
        }

        if(resultFilter.getTags() != null) {
            for(ContentTag tag : resultFilter.getTags()) {
                ps.addLong(tag.getID());
            }
        }
        return ps;
    }
    
    public CachedPreparedStatement getAllMemosCountSQL() {
        return this.getAllMemosSQL(true);
    }
    
    public CachedPreparedStatement getAllMemosSQL() {
        return this.getAllMemosSQL(false);
    }
    
    protected CachedPreparedStatement getAllMemosSQL(boolean countQuery) {
        StringBuilder sql = new StringBuilder();
        sql.append(SELECT).append(countQuery ? COUNT : MEMO_FIELDS);
        sql.append(FROM).append(JIVE_MEMO);
    
        if (!countQuery) {
            sql.append(ORDER_BY).append(MODIFICATION_DATE).append(DESCENDING);
        }
    
        return new CachedPreparedStatement(sql.toString());
    }
    
    public List<Long> getAllMemoIDs(JiveContainer container) {
        return getSimpleJdbcTemplate().query(SELECT_ALL_MEMO_IDS_FOR_CONTAINER_SQL, LongRowMapper.getLongRowMapper(), container.getID(), container.getObjectType());
    }
    
    public List<Long> getAllMemoIDs(User user) {
        return getSimpleJdbcTemplate().query(SELECT_ALL_MEMO_IDS_FOR_USER_SQL, LongRowMapper.getLongRowMapper(), user.getID());
    }
    
    public void deleteMemos(List<Long> memoIDs) {
        for(long memoID : memoIDs) {
            deleteMemo(memoID);
        }
    }
    
    public void deleteMemo(long id) {
        getSimpleJdbcTemplate().update(DELETE_SQL, id);
    }
    
    public MemoBean getMemo(long id) {
        try {
           return getSimpleJdbcTemplate().queryForObject(SELECT_SQL, rowMapper, id);
        } catch(EmptyResultDataAccessException e) {
           return null;
        }
    }
    
    public long saveMemo(MemoBean bean) {
        long memoId = sequenceProvider.nextId();
        bean.setID(memoId);
        SqlParameterSource namedParameters = new JiveBeanPropertySqlParameterSource(bean);
        getSimpleJdbcTemplate().update(INSERT_SQL, namedParameters);
        
        return memoId;
    }
    
    public void updateMemo(MemoBean object) {
        SqlParameterSource namedParameters = new JiveBeanPropertySqlParameterSource(object);
        getSimpleJdbcTemplate().update(UPDATE_SQL, namedParameters);
    }
    
    public void moveMemo(Memo memo, JiveContainer destination) {
        getSimpleJdbcTemplate().update(MOVE_SQL, destination.getID(), destination.getObjectType(), memo.getID(), memo.getContainerID(), memo.getContainerType());
    }
    
    public List<EntityDescriptor> getFrequentMemoContainers(long userID) {
        try {
            return getSimpleJdbcTemplate().query(FREQUENT_MEMO_CONTAINERS, new EntityDescriptorRowMapper(), userID);
        }
        catch (Exception ex) {
            final String message = "Failed to retrieve frequent memo containers for user: " + userID;
            log.error(message, ex);
            throw new DAOException(message, ex);
        }
    }

    public void setRowMapper(ParameterizedRowMapper<MemoBean> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public void setSequenceProvider(SequenceProvider sequenceProvider) {
        this.sequenceProvider = sequenceProvider;
    }
}
