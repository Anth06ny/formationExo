package com.example.anthony.maps.beans;

import com.example.anthony.maps.beans.tracer.Position;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Anthony on 03/05/2017.
 */

public class Station implements ClusterItem {

    private Integer number;
    private String name;
    private String address;
    private Position position;
    private Boolean banking;
    private Boolean bonus;
    private String status;
    private String contract_name;
    private Integer bike_stands;
    private Integer available_bike_stands;
    private Integer available_bikes;
    private Long last_update;

    //calculé
    private LatLng latLng;

    public Station() {
    }

    public LatLng getPosition() {
        if (latLng == null) {
            latLng = new LatLng(position.getLat(), position.getLng());
        }
        return latLng;
    }

    public Position getPositionObject() {
        return position;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Boolean getBanking() {
        return banking;
    }

    public void setBanking(Boolean banking) {
        this.banking = banking;
    }

    public Boolean getBonus() {
        return bonus;
    }

    public void setBonus(Boolean bonus) {
        this.bonus = bonus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContract_name() {
        return contract_name;
    }

    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }

    public Integer getBike_stands() {
        return bike_stands;
    }

    public void setBike_stands(Integer bike_stands) {
        this.bike_stands = bike_stands;
    }

    public Integer getAvailable_bike_stands() {
        return available_bike_stands;
    }

    public void setAvailable_bike_stands(Integer available_bike_stands) {
        this.available_bike_stands = available_bike_stands;
    }

    public Integer getAvailable_bikes() {
        return available_bikes;
    }

    public void setAvailable_bikes(Integer available_bikes) {
        this.available_bikes = available_bikes;
    }

    public Long getLast_update() {
        return last_update;
    }

    public void setLast_update(Long last_update) {
        this.last_update = last_update;
    }
}