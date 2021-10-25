package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employee;

public class Employee1Adapter extends RecyclerView.Adapter<Employee1Adapter.ViewHolder> {
    ArrayList<Employee> listEmployees;
    private Context context;
    Employee1Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Employee1Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Employee1Adapter(ArrayList<Employee> listEmployees, Context context) {
        this.listEmployees = listEmployees;
        this.context = context;
    }

    @NonNull
    @Override
    public Employee1Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_employee_1, parent, false);
        Employee1Adapter.ViewHolder viewHolder = new Employee1Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Employee1Adapter.ViewHolder holder, int position) {
        Employee item = listEmployees.get(position);
        holder.im_item.setImageResource(R.drawable.baseline_person_24);
//        holder.tv_name.setText(item.getHoTenNV());
//        holder.tv_manv.setText(item.getMaNV());
//        holder.tv_chucvu.setText(item.getChucVu());
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
        return listEmployees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item, im_edit, im_delete;
        TextView tv_name, tv_manv, tv_chucvu;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_manv);
            tv_manv = itemView.findViewById(R.id.txt_hoten);
            tv_chucvu = itemView.findViewById(R.id.txt_chucvu);
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
        void getInfor(Employee item);
    }
}
