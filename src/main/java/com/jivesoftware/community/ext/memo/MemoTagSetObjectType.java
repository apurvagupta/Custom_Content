package com.jivesoftware.community.ext.memo;

public class MemoTagSetObjectType {
    private static MemoTagSetObjectType instance = new MemoTagSetObjectType();
    
    private MemoTagSetObjectType() {
        
    }
    
    public int getTypeID() {
        return MemoObjectType.MEMO_TYPE_ID;
    }
    
    public int getKey() {
        return MemoObjectType.MEMO_TYPE_ID;
    }
    
    public String getCode() {
        return new MemoObjectType().getCode();
    }
    
    public boolean isEquals(int typeID) {
        return getTypeID() == typeID;
    }
    
    public static MemoTagSetObjectType getInstance() {
        return instance;
    }
}
