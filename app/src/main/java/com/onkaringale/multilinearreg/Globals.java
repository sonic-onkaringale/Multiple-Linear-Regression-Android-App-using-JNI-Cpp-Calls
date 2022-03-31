package com.onkaringale.multilinearreg;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class Globals extends Application {

    //Main Activity
    private List<String[]> dataset = null;

    //CSV Dashboard

    private String[] output = null;
    private String[] header = null;
    private ArrayList<Integer> header_order = null;

    public void setDataset(List<String[]> dataset) {
        this.dataset = dataset;
    }

    public List<String[]> getDataset() {
        return dataset;
    }


    public String[] getOutput() {
        return output;
    }

    public void setOutput(String[] output) {
        this.output = output;
    }

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    public ArrayList<Integer> getHeader_order() {
        return header_order;
    }

    public void setHeader_order(ArrayList<Integer> header_order) {
        this.header_order = header_order;
    }
}
