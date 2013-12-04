package com.jivesoftware.community.ext.memo;

import static com.tangosol.util.ExternalizableHelper.readObject;
import static com.tangosol.util.ExternalizableHelper.readSafeUTF;
import static com.tangosol.util.ExternalizableHelper.writeObject;
import static com.tangosol.util.ExternalizableHelper.writeSafeUTF;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.jivesoftware.community.cache.CacheSizes;
import com.jivesoftware.community.cache.Cacheable;
import com.tangosol.io.ExternalizableLite;

public class MemoBean implements Cloneable, Cacheable, ExternalizableLite {
    private long id;
    private long containerID;
    private int containerType;
    private long userID;
    private int statusID;
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private String description;

    public long getID() {
        return id;
    }
    
    public void setID(long id) {
       this.id = id;
    }
    
    public long getContainerID() {
        return containerID;
    }
    
    public void setContainerID(long containerID) {
       this.containerID = containerID;
    }
    
    public int getContainerType() {
        return containerType;
    }
    
    public void setContainerType(int containerType) {
       this.containerType = containerType;
    }
    
    public long getUserID() {
        return userID;
    }
    
    public void setUserID(long userID) {
       this.userID = userID;
    }
    
    public int getStatusID() {
        return statusID;
    }
    
    public void setStatusID(int statusID) {
       this.statusID = statusID;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
       this.creationDate = creationDate;
    }
    
    public Date getModificationDate() {
        return modificationDate;
    }
    
    public void setModificationDate(Date modificationDate) {
       this.modificationDate = modificationDate;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
       this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
       this.description = description;
    }
    

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MemoBean)) {
            return false;
        }
        MemoBean that = (MemoBean) object;
        
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.id, that.id);
        builder.append(this.containerID, that.containerID);
        builder.append(this.containerType, that.containerType);
        builder.append(this.userID, that.userID);
        builder.append(this.statusID, that.statusID);
        builder.append(this.title, that.title);
        builder.append(this.description, that.description);
        
        return builder.isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(1582535347, 1773925573);
        
        builder.append(id);
        builder.append(containerID);
        builder.append(containerType);
        builder.append(userID);
        builder.append(statusID);
        builder.append(creationDate);
        builder.append(modificationDate);
        builder.append(title);
        builder.append(description);

        return builder.hashCode();
    }

    public void readExternal(DataInput in) throws IOException {
    
        id = in.readLong();
        containerID = in.readLong();
        containerType = in.readInt();
        userID = in.readLong();
        statusID = in.readInt();
        creationDate = (Date) readObject(in);
        modificationDate = (Date) readObject(in);
        title = readSafeUTF(in);
        description = readSafeUTF(in);
        
        //use readSafeUTF() to read in String data
    }

    public void writeExternal(DataOutput out) throws IOException {
    
        out.writeLong(id);
        out.writeLong(containerID);
        out.writeInt(containerType);
        out.writeLong(userID);
        out.writeInt(statusID);
        writeObject(out, creationDate);
        writeObject(out, modificationDate);
        writeSafeUTF(out, title);
        writeSafeUTF(out, description);
        
    }

    public int getCachedSize() {
        int size = 0;
        
        size += CacheSizes.sizeOfLong(); //id
        size += CacheSizes.sizeOfLong(); //containerID
        size += CacheSizes.sizeOfInt(); //containerType
        size += CacheSizes.sizeOfLong(); //userID
        size += CacheSizes.sizeOfInt(); //statusID
        size += CacheSizes.sizeOfDate(); //creationDate
        size += CacheSizes.sizeOfDate(); //modificationDate
        size += CacheSizes.sizeOfString(title); //title
        size += CacheSizes.sizeOfString(description); //description
        
        return size;
    }

    public Object clone() throws CloneNotSupportedException {
        return (MemoBean) super.clone();
    }
}
