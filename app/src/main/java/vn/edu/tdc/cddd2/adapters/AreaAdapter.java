package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Area;

public class AreaAdapter extends BaseAdapter {
    final ArrayList<Area> list;
    LayoutInflater layoutInflater;
    Context context;

    public AreaAdapter(Context context, ArrayList<Area> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewProduct;
        if (convertView == null) {
            viewProduct = View.inflate(parent.getContext(), R.layout.item_area, null);
        } else viewProduct = convertView;

        Area area = (Area) getItem(position);
        ((TextView) viewProduct.findViewById(R.id.makv)).setText(area.getKey());
        ((TextView) viewProduct.findViewById(R.id.kv)).setText(area.getName());
        return viewProduct;
    }
}
