package com.example.tp2;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

public class LaunchActivity extends AppCompatActivity
{
    private Toolbar toolbar ;
    public static SharedPreferences sharedPreferences;
    public static String SEL_Group;
    public static final String PREFS = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        if (!sharedPreferences.contains(SEL_Group)) {
            setContentView(R.layout.main_page);
            toolbar = (Toolbar) findViewById(R.id.toolbar1);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Choix du groupe");


            ImageView g1 = (ImageView) findViewById(R.id.groupe1);

            g1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putString(SEL_Group, "L3INFO_TD1").apply();
                    Intent intent = new Intent(LaunchActivity.this, NextCour.class);
                    startActivity(intent);
                    finish();
                }
            });

            ImageView g2 = (ImageView) findViewById(R.id.groupe2);
            g2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putString(SEL_Group, "L3INFO_TD2").apply();
                    Intent intent = new Intent(LaunchActivity.this, NextCour.class);
                    startActivity(intent);
                    finish();
                }
            });

            ImageView g3 = (ImageView) findViewById(R.id.groupe3);
            g3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putString(SEL_Group, "L3INFO_TD3").apply();
                    Intent intent = new Intent(LaunchActivity.this, NextCour.class);
                    startActivity(intent);
                    finish();
                }
            });

            ImageView g4 = (ImageView) findViewById(R.id.groupe4);
            g4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putString(SEL_Group, "L3INFO_TD4").apply();
                    Intent intent = new Intent(LaunchActivity.this, NextCour.class);
                    startActivity(intent);
                    finish();
                }
            });

            ImageView g5 = (ImageView) findViewById(R.id.groupe5);
            g5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putString(SEL_Group, "L3INFO_TD5_ALT").apply();
                    Intent intent = new Intent(LaunchActivity.this, NextCour.class);
                    startActivity(intent);
                    finish();
                }
            });

            ImageView g6 = (ImageView) findViewById(R.id.groupe6);
            g6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putString(SEL_Group, "L3INFO_TD6_ALT").apply();
                    Intent intent = new Intent(LaunchActivity.this, NextCour.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            startActivity(new Intent(LaunchActivity.this, NextCour.class));
            finish();
        }
    }
}
