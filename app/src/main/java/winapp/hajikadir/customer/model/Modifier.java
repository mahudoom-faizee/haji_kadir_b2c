package winapp.hajikadir.customer.model;

import java.io.Serializable;

/**
 * Created by user on 01-Apr-17.
 */

public class Modifier implements Serializable{
    public String sno;
    public String id;
    public String code;
    public String name;
    public String price;
    public boolean selected = false;

    public Modifier() {

    }
    public Modifier(String code, String name, String price, boolean selected) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
