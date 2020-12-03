package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.InstanceIdResult;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.google.firebase.iid.FirebaseInstanceId.getInstance;


// TODO: add user settings
// TODO: add permission checks
public class Profile extends AppCompatActivity implements OnMapReadyCallback  {
    private final static String TAG = "Profile";

    private GoogleMap map;
    private Toolbar toolbar;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageView imgNavHeaderBg;
    private ImageView imgProfile;
    private TextView textName;
    private TextView textEmail;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String profileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private Button signOutButton;
    private Button createEventButton;
    private Button findEventButton;

    private final Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM  d, YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        Log.d(TAG, "Current User:" + currentUser.getDisplayName());

        GoogleSignInAccount currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        TextView currentDate = findViewById(R.id.currentDate);
        currentDate.setText(dateFormat.format(calendar.getTime()));
        TextView currentDay = findViewById(R.id.currentDay);
        currentDay.setText(dayFormat.format(calendar.getTime()));

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initButtons();

        getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                String message = getString(R.string.token_prefix, token);
                Log.d(TAG, message);
                // TODO: change message to something meaningful
                // Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        if (!checkGooglePlayServices()) {
            Log.w(TAG, "Device doesn't have google play services");
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        textName = navHeader.findViewById(R.id.name);
        textEmail = navHeader.findViewById(R.id.email);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        // load nav menu header data
        loadNavHeader(currentAccount);

        // initializing navigation menu
        setUpNavigationView();

    }

    private void initButtons() {

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        createEventButton = findViewById(R.id.eventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEventIntent = new Intent(Profile.this, AddEventActivity.class);
                startActivity(addEventIntent);
            }
        });

        findEventButton = findViewById(R.id.find_events_button);
        findEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findEventIntent = new Intent(Profile.this, DisplayEventActivity.class);
                findEventIntent.putExtra("activity", "findEvent");
                startActivity(findEventIntent);
            }
        });

    }

    private void loadNavHeader( GoogleSignInAccount currentAccount) {


        if (currentAccount == null) {
            // TODO: get name + email
            textName.setText("First Middle Last");
            textEmail.setText("www.test.com");

            // Loading profile image
            Glide.with(this).load(profileImg)
                    .circleCrop()
                    .into(imgProfile);

            // loading header background image
            Glide.with(this).load(urlNavHeaderBg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgNavHeaderBg);

        } else {
            textName.setText(String.format("%s %s", currentAccount.getGivenName(), currentAccount.getFamilyName()));
            textEmail.setText(currentAccount.getEmail());

            // Loading profile image
            Glide.with(this)
                    .load(currentAccount.getPhotoUrl())
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);

            // loading header background image
            Glide.with(this).load(urlNavHeaderBg)

                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgNavHeaderBg);
        }

    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_profile:
                        Intent profileSettingsIntent = new Intent(Profile.this, ProfileSettingsActivity.class);
                        startActivity(profileSettingsIntent);
                        break;
                    case R.id.nav_friends:
                        Intent friendIntent = new Intent(Profile.this, BrowseUsersActivity.class);
                        friendIntent.putExtra("activity", "friends");
                        startActivity(friendIntent);
                        break;
                    case R.id.nav_browse_users:
                        Intent browseUsersIntent = new Intent(Profile.this, BrowseUsersActivity.class);
                        browseUsersIntent.putExtra("activity", "users");
                        startActivity(browseUsersIntent);
                        break;
                    case R.id.nav_my_events:
                        Intent myEventsIntent = new Intent(Profile.this, DisplayEventActivity.class);
                        myEventsIntent.putExtra("activity", "myEvent");
                        startActivity(myEventsIntent);
                        break;
                    case R.id.nav_friend_requests:
                        Intent friendRequests = new Intent(Profile.this, BrowseUsersActivity.class);
                        friendRequests.putExtra("activity", "friend_requests");
                        startActivity(friendRequests);
                        break;
                    default:
                        break;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                menuItem.setChecked(!menuItem.isChecked());
                menuItem.setChecked(true);

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer,  R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                signOutButton.bringToFront();
                createEventButton.bringToFront();
                findEventButton.bringToFront();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                navigationView.bringToFront();
                drawer.bringToFront();
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private boolean checkGooglePlayServices() {

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error");
            // TODO: ask user to update google play services and manage the error
            return false;
        } else {
            Log.i(TAG, "Google play services updated");
            return true;
        }
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(getBaseContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        checkPermission();
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                map.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }
}