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
import vn.edu.tdc.cddd2.data_models.Manufacture;

public class ManuAdapter extends RecyclerView.Adapter<ManuAdapter.ViewHolder> implements Filterable {
    ArrayList<Manufacture> listManus, listManuFilter, list;
    Context context;
    ManuAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(ManuAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ManuAdapter(ArrayList<Manufacture> listManus, Context context) {
        this.listManus = listManus;
        this.context = context;
        this.list = listManus;
    }

    @NonNull
    @Override
    public ManuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_manu_1, parent, false);
        ManuAdapter.ViewHolder viewHolder = new ManuAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManuAdapter.ViewHolder holder, int position) {
        Manufacture item = listManus.get(position);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/manufactures/" + item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).fit().into(holder.im_item));
        holder.tv_name.setText(item.getName());
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v.getId() == R.id.btnEdit) {
                    itemClickListener.editManufacture(item);
                } else itemClickListener.deleteManufacture(item.getKey());
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listManus.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listManuFilter = list;
                } else {
                    ArrayList<Manufacture> filters = new ArrayList<>();
                    for (Manufacture row : listManus) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filters.add(row);
                        }
                    }
                    listManuFilter = filters;
                }
                filterResults.values = listManuFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listManus = (ArrayList<Manufacture>) filterResults.values;
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
        void deleteManufacture(String key);

        void editManufacture(Manufacture item);
    }
}
