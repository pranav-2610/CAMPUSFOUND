package com.campus.lostfound.domain; import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="found_reports") public class FoundReport extends ItemReport {
 private String foundTime; private String storageLocation; @Column(length=2000) private String privateIdentificationDetails;
 protected FoundReport(){}
 public FoundReport(String a,String b,String c,String d,LocalDate e,String f,User u,String time,String storage,String secret){super(a,b,c,d,e,f,u,ReportType.FOUND);foundTime=time;storageLocation=storage;privateIdentificationDetails=secret;}
}
