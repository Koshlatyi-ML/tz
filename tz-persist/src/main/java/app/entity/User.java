package app.entity;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import lombok.Data;

@Data
@Entity
@Table(name = "endUser")
public class User {

    @Id
    private long id;

    private String name;

    private String username;

    private String email;

    @Embedded
    private Address address;

    private String phone;

    private String website;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "companyName"))
    private Company company;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "endUser_id")
    private List<Post> posts;
}
