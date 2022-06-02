package winapp.hajikadir.customer.activity;

/**
 * Created by Sathish on 2019-11-15.
 */

public class Paypalitems
{
    String productname, slno, price;
    int qty;

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}