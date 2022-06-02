package winapp.hajikadir.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
;import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.MyOrderDetailAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;

public class MyOrderDetailActivity extends AppCompatActivity implements DialogInterface.OnCancelListener, Constants {
    private ProgressDialog mProgressDialog;
    private Animation mAnimFadeIn;
    private Thread thrdGetOrder;
    private UIHelper mUIHelper;
    private Bundle mBundle;
    private String mOrderNoStr = "";
    private ArrayList<Product> mProductArr;
    private MyOrderDetailAdapter mMyOrderDetailAdapter;
    public RecyclerView mRecyclerView;
    private ActionBar mActionBar;
    private TextView mlblCartNoOfItem,mlblTitle;
    private ArrayList<Modifier> modifierList;
   // public double mTotal=0;
   private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_order_detail);


        //Customize the ActionBar
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);
        mlblCartNoOfItem.setVisibility(View.GONE);

        mlblTitle.setText(getResources().getString(R.string.title_activity_my_order_detail));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProductArr = new ArrayList<>();
        modifierList = new ArrayList<Modifier>();
        mIntent = new Intent();
        mUIHelper = new UIHelper(MyOrderDetailActivity.this);
        mBundle = new Bundle();
        mBundle = getIntent().getExtras();
        if (mBundle != null){
            //Extract the data…
            mOrderNoStr = mBundle.getString(StringUtils.EXTRA_ORDER_ID);


        }
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager3);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProgressDialog = ProgressDialog.show(MyOrderDetailActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);

        mGetOrderDetail();

    }
    private void mGetOrderDetail(){

        thrdGetOrder = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(MyOrderDetailActivity.this, FUNC_GET_ORDER);
                    client.addParam("fdate", "");
                    client.addParam("tdate", "");
                    client.addParam("orderid", mOrderNoStr);
                    client.addParam("ostatus","");
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
                msg.setData(bndle);
                ctgOrderProgressHandler.sendMessage(msg);
            }
        };
        thrdGetOrder.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgOrderProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetOrder.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if(jsonObject!=null) {
                        JSONObject parent = (JSONObject) jsonObject.get("parent");
                        Log.d("parent", "-->" + parent);


                        JSONArray child1 = (JSONArray) parent.get("child1");
                        Log.d("child1", "-->" + child1);
                        JSONObject objectChild1 = child1.getJSONObject(0);
                       ((TextView) findViewById(R.id.orderNo)).setText(objectChild1.getString("order_id"));
                       ((TextView) findViewById(R.id.orderDate)).setText(objectChild1.getString("date_added"));
                        String payment_address_1 = objectChild1.getString("payment_address_1");
                        String payment_address_2 = objectChild1.getString("payment_address_2");
                        String payment_country = objectChild1.getString("payment_country");
                        String payment_postcode = objectChild1.getString("payment_postcode");

                        if(payment_address_1!=null && !payment_address_1.isEmpty()){
                  //          ((TextView) findViewById(R.id.textView43)).setText(payment_address_1);
                        }else{
                  //          findViewById(R.id.textView42).setVisibility(View.GONE);
                  //          findViewById(R.id.textView43).setVisibility(View.GONE);

                        }

                        if(payment_address_2!=null && !payment_address_2.isEmpty()){
                    //        ((TextView) findViewById(R.id.textView45)).setText(payment_address_2);
                        }else{
                   //         findViewById(R.id.textView44).setVisibility(View.GONE);
                   //         findViewById(R.id.textView45).setVisibility(View.GONE);
                        }

                        if(payment_country!=null && !payment_country.isEmpty()){
                    //        ((TextView) findViewById(R.id.textView47)).setText(payment_country+" "+payment_postcode);
                        }else{
                   //        findViewById(R.id.textView46).setVisibility(View.GONE);
                    //       findViewById(R.id.textView47).setVisibility(View.GONE);
                        }

                  //      ((TextView) findViewById(R.id.textView17)).setText(objectChild1.getString("payment_method"));
                  //      ((TextView) findViewById(R.id.textView19)).setText(objectChild1.getString("shipping_method"));

                        JSONArray child2 = (JSONArray) parent.get("child2");
                        Log.d("child2", "-->" + child2);
                        for (int i = 0; i < child2.length(); i++) {
                            JSONObject object = child2.getJSONObject(i);
                            Product product = new Product();
                            String sId = object.getString("order_product_id");
                            String sName = object.getString("name");
                            String sProductName = Html.fromHtml(sName).toString();
                            String sPrice = object.getString("price");
                            int quantity = object.getInt("quantity");
                            String sImgUrl = object.getString("image");
                            double total = object.getDouble("total");

                            if(sImgUrl!=null && !sImgUrl.isEmpty()){
                                String imageUrl =  API_SERVER_URL+"image/"+sImgUrl;
                                imageUrl = imageUrl.replaceAll(" ", "%20");
                                product.setImage(imageUrl);
                            }else{
                                product.setImage(null);
                            }
                            //mTotal += total;
                            product.setId(sId);
                            product.setName(sProductName);
                            product.setPrice(sPrice);
                            product.setQuantity(quantity);
                            product.setTotal(total);
                            mProductArr.add(product);
                        }
                        JSONArray child3 = (JSONArray) parent.get("child3");
                        Log.d("child3", "-->" + child3);
                        for (int i = 0; i < child3.length(); i++) {
                            double dValue = 0.00;
                            JSONObject object = child3.getJSONObject(i);
                            String value = object.getString("value");
                            String code = object.getString("code");
                            String title = object.getString("title");
                            dValue = value.equals("") ? 0 : Double.valueOf(value);
                            if(code.equalsIgnoreCase("sub_total")){
                                ((TextView) findViewById(R.id.lblSubTotal)).setText(Utils.twoDecimalPoint(dValue));
                            }
                          /*  else if(code.equals("Service Charges (15%)")){
                                ((TextView) findViewById(R.id.lblFlatShippingRate)).setText(value);
                            }*/

                             else if(code.equalsIgnoreCase("tax")){
                                Log.d("Service Charges (15%)", "-->" + code);
                                Log.d("Service Charges (15%)", "-->" + value);
                                ((TextView) findViewById(R.id.serviceCharge)).setText(title+" : ");
                                ((TextView) findViewById(R.id.lblFlatShippingRate)).setText(Utils.twoDecimalPoint(dValue));

                            }
                            else if(code.equalsIgnoreCase("shipping")){
                                 ((TextView) findViewById(R.id.lblCharges)).setText(title+" : ");
                                ((TextView) findViewById(R.id.lblDeliveryCharge)).setText(Utils.twoDecimalPoint(dValue));
                            }
                            else if(code.equalsIgnoreCase("total")){
                                Log.d("child3", "-->" + code);
                                ((TextView) findViewById(R.id.lblGrandTotal)).setText(Utils.twoDecimalPoint(dValue));
                                ((TextView) findViewById(R.id.order_total)).setText(Utils.twoDecimalPoint(dValue)+"("+child2.length()  +" item )");

                            }

                        }
                        JSONArray child4 = (JSONArray) parent.get("child4");
                        Log.d("child4", "-->" + child4);
                        for (int i = 0; i < child4.length(); i++) {
                            Modifier modifier = new Modifier();
                            JSONObject object = child4.getJSONObject(i);
                            String order_product_id = object.getString("order_product_id");
                            String modifier_id = object.getString("modifier_id");
                            String modifier_name = object.getString("modifier_name");
                            String price = object.getString("price");
                            modifier.setId(order_product_id);
                            modifier.setCode(modifier_id);
                            modifier.setName(modifier_name);
                            modifier.setPrice(price);
                            modifierList.add(modifier);
                        }
                      /*  Log.d("child3", "-->" + child3);
                        JSONObject object1 = child3.getJSONObject(0);
                        String total = object1.getString("value");

                        JSONObject object2 = child3.getJSONObject(1);
                        String flatShippingRate = object2.getString("value");

                        JSONObject object3 = child3.getJSONObject(2);
                        String netTotal = object3.getString("value");


                        ((TextView) findViewById(R.id.lblFlatShippingRate)).setText(flatShippingRate);
                        ((TextView) findViewById(R.id.lblGrandTotal)).setText(netTotal);*/
                    }
                    mProgressDialog.setMessage("Loading...");
                    initLoadCompleted();

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
            }

        }
    };

    public void initLoadCompleted() {
      if (mProductArr != null && !mProductArr.isEmpty()) {


          mMyOrderDetailAdapter = new MyOrderDetailAdapter(MyOrderDetailActivity.this, mProductArr,modifierList);
          mRecyclerView.setAdapter(mMyOrderDetailAdapter);
      }

        mProgressDialog.dismiss();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);
        menu.findItem(R.id.MyCart).setVisible(false);


        menu.findItem(R.id.Products).setVisible(true);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(true);
        menu.findItem(R.id.ContactUs).setVisible(true);
        menu.findItem(R.id.PrivacyPolicy).setVisible(true);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.Home:
                mIntent.setClass(this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.Products:
                mIntent.setClass(this, ProductActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
                mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
                startActivity(mIntent);
                return true;
            case R.id.Promotions:
                // TODO Something when menu item selected
                return true;
            case R.id.AboutUs:
                // TODO Something when menu item selected
                mIntent.setClass(this, AboutUsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.ContactUs:
                // TODO Something when menu item selected
                mIntent.setClass(this, ContactUsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.PrivacyPolicy:
                mIntent.setClass(this, PrivacyPolicyActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
}
