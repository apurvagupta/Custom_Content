package com.jivesoftware.community.ext.memo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.jivesoftware.community.ext.memo.MemoBean;

public class MemoBeanRowMapper implements ParameterizedRowMapper<MemoBean> {

    public MemoBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        MemoBean memo = new MemoBean();
        int i = 1;
        
        memo.setID(rs.getLong(i++));
        memo.setContainerID(rs.getLong(i++));
        memo.setContainerType(rs.getInt(i++));
        memo.setUserID(rs.getLong(i++));
        memo.setStatusID(rs.getInt(i++));
        memo.setCreationDate(new Date(rs.getLong(i++)));
        memo.setModificationDate(new Date(rs.getLong(i++)));
        memo.setTitle(rs.getString(i++));
        memo.setDescription(rs.getString(i++));

        return memo;
    }
}
