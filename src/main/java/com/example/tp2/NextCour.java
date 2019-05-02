package com.example.tp2;

import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.TimeZone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.concurrent.TimeUnit;



public class NextCour extends AppCompatActivity {

    private  NotificationManagerCompat notificationManager ;

    private android.support.v7.widget.Toolbar toolbar;
    private TextView cours1;
    private ImageView image1;
    private ImageView refresh ;
    private TextView prochainCours;

    public String str;
    public StringBuilder cour = new StringBuilder();
    public StringBuilder date = new StringBuilder();
    public StringBuilder salle = new StringBuilder();
    public StringBuilder professeur = new StringBuilder();

    public StringBuilder courEval = new StringBuilder();
    public  StringBuilder dateEval = new StringBuilder();
    public  StringBuilder salleEval = new StringBuilder();
    public  StringBuilder professeurEval = new StringBuilder();

    private String space = "       ";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        notificationManager = NotificationManagerCompat.from(this) ;

        toolbar = (Toolbar) findViewById(R.id.Toolbar1);
        cours1 = (TextView) findViewById(R.id.textView5);
        prochainCours = (TextView) findViewById(R.id.textTime);
        refresh = (ImageView) findViewById(R.id.refresh);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(space + LaunchActivity.sharedPreferences.getString(LaunchActivity.SEL_Group, null));

        ImageView option = (ImageView) findViewById(R.id.option);

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isConnected())
                {
                    Toast.makeText(getApplicationContext(),"Error : pas de connection",Toast.LENGTH_LONG).show();
                    charger();
                    return ;
                }
                else {
                    new FetchTask().execute("https://accueil-ent2.univ-avignon.fr/edt/exportAgendaUrl?codeDip=2-L3IN");
                }
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intento = new Intent(NextCour.this, option.class);
                Bundle bundle = new Bundle();
                bundle.putString("cour", cour.toString());
                bundle.putString("date", date.toString());
                bundle.putString("salle", salle.toString());
                bundle.putString("professeur", professeur.toString());
                intento.putExtra("bundle", bundle);

                Bundle bundle1 = new Bundle();
                bundle1.putString("cour", courEval.toString());
                bundle1.putString("date", dateEval.toString());
                bundle1.putString("salle", salleEval.toString());
                bundle1.putString("professeur", professeurEval.toString());
                intento.putExtra("bundle1", bundle1);
                startActivity(intento);
            }
        });

        if(isConnected()) {
            new FetchTask().execute("https://accueil-ent2.univ-avignon.fr/edt/exportAgendaUrl?codeDip=2-L3IN");
        }
        else {
            charger();
            return;
        }
    }

    public void alert()
    {
        String[] matiere = courEval.toString().split("\n") ;
        String[] date = dateEval.toString().split("\n") ;
        String[] lieux = professeurEval.toString().split("\n") ;
        Date zob = new Date() ;
        for(int i = 0 ; i < date.length ; i++)
        {
            Date tmp = new Date(date[i]) ;
            long x = getDateDiff(tmp, zob, TimeUnit.MINUTES) ;
            Long nombreHeure = (x / 60)*-1;
            if(x < (24*7))
            {
                RemoteViews c = new RemoteViews(getPackageName(), R.layout.notification) ;
                c.setTextViewText(R.id.text, matiere[i]+" le "+date[i]+ " \n en "+lieux[i]);

                Notification notification = new NotificationCompat.Builder(this, AppTest.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setCustomContentView(c)
                        .build() ;

                notificationManager.notify(i,notification);
            }
        }


    }

    private void updateListView(String ajout, String ajout2,int etat,String prochainCours)
    {
        String tab[] = ajout.split(" - ue ");
        String tab2[] = ajout2.split(" - ue ");
        cours1.setText(tab[1]);
        this.prochainCours.setText(prochainCours);

        if(etat ==1){
            image1.setImageResource(R.drawable.attention);
        }

        alert();
        sauvegarde();
    }

    public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit)
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
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
            dialog = new ProgressDialog(NextCour.this);
            dialog.setMessage("Chargement en cour, patientez svp :)");
            dialog.setCancelable(false);
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

                /**
                 * Si le site ne répond pas on arrete
                 */
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
            } catch (IOException e)
            {
                return null;
            }
            finally
            {
                if (conn != null)
                {
                    conn.disconnect();
                }

                if(inputStream != null)
                {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
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
                updateListView(str,str,0,str);
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

                StringBuilder az = new StringBuilder();
                String tab[];

                HashMap<Date, String> hashmap = new HashMap<Date, String>();
                HashMap<Date, String> hashmapDay = new HashMap<Date, String>();
                HashMap<Date, String> hashmapEval = new HashMap<Date, String>();



                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();)
                {
                    Component component = (Component) i.next();

                    String icalvalue = component.getProperty("DTSTART").getValue();
                    String icalvalue2 = component.getProperty("DTEND").getValue();
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyyMMdd'T'HHmmss'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date actuelle = new Date();

                    DateFormat dateFormato = new SimpleDateFormat("dd MM yyyy");
                    String dat = dateFormato.format(actuelle);
                    String der = "";
                    String der2 = "";

                    Date date = null;
                    Date date2 = null;
                    try {
                        date = dateFormat.parse(icalvalue);
                        der = dateFormato.format(date);

                        date2 = dateFormat.parse(icalvalue2);
                        der2 = dateFormato.format(date2);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date!= null) {
                        long ecart = getDateDiff(date, actuelle, TimeUnit.MINUTES);

                        if (date != null && date.after(actuelle) || (ecart < 16 && ecart > 0)) {
                            if (LaunchActivity.sharedPreferences.contains(LaunchActivity.SEL_Group)) {
                                String pref = LaunchActivity.sharedPreferences.getString(LaunchActivity.SEL_Group, null);

                                tab = component.getProperty("DESCRIPTION").getValue().split("\n");
                                if (!tab[0].startsWith("Annulation") && tab.length > 2 && (tab[2].contains(pref) || tab[2].startsWith("Promotion"))) {
                                    hashmap.put(date, tab[0].toLowerCase() + "\n" + tab[1] + "\n" + tab[3]);
                                    if (tab[4].contains("Evaluation")) {
                                        hashmapEval.put(date, tab[0].toLowerCase()+"\n"+tab[1]+"\n"+tab[3]);
                                    }
                                }
                            }

                        }
                    }


                    if(Objects.equals(der, dat)) // si le meme jours
                    {
                        if (LaunchActivity.sharedPreferences.contains(LaunchActivity.SEL_Group))
                        {
                            String pref = LaunchActivity.sharedPreferences.getString(LaunchActivity.SEL_Group, null);
                            tab = component.getProperty("DESCRIPTION").getValue().split("\n");
                            if (!tab[0].startsWith("Annulation") && tab.length > 2 && (tab[2].contains(pref) || tab[2].startsWith("Promotion")))
                            {
                                hashmapDay.put(date, tab[0].toLowerCase()+"\n"+tab[1]+"\n"+tab[3]);
                            }
                        }
                    }
                }

                if(hashmapDay.isEmpty()){
                    cour.append("vide");
                }else {
                    Map<Date, String> mapDay = new TreeMap<Date, String>(hashmapDay);
                    Set setDay = mapDay.entrySet();
                    Iterator itDay = setDay.iterator();
                    while (itDay.hasNext()) {
                        Map.Entry entryDay = (Map.Entry) itDay.next();
                        String info = entryDay.getValue().toString();
                        String[] tabInfo = info.split("\n");
                        cour.append(tabInfo[0] + "\n");
                        date.append(entryDay.getKey().toString() + "\n");
                        salle.append(tabInfo[1] + "\n");
                        professeur.append(tabInfo[2] + "\n");
                    }
                }

                courEval.delete(0, courEval.length()) ;
                dateEval.delete(0, dateEval.length()) ;
                salleEval.delete(0, salleEval.length()) ;
                professeurEval.delete(0, professeurEval.length()) ;
                Map<Date, String> mapEval = new TreeMap<Date, String>(hashmapEval);
                Set setEval = mapEval.entrySet();
                Iterator itEval = setEval.iterator();
                while (itEval.hasNext()){
                    Map.Entry entryEval = (Map.Entry)itEval.next();
                    String info = entryEval.getValue().toString();
                    String[] tabInfo = info.split("\n");
                    courEval.append(tabInfo[0]+"\n");
                    dateEval.append(entryEval.getKey().toString()+"\n");
                    salleEval.append(tabInfo[1]+"\n");
                    professeurEval.append(tabInfo[2]+"\n");
                }


                Date hactuelle = new Date();

                Map<Date, String> map = new TreeMap<Date, String>(hashmap);
                Set set = map.entrySet();
                Iterator it = set.iterator();
                Map.Entry entry = (Map.Entry)it.next(); // on prend le premier

                java.util.Date mydate = (java.util.Date) entry.getKey();  // prend la date
                String text = "";
                int etat = 0;


                Long diff = getDateDiff(mydate,hactuelle,TimeUnit.MINUTES); // < 15 et > 0

                if(diff < 16 )
                {
                    text = entry.getValue().toString()+"\n"+entry.getKey().toString();  //prochain cours
                    if(diff >0){
                        etat = 1;
                    }
                }else{ // on prend le suivant
                    entry = (Map.Entry)it.next();
                    mydate = (java.util.Date) entry.getKey();
                    text = entry.getValue().toString()+"\n"+entry.getKey().toString();  //prochain cours
                }

                entry = (Map.Entry)it.next();

                String text2 = entry.getValue().toString()+"\n"+entry.getKey().toString();   // 2 eme cours
                java.util.Date mydate2 = (java.util.Date) entry.getKey();

                Long nombreHeure = (diff / 60)*-1;
                diff %=60;
                diff*=-1;

                String prochainCours = "LE PROCHAIN COURS SERA DANS "+nombreHeure.toString()+"H "+diff.toString()+" MIN";

                updateListView(text,text2,etat,prochainCours);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sauvegarde() {

        File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File nextCour = new File(chemin, "NextCour.txt");
        File dayCour = new File(chemin, "DayCour.txt") ;
        File evaluation = new File(chemin, "Evaluation.txt") ;

        try {

            FileWriter nCour = new FileWriter(nextCour);
            nCour.write(cours1.getText().toString());
            nCour.write("\n");

            nCour.close();

            FileWriter dCour = new FileWriter(dayCour) ;
            dCour.write(cour.toString());
            dCour.write(date.toString());
            dCour.write(salle.toString()) ;
            dCour.write(professeur.toString()) ;
            dCour.close();

            FileWriter eval = new FileWriter(evaluation) ;

            eval.write(courEval.toString());
            eval.write(dateEval.toString());
            eval.write(salleEval.toString());
            eval.write(professeurEval.toString());
            eval.close();
        }
        catch(Exception e){}
    }


    private void charger()
    {
        File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File fichier = new File(chemin, "NextCour.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();
        StringBuilder text1 = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fichier));

            String lineCour2 ;

            int cpt = 0 ;

            while ((lineCour2 = br.readLine()) != null) {
                if(cpt < 4) {
                    text.append(lineCour2);
                    text.append("\n") ;
                    cpt++ ;
                }
                else {
                    text1.append(lineCour2);
                    text1.append("\n");
                    cpt++;
                }
            }

            br.close();
        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        cours1 = (TextView)findViewById(R.id.textView5) ;
        cours1.setText(text.toString());

    }
}
