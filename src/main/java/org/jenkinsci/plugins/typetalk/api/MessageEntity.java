package org.jenkinsci.plugins.typetalk.api;

public class MessageEntity {

    private String message;

    private Boolean ignoreHashtag;

    private Long[] talkIds;

    private Long replyTo;

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
