package vn.edu.tdc.ltdd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.data_models.Product;

public class Product5Adapter extends RecyclerView.Adapter<Product5Adapter.ViewHolder> {
    ArrayList<Product> listProducts;
    private Context context;

    public Product5Adapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public Product5Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_detail_order_invoice,parent,false);
        Product5Adapter.ViewHolder viewHolder = new Product5Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Product5Adapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/products/"+item.getName()+"/"+item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri.toString()).resize(holder.im_item.getWidth(), holder.im_item.getHeight()).into(holder.im_item));
        holder.tv_name.setText(item.getName());
        holder.tv_amount.setText(formatPrice(item.getPrice()) + " x " + String.valueOf(item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView im_item ;
        TextView tv_name, tv_amount ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_amount = itemView.findViewById(R.id.txt_amount);
        }
    }

    private String formatPrice(int price) {
        String stmp = String.valueOf(price);
        int amount;
        amount = (int) (stmp.length() / 3);
        if (stmp.length() % 3 == 0)
            amount--;
        for (int i = 1; i <= amount; i++) {
            stmp = new StringBuilder(stmp).insert(stmp.length() - (i * 3) - (i - 1), ",").toString();
        }
        return stmp + " ₫";
    }
}
