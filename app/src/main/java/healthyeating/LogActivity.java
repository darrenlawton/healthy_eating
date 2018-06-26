package healthyeating;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by darren on 4/6/17.
 */

public class LogActivity extends AppCompatActivity {

    DBHandler dbHelper = new DBHandler(LogActivity.this);
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        date = new Date();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView dateText = (TextView) findViewById(R.id.date_text);
        dateText .setText(String.valueOf("As at " + dateFormat.format(date) + ":"));

        setOutputText(date);

        Button buttGood = (Button) findViewById(R.id.bHealthy);
        Button buttTomm = (Button) findViewById(R.id.bNonHealthy);
        Button buttBack = (Button) findViewById(R.id.bBack);

        //Set listener for healthy day button
        buttGood.setOnClickListener(new healthBut());
        //Set listener for non healthy day button
        buttTomm.setOnClickListener(new tommBut());
        buttBack.setOnClickListener(new backBut());
    }

    /*INNER CLASS - Action if 'I did good' button clicked*/
    private class healthBut implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String tm1Consec = "0";
            Boolean record;

            date = new Date();

            //Get consec days from t-1 entry
            Cursor data = dbHelper.getRecent();
            Cursor maxData = dbHelper.getMax();

            int indexConsec = data.getColumnIndexOrThrow("consecutive_healthy_days");
            int indexDate = data.getColumnIndexOrThrow("date");
            int recordDays = 0;

            //get max consec days of health
            if (maxData.getString(0)!= null) recordDays = Integer.parseInt(maxData.getString(0));

            if (data.getCount() > 1){
                if (data.getString(indexDate).equals(dateFormat.format(date))) data.moveToNext();
                tm1Consec = data.getString(indexConsec);
            }

            data.close();
            maxData.close();
            int tConsec = Integer.parseInt(tm1Consec) + 1;
            record = tConsec > recordDays;

            //Create daily log
            DailyLog tlog = new DailyLog(dateFormat.format(date),1,tConsec,"");
            dbHelper.updateDB(tlog);

            //Display positive encouragement
            setOutputText(date);
            inflateGoodSplash(record);

        }
    }

    /*INNER CLASS - Action if 'Tomorrow' button clicked*/
    private class tommBut implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            date = new Date();
            inflateBadSplash(date);
        }
    }

    /*set text for id: output_consecutive*/
    private void setOutputText(Date date){
        String tConsec;

        //Set text for output_consecutive
        Cursor data = dbHelper.getLog(dateFormat.format(date));
        int indexConsec = data.getColumnIndexOrThrow("consecutive_healthy_days");

        //Check records exists within db
        if (data.getCount()>0) {
            tConsec = data.getString(indexConsec);
        }else{
            Cursor dataRecent = dbHelper.getRecent();
            if (dataRecent.getCount()>1){
                tConsec = data.getString(indexConsec);
                dataRecent.close();
            }else{
                tConsec = "0";
            }
        }
        data.close();

        TextView outputText = (TextView) findViewById(R.id.output_consecutive);

        switch (Integer.parseInt(tConsec)) {
            case 0:
                outputText .setText(String.valueOf(tConsec + " days of healthy eating. That changes now!"));
                outputText.setTextSize(30);
                break;
            case 1:
                outputText .setText(String.valueOf(tConsec + " day of healthy eating!"));
                outputText.setTextSize(45);
                break;
            default:
                outputText .setText(String.valueOf(tConsec + " days of healthy eating!"));
                outputText.setTextSize(45);
                break;
        }
    }

    private void inflateGoodSplash(Boolean record){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (record){
            alert.setTitle("NEW RECORD");
            alert.setMessage("Good work, very proud of you!");
        } else{
            alert.setTitle("Good Work!! Proud of You!!");
        }

        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Sar did gooood", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void inflateBadSplash(final Date date){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.splash_badday, null);
        final EditText etReason = (EditText) alertLayout.findViewById(R.id.et_reason);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("What happen today?");

        // set the view from XML inside alertdialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Update not logged", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reason = etReason.getText().toString();

                DailyLog tlog = new DailyLog(dateFormat.format(date),0,0,reason);
                dbHelper.updateDB(tlog);
                setOutputText(date);
                Toast.makeText(getBaseContext(), "Logged. We'll get it tomorrow!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
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
