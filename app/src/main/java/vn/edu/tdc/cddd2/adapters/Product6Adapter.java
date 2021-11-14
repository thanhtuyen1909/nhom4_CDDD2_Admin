package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.Product;

public class Product6Adapter extends RecyclerView.Adapter<Product6Adapter.ViewHolder>{
    ArrayList<Product> listProducts;
    private Context context;
    Product6Adapter.ItemClickListener itemClickListener;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setItemClickListener(Product6Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Product6Adapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public Product6Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_rating_product, parent, false);
        Product6Adapter.ViewHolder viewHolder = new Product6Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Product6Adapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        holder.ratingBar.setRating(0.0F);
        StorageReference imageRef = storage.getReference("images/products/" + item.getName() + "/" + item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).fit().into(holder.im_item));
        holder.tv_name.setText(item.getName());
        holder.ratingBar.setRating(item.getRating());
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                itemClickListener.getInfor(item.getKey());
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item;
        TextView tv_name;
        RatingBar ratingBar;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(String key);
    }
}
