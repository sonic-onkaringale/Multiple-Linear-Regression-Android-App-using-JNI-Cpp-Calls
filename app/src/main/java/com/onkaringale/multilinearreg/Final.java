package com.onkaringale.multilinearreg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpp.R;

import java.util.ArrayList;

public class Final extends AppCompatActivity {

    private static final String TAG = "Test";
    private static String[] stored_values;

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private static String[] mDataset;

        public MyAdapter(String[] myDataset)
        {
            mDataset = myDataset;
            stored_values = new String[myDataset.length];
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapt_edittext, parent, false);
            ViewHolder vh = new ViewHolder(v, new ViewHolder.ITextWatcher() {
                @Override
                public void beforeTextChanged(int position, CharSequence s, int start, int count, int after) {
                    // do something
                }

                @Override
                public void onTextChanged(int position, CharSequence s, int start, int before, int count) {
                    stored_values[position] = s.toString();
                }

                @Override
                public void afterTextChanged(int position, Editable s) {
                    // do something
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
//            holder.mEditText.setHint(mDataset[position]);
            holder.mText.setText(mDataset[position]);
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {

            public EditText mEditText;
            public TextView mText;
            private ITextWatcher mTextWatcher;

            public interface ITextWatcher {
                // you can add/remove methods as you please, maybe you dont need this much
                void beforeTextChanged(int position, CharSequence s, int start, int count, int after);

                void onTextChanged(int position, CharSequence s, int start, int before, int count);

                void afterTextChanged(int position, Editable s);
            }

            public ViewHolder(View v, ITextWatcher textWatcher) {
                super(v);

                this.mEditText = (EditText) v.findViewById(R.id.etadap_editText);
                this.mText = (TextView) v.findViewById(R.id.etadap_Text);
                this.mTextWatcher = textWatcher;

                this.mEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        mTextWatcher.beforeTextChanged(getAdapterPosition(), s, start, count, after);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mTextWatcher.onTextChanged(getAdapterPosition(), s, start, before, count);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mTextWatcher.afterTextChanged(getAdapterPosition(), s);
                    }
                });
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        /*
            []output = [r2score,betas...]
            []header = [all headers]
            []header_order = [needed headers only with index] use: for 0 to ho.size() print(header[ho.get(i)])

         */

        String []output = bundle.getStringArray("result");
        String []header = bundle.getStringArray("header");
        ArrayList<Integer> header_order = (ArrayList<Integer>) bundle.getSerializable("header_order");

        TextView r2score = findViewById(R.id.r2score);
        r2score.setText(("R-Squared Score\n"+output[0]));
//        EditText editText = findViewById(R.id.predict_x);
        TextView answer = findViewById(R.id.predict_ans);
        Button predict = findViewById(R.id.predictbtn);
//        StringBuilder eheader = new StringBuilder();
//        for (int i = 0; i<header_order.size() -1;i++)
//        {
//            if (i == header_order.size()-2)
//                eheader.append(header[header_order.get(i)]);
//            eheader.append(header[header_order.get(i)]).append(", ");
//        }
//        editText.setText(eheader);

        String[] oheader = new String[header_order.size()-1];
        for (int i = 0; i<oheader.length;i++)
        {
                oheader[i] = header[header_order.get(i)];
        }

        RecyclerView editView = findViewById(R.id.edrv);
        editView.setLayoutManager(new LinearLayoutManager(this));
        editView.setAdapter(new MyAdapter(oheader));




        Log.v(TAG, "onCreate: " );
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> x = new ArrayList<>(Arrays.asList(editText.getText().toString().split(",")));
//                String[] x = editText.getText().toString().split("\\s*,\\s*");
//                if (x.length != output.length-2)
//                {
//                    Toast.makeText(getApplicationContext(),"Error: Wrong Input",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                for (int i = 0; i < stored_values.length; i++) {
                    if (stored_values[i] == ("") ||stored_values[i]==null||stored_values[i].isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Please fill the "+ header[header_order.get(i)]+" field.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                double predict_output = Double.parseDouble(output[1]);
                for (int i = 2; i < output.length; i++)
                {
                    predict_output += (Double.parseDouble(output[i]) * Double.parseDouble(stored_values[i-2]));
                }
                answer.setText((header[header_order.get(header_order.size() - 1)] + " : "+predict_output));

            }
        });




    }
}