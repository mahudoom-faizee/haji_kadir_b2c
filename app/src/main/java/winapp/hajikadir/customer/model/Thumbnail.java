package winapp.hajikadir.customer.model;

/**
 * Created by user on 15-Jul-16.
 */
public  class Thumbnail {
    static int thumbnailLeft;
    static int thumbnailTop;
    static int thumbnailWidth;
    static int thumbnailHeight;
    static String thumbnailName;
    static String thumbnailImage;

    public static int getThumbnailLeft() {
        return thumbnailLeft;
    }

    public static void setThumbnailLeft(int thumbnailLeft) {
        Thumbnail.thumbnailLeft = thumbnailLeft;
    }

    public static int getThumbnailTop() {
        return thumbnailTop;
    }

    public static void setThumbnailTop(int thumbnailTop) {
        Thumbnail.thumbnailTop = thumbnailTop;
    }

    public static int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public static void setThumbnailWidth(int thumbnailWidth) {
        Thumbnail.thumbnailWidth = thumbnailWidth;
    }

    public static int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public static void setThumbnailHeight(int thumbnailHeight) {
        Thumbnail.thumbnailHeight = thumbnailHeight;
    }

    public static String getThumbnailName() {
        return thumbnailName;
    }

    public static void setThumbnailName(String thumbnailName) {
        Thumbnail.thumbnailName = thumbnailName;
    }

    public static String getThumbnailImage() {
        return thumbnailImage;
    }

    public static void setThumbnailImage(String thumbnailImage) {
        Thumbnail.thumbnailImage = thumbnailImage;
    }
}

