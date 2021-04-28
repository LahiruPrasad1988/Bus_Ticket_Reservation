package com.example.busticketreservation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Book_Bus extends AppCompatActivity {
    TextView txtbusNo,txtfrom,txtto,txtDtime,txtAtime,txtAmount,txtfBill;
    EditText txtNoSeat;

    private double baseBill,totBill,numSeat;
    private String from,to,No,bsNo;
    FirebaseAuth frbAuth;
    FirebaseFirestore fStore;
    private String useId ;
    private String frm = "Kottava";
    private String trm = "Matara";
    public static final String TAG = "TAG";
    Context context;
    private String busNo = "NV1998";
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book__bus);

        txtbusNo = findViewById(R.id.busNo);
        txtfrom = findViewById(R.id.from);
        txtto = findViewById(R.id.to);
        txtDtime = findViewById(R.id.dTime);
        txtAtime = findViewById(R.id.aTime);
        txtAmount = findViewById(R.id.baseAmount);
        txtNoSeat = findViewById(R.id.noSeat);
        txtfBill = findViewById(R.id.totBill);




        frbAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        useId = frbAuth.getCurrentUser().getUid();




        Intent intent = getIntent();
        bsNo = intent.getStringExtra("BusNo");
        fStore.collection("Routes").whereEqualTo("Bus_No",bsNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshots : task.getResult()){
                        txtbusNo.setText(documentSnapshots.getString("Bus_No"));

                        txtfrom.setText(documentSnapshots.getString("From"));
                        from = documentSnapshots.getString("From");

                        txtto.setText(documentSnapshots.getString("To"));
                        to = documentSnapshots.getString("To");

                        txtAtime.setText(documentSnapshots.getString("Arrival_Time"));

                        txtDtime.setText(documentSnapshots.getString("Departure_Time"));
                        txtAmount.setText(String.valueOf(documentSnapshots.getDouble("Price")));
                        baseBill = documentSnapshots.getDouble("Price");

                        Toast.makeText(Book_Bus.this, documentSnapshots.getString("Bus_No"), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Book_Bus.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    public void calFinalBill(View view){
        numSeat = Integer.parseInt(txtNoSeat.getText().toString());
        totBill = baseBill*numSeat;
        txtfBill.setText(String.valueOf("LKR "+totBill));
        txtfBill.setVisibility(View.VISIBLE);
    }


    public void AddFinalBill(View view){

        Map<String,Object> fBill = new HashMap<>();
        fBill.put("User_Id",useId);
        fBill.put("Bus_No",bsNo);
        fBill.put("From",from);
        fBill.put("To",to);
        fBill.put("Num_Of_Seats",numSeat);
        fBill.put("Amount",totBill);

        fStore.collection("Final-Bill")
                .add(fBill).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Book_Bus.this, "Booking Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Book_Bus.this, "Booking Faild", Toast.LENGTH_SHORT).show();
            }
        });
    }






}