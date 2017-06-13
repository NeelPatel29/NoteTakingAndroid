package com.example.neel.notetakingandroid;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 23;
    RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    DatabaseHelper myDB;
    private List<NotesPojo> items;
    private CoordinatorLayout coLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout ctLayout;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        img = (ImageView) findViewById(R.id.imageviewHeadline);

        coLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ctLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        myDB = new DatabaseHelper(this);
        List<NotesPojo> contacts = myDB.readAllContacts();
        int contact = contacts.size();
        if (contact != 0) {
            Toast.makeText(getApplicationContext(), "Images:" + contact, Toast.LENGTH_LONG).show();
            for (NotesPojo c : contacts) {

                img.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(c.getImage())));


                Bitmap image = BitmapFactory.decodeFile(c.getImage());

                Palette.from(image).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {

                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                        if (vibrantSwatch != null) {
                            ctLayout.setContentScrimColor(palette.getMutedColor(vibrantSwatch.getRgb()));
                            ctLayout.setStatusBarScrimColor(palette.getMutedColor(vibrantSwatch.getRgb()));

                        }
                    }
                });
            }
        }
        setSupportActionBar(toolbar);

        items = new ArrayList<>();
        customAdapter = new CustomAdapter(this, items);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        prepareAlbums();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), NoteTaking.class);
                String note = ((TextView) view.findViewById(R.id.textViewNote)).getText().toString();
                String location = ((TextView) view.findViewById(R.id.textViewLocation)).getText().toString();
                String lat = ((TextView) view.findViewById(R.id.textViewLat)).getText().toString();
                String lon = ((TextView) view.findViewById(R.id.textViewLon)).getText().toString();
                ImageView image = ((ImageView) view.findViewById(R.id.imageView2));

                List<NotesPojo> contacts = myDB.readAllContacts();
                NotesPojo np = contacts.get(position);
                String s = np.getImage();
                Log.d("STRING:", np.getImage());

                intent.putExtra("note", note);
                intent.putExtra("location", location);
                intent.putExtra("Latitude", lat);
                intent.putExtra("Longitude", lon);
                intent.putExtra("image", s);
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                List<NotesPojo> contacts = myDB.readAllContacts();
                NotesPojo np = contacts.get(position);
                String s = np.getNote();
                myDB.delete_byID(s);
                prepareAlbums();
            }
        }));


        locationService();

        if (isReadStorageAllowed()) {
            //If permission is already having then showing the toast
            Toast.makeText(MainActivity.this, "You already have the permission", Toast.LENGTH_LONG).show();
            //Existing the method with return
            return;
        }

        //If the app has not the permission then asking for the permission
        requestStoragePermission();

    }

    private void prepareAlbums() {
        List<NotesPojo> contacts = myDB.readAllContacts();
        // Initialize Custom Adapter
        customAdapter = new CustomAdapter(MainActivity.this, contacts);
        // Set Adapter to ListView
        recyclerView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

        // See the log int LogCat
        for (NotesPojo c : contacts) {
            String record = "ID=" + c.getNote() + " | Name=" + c.getLocation() + " | " + c.getImage();
            Log.d("Record", record);
        }


    }


    public void locationService() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services");
            builder.setMessage("Please Enable Location Service");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add) {
            Intent i = new Intent(MainActivity.this, NoteTaking.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private class ProcessTask extends AsyncTask<String,String,String>{

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.show();
            pDialog.setContentView(R.layout.progress_dialog);
            pDialog.setCancelable(false);
        }


        @Override
        protected String doInBackground(String... params) {


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
