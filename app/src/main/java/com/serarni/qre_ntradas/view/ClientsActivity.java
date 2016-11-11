package com.serarni.qre_ntradas.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.serarni.aboutdialog.AboutDialog;
import com.serarni.common.TaskCallback;
import com.serarni.common.TaskError;
import com.serarni.common.ViewHelper;
import com.serarni.qre_ntradas.BuildConfig;
import com.serarni.qre_ntradas.R;
import com.serarni.qre_ntradas.model.AppPreferences;
import com.serarni.qre_ntradas.model.Clients;
import com.serarni.qre_ntradas.model.DataManager;
import com.serarni.qre_ntradas.model.Events;
import com.serarni.qre_ntradas.model.TicketValidationResult;

import java.util.ArrayList;
import java.util.List;


public class ClientsActivity extends AppCompatActivity{

    Bundle extras;
    ListView lista;
    TextView vacio;
    TextView ProgressText;
    ProgressBar PB;
    EditText inputSearch;
    ImageButton SearchOptions;

    String json;
    String Criterio = "";
    int TotalClientes;
    int TotalValidados;
    int textlength = 0;
    List<Clients> ClientSearch = new ArrayList<>();
    private ProgressDialog dialog;
    AppPreferences appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_list);

        lista = (ListView) findViewById(android.R.id.list);
        vacio = (TextView) findViewById(android.R.id.empty);
        ProgressText = (TextView) findViewById(R.id.ProgressText);
        PB = (ProgressBar) findViewById(R.id.ProgressBar);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        SearchOptions = (ImageButton) findViewById(R.id.SearchOptions);

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
        else {
            extras = getIntent().getExtras();
            appPrefs = AppPreferences.getSingleton(this);
            if (extras != null) {
                json = extras.getString("Event");
                dialog = new ProgressDialog(this);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.please_wait));
                Events event = new Gson().fromJson(json, Events.class);
                String sID = event.getEventID();
                SearchOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String[] Options = new String[3];
                        Options[0]="Validados";
                        Options[1]="No Validados";
                        Options[2]="Todos";
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClientsActivity.this);
                        builder.setTitle(R.string.set_search_key);
                        builder.setItems(Options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        Intent i0 = new Intent(ClientsActivity.this,ClientsActivity.class);
                                        i0.putExtra("Criterio",Options[0]);
                                        i0.putExtra("Event", json);
                                        startActivity(i0);
                                        break;
                                    case 1:
                                        Intent i1 = new Intent(ClientsActivity.this,ClientsActivity.class);
                                        i1.putExtra("Criterio",Options[1]);
                                        i1.putExtra("Event", json);
                                        startActivity(i1);
                                        break;
                                    case 2:
                                        Intent i2 = new Intent(ClientsActivity.this,ClientsActivity.class);
                                        i2.putExtra("Criterio",Options[2]);
                                        i2.putExtra("Event", json);
                                        startActivity(i2);
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    }
                });
                showClients();
            }
        }
    }

    private void showClients() {
        json = extras.getString("Event");
        Events event = new Gson().fromJson(json, Events.class);
        String sID = event.getEventID();
        final Dialog pDlg = ViewHelper.showLoadingProgressDialog(this, R.string.loading_clients);
        DataManager.getSingleton().getClients(
                sID,
                new TaskCallback<ArrayList<Clients>>() {

                    @Override
                    public void success(@NonNull final ArrayList<Clients> result) {
                        pDlg.show();
                        if (extras.getString("Criterio") != null){
                            Criterio = extras.getString("Criterio");
                        }
                        List<Clients> CheckList = new ArrayList<>();
                        List<Clients> NoCheckList = new ArrayList<>();

                        if (result.size()==0){
                            vacio.setText(R.string.no_clients);
                            ProgressText.setText(R.string.no_progress);
                            PB.setProgress(0);
                        }
                        else {
                            for (int i=0; i<result.size(); i++){
                                if (result.get(i).getClientCheck().equals("1")){
                                    CheckList.add(result.get(i));
                                }
                                else {
                                    NoCheckList.add(result.get(i));
                                }
                            }
                            TotalValidados=CheckList.size();
                            TotalClientes=result.size();
                            switch (Criterio){
                                case "Validados":
                                    ClientAdapter adapter0 = new ClientAdapter(getApplicationContext(), R.layout.list_item, CheckList);
                                    lista.setAdapter(adapter0);
                                    if (CheckList.size()<=0){
                                        vacio.setText(R.string.check_empty);
                                    }
                                    Search(CheckList);
                                    break;
                                case "No Validados":
                                    ClientAdapter adapter1 = new ClientAdapter(getApplicationContext(), R.layout.list_item, NoCheckList);
                                    lista.setAdapter(adapter1);
                                    if (NoCheckList.size()<=0){
                                        vacio.setText(R.string.all_checked);
                                    }
                                    Search(NoCheckList);
                                    break;
                                default:
                                    ClientAdapter adapter2 = new ClientAdapter(getApplicationContext(), R.layout.list_item, result);
                                    lista.setAdapter(adapter2);
                                    Search(result);
                                    break;
                            }
                        }
                        ProgressText.setText(String.valueOf(TotalValidados) + " de " + String.valueOf(TotalClientes) + " Validados");
                        if (TotalClientes==0){
                            int progress=0;
                            PB.setProgress(progress);
                        }
                        else{
                            int progress = (TotalValidados * 100) / TotalClientes;
                            PB.setProgress(progress);
                        }

                        pDlg.dismiss();
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        Dialog dlg = new android.app.AlertDialog.Builder(ClientsActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.error)
                                .setMessage(R.string.client_error)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(ClientsActivity.this,EventsActivity.class);
                                        intent.putExtra("Event", json);
                                        intent.putExtra("Criterio",Criterio);
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

    public void Search(final List<Clients> ClientList){
        inputSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Abstract Method of TextWatcher Interface.
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Abstract Method of TextWatcher Interface.
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textlength = inputSearch.getText().length();
                ClientSearch.clear();

                for (int i = 0; i < ClientList.size(); i++) {
                    Clients client = ClientList.get(i);
                    if (textlength>0) {
                        if (textlength <= client.getClientName().length()) {
                            int resultado = client.getClientName().toLowerCase().indexOf(inputSearch.getText().toString().toLowerCase());
                            if (resultado != -1){
                                ClientSearch.add(client);
                            }
                        }
                        if (textlength <= client.getClientDNI().length()) {
                            if (inputSearch.getText().toString().equalsIgnoreCase((String) client.getClientDNI().subSequence(0, textlength))) {
                                ClientSearch.add(client);
                            }
                        }
                    }
                    else{
                        ClientSearch.add(client);
                    }
                }
                if(ClientSearch.size()==0){
                    if (textlength<=0) {
                        vacio.setText(R.string.no_clients);
                    }
                    else{
                        vacio.setText("No hay coincidencias para '"+ inputSearch.getText()+"'");
                    }
                }
                else{
                    vacio.setText("");
                }
                ClientAdapter adapter = new ClientAdapter(getApplicationContext(), R.layout.list_item, ClientSearch);
                lista.setAdapter(adapter);
            }
        });
    }

    public class ClientAdapter extends ArrayAdapter {

        private List<Clients> ClientList;
        private int resource;
        private LayoutInflater inflater;
        public ClientAdapter(Context context, int resource, List<Clients> objects) {
            super(context, resource, objects);
            ClientList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.StackContent = (RelativeLayout) convertView.findViewById(R.id.StackContent);
                holder.ClientName = (TextView)convertView.findViewById(R.id.ClientName);
                holder.ClientDni = (TextView)convertView.findViewById(R.id.ClientDNI);
                holder.ClientPhone = (TextView)convertView.findViewById(R.id.ClientPhone);
                holder.Ticket = (TextView)convertView.findViewById(R.id.TicketCat);
                holder.Validado = (Switch) convertView.findViewById(R.id.checkState);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ClientName.setText(ClientList.get(position).getClientName());
            holder.ClientDni.setText("DNI: " + ClientList.get(position).getClientDNI());
            holder.ClientPhone.setText("Tlf: "+String.valueOf(ClientList.get(position).getClientPhone()));
            holder.Ticket.setText(ClientList.get(position).getTCategory());
            if(ClientList.get(position).getClientCheck().equals("1")){
                holder.Validado.setChecked(true);
                holder.StackContent.setBackgroundColor(getResources().getColor(R.color.check_color));
            }
            else{
                holder.Validado.setChecked(false);
                holder.StackContent.setBackgroundColor(getResources().getColor(R.color.font_white));
            }
            holder.Validado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Clients client = ClientList.get(position);
                    final String QR = client.getClientQR();
                    if (client.getClientCheck().equals("0")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClientsActivity.this);
                        builder.setTitle(R.string.another_client);
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        holder.Validado.setChecked(false);
                                    }
                                });
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        validateClient(QR);
                                    }
                                });
                        builder.create().show();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClientsActivity.this);
                        builder.setTitle(R.string.reactivate_client);
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        holder.Validado.setChecked(true);
                                    }
                                });
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reactivateClient(QR);
                                    }
                                });
                        builder.create().show();
                    }
                }
            });

            return convertView;
        }

        class ViewHolder{
            private TextView ClientName;
            private TextView ClientDni;
            private TextView ClientPhone;
            private TextView Ticket;
            private Switch Validado;
            private RelativeLayout StackContent;

        }

    }

    private void redirectToLoginActivity(boolean bAutoLogin) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_AUTO_LOGIN, bAutoLogin);
        startActivity(intent);
        this.finish();
    }

    private void validateClient (String sUrlTicket) {

        final Dialog pDlg = ViewHelper.showLoadingProgressDialog(this, R.string.checking);
        DataManager.getSingleton().validateClient(
                sUrlTicket,
                new TaskCallback<TicketValidationResult>() {
                    @Override
                    public void success(@NonNull TicketValidationResult result) {
                        json = extras.getString("Event");
                        Dialog dlg = new android.app.AlertDialog.Builder(ClientsActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.validation_correct)
                                .setMessage(R.string.client_validated)
                                .setCancelable(false)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(ClientsActivity.this, ClientsActivity.class);
                                        intent.putExtra("Event",json);
                                        startActivity(intent);
                                    }
                                })
                                .create();
                        dlg.show();
                    }

                    @Override
                    public void error(@NonNull TaskError e) {

                        Dialog dlg = new android.app.AlertDialog.Builder(ClientsActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.validation_incorrect)
                                .setMessage(e.getError())
                                .setCancelable(true)
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

    private void reactivateClient (String sUrlTicket) {

        final Dialog pDlg = ViewHelper.showLoadingProgressDialog(this, "Comprobando");
        DataManager.getSingleton().reactivateClient(
                sUrlTicket,
                new TaskCallback<TicketValidationResult>() {
                    @Override
                    public void success(@NonNull TicketValidationResult result) {
                        json = extras.getString("Event");
                        Dialog dlg = new android.app.AlertDialog.Builder(ClientsActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.reactivation_ok)
                                .setMessage(R.string.reactivation_ok_msg)
                                .setCancelable(false)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(ClientsActivity.this, ClientsActivity.class);
                                        intent.putExtra("Event",json);
                                        startActivity(intent);
                                    }
                                })
                                .create();
                        dlg.show();
                    }

                    @Override
                    public void error(@NonNull TaskError e) {
                        Dialog dlg = new android.app.AlertDialog.Builder(ClientsActivity.this, R.style.AppTheme_AlertDialogTheme_validationResult)
                                .setTitle(R.string.validation_incorrect)
                                .setMessage(e.getError())
                                .setCancelable(true)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                json = extras.getString("Event");
                Intent intent = new Intent (this, EventDetailActivity.class);
                intent.putExtra("Event",json);
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
            case R.id.activateTicket:{
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientsActivity.this);
                builder.setTitle(R.string.another_ticket);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ClientsActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                builder.create().show();

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}