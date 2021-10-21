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

public class Order1Adapter extends RecyclerView.Adapter<Order1Adapter.ViewHolder> {
    ArrayList<Order> listOrder;
    private Context context;
    Order1Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Order1Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order1Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public Order1Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_waitship_whm, parent, false);
        Order1Adapter.ViewHolder viewHolder = new Order1Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Order1Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getMaDH());
        //holder.tv_tong.setText("Tổng: " + item.getTongTien());
        //holder.tv_diachi.setText("Địa chỉ: " + item.getDiaChi());
        //holder.tv_nguoiship.setText(item.getNguoiGiao());
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
        TextView tv_maDH, tv_tong, tv_diachi, tv_nguoiship;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            tv_nguoiship = itemView.findViewById(R.id.txt_nguoigiao);
            im_detail = itemView.findViewById(R.id.btnDetail);
            im_detail.setOnClickListener(this);
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
