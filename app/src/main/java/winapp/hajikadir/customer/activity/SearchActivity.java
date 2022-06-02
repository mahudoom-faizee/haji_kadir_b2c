package winapp.hajikadir.customer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.ProductRecyclerViewAdapter;
import winapp.hajikadir.customer.adapter.SearchAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.InitiateSearch;
import winapp.hajikadir.customer.util.Utils;

/**
 * Created by user on 04-Feb-17.
 */

public class SearchActivity extends AppCompatActivity implements  DialogInterface.OnCancelListener,Constants {
    private InitiateSearch initiateSearch;
    private Toolbar toolbar;
    private View line_divider, toolbar_shadow;
    // private RelativeLayout view_search;
    private CardView card_search;
    private ImageView image_search_back, clearSearch,cart;
    private EditText edit_text_search;
    private ListView listView;
    private SettingsManager mSettings;
    private UIHelper helper;
    private SearchAdapter mAdapter;
    private ArrayList<Product> mSearchList,mSearchProductList;
    //  public GridView mGridView;
    private RecyclerView mRecyclerView;
    private RelativeLayout searchView;
    private ProgressDialog mProgressDialog;
    private GridLayoutManager mGridLayoutManager;
    private Thread mThreadProducts;
    private Intent mIntent;
    private Bundle mBundle;
    private DBHelper mDBHelper;
    private TextView mEmpty;
    private ProductRecyclerViewAdapter mProductRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //View ID
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (RelativeLayout) findViewById(R.id.search_layout);
        toolbar_shadow = findViewById(R.id.toolbar_shadow);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        card_search = (CardView) findViewById(R.id.card_search);
        image_search_back = (ImageView) findViewById(R.id.image_search_back);
        clearSearch = (ImageView) findViewById(R.id.clearSearch);
        listView = (ListView) findViewById(R.id.listView);

        cart = (ImageView) findViewById(R.id.cart);
        // mGridView = (GridView) findViewById(R.id.gridView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEmpty  = (TextView) findViewById(R.id.empty);
        mGridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mProductRecyclerViewAdapter = new ProductRecyclerViewAdapter();
        //Object Initialization
        mSettings = new SettingsManager(SearchActivity.this);
        mDBHelper = new DBHelper(SearchActivity.this);
        helper = new UIHelper(SearchActivity.this);
        initiateSearch = new InitiateSearch();
        mSearchList = new ArrayList<Product>();
        mSearchProductList = new ArrayList<Product>();
        mIntent = new Intent();
        mBundle = new Bundle();
        loadProducts();

        InitiateSearch();
    }
    private void InitiateSearch() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = mAdapter.getItem(position);
                edit_text_search.setText(product.getName());
                listView.setVisibility(View.GONE);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
                toolbar_shadow.setVisibility(View.GONE);
                filterSearchProduct(product.getId(), product.getName());

                edit_text_search.setSelection(edit_text_search.getText().length());
                edit_text_search.requestFocus();

            }
        });
        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = edit_text_search.getText().toString();
                int length = edit_text_search.getText().length();
                try {
                    if (length == 0) {
                        Utils.showViews(true, listView);
                        Utils.showViews(false, mRecyclerView);

                    }
                    mAdapter.filter(text);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent.setClass(SearchActivity.this, CartActivity.class);
                startActivity(mIntent);
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = edit_text_search.getText().length();
                if (length > 0) {
                    edit_text_search.getText().delete(length - 1, length);
                } else if (length == 0) {
                    Utils.showViews(true, listView);
                    Utils.showViews(false, mRecyclerView);

                }
            }
        });

        clearSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text_search.setText("");

                return false;
            }
        });
        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
                finish();
            }
        });
    }
    public void loadProducts(){
        mProgressDialog = ProgressDialog.show(SearchActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mSearchList.clear();
        mThreadProducts = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(SearchActivity.this,FUNC_GET_PRODUCT_VALUES);
                    client.addParam("category", "");
                    client.addParam("subcategory", "");
                    client.addParam("products", "");
                    client.addParam("status", "1");
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                    } else {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading products."+ client.getErrorMessage();
                    }
                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading products!";
                }
                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                progresSearchHandler.sendMessage(msg);
            }
        };
        mThreadProducts.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler progresSearchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                mThreadProducts.interrupt();
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
                        for (int i = 0; i < len; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Product product = new Product();
                            String sId = object.getString("product_id");
                            String sName = object.getString("model");

                            product.setId(sId);
                            product.setName(sName);
                            if(!hasDuplicatesId(sId)) {
                                mSearchList.add(product);
                            }

                        }
                        mProgressDialog.setMessage("Loading...");
                        initDataLoadCompleted("Inital data loaded successfully!");
                        if (mSearchList != null && !mSearchList.isEmpty()) {
                            mAdapter = new SearchAdapter(SearchActivity.this, mSearchList);
                            listView.setAdapter(mAdapter);
                            Utils.showViews(true, listView);
                        } else {
                            Utils.showViews(false, listView);
                        }
                        initiateSearch.handleToolBar(SearchActivity.this, card_search, toolbar,searchView, listView, edit_text_search);
                        edit_text_search.setSelection(edit_text_search.getText().length());
                        edit_text_search.requestFocus();

                        if (mSearchList.size() > 0) {
                            toolbar_shadow.setVisibility(View.GONE);
                            TranslateAnimation slide = new TranslateAnimation(0, 0, mRecyclerView.getHeight(), 0);
                            slide.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            slide.setDuration(300);
                            mRecyclerView.startAnimation(slide);
                            mRecyclerView.setVerticalScrollbarPosition(0);
                            //   mRecyclerView.setSelection(0);


                        } else {
                            toolbar_shadow.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    helper.showErrorDialog(e.getMessage());

                }
            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                helper.showErrorDialog(sResponseMsg);
            }
        }
    };

    public void filterSearchProduct(String productCode,String productName) {
        mProgressDialog = ProgressDialog.show(SearchActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        ArrayList<String> searchlist = new ArrayList<String>();
        searchlist.clear();
        for (Product s : mSearchList) {
            if (s.getName().toLowerCase().contains(productName.toString().toLowerCase())) {
                Log.d("productName", "-->" + s.getName().toString().toLowerCase());
                Log.d("productcode", "-->" + s.getId().toString());
                searchlist.add(s.getId());
            }
        }
        loadSearchProduct(searchlist);
    }
    public void loadSearchProduct(final ArrayList<String> searchlist) {
        mSearchProductList.clear();

        mThreadProducts = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                for (int i = 0; i < searchlist.size(); i++) {
                    try {
                        RestClientAcessTask client = new RestClientAcessTask(SearchActivity.this, FUNC_GET_PRODUCT_VALUES);
                        client.addParam("category", "");
                        client.addParam("subcategory", "");
                        client.addParam("products", searchlist.get(i).toString());
                        client.addParam("status", "1");
                        RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                        if (status == RestClientAcessTask.Status.SUCCESS) {
                            String sResponse = client.getResponse();
                            sCode = "SUCCESS";
                            sDesc = sResponse;
                            JSONObject jsonObject = new JSONObject(sDesc);
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                int len = jsonArray.length();
                                for (int j = 0; j < len; j++) {
                                    JSONObject object = jsonArray.getJSONObject(j);
                                    Product product = new Product();
                                    String sId = object.getString("product_id");
                                    String sTaxClassId = object.getString("tax_class_id");
                                    String sName = object.getString("model");
                                    String sProductName = Html.fromHtml(sName).toString();
                                    String sDescription = object.getString("description");
                                    String sPrice = object.getString("price");
                                    int quantity = object.getInt("quantity");
                                    String sRate = object.getString("rate");
                                    String sImgUrl = object.getString("image");
                                    //String model = "";//object.getString("model");

                                    if(sImgUrl!=null && !sImgUrl.isEmpty()){
                                        String imageUrl =  API_SERVER_URL+"image/"+sImgUrl;
                                        imageUrl = imageUrl.replaceAll(" ", "%20");
                                        product.setImage(imageUrl);
                                    }else{
                                        product.setImage(null);
                                    }

                                    product.setId(sId);
                                    if(sTaxClassId!=null && !sTaxClassId.isEmpty()){
                                        product.setTaxClassId(sTaxClassId);
                                    }else{
                                        product.setTaxClassId("0");
                                    }
                                    product.setName(sProductName);
                                    product.setPrice(sPrice);
                                    product.setModifierPrice("0");
                                    product.setDescription(sDescription);
                                    product.setQuantity(quantity);
                                    product.setQty("1");
                                    product.setRate(sRate);
                                    product.setModel(sName);
                                   // boolean isModifier = mDBHelper.isModifier(sId);
                                   // product.setProductModifier(isModifier);
                                    if(!hasDuplicates(sId)) {
                                        mSearchProductList.add(product);
                                    }
                                }

                            }
                        } else {
                            sCode = "ERROR";
                            sDesc = "Error occured while loading products." + client.getErrorMessage();
                        }
                    } catch (Exception e) {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading products!";
                    }
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                progressHandler.sendMessage(msg);
            }
        };
        mThreadProducts.start();

    }

    /**
     * Handling the message while progressing
     */
    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                mThreadProducts.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");
            if (sResponseCode.equals("SUCCESS")) {
                try{
                    mProgressDialog.setMessage("Loading...");
                    initDataLoadCompleted("Inital data loaded successfully!");
                    mLoadProduct();

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    helper.showErrorDialog(e.getMessage());

                }
            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
            }
        }
    };
    public void initDataLoadCompleted(String sMsg) {

        progressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mProgressDialog.dismiss();
                if(mSearchProductList.size()>0){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmpty.setVisibility(View.GONE);
                }else{
                    mRecyclerView.setVisibility(View.GONE);
                    mEmpty.setVisibility(View.VISIBLE);
                }

            }
        }, 1000);

    }
    private boolean hasDuplicatesId(String productId){
        for(Product product : mSearchList){
            if(productId.equals(product.getId())){
                return true;
            }
        }
        // No duplicates found.
        return  false;
    }
    private boolean hasDuplicates(String productId){
        for(Product product : mSearchProductList){
            if(productId.contains(product.getId())){
                return true;
            }
        }
        // No duplicates found.
        return  false;
    }
    private void mLoadProduct(){
        Log.d("length","-->"+mSearchProductList.size());
        if (mSearchProductList != null && !mSearchProductList.isEmpty()) {
            mProductRecyclerViewAdapter = new ProductRecyclerViewAdapter(SearchActivity.this,mSearchProductList, new ProductRecyclerViewAdapter.OnItemClickListener(){
                @Override
                public void onItemClick(int position) {
                    Product selectedProduct = mProductRecyclerViewAdapter.getItem(position);
                    mIntent.setClass(SearchActivity.this, ProductDetailActivity.class);
                    mBundle.putSerializable(StringUtils.EXTRA_PRODUCT,selectedProduct);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                }
            });
            mRecyclerView.setAdapter(mProductRecyclerViewAdapter);
            mProductRecyclerViewAdapter.notifyDataSetChanged();
            Utils.showViews(true, mRecyclerView);
        } else {

            Utils.showViews(false, mRecyclerView);
        }

    }
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }


    public void onResume(){
        super.onResume();
        mProductRecyclerViewAdapter.notifyDataSetChanged();
    }
}
