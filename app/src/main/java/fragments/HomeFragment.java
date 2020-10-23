package fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.covidtracker.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import activities.BackgroundTask;
import interfaces.Task;
import services.and.receivers.Constants;
import services.and.receivers.SnackCreator;

import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings("ConstantConditions")
public class HomeFragment extends Fragment {
    public static Task<Boolean> notifySwitchChange;
    private SharedPreferences pref;

    private SwitchMaterial caseSwitch, indicationSwitch;
    private Button btnTestHome, btnDetailsHome, btnAdvicesHome;
    private ImageView statusIcon;
    private TextView statusTextHome;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private int counter = 0;
    private int impTfCounter = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pref = getActivity().getApplicationContext().getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);

        BackgroundTask.UI = s -> {
            if (s.equals(Constants.SUSPICIOUS)) {
                statusIcon.setImageResource(R.drawable.suspicion);
                statusTextHome.setText(Constants.SUSPICIOUS_MSG_HOME);
            }
            if (s.equals(Constants.CASE)) {
                statusIcon.setImageResource(R.drawable.warnred);
                statusTextHome.setText(Constants.CASE_MSG_HOME);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        caseSwitch = view.findViewById(R.id.caseSwitch);
        indicationSwitch = view.findViewById(R.id.indicationSwitch);
        btnDetailsHome = view.findViewById(R.id.btnDetailsHome);
        btnTestHome = view.findViewById(R.id.btnTestHome);
        btnAdvicesHome = view.findViewById(R.id.btnAdvicesHome);
        statusIcon = view.findViewById(R.id.statusIcon);
        statusTextHome = view.findViewById(R.id.statusTextHome);
        setSwitchData();

        SharedPreferences.Editor e = pref.edit();

        caseSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                caseSwitch.setEnabled(false);
                caseSwitch.setChecked(!b);

                SnackCreator.showActionSnack(getActivity(), Constants.SWITCH_ACTION_TITLE,
                        "Değiştir", snackbar -> {
                            Map<String, Boolean> map = new HashMap<>();
                            map.put("case", b);

                            db.collection("users").document(auth.getCurrentUser().getPhoneNumber()).set(map, SetOptions.merge())
                                    .addOnSuccessListener(runnable -> {
                                        caseSwitch.setChecked(b);
                                        caseSwitch.setEnabled(true);
                                        e.putString("case_switch", String.valueOf(b));
                                        e.apply();
                                        notifySwitchChange.exec(b);
                                    });
                            snackbar.dismiss();
                        });
            }
        });

        indicationSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                indicationSwitch.setChecked(!b);
                indicationSwitch.setEnabled(false);

                SnackCreator.showActionSnack(getActivity(), Constants.SWITCH_ACTION_TITLE,
                        "Değiştir", snackbar -> {
                            Map<String, Boolean> map = new HashMap<>();
                            map.put("indication", b);

                            db.collection("users").document(auth.getCurrentUser().getPhoneNumber()).set(map, SetOptions.merge())
                                    .addOnSuccessListener(runnable -> {
                                        indicationSwitch.setChecked(b);
                                        indicationSwitch.setEnabled(true);
                                        e.putString("indication_switch", String.valueOf(b));
                                        e.apply();
                                        notifySwitchChange.exec(b);
                                    });
                            snackbar.dismiss();
                        });
            }
        });
        return view;
    }

    private void setSwitchData() {
        String caseSwitchVal = pref.getString("case_switch", null);
        String indicationSwitchVal = pref.getString("indication_switch", null);

        if (caseSwitchVal == null || indicationSwitchVal == null) {
            DocumentReference dr = db.collection("users").document(auth.getCurrentUser().getPhoneNumber());
            dr.get().addOnSuccessListener(documentSnapshot -> {
                try {
                    caseSwitch.setChecked(documentSnapshot.getBoolean("case"));
                    indicationSwitch.setChecked(documentSnapshot.getBoolean("indication"));
                }
                catch (NullPointerException e) {
                    caseSwitch.setChecked(false);
                    indicationSwitch.setChecked(false);
                }
            });
        }
        else {
            caseSwitch.setChecked(Boolean.parseBoolean(caseSwitchVal));
            indicationSwitch.setChecked(Boolean.parseBoolean(indicationSwitchVal));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTestHome.setOnClickListener(v -> showDialogForTest());

        btnDetailsHome.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                    .setTitle(Constants.PRESERVATION_COVID_TITLE)
                    .setMessage(Constants.PRESERVATION_COVID_BODY)
                    .setCancelable(true)
                    .setAnimation(R.raw.docs)
                    .build();

            LottieAnimationView animationView = mBottomSheetDialog.getAnimationView();
            animationView.setPaddingRelative(0, 80, 0, 0);

            mBottomSheetDialog.show();
        });

        btnAdvicesHome.setOnClickListener(v -> {
            AtomicReference<String> msg = new AtomicReference<>(Constants.ADVICE_6);
            String birthOnPref = pref.getString("birth", "01/01/1994");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            try {
                Date d = formatter.parse(birthOnPref);
                if (d.after(formatter.parse("01/01/1920"))) msg.set(Constants.ADVICE_5); //80 -> 65
                if (d.after(formatter.parse("01/01/1955"))) msg.set(Constants.ADVICE_4); //65 -> 50
                if (d.after(formatter.parse("01/01/1970"))) msg.set(Constants.ADVICE_3);// 50 -> 25
                if (d.after(formatter.parse("01/01/1995"))) msg.set(Constants.ADVICE_2);// 25 -> 15
                if (d.after(formatter.parse("01/01/2005"))) msg.set(Constants.ADVICE_1);// 15 -> 0

                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                        .setTitle(Constants.ADVICE_TITLE)
                        .setMessage(msg.get())
                        .setCancelable(true)
                        .setAnimation(R.raw.shield_person)
                        .build();
                LottieAnimationView animationView = mBottomSheetDialog.getAnimationView();
                animationView.setPaddingRelative(0, 80, 0, 0);

                mBottomSheetDialog.show();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void showDialogForTest() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                .setTitle(Constants.TEST_TITLES.get(counter))
                .setMessage(Constants.TEST_BODIES.get(counter))
                .setCancelable(true)
                .setPositiveButton("Evet", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    if (counter < 5) {
                        impTfCounter++;
                    }
                    if (counter == 7) {
                        counter = 0;
                        dialogInterface.dismiss();
                        if (impTfCounter > 0) {
                            SnackCreator.showSnack(getActivity(), Constants.TEST_RES_POSITIVE);
                        }
                        else {
                            SnackCreator.showSnack(getActivity(), Constants.TEST_RES_NEGATIVE);
                        }
                        impTfCounter = 0;
                    }
                    else {
                        showDialogForTest();
                    }
                })
                .setNegativeButton("Hayır", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    if (counter < 5) {
                        impTfCounter--;
                    }
                    if (counter == 7) {
                        counter = 0;
                        dialogInterface.dismiss();
                        if (impTfCounter > 0) {
                            SnackCreator.showSnack(getActivity(), Constants.TEST_RES_POSITIVE);
                        }
                        else {
                            SnackCreator.showSnack(getActivity(), Constants.TEST_RES_NEGATIVE);
                        }
                        impTfCounter = 0;
                    }
                    else {
                        showDialogForTest();
                    }
                })
                .build();
        mBottomSheetDialog.setOnCancelListener(dialogInterface -> {
            counter = 0;
            impTfCounter = 0;
        });

        mBottomSheetDialog.show();
        counter++;
    }
}