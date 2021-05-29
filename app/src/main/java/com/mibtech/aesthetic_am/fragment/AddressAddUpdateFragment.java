package com.mibtech.aesthetic_am.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.activity.MainActivity;
import com.mibtech.aesthetic_am.adapter.AddressAdapter;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;
import com.mibtech.aesthetic_am.helper.VolleyCallback;
import com.mibtech.aesthetic_am.model.Address;
import com.mibtech.aesthetic_am.model.City;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.mibtech.aesthetic_am.fragment.AddressListFragment.addressAdapter;
import static com.mibtech.aesthetic_am.fragment.AddressListFragment.addresses;
import static com.mibtech.aesthetic_am.fragment.AddressListFragment.recyclerView;


public class AddressAddUpdateFragment extends Fragment implements OnMapReadyCallback {
    public static TextView tvCurrent;
    public static double latitude = 0.00, longitude = 0.00;
    public static Address address1;
    public static SupportMapFragment mapFragment;
    public static OnMapReadyCallback mapReadyCallback;
    View root;

    Button btnsubmit;
    ProgressBar progressBar;
    CheckBox chIsDefault;
    RadioButton rdHome, rdOffice, rdOther;
    Session session;
    String isDefault = "0";
    TextView tvUpdate, edtName, edtMobile, edtAlternateMobile, edtAddress, edtLanmark, edtPinCode, edtState, edtCounty;
    ScrollView scrollView;
    String name, mobile, alternateMobile, address2, landmark, pincode, state, country, isdefault, addressType;
    int position;
    Activity activity;
    int offset = 0;
    String For;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_address_add_update, container, false);
        activity = getActivity();
        setHasOptionsMenu(true);


        edtName = root.findViewById(R.id.edtName);
        edtMobile = root.findViewById(R.id.edtMobile);
        edtAlternateMobile = root.findViewById(R.id.edtAlternateMobile);
        edtLanmark = root.findViewById(R.id.edtLanmark);
        edtAddress = root.findViewById(R.id.edtAddress);
        edtPinCode = root.findViewById(R.id.edtPinCode);
        edtState = root.findViewById(R.id.edtState);
        edtCounty = root.findViewById(R.id.edtCountry);
        btnsubmit = root.findViewById(R.id.btnsubmit);
        scrollView = root.findViewById(R.id.scrollView);
        progressBar = root.findViewById(R.id.progressBar);
        chIsDefault = root.findViewById(R.id.chIsDefault);
        rdHome = root.findViewById(R.id.rdHome);
        rdOffice = root.findViewById(R.id.rdOffice);
        rdOther = root.findViewById(R.id.rdOther);
        tvCurrent = root.findViewById(R.id.tvCurrent);
        tvUpdate = root.findViewById(R.id.tvUpdate);

        session = new Session(activity);

        edtName.setText(session.getData(Constant.NAME));
        edtAddress.setText(session.getData(Constant.ADDRESS));
        edtPinCode.setText(session.getData(Constant.PINCODE));
        edtMobile.setText(session.getData(Constant.MOBILE));


        mapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                double saveLatitude, saveLongitude;
                if (latitude <= 0 || longitude <= 0) {
                    saveLatitude = Double.parseDouble(session.getCoordinates(Constant.LATITUDE));
                    saveLongitude = Double.parseDouble(session.getCoordinates(Constant.LONGITUDE));
                } else {
                    saveLatitude = latitude;
                    saveLongitude = longitude;
                }

                googleMap.clear();

                LatLng latLng = new LatLng(saveLatitude, saveLongitude);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(getString(R.string.current_location)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            }
        };

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Bundle bundle = getArguments();
        assert bundle != null;
        For = bundle.getString("for");
        position = bundle.getInt("position");
        if (For.equals("update")) {
            btnsubmit.setText(getString(R.string.update));
            address1 = (Address) bundle.getSerializable("model");
            latitude = Double.parseDouble(address1.getLatitude());
            longitude = Double.parseDouble(address1.getLongitude());
            tvCurrent.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, getActivity()));
            mapFragment.getMapAsync(this);
            SetData();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            SetSpinnerData();

            scrollView.setVisibility(View.VISIBLE);
            btnsubmit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddUpdateAddress();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "address");
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        chIsDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDefault.equalsIgnoreCase("0")) {
                    isDefault = "1";
                } else {
                    isDefault = "0";
                }
            }
        });

        return root;
    }

    void SetData() {

        name = address1.getName();
        mobile = address1.getMobile();
        address2 = address1.getAddress();
        alternateMobile = address1.getAlternate_mobile();
        landmark = address1.getLandmark();
        pincode = address1.getPincode();
        state = address1.getState();
        country = address1.getCountry();
        isdefault = address1.getIs_default();
        addressType = address1.getType();


        progressBar.setVisibility(View.VISIBLE);
        SetSpinnerData();
        edtName.setText(name);
        edtMobile.setText(mobile);
        edtAlternateMobile.setText(alternateMobile);
        edtAddress.setText(address2);
        edtLanmark.setText(landmark);
        edtPinCode.setText(pincode);
        edtState.setText(state);
        edtCounty.setText(country);
        chIsDefault.setChecked(isdefault.equalsIgnoreCase("1"));

        if (addressType.equalsIgnoreCase("home")) {
            rdHome.setChecked(true);
        } else if (addressType.equalsIgnoreCase("office")) {
            rdOffice.setChecked(true);
        } else {
            rdOther.setChecked(true);
        }

        progressBar.setVisibility(View.GONE);

        btnsubmit.setVisibility(View.VISIBLE);
        btnsubmit.setVisibility(View.VISIBLE);
    }

    void AddUpdateAddress() {


        String isDefault = chIsDefault.isChecked() ? "1" : "0";
        String type = rdHome.isChecked() ? "Home" : rdOffice.isChecked() ? "Office" : "Other";
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.requestFocus();
            edtName.setError("Please enter name!");
        } else if (edtMobile.getText().toString().trim().isEmpty()) {
            edtMobile.requestFocus();
            edtMobile.setError("Please enter mobile!");

        } else if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.requestFocus();
            edtAddress.setError("Please enter address!");
        } else if (edtLanmark.getText().toString().trim().isEmpty()) {
            edtLanmark.requestFocus();
            edtLanmark.setError("Please enter landmark!");
        } else if (edtPinCode.getText().toString().trim().isEmpty()) {
            edtPinCode.requestFocus();
            edtPinCode.setError("Please enter pin code!");
        } else if (edtState.getText().toString().trim().isEmpty()) {
            edtState.requestFocus();
            edtState.setError("Please enter state!");

        } else if (edtCounty.getText().toString().trim().isEmpty()) {
            edtCounty.requestFocus();
            edtCounty.setError("Please enter country");
        } else {
            Map<String, String> params = new HashMap<>();
            if (For.equalsIgnoreCase("add")) {
                params.put(Constant.ADD_ADDRESS, Constant.GetVal);
            } else if (For.equalsIgnoreCase("update")) {
                params.put(Constant.UPDATE_ADDRESS, Constant.GetVal);
                params.put(Constant.ID, address1.getId());
            }

            params.put(Constant.USER_ID, session.getData(Constant.ID));
            params.put(Constant.TYPE, type);
            params.put(Constant.NAME, edtName.getText().toString().trim());
            params.put(Constant.COUNTRY_CODE, session.getData(Constant.COUNTRY_CODE));
            params.put(Constant.MOBILE, edtMobile.getText().toString().trim());
            params.put(Constant.ALTERNATE_MOBILE, edtAlternateMobile.getText().toString().trim());
            params.put(Constant.ADDRESS, edtAddress.getText().toString().trim());
            params.put(Constant.LANDMARK, edtLanmark.getText().toString().trim());
            params.put(Constant.CITY_ID, "3");
            params.put(Constant.AREA_ID, "5");
            params.put(Constant.PINCODE, edtPinCode.getText().toString().trim());
            params.put(Constant.STATE, edtState.getText().toString().trim());
            params.put(Constant.COUNTRY, edtCounty.getText().toString().trim());
            params.put(Constant.IS_DEFAULT, isDefault);
            if (address1 != null) {
                if (address1.getLongitude() != null || address1.getLatitude() != null) {
                    params.put(Constant.LONGITUDE, session.getCoordinates(Constant.LONGITUDE));
                    params.put(Constant.LATITUDE, session.getCoordinates(Constant.LATITUDE));
                } else {
                    params.put(Constant.LONGITUDE, "" + longitude);
                    params.put(Constant.LATITUDE, "" + latitude);
                }
            }

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {

                            String msg;
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                offset = 0;
                                Gson g = new Gson();
                                Address address = g.fromJson(jsonObject.toString(), Address.class);
                                address.setCity_name("In India");
                                address.setArea_name("In India");

                                if (address.getIs_default().equals("1")) {
                                    for (int i = 0; i < addresses.size(); i++) {
                                        addresses.get(i).setIs_default("0");
                                    }
                                }

                                if (For.equalsIgnoreCase("add")) {
                                    msg = "Address added.";
                                    if (addressAdapter != null) {
                                        addresses.add(address);
                                    } else {
                                        addresses = new ArrayList<>();
                                        addresses.add(address);
                                        addressAdapter = new AddressAdapter(getContext(), getActivity(), addresses);
                                        recyclerView.setAdapter(addressAdapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    addresses.set(position, address);
                                    msg = "Address updated.";
                                }

                                AddressListFragment.tvAlert.setVisibility(View.GONE);

                                if (addressAdapter != null) {
                                    addressAdapter.notifyDataSetChanged();
                                }

                                if (address.getIs_default().equals("1")) {
                                    Constant.selectedAddressId = address.getId();
                                } else {
                                    if (Constant.selectedAddressId.equals(address.getId()))
                                        Constant.selectedAddressId = "";
                                }

                                MainActivity.fm.popBackStack();
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                        }
                    }
                }
            }, getActivity(), Constant.GET_ADDRESS_URL, params, true);
        }
    }

    void SetSpinnerData() {
        try {
            Map<String, String> params = new HashMap<>();

            Activity activity = getActivity();
            if (activity != null) {


                ApiConfig.RequestToVolley((result, response) -> {
                    if (result) {
                        try {
                            JSONObject objectbject = new JSONObject(response);
                            int pos = -1;
                            if (!objectbject.getBoolean(Constant.ERROR)) {

                                if (getContext() != null) {

                                    JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    }

                                }
                            }
                        } catch (JSONException e) {

                        }
                    }
                }, activity, Constant.CITY_URL, params, false);





            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onResume() {
        super.onResume();

        Constant.TOOLBAR_TITLE = getActivity().getString(R.string.address);
        getActivity().invalidateOptionsMenu();
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
    public void onMapReady(GoogleMap googleMap) {
        double saveLatitude, saveLongitude;
        if (For.equals("update")) {
            btnsubmit.setText(getString(R.string.update));
            assert getArguments() != null;
            address1 = (Address) getArguments().getSerializable("model");

            latitude = Double.parseDouble(address1.getLatitude());
            longitude = Double.parseDouble(address1.getLongitude());
        }
        if (latitude <= 0 || longitude <= 0) {
            saveLatitude = Double.parseDouble(session.getCoordinates(Constant.LATITUDE));
            saveLongitude = Double.parseDouble(session.getCoordinates(Constant.LONGITUDE));
        } else {
            saveLatitude = latitude;
            saveLongitude = longitude;
        }
        googleMap.clear();

        LatLng latLng = new LatLng(saveLatitude, saveLongitude);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }

}