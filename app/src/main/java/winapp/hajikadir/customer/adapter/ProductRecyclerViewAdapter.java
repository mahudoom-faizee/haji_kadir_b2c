package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.activity.LoginActivity;
import winapp.hajikadir.customer.activity.ProductActivity;
import winapp.hajikadir.customer.activity.ProductDetailActivity;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.Specs;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.model.Thumbnail;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;
/**
 * Created by user on 15-Jul-16.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.RecyclerViewHolders> implements DialogInterface.OnCancelListener,Constants {
    private final OnItemClickListener listener;
    private List<Product> mItemList;
    private Activity activity;
  //  private ImageLoader mImageLoader;
    private DBHelper dbhelper;
    private SettingsManager settings;
    private int qtyFlag = 0,resourceId;
    private SessionManager mSession;
    private ProductActivity mProductActivity;
    private Thread thrdGetModifier;
    private ProgressDialog mProgressDialog;
    private UIHelper helper;
    private ArrayList<Modifier> mModiferArr;
    private ModifierAdapter mModifierAdapter;
    private String flag = "";
    private Intent mIntent;
    private Bundle mBundle;
    private Product product;
    private String checkHoliday="";
   private UrlImageLoader mUrlImageLoader;
   // private boolean isProductModifier = false;
    public AlertDialog minusAlertDialog=null;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public ProductRecyclerViewAdapter(){
        listener = null;
    }

    public ProductRecyclerViewAdapter(Activity activity, List<Product> itemList, OnItemClickListener listener) {
        this.mItemList = itemList;
        this.activity = activity;
        this.listener = listener;
        mModifierAdapter = new ModifierAdapter();
        mModiferArr = new ArrayList<>();
        dbhelper = new DBHelper(activity);
        mProductActivity = new ProductActivity();
        settings = new SettingsManager(activity);
        mSession = new SessionManager(activity);
        helper = new UIHelper(activity);
     //   mImageLoader = HajikadirApp.getImageLoader();
        mUrlImageLoader = new UrlImageLoader(activity);
        mIntent = new Intent();
        mBundle = new Bundle();


    }
    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_product_recycler_view, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        layoutView.setId(viewType);
        return rcv;
    }
    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, int position) {
        try {
            Product product = getItem(position);
            DBHelper.init(activity);
            String imageUrl = product.getImage();
//            checkHoliday=Product.getHolidayCode();
//            Cursor cursor = dbhelper.getHolidayCursor();
//            int cartItemCount =cursor.getCount();
//            Log.d("getHolidayCursor",""+cartItemCount);
//
//            if (cursor != null && cursor.getCount() > 0) {
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        checkHoliday = cursor.getString(cursor
//                                .getColumnIndex(DBHelper.COL_HOLIDAY));
//                        Log.d("holidayCode", "" + checkHoliday);
//                    } while (cursor.moveToNext());
//                }
//            }

            checkHoliday=dbhelper.getHoliday();
            Log.d("getHolidayCursor",""+checkHoliday);

            Cursor holidayCursor=dbhelper.getHolidayCursor();
            String shop=holidayCursor.getString(holidayCursor.getColumnIndex(COL_SHOP));
            Log.d("checkHoliday",""+checkHoliday+shop);

            if(checkHoliday.matches("1")){
                if(shop.equals("1")){
                    holder.layout.setVisibility(View.VISIBLE);
                }else{
                    holder.layout.setVisibility(View.GONE);
                }
                Log.d("checkHolidayList",""+checkHoliday);
            }else{
                holder.layout.setVisibility(View.VISIBLE);
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
           // mImageLoader.displayImage(imageUrl, holder.mImage);
             //   ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
              //  holder.mImage.loadImage(imageUrl, spec.getKey());
                mUrlImageLoader.displayImage(imageUrl, holder.mImage);

            } else {
                holder.mImage.setImageResource(R.mipmap.no_img_available);
            }
           // Log.d("sort order", "adapter-->" + product.getSortOrder());
            holder.mName.setText(product.getName());
            String price = product.getPrice();
            if (price != null && !price.isEmpty()) {
                double dPrice = Double.valueOf(price);
                price = Utils.twoDecimalPoint(dPrice);
            } else {
                price = "0.00";
            }
            holder.mPrice.setText("$ " + price);

            int stock = product.getQuantity();
               if(stock>0){
            String productCode = dbhelper.getProductCode(product.getId());
            if (productCode != null && !productCode.isEmpty()) {
                int qty = dbhelper.getQty(product.getId());
                //    Log.d("qty", "db-->" + qty);
                product.setQty(String.valueOf(qty));
            } else {
                product.setQty("1");
            }
            boolean isModifier = dbhelper.isModifier(product.getId());
            product.setProductModifier(isModifier);
            if (productCode != null && !productCode.isEmpty()) {
                int quantity = product.getQty().equals("") ? 0 : Integer.valueOf(product.getQty());
                if (quantity == 1) {
                    holder.mQtyMinus.setVisibility(View.GONE);
                    holder.mQtyDelete.setVisibility(View.VISIBLE);
                } else if (quantity > 1) {
                    holder.mQtyMinus.setVisibility(View.VISIBLE);
                    holder.mQtyDelete.setVisibility(View.GONE);

                } else {
                    holder.mQtyDelete.setVisibility(View.GONE);
                    holder.mQtyDelete.setVisibility(View.VISIBLE);
                }
                holder.mQty.setText(product.getQty());
                holder.mQty.setVisibility(View.VISIBLE);
                holder.mQtyPlus.setVisibility(View.VISIBLE);
                holder.mAddToCart.setVisibility(View.GONE);
            } else {
                holder.mAddToCart.setVisibility(View.VISIBLE);
                holder.mQty.setVisibility(View.GONE);
                holder.mQtyPlus.setVisibility(View.GONE);
                holder.mQtyDelete.setVisibility(View.GONE);
                holder.mQtyMinus.setVisibility(View.GONE);
            }
            holder.mOutOfStockImage.setVisibility(View.GONE);
            holder.mOutOfStock.setVisibility(View.GONE);
        }else{
            holder.mOutOfStockImage.setVisibility(View.VISIBLE);
            holder.mOutOfStock.setVisibility(View.VISIBLE);
            holder.mAddToCart.setVisibility(View.GONE);
        }
            holder.mAddToCart.setId(position);
            holder.mAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try {
                        HashMap<String, String> userDetails = mSession.getUserDetails();
                        String userId = userDetails.get(PREF_SESSION_USER_ID);
                        String password = userDetails.get(PREF_SESSION_PASSWORD);
                        //    Log.d("userId", "-->" + userId);
                        //    Log.d("password", "-->" + password);
                        if (userId != null && !userId.isEmpty() && password != null && !password.isEmpty()) {
                            int position = v.getId();
                            Product product = getItem(position);
                       /* mGetModifier(product);*/
                            mIntent.setClass(activity, ProductDetailActivity.class);
                            mBundle.putSerializable(StringUtils.EXTRA_PRODUCT, product);
                            mIntent.putExtras(mBundle);
                            activity.startActivity(mIntent);

                        } else {
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.mQtyMinus.setId(position);
            holder.mQtyMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isModifier(v)) {
                        mMinusDialog(v);
                    } else {
                        qtyMinusAction(v);
                    }


                }
            });
            holder.mQtyPlus.setId(position);
            holder.mQtyPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isModifier(v)) {
                        int position = v.getId();
                        Product product = getItem(position);
                        mGetModifier(product);
                    } else {
                        qtyPlusAction(v);
                    }
                }
            });
            holder.mQtyDelete.setId(position);
            holder.mQtyDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = v.getId();
                    Product product = getItem(position);
                    String productCode = product.getId();
                    dbhelper.deleteAllProductCode(productCode);
                    mProductActivity.cartBadgeCount(activity);
                    notifyDataSetChanged();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public Product getItem(int position)
    {
        return mItemList.get(position);
    }
    @Override
    public int getItemCount() {
        return mItemList.size();
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mName;
        public TextView mPrice;
        public ImageView mOutOfStockImage;
        public ImageView mImage;
        private Button mAddToCart,mOutOfStock;
        private TextView mQty;
        private ImageButton mQtyMinus,mQtyPlus,mQtyDelete;
        private LinearLayout layout;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.lblProdName);
            mPrice = (TextView)itemView.findViewById(R.id.lblProdPrice);
            mImage = (ImageView)itemView.findViewById(R.id.imgProd);
            mOutOfStockImage = (ImageView)itemView.findViewById(R.id.outofstock);
            mAddToCart = (Button)itemView.findViewById(R.id.btnAddToCart);
            mOutOfStock = (Button)itemView.findViewById(R.id.btOutOfStock);
            mQty = (TextView)itemView.findViewById(R.id.qty);
            mQtyMinus = (ImageButton)itemView.findViewById(R.id.qty_minus);
            mQtyPlus = (ImageButton)itemView.findViewById(R.id.qty_plus);
            mQtyDelete = (ImageButton)itemView.findViewById(R.id.qty_delete);
            layout=(LinearLayout)itemView.findViewById(R.id.linear);
            mImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imgProd);
            int[] screenLocation = new int[2];
            imageView.getLocationOnScreen(screenLocation);
            Thumbnail.setThumbnailLeft(screenLocation[0]);
            Thumbnail.setThumbnailTop(screenLocation[1]);
            Thumbnail.setThumbnailWidth(imageView.getWidth());
            Thumbnail.setThumbnailHeight(imageView.getHeight());
            listener.onItemClick(getPosition());

        }
    }

    /*private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }*/


    public boolean isModifier(View v){
        int position = v.getId();
        Product product = getItem(position);
        Log.d("isModifier", "-->" + product.isProductModifier());
        return product.isProductModifier();
    }
    public static Uri resIdToUri(Context context, int resId) {
        return Uri.parse(Constants.ANDROID_RESOURCE + context.getPackageName()
                + Constants.FORESLASH + resId);
    }
    private void qtyPlusAction(View v) {
        try {
            int position = v.getId();
            Product product = getItem(position);
            int qtyStock = product.getQuantity();
            Log.d("Balance Qty", "-->" + qtyStock);
            Log.d("qtyFlag", "-->" + qtyFlag);
            String qty = product.getQty();
            qtyFlag = Integer.valueOf(qty);
          //  if (qtyStock > qtyFlag) {
                if (product.getQty() != null && !product.getQty().isEmpty()) {
                    qtyFlag = Integer.valueOf(product.getQty().toString());
                    Log.d("qtyflag", "!null-->" + qtyFlag);
                } else {
                    qtyFlag = 0;
                    Log.d("qtyflag", "0-->" + qtyFlag);
                }
                // int qty = ++qtyFlag;
                //  Log.d("qty","-->"+ qty);
                product.setQty(String.valueOf(++qtyFlag));
                Log.d("qtyflag", "-->" + qtyFlag);
                notifyDataSetChanged();
                totalCalc(product);



//            } else {
//                Toast.makeText(activity, "Only "+qtyStock +" quantity of "+ product.getName() +" is available", Toast.LENGTH_LONG).show();
//
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void qtyMinusAction(View v) {
        try {
            int position = v.getId();
            Product product = getItem(position);
            if (product.getQty() != null && !product.getQty().isEmpty()) {
                qtyFlag = Integer.valueOf(product.getQty().toString());
                if (qtyFlag > 1) {
                    product.setQty(String.valueOf(--qtyFlag));
                } else {
                    product.setQty("1");
                }
            }
            notifyDataSetChanged();
            totalCalc(product);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void totalCalc(Product product){
        HashMap<String, String> productvalues = new HashMap<String, String>();
        try {
            double dNetTotal = 0.00,dprice=0.00;
            int slNo = dbhelper.getCount();
            String price = product.getPrice();
            String modifierprice = product.getModifierPrice();
            boolean isModifier =  product.isProductModifier();
            Log.d("flag","-->"+flag);
            if(flag!=null && !flag.isEmpty()){
                if (flag.equalsIgnoreCase("add")){
                    if(isModifier){
                        dprice = modifierprice.equals("") ? 0:Double.valueOf(modifierprice);
                    }else{
                        dprice = price.equals("") ? 0:Double.valueOf(price);
                    }
                }else if (flag.equalsIgnoreCase("skip")){
                    dprice = price.equals("") ? 0:Double.valueOf(price);
                }
            }else{
                dprice = price.equals("") ? 0:Double.valueOf(price);
            }


            String tax_class_id  = product.getTaxClassId();
            String rate  = product.getRate();
            String qty = product.getQty();
            String model = product.getModel();
            Double dquantity = qty.equals("") ? 0:Double.valueOf(qty);

            //  Double dquantity = new Double(qty);
            //5Double price = new Double(price);
            Double total = dquantity * dprice;
            String sno = String.valueOf(++slNo);

            if(tax_class_id!=null && !tax_class_id.isEmpty()){
                int itax_class_id = Integer.valueOf(tax_class_id);
                if(itax_class_id>0){
                    if(rate!=null && !rate.isEmpty()){
                        double dRate = Double.valueOf(rate);
                        dNetTotal = dRate*total/100;

                    }

                }else{
                    dNetTotal = 0.00;
                }

            }else{
                dNetTotal = 0.00;
            }

            Log.d("slNo","-->"+sno);
            Log.d("code","-->"+product.getId());
            Log.d("name","-->"+product.getName());
            Log.d("qty","-->"+ qty);
            Log.d("price","-->"+price);
            Log.d("modifierprice","-->"+modifierprice);
            Log.d("model","-->"+model);
            Log.d("total","-->"+String.valueOf(total));
            Log.d("netTotal","-->"+String.valueOf(dNetTotal));
            Log.d("balance","-->"+String.valueOf(product.getQuantity()));
            productvalues.put(KEY_FLAG, "Product");
            productvalues.put(KEY_SL_NO, sno);
            productvalues.put(KEY_PRODUCTCODE, product.getId());
            productvalues.put(KEY_PRODUCTNAME, product.getName());
            productvalues.put(KEY_MODEL, model);
            productvalues.put(KEY_QUANTITY, qty);
            productvalues.put(KEY_BALANCE_QUANTITY, String.valueOf(product.getQuantity()));
            productvalues.put(KEY_PRICE, price);
            productvalues.put(KEY_MODIFIER_PRICE, modifierprice);
            productvalues.put(KEY_RATE, rate);
            productvalues.put(KEY_TAX_CLASS_ID, tax_class_id);
            productvalues.put(KEY_TAX, String.valueOf(dNetTotal));
            productvalues.put(KEY_NETTOTAL, String.valueOf(dNetTotal));
            productvalues.put(KEY_TOTAL, Utils.twoDecimalPoint(total));
            productvalues.put(KEY_PRODUCTIMAGE, product.getImage());
            productvalues.put(KEY_IS_PRODUCT_MODIFIER, String.valueOf(isModifier));
            dbhelper.insertProduct(productvalues);

            if(flag!=null && !flag.isEmpty()){
                if (flag.equalsIgnoreCase("add")){
                    if(isModifier) {
                        ArrayList<Modifier> modifiersList = mModifierAdapter.getSelectedModifier();
                        if (modifiersList.size() > 0) {
                            for (Modifier modifier : modifiersList) {
                                if (modifier.isSelected()) {
                                    HashMap<String, String> modifierValues = new HashMap<String, String>();
                                    String modiferCode = modifier.getCode();
                                    String modiferName = modifier.getName();
                                    String modifierPrice = modifier.getPrice();
                                    modifierValues.put(KEY_SL_NO, sno);
                                    modifierValues.put(KEY_PRODUCTCODE, product.getId());
                                    modifierValues.put(KEY_MODIFIER_ID, modiferCode);
                                    modifierValues.put(KEY_MODIFIER_NAME, modiferName);
                                    modifierValues.put(KEY_PRICE, modifierPrice);
                                    dbhelper.insertModifier(modifierValues);
                                }
                            }
                        }
                    }
                }
            }


            notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void mMinusDialog(View v){
        int position = v.getId();
        Product product = getItem(position);
        AlertDialog.Builder myDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_minus_delete, null);
        myDialog.setView(dialogView);
        ListView listview = (ListView) dialogView.findViewById(R.id.listView);
        ImageView close = (ImageView) dialogView.findViewById(R.id.close);
        String productCode = product.getId();

        ArrayList<Product> productList = dbhelper.products(productCode);
        MinusAdapter mMinusAdapter = new MinusAdapter(activity, R.layout.item_list_cart, productList);
        // Assign adapter to ListView
        listview.setAdapter(mMinusAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusAlertDialog.dismiss();
            }
        });
        mMinusAdapter.setOnCompletionListener(new MinusAdapter.OnCompletionListener() {
            @Override
            public void onCompleted() {
                mProductActivity.cartBadgeCount(activity);
                notifyDataSetChanged();
                int count = dbhelper.getCount();
                if(count == 0){
                    minusAlertDialog.dismiss();
                }
            }
        });
      /*  myDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {


            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });*/


        minusAlertDialog=myDialog.show();

    }
    private void mGetModifier(final Product product){
        mModiferArr.clear();
        final String productId = product.getId();
        mProgressDialog = ProgressDialog.show(activity,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        thrdGetModifier= new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(activity, FUNC_GET_MODIFIER);
                    client.addParam("product_id", productId);
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        if (!Utils.isEmpty(sResponse)) {
                            sCode = "SUCCESS";
                            sDesc = sResponse;
                        } else {
                            sCode = "ERROR";
                            sDesc = "Error occured while loading data. No response!";
                        }
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                    } else {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading data."
                                + client.getErrorMessage();
                    }

                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading data!";
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                bndle.putSerializable("OBJECT", product);
                msg.setData(bndle);
                ctgModiferProgressHandler.sendMessage(msg);
            }
        };
        thrdGetModifier.start();
    }
    private Handler ctgModiferProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetModifier.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");
            Product product  = (Product) msg.getData().getSerializable("OBJECT");

            mProgressDialog.setMessage("Loading...");
            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArray", "" + jsonArray);
                        for (int i = 0; i < len; i++) {
                            Modifier modifier = new Modifier();
                            JSONObject object = jsonArray.getJSONObject(i);
                            String modiferCode = object.getString(KEY_MODIFIER_ID);
                            String modiferName = object.getString(KEY_MODIFIER_NAME);
                            String price = object.getString(KEY_PRICE);
                            modifier.setCode(modiferCode);
                            modifier.setName(modiferName);
                            modifier.setPrice(price);
                            mModiferArr.add(modifier);
                        }
                    }

                    mProgressDialog.dismiss();
                    initModfierLoadCompleted(product);

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    helper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                helper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }

        }
    };
    public void initModfierLoadCompleted(Product product) {
         if(mModiferArr.size()>0){
           //  isProductModifier = true;

             openProducModifier(product);
         }else{
           //  isProductModifier = false;
             totalCalc(product);
             mProductActivity.cartBadgeCount(activity);
         }
    }
    public void openProducModifier(final Product product){
        final Dialog dialog = new Dialog(activity, R.style.FullHeightDialog); //this is a reference to the style above
        dialog.setContentView(R.layout.dialog_product_modifier); //I saved the xml file above as yesnomessage.xml
        dialog.setCancelable(true);
        ImageView imgProductImgV  = (ImageView) dialog.findViewById(R.id.imgProduct);
        Button addBtn  = (Button) dialog.findViewById(R.id.add);
        Button skipBtn = (Button) dialog.findViewById(R.id.skip);
        TextView mName = (TextView)dialog.findViewById(R.id.lblProdName);
        final TextView mProductPrice = (TextView)dialog.findViewById(R.id.lblProdPrice);

        final TextView mModifierPrice = (TextView)dialog.findViewById(R.id.lblProdModifierPrice);



        final TextView mQty = (TextView)dialog.findViewById(R.id.lblQty);
        ImageButton mQtyMinusImgBtn = (ImageButton) dialog.findViewById(R.id.qty_minus);
        ImageButton mQtyPlusImgBtn = (ImageButton) dialog.findViewById(R.id.qty_plus);
        GridView mGridView = (GridView) dialog.findViewById(R.id.gridView);

        ImageView mClose = (ImageView) dialog.findViewById(R.id.close);



        String imageUrl =  product.getImage();
        if(imageUrl!=null && !imageUrl.isEmpty()){
          //  mImageLoader.displayImage(imageUrl, imgProductImgV);
            mUrlImageLoader.displayImage(imageUrl, imgProductImgV);

        }else{
            imgProductImgV.setImageResource(R.mipmap.no_img_available);
        }
        mName.setText(product.getName());
        String price = product.getPrice();
        if(price!=null && !price.isEmpty()){
            double dPrice = Double.valueOf(price);
            price = Utils.twoDecimalPoint(dPrice);
        }else{
            price = "0.00";
        }
        mProductPrice.setText(price);
        mModifierPrice.setText(price);
        String qty = "1";
        mQty.setText(qty);
        final int[] quantity = {qty.equals("") ? 0 : Integer.valueOf(qty)};

        mModifierAdapter = new ModifierAdapter(activity,R.layout.item_modifier,mModiferArr);
        mGridView.setAdapter(mModifierAdapter);
        mModifierAdapter.setPrice(price,mModifierPrice);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               Modifier modifier =  mModifierAdapter.getModifier(position);
               modifier.setSelected(true);
                mModifierAdapter.notifyDataSetChanged();
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mQtyMinusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity[0]>1){
                    --quantity[0];
                }
                mQty.setText(String.valueOf(quantity[0]));
               // product.setQty(String.valueOf(quantity[0]));
            }
        });

        mQtyPlusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++quantity[0];

                mQty.setText(String.valueOf(quantity[0]));
              //  product.setQty(String.valueOf(quantity[0]));
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                flag = "add";
                dialog.dismiss();
                product.setQty(String.valueOf(quantity[0]));
                String modifierprice = mModifierPrice.getText().toString();
                product.setModifierPrice(modifierprice);
                product.setProductModifier(true);
                notifyDataSetChanged();
                totalCalc(product);

                mProductActivity.cartBadgeCount(activity);
            }
        });


        skipBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag = "skip";
                dialog.dismiss();
//                product.setQty(String.valueOf(quantity[0]));
//                String productprice = mProductPrice.getText().toString();
//                product.setModifierPrice(productprice);
//                product.setProductModifier(true);
//                notifyDataSetChanged();
//                totalCalc(product);
//
//                mProductActivity.cartBadgeCount(activity);

            }
        });

        dialog.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
}