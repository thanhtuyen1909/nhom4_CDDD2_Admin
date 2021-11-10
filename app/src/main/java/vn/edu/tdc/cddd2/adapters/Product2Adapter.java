package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.OfferDetail;
import vn.edu.tdc.cddd2.data_models.Product;

public class Product2Adapter extends RecyclerView.Adapter<Product2Adapter.ViewHolder> {
    ArrayList<Product> listProducts;
    Context context;
    Product2Adapter.ItemClickListener itemClickListener;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Manufactures");
    DatabaseReference offerDetailRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setItemClickListener(Product2Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Product2Adapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public Product2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_3, parent, false);
        Product2Adapter.ViewHolder viewHolder = new Product2Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Product2Adapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        StorageReference imageRef = storage.getReference("images/products/" + item.getName() + "/" + item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).resize(holder.im_item.getWidth(), holder.im_item.getHeight()).into(holder.im_item);
            holder.tv_name.setText(item.getName());
            //Giá
            offerDetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int maxSale = 0;
                    for (DataSnapshot node1 : snapshot.getChildren()) {
                        OfferDetail detail = node1.getValue(OfferDetail.class);
                        if (detail.getProductID().equals(item.getKey())) {
                            if (detail.getPercentSale() > maxSale) {
                                maxSale = detail.getPercentSale();
                            }
                        }
                    }
                    if (maxSale != 0) {
                        holder.tv_pricemain.setVisibility(View.VISIBLE);
                        int discount = item.getPrice() / 100 * (100 - maxSale);
                        holder.tv_price.setText(formatPrice(discount));
                        holder.tv_pricemain.setText(formatPrice(item.getPrice()));
                        holder.tv_pricemain.setPaintFlags(holder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        holder.tv_price.setText(formatPrice(item.getPrice()));
                        holder.tv_pricemain.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Manufacture temp = node.getValue(Manufacture.class);
                        if (node.getKey().equals(item.getManu_id())) {
                            holder.tv_manu.setText("Hãng: " + temp.getName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.tv_amount.setText("Số lượng: " + item.getQuantity());
        });
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.addCart(item.getKey(), formatInt(holder.tv_price.getText().toString()));
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item;
        TextView tv_name, tv_price, tv_pricemain, tv_amount, tv_manu;
        Button bt_muahang;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_price = itemView.findViewById(R.id.txt_price);
            tv_pricemain = itemView.findViewById(R.id.txt_pricemain);
            tv_amount = itemView.findViewById(R.id.txt_amount);
            tv_manu = itemView.findViewById(R.id.txt_manu);
            bt_muahang = itemView.findViewById(R.id.btnAddCart);
            bt_muahang.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void addCart(String key, int price);
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

    private int formatInt(String price) {
        return Integer.parseInt(price.substring(0, price.length() - 2).replace(",", ""));
    }
}
