package vn.edu.tdc.cddd2.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import vn.edu.tdc.cddd2.activitys.DetailOrderActivity;
import vn.edu.tdc.cddd2.adapters.Order3Adapter;
import vn.edu.tdc.cddd2.adapters.Order4Adapter;
import vn.edu.tdc.cddd2.data_models.Order;

public class FragmentListOrderSP extends Fragment {
    // Khai báo biến:
    RecyclerView recyclerView;
    SearchView searchView;
    ArrayList<Order> listOrder;
    Order4Adapter orderAdapter;
    Intent intent;
    String accountID = "a";
    DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        //Khởi tạo biến:
        listOrder = new ArrayList<>();
        searchView = view.findViewById(R.id.editSearch);

        //RecycleView
        recyclerView = view.findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        data();
        orderAdapter = new Order4Adapter(listOrder, getActivity());
        orderAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Xử lý sự kiện thay đổi dữ liệu searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                orderAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private Order4Adapter.ItemClickListener itemClickListener = new Order4Adapter.ItemClickListener() {
        @Override
        public void getInfor(Order item) {
            intent = new Intent(getActivity(), DetailOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("item", (Parcelable) item);
            startActivity(intent);
        }
    };

    private void data() {
        // Lấy ra employeesID
        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                    if (snapshot1.child("accountID").getValue(String.class).equals(accountID) &&
                            snapshot1.child("position").getValue(String.class).equals("Nhân viên giao hàng")) {
                        orderRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listOrder.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("shipperID").getValue(String.class).equals(snapshot1.getKey()) &&
                                            (snapshot.child("status").getValue(Integer.class) == 3 || snapshot.child("status").getValue(Integer.class) == 4)) {
                                        Order order = snapshot.getValue(Order.class);
                                        order.setOrderID(snapshot.getKey());
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Order> getList() {
        return listOrder;
    }
}
