package database;

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

    public ArrayList<String[]> getResults() {
        return this.results;
    }

    public void setResults(ArrayList<String[]> results) {
        this.results = results;
    }
}
