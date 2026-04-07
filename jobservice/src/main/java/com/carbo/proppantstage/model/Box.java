package com.carbo.proppantstage.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Box extends ProppantContainer {
    @Field("total")
    private Float total;

    @Field("designVolume")
    private Float designVolume;

    @Field("remainOnTickets")
    private Float remainOnTickets;

    @Field("note")
    private String note;

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getDesignVolume() {
        return designVolume;
    }

    public void setDesignVolume(Float designVolume) {
        this.designVolume = designVolume;
    }

    public Float getRemainOnTickets() {
        return remainOnTickets;
    }

    public void setRemainOnTickets(Float remainOnTickets) {
        this.remainOnTickets = remainOnTickets;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
