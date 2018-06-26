package healthyeating;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by darren on 4/6/17.
 */

public class SupportActivity extends AppCompatActivity {
    DBHandler dbHelper = new DBHandler(SupportActivity.this);

    static final int PICK_CONTACT=1;
    PersistDataPreferences contact_data = new PersistDataPreferences();

    private static final int SEND_SMS =0 ;
    String sendNumber;
    String sendMessage = "URGENT: Call me now.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Button buttBack = (Button) findViewById(R.id.bBack);
        Button buttMax = (Button) findViewById(R.id.bMax);
        Button buttStat = (Button) findViewById(R.id.bStats);
        Button buttData = (Button) findViewById(R.id.bData);
        Button buttSet = (Button) findViewById(R.id.bSettings);
        Button buttCall = (Button) findViewById(R.id.bCall);
        Button buttIam = (Button) findViewById(R.id.bDescription);

        buttBack.setOnClickListener(new backBut());
        buttMax.setOnClickListener(new buttMax());
        buttStat.setOnClickListener(new buttStat());
        buttData.setOnClickListener(new buttData());
        buttSet.setOnClickListener(new buttSet());
        buttCall.setOnClickListener(new buttCall());
        buttIam.setOnClickListener(new buttIam());
    }

    /*INNER CLASS - Action if 'Max' button clicked*/
    private class buttMax implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Cursor data = dbHelper.getMax();
            AlertDialog.Builder alert = new AlertDialog.Builder(SupportActivity.this);

            if (data.getString(0) != null){
                alert.setTitle("My Record");
                alert.setMessage("Max consecutive days of healthy eating is " + data.getString(0));
            }else{
                alert.setTitle("No data logged to date");
            }

            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(true);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), "Max queried", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
            data.close();
        }
    }

    /*INNER CLASS - Action if 'Stats' button clicked*/
    private class buttStat implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String earliestDate = "";
            String notString = "";

            //Number of logs
            long logCount = dbHelper.getCount();

            //Earliest log
            Cursor dataEarliest = dbHelper.getEarliest();
            int indexDate = dataEarliest.getColumnIndexOrThrow("date");
            if (dataEarliest.getCount()>0) earliestDate = dataEarliest .getString(indexDate);

            //Number of healthy days logged
            long healthDays = dbHelper.getHealthDays();


            AlertDialog.Builder alert = new AlertDialog.Builder(SupportActivity.this);
            if (dataEarliest.getCount()>0){
                double healthyPercentage = (double) (healthDays*100/logCount);
                alert.setTitle("My Health Stats");
                if (logCount == 1){
                    if (healthDays == 0) notString = " not";
                    alert.setMessage("Since " + earliestDate + ", you have: " + System.lineSeparator() +
                            "- " + logCount + " daily log" + System.lineSeparator() +
                            "- this was" + notString + " a healthy eating day");
                }else{
                    alert.setMessage("Since " + earliestDate + ", you have: " + System.lineSeparator() +
                            "- " + logCount + " daily logs" + System.lineSeparator() +
                            "- of these " + healthyPercentage + "% have been heathy days");
                }

            }else{
                alert.setTitle("No data logged to date.");
            }

            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(true);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), "Stats queried", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
            dataEarliest.close();
        }
    }

    /*INNER CLASS - Action if 'Settings' button clicked*/
    private class buttSet implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SupportActivity.this);

            alert.setTitle("Emergency Contact");
            alert.setMessage("Set new emergency contact?");

            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), "Emergency contact not set", Toast.LENGTH_SHORT).show();
                }
            });

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectSingleContact();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
        }
    }

    /*Show contact list for selection*/
    private void selectSingleContact() {
        Intent contactIntent = new Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(Intent.createChooser(contactIntent,"Select Emergency Contact"), PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();


                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                //Write data to relevant files
                contact_data.writeToFile(name,this.getApplicationContext(),"contact_name.txt");
                contact_data.writeToFile(number,this.getApplicationContext(),"contact_number.txt");

                Toast.makeText(getBaseContext(), "Emergency contact set", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*INNER CLASS - Action if 'Call' button clicked*/
    private class buttCall implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String contactName = "";
            String contactNumber = "";

            contactName = contact_data.readFromFile(SupportActivity.this.getApplicationContext(),"contact_name.txt");
            contactNumber = contact_data.readFromFile(SupportActivity.this.getApplicationContext(),"contact_number.txt");

            AlertDialog.Builder alert = new AlertDialog.Builder(SupportActivity.this);

            alert.setTitle("Emergency Contact");
            alert.setMessage("Do you want to message " + contactName + "?");

            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), "No message sent", Toast.LENGTH_SHORT).show();
                }
            });

            final String finalContactNumber = contactNumber;
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendSMS(finalContactNumber);
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
        }
    }

    /*Get SMS permissions, and then send message*/
    protected void sendSMS(String contactNumber) {
        sendNumber = contactNumber;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sendNumber, null, sendMessage , null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed to send", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    /*INNER CLASS - Action if 'Log Data' symbol clicked*/
    private class buttData implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SupportActivity.this, ReasonsActivity.class);
            startActivity(intent);
        }
    }

    /*INNER CLASS - Action if 'I am' symbol clicked*/
    private class buttIam implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SupportActivity.this, IamActivity.class);
            startActivity(intent);
        }
    }

    /*INNER CLASS - Action if 'back' button clicked*/
    private class backBut implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent startMain = new Intent(getParentActivityIntent());
            startActivity(startMain);
        }
    }
}
