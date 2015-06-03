package com.ibm.mfp.wishlistsample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.mfp.wishlistsample.models.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by chethan on 15/05/15.
 */
public class CatalogListViewAdapter extends BaseAdapter{

    ArrayList<Item> itemList = new ArrayList<Item>();
    int layoutResource = 0;

    public CatalogListViewAdapter(ArrayList<Item> items){
        if (items!=null)
            this.itemList = items;

        EventBus.getDefault().register(this);
        Timber.d("event bus register");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventBus.getDefault().unregister(this);
        Timber.d("event bus un - register");
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.layout_item,parent,false);
            viewHolder.title = (TextView)convertView.findViewById(R.id.item_title);
            viewHolder.store = (TextView)convertView.findViewById(R.id.item_store);
            viewHolder.price = (TextView)convertView.findViewById(R.id.item_value);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_imageView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(itemList.get(position).getTitle());
        viewHolder.store.setText(itemList.get(position).getStore());
        viewHolder.price.setText("$ "+String.valueOf(itemList.get(position).getPrice()));
        Picasso.with(parent.getContext()).load("http://boxstore-catalog.mybluemix.net/MFPSampleWebService"+itemList.get(position).getImgURL()).into(viewHolder.image);
        return convertView;
    }

    //Event bus receives the catalog items list data in this method
    public   void onEventMainThread(ArrayList<Item> list){
        Timber.d("Got catalog item list in Catalog Adapter " + list.size());
        for(Item item : list){
            item.prettyPrint();
        }
//        this.itemList.clear();
        this.itemList = list;
        this.notifyDataSetChanged();
    }

    class ViewHolder {
        TextView title;
        TextView store;
        TextView price;
        ImageView image;
    }
}
