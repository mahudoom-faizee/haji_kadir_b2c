package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import winapp.hajikadir.customer.R;

import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;
import winapp.hajikadir.customer.view.ExpandableHeightGridView;

/**
 * Created by user on 03-Apr-17.
 */

public class MinusAdapter extends ArrayAdapter<Product> implements Constants {
    public interface OnCompletionListener {
        public void onCompleted();
    }
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }
    private ArrayList<Product> productList;
    private Activity activity;
   // private ImageLoader mImageLoader;
    private UrlImageLoader mUrlImageLoader;
    private DBHelper mDBHelper;
    private ModifierAdapter mModifierAdapter;
    private OnCompletionListener listener;
    public MinusAdapter(Activity activity, int textViewResourceId,
                        ArrayList<Product> productArrList) {
        super(activity, textViewResourceId, productArrList);
        this.activity = activity;
        this.mDBHelper = new DBHelper(activity);
       // this.mImageLoader = HajikadirApp.getImageLoader();
        this.mUrlImageLoader = new UrlImageLoader(activity);
        productList = new ArrayList<Product>();
        productList = productArrList;
    }

    public class ViewHolder {
        private TextView mNameTxtV,mQtyTxtV, mPriceTxtV;
        private ImageView mDeleteImgV, mProductImgV;
        private ExpandableHeightGridView mExpandableHeightGridView;
        private LinearLayout mModifierLayout;
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final MinusAdapter.ViewHolder holder;
        Product product = getItem(position);
        if (view == null) {
            holder = new MinusAdapter.ViewHolder();
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_list_minus, null);
            holder.mNameTxtV = (TextView) view.findViewById(R.id.description);
            holder.mPriceTxtV = (TextView) view.findViewById(R.id.price);
            holder.mQtyTxtV = (TextView) view.findViewById(R.id.quantity);
            holder.mProductImgV = (ImageView) view.findViewById(R.id.productImage);
            holder.mDeleteImgV = (ImageView) view.findViewById(R.id.delete);
            holder.mExpandableHeightGridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
            holder.mModifierLayout = (LinearLayout) view.findViewById(R.id.modifier_layout);
            view.setTag(holder);
            view.setId(position);
        } else {
            holder = (MinusAdapter.ViewHolder) view.getTag();
        }
        String sno = product.getSno();
        ArrayList<Modifier> modifierList = mDBHelper.getProductModifier(sno);
        Log.d("modifierList", "--" + modifierList.size());
        if (modifierList.size() > 0) {
            mModifierAdapter = new ModifierAdapter(activity, R.layout.item_cart_modifier, modifierList);
            holder.mExpandableHeightGridView.setAdapter(mModifierAdapter);
            holder.mModifierLayout.setVisibility(View.VISIBLE);
            holder.mExpandableHeightGridView.setExpanded(true);
        } else {
            holder.mModifierLayout.setVisibility(View.GONE);
        }
        mUrlImageLoader.displayImage(product.getImage(), holder.mProductImgV);
        holder.mQtyTxtV.setTag(product);
        if (product.getQuantity() != 0) {
            holder.mQtyTxtV.setText(String.valueOf(product.getQuantity()));
        } else {
            holder.mQtyTxtV.setText("");
        }

        holder.mNameTxtV.setText(product.getName());
        Double dPrice = product.getPrice().equals("") ? 0 : Double.valueOf(product.getPrice());
        holder.mPriceTxtV.setText("$" + Utils.twoDecimalPoint(dPrice));

        holder.mDeleteImgV.setId(position);
        holder.mDeleteImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = view.getId();
                final Product product = productList.get(position);
                final String productId = product.getId();
                final String productSno = product.getSno();
                remove(productId, productSno);

            }
        });




    return view;
    }

    public void remove(String productId, String productSNO) {
        try {
            if (productList.size() > 0) {
                Iterator<Product> i = productList.iterator();
                while (i.hasNext()) {
                    Product mProduct = i.next();
                    // must be called before you can call i.remove()
                    if (productId.equals(mProduct.getId()) && productSNO.equals(mProduct.getSno())) {
                        i.remove();
                        notifyDataSetChanged();
                    }
                }
                mDBHelper.deleteProductModifier(productId,productSNO);
                mDBHelper.deleteProduct(productId, productSNO);
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}


