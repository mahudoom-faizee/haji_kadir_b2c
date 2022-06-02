package winapp.hajikadir.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.OrderAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Order;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.RecyclerItemClickListener;
import winapp.hajikadir.customer.util.Utils;

public class MyOrderActivity extends AppCompatActivity implements DialogInterface.OnCancelListener,Constants {
    private ProgressDialog mProgressDialog;
    private Animation mAnimFadeIn;
    private Thread thrdGetOrder;
    private UIHelper mUIHelper;
    private ArrayList<Order> mOrderArrList,mOrderArrDetailList;
    private RecyclerView mRecyclerView;
    private LinearLayout mSearchLinearLayout;
    private Intent mIntent;
    private TextView mEmpty,mlblTitle,mlblCartNoOfItem,mlblFromDate,mlblToDate;
    private boolean searchFlag = true;
    private SessionManager mSession;
    private OrderAdapter mOrderAdapter;
    private String userId ="",mDateFlag="",currentDate="";
    private ActionBar mActionBar;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    private Bundle state;
    private SimpleDateFormat formatter;
    private Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        state = savedInstanceState;
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);
        mlblTitle.setText(getResources().getString(R.string.title_activity_my_order));
        mlblCartNoOfItem.setVisibility(View.GONE);
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);

        formatter = new SimpleDateFormat("yyyy-MM-dd");

        mUIHelper = new UIHelper(MyOrderActivity.this);
        mOrderArrList = new ArrayList<>();
        mOrderArrDetailList = new ArrayList<>();
        caldroidFragment = new CaldroidFragment();
        mSession = new SessionManager(MyOrderActivity.this);
        mIntent = new Intent();
        mEmpty = (TextView) findViewById(R.id.empty);

        mlblFromDate  = (TextView) findViewById(R.id.fromDate);
        mlblToDate  = (TextView) findViewById(R.id.toDate);

        mSearchLinearLayout = (LinearLayout) findViewById(R.id.search_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        HashMap<String, String> userDetails = mSession.getUserDetails();
        userId = userDetails.get(PREF_SESSION_USER_ID);

        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager3);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        if (mSearchLinearLayout.getVisibility() == View.VISIBLE) {
            Utils.showViews(false, mSearchLinearLayout);
        }

        cal = Calendar.getInstance();
        currentDate = formatter.format(cal.getTime());
        mlblFromDate.setText(currentDate);
        mlblToDate.setText(currentDate);

        ((ImageView)findViewById(R.id.bottom_myorder_img)).setImageResource(R.mipmap.ic_action_myorder_active);
        ((TextView)findViewById(R.id.bottom_myorder_txt)).setTextColor(getResources().getColor(android.R.color.white));


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                       try {
                          Order mOrder = mOrderAdapter.getItem(position);
                           String orderNo = mOrder.getNo();
                          //  Toast.makeText(MyOrderActivity.this,orderNo,Toast.LENGTH_SHORT).show();
                            mIntent.setClass(MyOrderActivity.this, MyOrderDetailActivity.class);
                            mIntent.putExtra(StringUtils.EXTRA_ORDER_ID,orderNo);
                            startActivity(mIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
//                Toast.makeText(getApplicationContext(), formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
                if(mDateFlag.equals("fromdate")){
                    mlblFromDate.setText(formatter.format(date));
                }else if(mDateFlag.equals("todate")){
                    mlblToDate.setText(formatter.format(date));
                }
                dialogCaldroidFragment.dismiss();
            }

            @Override
            public void onChangeMonth(int month, int year) {
//                String text = "month: " + month + " year: " + year;
//                Toast.makeText(getApplicationContext(), text,
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
//                Toast.makeText(getApplicationContext(),
//                        "Long click " + formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
//                if (caldroidFragment.getLeftArrowButton() != null) {
//                    Toast.makeText(getApplicationContext(),
//                            "Caldroid view is created", Toast.LENGTH_SHORT)
//                            .show();
//                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
        mlblFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateFlag = "fromdate";
                customDate(listener);
            }
        });

        mlblToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateFlag = "todate";
                customDate(listener);
            }
        });
        findViewById(R.id.filterSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetOrder();
            }
        });
        findViewById(R.id.home_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(MyOrderActivity.this, HomeActivity.class);               ;
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.product_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(MyOrderActivity.this, ProductActivity.class);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
                mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.myaccount_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(MyOrderActivity.this, MyAccountActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.registration_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(MyOrderActivity.this, RegistrationActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        mGetOrder();

    }
    private void customDate(CaldroidListener listener){
        // Setup caldroid to use as dialog
        dialogCaldroidFragment = new CaldroidFragment();
        dialogCaldroidFragment.setCaldroidListener(listener);

        // If activity is recovered from rotation
        final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
        if (state != null) {
            dialogCaldroidFragment.restoreDialogStatesFromKey(
                    getSupportFragmentManager(), state,
                    "DIALOG_CALDROID_SAVED_STATE", dialogTag);
            Bundle args = dialogCaldroidFragment.getArguments();
            if (args == null) {
                args = new Bundle();
                dialogCaldroidFragment.setArguments(args);
            }
        } else {
            // Setup arguments
            Bundle bundle = new Bundle();
            // Setup dialogTitle
            dialogCaldroidFragment.setArguments(bundle);
        }

        dialogCaldroidFragment.show(getSupportFragmentManager(),
                dialogTag);
    }

    private void mGetOrder(){

        mOrderArrList.clear();
        mProgressDialog = ProgressDialog.show(MyOrderActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);

        thrdGetOrder = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(MyOrderActivity.this, FUNC_GET_ORDER);

                    client.addParam("fdate", mlblFromDate.getText().toString());
                    client.addParam("tdate", mlblToDate.getText().toString());
                    client.addParam("orderid","");
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

            Log.d("sResponseMsg","-->"+sResponseMsg);

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);

                    JSONObject parent = (JSONObject) jsonObject.get("parent");

                    JSONArray child1 = (JSONArray) parent.get("child1");
                    Log.d("child1","-->"+child1);
                    JSONArray child3 = (JSONArray) parent.get("child3");
                    int child1Length = child1.length();
                    int child3Length = child3.length();
                    if(child1Length>0) {
                        Log.d("jsonArrayChild1", "-->" + child1);
                        for (int i = 0; i < child1Length; i++) {
                            Order order = new Order();
                            JSONObject  childObject1 = child1.getJSONObject(i);
                            String orderStatus = childObject1.getString("order_status");
                            String orderNo = childObject1.getString("order_id");
                            String email = childObject1.getString("email");
                            String date = childObject1.getString("date_modified");
                           // String total = childObject1.getString("total");
                            order.setNo(orderNo);
                            order.setDate(date);
                            order.setStatus(orderStatus);
                            order.setTotal("");
                           if(email.equals(userId)){
                                mOrderArrList.add(order);
                           }

                        }
                    }
                    if(child3Length>0) {
                        Log.d("jsonArray child3", "-->" + child3);
                        for (int i = 0; i < child3Length; i++) {
                            Order order = new Order();
                            JSONObject childObject3 = child3.getJSONObject(i);
                            String orderNo = childObject3.getString("order_id");
                            String title= childObject3.getString("title");
                            String netTotal= childObject3.getString("value");
                            order.setNo(orderNo);
                            order.setTotal(netTotal);
                            if(title.equalsIgnoreCase("Total")){
                                mOrderArrDetailList.add(order);
                            }

                        }


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
        if (mOrderArrList != null && !mOrderArrList.isEmpty() && mOrderArrDetailList!=null && !mOrderArrDetailList.isEmpty()) {

            for(Order order1 : mOrderArrDetailList){
                String orderNo1 = order1.getNo();
                String orderNo1Total = order1.getTotal();
                for(Order order2 : mOrderArrList){
                    String orderNo2 = order2.getNo();
                    String orderNo2Date = order2.getDate();
                    if(orderNo1.equals(orderNo2)){
                        order2.setTotal(orderNo1Total);
                    }
                }
            }
            //Sorting using Anonymous inner class type
            Collections.sort(mOrderArrList, new Comparator<Order>() {
                        @Override
                        public int compare(Order o1, Order o2) {
                            String date1 = ((Order) o1).getDate();
                            String date2 = ((Order) o2).getDate();

                            // ascending order
                          //  return date1.compareTo(date2);

                            // descending order
                            return date2.compareTo(date1);

                        }
                    });
            mOrderAdapter = new OrderAdapter(MyOrderActivity.this, mOrderArrList);
            mRecyclerView.setAdapter(mOrderAdapter);
            Utils.showViews(false, mEmpty);
            Utils.showViews(true, mRecyclerView);
        } else {
            Utils.showViews(true, mEmpty);
            Utils.showViews(false, mRecyclerView);

        }
        mProgressDialog.dismiss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.Search).setVisible(true);


        menu.findItem(R.id.MyOrders).setVisible(false);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
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

            case R.id.Search:
               if (searchFlag) {
                    Utils.showViews(true, mSearchLinearLayout);
                    searchFlag = false;
                } else {
                    Utils.showViews(false, mSearchLinearLayout);
                    searchFlag = true;
                }
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
