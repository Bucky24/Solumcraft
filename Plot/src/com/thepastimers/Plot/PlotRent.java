package com.thepastimers.Plot;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 8/15/13
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlotRent extends Table {
    public static String table = "plot_rent";
    
    int plot;
    int amount;
    Timestamp lastPaid;
    int duration;

    public int getPlot() {
        return plot;
    }

    public void setPlot(int plot) {
        this.plot = plot;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Timestamp getLastPaid() {
        return lastPaid;
    }

    public void setLastPaid(Timestamp lastPaid) {
        this.lastPaid = lastPaid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
