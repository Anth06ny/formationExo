
package com.example.anthony.maps.beans.metro;

import java.util.List;

public class Parameters {

    private List<String> dataset = null;
    private String timezone;
    private Integer rows;
    private String format;

    public List<String> getDataset() {
        return dataset;
    }

    public void setDataset(List<String> dataset) {
        this.dataset = dataset;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
