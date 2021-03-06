package vn.edu.tdc.cddd2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.activitys.DetailOrderActivity;
import vn.edu.tdc.cddd2.adapters.Order3Adapter;
import vn.edu.tdc.cddd2.data_models.Order;

public class FragmentCancelOrderOH extends Fragment {

    // Khai báo biến:
    private RecyclerView recyclerView;
    private ArrayList<Order> listOrder;
    private Order3Adapter orderAdapter;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Order");
    Intent intent;

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

    private Order3Adapter.ItemClickListener itemClickListener = item -> {
        intent = new Intent(getActivity(), DetailOrderActivity.class);
        intent.putExtra("item", (Parcelable) item);
        startActivity(intent);
    };

    private void data() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOrder.clear();
                for (DataSnapshot DSOrder : dataSnapshot.getChildren()) {
                    Order order = DSOrder.getValue(Order.class);
                    order.setOrderID(DSOrder.getKey());
                    if (order.getStatus() == 0) {
                        listOrder.add(order);

                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Order> getListOrder() {
        return listOrder;
    }
}
