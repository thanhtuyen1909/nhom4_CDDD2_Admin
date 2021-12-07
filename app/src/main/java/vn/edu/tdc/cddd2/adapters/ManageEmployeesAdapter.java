package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.activitys.DetailEmployeeActivity;
import vn.edu.tdc.cddd2.data_models.Employee;

public class ManageEmployeesAdapter extends RecyclerView.Adapter<ManageEmployeesAdapter.ManageEmployeesViewHolder> {
    Context context;
    ArrayList<Employee> arrEmployees;
    ManageEmployeesAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(ManageEmployeesAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ManageEmployeesAdapter(Context context, ArrayList<Employee> arrEmployees) {
        this.context = context;
        this.arrEmployees = arrEmployees;
    }

    @NonNull
    @Override
    public ManageEmployeesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_1, parent, false);
        return new ManageEmployeesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageEmployeesViewHolder holder, int position) {
        Employee employees = arrEmployees.get(position);
        holder.tvCardManageEmployeesPosition.setText(employees.getPosition());
        holder.tvCardManageEmployeesId.setText(employees.getId());
        holder.tvCardManageEmployeesName.setText(employees.getName());

        if (employees.getImage().equals("") || employees.getImage().equals("null")) {
            holder.imgCardManageEmployees.setImageResource(R.drawable.user);
        } else {
            Picasso.get().load(employees.getImage()).into(holder.imgCardManageEmployees);
        }

        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v.getId() == R.id.imgCardManageEmployeesDelete) {
                    itemClickListener.removeEmployee(employees.getId(), employees.getAccountID());
                }
            } else {
                return;
            }
        };
        //Selected edit
        holder.imgCardManageEmployeesEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailEmployeeActivity.class);
            intent.putExtra("type", "edit");
            intent.putExtra("item", (Parcelable) employees);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrEmployees.size();
    }


    protected static class ManageEmployeesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgCardManageEmployeesEdit, imgCardManageEmployeesDelete;
        CircleImageView imgCardManageEmployees;
        TextView tvCardManageEmployeesId, tvCardManageEmployeesName, tvCardManageEmployeesPosition;
        View.OnClickListener onClickListener;
        public ManageEmployeesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCardManageEmployees = itemView.findViewById(R.id.imgCardManageEmployees);
            imgCardManageEmployeesDelete = itemView.findViewById(R.id.imgCardManageEmployeesDelete);
            imgCardManageEmployeesEdit = itemView.findViewById(R.id.imgCardManageEmployeesEdit);
            tvCardManageEmployeesId = itemView.findViewById(R.id.tvCardManageEmployeesId);
            tvCardManageEmployeesName = itemView.findViewById(R.id.tvCardManageEmployeesName);
            tvCardManageEmployeesPosition = itemView.findViewById(R.id.tvCardManageEmployeesPosition);
            imgCardManageEmployeesDelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }
    public interface ItemClickListener {
        void removeEmployee(String keyEmployees, String accountID);
    }
}
