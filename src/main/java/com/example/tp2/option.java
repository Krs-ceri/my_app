package com.example.tp2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

public class option extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private java.util.Calendar mCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        Intent intentPrevious = getIntent();
        final Bundle bundlePrevious = intentPrevious.getBundleExtra("bundle");

        ImageView courJournee = (ImageView)findViewById(R.id.courJournee) ;
        courJournee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(Objects.equals(bundlePrevious.getString("cour"), "vide"))
                {
                    Toast.makeText(option.this, "Pas de cour",
                            Toast.LENGTH_LONG).show();

                    return ;
                }
                else {
                    Intent intent = getIntent();
                    Bundle bundle = intent.getBundleExtra("bundle");
                    Intent intento = new Intent(option.this, CourAujourdhui.class);
                    intento.putExtra("bundle", bundle);
                    startActivity(intento);
                }
            }
        });

        ImageView choisirJournee = (ImageView)findViewById(R.id.calendrier) ;
        choisirJournee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentDate = java.util.Calendar.getInstance();
                int year = mCurrentDate.get(java.util.Calendar.YEAR);
                int month = mCurrentDate.get(java.util.Calendar.MONTH);
                int day = mCurrentDate.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(option.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int Selected_year, int Selected_month, int Selected_dayOfMonth)
                    {
                        String ladate = String.format("%02d",Selected_dayOfMonth)+" "+String.format("%02d",Selected_month+1)+" "+String.format("%02d", Selected_year);
                        Intent intent = new Intent(option.this, ChoisirJour.class);
                        intent.putExtra("calday",ladate);
                        startActivity(intent);
                    }
                }, year, month, day);


                mDatePicker.show();
            }
        });

        ImageView evaluation = (ImageView)findViewById(R.id.eval) ;
        evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Bundle bundle = intent.getBundleExtra("bundle1");
                Intent intenti = new Intent(option.this,EvaluationDetail.class);
                intenti.putExtra("bundle",bundle);
                startActivity(intenti);
            }
        });

        ImageView chooseGroupe = (ImageView)findViewById(R.id.groupe) ;
        chooseGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchActivity.sharedPreferences.edit().clear().apply(); ;
                startActivity(new Intent(option.this, LaunchActivity.class));
            }
        });
    }
}
