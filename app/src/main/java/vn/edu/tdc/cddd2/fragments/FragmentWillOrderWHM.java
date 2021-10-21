package vn.edu.tdc.cddd2.fragments;

import android.annotation.SuppressLint;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.OrderAdapter;
import vn.edu.tdc.cddd2.data_models.Order;

public class FragmentWillOrderWHM extends Fragment {
    // Khai báo biến:
    RecyclerView recyclerView;
    ArrayList<Order> listOrder;
    OrderAdapter orderAdapter;
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");

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
        orderAdapter = new OrderAdapter(listOrder,getActivity());
        orderAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private OrderAdapter.ItemClickListener itemClickListener = item -> Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();

    private void data(){
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("status").getValue(Integer.class) == 6) {
                        Order order = snapshot.getValue(Order.class);
                        order.setMaDH(snapshot.getKey());
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
}
