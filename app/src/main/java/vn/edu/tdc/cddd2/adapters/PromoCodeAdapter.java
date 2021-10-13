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
import vn.edu.tdc.cddd2.data_models.PromoCode;

public class PromoCodeAdapter extends RecyclerView.Adapter<PromoCodeAdapter.ViewHolder> {
    ArrayList<PromoCode> listPromoCode;
    private Context context;
    PromoCodeAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(PromoCodeAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PromoCodeAdapter(ArrayList<PromoCode> listPromoCode, Context context) {
        this.listPromoCode = listPromoCode;
        this.context = context;
    }

    @NonNull
    @Override
    public PromoCodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_promo_1, parent, false);
        PromoCodeAdapter.ViewHolder viewHolder = new PromoCodeAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PromoCodeAdapter.ViewHolder holder, int position) {
        PromoCode item = listPromoCode.get(position);
        holder.im_item.setImageResource(R.drawable.custom_button_1);
        holder.tv_name.setText(item.getName());
        holder.tv_startDate.setText("Ngày bắt đầu: " + item.getDateStart());
        holder.tv_endDate.setText("Ngày kết thúc: " + item.getDateEnd());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    if(v.getId() == R.id.btnAddDetail) {
                        itemClickListener.getLayoutAddDetailPromoCode();
                    } else {
                        itemClickListener.getInfor(item);
                    }
                } else {
                    return;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return listPromoCode.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item, im_edit, im_delete, im_addDetail;
        TextView tv_name, tv_startDate, tv_endDate;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_startDate = itemView.findViewById(R.id.txt_startDate);
            tv_endDate = itemView.findViewById(R.id.txt_endDate);
            im_edit = itemView.findViewById(R.id.btnEdit);
            im_delete = itemView.findViewById(R.id.btnDelete);
            im_addDetail = itemView.findViewById(R.id.btnAddDetail);
            im_edit.setOnClickListener(this);
            im_delete.setOnClickListener(this);
            im_addDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v) ;
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(PromoCode item);
        void getLayoutAddDetailPromoCode();
    }
}

