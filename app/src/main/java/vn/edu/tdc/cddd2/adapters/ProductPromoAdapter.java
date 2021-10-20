package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
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

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Product;

public class ProductPromoAdapter  extends RecyclerView.Adapter<ProductPromoAdapter.ViewHolder> {
    ArrayList<Product> listProducts;
    private Context context;
    DatabaseReference promoDetailRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
    ProductPromoAdapter.ItemClickListener itemClickListener;
    String key, keyPD;

    public void setItemClickListener(ProductPromoAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ProductPromoAdapter(ArrayList<Product> listProducts, Context context, String key) {
        this.listProducts = listProducts;
        this.context = context;
        this.key = key;
    }

    @NonNull
    @Override
    public ProductPromoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_2,parent,false);
        ProductPromoAdapter.ViewHolder viewHolder = new ProductPromoAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductPromoAdapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/products/"+item.getName()+"/"+item.getName()+".jpg");
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(holder.im_item.getWidth(), holder.im_item.getHeight()).into(holder.im_item);
            }
        });
        holder.tv_name.setText(item.getName());
        keyPD = item.getKey();

        promoDetailRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("offerID").getValue(String.class).equals(key) && snapshot.child("productID").getValue(String.class).equals(item.getKey())) {
                        holder.tv_percent.setText("Khuyến mãi: " + snapshot.child("percentSale").getValue(Integer.class) + "%");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.onClickListener = v -> {
            if(itemClickListener != null) {
                if(v == holder.im_delete) {
                    itemClickListener.deleteProductInPromo(keyPD);
                } else itemClickListener.editProductInPromo(keyPD);
            } else {
                return;
            }
        };
    }


    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void deleteProductInPromo(String key);
        void editProductInPromo(String key);
    }
}