package vn.com.mattana.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HAI on 3/1/2018.
 */

public class NoticeResult {

    @SerializedName("id")
    private String id ;
    @SerializedName("title")
    private String title ;
    @SerializedName("message")
    private String message ;
    @SerializedName("time")
    private String time ;
    @SerializedName("read")
    public int read ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
