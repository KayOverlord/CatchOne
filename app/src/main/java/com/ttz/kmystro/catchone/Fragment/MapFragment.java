package com.ttz.kmystro.catchone.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.model.DirectionsResult;
import com.ttz.kmystro.catchone.ContributionActivity;
import com.ttz.kmystro.catchone.LoginActivity;
import com.ttz.kmystro.catchone.ProfileActivity;
import com.ttz.kmystro.catchone.R;
import com.ttz.kmystro.catchone.WelcomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {


    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    public MapFragment() {
        // Required empty public constructor
    }

    private GoogleMap mMap;
    private static final String TAG ="map_status" ;

    DirectionsResult result;
    String fromlocation;
    String tolocation;

    private static final int overview = 0;
    private LatLng pos;
    private Marker marker;
    private Task<QuerySnapshot> dicount;
    private Task<QuerySnapshot> licount;
    private String docid;
    private String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("mykey").document("mapplace").addSnapshotListener((documentSnapshot, e) -> {

            if(documentSnapshot != null&&documentSnapshot.exists()){

                key = documentSnapshot.getString("apikey");
            }
        });

        Button btn = view.findViewById(R.id.buttonclick);
        btn.setOnClickListener(v -> {
            if (InternetConnection.checkConnection(view.getContext())) {
                // Its Available...
                Intent intent = new Intent(view.getContext(), ContributionActivity.class);
                intent.putExtra("apikey",key);
                startActivity(intent);
            } else {
                // Not Available...
                Toast.makeText(view.getContext(),"Check your Internet connection",Toast.LENGTH_LONG).show();
            }


        });

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);


        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(true);

        mMap.setPadding(0,0,0,140);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
             mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(getActivity()).getApplicationContext(), R.raw.style_json));


        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        addHeatMap();
        mMap.setOnMarkerClickListener(this);


        // Add a marker in Sydney and move the camera
        LatLng sa = new LatLng(-25.7275706, 28.1773415);
        //mMap.addMarker(new MarkerOptions().position(sa).title("Marker in South Africa"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sa,5));

       /* AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setCountry("za");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+","+place.getLatLng());
                mMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                fromlocation = place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });*/

       /* AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_to);
        assert autocompleteFragment2 != null;
        autocompleteFragment2.setCountry("za");
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+","+place.getLatLng());
                mMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()), 3000, null);
                tolocation = place.getName();
                DirectionsDetails(fromlocation,tolocation,TravelMode.DRIVING);

            }


            private void DirectionsDetails(String origin, String destination, TravelMode mode) {


                try {
                     result = DirectionsApi.newRequest(getGeoContext())
                            .mode(mode)
                            .origin(origin)
                            .destination(destination).await();
                     addPolyline(result,mMap);
                } catch (ApiException | InterruptedException | IOException e) {
                    e.printStackTrace();

                }

            }

            private void addPolyline(DirectionsResult results, GoogleMap mMap) {
                List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
                mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.YELLOW));

            }

            private String getEndLocationTitle(DirectionsResult results){
                return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
            }


            private GeoApiContext getGeoContext() {
                return new GeoApiContext.Builder()
                        .apiKey(apikey)
                        .build();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });*/

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Spots").addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if(!value.isEmpty()) {


                for (QueryDocumentSnapshot doc : value) {
                    Double lt = doc.getDouble("Lati");
                    Double ln= doc.getDouble("Long");
                    pos = new LatLng(lt,ln);
                    marker =mMap.addMarker(new MarkerOptions()
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxis))
                            .position(pos)
                            .title(doc.getString("Address")+" "+"R"+doc.getString("Price")));
                    marker.setTag(doc);


                    docid=doc.getId();


                }

            }

        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Spots").whereEqualTo("Lati",marker.getPosition().latitude).whereEqualTo("Long",marker.getPosition().longitude) .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            viewdialog(document.getId(),document.getString("Address"),document.getString("Price"),document.getString("Info"));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


        return false;
    }

    private void viewdialog(String documentId, String address, String price, String einfo) {
        final Dialog dialog = new Dialog(getContext()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                dismiss();
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialoglayoutmarker);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();



        Button buttondone = dialog.findViewById(R.id.buttonDoneM);
        EditText txtinfo = dialog.findViewById(R.id.editTextinfo2);
        EditText txtdest = dialog.findViewById(R.id.editTextDestination);
        EditText txtprice = dialog.findViewById(R.id.editTextPrice);
        ImageButton btnDislike = dialog.findViewById(R.id.imageButtondislike);
        ImageButton btnlike = dialog.findViewById(R.id.imageButtonlike);
        TextView likecount = dialog.findViewById(R.id.textViewlike);
        TextView dislikecount = dialog.findViewById(R.id.textViewdislike);

        txtdest.setText(address);
        txtprice.setText(price);
        txtinfo.setText(einfo);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getUid();
        final int[] destch = {0};
        final int[] pricech = {0};
        final int[] infoch = {0};
        buttondone.setOnClickListener(v -> {
            if(destch[0] >0){


                Map<String,Object> info = new HashMap<>();
                info.put("Address",txtdest.getText().toString().trim());


                db.collection("Spots").document(documentId).update(info);
            }else if(pricech[0] >0){

                Map<String,Object> info = new HashMap<>();
                info.put("Price",txtprice.getText().toString().trim());


                db.collection("Spots").document(documentId).update(info);
            }
            if(infoch[0] >0){


                Map<String,Object> info = new HashMap<>();
                info.put("Info",txtinfo.getText().toString().trim());
                db.collection("Spots").document(documentId).update(info);
            }
            dialog.dismiss();



        });

        txtdest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!txtdest.getText().toString().trim().isEmpty()){
                    destch[0] =1;
                }
            }
        });

        txtprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!txtprice.getText().toString().trim().isEmpty()){
                    pricech[0] =1;
                }

            }
        });

        txtinfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!txtinfo.getText().toString().trim().isEmpty()){
                    infoch[0] =1;
                }
            }
        });


        db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()) {
                dislikecount.setText(String.valueOf(queryDocumentSnapshots.size()));
            }else {
                dislikecount.setText(String.valueOf(0));
            }
        });

        db.collection("Likes").document(documentId).collection("Likesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()) {
                likecount.setText(String.valueOf(queryDocumentSnapshots.size()));
            }else {
                likecount.setText(String.valueOf(0));
            }
        });


        db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").document(user).get().addOnSuccessListener(documentSnapshot -> {
            if(!documentSnapshot.exists()){
                btnDislike.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_thumb_down_black_24dp));
                btnDislike.setVisibility(View.VISIBLE);
            }else {
                btnDislike.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_thumb_down_blue));
                btnDislike.setVisibility(View.VISIBLE);
            }

        });
        db.collection("Likes").document(documentId).collection("Likesbyuser").document(user).get().addOnSuccessListener(documentSnapshot -> {
            if(!documentSnapshot.exists()){
                btnlike.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_thumb_up_black_24dp));
                btnlike.setVisibility(View.VISIBLE);
            }else {
                btnlike.setImageDrawable(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_thumb_up_blue));
                btnlike.setVisibility(View.VISIBLE);
            }

        });



        btnDislike.setOnClickListener(v->{
            db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").document(user).get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    btnDislike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_thumb_down_black_24dp));
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").document(user).delete();
                    btnDislike.setVisibility(View.VISIBLE);
                    dislikecount.setText(String.valueOf(0));
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            dislikecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            dislikecount.setText(String.valueOf(0));
                        }
                    });
                    db.collection("Likes").document(documentId).collection("Likesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            likecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            likecount.setText(String.valueOf(0));
                        }
                    });

                }else {
                    btnDislike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_thumb_down_blue));
                    Map<String,Object> info = new HashMap<>();
                    info.put("Like","no");
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").document(user).set(info);
                    db.collection("Likes").document(documentId).collection("Likesbyuser").document(user).delete();
                    btnDislike.setVisibility(View.VISIBLE);
                    btnlike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            dislikecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            dislikecount.setText(String.valueOf(0));
                        }
                    });
                    db.collection("Likes").document(documentId).collection("Likesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            likecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            likecount.setText(String.valueOf(0));
                        }
                    });


                }

            });

        });

        btnlike.setOnClickListener(v->{
            db.collection("Likes").document(documentId).collection("Likesbyuser").document(user).get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    btnlike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                    db.collection("Likes").document(documentId).collection("Likesbyuser").document(user).delete();
                    btnlike.setVisibility(View.VISIBLE);
                    likecount.setText(String.valueOf(0));
                    db.collection("Likes").document(documentId).collection("Likesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            likecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            likecount.setText(String.valueOf(0));
                        }
                    });
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            dislikecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            dislikecount.setText(String.valueOf(0));
                        }
                    });
                }else {
                    btnlike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_thumb_up_blue));
                    Map<String,Object> info = new HashMap<>();
                    info.put("Like","no");
                    db.collection("Likes").document(documentId).collection("Likesbyuser").document(user).set(info);
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").document(user).delete();
                    btnlike.setVisibility(View.VISIBLE);
                    btnDislike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_thumb_down_black_24dp));
                    db.collection("Likes").document(documentId).collection("Likesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            likecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            likecount.setText(String.valueOf(0));
                        }
                    });
                    db.collection("DisLikes").document(documentId).collection("Dislikesbyuser").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            dislikecount.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }else {
                            dislikecount.setText(String.valueOf(0));
                        }
                    });

                }

            });

        });


    }

    public static class InternetConnection {

        /**
         * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
         */
        public static boolean checkConnection(Context context) {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr != null) {
                NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                if (activeNetworkInfo != null) { // connected to the internet
                    // connected to the mobile provider's data plan
                    if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
                }
            }
            return false;
        }
    }
    private void addHeatMap() {

        // Create the gradient.
        int[] colors = {
                Color.rgb(32, 1, 15), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.1f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        final ArrayList<LatLng> list =new ArrayList<LatLng>();
        // Get the data: latitude/longitude positions of police stations.

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Covid").addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            assert value != null;
            for (QueryDocumentSnapshot doc : value) {
                if (doc.getString("lat") != null) {
                    String lat = doc.getString("lat");
                    String lng = doc.getString("lng");

                    double nlat = Double.parseDouble(lat);
                    double nlng = Double.parseDouble(lng);
                    list.add(new LatLng(nlat, nlng));
                    // Create a heat map tile provider, passing it the latlngs of the police stations.
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(list)
                            .gradient(gradient)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                }
            }
        });
    }
}