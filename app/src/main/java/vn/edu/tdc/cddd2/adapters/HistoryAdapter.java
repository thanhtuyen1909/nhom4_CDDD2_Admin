package vn.edu.tdc.cddd2.adapters;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.History;

public class HistoryAdapter extends ArrayAdapter<History> {

    // Khai báo biến
    private Activity context;
    private  int layoutItemID;
    private ArrayList<History> listAdapter;
    private TextView txt_date, txt_name, txt_amount;
    private   Date date;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Hàm khởi tạo
    public HistoryAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<History> objects) {
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

        History history = listAdapter.get(position);
//        String[] temp = history.getDetail().split("\n");
//        String maDH=temp[1];
//        String tongtien=temp[2];



        //chuyen string thanh date
        try {
             date=dateFormat.parse(history.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //chuyen date thanh string
        String strDate=dateFormat.format(date);
        txt_date.setText("Ngay: "+strDate);
        txt_amount.setText("");
        txt_name.setText(history.getDetail());
        return view;
    }
}
