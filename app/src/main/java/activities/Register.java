package activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covidtracker.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import data.FireStore;
import services.and.receivers.Constants;
import services.and.receivers.FieldChecker;
import services.and.receivers.FireBaseExceptionHandler;
import services.and.receivers.KeyboardUtils;
import services.and.receivers.SnackCreator;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {
    private final AtomicBoolean isKeyboardVisible = new AtomicBoolean();
    private final ArrayList<FieldChecker.FieldsList> fieldsList = new ArrayList<>();
    private final FireStore fs = new FireStore();
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year, month, dayOfMonth;
    private String birthday = "";
    private String name, phone;
    private Button pickerBirth;
    private EditText etName, etPhone;
    private FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks cb = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            fs.addUserData(name, birthday, phone);
            signInWithPhone(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                SnackCreator.showSnack(Register.this, FireBaseExceptionHandler.handle(e));
            }
            else if (e instanceof FirebaseTooManyRequestsException) {
                //sms sınırı aşıldı
                SnackCreator.showSnack(Register.this, Constants.PHONE_VERIFY_ERR);
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);
            createAlertDialog(token, verificationId, phone);

            // Save verification ID and resending token so we can use them later
                                /*mVerificationId = verificationId;
                                mResendToken = token;*/
        }
    };
    private PhoneAuthProvider phoneAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        KeyboardUtils.addKeyboardToggleListener(this, isKeyboardVisible::set);
        mAuth = FirebaseAuth.getInstance();
        phoneAuth = PhoneAuthProvider.getInstance();

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);

        setConstantToEtPhone();

        //Date Picker
        pickerBirth = findViewById(R.id.pickDate);
        pickerBirth.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(Register.this,
                    (datePicker, year, month, day) -> {
                        pickerBirth.setText(day + "/" + (month + 1) + "/" + year);
                        pickerBirth.setTextColor(Color.parseColor("#000000"));
                        birthday = dayOfMonth + "/" + month + "/" + year;
                    }, year, month, dayOfMonth);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {
            if (isKeyboardVisible.get()) KeyboardUtils.toggleKeyboardVisibility(getApplicationContext());
            name = etName.getText().toString();
            phone = etPhone.getText().toString();

            fieldsList.clear();
            fieldsList.add(new FieldChecker.FieldsList("name", name));
            fieldsList.add(new FieldChecker.FieldsList("phone", phone));
            fieldsList.add(new FieldChecker.FieldsList("birthday", birthday));

            //fields not correct or empty
            if (!FieldChecker.checkFields(fieldsList).equals("ok")) {
                System.out.println(FieldChecker.checkFields(fieldsList));
                SnackCreator.showSnack(this, FieldChecker.checkFields(fieldsList));
            }

            //fields acceptable, continue registering
            else {
                phoneAuth.verifyPhoneNumber(
                        phone,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        cb);
            }
        });
    }

    private void createAlertDialog(PhoneAuthProvider.ForceResendingToken token, String verificationId, String phone) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        final EditText etCode = new EditText(getApplicationContext());
        etCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        etCode.setWidth((int) (width / 1.3));

        AtomicReference<String> code = new AtomicReference<>();
        AlertDialog dialog =
                new AlertDialog.Builder(Register.this)
                        .setView(etCode)
                        .setCancelable(false)
                        .setTitle("Sms Kodu:")
                        .setPositiveButton("Onayla", null)
                        .setNegativeButton("Tekrar Gönder", null)
                        .setNeutralButton("Kapat", null)
                        .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

            positive.setOnClickListener(view -> {
                code.set(etCode.getText().toString());
                Log.e("code", etCode.getText().toString());
                if (isKeyboardVisible.get()) {
                    KeyboardUtils.toggleKeyboardVisibility(getApplicationContext());
                }

                if (!(etCode.getText().toString().length() < 3)) {
                    Log.v("Code", code.get());
                    PhoneAuthCredential pac = PhoneAuthProvider.getCredential(verificationId, etCode.getText().toString());
                    signInWithPhone(pac);
                    dialog.dismiss();
                }
                else {
                    SnackCreator.showSnack(Register.this, "Sms kodunu girin veya kod almadıysanız 'Tekrar Gönder' butonuna dokunun.");
                }
            });

            negative.setOnClickListener(view -> {
                Log.v("code n btn", "clicked");
                if (isKeyboardVisible.get()) {
                    KeyboardUtils.toggleKeyboardVisibility(getApplicationContext());
                }

                SnackCreator.showSnack(Register.this, "Yeni bir kod gönderildi.");
                dialog.dismiss();
                resendVerificationCode(phone, token);
            });

            neutral.setOnClickListener(view -> dialog.dismiss());
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (width / 1.1), height / 3);
    }

    private void resendVerificationCode(String phoneNumber,
            PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                cb,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor e = pref.edit();

                        e.putString("birth", birthday);
                        e.apply();
                        fs.addUserData(name, birthday, phone);
                        Log.v("FirebaseMsg", "signInWithCredential:success");
                        startActivity(new Intent(this, MainActivity.class));

                        /*FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();*/
                    }
                    else {
                        SnackCreator.showSnack(this, FireBaseExceptionHandler.handle(task.getException()));

                        Log.v("FirebaseMsg", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.v("FirebaseMsg", "çözülmesi gereken bir sorun var. Aşağıya sorunu bıraktım:\n ");
                            task.getException().printStackTrace();
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setConstantToEtPhone() {
        etPhone.setText("+90");
        Selection.setSelection(etPhone.getText(), etPhone.getText().length());
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().startsWith("+90")) {
                    etPhone.setText("+90");
                    Selection.setSelection(etPhone.getText(), etPhone.getText().length());
                }
            }
        });
    }

    //Cancel back button default behaviour
    @Override
    public void onBackPressed() {}
}
