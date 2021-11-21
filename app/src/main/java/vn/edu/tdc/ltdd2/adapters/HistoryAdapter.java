package vn.edu.tdc.ltdd2.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.data_models.AccountHistory;

public class HistoryAdapter extends ArrayAdapter<AccountHistory> {

    // Khai báo biến
    private Activity context;
    private  int layoutItemID;
    private ArrayList<AccountHistory> listAdapter;
    private TextView txt_date, txt_name, txt_amount;

    // Hàm khởi tạo
    public HistoryAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<AccountHistory> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutItemID = resource;
        this.listAdapter = objects;
    }

    // Mỗi lần sinh ra 1 dòng mới thì thực hiện set lại dữ liệu
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        LayoutInflater inflater = context.getLayoutInflater();
        view = inflater.inflate(layoutItemID,parent,false);
        txt_date = view.findViewById(R.id.txt_date);
        txt_name =  view.findViewById(R.id.txt_name);
        txt_amount =  view.findViewById(R.id.txt_amount);

        AccountHistory history = listAdapter.get(position);
        txt_name.setText(history.getAction());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        txt_date.setText("Ngày: " + sdf.format(Date.parse(history.getDate())));
        txt_amount.setText(history.getDetail());

        return view;
    }
}
