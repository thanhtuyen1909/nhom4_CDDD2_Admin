package vn.edu.tdc.cddd2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Order3Adapter;
import vn.edu.tdc.cddd2.data_models.Order;

public class FragmentWillOrderOH extends Fragment {

    // Khai báo biến:
    private RecyclerView recyclerView;
    private ArrayList<Order> listOrder;
    private Order3Adapter orderAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        //Khởi tạo biến:
        listOrder = new ArrayList<>();
        //RecycleView
        recyclerView = view.findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        data();
        orderAdapter = new Order3Adapter(listOrder, getActivity());
        orderAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private Order3Adapter.ItemClickListener itemClickListener = new Order3Adapter.ItemClickListener() {
        @Override
        public void getInfor(Order item) {
            Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void data(){
//        listOrder.add(new Order("DH001", 15000000, "53, Võ Văn Ngân", "11/10/2021"));
//        listOrder.add(new Order("DH002", 14000000, "53, Võ Văn Ngân", "11/10/2021"));
    }
}
