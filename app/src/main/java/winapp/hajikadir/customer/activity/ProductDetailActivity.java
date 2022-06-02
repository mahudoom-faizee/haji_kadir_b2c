package winapp.hajikadir.customer.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.ModifierAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.model.Thumbnail;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;
import winapp.hajikadir.customer.view.ExpandableHeightGridView;

public class ProductDetailActivity extends AppCompatActivity implements/* DialogInterface.OnCancelListener,*/Constants {
    private ImageView mProductImgV;
    private FrameLayout mFrameLayout;
    private ColorDrawable mColorDrawable;
    private int mTopDelta ,mLeftDelta;
    private float mHeightScale, mWidthScale;
    private int ANIM_DURATION = 600,dbQty=0;
    private Product product;
    private TextView mProductNameTxtV,mTotalTxtV,mPriceTxtV,mDescriptionTxtV,mModifierLbl;
    private int thumbnailTop, thumbnailLeft, thumbnailWidth, thumbnailHeight;
    private ImageLoader mImageLoader;
    private Intent mIntent;
    private DBHelper mDBHelper;
    private EditText mQuantityEdt;
    private Button mAddToCartBtn;
    public SessionManager mSession;
    public static MenuItem item;
    private String productIdStr = "";
    private Thread thrdGetModifier;
   // private ProgressDialog mProgressDialog;
    private UIHelper helper;
    private ArrayList<Modifier> mModiferArr;
    private ModifierAdapter mModifierAdapter;
    private ExpandableHeightGridView mGridView;
    private LinearLayout mModifierLayout;
    String qty = "1";
    final int[] quantity = {qty.equals("") ? 0 : Integer.valueOf(qty)};
    ImageButton mQtyPlusImgBtn,mQtyMinusImgBtn;
    String checkHoliday="";
    private Button mOutofStoctBtn;
    //private boolean isProductModifier = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DBHelper.init(ProductDetailActivity.this);
        mModifierLbl = (TextView) findViewById(R.id.modifier_label);

        mOutofStoctBtn=(Button)findViewById(R.id.outofstock);
        mModifierLayout = (LinearLayout) findViewById(R.id.modifier_layout);
        mGridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        mProductNameTxtV = (TextView) findViewById(R.id.lblProductName);
        mPriceTxtV = (TextView) findViewById(R.id.lblPrice);
        mDescriptionTxtV = (TextView) findViewById(R.id.lblDescription);
        mProductImgV = (ImageView) findViewById(R.id.imgProduct);
        mQuantityEdt = (EditText) findViewById(R.id.txtQty);
        mTotalTxtV = (TextView) findViewById(R.id.txtTotal);

        mAddToCartBtn = (Button)findViewById(R.id.btnAddToCart);
        mQtyPlusImgBtn=(ImageButton)findViewById(R.id.qty_plus);
        mQtyMinusImgBtn=(ImageButton)findViewById(R.id.qty_minus);
        mFrameLayout = (FrameLayout) findViewById(R.id.main_background);
        mColorDrawable = new ColorDrawable(Color.WHITE);
        mFrameLayout.setBackground(mColorDrawable);

        mIntent = new Intent();
        mModiferArr = new ArrayList<>();
        mModifierAdapter = new ModifierAdapter();
        helper = new UIHelper(ProductDetailActivity.this);

        mSession = new SessionManager(ProductDetailActivity.this);
        mDBHelper = new DBHelper(ProductDetailActivity.this);

        mImageLoader = HajikadirApp.getImageLoader();

        mGridView.setExpanded(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
                product = (Product) bundle
                        .getSerializable(StringUtils.EXTRA_PRODUCT);
        }
        //retrieves the thumbnail data
        thumbnailLeft = Thumbnail.getThumbnailLeft();
        thumbnailTop = Thumbnail.getThumbnailTop();
        thumbnailWidth = Thumbnail.getThumbnailWidth();
        thumbnailHeight = Thumbnail.getThumbnailHeight();

//        checkHoliday=Product.getHolidayCode();
        int quants=product.getQuantity();
        Log.d("checkQuantity",""+quants);
        checkHoliday=mDBHelper.getHoliday();

        Cursor holidayCursor=mDBHelper.getHolidayCursor();
        String shop=holidayCursor.getString(holidayCursor.getColumnIndex(COL_SHOP));
        String delivery=holidayCursor.getString(holidayCursor.getColumnIndex(COL_DELIVERY));
        Log.d("checkHoliday",""+checkHoliday +shop+delivery);

        if(checkHoliday.matches("1")){
            if(quants<=0){
                mOutofStoctBtn.setVisibility(View.VISIBLE);
                if(shop.equals("1")){
                    mAddToCartBtn.setVisibility(View.VISIBLE);
                }else{
                    mAddToCartBtn.setVisibility(View.GONE);
                }
            }else{
                if(shop.equals("1")){
                    mAddToCartBtn.setVisibility(View.VISIBLE);
                }else{
                    mAddToCartBtn.setVisibility(View.GONE);
                }

            }

        }else if(checkHoliday.matches("0")) {
            Log.d("checkHoliday",""+checkHoliday);
            if(quants<=0){
                mOutofStoctBtn.setVisibility(View.VISIBLE);
                if(shop.equals("1")){
                    mAddToCartBtn.setVisibility(View.VISIBLE);
                }else{
                    mAddToCartBtn.setVisibility(View.GONE);
                }
            }else{
                Log.d("checkHolidayLists",""+checkHoliday);
                mAddToCartBtn.setVisibility(View.VISIBLE);
            }
        }

      //  setBtnLabel();

        if (product != null) {
            showProductDetails();
        }else{
            mPriceTxtV.setText("0");
        }
        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
       mAddToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = mAddToCartBtn.getText().toString();
                Log.d("data",""+data);
                if(data.equals("Go To Cart")){
                    mIntent.setClass(ProductDetailActivity.this, CartActivity.class);
                    startActivity(mIntent);
                }else{
                    storeDataBase();
                }
            }
        });
        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        if (savedInstanceState == null) {
            ViewTreeObserver observer = mProductImgV.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mProductImgV.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mProductImgV.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version selectedImageView same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mProductImgV.getWidth();
                    mHeightScale = (float) thumbnailHeight / mProductImgV.getHeight();

                    enterAnimation();

                    return true;
                }
            });
        }

        mQtyPlusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++quantity[0];

                mQuantityEdt.setText(String.valueOf(quantity[0]));
                //  product.setQty(String.valueOf(quantity[0]));
            }
        });

        mQtyMinusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity[0]>1){
                    --quantity[0];
                }
                mQuantityEdt.setText(String.valueOf(quantity[0]));
                // product.setQty(String.valueOf(quantity[0]));
            }
        });


        mQuantityEdt.addTextChangedListener(qtywatcher);
        mQuantityEdt.setText(qty);
        mPriceTxtV.addTextChangedListener(pricewatcher);


    }
    TextWatcher qtywatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String qtyString = s.toString().trim();
            String priceString = mPriceTxtV.getText().toString();
            int quantity = qtyString.equals("") ? 0:Integer.valueOf(qtyString);
            Double price = priceString.equals("") ? 0:Double.valueOf(priceString);
            Log.d("qtyString","-->"+qtyString);
            if(quantity!=0){
                Log.d("quantity","-->"+quantity);
                Double dquantity = new Double(quantity);
                Double total = dquantity * price;
                Log.d("total","-->"+total);
                mTotalTxtV.setText(Utils.twoDecimalPoint(total));
            }
            else{
                mTotalTxtV.setText("0.00");
            }
            }
    };
    TextWatcher pricewatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String priceString = s.toString().trim();
            String qtyString = mQuantityEdt.getText().toString();
           // int price = priceString.equals("") ? 0:Integer.valueOf(priceString);
            Double dPrice = priceString.equals("") ? 0:Double.valueOf(priceString);
            Double dquantity = qtyString.equals("") ? 0:Double.valueOf(qtyString);
            if(dPrice!=0){
                Double dprice = new Double(dPrice);
                Double total = dquantity * dprice;
                mTotalTxtV.setText(""+total);

            }

        }
    };
    private void setBtnLabel(){
       if(!product.isProductModifier()){
        productIdStr =  mDBHelper.getProductCode(product.getId());
        //dbQty =  mDBHelper.getQty(product.getId());
        if(productIdStr!=null && !productIdStr.isEmpty()){
            mAddToCartBtn.setText("Go To Cart");
        }else{
            mAddToCartBtn.setText("Add To Cart");
        }
       if(dbQty>0){
            mQuantityEdt.setText(""+ dbQty);
            mQuantityEdt.setSelection(mQuantityEdt.getText().length());
        }else{
            mQuantityEdt.setText("");
            mTotalTxtV.setText("");
        }
       }else{
            mAddToCartBtn.setText("Add To Cart");
        }
    }
    private void mGetModifier(final Product product){
        mModiferArr.clear();
        final String productId = product.getId();
       // mProgressDialog = ProgressDialog.show(ProductDetailActivity.this,"Loading...", true,true,this);
       // mProgressDialog.setCancelable(false);
        thrdGetModifier= new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(ProductDetailActivity.this, FUNC_GET_MODIFIER);
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

         //   mProgressDialog.setMessage("Loading...");
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
                            String modiferCode= object.getString(KEY_MODIFIER_ID);
                            String modiferName = object.getString(KEY_MODIFIER_NAME);
                            String price = object.getString(KEY_PRICE);
                            double dPrice = price.equals("") ? 0 : Double.valueOf(price);
                            modifier.setCode(modiferCode);
                            modifier.setName(modiferName);
                            modifier.setPrice(price);
                           /* if(dPrice>0){
                                mModiferArr.add(modifier);
                            }*/
                            if(dPrice>=0){
                                mModiferArr.add(modifier);
                            }

                        }
                    }

            //        mProgressDialog.dismiss();
                    initModfierLoadCompleted();

                } catch (Exception e) {
            //        mProgressDialog.dismiss();
                    helper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                helper.showErrorDialog(sResponseMsg);
           //     mProgressDialog.dismiss();
            }

        }
    };
    public void initModfierLoadCompleted() {
        if(mModiferArr.size()>0){
            mModifierAdapter = new ModifierAdapter(ProductDetailActivity.this,R.layout.item_modifier,mModiferArr);
            mGridView.setAdapter(mModifierAdapter);
            mModifierLayout.setVisibility(View.VISIBLE);
            String price = product.getPrice();
            mModifierAdapter.setPrice(price,mPriceTxtV);
            //isProductModifier = true;
            product.setProductModifier(true);
            mQuantityEdt.setText("1");
            mQuantityEdt.setSelection(mQuantityEdt.getText().length());
            setBtnLabel();
        }
        else{
            mModifierLayout.setVisibility(View.GONE);
           // isProductModifier = false;
            mQuantityEdt.setText(product.getQty());
            mQuantityEdt.setSelection(mQuantityEdt.getText().length());
        }
    }
    private void storeDataBase(){
//        mProgressDialog = ProgressDialog.show(ProductDetailActivity.this,"Loading...", true,true,this);
//        mProgressDialog.setCancelable(false);
        Log.d("is modifier","Prod Det"+product.isProductModifier());
        double dNetTotal = 0.00;
        int slNo = mDBHelper.getCount();
        String sno = String.valueOf(++slNo);
        String tax_class_id  = product.getTaxClassId();
        String rate  = product.getRate();
        String price  = mPriceTxtV.getText().toString();
        String total  = mTotalTxtV.getText().toString();
        if(tax_class_id!=null && !tax_class_id.isEmpty()){
         int itax_class_id = Integer.valueOf(tax_class_id);
                if(itax_class_id>0){
                    if(rate!=null && !rate.isEmpty()){
                        double dRate = Double.valueOf(rate);
                      //  double dTotal= Double.valueOf(total);
                        double dTotal = total.equals("") ? 0:Double.valueOf(total);
                        dNetTotal = dRate*dTotal/100;
                    }
                }else{
                    dNetTotal = 0.00;
                }
            }else{
                dNetTotal = 0.00;
            }
            Log.d("netTotal","-->"+String.valueOf(dNetTotal));
            HashMap<String, String> productvalues = new HashMap<String, String>();
            String qty = mQuantityEdt.getText().toString();
            int quantity = qty.equals("") ? 0:Integer.valueOf(qty);
           if(quantity>0){
            productvalues.put(KEY_FLAG, "Product");
            productvalues.put(KEY_SL_NO, sno);
            productvalues.put(KEY_PRODUCTCODE, product.getId());
            productvalues.put(KEY_PRODUCTNAME, product.getName());
            productvalues.put(KEY_BALANCE_QUANTITY, String.valueOf(product.getQuantity()));
            productvalues.put(KEY_QUANTITY, qty);
            productvalues.put(KEY_PRICE, product.getPrice());
            productvalues.put(KEY_MODIFIER_PRICE, price);
            productvalues.put(KEY_TOTAL, total);
            productvalues.put(KEY_PRODUCTIMAGE,product.getImage());
            productvalues.put(KEY_RATE, rate);
            productvalues.put(KEY_TAX, String.valueOf(dNetTotal));
            productvalues.put(KEY_NETTOTAL, String.valueOf(dNetTotal));
            productvalues.put(KEY_TAX_CLASS_ID, tax_class_id);
            productvalues.put(KEY_IS_PRODUCT_MODIFIER, String.valueOf(product.isProductModifier()));
            mDBHelper.insertProduct(productvalues);
               if(product.isProductModifier()){
                   ArrayList<Modifier> modifiersList = mModifierAdapter.getSelectedModifier();
                   if(modifiersList.size()>0){
                       for(Modifier modifier : modifiersList){
                           if(modifier.isSelected()){
                               HashMap<String, String> modifierValues = new HashMap<String, String>();
                               String modiferCode= modifier.getCode();
                               String modiferName= modifier.getName();
                               String modifierPrice = modifier.getPrice();
                               modifierValues.put(KEY_SL_NO, sno);
                               modifierValues.put(KEY_PRODUCTCODE, product.getId());
                               modifierValues.put(KEY_MODIFIER_ID, modiferCode);
                               modifierValues.put(KEY_MODIFIER_NAME, modiferName);
                               modifierValues.put(KEY_PRICE, modifierPrice);
                               mDBHelper.insertModifier(modifierValues);
                           }
                       }
                   }
                   mPriceTxtV.setText(" "+ Utils.getAmountInFormat(Double.parseDouble(product.getPrice())));
                   mModifierAdapter.unCheckedAll();
                   Toast.makeText(ProductDetailActivity.this,"Added",Toast.LENGTH_LONG).show();
                   mQuantityEdt.setText("");
                   mAddToCartBtn.setText("Go To Cart");
               }else{
                   mAddToCartBtn.setText("Go To Cart");

               }
               cartBadgeCount();
             }else{
               Toast.makeText(ProductDetailActivity.this,"Please enter the quantity",Toast.LENGTH_LONG).show();
             }

    }
    protected void showProductDetails() {
        String imageUrl = product.getImage();
        Log.d("imageUrl","-->"+imageUrl);
        Log.d("product.getName()","-->"+product.getName());
        mImageLoader.displayImage(imageUrl, mProductImgV);
        mProductNameTxtV.setText(product.getName());
        if (!Utils.isEmpty(product.getPrice()))
            mPriceTxtV.setText(" "+ Utils.getAmountInFormat(Double.parseDouble(product.getPrice())));
        Spanned formatted = Html.fromHtml(product.getDescription());
        mDescriptionTxtV.setText(formatted);
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location.
     */
    public void enterAnimation() {

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mProductImgV.setPivotX(0);
        mProductImgV.setPivotY(0);
        mProductImgV.setScaleX(mWidthScale);
        mProductImgV.setScaleY(mHeightScale);
        mProductImgV.setTranslationX(mLeftDelta);
        mProductImgV.setTranslationY(mTopDelta);

        // interpolator where the rate of change starts out quickly and then decelerates.
        TimeInterpolator sDecelerator = new DecelerateInterpolator();

        // Animate scale and translation to go from thumbnail to full size
        mProductImgV.animate().setDuration(ANIM_DURATION).scaleX(1).scaleY(1).
                translationX(0).translationY(0).setInterpolator(sDecelerator);

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mColorDrawable, "alpha", 0, 255);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();

        if (product != null) {
            mGetModifier(product);
        }

    }

    /**
     * The exit animation is basically a reverse of the enter animation.
     * This Animate image back to thumbnail size/location as relieved from bundle.
     *
     * @param endAction This action gets run after the animation completes (this is
     *                  when we actually switch activities)
     */
    public void exitAnimation(final Runnable endAction) {

        TimeInterpolator sInterpolator = new AccelerateInterpolator();
        mProductImgV.animate().setDuration(ANIM_DURATION).scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta)
                .setInterpolator(sInterpolator).withEndAction(endAction);

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mColorDrawable, "alpha", 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }
    @Override
    public void onBackPressed() {
        exitAnimation(new Runnable() {
            public void run() {
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
          item = menu.findItem(R.id.MyCart);
          /* Cursor cursor = mDBHelper.getProductCursor();
        int cartItemCount =cursor.getCount();
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        Utils.setBadgeCount(this, icon, cartItemCount);*/
        HashMap<String, String> userDetails = mSession.getUserDetails();
        final String userId = userDetails.get(PREF_SESSION_USER_ID);
        final String password = userDetails.get(PREF_SESSION_PASSWORD);
        menu.findItem(R.id.MyCart).setVisible(true);
        menu.findItem(R.id.Search).setVisible(true);

        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);

        menu.findItem(R.id.Home).setVisible(true);

        if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
            menu.findItem(R.id.MyOrders).setVisible(true);
        }else{
            menu.findItem(R.id.MyOrders).setVisible(false);
        }

        menu.findItem(R.id.Products).setVisible(true);
        menu.findItem(R.id.Promotions).setVisible(true);
        menu.findItem(R.id.AboutUs).setVisible(true);
        menu.findItem(R.id.ContactUs).setVisible(true);
        menu.findItem(R.id.PrivacyPolicy).setVisible(true);

        cartBadgeCount();
        return true;
    }
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitAnimation(new Runnable() {
                    public void run() {
                        finish();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitAnimation(new Runnable() {
                    public void run() {
                        finish();
                    }
                });
                return true;
            case R.id.UserName:
                // TODO Something when menu item selected
//                mIntent.setClass(this, MyAccountActivity.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mIntent);
                //  finish();
                return true;
            case R.id.Login:
                // TODO Something when menu item selected
//                mIntent.setClass(this, LoginActivity.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mIntent);
                //  finish();
                return true;
            case R.id.MyCart:
                // TODO Something when menu item selected
                HashMap<String, String> userDetails = mSession.getUserDetails();
                String userId = userDetails.get(PREF_SESSION_USER_ID);
                String password = userDetails.get(PREF_SESSION_PASSWORD);
                Log.d("userId", "-->" + userId);
                Log.d("password", "-->" + password);
                 if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                mIntent.setClass(this, CartActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                 }else{
                    mIntent.setClass(this, LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                 }
                return true;
            case R.id.Products:
                mIntent.setClass(this, ProductActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID, "");
                mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
                startActivity(mIntent);
                return true;
            case R.id.Search:
                // TODO Something when menu item selected
                mIntent.setClass(this, SearchActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.Promotions:
                // TODO Something when menu item selected
                return true;
            case R.id.AboutUs:
                // TODO Something when menu item selected
//                mIntent.setClass(this, AboutUsActivity.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mIntent);
                return true;
            case R.id.ContactUs:
                // TODO Something when menu item selected
                mIntent.setClass(this, ContactUsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);

                return true;
            case R.id.SignOut:
                // TODO Something when menu item selected
//                mSession.clearLoginSession();
//                mSettings.clearLoginSession();
//                dbHelper.deleteAllProduct();
//                startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        public void cartBadgeCount(){
        Cursor cursor = mDBHelper.getProductCursor();
        int cartItemCount =cursor.getCount();
        Log.d("cartSize", cartItemCount + "");
        if(item!=null){
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            Utils.setBadgeCount(ProductDetailActivity.this, icon, cartItemCount);
        }

    }

@Override

    public void onResume(){
        super.onResume();
        cartBadgeCount();
         setBtnLabel();
    }

   /* @Override
    public void onCancel(DialogInterface dialogInterface) {

    }*/
}
