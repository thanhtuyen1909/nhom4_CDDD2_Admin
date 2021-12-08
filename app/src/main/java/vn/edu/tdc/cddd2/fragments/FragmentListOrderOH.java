package vn.edu.tdc.cddd2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import vn.edu.tdc.cddd2.adapters.Order5Adapter;
import vn.edu.tdc.cddd2.data_models.Order;

public class FragmentListOrderOH extends Fragment {
    // Khai báo biến:
    Handler handler = new Handler();
    private RecyclerView recyclerView;
    private ArrayList<Order> listOrder;
    private Order5Adapter orderAdapter;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Order");
    DatabaseReference cusRef = FirebaseDatabase.getInstance().getReference("Customer");
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
        orderAdapter = new Order5Adapter(listOrder, getActivity());
        orderAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private Order5Adapter.ItemClickListener itemClickListener = item -> {
        intent = new Intent(getActivity(), DetailOrderActivity.class);
        intent.putExtra("item", (Parcelable) item);
        startActivity(intent);
    };

    private void data() {
        myRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOrder.clear();
                ArrayList<Order> listBlack = new ArrayList<>();
                listBlack.clear();
                for (DataSnapshot DSOrder : dataSnapshot.getChildren()) {
                    Order order = DSOrder.getValue(Order.class);
                    order.setOrderID(DSOrder.getKey());
                    cusRef.orderByChild("accountID").equalTo(order.getAccountID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if(snapshot1.child("status").equals("black")) {
                                    listBlack.add(order);
                                } else {
                                    listOrder.add(order);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listOrder.addAll(listBlack);
                    }
                }, 200);
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

    private void Swap(Order a, Order b) {
        Order temp = a;
        a = b;
        b = temp;
    }
}


