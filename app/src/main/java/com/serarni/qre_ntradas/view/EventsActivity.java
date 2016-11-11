package com.serarni.qre_ntradas.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.serarni.aboutdialog.AboutDialog;
import com.serarni.common.TaskCallback;
import com.serarni.common.TaskError;
import com.serarni.common.ViewHelper;
import com.serarni.qre_ntradas.BuildConfig;
import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.model.AppPreferences;
import com.serarni.qre_ntradas.model.DataManager;
import com.serarni.qre_ntradas.model.Events;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class EventsActivity extends AppCompatActivity {

    ListView lista;
    TextView vacio;
    AppPreferences appPrefs;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);
        lista = (ListView) findViewById(android.R.id.list);
        vacio = (TextView) findViewById(android.R.id.empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DataManager.getSingleton().initialize(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        if (!DataManager.getSingleton().isUserAuthenticated()){
            redirectToLoginActivity(true);
        }
        else{
            appPrefs = AppPreferences.getSingleton(this);
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.please_wait));
            showEvents();

        }
    }

    private void showEvents() {
        final Dialog pDlg = ViewHelper.showLoadingProgressDialog(this, R.string.loading_events);
        DataManager.getSingleton().getEvents(
                new TaskCallback<ArrayList<Events>>() {

                    @Override
                    public void success(@NonNull final ArrayList<Events> result) {
                        pDlg.show();

                        if (result.size()==0){
                            vacio.setText("No hay eventos asignados a "+ appPrefs.getLastUserLogin());
                        }
                        else {
                            EventAdapter adapter = new EventAdapter(getApplicationContext(), R.layout.event_item, result);
                            lista.setAdapter(adapter);
                            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Events event = result.get(position);
                                    Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
                                    intent.putExtra("Event", new Gson().toJson(event));
                                    startActivity(intent);
                                }
                            });
                        }

                        pDlg.dismiss();
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        Dialog dlg = new AlertDialog.Builder(EventsActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.error)
                                .setMessage(R.string.event_error)
                                .setCancelable(false)
                                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(EventsActivity.this,EventsActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .create();
                        dlg.show();
                    }

                    @Override
                    public void completed() {
                        pDlg.dismiss();
                    }
                }
        );
    }

    public class EventAdapter extends ArrayAdapter{

        private List<Events> EventList;
        private int resource;
        private LayoutInflater inflater;
        public EventAdapter(Context context, int resource, List<Events> objects) {
            super(context, resource, objects);
            EventList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.eventName = (TextView)convertView.findViewById(R.id.EventName);
                holder.eventDate = (TextView)convertView.findViewById(R.id.EventDate);
                holder.eventImage = (ImageView)convertView.findViewById(R.id.EventImage);
                holder.StackContent = (RelativeLayout)convertView.findViewById(R.id.StackContent);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String URLImage = "http://www.gestionentradas.com/gestion/app/web/upload/events/"
                    +EventList.get(position).getEventImage()+".jpg";
            new LoadImage(holder.eventImage).execute(URLImage);

            holder.eventName.setText(EventList.get(position).getEventName());
            String fecha = EventList.get(position).getEventDate();
            StringTokenizer tokens = new StringTokenizer(fecha, ".");
            String day = tokens.nextToken();
            String month = tokens.nextToken();
            String year = tokens.nextToken();
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            holder.eventDate.setText(fecha);
            if (Integer.parseInt(year)>today.year){
                holder.StackContent.setBackgroundColor(getResources().getColor(R.color.check_color));
            }
            else if(Integer.parseInt(year)==today.year){
                if (Integer.parseInt(month)>(today.month+1)){
                    holder.StackContent.setBackgroundColor(getResources().getColor(R.color.check_color));
                }
                else if(Integer.parseInt(month)==(today.month+1)){
                    if (Integer.parseInt(day)>=today.monthDay){
                        holder.StackContent.setBackgroundColor(getResources().getColor(R.color.check_color));
                    }
                    else{
                        holder.StackContent.setBackgroundColor(getResources().getColor(R.color.font_white));
                    }
                }
                else{
                    holder.StackContent.setBackgroundColor(getResources().getColor(R.color.font_white));
                }
            }
            else{
                holder.StackContent.setBackgroundColor(getResources().getColor(R.color.font_white));
            }

            return convertView;
        }

        class LoadImage extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public LoadImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);

                } catch (Exception e) {
                    //Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    mIcon11 = null;
                }
                return mIcon11;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }
        class ViewHolder{
            private TextView eventName;
            private TextView eventDate;
            private ImageView eventImage;
            private RelativeLayout StackContent;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:{
                // show about dialog
                AboutDialog about = new AboutDialog(
                        this,
                        R.string.app_name,
                        BuildConfig.VERSION_NAME,
                        -1,
                        R.string.app_legal_statement,
                        R.mipmap.ic_launcher);
                about.show();
                return true;
            }
            case R.id.preferences:{
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
                break;
            }
            case R.id.logout:{
                // redirect to login activity
                redirectToLoginActivity(false);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void redirectToLoginActivity(boolean bAutoLogin) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_AUTO_LOGIN, bAutoLogin);
        startActivity(intent);
        this.finish();
    }

}
