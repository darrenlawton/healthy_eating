package healthyeating;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    PersistDataPreferences display_data = new PersistDataPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set reminder (one time)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int i = preferences.getInt("numberoflaunches",1);

        if (i<2){
            reminderMethod();
            i++;
            editor.putInt("numberoflaunches",i);
            editor.commit();
        }

        //Set user saved image
        loadImage(display_data.readFromFile(this.getApplicationContext(),"display.txt"));

        //Set listener for Image, allow manual selection?
        CircularImageView displayImage = (CircularImageView) findViewById(R.id.circular_image_view);

        displayImage.setOnClickListener(new displayImage());
    }

    /*INNER CLASS - Action if 'image view' clicked*/
    class displayImage implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            openGallery();
        }
    }

    private void openGallery(){
        Intent galIntent = new Intent();
        galIntent.setType("image/*");
        galIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galIntent,"Select Image"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();
            try{
                //save image, for reload oncreate
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                String imagePath = saveImage(bitmap);
                display_data.writeToFile(imagePath,this.getApplicationContext(),"display.txt");

                loadImage(display_data.readFromFile(this.getApplicationContext(),"display.txt"));

            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Stupid Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*Adapted from : http://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android*/
    private String saveImage(Bitmap image){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /*Adapted from : http://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android*/
    private void loadImage(String path){
        try{
            File f = new File(path,"profile.jpg");
            Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));

            CircularImageView updateImage = (CircularImageView) findViewById(R.id.circular_image_view);
            updateImage.setImageBitmap(image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /*On click - open LogActivity*/
    public void logHealth(View view) {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    /*On click - open SuuportActivity*/
    public void activitySupport(View view) {
        Intent intent = new Intent(this, SupportActivity.class);
        startActivity(intent);
    }


    /*Set daily reminder on first opening of app*/
    private void reminderMethod(){
        Intent intent = new Intent(this, NotifyService.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.SECOND, 0);
        calendar.set(calendar.MINUTE, 0);
        calendar.set(calendar.HOUR, 9);
        calendar.set(calendar.AM_PM, calendar.PM);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*60*60*24,pendingIntent);

        Toast.makeText(MainActivity.this, "Daily Reminder Set",Toast.LENGTH_LONG).show();
    }


}
