package com.store.Online.Store.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    @Column(name = "role_name")
    private String roleName;

    @JsonIgnore
    @OneToMany(mappedBy = "roleId")
    private Collection<User> user;

    public Role() {
    }

    public Role(String roleName){
        this.roleName=roleName;
    }

    public Role(int id,String roleName){
        this.roleName=roleName;
        this.roleId=id;
    }

    @Override
    public String toString(){
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
