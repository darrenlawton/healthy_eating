package healthyeating;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by darren on 4/22/17.
 */

public class IamActivity extends AppCompatActivity {
    Button buttBack;
    RelativeLayout myLayout;
    Random outputRand = new Random();
    List<String> listAttributes = new ArrayList<>(Arrays.asList("Strong", "Going to be fine"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iam);

        buttBack = (Button) findViewById(R.id.bBack);
        buttBack.setOnClickListener(new backBut());

        myLayout = (RelativeLayout) findViewById(R.id.activity_iam);
        myLayout.setOnClickListener(new updateText());
    }

    /*INNER CLASS - Action if 'layout: clicked clicked*/
    private class updateText implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setOutputText();
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

    private void setOutputText(){
        TextView outputText = (TextView) findViewById(R.id.output_iam);
        outputText.setTextSize(70);
        int randomNum = outputRand.nextInt(listAttributes.size());
        String stringBuilder = "";
        String attribute = listAttributes.get(randomNum);

        String[] splitOutput = attribute.split("\\s+");

        for (String word:splitOutput) {
            if (word.length() > 6) outputText.setTextSize(50);

            stringBuilder = stringBuilder + word + System.lineSeparator();
        }

        outputText.setText(stringBuilder);
    }

}
