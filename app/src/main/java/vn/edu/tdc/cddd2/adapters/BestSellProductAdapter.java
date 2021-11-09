package vn.edu.tdc.cddd2.adapters;

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

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Product;

public class BestSellProductAdapter extends RecyclerView.Adapter<BestSellProductAdapter.ViewHolder> {
    ArrayList<Product> listProducts, list;
    Context context;



    public BestSellProductAdapter(ArrayList<Product> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
        this.list = listProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_bestsell_product,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = listProducts.get(position);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference("images/products/"+item.getName()+"/"+item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).fit().into(holder.im_item));
        holder.tv_name.setText(item.getName());
        holder.tv_sold.setText("Đã bán : " + String.valueOf(item.getSold()));

    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView im_item;
        TextView tv_name ,tv_sold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_sold = itemView.findViewById(R.id.txt_sold);
        }


    }
}
