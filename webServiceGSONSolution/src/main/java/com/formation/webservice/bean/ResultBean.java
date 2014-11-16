package com.formation.webservice.bean;

/**
 * Created by Anthony on 16/11/2014.
 */
public class ResultBean {

    private CityBean[] results;
    private int nbr;
    private ErrorBean errors;

    public CityBean[] getCityBean() {
        return results;
    }

    public void setCityBean(CityBean[] results) {
        this.results = results;
    }

    public int getNbr() {
        return nbr;
    }

    public void setNbr(int nbr) {
        this.nbr = nbr;
    }

    public ErrorBean getErrors() {
        return errors;
    }

    public void setErrors(ErrorBean errors) {
        this.errors = errors;
    }
}
