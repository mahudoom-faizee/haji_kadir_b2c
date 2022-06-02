package winapp.hajikadir.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.model.Product;

/**
 * Created by user on 04-Feb-17.
 */

public class SearchAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Product> originalArraylist;
    private List<Product> Productlist;

    public SearchAdapter(Context context, List<Product> ProductArrayList) {

        mContext = context;
        this.originalArraylist = new ArrayList<Product>();
        this.Productlist = new ArrayList<Product>();
        originalArraylist.clear();
        Productlist.clear();
        this.Productlist = ProductArrayList;
        this.originalArraylist.addAll(ProductArrayList);
        inflater = LayoutInflater.from(mContext);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return Productlist.size();
    }

    @Override
    public Product getItem(int position) {
        return Productlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final Product Product = Productlist.get(position);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item_search_row, null);
            holder.name = (TextView) view.findViewById(R.id.textView);
            view.setTag(holder);

            view.setId(position);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(Product.getName());



        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Productlist.clear();
        if (charText.length() == 0) {
            Productlist.addAll(originalArraylist);
        }
        else
        {
            for (Product fltr : originalArraylist)
            {
                if ((fltr.getName().toLowerCase(Locale.getDefault()).contains(charText)))
                {
                    Productlist.add(fltr);
                }
            }
        }
        notifyDataSetChanged();
    }

}

