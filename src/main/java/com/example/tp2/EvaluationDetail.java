package com.example.tp2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EvaluationDetail extends AppCompatActivity {
        private ListView listView;
        private Toolbar toolbar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_day_calendar);

            listView = (ListView)findViewById(R.id.lvDayCalendar);
            toolbar = (Toolbar)findViewById(R.id.ToolbarDayCalendar);

            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Day");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra("bundle");

            if(isConnected()) {
                String[] buff1 = bundle.getString("cour").split("\n");
                String[] buff2 = bundle.getString("date").split("\n");
                String[] buff3 = bundle.getString("salle").split("\n");
                String[] buff4 = bundle.getString("professeur").split("\n");
                SimpleAdapter simpleAdapter = new SimpleAdapter(this, buff1, buff2, buff3, buff4);
                listView.setAdapter(simpleAdapter);
            }
            else {

                File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File fichier = new File(chemin, "Evaluation.txt");

                //Read text from file
                StringBuilder matiere = new StringBuilder();
                StringBuilder enseignant = new StringBuilder();
                StringBuilder salle = new StringBuilder();
                StringBuilder jour = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(fichier));

                    String lineCour2;

                    int cpt = 0;

                    while ((lineCour2 = br.readLine()) != null) {
                        if(lineCour2.startsWith("matière")){
                            matiere.append(lineCour2) ;
                            matiere.append("\n") ;
                        }
                        else if(lineCour2.startsWith("Enseignant"))
                        {
                            enseignant.append(lineCour2) ;
                            enseignant.append("\n") ;
                        }
                        else if(lineCour2.startsWith("Salle"))
                        {
                            salle.append(lineCour2) ;
                            salle.append("\n") ;
                        }
                        else
                        {
                            jour.append(lineCour2) ;
                            jour.append("\n") ;
                        }
                    }
                    br.close();
                } catch (IOException e) {

                }

                String[] cour = matiere.toString().split("\n") ;
                String[] date = jour.toString().split("\n") ;
                String[] prof = enseignant.toString().split("\n") ;
                String[] lieux = salle.toString().split("\n") ;
                SimpleAdapter simpleAdapter = new SimpleAdapter(this, cour, date, prof, lieux) ;
                listView.setAdapter(simpleAdapter);
            }
        }

        private boolean isConnected() {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

     public class SimpleAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater layoutInflater;
        private TextView subject,time , enseignant, salleCour;
        private String[] cour;
        private String[] date;
        private String[] professeur;
        private String[] salle;
        private LetterImageView letterImageView;

        public SimpleAdapter(Context context,String[] cour,String[] date,String[] professeur,String[] salle){
            mContext = context;
            this.cour = cour;
            this.date = date;
            this.professeur = professeur;
            this.salle = salle;
            layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return cour.length;
        }

        @Override
        public Object getItem(int position) {
            return cour[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            if(convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.day_calendar_single_item,null);
            }

            subject = (TextView)convertView.findViewById(R.id.subjects);
            time = (TextView)convertView.findViewById(R.id.time);
            enseignant = (TextView)convertView.findViewById(R.id.enseignant);
            salleCour = (TextView)convertView.findViewById(R.id.salle);

            letterImageView = (LetterImageView)convertView.findViewById(R.id.ivDayCalendar);

            subject.setText(cour[position]);
            time.setText(date[position]);
            enseignant.setText(professeur[position]);
            salleCour.setText(salle[position]);

            letterImageView.setOval(true);
            letterImageView.setLetter(cour[position].charAt(13));

            return convertView;
        }
    }
}
