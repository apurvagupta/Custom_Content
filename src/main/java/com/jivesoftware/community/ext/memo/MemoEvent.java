package com.jivesoftware.community.ext.memo;

import java.util.Map;

import com.jivesoftware.base.event.ContentEvent;
import com.jivesoftware.community.ContainerAwareEntityDescriptor;

public class MemoEvent extends ContentEvent<MemoEvent.Type, ContainerAwareEntityDescriptor> {

    public MemoEvent() {
        super();
    }

    public MemoEvent(Type eventType, ContainerAwareEntityDescriptor payload, long actorID) {
        super(eventType, payload, actorID);
    }

    public MemoEvent(Type eventType, ContainerAwareEntityDescriptor payload, Map<String, ?> params, long actorID) {
        super(eventType, payload, params, actorID);
    }

    public MemoEvent(Type eventType, ContainerAwareEntityDescriptor payload, Map<String, ?> params) {
        super(eventType, payload, params);
    }

    public MemoEvent(Type eventType, ContainerAwareEntityDescriptor payload) {
        super(eventType, payload);
    }

    public enum Type {
        CREATED, DELETED, MODIFIED, RATED, VIEWED, MOVED, MODERATED;
    }

    public ModificationType getContentModificationType() {
        switch (this.getType()) {
        case CREATED:
            return ModificationType.Create;
        case DELETED:
            return ModificationType.Deleted;
        case MODIFIED:
            return ModificationType.Modify;
        case VIEWED:
            return ModificationType.View;
        case RATED:
            return ModificationType.Rate;
        case MOVED:
            return ModificationType.Move;
        case MODERATED:
            return ModificationType.Moderate;
        }
        
        throw new IllegalArgumentException("No content modification type registered for " + this.getType());
    }

}
