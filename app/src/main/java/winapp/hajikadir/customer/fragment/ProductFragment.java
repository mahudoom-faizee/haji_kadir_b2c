package winapp.hajikadir.customer.fragment;

/**
 * Created by user on 14-Jul-16.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.activity.LoginActivity;
import winapp.hajikadir.customer.activity.ProductDetailActivity;
import winapp.hajikadir.customer.adapter.ProductRecyclerViewAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Parent;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.BitmapUtils;
import winapp.hajikadir.customer.util.Constants;

public class ProductFragment extends Fragment implements DialogInterface.OnCancelListener,Constants{

    private Thread mThreadProducts;
    private UIHelper mUIHelper;
    private ArrayList<Product> mProductArr;
    private ProductRecyclerViewAdapter mProductRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private Intent mIntent;
    private Bundle mBundle;
    private GridLayoutManager mGridLayoutManager;
    private ProgressDialog mProgressDialog;
    private SessionManager mSession;
    private TextView mEmpty;
    private DBHelper mDBHelper;
    private String session_location,hq_location;
   public ProductFragment() {
       // Required empty public constructor
   }
    @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
   }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Parent.setIsLoading(true);
    }
    @Override
    public void onResume() {
        View view = getView();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mEmpty  = (TextView) view.findViewById(R.id.empty);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mIntent = new Intent();
        mBundle = new Bundle();
        mProductArr = new ArrayList<>();
        mDBHelper = new DBHelper(getActivity());
        mSession = new SessionManager(getActivity());
        mUIHelper = new UIHelper(getActivity());
        if(Parent.isLoading()){
           mGetProducts();
        }
        super.onResume();
    }
    private void mGetProducts() {
        Parent.setIsLoading(false);
        mProgressDialog = ProgressDialog.show(getActivity(),"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mProductArr.clear();
        mThreadProducts = new Thread() {
            @Override
            public void run() {
                String category = Parent.getCategoryId();
                String subCategory = Parent.getSubCategoryId();
                Log.d("getCategoryId","-->"+category);
                Log.d("getSubCategoryId","-->"+subCategory);
                String branch_id= mSession.getBranchId("PREF_BRANCH_ID",null);

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(getActivity(),FUNC_GET_PRODUCT_VALUES);
                    client.addParam("category", category);
                    client.addParam("subcategory", subCategory);
                    client.addParam("products", "");
                    client.addParam("status", "1");
                    client.addParam("branchid",branch_id);

                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                       /* try {
                            JSONObject jsonObject = new JSONObject(sResponse);
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                int len = jsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Product product = new Product();
                                    String sId = object.getString("product_id");
                                    String sTaxClassId = object.getString("tax_class_id");
                                    String sName = object.getString("model");
                                    String sProductName = Html.fromHtml(sName).toString();
                                    String sDescr = object.getString("description");
                                    String sPrice = object.getString("price");
                                    String sImgUrl = object.getString("image");
                                    String sRate = object.getString("rate");
                                    String sSortOrder = object.getString("sort_order");
                                    int sortOrder = 0;
                                    if(sSortOrder!=null && !sSortOrder.isEmpty()){
                                        sortOrder = Integer.valueOf(sSortOrder);
                                        product.setSortOrder(sortOrder);
                                    }else{
                                        product.setSortOrder(sortOrder);
                                    }
                                    int quantity = object.getInt("quantity");
                                    if(sImgUrl!=null && !sImgUrl.isEmpty()){
                               *//* sImgUrl = sImgUrl.replaceAll(" ", "%20");
                                Log.d("sImgUrl","-->"+sImgUrl);*//*
                                        String imageUrl =  API_SERVER_URL+"image/"+sImgUrl;
                                        imageUrl = imageUrl.replaceAll(" ", "%20");
                                        Bitmap image = BitmapUtils.loadBitmap(imageUrl);
                                        //   image = getResizedBitmap(image,400);
                                        product.setBitmap(image);
                                        product.setImage(imageUrl);
                                    }else{
                                        product.setImage(null);
                                    }
                                    product.setId(sId);
                                    Log.d("sortOrder","-->"+product.getSortOrder());
                                    if(sTaxClassId!=null && !sTaxClassId.isEmpty()){
                                        product.setTaxClassId(sTaxClassId);
                                    }else{
                                        product.setTaxClassId("0");
                                    }
                                    product.setName(sProductName);
                                    product.setPrice(sPrice);
                                    product.setModifierPrice("0");
                                    product.setQty("1");
                                    product.setDescription(sDescr);
                                    product.setQuantity(quantity);
                                    product.setRate(sRate);
                            *//*boolean isModifier = mDBHelper.isModifier(sId);
                            product.setProductModifier(isModifier);*//*
                                    if(!hasDuplicates(sId)){
                                        mProductArr.add(product);
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            mProgressDialog.dismiss();
                            mUIHelper.showErrorDialog(e.getMessage());

                        }*/
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

                mProgressDialog.setMessage("Loading...");
               /* initDataLoadCompleted("Inital data loaded successfully!");
                mLoadProduct();
                //Sort By Name
                Collections.sort(mProductArr, new Comparator<Product>(){
                    public int compare(Product o1, Product o2)
                    {
                        return (o1.getName().compareTo(o2.getName()));
                    }
                });
                //Sort By SortOrder
                Collections.sort(mProductArr, new Comparator<Product>(){
                    public int compare(Product o1, Product o2)
                    {
                        return (o1.getSortOrder()- (o2.getSortOrder()));
                    }
                });*/
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        for (int i = 0; i < len; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Product product = new Product();
                            String sId = object.getString("product_id");
                            String sTaxClassId = object.getString("tax_class_id");
                            String sName = object.getString("model");
                            String sProductName = Html.fromHtml(sName).toString();
                            String sDesc = object.getString("description");
                            String sPrice = object.getString("price");
                            String sImgUrl = object.getString("image");
                            String sRate = object.getString("rate");
                            String sSortOrder = object.getString("sort_order");
                            int sortOrder = 0;
                            if(sSortOrder!=null && !sSortOrder.isEmpty()){
                                sortOrder = Integer.valueOf(sSortOrder);
                                product.setSortOrder(sortOrder);
                            }else{
                                product.setSortOrder(sortOrder);
                            }
                            int quantity = object.getInt("quantity");
                            if(sImgUrl!=null && !sImgUrl.isEmpty()){
                               /* sImgUrl = sImgUrl.replaceAll(" ", "%20");
                                Log.d("sImgUrl","-->"+sImgUrl);*/
                                String imageUrl =  API_SERVER_URL+"image/"+sImgUrl;
                                imageUrl = imageUrl.replaceAll(" ", "%20");
                                //* URL url = new URL(imageUrl);
                                // Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                // image = getResizedBitmap(image,500);
                                // product.setBitmap(image);*//*
                                product.setImage(imageUrl);
                            }else{
                                product.setImage(null);
                            }
                            product.setId(sId);
                            Log.d("sortOrder","-->"+product.getSortOrder());
                            if(sTaxClassId!=null && !sTaxClassId.isEmpty()){
                                product.setTaxClassId(sTaxClassId);
                            }else{
                                product.setTaxClassId("0");
                            }
                            product.setName(sProductName);
                            product.setPrice(sPrice);
                            product.setModifierPrice("0");
                            product.setQty("1");
                            product.setDescription(sDesc);
                            product.setQuantity(quantity);
                            product.setRate(sRate);
                            /*boolean isModifier = mDBHelper.isModifier(sId);
                            product.setProductModifier(isModifier);*/
                            if(!hasDuplicates(sId)){
                                mProductArr.add(product);
                            }
                        }
                        mProgressDialog.setMessage("Loading...");
                        initDataLoadCompleted("Inital data loaded successfully!");
                        mLoadProduct();
                        //Sort By Name
                        Collections.sort(mProductArr, new Comparator<Product>(){
                            public int compare(Product o1, Product o2)
                            {
                             return (o1.getName().compareTo(o2.getName()));
                            }
                        });
                        //Sort By SortOrder
                        Collections.sort(mProductArr, new Comparator<Product>(){
                            public int compare(Product o1, Product o2)
                            {
                             return (o1.getSortOrder()- (o2.getSortOrder()));
                            }
                        });
                    }
                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());

                }
            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                mUIHelper.showErrorDialog(sResponseMsg);
            }
        }
    };
    public void initDataLoadCompleted(String sMsg) {
        session_location = mSession.getBranchDisplayName();
        hq_location="Tampines";
        Log.v("BranchName", "--->" + session_location);
        progressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mProgressDialog.dismiss();
                if(mProductArr.size()>0){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmpty.setVisibility(View.GONE);
                }else{
                    mRecyclerView.setVisibility(View.GONE);

                    String sessLocation = session_location.replaceAll("\\s","");
                    String hqLocation = hq_location.replaceAll("\\s", "");
                    Log.v("session_location", "--->" + sessLocation);
                    Log.v("hq_Location", "--->" + hqLocation);
                    if(!sessLocation.equalsIgnoreCase(hqLocation)){
                     //   Toast.makeText(getActivity(),"This category item is available at Tampines",Toast.LENGTH_LONG).show();
                        noProductAlert();
                    }else {
                        mEmpty.setVisibility(View.VISIBLE);
                    }
                }

            }
        }, 1000);

    }

    public void noProductAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("This category item is available at Tampines Branch");
        alertDialog.setIcon(R.mipmap.ic_action_term_condition);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private boolean hasDuplicates(String productId){
        for(Product product : mProductArr){
            if(productId.contains(product.getId())){
                 return true;
                }
        }
        // No duplicates found.
        return  false;
    }
    private void mLoadProduct(){

        mProductRecyclerViewAdapter = new ProductRecyclerViewAdapter(getActivity(),mProductArr, new ProductRecyclerViewAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> userDetails = mSession.getUserDetails();
                final String userId = userDetails.get(PREF_SESSION_USER_ID);
                final String password = userDetails.get(PREF_SESSION_PASSWORD);
                if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                    Product selectedProduct = mProductRecyclerViewAdapter.getItem(position);
                    mIntent.setClass(getActivity(), ProductDetailActivity.class);
                    mBundle.putSerializable(StringUtils.EXTRA_PRODUCT,selectedProduct);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                }else{
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }


            }
        });
        mRecyclerView.setAdapter(mProductRecyclerViewAdapter);
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
}