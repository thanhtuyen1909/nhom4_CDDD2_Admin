package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Category;

public class CateAdapter extends RecyclerView.Adapter<CateAdapter.ViewHolder> implements Filterable {
    ArrayList<Category> listCatas, listCataFilter, list;
    Context context;
    CateAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(CateAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CateAdapter(ArrayList<Category> listCatas, Context context) {
        this.listCatas = listCatas;
        this.context = context;
        this.list = listCatas;
    }

    @NonNull
    @Override
    public CateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_manu_1, parent, false);
        CateAdapter.ViewHolder viewHolder = new CateAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CateAdapter.ViewHolder holder, int position) {
        Category item = listCatas.get(position);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/categories/" + item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).fit().into(holder.im_item));
        holder.tv_name.setText(item.getName());
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v.getId() == R.id.btnEdit) {
                    itemClickListener.editCategory(item);
                } else {
                    itemClickListener.deleteCategory(item.getKey());
                }
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listCatas.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listCataFilter = list;
                } else {
                    ArrayList<Category> filters = new ArrayList<>();
                    for (Category row : listCatas) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filters.add(row);
                        }
                    }
                    listCataFilter = filters;
                }
                filterResults.values = listCataFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listCatas = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item, im_edit, im_delete;
        TextView tv_name;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            im_edit = itemView.findViewById(R.id.btnEdit);
            im_delete = itemView.findViewById(R.id.btnDelete);
            im_edit.setOnClickListener(this);
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
        void deleteCategory(String key);

        void editCategory(Category item);
    }
}
