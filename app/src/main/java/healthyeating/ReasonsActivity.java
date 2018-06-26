package healthyeating;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by darren on 4/20/17.
 */

public class  ReasonsActivity extends AppCompatActivity {
    DBHandler dbHelper = new DBHandler(ReasonsActivity.this);
    ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        Boolean clickable = TRUE;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reasons);

        Button buttBack = (Button) findViewById(R.id.bBack);

        buttBack.setOnClickListener(new ReasonsActivity.backBut());

        final ArrayList<String> reasonList = getReasons();

        if (reasonList.isEmpty()) {
            reasonList.add("There is no logged data.");
            clickable = FALSE;
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, reasonList);

        listView = (ListView) findViewById(R.id.reasonList);
        listView.setAdapter(adapter);

        if (clickable){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ReasonsActivity.this);
                    alert.setMessage(reasonList.get(position));
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
            });
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

    private ArrayList getReasons(){
        ArrayList<String> reasonList = new ArrayList<>();

        Cursor data = dbHelper.getNonHealthLogs();
        int indexDate = data.getColumnIndexOrThrow("date");
        int indexReason = data.getColumnIndexOrThrow("reason");

        System.out.println("size: " + data.getCount());
        //Populate dictionary with log dates and reasons for non healthy days
        while(data.moveToNext()) {
            String logDate = data.getString(indexDate);
            String logReason = data.getString(indexReason);
            String tempValue = logDate + ": " + logReason;
            reasonList.add(tempValue);
        }
        data.close();
        System.out.println(reasonList.toString());
        return reasonList;
    }

}
