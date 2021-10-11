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
import vn.edu.tdc.cddd2.data_models.Manufacturer;
import vn.edu.tdc.cddd2.data_models.Product;

public class ManuAdapter  extends RecyclerView.Adapter<ManuAdapter.ViewHolder> {
    ArrayList<Manufacturer> listManus;
    private Context context;
    ManuAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(ManuAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ManuAdapter(ArrayList<Manufacturer> listManus, Context context) {
        this.listManus = listManus;
        this.context = context;
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
        Manufacturer item = listManus.get(position);
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
        return listManus.size();
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
        void getInfor(Manufacturer item);
    }
}
