package com.onkaringale.multilinearreg;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cpp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class csv_dashboard extends AppCompatActivity {

    static
    {
        System.loadLibrary("cpp");
    }
    public boolean[] x_state,y_state;

    public class RCustomAdapter_x extends RecyclerView.Adapter<RCustomAdapter_x.ViewHolder>
    {

        private String[] headers;

        RCustomAdapter_x(String[] obj)
        {
            this.headers = obj;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            private SwitchMaterial switchMaterial ;

            public ViewHolder(@NonNull View itemView) {

                super(itemView);
                switchMaterial = (SwitchMaterial) itemView.findViewById(R.id.switch1);
            }

            public SwitchMaterial getSwitchMaterial()
            {
                return switchMaterial;
            }
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.checklist_adapt,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            holder.getSwitchMaterial().setText(headers[position]);
            holder.getSwitchMaterial().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        x_state[holder.getAdapterPosition()] = true;
                    }
                    else
                    {
                        x_state[holder.getAdapterPosition()] = false;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return headers.length;
        }


    }

    public class RCustomAdapter_y extends RecyclerView.Adapter<RCustomAdapter_y.ViewHolder>
    {

        private String[] headers;

        RCustomAdapter_y(String[] obj)
        {
            this.headers = obj;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            private SwitchMaterial switchMaterial ;

            public ViewHolder(@NonNull View itemView) {

                super(itemView);
                switchMaterial = (SwitchMaterial) itemView.findViewById(R.id.switch1);
            }

            public SwitchMaterial getSwitchMaterial()
            {
                return switchMaterial;
            }
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.checklist_adapt,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            holder.getSwitchMaterial().setText(headers[position]);
            holder.getSwitchMaterial().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        y_state[holder.getAdapterPosition()] = true;
                    }
                    else
                    {
                        y_state[holder.getAdapterPosition()] = false;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return headers.length;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_dashboard);

        RecyclerView recyclerView_x = findViewById(R.id.lv_x);

        RecyclerView recyclerView_y = findViewById(R.id.lv_y);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        int dataset_size = intent.getIntExtra("size",0);
        ArrayList<String[]> dataset = new ArrayList<>();

        for (int i = 0; i < dataset_size; i++)
        {
            dataset.add(bundle.getStringArray(("row_"+i)));
        }
        String[] header = dataset.get(0);
        x_state = new boolean[header.length];
        y_state = new boolean[header.length];
        for (int i = 0; i < x_state.length; i++) {
            x_state[i] = false;
            y_state[i] = false;
        }
        RCustomAdapter_x rCustomAdapter_x = new RCustomAdapter_x(header);
        RCustomAdapter_y rCustomAdapter_y = new RCustomAdapter_y(header);
        recyclerView_x.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_y.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_x.setAdapter(rCustomAdapter_x);
        recyclerView_y.setAdapter(rCustomAdapter_y);
        Button train_btn = (Button) findViewById(R.id.trainbtn);
        train_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (header.length ==0 || header.length == 1)
                {
                    Toast.makeText(getApplicationContext(),"Error: Invalid DataSet",Toast.LENGTH_SHORT).show();
                    return;
                }
                int count_x =0,count_y =0;
                for (int i = 0; i < x_state.length; i++)
                {
                    if (x_state[i] == true && y_state[i] == true)
                    {
                        Toast.makeText(getApplicationContext(),"Error: Check the Switches Again,\nX_Train & Y_Train Field Found Common.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (x_state[i])
                        count_x += 1;
                    if (y_state[i])
                        count_y += 1;
                }
                if (count_y  > 1)
                {
                    Toast.makeText(getApplicationContext(),"Error: More than one field selected for Y_Train",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (count_y == 0)
                {
                    Toast.makeText(getApplicationContext(),"Error: Select atleast one field for Y_Train",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (count_x ==0)
                {
                    Toast.makeText(getApplicationContext(),"Error: Select atleast one field for X_Train",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(),"Your Model is being Trained",Toast.LENGTH_SHORT).show();
                ArrayList<Integer> dataset_iterator = new ArrayList<>();
                for (int i = 0; i < x_state.length; i++) 
                {
                    if (x_state[i])
                        dataset_iterator.add(i);
                }
                for (int i = 0; i < y_state.length; i++)
                {
                    if (y_state[i])
                        dataset_iterator.add(i);
                }
                
                ArrayList<String> forward_dataset = new ArrayList<>();
                for (int i = 0; i < dataset.size(); i++)
                {
                    for (int j: dataset_iterator)
                    {
                        forward_dataset.add(dataset.get(i)[j]);
                    }
                }
                String[] output = new String[dataset_iterator.size()+1]; //R2 Score + B0 + BnXi
                output = stringarrayfromJNI(dataset.size(),dataset_iterator.size(),forward_dataset);
                Toast.makeText(getApplicationContext(),"Model Trained Successfully",Toast.LENGTH_SHORT).show();
                if (output != null)
                {
                    Intent result = new Intent(csv_dashboard.this,Final.class);

                    result.putExtra("result",output);
                    result.putExtra("header",header);
                    result.putExtra("header_order",dataset_iterator);
                    startActivity(result);
                }

            }
        });

    }

    public native String[] stringarrayfromJNI(int rows,int cols,ArrayList<String> dataset);
}