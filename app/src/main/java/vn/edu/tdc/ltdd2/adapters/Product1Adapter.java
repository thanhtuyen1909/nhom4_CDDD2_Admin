package vn.edu.tdc.ltdd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.data_models.Manufacture;
import vn.edu.tdc.ltdd2.data_models.Product;

public class Product1Adapter extends RecyclerView.Adapter<Product1Adapter.ViewHolder> {
    ArrayList<Product> listProducts;
    Context context;
    Product1Adapter.ItemClickListener itemClickListener;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Manufactures");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setItemClickListener(Product1Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Product1Adapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public Product1Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_6, parent, false);
        Product1Adapter.ViewHolder viewHolder = new Product1Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Product1Adapter.ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        StorageReference imageRef = storage.getReference("images/products/" + item.getName() + "/" + item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).resize(holder.im_item.getWidth(), holder.im_item.getHeight()).into(holder.im_item);
            holder.tv_name.setText(item.getName());
            holder.tv_price.setText("Giá: " + formatPrice(item.getPrice()));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Manufacture temp = node.getValue(Manufacture.class);
                        if (node.getKey() == item.getManu_id()) {
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

        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                itemClickListener.getInfor(item.getId());
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
        TextView tv_name, tv_price, tv_amount, tv_manu;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_price = itemView.findViewById(R.id.txt_price);
            tv_amount = itemView.findViewById(R.id.txt_amount);
            tv_manu = itemView.findViewById(R.id.txt_manu);
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
        void getInfor(String key);
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
