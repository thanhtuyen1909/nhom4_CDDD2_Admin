package vn.edu.tdc.cddd2.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tdc.cddd2.activitys.DetailEmployeeActivity;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.interfaceClick.ItemClickRefreshEmployees;

public class ManageEmployeesAdapter extends RecyclerView.Adapter<ManageEmployeesAdapter.ManageEmployeesViewHolder> {
    Context context;
    ArrayList<Employee> arrEmployees;
    ItemClickRefreshEmployees onRefresh;

    public ManageEmployeesAdapter(Context context, ArrayList<Employee> arrEmployees, ItemClickRefreshEmployees onRefresh) {
        this.context = context;
        this.arrEmployees = arrEmployees;
        this.onRefresh = onRefresh;
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

        holder.imgCardManageEmployeesDelete.setOnClickListener(view -> removeEmployees(employees.getId(), employees.getAccountID()));

        //Selected edit
        holder.imgCardManageEmployeesEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailEmployeeActivity.class);
            intent.putExtra("type", "edit");
            intent.putExtra("item", (Parcelable) employees);
            context.startActivity(intent);
        });
    }

    private void removeEmployees(String keyEmployees, String accountId) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setMessage("Bạn có chắc muốn xoá nhân viên này không?");

        b.setPositiveButton("Đồng ý", (dialog, id) -> {
            //Remove data in employees
            FirebaseDatabase.getInstance().getReference().child("Employees")
                    .child(keyEmployees).removeValue();
            //Remove data in account
            FirebaseDatabase.getInstance().getReference().child("Account")
                    .child(accountId).removeValue();
            //Refresh data
            onRefresh.onItemClickEmployees();
        });

        b.setNegativeButton("Huỷ", (dialog, id) -> dialog.cancel());
        AlertDialog al = b.create();
        al.show();
    }

    @Override
    public int getItemCount() {
        return arrEmployees.size();
    }


    protected static class ManageEmployeesViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCardManageEmployeesEdit, imgCardManageEmployeesDelete;
        CircleImageView imgCardManageEmployees;
        TextView tvCardManageEmployeesId, tvCardManageEmployeesName, tvCardManageEmployeesPosition;

        public ManageEmployeesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCardManageEmployees = itemView.findViewById(R.id.imgCardManageEmployees);
            imgCardManageEmployeesDelete = itemView.findViewById(R.id.imgCardManageEmployeesDelete);
            imgCardManageEmployeesEdit = itemView.findViewById(R.id.imgCardManageEmployeesEdit);
            tvCardManageEmployeesId = itemView.findViewById(R.id.tvCardManageEmployeesId);
            tvCardManageEmployeesName = itemView.findViewById(R.id.tvCardManageEmployeesName);
            tvCardManageEmployeesPosition = itemView.findViewById(R.id.tvCardManageEmployeesPosition);
        }
    }
}
