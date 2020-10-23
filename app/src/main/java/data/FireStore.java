package data;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class FireStore {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addUserData(String fullName, String birth, String phone) {
        HashMap<String, Object> userInfo = new HashMap<>();
        HashMap<String, Object> userWithCondition = new HashMap<>();

        userInfo.put("fullName", fullName);
        userInfo.put("birth", birth);
        userInfo.put("phone", phone.substring(1));
        userInfo.put("case", false);
        userInfo.put("indication", false);
        userInfo.put("contacts", userWithCondition);
        userInfo.put("contactedPeopleCount", 0);

        DocumentReference dr = db.collection("users").document(phone);
        dr.set(userInfo)
                .addOnSuccessListener(documentReference -> Log.v("document snap", String.valueOf(documentReference)))
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
