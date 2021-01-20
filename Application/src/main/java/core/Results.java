package core;

import java.util.ArrayList;

public class Results {
    private ArrayList<String[]> results;

    public Results(ArrayList<String[]> results) {
        this.results = results;
    }

    public String getTopResult(int col) {
        return this.results.get(0)[col];
    }

    public boolean isEmpty() {
        return this.results.size() == 0;
    }

    public String[][] getRowData() {
        String[][] rowData = new String[this.results.size()][];

        for (int i = 0; i < this.results.size(); ++i) {
            rowData[i] = this.results.get(i);
        }

        return rowData;
    }

    public ArrayList<String[]> getResults() {
        return this.results;
    }

    public void setResults(ArrayList<String[]> results) {
        this.results = results;
    }
}