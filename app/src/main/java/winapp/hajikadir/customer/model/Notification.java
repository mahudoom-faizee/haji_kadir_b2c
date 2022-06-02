package winapp.hajikadir.customer.model;

/**
 * Created by user on 12-Oct-17.
 */

public class Notification {

    String id;
    String title;
    String message;
    String isBackground;
    String payload;
    String imageUrl;
    String timestamp;

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

    public String getIsBackground() {
        return isBackground;
    }

    public void setIsBackground(String isBackground) {
        this.isBackground = isBackground;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
