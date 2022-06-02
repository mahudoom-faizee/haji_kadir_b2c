package winapp.hajikadir.customer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import winapp.hajikadir.customer.model.Address;
import winapp.hajikadir.customer.model.Category;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Notification;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;

/**
 * Created by user on 18-Jul-16.
 */
public class DBHelper extends SQLiteOpenHelper implements Constants {
    static DBHelper instance = null;
    static SQLiteDatabase database = null;
    private static final String TAG = "DBHelper";
    private SQLiteDatabase db;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = getWritableDatabase();
    }
    public static void init(Context context) {
        if (null == instance) {
            instance = new DBHelper(context);
        }
    }

    public static SQLiteDatabase getDatabase() {
        if (null == database) {
            database = instance.getWritableDatabase();
        }
        return database;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createProductTable(db);
        createCategoryTable(db);
        createModifierTable(db);
        createHolidayTable(db);
        createCouponCodeTable(db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteProductTables(db);
        createProductTable(db);
        deleteCategoryTables(db);
        createCategoryTable(db);
        deleteModifierTable(db);
        createModifierTable(db);
        deleteNotificationTable(db);
        createNotificationTable(db);
        createHolidayTable(db);
        deleleHolidayTable(db);
        createCouponCodeTable(db);
        deleteCouponCodeTable(db);

    }


    private void createCouponCodeTable(SQLiteDatabase db) {

        String CREATE_COUPON_CODE_TABLE = "CREATE TABLE " + TABLE_COUPON_CODE + " ("
                + COL_ID + " INTEGER primary key autoincrement, "
                + COL_COUPON + " TEXT, "
                + COL_DISCOUNT + " TEXT, "
                + COL_MIN_AMT + " TEXT, "
                + COL_MAX_AMT + " TEXT, "
                + COL_TYPE + " TEXT);";

        try {

            db.execSQL(CREATE_COUPON_CODE_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createHolidayTable(SQLiteDatabase db) {

        String CREATE_HOLIDAY_TABLE = "CREATE TABLE " + TABLE_HOLIDAY + " ("
                + COL_ID + " INTEGER primary key autoincrement, "
                + COL_HOLIDAY + " INTERGER, "
                + COL_SHOP + " TEXT, "
                + COL_DELIVERY + " TEXT);";

        try {

            db.execSQL(CREATE_HOLIDAY_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void setHoliday(int mode, String shop, String delivery) {
        Log.d("setHoliday",""+mode);
        ContentValues cv = new ContentValues();
        cv.put(COL_HOLIDAY, mode);
        cv.put(COL_SHOP,shop);
        cv.put(COL_DELIVERY,delivery);
        getDatabase().insert(TABLE_HOLIDAY, null, cv);
    }

    public static void updateHoliday(int mode, String shop, String delivery) {
        ContentValues cv = new ContentValues();
        cv.put(COL_HOLIDAY, mode);
        cv.put(COL_SHOP,shop);
        cv.put(COL_DELIVERY,delivery);
        Log.d("UpdateHoliday",""+mode+shop+delivery);
        getDatabase().update(TABLE_HOLIDAY, cv, "_id = 1", null);
    }





    private void createProductTable(SQLiteDatabase db) {

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + " ("
                + COL_ID + " INTEGER primary key autoincrement, "
                + COL_SL_NO + " TEXT, "
                + COL_PRODUCT_CODE + " TEXT, "
                + COL_PRODUCT_NAME + " TEXT, "
                + COL_MODEL + " TEXT, "
                + COL_QUANTITY + " INTEGER, "
                + COL_BALANCE_QUANTITY + " INTEGER, "
                + COL_PRICE + " FLOAT, "
                + COL_MODIFIER_PRICE + " FLOAT, "
                + COL_TOTAL+ " FLOAT, "
                + COL_RATE + " TEXT, "
                + COL_IS_PRODUCT_MODIFIER + " TEXT, "
                + COL_TAX_CLASS_ID + " TEXT, "
                + COL_TAX + " FLOAT, "
                + COL_NETTOTAL + " FLOAT, "
                + COL_PRODUCT_IMAGE + " TEXT);";




        try {

            db.execSQL(CREATE_PRODUCT_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void createNotificationTable(SQLiteDatabase db){
        String CREATE_NOTIFICATION_TABLE =
                "CREATE TABLE " + NOTIFICATION_TABLE + " (" +
                        COL_ID + " INTEGER PRIMARY KEY," +
                        COL_TITLE + " TEXT," +
                        COL_MESSAGE + " TEXT," +
                        COL_ISBACKGROUND + " TEXT," +
                        COL_PAYLOAD + " TEXT," +
                        COL_IMAGEURL + " TEXT," +
                        COL_TIMESTAMP + " TEXT)";

        try {

            db.execSQL(CREATE_NOTIFICATION_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createModifierTable(SQLiteDatabase db) {

        String CREATE_MODIFIER_TABLE = "CREATE TABLE " + TABLE_MODIFIER + " ("
                + COL_ID + " INTEGER primary key autoincrement, "
                + COL_SL_NO + " TEXT, "
                + COL_PRODUCT_CODE + " TEXT, "
                + COL_MODIFIER_CODE + " TEXT, "
                + COL_MODIFIER_NAME + " TEXT, "
                + COL_PRICE + " FLOAT);";

        try {

            db.execSQL(CREATE_MODIFIER_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCategoryTable(SQLiteDatabase db) {



        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " ("
                + COL_ID + " INTEGER primary key autoincrement, "
                + COL_CATEGORY_ID + " TEXT, "
                + COL_PARENT_ID + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_PRODUCT_IMAGE + " TEXT);";


        try {

            db.execSQL(CREATE_CATEGORY_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleleHolidayTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_HOLIDAY + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteProductTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_PRODUCT + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteCategoryTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_CATEGORY + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteModifierTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_MODIFIER + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteNotificationTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + NOTIFICATION_TABLE + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteCouponCodeTable(SQLiteDatabase db) {

        try {
            db.execSQL("DROP TABLE " + TABLE_COUPON_CODE + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void truncateTables() {
        deleteProductTables(getWritableDatabase());
        createProductTable(getWritableDatabase());
    }



    public void truncateCategoryTables()
    {
        deleteCategoryTables(getWritableDatabase());
        createCategoryTable(getWritableDatabase());
    }
    public Cursor getHolidayCursor(){
        Cursor c=db.query(TABLE_HOLIDAY,new String[]{COL_HOLIDAY,COL_SHOP,COL_DELIVERY},null,null,null,null ,null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    public Cursor getCouponCodeCursor(){
        Cursor c = db.query(TABLE_COUPON_CODE, new String[] {COL_ID,COL_COUPON,COL_DISCOUNT,COL_MIN_AMT,COL_MAX_AMT,COL_TYPE},
                null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    public Cursor getProductCursor() {
        Cursor c = db.query(TABLE_PRODUCT, new String[] {COL_ID,COL_SL_NO,COL_PRODUCT_CODE,COL_PRODUCT_NAME,COL_MODEL,
                        COL_QUANTITY,COL_BALANCE_QUANTITY,COL_PRICE,COL_MODIFIER_PRICE,COL_TOTAL,COL_RATE,COL_IS_PRODUCT_MODIFIER,COL_TAX_CLASS_ID,COL_TAX,COL_NETTOTAL,COL_PRODUCT_IMAGE},
                null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public Cursor getProductModifierCursor(String slNo,String productCode) {
        Cursor c = db.query(
                TABLE_MODIFIER,
                new String[] {COL_ID,COL_SL_NO,COL_PRODUCT_CODE,COL_MODIFIER_CODE,COL_MODIFIER_NAME,COL_PRICE},
                "sl_no = '" + slNo + "' AND product_code = '" + productCode
                        + "'", null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public boolean insertCategory(HashMap<String,String> hashMap) {
        long insertId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CATEGORY_ID, hashMap.get(KEY_CATEGORY_ID));
            values.put(COL_PARENT_ID, hashMap.get(KEY_PARENT_ID));
            values.put(COL_NAME, hashMap.get(KEY_NAME));
            values.put(COL_PRODUCT_IMAGE, hashMap.get(KEY_PRODUCTIMAGE));
            insertId = db.insert(TABLE_CATEGORY, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertId != -1;
    }


    public boolean insertProduct(HashMap<String,String> hashMap) {
        long insertId = -1;
        ContentValues values = new ContentValues();
        boolean isProductModifier = Boolean.valueOf(hashMap.get(KEY_IS_PRODUCT_MODIFIER));
        String flag = hashMap.get(KEY_FLAG);
        String slno = hashMap.get(KEY_SL_NO);
        values.put(COL_PRODUCT_CODE, hashMap.get(KEY_PRODUCTCODE));
        values.put(COL_QUANTITY, hashMap.get(KEY_QUANTITY));
        values.put(COL_NETTOTAL, hashMap.get(KEY_NETTOTAL));
        values.put(COL_TAX, hashMap.get(KEY_TAX));
        values.put(COL_TOTAL, hashMap.get(KEY_TOTAL));

        try {
            if(isProductModifier){
                values.put(COL_SL_NO, hashMap.get(KEY_SL_NO));
                values.put(COL_PRODUCT_NAME, hashMap.get(KEY_PRODUCTNAME));
                values.put(COL_PRICE, hashMap.get(KEY_PRICE));
                values.put(COL_MODIFIER_PRICE, hashMap.get(KEY_MODIFIER_PRICE));
                values.put(COL_PRODUCT_IMAGE, hashMap.get(KEY_PRODUCTIMAGE));
                values.put(COL_RATE, hashMap.get(KEY_RATE));
                values.put(COL_TAX_CLASS_ID, hashMap.get(KEY_TAX_CLASS_ID));
                values.put(COL_BALANCE_QUANTITY, hashMap.get(KEY_BALANCE_QUANTITY));
                values.put(COL_MODEL, hashMap.get(KEY_PRODUCTNAME));
                values.put(COL_IS_PRODUCT_MODIFIER, hashMap.get(KEY_IS_PRODUCT_MODIFIER));
                insertId = db.insert(TABLE_PRODUCT, null, values);
            }else{
            String productCode = getProductCode(hashMap.get(KEY_PRODUCTCODE));
            if (productCode != null && !productCode.isEmpty()){
                String where = "";
                if(flag.equalsIgnoreCase("Product")){
                    where = "product_code = '"+ productCode+ "'";
                }else if(flag.equalsIgnoreCase("Cart")){
                    where = "product_code = '"+ productCode+ "' AND sl_no = '"+ slno +"'";
                }
               // String where = "product_code = '"+ productCode+ "'";
              //  String where = "product_code = '"+ productCode+ "' AND sl_no = '"+ slno +"'";
                Log.d("where",""+where);
                insertId = db.update(TABLE_PRODUCT, values, where, null);
            }else{
                values.put(COL_SL_NO, hashMap.get(KEY_SL_NO));
                values.put(COL_PRODUCT_NAME, hashMap.get(KEY_PRODUCTNAME));
                values.put(COL_PRICE, hashMap.get(KEY_PRICE));
                values.put(COL_MODIFIER_PRICE, hashMap.get(KEY_MODIFIER_PRICE));
                values.put(COL_PRODUCT_IMAGE, hashMap.get(KEY_PRODUCTIMAGE));
                values.put(COL_RATE, hashMap.get(KEY_RATE));
                values.put(COL_TAX_CLASS_ID, hashMap.get(KEY_TAX_CLASS_ID));
                values.put(COL_BALANCE_QUANTITY, hashMap.get(KEY_BALANCE_QUANTITY));
                values.put(COL_MODEL, hashMap.get(KEY_PRODUCTNAME));
                values.put(COL_IS_PRODUCT_MODIFIER, hashMap.get(KEY_IS_PRODUCT_MODIFIER));
                insertId = db.insert(TABLE_PRODUCT, null, values);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertId != -1;
    }

    public boolean insertModifier(HashMap<String,String> hashMap) {
        long insertId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_SL_NO, hashMap.get(KEY_SL_NO));
            values.put(COL_PRODUCT_CODE, hashMap.get(KEY_PRODUCTCODE));
            values.put(COL_MODIFIER_CODE, hashMap.get(KEY_MODIFIER_ID));
            values.put(COL_MODIFIER_NAME, hashMap.get(KEY_MODIFIER_NAME));
            values.put(COL_PRICE, hashMap.get(KEY_PRICE));
            insertId = db.insert(TABLE_MODIFIER, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertId != -1;
    }

    public boolean insertCouponCode(HashMap<String,String> hashMap){
        Log.d("InsertCoupon",""+"InsertCoupon");
        long insertId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_COUPON, hashMap.get(KEY_COUPON_CODE));
            values.put(COL_DISCOUNT, hashMap.get(KEY_DISCOUNT));
            values.put(COL_MIN_AMT, hashMap.get(KEY_MIN_AMT));
            values.put(COL_MAX_AMT, hashMap.get(KEY_MAX_AMT));
            values.put(COL_TYPE ,hashMap.get(KEY_TYPE));
            insertId = db.insert(TABLE_COUPON_CODE, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertId != -1;
    }
//
    public ArrayList<String> getCouponCode() {
       String couponCode = "", discount ="" ,fromAmt ="" , toAmt ="";
       ArrayList<String> couponCodeList = new ArrayList<String>();
       try {
           String query = "SELECT * FROM coupon_data";
           Log.d("Selected",""+query);
           Cursor c = db.rawQuery(query, null);
           c.moveToFirst();
           if (c.getCount() > 0) {
               couponCode = c.getString(c.getColumnIndex(COL_COUPON));
               discount =c.getString(c.getColumnIndex(COL_DISCOUNT));
               fromAmt=c.getString(c.getColumnIndex(COL_MIN_AMT));
               toAmt=c.getString(c.getColumnIndex(COL_MAX_AMT));
               couponCodeList.add(couponCode);
               couponCodeList.add(discount);
                couponCodeList.add(fromAmt);
               couponCodeList.add(toAmt);
           }            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
        return couponCodeList;
    }





    public boolean updateModifier(HashMap<String,String> hashMap) {
        long insertId = -1;
        try {
            ContentValues values = new ContentValues();
            String slNo = hashMap.get(KEY_SL_NO);
            String is_product_modifier = hashMap.get(KEY_IS_PRODUCT_MODIFIER);
            boolean isModifier = Boolean.valueOf(is_product_modifier);
            String productCode = hashMap.get(KEY_PRODUCTCODE);
            String modifierCode = hashMap.get(KEY_MODIFIER_ID);
            Log.d("productCode",""+productCode);
            Log.d("modifierCode",""+modifierCode);
            Log.d("slNo",""+slNo);
            Log.d("isModifier",""+isModifier);
            values.put(COL_SL_NO, slNo);
            values.put(COL_PRODUCT_CODE, productCode);
            values.put(COL_MODIFIER_CODE, modifierCode);
            values.put(COL_MODIFIER_NAME, hashMap.get(KEY_MODIFIER_NAME));
            values.put(COL_PRICE, hashMap.get(KEY_PRICE));
            String modifierId= getModifierCode(modifierCode,productCode,slNo);
            if(modifierId != null && !modifierId.isEmpty()) {

             if(!isModifier){
                 Log.d("delete",""+hashMap.get(KEY_MODIFIER_NAME));
                    deleteCartModifier(productCode,modifierCode,slNo);
             }
            }else{
                if(isModifier) {
                    insertId = db.insert(TABLE_MODIFIER, null, values);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertId != -1;
    }

     public boolean isModifier(String productcode){
         boolean isProductModifer = false;
         try {
             String query = "SELECT * FROM "
                     + TABLE_PRODUCT + " WHERE " + COL_PRODUCT_CODE
                     + " = '"+ productcode +"'";
           //  Log.d("Selected",""+query);
             Cursor c = db.rawQuery(query, null);
             c.moveToFirst();
             if (c.getCount() > 0) {
                 isProductModifer = Boolean.valueOf(c.getString(c.getColumnIndex(COL_IS_PRODUCT_MODIFIER)));
             }
             c.close();
         } catch (SQLException e) {
             e.printStackTrace();
         }
      //   Log.d("isProductModifer","db-> "+isProductModifer);
         return isProductModifer;
     }
    public boolean deleteCartModifier(String productCode,String modiferCode,String slno) {
        try {
            db.beginTransaction();
            db.delete(TABLE_MODIFIER, COL_PRODUCT_CODE + " = ? AND "+ COL_MODIFIER_CODE + " = ? AND "+
                            COL_SL_NO + " = ? ",
                    new String[] {productCode,modiferCode,slno});
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }


    public boolean updateTotal(String slno,String productCode,String total){
        long updateTotal = -1;
        try {
            String where = "product_code = '"+ productCode+ "' AND sl_no = '"+ slno +"'";

            ContentValues values = new ContentValues();
            values.put(COL_TOTAL, total);
            updateTotal = db.update(TABLE_PRODUCT, values, where, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updateTotal != -1;
    }
    public boolean updateModifierStatus(String slno,String productCode){
        long updateTotal = -1;
        try {
            String where = "product_code = '"+ productCode+ "' AND sl_no = '"+ slno +"'";

            ContentValues values = new ContentValues();
            values.put(COL_IS_PRODUCT_MODIFIER, "false");
            updateTotal = db.update(TABLE_PRODUCT, values, where, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updateTotal != -1;
    }
    public String getProductCode(String productcode) {
        String productCode = "";
        try {
            Cursor c = db.query(TABLE_PRODUCT,
                    new String[] { COL_PRODUCT_CODE }, COL_PRODUCT_CODE
                            + " = ? ", new String[] { productcode }, null,
                    null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                productCode = c.getString(c.getColumnIndex(COL_PRODUCT_CODE));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productCode;
    }
    public String getModifierCode(String modifiercode,String productCode,String productSlNo) {
        String modifierCode = "";
        try {
            String query = "SELECT " + COL_MODIFIER_CODE +  " FROM "
                    + TABLE_MODIFIER + " WHERE " + COL_MODIFIER_CODE
                    + " = '"+ modifiercode +"' AND " + COL_PRODUCT_CODE + " = '"+ productCode +"' AND "
                    + COL_SL_NO + " = '"+ productSlNo +"'";
            Log.d("Selected",""+query);
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                modifierCode = c.getString(c.getColumnIndex(COL_MODIFIER_CODE));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modifierCode;
    }

    //Put Information into a Database
    public int storeNotification(Notification notification) {
        long newRowId = 0;
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, notification.getTitle());
        values.put(COL_MESSAGE, notification.getMessage());
        values.put(COL_ISBACKGROUND, notification.getMessage());
        values.put(COL_PAYLOAD, notification.getPayload());
        values.put(COL_IMAGEURL, notification.getImageUrl());
        values.put(COL_TIMESTAMP, notification.getTimestamp());


        try {
            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(NOTIFICATION_TABLE, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (int) newRowId;
    }
    //Read Information from a Database
    public List<Notification> readNotification() {
        List<Notification> notificationList = new ArrayList<Notification>();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COL_ID,
                COL_TITLE ,
                COL_MESSAGE ,
                COL_ISBACKGROUND ,
                COL_PAYLOAD,
                COL_IMAGEURL,
                COL_TIMESTAMP
        };
        // Filter results WHERE "title" = 'My Title'
        //  String selection = COLUMN_NAME_TITLE + " = ?";
        //  String[] selectionArgs = {"My Title"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                COL_ID + " DESC";

        Cursor cursor = db.query(
                NOTIFICATION_TABLE,                               // The table to query
                projection,                               // The columns to return
                null,   // selection,                     // The columns for the WHERE clause
                null,  // selectionArgs,                  // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getString(0));
                notification.setTitle(cursor.getString(1));
                notification.setMessage(cursor.getString(2));
                notification.setIsBackground(cursor.getString(3));
                notification.setPayload(cursor.getString(4));
                notification.setImageUrl(cursor.getString(5));
                notification.setTimestamp(cursor.getString(6));

                // Adding entries to list
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }

        // return entries list
        return notificationList;
    }
   /* public String getProductId(String sno,String productcode) {
        String productId = "";
        try {
            Cursor c = db.query(TABLE_PRODUCT,
                    new String[] {COL_PRODUCT_CODE }, COL_PRODUCT_CODE
                            + " = ? ", new String[] {sno, productcode }, null,
                    null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                productId = c.getString(c.getColumnIndex(COL_ID));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productId;
    }*/

    /*  public int getQty(String productCode) {
          int qty;
          Cursor c = db.query(TABLE_PRODUCT, new String[] { COL_QUANTITY },
                  COL_PRODUCT_CODE + " = ? ",
                  new String[] { productCode }, null, null, null);
          c.moveToFirst();
          qty = c.getInt(c.getColumnIndex(COL_QUANTITY));
          c.close();
          return qty;
      }*/
/*    public int getQty(String productCode) {
        int qty = 0;
        try {
            Cursor c1 = db.query(TABLE_PRODUCT,
                    new String[] { COL_QUANTITY }, COL_PRODUCT_CODE
                            + " = ? ", new String[] { productCode }, null,
                    null, null);
            c1.moveToFirst();
            if (c1.getCount() > 0) {
                qty = c1.getInt(c1.getColumnIndex(COL_QUANTITY));
            }
            c1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return qty;
    }*/
    public int getQty(String productCode) {
        int qty = 0;
        String query = "SELECT sum(quantity) FROM " + TABLE_PRODUCT + " where product_code = '"
                + productCode  + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                qty = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return qty;
    }
   /*public double getModifierPrice(String slNo,String productCode) {
        double price = 0;
        String query = "SELECT sum(price) FROM " + TABLE_MODIFIER + "  WHERE " + COL_PRODUCT_CODE + " = '"+ productCode +"' AND "
                + COL_SL_NO + " = '"+ slNo +"'";
        Log.d("Selected",""+query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                price = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return price;
    }*/
    /*public int getQty(String productCode) {
          int qty = 0;
          String selectQuery = "SELECT quantity FROM product Where product_code = '"
                  + productCode  + "'";
          Cursor cursor = db.rawQuery(selectQuery, null);
          if (cursor.moveToFirst()) {
              do {
                  qty = cursor.getInt(cursor.getColumnIndex(COL_QUANTITY));
              } while (cursor.moveToNext());
          }
          return qty;
      }*/
    public int getCount(){
        int count = 0;
        try {
            String countQuery = "SELECT MAX(sl_no) FROM " + TABLE_PRODUCT;
            Cursor c = db.rawQuery(countQuery, null);
           // count = c.getCount();
            if (c.moveToFirst()) {
                do {
                    count = c.getInt(c.getColumnIndex("MAX(sl_no)"));
                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }



    public String getHoliday() {
        String mode = "";
        String query = "SELECT * FROM " + TABLE_HOLIDAY;
        Cursor cursor = db.rawQuery(query, null);
        Log.d("CursorCount",""+cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                mode = cursor.getString(cursor.getColumnIndex(COL_HOLIDAY));
                Log.d("CursorData",""+mode);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mode;
    }


    public double getTotal() {
        double total = 0.00;
        String query = "SELECT sum(total)  FROM " + TABLE_PRODUCT;

        Cursor cursor = db.rawQuery(query, null);
        Log.d("CursorCounts",""+cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                total = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }



    public double getServiceTaxTotal() {
        double total = 0.00;
        String query = "SELECT sum(net_total)  FROM " + TABLE_PRODUCT;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                total = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }
    public  ArrayList<Product> products(String productCode) {
        ArrayList<Product> productList = new ArrayList<Product>();
        String selectQuery ="";
        if(productCode!=null && !productCode.isEmpty()){
            selectQuery = "SELECT  * FROM product where product_code = '"+ productCode +"'";
        }else{
            selectQuery = "SELECT  * FROM product";
        }
      //  String selectQuery = "SELECT  * FROM product";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Product productdetail = new Product();
                productdetail.setSno(cursor.getString(cursor
                        .getColumnIndex(COL_SL_NO)));
                productdetail.setId(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_CODE)));
                productdetail.setName(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_NAME)));
                productdetail.setQuantity(cursor.getInt(cursor
                        .getColumnIndex(COL_QUANTITY)));
                productdetail.setPrice(cursor.getString(cursor
                        .getColumnIndex(COL_PRICE)));
                productdetail.setModifierPrice(cursor.getString(cursor
                        .getColumnIndex(COL_MODIFIER_PRICE)));
                productdetail.setTotal(cursor.getDouble(cursor
                        .getColumnIndex(COL_TOTAL)));
                productdetail.setBalanceQty(cursor.getInt(cursor
                        .getColumnIndex(COL_BALANCE_QUANTITY)));
                productdetail.setImage(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_IMAGE)));

                productdetail.setTaxClassId(cursor.getString(cursor
                        .getColumnIndex(COL_TAX_CLASS_ID)));

                productdetail.setTax(cursor.getString(cursor
                        .getColumnIndex(COL_TAX)));

                productdetail.setRate(cursor.getString(cursor
                        .getColumnIndex(COL_RATE)));

                String productModifier = cursor.getString(cursor
                        .getColumnIndex(COL_IS_PRODUCT_MODIFIER));
                boolean isProductModifier = Boolean.valueOf(productModifier);
                productdetail.setProductModifier(isProductModifier);

                productList.add(productdetail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;

    }

    public  ArrayList<Modifier> getProductModifier(String sno) {
        ArrayList<Modifier> modifierList = new ArrayList<Modifier>();
        String selectQuery = "SELECT  * FROM  modifier where sl_no = '" + sno + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Modifier modifier = new Modifier();
                modifier.setId(cursor.getString(cursor
                        .getColumnIndex(COL_ID)));
                modifier.setSno(cursor.getString(cursor
                        .getColumnIndex(COL_SL_NO)));
                modifier.setCode(cursor.getString(cursor
                        .getColumnIndex(COL_MODIFIER_CODE)));
                modifier.setName(cursor.getString(cursor
                        .getColumnIndex(COL_MODIFIER_NAME)));
                modifier.setPrice(cursor.getString(cursor
                        .getColumnIndex(COL_PRICE)));

                modifierList.add(modifier);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return modifierList;

    }

   public  ArrayList<Category> category() {
        ArrayList<Category> categoryList = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM category";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Category mCategory = new Category();

        mCategory.setId("All Products");
        mCategory.setParentId("All Products");
        mCategory.setName("All Products");
        mCategory.setImage("");
        categoryList.add(mCategory);
        if (cursor.moveToFirst()) {
            do {
                mCategory = new Category();
                mCategory.setId(cursor.getString(cursor
                        .getColumnIndex(COL_CATEGORY_ID)));
                String parentId = cursor.getString(cursor
                        .getColumnIndex(COL_PARENT_ID));
                mCategory.setParentId(parentId);
                mCategory.setName(cursor.getString(cursor
                        .getColumnIndex(COL_NAME)));
                mCategory.setImage(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_IMAGE)));
                if (parentId.matches("0")) {
                    categoryList.add(mCategory);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;

    }
    public  ArrayList<Category> categoryAll() {
        ArrayList<Category> categoryAllList = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM category";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Category mCategory = new Category();
                mCategory.setId(cursor.getString(cursor
                        .getColumnIndex(COL_CATEGORY_ID)));
                String parentId = cursor.getString(cursor
                        .getColumnIndex(COL_PARENT_ID));
                mCategory.setParentId(parentId);
                mCategory.setName(cursor.getString(cursor
                        .getColumnIndex(COL_NAME)));
                mCategory.setImage(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_IMAGE)));
                categoryAllList.add(mCategory);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryAllList;

    }
    public boolean deleteAllProductCode(String productCode) {
        try {
            db.beginTransaction();
            db.delete(TABLE_PRODUCT, COL_PRODUCT_CODE + " = ? ",
                    new String[] { productCode });
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }
    public boolean deleteProduct(String productCode,String slno) {
        try {
            db.beginTransaction();
            db.delete(TABLE_PRODUCT, COL_PRODUCT_CODE + " = ? AND "+ COL_SL_NO
                            + " = ? ",
                    new String[] {productCode, slno });
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }


    public boolean deleteProductModifier(String productCode,String slno) {
        try {
            db.beginTransaction();
          /*  db.delete(TABLE_MODIFIER, COL_SL_NO + " = ? ",
                    new String[] { sno });*/
            db.delete(TABLE_MODIFIER, COL_PRODUCT_CODE + " = ? AND "+ COL_SL_NO
                            + " = ? ",
                    new String[] {productCode, slno });
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }



    public boolean deleteNotification(String id) {
        try {
            db.beginTransaction();
            db.delete(NOTIFICATION_TABLE,COL_ID
                            + " = ? ",
                    new String[] {id});
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }
    public int deleteAllCategory() {
        return db.delete(TABLE_CATEGORY, null, null);

    }
    public int deleteAllProduct() {
        return db.delete(TABLE_PRODUCT, null, null);

    }
    public int deleteAllProductModifier() {
        return db.delete(TABLE_MODIFIER, null, null);

    }

    public int deleteAllCouponCode() {
        return db.delete(TABLE_COUPON_CODE, null, null);

    }

}

/*
public class DBHelper extends SQLiteOpenHelper implements Constants {

    private static final String TAG = "DBHelper";
    private SQLiteDatabase db;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createProductTable(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteProductTables(db);
        createProductTable(db);
    }
    private void createProductTable(SQLiteDatabase db) {

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + " ("
                + COL_ID + " INTEGER primary key autoincrement, "
                + COL_PRODUCT_CODE + " TEXT, "
                + COL_PRODUCT_NAME + " TEXT, "
                + COL_QUANTITY + " FLOAT, "
                + COL_PRICE + " FLOAT, "
                + COL_TOTAL+ " FLOAT, "
                + COL_PRODUCT_IMAGE + " TEXT);";

        try {

            db.execSQL(CREATE_PRODUCT_TABLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteProductTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_PRODUCT + ";");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void truncateTables() {
        deleteProductTables(getWritableDatabase());
        createProductTable(getWritableDatabase());
    }
    public boolean insertProduct(HashMap<String,String> hashMap) {
        long insertId = -1;
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_CODE, hashMap.get(KEY_PRODUCTCODE));
        values.put(COL_PRODUCT_NAME, hashMap.get(KEY_PRODUCTNAME));
        values.put(COL_PRICE, hashMap.get(KEY_PRICE));
        values.put(COL_TOTAL, hashMap.get(KEY_TOTAL));
        values.put(COL_PRODUCT_IMAGE, hashMap.get(KEY_PRODUCTIMAGE));
        try {
            insertId = db.insert(TABLE_PRODUCT, null, values);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertId != -1;
    }
    public String getProductCode(String productcode) {
        String productCode = "";
        try {
            Cursor c = db.query(TABLE_PRODUCT,
                    new String[] { COL_PRODUCT_CODE }, COL_PRODUCT_CODE
                            + " = ? ", new String[] { productcode }, null,
                    null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                productCode = c.getString(c.getColumnIndex(COL_PRODUCT_CODE));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productCode;
    }
    public int getCount(){
        int count = 0;
        try {
            String countQuery = "SELECT  * FROM " + TABLE_PRODUCT;
            Cursor c = db.rawQuery(countQuery, null);
            count = c.getCount();

            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public double getTotal() {
        double total = 0.00;
        String query = "SELECT sum(total)  FROM " + TABLE_PRODUCT;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                total = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }
    public  ArrayList<Product> products() {
        ArrayList<Product> productList = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM product";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Product productdetail = new Product();
                productdetail.setId(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_CODE)));

                productdetail.setName(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_NAME)));
                productdetail.setQty(cursor.getInt(cursor
                        .getColumnIndex(COL_QUANTITY)));
                productdetail.setPrice(cursor.getString(cursor
                        .getColumnIndex(COL_PRICE)));
                productdetail.setTotal(cursor.getDouble(cursor
                        .getColumnIndex(COL_TOTAL)));
                productdetail.setImage(cursor.getString(cursor
                        .getColumnIndex(COL_PRODUCT_IMAGE)));

                productList.add(productdetail);
            } while (cursor.moveToNext());
        }
        return productList;

    }
    public boolean deleteProduct(String id) {
        try {
            db.beginTransaction();
            db.delete(TABLE_PRODUCT, COL_ID + " = ? ",
                    new String[] { id });
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }
    public int deleteAllProduct() {
        return db.delete(TABLE_PRODUCT, null, null);

    }
}
*/
