package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.activity.CartActivity;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;
import winapp.hajikadir.customer.view.ExpandableHeightGridView;

/**
 * Created by user on 19-Jul-16.
 */
public class CartAdapter extends ArrayAdapter<Product> implements DialogInterface.OnCancelListener,Constants {
    private Double orderTotal = 0.00,orderSubTotal=0.00,orderTax = 0.00;
    private ArrayList<Product> productList;
    private Activity activity;
    private TextView mlblGrandTotal,mlblCartNoOfItem,mlblSubTotal,mlblServiceCharge;
   // private ImageLoader mImageLoader;
    private DBHelper mDBHelper;
    private CartActivity mCartActivity;
    private ModifierAdapter mModifierAdapter;
    private Thread thrdGetModifier;
    private ProgressDialog mProgressDialog;
    private UIHelper helper;
    private ArrayList<Modifier> mModiferArr;
    public AlertDialog mAlertDialog=null;
    private UrlImageLoader mUrlImageLoader;
    private int qtyFlag = 0;
    public CartAdapter(Activity activity, int textViewResourceId,
                       ArrayList<Product> productArrList, TextView mlblCartNoOfItem, TextView mlblSubTotal, TextView mlblServiceCharge, TextView mlblGrandTotal) {
        super(activity, textViewResourceId, productArrList);
        this.activity = activity;
        this.mCartActivity = new CartActivity();
        this.mDBHelper = new DBHelper(activity);
        this.helper = new UIHelper(activity);
       // this.mImageLoader = HajikadirApp.getImageLoader();
        this.mUrlImageLoader = new UrlImageLoader(activity);
        this.mlblServiceCharge = mlblServiceCharge;
        this.mlblGrandTotal = mlblGrandTotal;
        this.mlblSubTotal = mlblSubTotal;
        this.mlblCartNoOfItem = mlblCartNoOfItem;
        productList = new ArrayList<Product>();
        mModiferArr = new ArrayList<>();
        productList= productArrList;
    }



    public class ViewHolder {
        private TextView mNameTxtV,mPriceTxtV,mTotalTxtV;
        private EditText mQtyEdt;
        private ImageView mDeleteImgV,mProductImgV,mMinusImgV,mPlusImgV,mDeleteModifierImgV,mEditModifierImgV;
        private ExpandableHeightGridView mExpandableHeightGridView;
        private LinearLayout mModifierLayout;
    }
    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        Product product = getItem(position);
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_list_cart, null);
            holder.mNameTxtV = (TextView) view.findViewById(R.id.description);
            holder.mPriceTxtV = (TextView) view.findViewById(R.id.price);
            holder.mTotalTxtV = (TextView) view.findViewById(R.id.total);
            holder.mQtyEdt = (EditText) view.findViewById(R.id.quantity);
            holder.mProductImgV = (ImageView) view.findViewById(R.id.productImage);
            holder.mDeleteImgV = (ImageView) view.findViewById(R.id.delete);
            holder.mPlusImgV = (ImageView) view.findViewById(R.id.plus);
            holder.mMinusImgV = (ImageView) view.findViewById(R.id.minus);
            holder.mExpandableHeightGridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
            holder.mModifierLayout = (LinearLayout) view.findViewById(R.id.modifier_layout);
            holder.mDeleteModifierImgV = (ImageView) view.findViewById(R.id.deleteModifier);
            holder.mEditModifierImgV = (ImageView) view.findViewById(R.id.editModifier);
            holder.mQtyEdt.addTextChangedListener(new QtyTextWatcher(view));
            view.setTag(holder);
            view.setId(position);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        String sno = product.getSno();
        ArrayList<Modifier> modifierList = mDBHelper.getProductModifier(sno);
        Log.d("modifierList","--"+modifierList.size());
        Double dPrice = 0.00;
        if(modifierList.size()>0){
            mModifierAdapter = new ModifierAdapter(activity,R.layout.item_cart_modifier,modifierList);
            holder.mExpandableHeightGridView.setAdapter(mModifierAdapter);
            holder.mModifierLayout.setVisibility(View.VISIBLE);
            holder.mExpandableHeightGridView.setExpanded(true);
            dPrice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
        }else{
            holder.mModifierLayout.setVisibility(View.GONE);
            dPrice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());

        }
        String imageUrl =  product.getImage();
        if(imageUrl!=null && !imageUrl.isEmpty()){
            mUrlImageLoader.displayImage(imageUrl, holder.mProductImgV);
        }else{
            holder.mProductImgV.setImageResource(R.mipmap.no_img_available);
        }
       // mImageLoader.displayImage(product.getImage(), holder.mProductImgV);
        holder.mQtyEdt.setTag(product);
        if(product.getQuantity() != 0){
            holder.mQtyEdt.setText(String.valueOf(product.getQuantity()));
        }
        else {
            holder.mQtyEdt.setText("");
        }

        holder.mNameTxtV .setText(product.getName());

        holder.mPriceTxtV.setText("$" + Utils.twoDecimalPoint(dPrice));
        if(product.getQuantity() != 0){
            holder.mTotalTxtV.setText("$" + Utils.twoDecimalPoint(product.getTotal()));
        }
        else {
            holder.mTotalTxtV.setText("");
        }

        orderSubTotal = mDBHelper.getTotal();

        orderTax = mDBHelper.getServiceTaxTotal();

        orderTotal = orderSubTotal+orderTax;

        mlblServiceCharge.setText(Utils.twoDecimalPoint(orderTax));
        mlblSubTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
        mlblGrandTotal.setText(Utils.twoDecimalPoint(orderSubTotal));

        holder.mDeleteModifierImgV.setId(position);
        holder.mDeleteModifierImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = view.getId();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                removeModifier(position);
                                dialog.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setMessage("Are you sure, Do you want to delete the modifier ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        holder.mEditModifierImgV.setId(position);
        holder.mEditModifierImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = view.getId();
                final Product product = productList.get(position);

                mGetModifier(product);
            }
        });

        holder.mDeleteImgV.setId(position);
        holder.mDeleteImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = view.getId();
                final Product product = productList.get(position);
                final String productId = product.getId();
                final String productSno = product.getSno();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                remove(productId,productSno);
                                dialog.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        holder.mPlusImgV.setId(position);
        holder.mPlusImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyPlusAction(view);

            }
        });
        holder.mMinusImgV.setId(position);
        holder.mMinusImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyMinusAction(view);

            }
        });
        return view;

    }
    public void removeModifier(final int position){
        try{
             Product product = productList.get(position);
             String productId = product.getId();
             String productSno = product.getSno();
             String productPrice = product.getPrice();
             int productQty = product.getQuantity();

            double  dprice = productPrice.equals("") ? 0:Double.valueOf(productPrice);
            Double mProductQty = new Double(productQty);
            double dTotal = mProductQty * dprice;
            String tot = Utils.twoDecimalPoint(dTotal);
            product.setTotal(Double.valueOf(tot));
            product.setProductModifier(false);
            mDBHelper.deleteProductModifier(productId,productSno);
            mDBHelper.updateTotal(productSno,productId,tot);
            mDBHelper.updateModifierStatus(productSno,productId);
            orderSubTotal = mDBHelper.getTotal();
            orderTax = mDBHelper.getServiceTaxTotal();
            orderTotal = orderSubTotal+orderTax;
            mlblServiceCharge.setText(Utils.twoDecimalPoint(orderTax));
            mlblSubTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
            mlblGrandTotal.setText(Utils.twoDecimalPoint(orderSubTotal));


            notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void remove(String productId,String productSNO){
        try{
            if(productList.size()>0){
                Iterator<Product> i = productList.iterator();
                while (i.hasNext()) {
                    Product mProduct= i.next();
                    // must be called before you can call i.remove()
                    if(productId.equals(mProduct.getId()) && productSNO.equals(mProduct.getSno()) ){
                        i.remove();
                        notifyDataSetChanged();
                    }
                }
                mDBHelper.deleteProductModifier(productId,productSNO);
                mDBHelper.deleteProduct(productId,productSNO);
                Cursor cursor = mDBHelper.getProductCursor();
                mlblCartNoOfItem.setText(""+cursor.getCount());
                orderSubTotal = mDBHelper.getTotal();
                orderTax = mDBHelper.getServiceTaxTotal();
                orderTotal = orderSubTotal+orderTax;
                mlblServiceCharge.setText(Utils.twoDecimalPoint(orderTax));
                mlblSubTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
                mlblGrandTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void qtyPlusAction(View v) {
        try {
            double dprice = 0.00;
            int position = v.getId();
            Product product = getItem(position);
            boolean isProductModifier = product.isProductModifier();
            int qtyStock = product.getBalanceQty();
            Log.d("Balance Qty", "-->" + qtyStock);
            Log.d("qtyFlag", "-->" + qtyFlag);
            qtyFlag = product.getQuantity();
          //  qtyFlag = Integer.valueOf(qty);
         //   if (qtyStock > qtyFlag) {
                if (product.getQuantity() >0) {
                    qtyFlag = product.getQuantity();
                    Log.d("qtyflag", "!null-->" + qtyFlag);
                } else {
                    qtyFlag = 0;
                    Log.d("qtyflag", "0-->" + qtyFlag);
                }
                // int qty = ++qtyFlag;
                //  Log.d("qty","-->"+ qty);
                product.setQuantity(++qtyFlag);
                Log.d("qtyflag", "-->" + qtyFlag);

            if(isProductModifier){
                dprice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
            }else{
                dprice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());
            }
            //  Double dquantity = new Double(qty);
            //5Double price = new Double(price);
                Double total = qtyFlag * dprice;
                String tot = Utils.twoDecimalPoint(total);
                product.setTotal(Double.valueOf(tot));
                notifyDataSetChanged();
                totalCalc(product);
          /* } else {
                Toast.makeText(context, "Only "+qtyStock +" quantity of "+ product.getName() +" is available", Toast.LENGTH_LONG).show();

            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void qtyMinusAction(View v) {
        try {
            double dprice = 0.00;
            int position = v.getId();
            Product product = getItem(position);
            boolean isProductModifier = product.isProductModifier();
            if (product.getQuantity()>0) {
                qtyFlag = product.getQuantity();
                if (qtyFlag > 1) {
                    product.setQuantity(--qtyFlag);
                } else {
                    product.setQuantity(1);
                }
            }
            int quantity =  product.getQuantity();
            if(isProductModifier){
                dprice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
            }else{
                dprice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());
            }
            Double total = quantity * dprice;
            String tot = Utils.twoDecimalPoint(total);
            product.setTotal(Double.valueOf(tot));

            notifyDataSetChanged();
            totalCalc(product);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private class QtyTextWatcher implements TextWatcher {

        private View view;
        private QtyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //do nothing
        }
        public void afterTextChanged(Editable s) {

            // DecimalFormat df = new DecimalFormat("#.##");
            double dprice=0.00;
            String qtyString = s.toString().trim();
            if(qtyString!=null && !qtyString.isEmpty()){
                int quantity = qtyString.equals("") ? 0 : Integer.valueOf(qtyString);

                EditText qtyView = (EditText) view.findViewById(R.id.quantity);
                Product product = (Product) qtyView.getTag();

                if(product.getQuantity() != quantity){

                    int balanceQty = product.getBalanceQty();
                    Log.d("balanceQty",""+balanceQty);
                    Log.d("quantity",""+quantity);

                    boolean isProductModifier = product.isProductModifier();
                    if(isProductModifier){
                        dprice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
                    }else{
                        dprice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());
                    }
             //   if(balanceQty>=quantity) {

                        //  Double currPrice = product.getTotal();
                     //   Double price = product.getPrice().equals("") ? 0 : Double.valueOf(product.getPrice());
                        Double dTotal = quantity * dprice;
                        //  Double priceDiff = Double.valueOf(df.format(extPrice - currPrice));

                        product.setQuantity(quantity);
                        product.setTotal(dTotal);

                        TextView total = (TextView) view.findViewById(R.id.total);
                        if (product.getQuantity() != 0) {
                            total.setText("$" + Utils.twoDecimalPoint(product.getTotal()));
                        } else {
                            total.setText("");
                        }

                        if (product.getQuantity() != 0) {
                            qtyView.setText(String.valueOf(product.getQuantity()));
                        } else {
                            qtyView.setText("");
                        }
                        qtyView.setSelection(qtyView.getText().length());
                        qtyView.requestFocus();
                        qtyView.setInputType(InputType.TYPE_CLASS_NUMBER);
                        // orderTotal += priceDiff;
                        totalCalc(product);
                     //   orderTotal = mDBHelper.getTotal();
                    //    mlblGrandTotal.setText(Utils.twoDecimalPoint(orderTotal));
                 /*  }
                    else{
                        qtyView.setText("");
                        Toast.makeText(context,"Only "+balanceQty +" quantity of "+ product.getName() +" is available",Toast.LENGTH_LONG).show();

                    }*/

                }

                return;
            }else{
                EditText qtyView = (EditText) view.findViewById(R.id.quantity);
                Product product = (Product) qtyView.getTag();
                TextView total = (TextView) view.findViewById(R.id.total);
                total.setText("0.00");
                product.setQuantity(0);
                product.setTotal(0.00);
                totalCalc(product);
            }

        }
    }
    public void totalCalc(Product product){
        HashMap<String, String> productvalues = new HashMap<String, String>();
        try {
            double dNetTotal = 0.00,dprice=0.00;
           // int slNo = mDBHelper.getCount();
            String rate  = product.getRate();
            String tax_class_id  = product.getTaxClassId();
            String price = product.getPrice();
            int quantity = product.getQuantity();
            String qty = String.valueOf(quantity);
            boolean isProductModifier = product.isProductModifier();
            if(isProductModifier){
                dprice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
            }else{
                dprice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());
            }
            Double dquantity = qty.equals("") ? 0:Double.valueOf(qty);
          //  Double dprice = price.equals("") ? 0:Double.valueOf(price);

            //  Double dquantity = new Double(qty);
            //5Double price = new Double(price);
            Double total = dquantity * dprice;
          //  String sno = String.valueOf(++slNo);
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
          //  Log.d("slNo","-->"+slNo);
            Log.d("code","-->"+product.getId());
            Log.d("name","-->"+product.getName());
            Log.d("qty","-->"+ qty);
            Log.d("price","-->"+price);
            Log.d("modifier","-->"+product.getModifierPrice());
            Log.d("total","-->"+String.valueOf(total));
            Log.d("netotal","-->"+String.valueOf(dNetTotal));
            Log.d("balance","-->"+String.valueOf(product.getBalanceQty()));
            productvalues.put(KEY_FLAG, "Cart");
             productvalues.put(KEY_SL_NO, product.getSno());
            productvalues.put(KEY_PRODUCTCODE, product.getId());
            productvalues.put(KEY_PRODUCTNAME, product.getName());
            productvalues.put(KEY_QUANTITY, qty);
            productvalues.put(KEY_PRICE, price);
            productvalues.put(KEY_BALANCE_QUANTITY, String.valueOf(product.getBalanceQty()));
            productvalues.put(KEY_TOTAL, Utils.twoDecimalPoint(total));
            productvalues.put(KEY_RATE, rate);
            productvalues.put(KEY_TAX, String.valueOf(dNetTotal));
            productvalues.put(KEY_NETTOTAL, String.valueOf(dNetTotal));
            productvalues.put(KEY_PRODUCTIMAGE, product.getImage());
            productvalues.put(KEY_IS_PRODUCT_MODIFIER, "false");

            mDBHelper.insertProduct(productvalues);

            orderSubTotal = mDBHelper.getTotal();
            orderTax = mDBHelper.getServiceTaxTotal();
            orderTotal = orderSubTotal+orderTax;
            mlblServiceCharge.setText(Utils.twoDecimalPoint(orderTax));
            mlblSubTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
            mlblGrandTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
        }catch (Exception e){
            e.printStackTrace();
        }

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
            final String sProductCode = product.getId();
            final String sProductSlNo = product.getSno();

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
                            String dbModifierCode = mDBHelper.getModifierCode(modiferCode,sProductCode,sProductSlNo);
                           // Log.d("dbModifierCode",""+dbModifierCode);
                           // Log.d("modiferCode",""+modiferCode);
                            if(dbModifierCode!=null && !dbModifierCode.isEmpty()){
                                if(dbModifierCode.equals(modiferCode)){
                                    modifier.setSelected(true);
                                }else{
                                    modifier.setSelected(false);
                                }
                            }else{
                                modifier.setSelected(false);
                            }

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
            openModifierDialog(product);
        }
    }
    public void openModifierDialog(final Product product){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cart_modifier, null);
        myDialog.setView(dialogView);
        GridView mGridView = (GridView) dialogView.findViewById(R.id.gridView);
        ImageView close = (ImageView) dialogView.findViewById(R.id.close);
        ImageView ok = (ImageView) dialogView.findViewById(R.id.ok);
        mModifierAdapter = new ModifierAdapter(activity,R.layout.item_modifier,mModiferArr);
        mGridView.setAdapter(mModifierAdapter);



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = mModifierAdapter.isSelected();
                if(isSelected){
                    ArrayList<Modifier> modifiersList = mModifierAdapter.getAllModifier();
                    if (modifiersList.size() > 0) {
                        for (Modifier modifier : modifiersList) {
                            HashMap<String, String> modifierValues = new HashMap<String, String>();
                            String modiferCode = modifier.getCode();
                            String modiferName = modifier.getName();
                            String modifierPrice = modifier.getPrice();
                            modifierValues.put(KEY_IS_PRODUCT_MODIFIER, String.valueOf(modifier.isSelected()));
                            modifierValues.put(KEY_SL_NO, product.getSno());
                            modifierValues.put(KEY_PRODUCTCODE, product.getId());
                            modifierValues.put(KEY_MODIFIER_ID, modiferCode);
                            modifierValues.put(KEY_MODIFIER_NAME, modiferName);
                            modifierValues.put(KEY_PRICE, modifierPrice);
                            mDBHelper.updateModifier(modifierValues);
                        }
                        double dPrice = Double.valueOf(product.getPrice());
                        double modifierPrice = mModifierAdapter.getSelectedModifierPrice();
                        double totalPrice = dPrice + modifierPrice;
                        String price = Utils.twoDecimalPoint(totalPrice);
                        product.setModifierPrice(price);
                        int quantity =  product.getQuantity();
                        Double total = quantity * totalPrice;
                        String tot = Utils.twoDecimalPoint(total);
                        product.setTotal(Double.valueOf(tot));
                        mDBHelper.updateTotal(product.getSno(),product.getId(),tot);
                        orderSubTotal = mDBHelper.getTotal();
                        orderTax = mDBHelper.getServiceTaxTotal();
                        orderTotal = orderSubTotal+orderTax;
                        mlblServiceCharge.setText(Utils.twoDecimalPoint(orderTax));
                        mlblSubTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
                        mlblGrandTotal.setText(Utils.twoDecimalPoint(orderSubTotal));
                        notifyDataSetChanged();

                    }
                    mAlertDialog.dismiss();
                }else{
                    Toast.makeText(activity,"Please select the modifier",Toast.LENGTH_SHORT).show();
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog=myDialog.show();
    }
    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }
}
