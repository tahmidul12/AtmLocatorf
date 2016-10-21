package com.atm.atmlocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Offlinedtl extends AppCompatActivity {

    private TextView textv_atmName, textv_bankName, textv_address, textv_city, textv_state;
    private ImageView imgv_atm, imgv_backButton, imgv_def;

    String bname, batmname, baddress, blat, blongi, bstate, bcity;
    int bpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlinedtl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4);
        getSupportActionBar().setTitle("ATM Details");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init layout components
        textv_atmName = (TextView) findViewById(R.id.textv_atmName);
        textv_bankName = (TextView) findViewById(R.id.textv_bankName);
        textv_address = (TextView) findViewById(R.id.textv_address);
        textv_city = (TextView) findViewById(R.id.textv_city);
        textv_state = (TextView) findViewById(R.id.textv_state);
        imgv_backButton = (ImageView) findViewById(R.id.imagev_backButton);
        imgv_def = (ImageView) findViewById(R.id.imgv_defbank);
        imgv_backButton.setOnClickListener(new OnclickListener());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        if(getIntent()!= null){
            Intent intent = getIntent();
            bpos = intent.getIntExtra("bpos", 0);
            bname = intent.getStringExtra("bname");
            batmname = intent.getStringExtra("batmname");
            baddress = intent.getStringExtra("baddress");
            bcity = intent.getStringExtra("bcity");
            bstate = intent.getStringExtra("bstate");
            blat = intent.getStringExtra("blat");
            blongi = intent.getStringExtra("blongi");
        }

        setDatatoLayout();
        setImage();
    }

    private void setDatatoLayout() {
        textv_atmName.setText(batmname);
        textv_bankName.setText(bname);
        textv_address.setText("Address : "+baddress);
        textv_city.setText("City : "+bcity);
        textv_state.setText("State : "+bstate);
    }

    private void setImage(){
        if(bname.equalsIgnoreCase("DBBL")) {
            imgv_def.setImageResource(R.mipmap.bg_dbbl);
        }else if(bname.equalsIgnoreCase("Brac Bank")) {
            imgv_def.setImageResource(R.mipmap.bg_brac);
        }else if(bname.equalsIgnoreCase("AB Bank")) {
            imgv_def.setImageResource(R.mipmap.bg_ab);
        }else if(bname.equalsIgnoreCase("City Bank")) {
            imgv_def.setImageResource(R.mipmap.bg_city);
        }else if(bname.equalsIgnoreCase("Exim Bank")) {
            imgv_def.setImageResource(R.mipmap.bg_exim);
        }else if(bname.equalsIgnoreCase("HSBC")) {
            imgv_def.setImageResource(R.mipmap.bg_hsbc);
        }else if(bname.equalsIgnoreCase("IFIC")){
            imgv_def.setImageResource(R.mipmap.bg_ific);
        }else if(bname.equalsIgnoreCase("One Bank")){
            imgv_def.setImageResource(R.mipmap.bg_one);
        }else if(bname.equalsIgnoreCase("Premier Bank")){
            imgv_def.setImageResource(R.mipmap.bg_premier);
        }else if(bname.equalsIgnoreCase("Prime Bank")){
            imgv_def.setImageResource(R.mipmap.bg_prime);
        }else if(bname.equalsIgnoreCase("SCB")) {
            imgv_def.setImageResource(R.mipmap.bg_scb);
        }else if(bname.equalsIgnoreCase("South-East Bank")) {
            imgv_def.setImageResource(R.mipmap.bg_scb);
        }else if(bname.equalsIgnoreCase("UCBL")) {
            imgv_def.setImageResource(R.mipmap.bg_ucb);
        }else if(bname.equalsIgnoreCase("Dhaka Bank")) {
            imgv_def.setImageResource(R.mipmap.bg_dhaka);
        }else if(bname.equalsIgnoreCase("EBL")) {
            imgv_def.setImageResource(R.mipmap.bg_ebl);
        }else if(bname.equalsIgnoreCase("Bank Asia")) {
            imgv_def.setImageResource(R.mipmap.bg_asia);
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            Log.d("SHAKIL", "yap back button clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.d("SHAKIL", "BOTAO HOME");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class OnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("listViewPos", bpos);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
