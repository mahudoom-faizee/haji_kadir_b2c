package winapp.hajikadir.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 14-Jul-16.
 */
public class Category implements Parcelable {
    String id;
    String parentId;
    String name;
    String image;

    public Category() {

    }

    public Category(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        name = in.readString();
        image = in.readString();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(parentId);
        dest.writeString(name);
        dest.writeString(image);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}