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
import vn.edu.tdc.cddd2.data_models.Employees;
import vn.edu.tdc.cddd2.interfaceClick.ItemClickRefreshEmployees;



public class ManageEmployeesAdapter extends RecyclerView.Adapter<ManageEmployeesAdapter.ManageEmployeesViewHolder>{
    Context context;
    ArrayList<Employees> arrEmployees;
    private ItemClickRefreshEmployees onRefresh;

    public ManageEmployeesAdapter(Context context, ArrayList<Employees> arrEmployees, ItemClickRefreshEmployees onRefresh) {
        this.context = context;
        this.arrEmployees = arrEmployees;
        this.onRefresh = onRefresh;
    }

    @NonNull
    @Override
    public ManageEmployeesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_manage_employees, parent, false);
        return new ManageEmployeesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageEmployeesViewHolder holder, int position) {
        Employees employees = arrEmployees.get(position);
        holder.tvCardManageEmployeesPosition.setText(employees.getPosition());
        holder.tvCardManageEmployeesId.setText(employees.getKeyEmployees());
        holder.tvCardManageEmployeesName.setText(employees.getName());

        if(employees.getImage().equals("")||employees.getImage().equals("null")){
            holder.imgCardManageEmployees.setImageResource(R.drawable.user);
        }else{
            Picasso.get().load(employees.getImage()).into(holder.imgCardManageEmployees);
        }

        holder.imgCardManageEmployeesDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               removeEmployees(employees.getKeyEmployees(), employees.getAccountID());
            }
        });

        //Selected activity
        holder.imgCardManageEmployeesActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, employees.getKeyEmployees(), Toast.LENGTH_SHORT).show();

            }
        });

        //Selected edit
        holder.imgCardManageEmployeesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, employees.getKeyEmployees(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailEmployeeActivity.class);
                intent.putExtra("type","edit");
                intent.putExtra("item",(Parcelable) employees);
                context.startActivity(intent);
            }
        });
    }

    private void removeEmployees(String keyEmployees, String accountId){
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setMessage("Bạn có chắc muốn xoá nhân viên này không?");

        b.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Remove data in employees
                FirebaseDatabase.getInstance().getReference().child("Employees")
                        .child(keyEmployees).removeValue();
                //Remove data in account
                FirebaseDatabase.getInstance().getReference().child("Account")
                        .child(accountId).removeValue();
                //Refresh data
                onRefresh.onItemClickEmployees();
                Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog al = b.create();
        al.show();
    }

    @Override
    public int getItemCount() {
        return arrEmployees.size();
    }


    protected class ManageEmployeesViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgCardManageEmployeesEdit,imgCardManageEmployeesActivity,imgCardManageEmployeesDelete;
        private CircleImageView imgCardManageEmployees;
        private TextView tvCardManageEmployeesId,tvCardManageEmployeesName,tvCardManageEmployeesPosition;

        public ManageEmployeesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCardManageEmployees = itemView.findViewById(R.id.imgCardManageEmployees);
            imgCardManageEmployeesActivity = itemView.findViewById(R.id.imgCardManageEmployeesActivity);
            imgCardManageEmployeesDelete = itemView.findViewById(R.id.imgCardManageEmployeesDelete);
            imgCardManageEmployeesEdit = itemView.findViewById(R.id.imgCardManageEmployeesEdit);
            tvCardManageEmployeesId = itemView.findViewById(R.id.tvCardManageEmployeesId);
            tvCardManageEmployeesName = itemView.findViewById(R.id.tvCardManageEmployeesName);
            tvCardManageEmployeesPosition = itemView.findViewById(R.id.tvCardManageEmployeesPosition);
        }
    }
}
