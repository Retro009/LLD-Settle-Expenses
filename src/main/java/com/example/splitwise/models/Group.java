package com.example.splitwise.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="groups")
public class Group extends BaseModel{
    private String name;
    private String description;
    @OneToMany(fetch = FetchType.LAZY)
    private List<User> users;
    @OneToMany(fetch = FetchType.LAZY)
    private List<User> admins;
}
