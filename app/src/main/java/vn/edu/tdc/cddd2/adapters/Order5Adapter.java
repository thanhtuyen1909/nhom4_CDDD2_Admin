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
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.Product;

public class Order5Adapter extends RecyclerView.Adapter<Order5Adapter.ViewHolder> {
    ArrayList<Order> listOrders;
    private Context context;
    Order5Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Order5Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order5Adapter(ArrayList<Order> listOrders, Context context) {
        this.listOrders = listOrders;
        this.context = context;
    }

    @NonNull
    @Override
    public Order5Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_order_historyorder,parent,false);
        Order5Adapter.ViewHolder viewHolder = new Order5Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Order5Adapter.ViewHolder holder, int position) {
        Order item = listOrders.get(position);
        holder.im_item.setImageResource(R.drawable.ic_baseline_laptop_mac_24);
        //holder.tv_name.setText(item.getMaDH());
        holder.tv_total.setText("Tổng: " + String.valueOf(item.getTotal()));
        holder.tv_date.setText("Ngày: " + item.getCreated_at());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null) {
                    itemClickListener.getInfor(item);
                } else {
                    return;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView im_item, im_detail;
        TextView tv_name, tv_total, tv_date;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_madonhang);
            tv_total = itemView.findViewById(R.id.txt_total);
            tv_date = itemView.findViewById(R.id.txt_date);
            im_detail = itemView.findViewById(R.id.btnDetail);
            im_detail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Order item);
    }
}
