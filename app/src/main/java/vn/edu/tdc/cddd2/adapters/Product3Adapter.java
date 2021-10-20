package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Product;

public class Product3Adapter extends RecyclerView.Adapter<Product3Adapter.ViewHolder> {
    ArrayList<Product> listProducts;
    private Context context;
    Product3Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Product3Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Product3Adapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public Product3Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_4,parent,false);
        Product3Adapter.ViewHolder viewHolder = new Product3Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Product3Adapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        holder.im_item.setImageResource(R.drawable.ic_baseline_laptop_mac_24);
        holder.tv_name.setText(item.getName());
        holder.tv_price.setText("Giá: " + String.valueOf(item.getPrice()));
        //holder.tv_manu.setText("Hãng: " + item.getManu());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null) {
                    itemClickListener.getInfor(item);
                } else {
                    return;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView im_item;
        TextView tv_name, tv_price, tv_manu;
        Button bt_increase, bt_decrease;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_price = itemView.findViewById(R.id.txt_price);
            tv_manu = itemView.findViewById(R.id.txt_manu);
            bt_increase = itemView.findViewById(R.id.plusButton);
            bt_decrease = itemView.findViewById(R.id.minusButton);
            bt_increase.setOnClickListener(this);
            bt_decrease.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Product item);
    }
}
