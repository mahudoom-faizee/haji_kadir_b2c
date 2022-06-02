package winapp.hajikadir.customer.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by user on 15-Jul-16.
 */
public class Product implements Serializable  {
    private String sno;
    private String id;
    private String name;
    private String description;
    private String price;
    private String image;
    private String qty;
    private int quantity;
    private int balanceQty;
    private int sortOrder;
    private String model;
    private String taxClassId;
    private String rate;
    private String tax;
    private String modifierId;
    private String modifierPrice;
    private boolean isProductModifier;
    private double total;
    private Bitmap bitmap;
    private static String holidayCode;
    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(int balanceQty) {
        this.balanceQty = balanceQty;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTaxClassId() {
        return taxClassId;
    }

    public void setTaxClassId(String taxClassId) {
        this.taxClassId = taxClassId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }


    public String getModifierPrice() {
        return modifierPrice;
    }

    public void setModifierPrice(String modifierPrice) {
        this.modifierPrice = modifierPrice;
    }

    public boolean isProductModifier() {
        return isProductModifier;
    }

    public void setProductModifier(boolean productModifier) {
        isProductModifier = productModifier;
    }


    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static String getHolidayCode() {
        return holidayCode;
    }

    public static void setHolidayCode(String holidayCode) {
        Product.holidayCode = holidayCode;
    }
}