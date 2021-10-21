package vn.edu.tdc.cddd2.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.tdc.cddd2.data_models.Product;

public class DAOProduct {
    private DatabaseReference ref;
    ArrayList<Product> list  = new ArrayList<Product>();
    public DAOProduct() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ref = db.getReference("Products");
    }
    public Task<Void> add(Product product){
        HashMap<String,Object> map = createMap(product);
        return ref.push().setValue(map);
    }
    public Task<Void> update(String key, Product product){
        HashMap<String,Object> map = createMap(product);
        return ref.child(key).updateChildren(map);
    }
    public  HashMap<String,Object> createMap(Product product){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("id",product.getId());
        map.put("name",product.getName());
        map.put("category_id",product.getCategory_id());
        map.put("image",product.getImage());
        map.put("quantity",product.getQuantity());
        map.put("description",product.getDescription());
        map.put("import_price",product.getImport_price());
        map.put("manu_id",product.getManu_id());
        map.put("price",product.getPrice());
        map.put("sold",product.getSold());
        map.put("created_at", product.getCreated_at());
        return map;
    }
    public Task<Void> delete(String key){
        return  ref.child(key).removeValue();
    }
}
