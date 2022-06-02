package winapp.hajikadir.customer.model;

/**
 * Created by user on 14-Jul-16.
 */
public class NameValuePair {
    String name;
    String value;

    public  NameValuePair(String name,
            String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
