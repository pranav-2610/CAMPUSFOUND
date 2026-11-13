package com.campus.lostfound.domain;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="campus_users") public class User {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private String fullName;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String passwordHash;
 @Column(nullable=false) private String campusId;
 private String department;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.STUDENT;
 protected User(){}
 public User(String name,String email,String hash,String campusId,String department,Role role){fullName=name;this.email=email.toLowerCase();passwordHash=hash;this.campusId=campusId;this.department=department;this.role=role;}
 public UUID getId(){return id;} public String getEmail(){return email;} public String getFullName(){return fullName;} public String getPasswordHash(){return passwordHash;} public Role getRole(){return role;} public void setRole(Role role){this.role=role;} public void setPasswordHash(String hash){passwordHash=hash;} public void updateProfile(String name,String department){if(name!=null&&!name.isBlank())fullName=name.trim();this.department=department;}
}
