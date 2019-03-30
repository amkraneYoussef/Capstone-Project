package chat.goulmima.com.letschat.POJOS;

public class TextMessage {
    private String mSenderUserID, mText,mMediaUrl;

    public TextMessage(String user, String text, String mediaUrl) {
        this.mSenderUserID = user;
        this.mText = text;
        this.mMediaUrl = mediaUrl;
    }

    public TextMessage(){}

    public String getmSenderUserID() {
        return mSenderUserID;
    }

    public void setmSenderUserID(String user) {
        this.mSenderUserID = user;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String text) {
        this.mText = text;
    }

    public String getMediaUrl() {
        return mMediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mMediaUrl = mediaUrl;
    }
}
