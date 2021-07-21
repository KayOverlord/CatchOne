package com.ttz.kmystro.catchone;


import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ContributionActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private static final String TAG ="map_status" ;
    private LatLng sa;
    ImageButton btnicon;
    Button infobtn;
    Button buttoncancel;
    Button buttondone;
    EditText txtinfo;
    EditText txtprice;
    String placename;
    LatLng placecoo;
    ProgressBar progressBar;
    Marker marker;
    LinearLayout linearLayout;
    private LatLng markerpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribution);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String intent = getIntent().getStringExtra("apikey");
        Places.initialize(getApplicationContext(),intent);
        Places.createClient(this);

        infobtn= findViewById(R.id.buttoninfo);
        infobtn.setOnClickListener(v -> viewdialog());
        linearLayout = findViewById(R.id.linearLayoutInfo);
        txtprice = findViewById(R.id.editTextRprice);
    }

    private void viewdialog() {
        final Dialog dialog = new Dialog(this) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                dismiss();
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialoglayout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        buttoncancel = dialog.findViewById(R.id.buttonCancel);
        buttondone = dialog.findViewById(R.id.buttonDoneM);
        txtinfo = dialog.findViewById(R.id.editTextinfo2);

        progressBar = dialog.findViewById(R.id.progressBar);

        buttoncancel.setOnClickListener(v -> dialog.cancel());

        buttondone.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String mT = txtinfo.getText().toString().trim();
            String tprice = String.valueOf(txtprice.getText());

        if(!mT.isEmpty() && mT.length()>10 && !tprice.isEmpty() ){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();


            Map<String,Object> info = new HashMap<>();
            info.put("Address",placename);
            info.put("Lati",markerpos.latitude);
            info.put("Long",markerpos.longitude);
            info.put("Dest",placecoo);
            info.put("Info",mT);
            info.put("Price",tprice);
            info.put("ID",auth.getCurrentUser().getUid());
            info.put("timestamp", FieldValue.serverTimestamp());
            txtinfo.setText("");


            db.collection("Spots").document().set(info).addOnCompleteListener(task -> {
                Toast.makeText(dialog.getContext(),"Done",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContributionActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                progressBar.setVisibility(View.INVISIBLE);
                dialog.cancel();
                dialog.dismiss();
            });
        }else {

            txtinfo.setError(" check Text if its not empty or too short");
            txtinfo.requestFocus();
            txtprice.setError("check if its not empty");
            txtprice.requestFocus();
        }

        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(true);
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        sa = new LatLng(-25.7275706, 28.1773415);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sa,6));


        btnicon = findViewById(R.id.imageButtonIcon);
        btnicon.setOnClickListener(v -> {
            if(marker == null) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(mMap.getCameraPosition().target)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxis))
                        .draggable(true));
                linearLayout.setVisibility(View.VISIBLE);
                markerpos = mMap.getCameraPosition().target;

            }else {
                mMap.clear();
                marker = mMap.addMarker(new MarkerOptions()
                        .position(mMap.getCameraPosition().target)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxis))
                        .draggable(true));
                linearLayout.setVisibility(View.VISIBLE);
                markerpos = mMap.getCameraPosition().target;
            }

        });

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment3);
        assert autocompleteFragment != null;
        autocompleteFragment.setCountry("za");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+","+place.getLatLng());
                placename = place.getName();
                placecoo = place.getLatLng();
                infobtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                markerpos = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                markerpos = marker.getPosition();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                markerpos = marker.getPosition();
            }
        });
    }

}
