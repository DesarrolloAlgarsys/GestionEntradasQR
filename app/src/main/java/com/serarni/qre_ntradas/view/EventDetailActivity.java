package com.serarni.qre_ntradas.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.serarni.aboutdialog.AboutDialog;
import com.serarni.qre_ntradas.BuildConfig;
import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.model.DataManager;
import com.serarni.qre_ntradas.model.Events;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView EventImage;
    private TextView EventName;
    private TextView EventDate;
    private TextView EventDescription;
    private TextView ToolbarTittle;
    private ProgressBar progressBar;
    private Button BTN_Asistentes;
    Bundle extras;
    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setUpUIViews();

        DataManager.getSingleton().initialize(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        if (!DataManager.getSingleton().isUserAuthenticated()){
            redirectToLoginActivity(true);
        }
        else {
            extras = getIntent().getExtras();
            if (extras != null) {
                json = extras.getString("Event");
                Events event = new Gson().fromJson(json, Events.class);

                if(EventImage != null) {
                    String URLImage = "http://www.gestionentradas.com/gestion/app/web/upload/events/"
                            +event.getEventImage()+".jpg";
                    Picasso.with(EventDetailActivity.this).load(URLImage).into(EventImage);
                }

                EventName.setText(event.getEventName());
                EventDate.setText(event.getEventDate());
                EventDescription.setText(event.getEventDescription());
                ToolbarTittle.setText(event.getEventName());
                BTN_Asistentes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventDetailActivity.this, ClientsActivity.class);
                        intent.putExtra("Event", json);
                        startActivity(intent);
                    }
                });

            }
        }
    }

    private void redirectToLoginActivity(boolean bAutoLogin) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_AUTO_LOGIN, bAutoLogin);
        startActivity(intent);
        this.finish();
    }

    private void setUpUIViews() {
        EventImage = (ImageView)findViewById(R.id.event_image);
        EventName = (TextView)findViewById(R.id.eventname);
        EventDate = (TextView)findViewById(R.id.eventdate);
        EventDescription = (TextView)findViewById(R.id.eventdescription);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ToolbarTittle = (TextView)findViewById(R.id.ToolbarTittle);
        BTN_Asistentes = (Button) findViewById(R.id.btn_asistentes);
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
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent (this, EventsActivity.class);
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


}