package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import vn.edu.tdc.cddd2.data_models.CartDetail;
import vn.edu.tdc.cddd2.data_models.Product;

public class CartDetailAdapter extends RecyclerView.Adapter<CartDetailAdapter.ViewHolder> {
    Context context;
    ArrayList<CartDetail> list;
    ItemClickListener itemClickListener;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference cartRef = db.getReference("Cart");
    DatabaseReference cartDetailRef = db.getReference("Cart_Detail");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartDetailAdapter(Context context, ArrayList<CartDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CartDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_4, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDetail item = list.get(position);
        holder.name.setText("");
        holder.price.setText("");
        holder.edtValue.setText("");
        proRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                    if (node.getKey().equals(item.getProductID())) {
                        if (product.getStatus() == -1) {
                            cartDetailRef.child(item.getKey()).removeValue();
                            cartRef.child(item.getCartID()).child("total").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    cartRef.child(item.getCartID()).child("total").setValue(snapshot.getValue(Integer.class) - item.getPrice() * item.getAmount());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            //set name
                            holder.name.setText(product.getName());
                            //set gia san pham
                            holder.price.setText(formatPrice(item.getPrice()));
                            holder.edtValue.setText("" + item.getAmount());
                            StorageReference imageRef = storage.getReference("images/products/" + product.getName() + "/" + product.getImage());
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                Picasso.get().load(uri).fit().into(holder.thumbnail);
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                int value = Integer.parseInt(String.valueOf(holder.edtValue.getText()));
                if (v == holder.btnAdd) {
                    value++;
                    holder.edtValue.setText(String.valueOf(value));
                    itemClickListener.changeQuantity(item.getKey(), value);
                }
                if (v == holder.btnMinus) {
                    value--;
                    holder.edtValue.setText(String.valueOf(value));
                    itemClickListener.changeQuantity(item.getKey(), value);
                }
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        cartRef.child(list.get(position).getCartID()).child("total").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartRef.child(list.get(position).getCartID()).child("total").setValue(snapshot.getValue(Integer.class) - list.get(position).getPrice() * list.get(position).getAmount()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        cartDetailRef.child(list.get(position).getKey()).removeValue();
                        list.remove(position);
                        // notify the item removed by position
                        // to perform recycler view delete animations
                        // NOTE: don't call notifyDataSetChanged()
                        notifyItemRemoved(position);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, price;
        public ImageView thumbnail;
        public EditText edtValue;
        public Button btnMinus, btnAdd;
        public RelativeLayout viewBackground, viewForeground;
        View.OnClickListener onClickListener;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            edtValue = view.findViewById(R.id.valueAmount);
            btnMinus = view.findViewById(R.id.minusButton);
            btnAdd = view.findViewById(R.id.plusButton);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            btnAdd.setOnClickListener(this);
            btnMinus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void changeQuantity(String key, int value);
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