package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.ShipArea;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    ArrayList<Order> listOrder;
    private Context context;
    OrderAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(OrderAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderAdapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_willorder_whm, parent, false);
        OrderAdapter.ViewHolder viewHolder = new OrderAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getMaDH());
        holder.tv_tong.setText("Tổng: " + item.getTotal());
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());
        // lấy dữ liệu cho spinner:
        getKhuVuc(item.getAddress());
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
        Spinner sp_shipper;
        TextView tv_maDH, tv_tong, tv_diachi;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            sp_shipper = itemView.findViewById(R.id.spinShipper);
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

    private void getKhuVuc(String address) {

    }

    public interface ItemClickListener {
        void getInfor(Order item);
    }
}
