package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Catagory;

public class CataAdapter extends RecyclerView.Adapter<CataAdapter.ViewHolder> {
    ArrayList<Catagory> listCatas;
    private Context context;
    CataAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(CataAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CataAdapter(ArrayList<Catagory> listCatas, Context context) {
        this.listCatas = listCatas;
        this.context = context;
    }

    @NonNull
    @Override
    public CataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_manu_1, parent, false);
        CataAdapter.ViewHolder viewHolder = new CataAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CataAdapter.ViewHolder holder, int position) {
        Catagory item = listCatas.get(position);
        holder.im_item.setImageResource(R.drawable.ic_baseline_laptop_mac_24);
        holder.tv_name.setText(item.getName());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.getInfor(item);
                } else {
                    return;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return listCatas.size();
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
        void getInfor(Catagory item);
    }
}
