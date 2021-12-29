package com.onkaringale.multilinearreg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Final extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        String []output = bundle.getStringArray("result");
        String []header = bundle.getStringArray("header");
        ArrayList<Integer> header_order = (ArrayList<Integer>) bundle.getSerializable("header_order");

        TextView r2score = findViewById(R.id.r2score);
        r2score.setText(("R2 Score\n"+output[0]));
        EditText editText = findViewById(R.id.predict_x);
        TextView answer = findViewById(R.id.predict_ans);
        Button predict = findViewById(R.id.predictbtn);
        StringBuilder eheader = new StringBuilder();
        for (int i = 0; i<header_order.size() -1;i++)
        {
            if (i == header_order.size()-2)
                eheader.append(header[header_order.get(i)]);
            eheader.append(header[header_order.get(i)]).append(", ");
        }
//        editText.setText(eheader);
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> x = new ArrayList<>(Arrays.asList(editText.getText().toString().split(",")));
                String[] x = editText.getText().toString().split("\\s*,\\s*");
                if (x.length != output.length-2)
                {
                    Toast.makeText(getApplicationContext(),"Error: Wrong Input",Toast.LENGTH_SHORT).show();
                    return;
                }
                double predict_output = Double.parseDouble(output[1]);
                for (int i = 2; i < output.length; i++)
                {
                    predict_output += (Double.parseDouble(output[i]) * Double.parseDouble(x[i-2]));
                }
                answer.setText((header[header_order.get(header_order.size() - 1)] + " : "+predict_output));

            }
        });




    }
}