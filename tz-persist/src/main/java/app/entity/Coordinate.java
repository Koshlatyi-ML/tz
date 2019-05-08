package app.entity;


import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Coordinate {

    private float lat;
    private float lng;
}
