package com.mibtech.aesthetic_am.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.mibtech.aesthetic_am.helper.ApiConfig.getAddress;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {
    View root;
    TextView tvLocation;
    Session session;
    FloatingActionButton fabSatellite, fabStreet, fabCurrent;
    int mapType = GoogleMap.MAP_TYPE_NORMAL;
    SupportMapFragment mapFragment;
    Button btnUpdateLocation;
    OnMapReadyCallback mapReadyCallback;
    String from;
    Activity activity;
    private GoogleApiClient googleApiClient;
    private double c_longitude, c_latitude, longitude, latitude, bundleLongitude, bundleLatitude;
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_map, container, false);

        activity = getActivity();
        session = new Session(activity);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnUpdateLocation = root.findViewById(R.id.btnUpdateLocation);

        from = getArguments().getString(Constant.FROM);
        setHasOptionsMenu(true);


        if (from.equalsIgnoreCase("address")) {
            bundleLatitude = getArguments().getDouble("latitude");
            bundleLongitude = getArguments().getDouble("longitude");
        }

        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equalsIgnoreCase("address")) {
                    AddressAddUpdateFragment.latitude = latitude;
                    AddressAddUpdateFragment.longitude = longitude;
                    session.setData(Constant.LATITUDE, "" + latitude);
                    session.setData(Constant.LONGITUDE, "" + longitude);
                    AddressAddUpdateFragment.tvCurrent.setText(getAddress(latitude, longitude, activity));
                    AddressAddUpdateFragment.mapFragment.getMapAsync(AddressAddUpdateFragment.mapReadyCallback);
                }
                MainActivity.fm.popBackStack();
            }
        });

        mapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                double saveLatitude = bundleLatitude;
                double saveLongitude = bundleLongitude;
                googleMap.clear();

                LatLng latLng = new LatLng(saveLatitude, saveLongitude);
                googleMap.setMapType(mapType);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(getString(R.string.current_location)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            }
        };

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        tvLocation = root.findViewById(R.id.tvLocation);
        fabSatellite = root.findViewById(R.id.fabSatellite);
        fabCurrent = root.findViewById(R.id.fabCurrent);
        fabStreet = root.findViewById(R.id.fabStreet);

        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                mapFragment.getMapAsync(mapReadyCallback);
            }
        });

        fabStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                mapFragment.getMapAsync(mapReadyCallback);
            }
        });

        fabCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mapType = GoogleMap.MAP_TYPE_NORMAL;
                LatLng latLng = new LatLng(c_latitude, c_longitude);
                saveLocation(c_latitude, c_longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(getString(R.string.current_location)));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                //tvLocation.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
                tvLocation.setText(getString(R.string.location_1) + getAddress(latitude, longitude, activity));
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mapFragment.getMapAsync(mapReadyCallback);
            }
        }, 1000);

        return root;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.clear();
        LatLng latLng;
        latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setMapType(mapType);
        mMap.setOnMarkerDragListener(this);

        mMap.setOnMapLongClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                //Moving the map
                mMap.clear();
                moveMap(false);
            }
        });
        tvLocation.setText(getString(R.string.location_1) + getAddress(latitude, longitude, activity));
    }


    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
        } else {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null) {
                        c_longitude = location.getLongitude();
                        c_latitude = location.getLatitude();
                        if (bundleLatitude <= 0.00 && bundleLongitude <= 0.00) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        } else {
                            longitude = bundleLongitude;
                            latitude = bundleLatitude;
                        }
                        moveMap(true);
                    }
                }
            });
        }
    }

    private void moveMap(boolean isfirst) {


        LatLng latLng = new LatLng(latitude, longitude);


        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.set_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        if (isfirst)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        tvLocation.setText(getString(R.string.location_1) + getAddress(latitude, longitude, activity));
        //  text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        latitude = bundleLatitude = latLng.latitude;
        longitude = bundleLatitude = latLng.longitude;
        saveLocation(latitude, longitude);
        //Moving the map
        moveMap(false);

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = bundleLongitude = marker.getPosition().latitude;
        longitude = bundleLatitude = marker.getPosition().longitude;
        moveMap(false);
    }

    public void saveLocation(double latitude, double longitude) {
        bundleLongitude = this.longitude = longitude;
        bundleLatitude = this.latitude = latitude;
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentLocation();
        mapFragment.getMapAsync(this);
        Constant.TOOLBAR_TITLE = getString(R.string.app_name);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_logout).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
    }
}