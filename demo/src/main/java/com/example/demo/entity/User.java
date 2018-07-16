package com.example.demo.entity;

import com.example.demo.configuration.Column;
import com.example.demo.configuration.Relation;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    Long id;
    @Column(name = "username")
    String username = "123";
    @Column(name = "password")
    String password;
    @Column(name = "date")
    Date date;
    @Column(name = "date1")
    java.sql.Date date1;
    @Column(name = "isnum")
    Boolean aBoolean;
    @Transient
    @Relation(name = "isnum")
    String arelation="!=";
    @Transient
    @Column(name = "adress")
    List<String> address;
    @Transient
    @Column(name = "phone")
    List<BigInteger> phone;
    @Transient
    @Relation(name = "phone")
    String phoneRelation = " not in";
}
