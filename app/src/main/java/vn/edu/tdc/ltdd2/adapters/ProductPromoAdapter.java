package vn.edu.tdc.ltdd2.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.data_models.DetailPromoCode;

public class ProductPromoAdapter extends RecyclerView.Adapter<ProductPromoAdapter.ViewHolder> {
    ArrayList<DetailPromoCode> listProducts;
    private Context context;
    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products");
    ProductPromoAdapter.ItemClickListener itemClickListener;
    String name = "";

    public void setItemClickListener(ProductPromoAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ProductPromoAdapter(ArrayList<DetailPromoCode> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductPromoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_2, parent, false);
        ProductPromoAdapter.ViewHolder viewHolder = new ProductPromoAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductPromoAdapter.ViewHolder holder, int position) {
        DetailPromoCode item = listProducts.get(position);
        holder.tv_percent.setText("Khuyến mãi: " + item.getPercentSale() + "%");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(item.getProductID())) {
                        name = snapshot.child("name").getValue(String.class);
                        holder.tv_name.setText(name);
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/products/" + name
                                + "/" + name + ".jpg");
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri.toString()).resize(holder.im_item.getWidth(), holder.im_item.getHeight()).into(holder.im_item);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v == holder.im_delete) {
                    itemClickListener.deleteProductInPromo(item.getProductID());
                } else itemClickListener.editProductInPromo(item.getProductID());
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
        ImageView im_item, im_delete;
        TextView tv_name, tv_percent;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_percent = itemView.findViewById(R.id.txt_percentSale);
            im_delete = itemView.findViewById(R.id.btnDelete);
            im_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void deleteProductInPromo(String key);

        void editProductInPromo(String key);
    }
}