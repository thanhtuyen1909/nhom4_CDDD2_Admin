package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;

public class Order2Adapter extends RecyclerView.Adapter<Order2Adapter.ViewHolder> {
    ArrayList<Order> listOrder;
    private Context context;
    Order2Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Order2Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order2Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public Order2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_order_oh, parent, false);
        Order2Adapter.ViewHolder viewHolder = new Order2Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Order2Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getMaDH());
        //holder.tv_tong.setText("Tổng: " + item.getTongTien());
        //holder.tv_ngaydat.setText("Địa chỉ: " + item.getNguoiGiao());
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
        return listOrder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_detail;
        TextView tv_maDH, tv_tong, tv_ngaydat;
        CheckBox cb_sedat, cb_huy;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_ngaydat = itemView.findViewById(R.id.txt_ngaydat);
            im_detail = itemView.findViewById(R.id.btnDetail);
            cb_sedat = itemView.findViewById(R.id.checksedat);
            cb_huy = itemView.findViewById(R.id.checkhuy);
            im_detail.setOnClickListener(this);
            cb_sedat.setOnClickListener(this);
            cb_huy.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Order item);
    }
}
