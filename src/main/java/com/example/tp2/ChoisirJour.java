package com.example.tp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.TimeZone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class ChoisirJour extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolbar;
    public StringBuilder cour = new StringBuilder();
    public  StringBuilder date = new StringBuilder();
    public  StringBuilder salleDeCour = new StringBuilder();
    public  StringBuilder professeur = new StringBuilder();
    public String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_calendar);

        listView = (ListView)findViewById(R.id.lvDayCalendar);
        toolbar = (Toolbar)findViewById(R.id.ToolbarDayCalendar);

        Intent intent = getIntent();
        String actuelle = intent.getStringExtra("calday");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jour "+actuelle);


        if (!isConnected()) {
            return;
        }
        new ChoisirJour.FetchTask().execute("https://edt-api.univ-avignon.fr/app.php/api/exportAgenda/diplome/2-L3IN");
    }

    private void setupListView(){

        if (cour.length() > 0 && date.length() > 0){
            String buff1[] = cour.toString().split("\n");
            String buff2[] = date.toString().split("\n");
            String buff3[] = salleDeCour.toString().split("\n");
            String buff4[] = professeur.toString().split("\n");

            SimpleAdapter simpleAdapter = new SimpleAdapter(ChoisirJour.this,buff1,buff2,buff3,buff4);
            listView.setAdapter(simpleAdapter);
        }
        else{
            String goodDay = "C'est un bon jour !\n";
            String gooddayTime = "Parce que tu n'as pas cour \n";
            String goodprof = "\n";
            String goodsalle = "\n";
            String good[] = goodDay.split("\n");
            String good2[] = gooddayTime.split("\n");
            String good3[] = goodprof.split("\n");
            String good4[] = goodsalle.split("\n");

            SimpleAdapter simpleAdapter = new SimpleAdapter(ChoisirJour.this,good,good2,good3,good4);
            listView.setAdapter(simpleAdapter);
        }
    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class FetchTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ChoisirJour.this);
            dialog.setMessage("Chargement en cour, patientez svp :)");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpURLConnection conn = null;

            String stringUrl = strings[0];
            try {
                URL url = new URL(stringUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int response = conn.getResponseCode();
                if (response != 200) {
                    return null;
                }

                inputStream = conn.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }

                return new String(buffer);
            } catch (IOException e) {
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if (s == null) {
                str = "erreur";

            } else {
                func(s);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }


        private void func(String s){

            try {

                StringReader sin = new StringReader(s);

                CalendarBuilder builder = new CalendarBuilder();

                Calendar calendar = builder.build(sin);

                String tab[];

                HashMap<Date, String> hashmapDay = new HashMap<Date, String>();

                Intent intent = getIntent();
                String actuelle = intent.getStringExtra("calday");

                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();)
                {
                    Component component = (Component) i.next();

                    String icalvalue = component.getProperty("DTSTART").getValue();
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyyMMdd'T'HHmmss'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    DateFormat dateFormato = new SimpleDateFormat("dd MM yyyy");

                    String der = "";

                    Date date = null;
                    try {
                        date = dateFormat.parse(icalvalue);
                        der = dateFormato.format(date);

                    }
                     catch (ParseException e) 
                    {
                        e.printStackTrace();
                    }


                    if(Objects.equals(der, actuelle))
                    {
                        if(LaunchActivity.sharedPreferences.contains(LaunchActivity.SEL_Group))
                        {
                            String pref = LaunchActivity.sharedPreferences.getString(LaunchActivity.SEL_Group, null);
                            tab = component.getProperty("DESCRIPTION").getValue().split("\n");
                            if (!tab[0].startsWith("Annulation") && tab.length > 2 && (tab[2].contains(pref) || tab[2].startsWith("Promotion")))
                            {
                                hashmapDay.put(date,tab[0].toLowerCase()+"\n"+tab[1]+"\n"+tab[3]);
                            }
                        }
                    }
                }


                Map<Date, String> mapDay = new TreeMap<Date, String>(hashmapDay);
                Set setDay = mapDay.entrySet();
                Iterator itDay = setDay.iterator();
                while (itDay.hasNext())
                {
                    Map.Entry entryDay = (Map.Entry)itDay.next();
                    String info = entryDay.getValue().toString();
                    String[] tabInfo = info.split("\n");
                    cour.append(tabInfo[0]+"\n");
                    date.append(entryDay.getKey().toString()+"\n");
                    salleDeCour.append(tabInfo[1]+"\n");
                    professeur.append(tabInfo[2]+"\n");
                }

                setupListView();


            } 
            catch(Exception e) 
            {
                e.printStackTrace();
            }
        }
    }



    public class SimpleAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater layoutInflater;
        private TextView subject;
        private TextView time;
        private TextView enseignant;
        private TextView salleCour ;
        private String[] salle;
        private String[] cour;
        private String[] date;
        private String[] professeur;
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
