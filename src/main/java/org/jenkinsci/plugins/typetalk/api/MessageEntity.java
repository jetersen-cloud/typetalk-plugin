package org.jenkinsci.plugins.typetalk.api;

import com.google.api.client.util.Key;

public class MessageEntity {

    @Key
    private String message;

    @Key
    private Boolean ignoreHashtag;

    @Key
    private Long[] talkIds;

    @Key
    private Long replyTo;

    @Key
    private String[] fileKeys;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIgnoreHashtag(Boolean ignoreHashtag) {
        this.ignoreHashtag = ignoreHashtag;
    }

    public Boolean getIgnoreHashtag(Boolean ignoreHashtag) {
        return ignoreHashtag;
    }

    public Long[] getTalkIds() {
        if (talkIds == null) {
            return null;
        }
        return talkIds.clone();
    }

    public void setTalkIds(Long[] talkIds) {
        if (talkIds == null) {
            this.talkIds = null;
        } else {
            this.talkIds = talkIds.clone();
        }
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    public String[] getFileKeys() {
        if (fileKeys == null) {
            return null;
        }
        return fileKeys.clone();
    }

    public void setFileKeys(String[] fileKeys) {
        if (fileKeys == null) {
            this.fileKeys = null;
        } else {
            this.fileKeys = fileKeys.clone();
        }
    }

}
