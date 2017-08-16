package couplegoals.com.couplegoals.model;

/**
 * Created by Brijesh on 8/16/2017.
 */

public class Post {
    String sTodayPostId;
    String sCoupleName;
    String sTodayPostMessage;
    String sPostedBy;
    String sWhen;
    String sTodayPostImagePath;

    public Post() {
    }

    public Post(String sTodayPostId, String sCoupleName, String sTodayPostMessage, String sPostedBy, String sWhen, String sTodayPostImagePath) {
        this.sTodayPostId = sTodayPostId;
        this.sCoupleName = sCoupleName;
        this.sTodayPostMessage = sTodayPostMessage;
        this.sPostedBy = sPostedBy;
        this.sWhen = sWhen;
        this.sTodayPostImagePath = sTodayPostImagePath;
    }

    public String getsTodayPostId() {
        return sTodayPostId;
    }

    public String getsCoupleName() {
        return sCoupleName;
    }

    public String getsTodayPostMessage() {
        return sTodayPostMessage;
    }

    public String getsPostedBy() {
        return sPostedBy;
    }

    public String getsWhen() {
        return sWhen;
    }

    public String getsTodayPostImagePath() {
        return sTodayPostImagePath;
    }
}
