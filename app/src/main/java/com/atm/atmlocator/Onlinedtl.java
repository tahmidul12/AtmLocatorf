package com.atm.atmlocator;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import apiconstant.ApiAtmPic;
import modelClasses.BankModel;

public class Onlinedtl extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    double lat, lng;
    private LatLng atmLatLng;
    private String bname, batmname;
    private boolean detFetchSuc;

    //
    private TextView textv_atmName, textv_bankName, textv_address, textv_city, textv_state, textv_floatbtn;
    private LinearLayout linearv_atm;
    private Cursor cursor;
    private ProgressBar pb_atm;
    //for add
    AdView adView;
    private LinearLayout linearv_add;
    private ImageView imgv_atm;
    //
    private boolean forMyLoc = false;
    private String address, city, state;
    private String lats = null, lngs = null;

    //for showing atm image of bank
    StreetViewPanoramaFragment streetViewPanoramaFragment;
    private int atm_id = 1;
    private boolean street_viewLoaded = false;
    private boolean atmPic_loaded = false;
    private boolean fabAtm_picMode = true;
    private FloatingActionButton fab_atm;
    private Activity activity;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlinedtl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4);

        // init layout components other than map
        fab_atm = (FloatingActionButton) findViewById(R.id.fab_atm);
        fab_atm.setOnClickListener(new CustomOnClickListener());
        try{
        fab_atm.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#ffffff")));
        }catch(Exception e){ e.printStackTrace(); }
        pb_atm = (ProgressBar) findViewById(R.id.pb_atm);
        textv_floatbtn = (TextView) findViewById(R.id.textv_floatbtn);
        textv_atmName = (TextView) findViewById(R.id.textv_atmName);
        textv_bankName = (TextView) findViewById(R.id.textv_bankName);
        textv_address = (TextView) findViewById(R.id.textv_address);
        textv_city = (TextView) findViewById(R.id.textv_city);
        textv_state = (TextView) findViewById(R.id.textv_state);
        //
        linearv_atm = (LinearLayout) findViewById(R.id.linearv_atm);
        //for adview
        adView = (AdView) findViewById(R.id.adView);
        linearv_add = (LinearLayout) findViewById(R.id.linearv_add);
        imgv_atm = (ImageView) findViewById(R.id.imgv_atm);
        imgv_atm.setImageResource(R.mipmap.defaultimg);
        // receving lat lng  and other details from previous activity
        detFetchSuc = false;
        if(getIntent() != null){
            lat = getIntent().getDoubleExtra("lat", 23.7917399);
            lng = getIntent().getDoubleExtra("lng", 90.4041357);
            bname = getIntent().getStringExtra("bname");
            if(!bname.equalsIgnoreCase("Current Position"))
               batmname = getIntent().getStringExtra("batmname");
            atmLatLng = new LatLng(lat, lng);
            detFetchSuc = true;
        }else{
            Toast.makeText(getApplicationContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        }

        // for users Current location
        if(bname.equalsIgnoreCase("My Location")){
            forMyLoc = true;
            textv_atmName.setText("My Location");
        }else if(bname.equalsIgnoreCase("Current Position")){
             textv_atmName.setText("");
        }

        //setting toolbar title based on userlocMarker clicked or atm marker clicked
        if(forMyLoc || !bname.equalsIgnoreCase("Current Position"))
            getSupportActionBar().setTitle("My Location");
        else
            getSupportActionBar().setTitle("Atm Details");
        //setting data received successfully from pre activity
        if(detFetchSuc && !forMyLoc && !bname.equalsIgnoreCase("Current Position")){
            textv_atmName.setText(batmname);
            textv_bankName.setText(bname);
        }

        //setting the streetview
        streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.mapstreet);
        streetViewPanoramaFragment.getView().setVisibility(View.GONE);
        activity = this;
        context = getApplicationContext();
        // when float buton for street view will be clicked then this line will be executed
        //streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        // cursor loading
        if(!forMyLoc)
            getSupportLoaderManager().initLoader(1, null, this);

        // getting users address city location detals and set to layout
        if(forMyLoc || bname.equalsIgnoreCase("Current Position"))
            getAndSetUserLoc();

        //setting adview
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //adView.setOnClickListener((View.OnClickListener) new CustomAdListener(this));
        adView.setAdListener(new CustomAdListener());

        //


    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        if(atmLatLng != null){
            street_viewLoaded = true;
            streetViewPanorama.setPosition(atmLatLng);
            StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder().bearing(180).build();
            streetViewPanorama.animateTo(camera, 1000);
        }else{
            Toast.makeText(getApplicationContext(), "Sorry could't load Street View!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.d("SHAKIL", "4m onCreateLoader");
        String URL = "content://com.atmlocator.Bank/atms";
        Uri atmsUri = Uri.parse(URL);
        return new android.support.v4.content.CursorLoader(this, atmsUri, null, null, null, "bank");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        addDatatoList();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setStreetViewtoAsync(){
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    private void addDatatoList() {

        String bnames, batmnames, baddress, lati, longi, state, city;
        boolean found = false;
        if (cursor.moveToFirst()) {
            do{
                bnames = cursor.getString(cursor.getColumnIndex(AtmProvider.BANK));
                batmnames = cursor.getString(cursor.getColumnIndex(AtmProvider.ATM_NAME));
                lati = cursor.getString(cursor.getColumnIndex(AtmProvider.LAT));
                longi = cursor.getString(cursor.getColumnIndex(AtmProvider.LONGI));
                baddress = cursor.getString(cursor.getColumnIndex(AtmProvider.ADDRESS));
                city = cursor.getString(cursor.getColumnIndex(AtmProvider.CITY));
                state = cursor.getString(cursor.getColumnIndex(AtmProvider.STATE));
                if(lat == Double.parseDouble(lati) && lng == Double.parseDouble(longi)
                        && bnames.equalsIgnoreCase(bname) && batmnames.equalsIgnoreCase(batmname)) {
                    atm_id = cursor.getInt(cursor.getColumnIndex(AtmProvider.IDS));
                    lats = lati;
                    lngs = longi;
                    Log.d("SHAKIL", "atm id = "+atm_id);
                    found = true;
                    break;
                }
                //listBank.add(new BankModel(bname, batmname, lat, longi, baddress, city, state, null));
                //adapter.notifyDataSetChanged();
            } while (cursor.moveToNext());
            if(found){
                textv_address.setText("Address : "+baddress);
                textv_city.setText("City : "+city);
                textv_state.setText( "State : "+state );
                found = false;
            }
            if(fabAtm_picMode){
                new AtmPicAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            //Log.d("SHAKIL", "yap back button clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAndSetUserLoc(){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.size()>0) {
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
        }else{
            address = "Sorry address not found";
            city = "not found";
            state = "not found";
        }
        textv_atmName.setText("");
        textv_address.setText("Address : "+address);
        textv_city.setText("city : "+city);
        textv_state.setText("state : "+state);
        //
        //if(imgv_atm.getVisibility() == View.VISIBLE || imgv_atm.getVisibility() == View.INVISIBLE)
           //imgv_atm.setVisibility(View.GONE);
    }

    private class CustomOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(fabAtm_picMode) {
                fabAtm_picMode = false;
                imgv_atm.setVisibility(View.GONE);
                textv_floatbtn.setText("show AtmPic");
                streetViewPanoramaFragment.getView().setVisibility(View.VISIBLE);
                // load street view
                if(!street_viewLoaded){
                    setStreetViewtoAsync();
                }
            }
            else{
                fabAtm_picMode = true;
                imgv_atm.setVisibility(View.VISIBLE);
                textv_floatbtn.setText("show StreetView");
                streetViewPanoramaFragment.getView().setVisibility(View.GONE);
                if(!atmPic_loaded){
                    new AtmPicAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }
    }

    private class AtmPicAsync extends AsyncTask<Void, Void, String>{
        Bitmap bitmapAtm = null;
        @Override
        protected void onPreExecute() {
            if(pb_atm != null)
                pb_atm.setVisibility(View.VISIBLE);
            Log.d("SHAKIL", "progressbar should be visible");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            String url = ApiAtmPic.API_ATM_PIC + Integer.toString(atm_id);
            Log.d("SHAKIL", "image url = "+url);
            try {
                InputStream in = new URL(url).openStream();
                bitmapAtm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(pb_atm != null)
                pb_atm.setVisibility(View.GONE);
            if(bitmapAtm != null){
                atmPic_loaded = true;
                imgv_atm.setImageBitmap(bitmapAtm);
            }else{
                imgv_atm.setImageResource(R.mipmap.defaultimg);
            }
            super.onPostExecute(s);
        }
    }

    private class CustomAdListener extends com.google.android.gms.ads.AdListener {
        @Override
        public void onAdLoaded() {
            if(linearv_add != null && linearv_add.getVisibility() == View.GONE){
                linearv_add.setVisibility(View.VISIBLE);
            }
            Log.d("SHAKIL", "yap add loaded");
            super.onAdLoaded();
        }

        @Override
        public void onAdOpened() {
            Log.d("SHAKIL", "yap add opened");
            super.onAdOpened();
        }

        @Override
        public void onAdClosed() {
            if(linearv_add.getVisibility() == View.VISIBLE){
                //linearv_add.setVisibility(View.GONE);
            }
            Log.d("SHAKIL", "yap add closed");
            super.onAdClosed();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d("SHAKIL", "yap add fail to load");
            super.onAdFailedToLoad(i);
        }

        @Override
        public void onAdLeftApplication() {
            Log.d("SHAKIL", "yap add left application");
            super.onAdLeftApplication();
        }
    }

    @Override
    protected void onDestroy() {
        try{
            cursor.close();
        }catch (Exception e){e.printStackTrace();}
        super.onDestroy();
    }
}
