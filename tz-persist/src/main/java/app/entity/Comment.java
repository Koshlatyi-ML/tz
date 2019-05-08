package app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Comment {

    @Id
    private long id;

    private String name;

    private String email;

    @Column(length = 500)
    private String body;
}
