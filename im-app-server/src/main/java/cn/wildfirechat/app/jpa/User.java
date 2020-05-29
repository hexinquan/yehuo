package cn.wildfirechat.app.jpa;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_user")
@Data
public class User {
    @Id
    private int id;
    @Basic
    @Column(name = "_uid")
    private String uid;
    @Basic
    @Column(name = "_name")
    private String name;
    @Basic
    @Column(name = "_display_name")
    private String displayName;
    @Basic
    @Column(name = "_gender")
    private int gender;
    @Basic
    @Column(name = "_portrait")
    private String portrait;
    @Basic
    @Column(name = "_mobile")
    private String mobile;
    @Basic
    @Column(name = "_email")
    private String email;
    @Basic
    @Column(name = "_address")
    private String address;
    @Basic
    @Column(name = "_company")
    private String company;
    @Basic
    @Column(name = "_social")
    private String social;
    @Basic
    @Column(name = "_passwd_md5")
    private String passwdMd5;
    @Basic
    @Column(name = "_salt")
    private String salt;
    @Basic
    @Column(name = "_extra")
    private String extra;
    @Basic
    @Column(name = "_type")
    private Byte type;
    @Basic
    @Column(name = "_dt")
    private long dt;
    @Basic
    @Column(name = "_deleted")
    private Byte deleted;
    @Basic
    @Column(name = "_balance_amount")
    private Integer balanceAmount;
    @Basic
    @Column(name = "_role_id")
    private Integer roleId;
    @Basic
    @Column(name = "pay_password")
    private String payPassword;
}
