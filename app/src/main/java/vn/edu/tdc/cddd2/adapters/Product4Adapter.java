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

public class Product4Adapter extends RecyclerView.Adapter<Product4Adapter.ViewHolder> {
    ArrayList<Product> listProducts;
    private Context context;

    public Product4Adapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public Product4Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_5,parent,false);
        Product4Adapter.ViewHolder viewHolder = new Product4Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Product4Adapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        holder.im_item.setImageResource(R.drawable.ic_baseline_laptop_mac_24);
        holder.tv_name.setText(item.getName());
        holder.tv_price.setText("Giá: " + String.valueOf(item.getPrice()));
        holder.tv_manu.setText("Hãng: " + item.getManu());
        holder.tv_manu.setText("Số lượng: " + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView im_item;
        TextView tv_name, tv_price, tv_manu, tv_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_price = itemView.findViewById(R.id.txt_price);
            tv_manu = itemView.findViewById(R.id.txt_manu);
            tv_amount = itemView.findViewById(R.id.txt_amount);
        }
    }
}