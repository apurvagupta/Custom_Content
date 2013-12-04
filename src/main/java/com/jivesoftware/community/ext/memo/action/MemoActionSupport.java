package com.jivesoftware.community.ext.memo.action;

import com.jivesoftware.community.action.ContentActionSupport;
import com.jivesoftware.community.ext.memo.Memo;

public class MemoActionSupport extends ContentActionSupport {
	protected Memo memo;

	public Memo getMemo() {
		return memo;
	}

	public void setMemo(Memo memo) {
		this.memo = memo;
	}

}
