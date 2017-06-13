package com.example.neel.notetakingandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteTaking extends AppCompatActivity implements LocationListener {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static String stimage;
    ImageView img;
    TextView tvltlng;
    EditText etnote;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Note Taking";
    Bitmap originalBitmap, bitmap;

    protected LocationManager locationManager;
    protected Context context;
    protected String latitude, longitude;
    DatabaseHelper myDB;
    private byte[] btimg = null;
    String getNote, s;
    String lati,lon;
    String uLat,uLon;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        img = (ImageView) findViewById(R.id.imageView);
        tvltlng = (TextView) findViewById(R.id.textViewLocation);
        etnote = (EditText) findViewById(R.id.etNote);

        myDB = new DatabaseHelper(this);
        Intent intent = getIntent();
        getNote = intent.getStringExtra("note");
        String getLocation = intent.getStringExtra("location");
        String getimage = intent.getStringExtra("image");
        String uLt = intent.getStringExtra("Latitude");
        String uLn = intent.getStringExtra("Longitude");

        etnote.setText(getNote);
        tvltlng.setText(getLocation);
        img.setImageBitmap(BitmapFactory.decodeFile(getimage));
        uLat = uLt;
        uLon = uLn;
        Toast.makeText(NoteTaking.this,""+uLat+""+uLon,Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.camera) {
            captureimage();
            return true;
        }
        if (id == R.id.location) {
            Intent intent = new Intent(NoteTaking.this, MapActivity.class);
            intent.putExtra("lat",uLat);
            intent.putExtra("lon",uLon);
            startActivity(intent);
            return true;
        }
        if (id == R.id.save) {
            AddData();
            Intent intent = new Intent(NoteTaking.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddData() {

        try {
            s = fileUri.getPath();

            if (getNote == null) {
                boolean isInserted = myDB.insertData(etnote.getText().toString(), tvltlng.getText().toString(), stimage.toString(),lati,lon);
            } else {
                boolean isIUpdated = myDB.updateData(etnote.getText().toString(), tvltlng.getText().toString(), stimage.toString(),lati,lon);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putParcelable("file_uri", fileUri);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public void captureimage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                previewCapturedImage();
            }
        }
    }

    public Uri getOutputMediaFileUri(int type) {

           return FileProvider.getUriForFile(NoteTaking.this, BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {




        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

             stimage = mediaFile.getAbsolutePath();

        } else {
            return null;
        }

        return mediaFile;
    }

    private void previewCapturedImage() {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(stimage, options);
            Log.d("PATH   :", fileUri.getPath());
            originalBitmap = Bitmap.createBitmap(bitmap);
            img.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(),
                Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location
                    .getLongitude(), 1);
            if (addresses.size() > 0)
                cityName = addresses.get(0).getAddressLine(0);
            cityName += "," + addresses.get(0).getAddressLine(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String s = "\n" + " City is: " + cityName;

        lati = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        if (tvltlng.getText().toString().isEmpty()) {
            tvltlng.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude() + s);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude", "disable");
    }
}
