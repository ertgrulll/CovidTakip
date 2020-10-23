package activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covidtracker.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import services.and.receivers.Constants;

public class NotificationActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    TextView nContactedPeople, nContactedUser, nContactedSuspicious, nContactedCase;

    @SuppressWarnings("unchecked")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        nContactedPeople = findViewById(R.id.nContactedPeople);
        nContactedUser = findViewById(R.id.nContactedUser);
        nContactedSuspicious = findViewById(R.id.nContactedSuspicious);
        nContactedCase = findViewById(R.id.nContactedCase);

        AtomicInteger contactedCase = new AtomicInteger(0);
        AtomicInteger contactedSuspicious = new AtomicInteger(0);
        AtomicInteger contactedNormal = new AtomicInteger(0);
        AtomicInteger size = new AtomicInteger(0);

        DocumentReference dr;
        Task<DocumentSnapshot> task;
        if (firebaseAuth.getCurrentUser() != null) {
            dr = firestore.collection("users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getPhoneNumber()));
            task = dr.get();
            task.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Object o = documentSnapshot.get("contacts");
                    HashMap<String, Object> map = (HashMap<String, Object>) o;
                    if (map != null) {
                        size.set(map.size());
                        for (String s : map.keySet()) {
                            if (Objects.equals(map.get(s), Constants.CASE)) {
                                contactedCase.getAndIncrement();
                            }
                            if (Objects.equals(map.get(s), Constants.SUSPICIOUS)) {
                                contactedSuspicious.getAndIncrement();
                            }
                            if (Objects.equals(map.get(s), Constants.NORMAL)) {
                                contactedNormal.getAndIncrement();
                            }
                        }
                    }
                    Long contactedPeopleCount = 0L;
                    if (documentSnapshot.get("contactedPeopleCount") != null)
                        contactedPeopleCount = (Long) documentSnapshot.get("contactedPeopleCount");

                    nContactedPeople.setText("Sosyal mesafeyi " + contactedPeopleCount + " defa aştınız.");
                    nContactedCase.setText("Bilinen " + contactedCase.get() + " Covid-19 hastatsıyla karşılaştınız");
                    nContactedSuspicious.setText("Hastalık şüphesi taşıyan " + +contactedSuspicious.get() + " kişi ile karşılatınız");
                    nContactedUser.setText("Toplam " + size + " CovidTakip kullanıcısı ile karşılaştınız.");
                }
            });
        }
    }


}
