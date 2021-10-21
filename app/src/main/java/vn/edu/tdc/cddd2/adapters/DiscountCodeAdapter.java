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
import vn.edu.tdc.cddd2.data_models.DiscountCode;

public class DiscountCodeAdapter extends RecyclerView.Adapter<DiscountCodeAdapter.ViewHolder> {
    ArrayList<DiscountCode> listCatas;
    private Context context;
    DiscountCodeAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(DiscountCodeAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DiscountCodeAdapter(ArrayList<DiscountCode> listCatas, Context context) {
        this.listCatas = listCatas;
        this.context = context;
    }

    @NonNull
    @Override
    public DiscountCodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_discount_1, parent, false);
        DiscountCodeAdapter.ViewHolder viewHolder = new DiscountCodeAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountCodeAdapter.ViewHolder holder, int position) {
        DiscountCode item = listCatas.get(position);
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
        ImageView im_edit, im_delete;
        TextView tv_name;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
        void getInfor(DiscountCode item);
    }
}
