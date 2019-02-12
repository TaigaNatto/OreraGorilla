package natto.com.oreragorilla;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBaseRepository {

    private DatabaseReference mRef;

    public FireBaseRepository(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference(id);
    }

    public void initial() {
        mRef.child("imageUrl").setValue("");
        mRef.child("percent").setValue(0.0f);
    }

    public void setValueEventListener(ValueEventListener listener) {
        mRef.addValueEventListener(listener);
    }
}
