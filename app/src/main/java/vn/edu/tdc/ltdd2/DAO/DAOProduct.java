package vn.edu.tdc.ltdd2.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import vn.edu.tdc.ltdd2.data_models.Product;

public class DAOProduct {
    private DatabaseReference ref;

    public DAOProduct() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        ref = db.getReference("Products");
    }

    public Task<Void> add(Product product) {
        HashMap<String, Object> map = createMap(product);
        return ref.push().setValue(map);
    }

    public Task<Void> update(String key, Product product) {
        HashMap<String, Object> map = createMap(product);
        return ref.child(key).setValue(map);
    }

    public HashMap<String, Object> createMap(Product product) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("category_id", product.getCategory_id());
        map.put("image", product.getImage());
        map.put("quantity", product.getQuantity());
        map.put("description", product.getDescription());
        map.put("import_price", product.getImport_price());
        map.put("manu_id", product.getManu_id());
        map.put("price", product.getPrice());
        map.put("sold", product.getSold());
        map.put("created_at", product.getCreated_at());
        map.put("status", product.getStatus());
        map.put("rating", product.getRating());
        return map;
    }

    public Task<Void> delete(String key) {
        return ref.child(key).removeValue();
    }
}
