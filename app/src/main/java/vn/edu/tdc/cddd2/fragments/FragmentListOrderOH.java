package vn.edu.tdc.cddd2.fragments;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Order1Adapter;
import vn.edu.tdc.cddd2.adapters.Order2Adapter;
import vn.edu.tdc.cddd2.adapters.OrderAdapter;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Order;

public class FragmentListOrderOH extends Fragment {
    // Khai báo biến:
    private RecyclerView recyclerView;
    private ArrayList<Order> listOrder;
    private ArrayList<Order> listOrder1;
    private Order2Adapter orderAdapter;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

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
        orderAdapter = new Order2Adapter(listOrder, getActivity());
        orderAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private Order2Adapter.ItemClickListener itemClickListener = new Order2Adapter.ItemClickListener() {
        @Override
        public void getInfor(Order item) {
            Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void data() {
        //listOrder.add(new Order("DH001", 15000000, "53, Võ Văn Ngân", "12/10/2021"));
        //listOrder.add(new Order("DH002", 14000000, "53, Võ Văn Ngân", "12/10/2021"));
        //listOrder.add(new Order("DH003", 12000000, "53, Võ Văn Ngân", "12/10/2021"));
        //listOrder.add(new Order("DH004", 16000000, "53, Võ Văn Ngân", "12/10/2021"));
        //listOrder.add(new Order("DH005", 12000000, "53, Võ Văn Ngân", "12/10/2021"));
        myRef.child("Account").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    Account account = node.getValue(Account.class);
                    account.setAccountID(node.getKey());
                    myRef.child("Order").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listOrder.clear();
                            int a = 0;
                            for (DataSnapshot DSOrder : dataSnapshot.getChildren()) {
                                Order order = DSOrder.getValue(Order.class);
                                order.setOrderID(DSOrder.getKey());
                                if (order.getStatus() == 0 && order.getAccountID().equals(account.getAccountID())) {
                                    a += 1;
                                    Log.d("TAG", "onDataChange: "+a);
                                }
                                if(order.getStatus()==1&&a<3){
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public ArrayList<Order> getListOrder() {
        return listOrder;
    }

    private void Swap(int a, int b) {
        int temp = a;
        a = b;
        b = temp;
    }
}


