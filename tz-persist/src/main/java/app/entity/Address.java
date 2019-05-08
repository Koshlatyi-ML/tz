package app.entity;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Data;

@Data
@Embeddable
public class Address {

    private String street;

    private String suite;

    private String city;

    private String zipcode;

    @Embedded
    private Coordinate geo;
}
