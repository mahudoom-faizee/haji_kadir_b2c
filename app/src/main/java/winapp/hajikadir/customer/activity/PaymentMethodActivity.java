package winapp.hajikadir.customer.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.CheckOutAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Branch;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;

public class PaymentMethodActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,DialogInterface.OnCancelListener,Constants {
    private  DecimalFormat df;
    private DBHelper mDBHelper;
    private RadioGroup radiogroup1;
    private Cursor cursor;
    private SettingsManager mSettings;
    private ProgressDialog mProgressDialog;
    private Thread thrdPlaceOrder,thrdPromoCode,thrdLateNightCharge;
    private Animation mAnimFadeIn;
    private UIHelper mUIHelper;
    private int slNo = 0;
    private Intent mIntent;
    private ArrayList<Product> productList;
    private SessionManager mSession;
    public RecyclerView mRecyclerView;
    private CheckOutAdapter mCheckOutAdapter;
    private ActionBar mActionBar;
    private TextView mlblCartNoOfItem,mlblTitle,lblDeliveryCharge,lblPickUpFromShop,lblGrandTotal,lblChargeValue,mapplyPromoCode,showPromoCode;
    private Thread thrdGetPercentage,thrdGetTaxCharge;
    private String name = "",rate = "",value="",label="", tAmount="0",paymethodType="",currentDate="",delivery_amount="",promo_Code="",code_value="";
    private Double dServiceTax = 0.00,dTotal=0.00,dNetTotal=0.00,dCharges=0.00,min_amt=0.00,max_amt=0.00,discount_amt=0.00,strt_amt=0.00,end_amt=0.00,dis_amt=0.00,late_charge=0.00,total=0.00;
    private EditText ed_delivery_date,ed_delivery_time,ed_promoCode;
    private LinearLayout delivery_layout,applyLayout,promo_layout;
    private DatePicker dpResult;
    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    static final int DATE_DIALOG_ID = 999;
    private Calendar cal;
    private SimpleDateFormat formatter;
    String myFormat = "dd/MM/yyyy"; //In which you need put here
    DatePickerDialog dpDialog;
    DateFormat timeFormat;
    String currentdate,code="",promocode="",lateCharge="";
    String capitalCheck="";
    String currentTime="",getTme="";
    String couponShow="notApplied";
    String toastShow="shown";
    boolean chooseAlert=false;
    String discount="",from_amt="",to_amt="";
    Cursor coupon_code_cursor;
    String subtotal="";
    double amt;
    String shop="",delivery_amt="";
    private TextView lblDiscount,deliveryLabel;
    String types="",type="";
    double sub_code=0.00;
    //Paypal Configuration Object
    /*private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(PAYPAL_CLIENT_ID)
            .merchantName("Haji Kadir Restaurant")
            .merchantPrivacyPolicyUri(
                    Uri.parse("http://hajikadirfoodchains.sg/index.php?route=information/information&information_id=3"))
            .merchantUserAgreementUri(
                    Uri.parse("http://hajikadirfoodchains.sg/index.php?route=information/information&information_id=5"));*/

    private static final String TAG = "paymentExample";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID)
            .merchantName("Haji Kadir Restaurant")
            .merchantPrivacyPolicyUri(Uri.parse(" https://www.paypal.com/webapps/mpp/ua/privacy-full"))
            .merchantUserAgreementUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/useragreement-full"));


    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    List<Paypalitems> paypalvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
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
        mlblTitle.setText(getResources().getString(R.string.title_activity_payment_method));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        radiogroup1 = (RadioGroup)findViewById(R.id.rGroup);
        mSettings = new SettingsManager(this);
        mSession = new SessionManager(this);
        mUIHelper = new UIHelper(PaymentMethodActivity.this);
        mDBHelper = new DBHelper(PaymentMethodActivity.this);
        df = new DecimalFormat("0.00");
        mIntent = new Intent();

        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        cal = Calendar.getInstance();
//        currentDate = formatter.format(cal.getTime());
//        cal.add(Calendar.MINUTE,60);
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        String currentTime = sdf.format(new Date());
        timeFormat = new SimpleDateFormat("hh:mm a");
//        String currentTime = cal.get(Calendar.HOUR_OF_DAY)
//                + ":"
//                + cal.get(Calendar.MINUTE);
      /*  String currentTime="";
        try {
            cal.add(Calendar.HOUR,1);
            currentTime = timeFormat.format(cal.getTime());
            System.out.println(currentTime);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        lblDeliveryCharge= (TextView) findViewById(R.id.lblDeliveryCharge);
        lblPickUpFromShop= (TextView) findViewById(R.id.lblPickUpFromShop);
        lblChargeValue= (TextView) findViewById(R.id.lblChargeValue);
        lblGrandTotal= (TextView) findViewById(R.id.lblGrandTotal);
        lblDiscount = (TextView) findViewById(R.id.lblDiscount);

        ed_delivery_date = (EditText) findViewById(R.id.ed_delivery_date);
        ed_delivery_time = (EditText) findViewById(R.id.ed_delivery_time);
        ed_promoCode=(EditText)findViewById(R.id.promoEdit);
        mapplyPromoCode=(TextView)findViewById(R.id.applyText);
        showPromoCode =(TextView)findViewById(R.id.promotxt);
        delivery_layout  = (LinearLayout) findViewById(R.id.delivery_layout);
        applyLayout =(LinearLayout)findViewById(R.id.applylayout);
        promo_layout=(LinearLayout)findViewById(R.id.promo_layout);
        deliveryLabel =(TextView)findViewById(R.id.deliveryLabl);

//        ed_delivery_date.setText(currentDate);
        //  ed_delivery_time.setText(""+currentTime);
        updateLabel();

        currentdate = ed_delivery_date.getText().toString();
        dTotal = mDBHelper.getTotal();
        ((TextView) findViewById(R.id.lblSubTotal)).setText(df.format(dTotal));

        mDBHelper = new DBHelper(PaymentMethodActivity.this);
        productList = new ArrayList<Product>();
        productList = mDBHelper.products(null);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        Bundle b = getIntent().getExtras();
        if(b!=null){
            label = b.getString(StringUtils.EXTRA_LABEL);

            Cursor holidayCursor=mDBHelper.getHolidayCursor();
            shop=holidayCursor.getString(holidayCursor.getColumnIndex(COL_SHOP));
            delivery_amt =holidayCursor.getString(holidayCursor.getColumnIndex(COL_DELIVERY));
            Log.d("shopDelivery",""+shop +" "+delivery_amt);
            amt=Double.parseDouble(delivery_amt);

            String  values = b.getString(StringUtils.EXTRA_VALUE);

            if(values.equals("")){
                values="0";
            }
            double data=Double.parseDouble(values);
            double valueAdded=data+amt;

            if(label.equals("Delivery Charges")) {
                if (shop.equals("1")) {
                    value = String.valueOf(valueAdded);
                } else {
                    value = b.getString(StringUtils.EXTRA_VALUE);
                }
            }else{
                value = b.getString(StringUtils.EXTRA_VALUE);
            }

            Log.d("CheckDetails",""+shop+" "+delivery_amt+" "+values+" "+value);

            delivery_amount=value;

            if(label.equals("Delivery Charges")){
                lblDeliveryCharge.setVisibility(View.VISIBLE);
                lblPickUpFromShop.setVisibility(View.GONE);
                findViewById(R.id.serviceCharge_Layout).setVisibility(View.VISIBLE);

            }else if(label.equals("Pickup from Store")){
                lblPickUpFromShop.setVisibility(View.VISIBLE);
                lblDeliveryCharge.setVisibility(View.GONE);
                findViewById(R.id.serviceCharge_Layout).setVisibility(View.GONE);
            }
            if(value!=null && !value.isEmpty()){
                lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(value)));
                value = Utils.twoDecimalPoint(Double.valueOf(value));
            }else{
                value="0";
            }
        }

        try {
            cal.add(Calendar.HOUR,1);
            String time = timeFormat.format(cal.getTime());
            SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
            currentTime =date24Format.format(date12Format.parse(time));
            Log.d("currentTime",""+currentTime);
            getLateNightAmount(currentTime);
            System.out.println(currentTime);
            ed_delivery_time.setText(""+time);
        }catch (Exception e){
            e.printStackTrace();
        }

//            int date=cal.get(Calendar.HOUR_OF_DAY);
//            int minute = cal.get(Calendar.MINUTE);



//            if(date>=22){
//                lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.10));
//                value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.10);
//                Log.d("deliveryValue2",""+value);
//
//            }else if(date<=7){
//                lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.25));
//                value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.25);
//                Log.d("deliveryValue3",""+value);
//            }else{
//                lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)));
//                value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount));
//                Log.d("deliveryValue4",""+value);
//            }




        //  if(label.equals("Delivery Charges")){
        dServiceTax = mDBHelper.getServiceTaxTotal();
        // }
        //set change listener
        radiogroup1.setOnCheckedChangeListener(this);
        findViewById(R.id.btnPlaceOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType();
            }
        });
        Log.d("productList","-->"+productList.size());

        mCheckOutAdapter = new CheckOutAdapter(PaymentMethodActivity.this, productList);
        mRecyclerView.setAdapter(mCheckOutAdapter);

        ed_delivery_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpDialog = new DatePickerDialog(PaymentMethodActivity.this, date, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
//                dpDialog.setMinDate(System.currentTimeMillis() - 1000);
//                dpDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dpDialog.show();
                dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }
        });

       /* ed_delivery_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PaymentMethodActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

                           if(currentdate.equalsIgnoreCase(ed_delivery_date.getText().toString())){
                               Log.d("current date",sdf.format(cal.getTime()));
                               if(hour < selectedHour){
                                   SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                                   SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

                                   Date date1 = displayFormat.parse("" + selectedHour + ":" + selectedMinute);
//                            System.out.println(parseFormat.format(date1) + " = " + displayFormat.format(date1));
                                   String deliverytime =parseFormat.format(date1) *//*+ " = " +displayFormat.format(date1)*//*;
                                   Log.d("deliverytime", "dt " + deliverytime);
                                   ed_delivery_time.setText(deliverytime);
                               }else{
                                   Toast.makeText(PaymentMethodActivity.this,"Please select valid delivery time",Toast.LENGTH_SHORT).show();
                               }

                           }else{
                               Log.d("another date",ed_delivery_date.getText().toString());
                               SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                               SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

                               Date date1 = displayFormat.parse("" + selectedHour + ":" + selectedMinute);
//                            System.out.println(parseFormat.format(date1) + " = " + displayFormat.format(date1));
                               String deliverytime =parseFormat.format(date1) *//*+ " = " +displayFormat.format(date1)*//*;
                               Log.d("deliverytime", "dt " + deliverytime);
                               ed_delivery_time.setText(deliverytime);
                           }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //ed_delivery_time.setText(timeFormat.format(cal.getTime()));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });*/

        ed_delivery_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d("valueonClick",""+delivery_amount);
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                Log.d("Calendarhour",""+hour);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PaymentMethodActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        try {
                            String selectedValue = String.valueOf(selectedHour).toString() + ":" + String.valueOf(selectedMinute).toString();

                            SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
                            SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
                            String time =date12Format.format(date24Format.parse(selectedValue));

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date strDate = sdf.parse(ed_delivery_date.getText().toString());
                            if (System.currentTimeMillis() > strDate.getTime()) {
                                getLateNightAmount(selectedValue);
                                Calendar datetime = Calendar.getInstance();
                                Calendar c = Calendar.getInstance();
                                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                                datetime.set(Calendar.MINUTE, selectedMinute);
                                if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                                    ed_delivery_time.setText(time);
                                } else {
                                    Toast.makeText(PaymentMethodActivity.this, "Invalid Time", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                ed_delivery_time.setText(time);
                            }

                            Log.d("deliverytime1", "dt "  +selectedValue);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //ed_delivery_time.setText(timeFormat.format(cal.getTime()));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

//                            Date date1 = displayFormat.parse("" + selectedHour + ":" + selectedMinute);
//                            String deliverytime =displayFormat.format(new Date());
//                            Log.d("deliverytime", "dt " + date1);



//                            if(currentdate.equalsIgnoreCase(ed_delivery_date.getText().toString())){
//                                Log.d("sameDate",ed_delivery_date.getText().toString());

        //manually check time
//                                if(hour < selectedHour){
//                                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
//                                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
//
//                                    Date date1 = displayFormat.parse("" + selectedHour + ":" + selectedMinute);
////                            System.out.println(parseFormat.format(date1) + " = " + displayFormat.format(date1));
//                                    String deliverytime =parseFormat.format(date1)
//                                           /*+ " = " +displayFormat.format(date1)*/;
//
//                                    Log.d("deliverytime1", "dt "  +selectedHour);
//
//                                    if(selectedHour>=22){
//                                        lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.10));
//                                        value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.10);
//                                        Log.d("deliveryValue2",""+delivery_amount +Utils.twoDecimalPoint(Double.valueOf(delivery_amount)));
//
//
//                                    }else if(selectedHour<=7){
//                                        lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.25));
//                                        value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.25);
//                                        Log.d("deliveryValue3",""+delivery_amount  +Utils.twoDecimalPoint(Double.valueOf(value)));
//
//                                    }else{
//                                        lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)));
//                                        value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount));
//                                        Log.d("deliveryValue4",""+delivery_amount +Utils.twoDecimalPoint(Double.valueOf(value)) );
//
//                                    }
//                                    Log.d("cashOnDeliveryCalc",""+value);
//                                    cashOnDeliveryCalc();
//
//                                    ed_delivery_time.setText(deliverytime);
//
//                                }else{
//                                    Toast.makeText(PaymentMethodActivity.this,"Please select valid delivery time",Toast.LENGTH_SHORT).show();
//                                }

//                            }else{
//                                Log.d("anotherDate",ed_delivery_date.getText().toString());
//                                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
//                                SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
//
//                                Date date1 = displayFormat.parse("" + selectedHour + ":" + selectedMinute);
////                            System.out.println(parseFormat.format(date1) + " = " + displayFormat.format(date1));
//                                String deliverytime =parseFormat.format(date1) /*+ " = " +displayFormat.format(date1)*/;
//                                Log.d("deliverytime", "dt " + deliverytime);
//
//                                Log.d("deliverytime1", "dt "  +selectedHour);
//
//
//                                if(selectedHour>=22){
//                                    lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.10));
//                                    value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.10);
//                                    Log.d("deliveryValue2",""+value);
//
//
//                                }else if(selectedHour<=7){
//                                    lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.25));
//                                    value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount)*1.25);
//                                    Log.d("deliveryValue3",""+value);
//
//                                }else{
//                                    lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)));
//                                    value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount));
//                                    Log.d("deliveryValue4",""+value);
//
//                                }
//                                Log.d("cashOnDeliveryCalc",""+value);
//                                cashOnDeliveryCalc();
//                                ed_delivery_time.setText(deliverytime);
//                            }



        //  ((TextView) findViewById(R.id.serviceChargelbl)).setText(name+" : ");

      /*  ((TextView) findViewById(R.id.lblserviceChargeRate)).setText(Utils.twoDecimalPoint(dServiceTax));


        dCharges = Double.valueOf(value);
        if(label.equals("Delivery Charges")){
            dNetTotal = dTotal + dServiceTax + dCharges;
        }else if(label.equals("Pickup from Store")){
            dNetTotal = dTotal + dCharges;
        }*/

        lblGrandTotal.setText(Utils.twoDecimalPoint(dNetTotal));
        mProgressDialog = ProgressDialog.show(PaymentMethodActivity.this, "Loading...", true, true, this);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mProgressDialog.setCancelable(false);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);

        mGetPercentage();
        applyCode();

//        promoLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                applyLayout.setVisibility(View.VISIBLE);
//                applyCode();
//            }
//        });
    }

    private void getCouponCodeCheck() {
        Log.d("CursorCouponCount",""+coupon_code_cursor.getCount());
        Log.d("SubTotalAmt1",""+dTotal);
        if (coupon_code_cursor != null && coupon_code_cursor.getCount() > 0) {
            applyLayout.setVisibility(View.GONE);
            if (coupon_code_cursor.moveToFirst()) {
                do {
                    String code = coupon_code_cursor.getString(coupon_code_cursor
                            .getColumnIndex(DBHelper.COL_COUPON));
                    Double discount = coupon_code_cursor.getDouble(coupon_code_cursor
                            .getColumnIndex(DBHelper.COL_DISCOUNT));
                    double frm_amt = coupon_code_cursor.getDouble(coupon_code_cursor
                            .getColumnIndex(DBHelper.COL_MIN_AMT));
                    double to_amt = coupon_code_cursor.getDouble(coupon_code_cursor
                            .getColumnIndex(DBHelper.COL_MAX_AMT));
                    String type=coupon_code_cursor.getString(coupon_code_cursor
                            .getColumnIndex(DBHelper.COL_TYPE));
                    Log.d("DiscountAmt",""+discount +type);
                    Log.d("DiscountAmtCode",""+code);

                    getCalc(frm_amt,to_amt,discount,dTotal,type);

                    promo_layout.setVisibility(View.VISIBLE);

                    showPromoCode.setText("The Applied Code is : "+ code +".");

                } while (coupon_code_cursor.moveToNext());
            }
        }else{
            applyLayout.setVisibility(View.VISIBLE);
        }

    }

    private void getLateNightAmount(final String deliverytime) {
        thrdLateNightCharge=  new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;

                try {

                    RestClientAcessTask client = new RestClientAcessTask(PaymentMethodActivity.this, FUNC_LATE_NIGHT_CHARGE);
                    client.addParam("Time", deliverytime);
                    getTme =deliverytime;
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        Log.d("ResponseStatus", "" + sResponse);
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
                msg.setData(bndle);
                ctgLateNightProgressHandler.sendMessage(msg);
            }
        };
        thrdLateNightCharge.start();
    }

    private Handler ctgLateNightProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdLateNightCharge.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArrayLength", "" + len +jsonArray);
                        if(len>0){
                            for (int i = 0; i < len; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                lateCharge=object.getString("latecharge");
                                late_charge=Double.parseDouble(lateCharge);
                            }
                            deliveryLabel.setVisibility(View.VISIBLE);
                            Log.d("LateCharge",""+late_charge+"   "+lateCharge);
                            lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount) * late_charge));
                            value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount) *late_charge);
                            deliveryLabel.setText("Selceted Time is: "+getTme +" and the delivery charge is : "+lateCharge);
                            Log.d("deliveryValue1",""+value);
                            if(paymethodType.equals("CashOnDelivery")){
                                cashOnDeliveryCalc();
                            }else if(paymethodType.equals("PayPal")) {
                                paypalCalc();
                            }
                        } else{
                            lblChargeValue.setText(Utils.twoDecimalPoint(Double.valueOf(delivery_amount)));
                            value = Utils.twoDecimalPoint(Double.valueOf(delivery_amount));
                            Log.d("deliveryValue2",""+value);
                            if(paymethodType.equals("CashOnDelivery")){
                                cashOnDeliveryCalc();
                            }else if(paymethodType.equals("PayPal")) {
                                paypalCalc();
                            }
                        }
                    }
                    mProgressDialog.setMessage("Loading...");


                } catch (Exception e) {
                    //  e.printStackTrace();
                    mUIHelper.showErrorDialog(e.getMessage());
                }
            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }
        }
    };


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        ed_delivery_date.setText(sdf.format(cal.getTime()));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton)group. findViewById(checkedId);
        switch(checkedId){
            case R.id.radio0:

                break;
            case R.id.radio1:

                coupon_code_cursor= mDBHelper.getCouponCodeCursor();
                Log.d("CouponShow",""+couponShow +coupon_code_cursor.getCount());
                paymethodType = "PayPal";
                findViewById(R.id.total_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.product_layout).setVisibility(View.VISIBLE);
                delivery_layout.setVisibility(View.VISIBLE);
                applyLayout.setVisibility(View.GONE);

                if(coupon_code_cursor.getCount()>0){
                    applyLayout.setVisibility(View.GONE);
                }else{
                    applyLayout.setVisibility(View.VISIBLE);
                }
                if(label.equals("Delivery Charges")) {
                    if (shop.equals("1")) {

                        Log.d("delivery_amt","-->"+delivery_amt);

                        if(mSettings.getToastShow().equals("NOTSHOW")){
                            mSettings.setToastShow("SHOW");
                            Toast.makeText(getApplicationContext(), "Public Holiday delivery charge:"+delivery_amt+"$ is Added!!!", Toast.LENGTH_SHORT).show();
                        }

//                        if (toastShow.matches("shown")) {
//                            Toast.makeText(getApplicationContext(), "Public Holiday delivery charge Added!!!", Toast.LENGTH_SHORT).show();
//                            toastShow = "";
//                        }
                    }
                }

                if(couponShow.matches("notApplied")){
                    paypalCalc();
                    applyLayout.setVisibility(View.VISIBLE);
                }else{
                    paypalCalc();
                    applyLayout.setVisibility(View.GONE);
                }

                paypal();
                getCouponCodeCheck();
                break;
            case R.id.radio2:
                coupon_code_cursor= mDBHelper.getCouponCodeCursor();

                Log.d("CouponShow",""+couponShow   +coupon_code_cursor.getCount());
                paymethodType = "CashOnDelivery";
                findViewById(R.id.total_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.product_layout).setVisibility(View.VISIBLE);
                delivery_layout.setVisibility(View.VISIBLE);
                applyLayout.setVisibility(View.GONE);

                if(coupon_code_cursor.getCount()>0){
                    applyLayout.setVisibility(View.GONE);
                }else{
                    applyLayout.setVisibility(View.VISIBLE);
                }

                if(label.equals("Delivery Charges")) {
                    if (shop.equals("1")) {
                        if(mSettings.getToastShow().equals("NOTSHOW")){
                            mSettings.setToastShow("SHOW");
                            Toast.makeText(getApplicationContext(), "Public Holiday delivery charge:"+delivery_amt+"$ is Added!!!", Toast.LENGTH_SHORT).show();
                        }
//                        if (toastShow.matches("shown")) {
//                            Toast.makeText(getApplicationContext(), "Public Holiday delivery charge Added!!!", Toast.LENGTH_SHORT).show();
//                            toastShow = "";
//                        }
                    }
                }

                if(couponShow.matches("notApplied")){
                    cashOnDeliveryCalc();
                    applyLayout.setVisibility(View.VISIBLE);
                }else{
                    cashOnDeliveryCalc();
                    applyLayout.setVisibility(View.GONE);
                }

                getCouponCodeCheck();
                break;
        }
    }

    private void applyCode() {
        mapplyPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promo_Code=ed_promoCode.getText().toString();
                if(Utils.isEmpty(promo_Code)){
                    ed_promoCode.setError("Type Promo Code");
                }else{
                    mProgressDialog.setMessage("Loading...");
                    applyPromoCode();
                }
            }
        });
    }

    private void applyPromoCode() {
        thrdPromoCode = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateToStr = dateFormat.format(today);

                try {

                    RestClientAcessTask client = new RestClientAcessTask(PaymentMethodActivity.this, FUNC_GET_COUPON_CODE);
                    client.addParam("Choose Date","2019-01-30");
//                    client.addParam("Choose Date",dateToStr);
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
                msg.setData(bndle);
                ctgPromoCodeProgressHandler.sendMessage(msg);
            }
        };
        thrdPromoCode.start();

    }

    private Handler ctgPromoCodeProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdPromoCode.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArrayLength", "" + len );

                        if (len > 0) {
                            for (int i = 0; i < len; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                code = object.getString("code");
                                from_amt = object.getString("total");
                                strt_amt = Double.parseDouble(from_amt);
                                to_amt = object.getString("toamount");
                                end_amt = Double.parseDouble(to_amt);
                                discount = object.getString("discount");
                                types=object.getString("type");
                                dis_amt = Double.parseDouble(discount);

                                if (code.equals(promo_Code)) {
                                    min_amt=strt_amt;
                                    max_amt=end_amt;
                                    discount_amt=dis_amt;
                                    type=types;
                                    capitalCheck="1";
                                }
                            }

                            Log.d("Discount", "" + discount_amt);
                            Log.d("promoAmt", "" +min_amt +"  "+max_amt);
                            Log.d("CapitalCheck",""+capitalCheck);
                            if(capitalCheck.equals("1")){
                                getCalc(min_amt,max_amt,discount_amt,dTotal,type);
                            }  else {
                                createAlertForValidPromoCode();
                            }

                        }else {
                            createAlertForDateNotFound();
                        }

                    }else{
                        mUIHelper.showErrorDialog(sResponseMsg);
                    }
                } catch (Exception e) {
                    //  e.printStackTrace();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }

        }
    };


    private void getCalc(Double min_amt, Double max_amt, Double discount_amt, Double dTotal,String type) {
        Log.d("subTotalAmt",""+dTotal);
        coupon_code_cursor= mDBHelper.getCouponCodeCursor();
        Log.d("minmaxdisc",""+min_amt+"  "+max_amt+"  "+discount_amt +" "+coupon_code_cursor.getCount());


        if(coupon_code_cursor.getCount()>0){
            applyLayout.setVisibility(View.GONE);
            if((min_amt <=dTotal)&&(dTotal<= max_amt)){
                Log.d("this.dTotal",""+this.dTotal);
                couponShow="applied";
                if(type.equals("P")){
                    sub_code=dTotal *(discount_amt /100);
                }else{
                    sub_code = discount_amt;
                }
                total= dTotal -sub_code;

                ((LinearLayout) findViewById(R.id.discountAmt)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.discount)).setVisibility(View.VISIBLE);
                lblDiscount.setText(df.format(sub_code));
                ((TextView) findViewById(R.id.lblDiscountAMT)).setText(df.format(total));
            }else{
                if(chooseAlert==false){
                    createAlertForRate();
                    chooseAlert=true;
                }
            }
        }else{
            if((min_amt <=dTotal)&&(dTotal<= max_amt)){
                String discount =String.valueOf(discount_amt);
                String minAmt=String.valueOf(min_amt);
                String maxAmt=String.valueOf(max_amt);

                HashMap<String,String> couponCode=new HashMap<>();
                couponCode.put(KEY_COUPON_CODE,promo_Code);
                couponCode.put(KEY_DISCOUNT,discount);
                couponCode.put(KEY_MIN_AMT,minAmt);
                couponCode.put(KEY_MAX_AMT,maxAmt);
                couponCode.put(KEY_TYPE,type);
                mDBHelper.insertCouponCode(couponCode);

                Toast.makeText(getApplicationContext(),"Coupon Code Applied Successfully",Toast.LENGTH_SHORT).show();
                applyLayout.setVisibility(View.GONE);
                couponShow="applied";
                if(type.equals("P")){
                    sub_code=dTotal *(discount_amt /100);
                }else{
                    sub_code = discount_amt;
                }
                total= dTotal -sub_code;

                ((LinearLayout) findViewById(R.id.discountAmt)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.discount)).setVisibility(View.VISIBLE);
                lblDiscount.setText(df.format(sub_code));
                ((TextView) findViewById(R.id.lblDiscountAMT)).setText(df.format(total));
            }else{
                createAlertForRate();

            }


        }

        if(paymethodType.equals("CashOnDelivery")){
            cashOnDeliveryCalc();
        }else if(paymethodType.equals("PayPal")) {
            paypalCalc();
        }
    }


    private void createAlertForRate() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Coupon Code Not Applicable for this price!! ..")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void createAlertForDateNotFound() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No Coupon Available Today....")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void createAlertForValidPromoCode() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Please Type Valid Coupon Code....")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void cashOnDeliveryCalc(){

        try {
            dServiceTax = Double.valueOf(tAmount);
            Log.d("NetCashTotal",""+dTotal+"-->"+value+"serviceTax"+dServiceTax);
            if (label.equals("Delivery Charges")) {
                dCharges = Double.valueOf(value);
                dServiceTax = roundToHalf(dServiceTax);
                Log.d("DeliveryServiceCharge",""+dServiceTax);
                if(total.equals(0.0)){
                    dNetTotal = dTotal + dServiceTax + dCharges;
                    Log.e("Deliveryzero", String.valueOf(dNetTotal));
                }else{
                    dNetTotal = total + dServiceTax + dCharges;
                    Log.e("Delivery", String.valueOf(dNetTotal));
                }

                findViewById(R.id.serviceCharge_Layout).setVisibility(View.VISIBLE);
            } else if (label.equals("Pickup from Store")) {
                dServiceTax = 0.00;
                if(total.equals(0.0)){
                    dNetTotal = dTotal;
                    Log.e("Pickupzero", String.valueOf(dNetTotal));
                }else{
                    dNetTotal = total;
                    Log.e("Pickup", String.valueOf(dNetTotal));
                }
                findViewById(R.id.serviceCharge_Layout).setVisibility(View.GONE);
            }
            ((TextView) findViewById(R.id.lblserviceChargeRate)).setText(Utils.twoDecimalPoint(dServiceTax));
            lblGrandTotal.setText(Utils.twoDecimalPoint(dNetTotal));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void paypalCalc(){
        Log.d("NetCashPaypalTotal",""+dTotal);
        try {
            dServiceTax = Double.valueOf(tAmount);
            if(label.equals("Delivery Charges")){
                dCharges = Double.valueOf(value);
                Log.d("dServiceTax","befr->"+dServiceTax);
                dServiceTax = dServiceTax +(dTotal*3.49)/100 + 1;
                dServiceTax = roundToHalf(dServiceTax);
                Log.d("dTotal","->"+total);
                Log.d("dServiceTax","af->"+dServiceTax);
                Log.d("dCharges","->"+(dCharges));
                if(total.equals(0.0)){
                    dNetTotal = dTotal + dServiceTax + dCharges;
                }else{
                    dNetTotal = total + dServiceTax + dCharges;
                }

            }else if(label.equals("Pickup from Store")){
                dServiceTax = (total*3.49)/100 + 1;
                dServiceTax = roundToHalf(dServiceTax);
                if(total.equals(0.0)){
                    dNetTotal = dTotal + dServiceTax;
                }else{
                    dNetTotal = total + dServiceTax;
                }

            }
            ((TextView) findViewById(R.id.lblserviceChargeRate)).setText(Utils.twoDecimalPoint(dServiceTax));
            findViewById(R.id.serviceCharge_Layout).setVisibility(View.VISIBLE);
            lblGrandTotal.setText(Utils.twoDecimalPoint(dNetTotal));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public double roundToHalf(double x) {
        /*return (double) (Math.ceil(x * 2) / 2);*/
        return  (double) (Math.round(x));
    }
    public void paymentType(){
        if(paymethodType!=null && !paymethodType.isEmpty()){
            if(paymethodType.matches("PayPal")){
                getPayment();
            }else if(paymethodType.matches("CashOnDelivery")){
                cashOnDelivery();
            }
        }else{
            Toast.makeText(PaymentMethodActivity.this,"Please Select the payment type",Toast.LENGTH_SHORT).show();
        }
    }

    public void paypal(){
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void getPayment() {
        //Getting the amount from editText
        String paymentAmount = lblGrandTotal.getText().toString();

        Log.d("paymentAmount",paymentAmount);

        //Creating a paypalpayment
//        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "SGD", "Haji Kadir Restaurant",
//                PayPalPayment.PAYMENT_INTENT_SALE);

        PayPalPayment payment = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }
    public void cashOnDelivery(){
        HashMap<String, String> userDetails = mSession.getUserDetails();
        String userId = userDetails.get(PREF_SESSION_USER_ID);
        String password = userDetails.get(PREF_SESSION_PASSWORD);
        Log.d("userId","-->"+userId);
        Log.d("password","-->"+password);
        if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()){

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            saveOrder();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder ab = new AlertDialog.Builder(PaymentMethodActivity.this);
            ab.setMessage("Are you sure to place order?").setPositiveButton("Yes", dialogClickListener)  .setNegativeButton("No", dialogClickListener).show();

        }
        else{
            mIntent.setClass(PaymentMethodActivity.this, LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
            finish();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);

        menu.findItem(R.id.Home).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
        menu.findItem(R.id.Products).setVisible(false);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(false);
        menu.findItem(R.id.ContactUs).setVisible(false);
        menu.findItem(R.id.PrivacyPolicy).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
//
//        Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);
//
//        // send the same configuration for restart resiliency
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
//
//        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//
//        saveOrder();
//
//    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("0.01"), "SGD", "Hajikadir Restaurant", paymentIntent);
    }

    private PayPalPayment getStuffToBuy(String paymentIntent) {
        paypalvalue = new ArrayList<>();
        double dPrice;
        PayPalPayment payment = new PayPalPayment(new BigDecimal("0"), "SGD", "Hajikadir Restaurant", paymentIntent);
        //--- include an item list, payment amount details
        cursor = mDBHelper.getProductCursor();
        final String totalValue =  lblGrandTotal.getText().toString();
        final String storeId = mSession.getBranchId();

        final  String totalTax = Utils.twoDecimalPoint(dServiceTax);

        final  String netTotal = Utils.twoDecimalPoint(dNetTotal);

        Log.d("getCountCheck","->"+cursor.getCount()+","+totalValue);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String slno = cursor.getString(cursor.getColumnIndex(DBHelper.COL_SL_NO));
                    String productCode = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PRODUCT_CODE));
                    String productName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PRODUCT_NAME));
                    String model = cursor.getString(cursor.getColumnIndex(DBHelper.COL_MODEL));
                    String qty = cursor.getString(cursor.getColumnIndex(DBHelper.COL_QUANTITY));

                    Log.d("productNameCheck", productName);

                    String productModifier = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IS_PRODUCT_MODIFIER));

                    boolean isProductModifier = Boolean.valueOf(productModifier);
                    if(isProductModifier){
                        dPrice = cursor.getDouble(cursor
                                .getColumnIndex(DBHelper.COL_MODIFIER_PRICE));
                    }else{
                        dPrice = cursor.getDouble(cursor
                                .getColumnIndex(DBHelper.COL_PRICE));
                    }
                    String price = Utils.twoDecimalPoint(Double.valueOf(dPrice));

                    Paypalitems paypalitems = new Paypalitems();
                    paypalitems.setProductname(productName);
                    paypalitems.setQty(Integer.parseInt(qty));
                    paypalitems.setPrice(price);
                    paypalitems.setSlno(slno);
                    paypalvalue.add(paypalitems);

                    PayPalItem[] items =new PayPalItem[paypalvalue.size()];
                    for(int i=0;i<paypalvalue.size();i++) {
                        items[i]=new PayPalItem(paypalvalue.get(i).getProductname(), Integer.parseInt(String.valueOf(paypalvalue.get(i).getQty())),
                                new BigDecimal(paypalvalue.get(i).getPrice()), "SGD",
                                paypalvalue.get(i).getSlno());

                        Log.e("Paypalitems", String.valueOf(items));
                    }

                    BigDecimal subtotal = PayPalItem.getItemTotal(items);
                    BigDecimal shipping = new BigDecimal(value);
                    BigDecimal tax = new BigDecimal(totalTax);
                    PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
                    BigDecimal amount = subtotal.add(shipping).add(tax);
                    payment = new PayPalPayment(amount, "SGD", "Hajikadir Restaurant", paymentIntent);
                    payment.items(items).paymentDetails(paymentDetails);
                    Log.e("PaymentItems", String.valueOf(payment.items(items)));
                    Log.e("PaymentItems", String.valueOf(payment.items(items).paymentDetails(paymentDetails)));
                    payment.custom("This is text that will be associated with the payment that the app can use.");

                    /*for (Paypalitems anArray : paypalvalue) {

                        PayPalItem[] items =
                                {
                                        new PayPalItem(anArray.getProductname(), Integer.parseInt(String.valueOf(anArray.getQty())), new BigDecimal(anArray.getPrice()), "SGD", anArray.getSlno())
                                };
                        Log.e("PaymentItems", String.valueOf(items));
                        BigDecimal subtotal = PayPalItem.getItemTotal(items);

                        BigDecimal shipping = new BigDecimal(value);
                        BigDecimal tax = new BigDecimal(totalTax);
                        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
                        BigDecimal amount = subtotal.add(shipping).add(tax);
                        payment = new PayPalPayment(amount, "SGD", "Hajikadir Restaurant", paymentIntent);
                        payment.items(items).paymentDetails(paymentDetails);
                        Log.e("PaymentItems", String.valueOf(payment.items(items)));
                        Log.e("PaymentItems", String.valueOf(payment.items(items).paymentDetails(paymentDetails)));
                        payment.custom("This is text that will be associated with the payment that the app can use.");
                    }*/

                    /*for(int i=0; i<paypalvalue.size(); i++)
                    {
                        Log.e("Productnames", paypalvalue.get(i).getProductname());

                        PayPalItem[] items =
                                {
                                        new PayPalItem(paypalvalue.get(i).getProductname(), Integer.parseInt(String.valueOf(paypalvalue.get(i).getQty())), new BigDecimal(paypalvalue.get(i).getPrice()), "SGD", paypalvalue.get(i).getSlno())
                                };
                        Log.e("PaymentItems", String.valueOf(items));
                        BigDecimal subtotal = PayPalItem.getItemTotal(items);

                        BigDecimal shipping = new BigDecimal(value);
                        BigDecimal tax = new BigDecimal(totalTax);
                        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
                        BigDecimal amount = subtotal.add(shipping).add(tax);
                        payment = new PayPalPayment(amount, "SGD", "Hajikadir Restaurant", paymentIntent);
                        payment.items(items).paymentDetails(paymentDetails);
                        Log.e("PaymentItems", String.valueOf(payment.items(items)));
                        Log.e("PaymentItems", String.valueOf(payment.items(items).paymentDetails(paymentDetails)));
                        payment.custom("This is text that will be associated with the payment that the app can use.");
                    }*/
                } while (cursor.moveToNext());
            }
        }

        return payment;
    }

    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName("Mom Parker").line1("52 North Main St.")
                        .city("Austin").state("TX").postalCode("78729").countryCode("US");
        paypalPayment.providedShippingAddress(shippingAddress);
    }

    /*
     * Enable retrieval of shipping addresses from buyer's PayPal account
     */
    private void enableShippingAddressRetrieval(PayPalPayment paypalPayment, boolean enable) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(PaymentMethodActivity.this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    public void onProfileSharingPressed(View pressed) {
        Intent intent = new Intent(PaymentMethodActivity.this, PayPalProfileSharingActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
        return new PayPalOAuthScopes(scopes);
    }

    protected void displayResultText(String result) {
        Log.d("resultCheck",result);
//        ((TextView)findViewById(R.id.txtResult)).setText("Result : " + result);
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivityCheck",""+resultCode);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);

                        /*startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", lblGrandTotal.getText().toString()));*/

                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        Toast.makeText(getApplicationContext(),"PaymentConfirmation info received from PayPal",Toast.LENGTH_SHORT).show();
//                        displayResultText("PaymentConfirmation info received from PayPal");
                        saveOrder();
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);

        // TODO: Send metadataId and transaction details to your server for processing with
        // PayPal...
        displayResultText("Client Metadata Id received from SDK");
    }


    /*  @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //If the result is from paypal
            if (requestCode == PAYPAL_REQUEST_CODE) {

                //If the result is OK i.e. user has not canceled the payment
                if (resultCode == Activity.RESULT_OK) {
                    //Getting the payment confirmation
                    PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                    //if confirmation is not null
                    if (confirm != null) {
                        try {
                            //Getting the payment details
                            String paymentDetails = confirm.toJSONObject().toString(4);
                            Log.i("paymentExample", paymentDetails);

                            startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", lblGrandTotal.getText().toString()));

                            saveOrder();

                            //Starting a new activity for the payment details and also putting the payment details with intent
                          *//*  startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));*//*

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }*/
    private void successDialog(){
        final SweetAlertDialog sd = new SweetAlertDialog(PaymentMethodActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sd.setCancelable(true);
        sd.setCanceledOnTouchOutside(false);
        sd.setTitleText("Success");
        sd.setContentText("Successfully order placed !");
        sd.setConfirmText("ok");
        sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                mIntent.setClass(PaymentMethodActivity.this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                finish();
            }
        });
        sd.setOnKeyListener(new SweetAlertDialog.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    mIntent.setClass(PaymentMethodActivity.this, HomeActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);
                    finish();
                }
                return false;
            }
        });
        sd.show();
    }

    public void mGetPercentage(){

        thrdGetPercentage = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(PaymentMethodActivity.this, FUNC_GET_PERCENTAGE);
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
                msg.setData(bndle);
                ctgDistanceProgressHandler.sendMessage(msg);
            }
        };
        thrdGetPercentage.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgDistanceProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetPercentage.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArray", "" + jsonArray);

                        JSONObject object = jsonArray.getJSONObject(0);
                        name = object.getString("name");
                        rate = object.getString("rate");
                    }
                    mProgressDialog.setMessage("Loading...");
                    if(label.equals("Delivery Charges")){
                        mGetTaxCharge();
                    }else{
                        initDataLoadCompleted();
                    }
                } catch (Exception e) {
                    //  e.printStackTrace();
                    mUIHelper.showErrorDialog(e.getMessage());
                }
            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }
        }
    };

    public void mGetTaxCharge(){

        thrdGetTaxCharge = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(PaymentMethodActivity.this, FUNC_GET_TAXCHARGE);
                    Log.d("dTotalTaxCharge","-->"+dTotal);
                    client.addParam("tamount", ""+dTotal);
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
                        sDesc = "Error occured while loading data." + client.getErrorMessage();
                    }

                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading data!";
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                ctgTaxChargeProgressHandler.sendMessage(msg);
            }
        };
        thrdGetTaxCharge.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgTaxChargeProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetTaxCharge.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        tAmount =  jsonObject.getString("data");
                        Log.d("tAmount", "--" + tAmount);
                    }
                    mProgressDialog.setMessage("Loading...");
                    initDataLoadCompleted();
                } catch (Exception e) {
                    //  e.printStackTrace();
                    mUIHelper.showErrorDialog(e.getMessage());
                }
            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }
        }
    };
    public void initDataLoadCompleted() {
       /* ((TextView) findViewById(R.id.serviceChargelbl)).setText(name+" : ");
        if(rate!=null && !rate.isEmpty()){
            double iRate = Double.valueOf(rate);
            double iValue = iRate*dServiceTax/100;

        ((TextView) findViewById(R.id.lblserviceChargeRate)).setText(""+iValue);
        }*/
        ctgDistanceProgressHandler.postDelayed(new Runnable() {
            public void run() {
                mProgressDialog.dismiss();
            }
        }, 3000);
    }


//    public void saveOrder1(){
//
//        cursor = mDBHelper.getProductCursor();
//        final double subtotal = mDBHelper.getTotal();
//        //final double flatShipping = 5.00;
//        // final double netTotal = subtotal + flatShipping;
//
//        final String totalValue =  lblGrandTotal.getText().toString();
//        final String storeId = mSession.getBranchId();
//        final  String totalTax = Utils.twoDecimalPoint(dServiceTax);
//        final  String netTotal = Utils.twoDecimalPoint(dNetTotal);
//        HashMap<String, String> userDetails = mSettings.getUserInfo();
//        final String userId = userDetails.get(PREF_USER_ID);
//        final String firstName = userDetails.get(PREF_FIRST_NAME);
//        final String lastName = userDetails.get(PREF_LAST_NAME);
//        final String address1 = userDetails.get(PREF_ADDRESS1);
//        final String address2 = userDetails.get(PREF_ADDRESS2);
//        final String phoneNo = userDetails.get(PREF_PHONENO);
//        final String pincode = userDetails.get(PREF_PINCODE);
//        final String customerId = userDetails.get(PREF_CUSTOMER_ID);
//        final String customerGroupId = userDetails.get(PREF_CUSTOMER_GROUP_ID);
//        final String fax = userDetails.get(PREF_FAX);
//
//        final String city = userDetails.get(PREF_CITY);
//        final String countryId = userDetails.get(PREF_COUNTRY_ID);
//        final String zoneId = userDetails.get(PREF_ZONE_ID);
//
//        final String company = userDetails.get(PREF_COMPANY);
//        final StringBuilder sDetail = new StringBuilder();
////        StringBuilder sModifierDetail;
//        final StringBuilder sDetailValues = new StringBuilder();
//
//        Log.d(" cursor.getCount()", ""+cursor.getCount());
//
//        if (cursor != null && cursor.getCount() > 0) {
//
//            Log.d(" cursor inner ", "" + cursor.getCount());
//
////            mProgressDialog = ProgressDialog.show(PaymentMethodActivity.this, "Saving...", true, true, this);
////            mProgressDialog.setCancelable(false);
////            mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
////            mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
////            mAnimFadeIn.setDuration(1000);
////            mAnimFadeIn.setStartOffset(3000);
//
//
//            if (cursor.moveToFirst()) {
//                do {
////                               sModifierDetail.setLength(0);
//                    StringBuilder sModifierDetail = new StringBuilder();
//                    String tax = "0.00";
//                    String slno = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_SL_NO));
//                    String productCode = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_PRODUCT_CODE));
//                    String productName = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_PRODUCT_NAME));
//                    String model = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_MODEL));
//                    String qty = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_QUANTITY));
//                    String price = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_PRICE));
//                    String productTax = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_TAX));
//                    String total = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_TOTAL));
//
//                    Cursor cursor1 = mDBHelper.getProductModifierCursor(slno, productCode);
//
//                    if (cursor1 != null && cursor1.getCount() > 0) {
//                        Log.d("slno", "" + slno);
//                        Log.d("productCode", "" + productCode);
//                        if (cursor1.moveToFirst()) {
//                            do {
//                                /*String sno = cursor1.getString(cursor1
//                                        .getColumnIndex(DBHelper.COL_SL_NO));*/
//                                String modifierCode = cursor1.getString(cursor1
//                                        .getColumnIndex(DBHelper.COL_MODIFIER_CODE));
//                                String modifierName = cursor1.getString(cursor1
//                                        .getColumnIndex(DBHelper.COL_MODIFIER_NAME));
//                                String modifierPrice = cursor1.getString(cursor1
//                                        .getColumnIndex(DBHelper.COL_PRICE));
//
//                                sModifierDetail.append(modifierCode).append(COLUMN_SEPARATOR1)
//                                        .append(modifierName).append(COLUMN_SEPARATOR1)
//                                        .append(modifierPrice).append(LINE_SEPARATOR1);
//
//                            } while (cursor1.moveToNext());
//                        }
//                        Log.d("sModifierDetail: ", "-->" + sModifierDetail.toString());
//                        sModifierDetail.deleteCharAt(sModifierDetail.lastIndexOf(LINE_SEPARATOR1));
////                         sModifierDetail.append(LINE_SEPARATOR);
//                        Log.d("sModifierDetail: ", "-->" + sModifierDetail.toString());
//                    }
//
//                    if (cursor1 != null && cursor1.getCount() > 0)
//                    {
//                        sDetail.append(slno).append(COLUMN_SEPARATOR)
//
//                                /******** PRODUCTCODE ***********/
//                                .append(productCode).append(COLUMN_SEPARATOR)
//
//                                /******** PRODUCTNAME ***********/
//                                .append(productName).append(COLUMN_SEPARATOR)
//
//                                /******** MODEL ***********/
//                                .append(productName).append(COLUMN_SEPARATOR)
//
//                                /******** QUANTITY ***********/
//                                .append(qty).append(COLUMN_SEPARATOR)
//
//                                /******** PRICE ***********/
//                                .append(price).append(COLUMN_SEPARATOR)
//
//                                /******** TOTAL ***********/
//                                .append(total).append(COLUMN_SEPARATOR)
//
//                                /******** TAX ***********/
//                                .append(productTax).append(COLUMN_SEPARATOR)
//
//                                /******** REWARD ***********/
//                                .append(DATA_ZERO)
//
//                                .append(COLUMN_SEPARATOR)
//
//                                .append(sModifierDetail)
//
//                                .append(LINE_SEPARATOR);
//
//                        Log.d("sDetail: ", "modifier is not null -->" + sDetail.toString());
//
//
//                    }else{
//                        sDetail.append(slno).append(COLUMN_SEPARATOR)
//
//                                /******** PRODUCTCODE ***********/
//                                .append(productCode).append(COLUMN_SEPARATOR)
//
//                                /******** PRODUCTNAME ***********/
//                                .append(productName).append(COLUMN_SEPARATOR)
//
//                                /******** MODEL ***********/
//                                .append(productName).append(COLUMN_SEPARATOR)
//
//                                /******** QUANTITY ***********/
//                                .append(qty).append(COLUMN_SEPARATOR)
//
//                                /******** PRICE ***********/
//                                .append(price).append(COLUMN_SEPARATOR)
//
//                                /******** TOTAL ***********/
//                                .append(total).append(COLUMN_SEPARATOR)
//
//                                /******** TAX ***********/
//                                .append(productTax).append(COLUMN_SEPARATOR)
//
//                                /******** REWARD ***********/
//                                .append(DATA_ZERO)
//
//                                .append(LINE_SEPARATOR);
//
//                        Log.d("sDetail: ", "modifier is null -->" + sDetail.toString());
//                    }
//
//
//
//                    slNo++;
//                } while (cursor.moveToNext());
//
//            }
//            Log.d("sDetail: ", "-->" + sDetail.toString());
//            sDetail.deleteCharAt(sDetail.lastIndexOf(LINE_SEPARATOR));
//        }
//    }

    public void saveOrder(){

        cursor = mDBHelper.getProductCursor();
        final double dSubtotal = mDBHelper.getTotal();
        coupon_code_cursor=mDBHelper.getCouponCodeCursor();
        if(coupon_code_cursor.getCount()>0){
            subtotal=Utils.twoDecimalPoint(total);
        }else{
            subtotal = Utils.twoDecimalPoint(Double.valueOf(dSubtotal));
        }

        //final double flatShipping = 5.00;
        // final double netTotal = subtotal + flatShipping;

        final String totalValue =  lblGrandTotal.getText().toString();
        final String storeId = mSession.getBranchId();

        final  String totalTax = Utils.twoDecimalPoint(dServiceTax);

        final  String netTotal = Utils.twoDecimalPoint(dNetTotal);
        //final  String netTotal =  String.valueOf(Math.round(dNetTotal));

        HashMap<String, String> userDetails = mSettings.getUserInfo();
        final String userId = userDetails.get(PREF_USER_ID);
        final String firstName = userDetails.get(PREF_FIRST_NAME);
        final String lastName = userDetails.get(PREF_LAST_NAME);
        final String address1 = userDetails.get(PREF_ADDRESS1);
        final String address2 = userDetails.get(PREF_ADDRESS2);
        final String unitno = userDetails.get(PREF_UNITNO);
        final String phoneNo = userDetails.get(PREF_PHONENO);
        final String pincode = userDetails.get(PREF_PINCODE);
        final String customerId = userDetails.get(PREF_CUSTOMER_ID);
        final String customerGroupId = userDetails.get(PREF_CUSTOMER_GROUP_ID);
        final String fax = userDetails.get(PREF_FAX);

        final String city = userDetails.get(PREF_CITY);
        final String countryId = userDetails.get(PREF_COUNTRY_ID);
        final String zoneId = userDetails.get(PREF_ZONE_ID);

        final String company = userDetails.get(PREF_COMPANY);
        final StringBuilder sDetail = new StringBuilder();
//        StringBuilder sModifierDetail;
        final StringBuilder sDetailValues = new StringBuilder();

        Log.d(" cursor.getCount()", ""+cursor.getCount());
        Log.d("unitno::","-->"+unitno);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d(" cursor inner ", ""+cursor.getCount());

            mProgressDialog = ProgressDialog.show(PaymentMethodActivity.this,"Saving...", true,true,this);
            mProgressDialog.setCancelable(false);
            mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
            mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
            mAnimFadeIn.setDuration(1000);
            mAnimFadeIn.setStartOffset(3000);

            thrdPlaceOrder = new Thread() {
                @Override
                public void run() {
                    Message msg = new Message();
                    Bundle bndle = new Bundle();
                    String sCode = null, sDesc = null;
                    try {
                        RestClientAcessTask client = new RestClientAcessTask(PaymentMethodActivity.this, FUNC_GET_PLACE_ORDER);
                        client.addParam("invoice_prefix", "INV-2013-00");
                        client.addParam("store_id", storeId);
                        client.addParam("store_name", "Haji Kadir Food Chains Pte Ltd");
                        client.addParam("store_url", API_SERVER_URL);
                        client.addParam("customer_id", customerId);
                        client.addParam("customer_group_id", customerGroupId);
                        client.addParam("firstname", firstName);
                        client.addParam("lastname", lastName);
                        client.addParam("email", userId);
                        client.addParam("telephone", phoneNo);
                        client.addParam("fax", DATA_EMPTY);
                        client.addParam("custom_field", DATA_EMPTY);
                        client.addParam("payment_firstname", DATA_EMPTY);
                        client.addParam("payment_lastname", DATA_EMPTY);
                        client.addParam("payment_company", DATA_EMPTY);
                        client.addParam("payment_address_1", DATA_EMPTY);
                        client.addParam("payment_address_2", DATA_EMPTY);
                        client.addParam("payment_city", DATA_EMPTY);
                        client.addParam("payment_postcode", DATA_EMPTY);
                        client.addParam("payment_zone", DATA_EMPTY);
                        client.addParam("payment_zone_id", DATA_EMPTY);
                        client.addParam("payment_country", DATA_EMPTY);
                        client.addParam("payment_country_id", DATA_EMPTY);
                        client.addParam("payment_address_format", DATA_EMPTY);
                        client.addParam("payment_custom_field", DATA_EMPTY);

                        if(paymethodType.equals("CashOnDelivery")){
                            client.addParam("payment_method", "cash on delivery");
                            client.addParam("payment_code", "cod");
                        }else if(paymethodType.equals("PayPal")){
                            client.addParam("payment_method", "paypal");
                            client.addParam("payment_code", "pp_standard");
                        }

                        String unitnumber="";
                        if(unitno!=null && !unitno.isEmpty()){
                            if(unitnumber.matches("null")){
                                unitnumber="";
                            }else{
                                unitnumber=unitno;
                            }
                        }else{
                            unitnumber="";
                        }

                        client.addParam("shipping_firstname", firstName);
                        client.addParam("shipping_lastname", lastName);
                        client.addParam("shipping_company", DATA_EMPTY);
                        client.addParam("shipping_address_1", address1);
                        client.addParam("shipping_address_2", address2);
                        client.addParam("shipping_unitno", unitnumber);
                        client.addParam("shipping_city", city);
                        client.addParam("shipping_postcode", pincode);
                        client.addParam("shipping_zone", DATA_EMPTY);
                        client.addParam("shipping_zone_id", zoneId);
                        client.addParam("order_status", "1");
                        client.addParam("shipping_country", "SINGAPORE");
                        client.addParam("shipping_country_id", "188");
                        client.addParam("shipping_address_format", DATA_EMPTY);
                        client.addParam("shipping_custom_field", DATA_EMPTY);
                        client.addParam("shipping_method", label);
                        if(label.equals("Delivery Charges")){
                            client.addParam("shipping_code", "flat.flat");
                        }else if(label.equals("Pickup from Store")){
                            client.addParam("shipping_code", "Pickup.Pickup");
                        }
                        client.addParam("comment", DATA_EMPTY);
                        client.addParam("total", totalValue);
                        client.addParam("affiliate_id", DATA_ZERO);
                        client.addParam("commission", DATA_ZERO);
                        client.addParam("marketing_id", DATA_ZERO);
                        client.addParam("tracking", DATA_EMPTY);
                        client.addParam("language_id", "1");
                        client.addParam("currency_id", "4");
                        client.addParam("currency_code", "SGD");
                        client.addParam("currency_value", "1.00000000");
                        client.addParam("ip", "::1");
                        client.addParam("forwarded_ip", DATA_EMPTY);
                        client.addParam("user_agent", "MOZILLA/5.0 (WINDOWS NT 6.1) APPLEWEBKIT/537.36 (KHTML, LIKE GECKO) CHROME/56.0.2924.87 SAFARI/537.36");
                        client.addParam("accept_language", "EN-US,EN;Q=0.8");

                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

                        Date date = format1.parse(ed_delivery_date.getText().toString());
                        System.out.println(format2.format(date));
                        String deliverydate = format2.format(date);
                        Log.d("deliverydate","dd "+deliverydate);

                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

                        Date date1 = parseFormat.parse(ed_delivery_time.getText().toString());
                        System.out.println(parseFormat.format(date1) + " = " + displayFormat.format(date1));
                        String deliverytime = /*parseFormat.format(date1) + " = " + */displayFormat.format(date1);
                        Log.d("deliverytime","dt "+deliverytime);

                        client.addParam("order_delivery_date", deliverydate);
                        client.addParam("order_delivery_time", deliverytime);

                        if (cursor.moveToFirst()) {
                            do {
//                               sModifierDetail.setLength(0);
                                StringBuilder sModifierDetail = new StringBuilder();
                                String tax = "0.00";
                                double dPrice = 0.00;
                                String slno = cursor.getString(cursor.getColumnIndex(DBHelper.COL_SL_NO));
                                String productCode = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PRODUCT_CODE));
                                String productName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PRODUCT_NAME));
                                String model = cursor.getString(cursor.getColumnIndex(DBHelper.COL_MODEL));
                                String qty = cursor.getString(cursor.getColumnIndex(DBHelper.COL_QUANTITY));

                                String productModifier = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IS_PRODUCT_MODIFIER));

                                boolean isProductModifier = Boolean.valueOf(productModifier);
                                if(isProductModifier){
                                    dPrice = cursor.getDouble(cursor
                                            .getColumnIndex(DBHelper.COL_MODIFIER_PRICE));
                                }else{
                                    dPrice = cursor.getDouble(cursor
                                            .getColumnIndex(DBHelper.COL_PRICE));
                                }
                                String price = Utils.twoDecimalPoint(Double.valueOf(dPrice));
                                double dProductTax = cursor.getDouble(cursor
                                        .getColumnIndex(DBHelper.COL_TAX));
                                String productTax = Utils.twoDecimalPoint(Double.valueOf(dProductTax));
                                double dTotal = cursor.getDouble(cursor
                                        .getColumnIndex(DBHelper.COL_TOTAL));
                                String total = Utils.twoDecimalPoint(Double.valueOf(dTotal));

                                Cursor cursor1 = mDBHelper.getProductModifierCursor(slno,productCode);

                                if (cursor1 != null && cursor1.getCount() > 0) {
                                    Log.d("slno",""+slno);
                                    Log.d("productCode",""+productCode);
                                    if (cursor1.moveToFirst()) {
                                        do {
                                          /* String sno = cursor1.getString(cursor1
                                                   .getColumnIndex(DBHelper.COL_SL_NO));*/
                                            String modifierCode = cursor1.getString(cursor1
                                                    .getColumnIndex(DBHelper.COL_MODIFIER_CODE));
                                            String modifierName = cursor1.getString(cursor1
                                                    .getColumnIndex(DBHelper.COL_MODIFIER_NAME));
                                            double dModifierPrice = cursor1.getDouble(cursor1
                                                    .getColumnIndex(DBHelper.COL_PRICE));
                                            String modifierPrice = Utils.twoDecimalPoint(Double.valueOf(dModifierPrice));
                                            sModifierDetail.append(modifierCode).append(COLUMN_SEPARATOR1)
                                                    .append(modifierName).append(COLUMN_SEPARATOR1)
                                                    .append(modifierPrice).append(LINE_SEPARATOR1);
                                        } while (cursor1.moveToNext());
                                    }
                                    sModifierDetail.deleteCharAt(sModifierDetail.lastIndexOf(LINE_SEPARATOR1));
                                    //  sModifierDetail.append(LINE_SEPARATOR);
                                    Log.d("sModifierDetail: ", "-->" + sModifierDetail.toString());
                                }
                                //modifier is not null
                                if (cursor1 != null && cursor1.getCount() > 0)
                                {
                                    // sDetail.append(slno).append(COLUMN_SEPARATOR)

                                    /******** PRODUCTCODE ***********/
                                    sDetail.append(productCode).append(COLUMN_SEPARATOR)

                                            /******** PRODUCTNAME ***********/
                                            .append(productName).append(COLUMN_SEPARATOR)

                                            /******** MODEL ***********/
                                            .append(productName).append(COLUMN_SEPARATOR)

                                            /******** QUANTITY ***********/
                                            .append(qty).append(COLUMN_SEPARATOR)

                                            /******** PRICE ***********/
                                            .append(price).append(COLUMN_SEPARATOR)

                                            /******** TOTAL ***********/
                                            .append(total).append(COLUMN_SEPARATOR)

                                            /******** TAX ***********/
                                            .append(productTax).append(COLUMN_SEPARATOR)

                                            /******** REWARD ***********/
                                            .append(DATA_ZERO)

                                            .append(COLUMN_SEPARATOR)

                                            /******** MODIFIER  ***********/
                                            .append(sModifierDetail)

                                            .append(LINE_SEPARATOR);

                                    Log.d("sDetail: ", "modifier is not null -->" + sDetail.toString());


                                }else{
                                    //modifier is null
                                    // sDetail.append(slno).append(COLUMN_SEPARATOR)
                                    /******** PRODUCTCODE ***********/
                                    sDetail.append(productCode).append(COLUMN_SEPARATOR)

                                            /******** PRODUCTNAME ***********/
                                            .append(productName).append(COLUMN_SEPARATOR)

                                            /******** MODEL ***********/
                                            .append(productName).append(COLUMN_SEPARATOR)

                                            /******** QUANTITY ***********/
                                            .append(qty).append(COLUMN_SEPARATOR)

                                            /******** PRICE ***********/
                                            .append(price).append(COLUMN_SEPARATOR)

                                            /******** TOTAL ***********/
                                            .append(total).append(COLUMN_SEPARATOR)

                                            /******** TAX ***********/
                                            .append(productTax).append(COLUMN_SEPARATOR)

                                            /******** REWARD ***********/
                                            .append(DATA_ZERO)

                                            .append(LINE_SEPARATOR);

                                    Log.d("sDetail: ", "modifier is null -->" + sDetail.toString());
                                }

                            /*   if(sModifierDetail == null && sModifierDetail.toString().equals(""))
                               {
                                   sDetail.append(slno).append(COLUMN_SEPARATOR)

                                           *//******** PRODUCTCODE ***********//*
                                           .append(productCode).append(COLUMN_SEPARATOR)

                                           *//******** PRODUCTNAME ***********//*
                                           .append(productName).append(COLUMN_SEPARATOR)

                                           *//******** MODEL ***********//*
                                           .append(productName).append(COLUMN_SEPARATOR)

                                           *//******** QUANTITY ***********//*
                                           .append(qty).append(COLUMN_SEPARATOR)

                                           *//******** PRICE ***********//*
                                           .append(price).append(COLUMN_SEPARATOR)

                                           *//******** TOTAL ***********//*
                                           .append(total).append(COLUMN_SEPARATOR)

                                           *//******** TAX ***********//*
                                           .append(productTax).append(COLUMN_SEPARATOR)

                                           *//******** REWARD ***********//*
                                           .append(DATA_ZERO)

                                           .append(LINE_SEPARATOR);
                               }else{
                                   sDetail.append(slno).append(COLUMN_SEPARATOR)

                                           *//******** PRODUCTCODE ***********//*
                                           .append(productCode).append(COLUMN_SEPARATOR)

                                           *//******** PRODUCTNAME ***********//*
                                           .append(productName).append(COLUMN_SEPARATOR)

                                           *//******** MODEL ***********//*
                                           .append(productName).append(COLUMN_SEPARATOR)

                                           *//******** QUANTITY ***********//*
                                           .append(qty).append(COLUMN_SEPARATOR)

                                           *//******** PRICE ***********//*
                                           .append(price).append(COLUMN_SEPARATOR)

                                           *//******** TOTAL ***********//*
                                           .append(total).append(COLUMN_SEPARATOR)

                                           *//******** TAX ***********//*
                                           .append(productTax).append(COLUMN_SEPARATOR)

                                           *//******** REWARD ***********//*
                                           .append(DATA_ZERO)

                                           .append(COLUMN_SEPARATOR)

                                           .append(sModifierDetail)

                                           .append(LINE_SEPARATOR);
                               } */

                                slNo++;
                            } while (cursor.moveToNext());

                        }
                        Log.d("sDetail: ", "-->" + sDetail.toString());
                        sDetail.deleteCharAt(sDetail.lastIndexOf(LINE_SEPARATOR));

                        Log.d("sDetail: ", "<-->" + sDetail.toString());
                        client.addParam("products",sDetail.toString());

                        if(coupon_code_cursor.getCount()>0){
                            if(label.equals("Delivery Charges")){
                                sDetailValues.append("sub_total").append(COLUMN_SEPARATOR)
                                        .append("sub-total").append(COLUMN_SEPARATOR)
                                        .append(String.valueOf(subtotal)).append(COLUMN_SEPARATOR)
                                        .append("1").append(LINE_SEPARATOR)

                                        .append("coupon").append(COLUMN_SEPARATOR)
                                        .append("Discount Coupon Code("+ed_promoCode.getText().toString()+")").append(COLUMN_SEPARATOR)
                                        .append(lblDiscount.getText().toString()).append(COLUMN_SEPARATOR)
                                        .append("2").append(LINE_SEPARATOR)

                                        .append("tax").append(COLUMN_SEPARATOR)
                                        .append(name).append(COLUMN_SEPARATOR)
                                        .append(totalTax).append(COLUMN_SEPARATOR)
                                        .append("3").append(LINE_SEPARATOR)

                                        .append("shipping").append(COLUMN_SEPARATOR)
                                        .append(label).append(COLUMN_SEPARATOR)
                                        .append(value).append(COLUMN_SEPARATOR)
                                        .append("5").append(LINE_SEPARATOR)

                                        .append("total").append(COLUMN_SEPARATOR)
                                        .append("Total").append(COLUMN_SEPARATOR)
                                        .append(netTotal).append(COLUMN_SEPARATOR)
                                        .append("9");
                            }
                            // No tax in pickup from store
                            else if(label.equals("Pickup from Store")){
                                sDetailValues.append("sub_total").append(COLUMN_SEPARATOR)
                                        .append("sub-total").append(COLUMN_SEPARATOR)
                                        .append(String.valueOf(subtotal)).append(COLUMN_SEPARATOR)
                                        .append("1").append(LINE_SEPARATOR)

                                        .append("coupon").append(COLUMN_SEPARATOR)
                                        .append("Discount Coupon Code("+ed_promoCode.getText().toString()+")").append(COLUMN_SEPARATOR)
                                        .append(lblDiscount.getText().toString()).append(COLUMN_SEPARATOR)
                                        .append("2").append(LINE_SEPARATOR)

                                        .append("tax").append(COLUMN_SEPARATOR)
                                        .append(name).append(COLUMN_SEPARATOR)
                                        .append(totalTax).append(COLUMN_SEPARATOR)
                                        .append("3").append(LINE_SEPARATOR)

                                        .append("shipping").append(COLUMN_SEPARATOR)
                                        .append(label).append(COLUMN_SEPARATOR)
                                        .append(value).append(COLUMN_SEPARATOR)
                                        .append("5").append(LINE_SEPARATOR)

                                        .append("total").append(COLUMN_SEPARATOR)
                                        .append("Total").append(COLUMN_SEPARATOR)
                                        .append(netTotal).append(COLUMN_SEPARATOR)
                                        .append("9");
                            }
                        }else{
                            if(label.equals("Delivery Charges")){
                                sDetailValues.append("sub_total").append(COLUMN_SEPARATOR)
                                        .append("sub-total").append(COLUMN_SEPARATOR)
                                        .append(String.valueOf(subtotal)).append(COLUMN_SEPARATOR)
                                        .append("1").append(LINE_SEPARATOR)

                                        .append("tax").append(COLUMN_SEPARATOR)
                                        .append(name).append(COLUMN_SEPARATOR)
                                        .append(totalTax).append(COLUMN_SEPARATOR)
                                        .append("3").append(LINE_SEPARATOR)

                                        .append("shipping").append(COLUMN_SEPARATOR)
                                        .append(label).append(COLUMN_SEPARATOR)
                                        .append(value).append(COLUMN_SEPARATOR)
                                        .append("5").append(LINE_SEPARATOR)

                                        .append("" +
                                                "total").append(COLUMN_SEPARATOR)
                                        .append("Total").append(COLUMN_SEPARATOR)
                                        .append(netTotal).append(COLUMN_SEPARATOR)
                                        .append("9");
                            }
                            // No tax in pickup from store
                            else if(label.equals("Pickup from Store")){
                                sDetailValues.append("sub_total").append(COLUMN_SEPARATOR)
                                        .append("sub-total").append(COLUMN_SEPARATOR)
                                        .append(String.valueOf(subtotal)).append(COLUMN_SEPARATOR)
                                        .append("1").append(LINE_SEPARATOR)

                                        .append("tax").append(COLUMN_SEPARATOR)
                                        .append(name).append(COLUMN_SEPARATOR)
                                        .append(totalTax).append(COLUMN_SEPARATOR)
                                        .append("3").append(LINE_SEPARATOR)

                                        .append("shipping").append(COLUMN_SEPARATOR)
                                        .append(label).append(COLUMN_SEPARATOR)
                                        .append(value).append(COLUMN_SEPARATOR)
                                        .append("5").append(LINE_SEPARATOR)

                                        .append("total").append(COLUMN_SEPARATOR)
                                        .append("Total").append(COLUMN_SEPARATOR)
                                        .append(netTotal).append(COLUMN_SEPARATOR)
                                        .append("9");
                            }
                        }

                        // tax in Delivery Charges
                        Log.d("sDetailValues: ", "-->" + sDetailValues.toString());
                        client.addParam("totals",sDetailValues.toString());

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
                        e.printStackTrace();
                        sCode = "ERROR";
                        sDesc = "Error occured while loading data!";
                    }

                    bndle.putString("CODE", sCode);
                    bndle.putString("DESC", sDesc);
                    msg.setData(bndle);
                    ctgPlaceOrderProgressHandler.sendMessage(msg);
                }
            };
            thrdPlaceOrder.start();
        }
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgPlaceOrderProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdPlaceOrder.interrupt();
            } catch (Exception e) {
            }
            String data ="",result="";
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {

                    Log.d("sResponseMsg", "-->" + sResponseMsg);
//                    Spanned sp = Html.fromHtml(sResponseMsg);
//                   String response =  sp.toString().split("\\ ")[0];
//
//                    Pattern p = Pattern.compile("\\{([^}]*)\\}");
//                   Matcher m = p.matcher(response);
//                    String responseValue = "{"+ m.toString()+"}";
//                   Log.d("response", "-->" + responseValue);
//                   JSONObject jsonObject = new JSONObject(sResponseMsg);
//                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
//                         data = jsonObject.getString("data");
//                         Log.d("data", "-->"+ data);
//                    }
//                   if(data!=null && !data.isEmpty()){
                    result = "Order Successfully Placed...";
                    mProgressDialog.setMessage("Order Successfully Placed...");
//                   }
//                   else{
//                       result = "Failed...";
//                       mProgressDialog.setMessage("Failed...");
//                   }
                    initDataLoadCompleted(result);
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                    initDataLoadCompleted(result);
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
            }

        }
    };

    public void initDataLoadCompleted(final String sMsg) {

        ctgPlaceOrderProgressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                if(mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
                if(sMsg!=null && !sMsg.isEmpty()){
                    if(sMsg.equals("Failed...")){
                        Toast.makeText(PaymentMethodActivity.this,"Failed..",Toast.LENGTH_SHORT).show();
                    }else{
                        mDBHelper.deleteAllProduct();
                        mDBHelper.deleteAllProductModifier();
                        successDialog();
                    }
                }
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
}
