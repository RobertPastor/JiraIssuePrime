package com.thales.IssuePrime.RestResource;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestResourceModel {

    @XmlElement(name = "value")
    private String message;

    public RestResourceModel() {
    }

    public RestResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}