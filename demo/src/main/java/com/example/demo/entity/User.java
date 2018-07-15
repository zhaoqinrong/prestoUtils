package com.example.demo.entity;

import com.example.demo.configuration.Column;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    Long id;
    @Column(name = "username")
    String username="123";
    @Column(name = "password")
    String password;
    @Column(name = "date")
    Date date;
    @Column(name = "date1")
    java.sql.Date date1;
    @Column(name = "isnum")
    Boolean aBoolean;
}
