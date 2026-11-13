package com.campus.lostfound.domain; import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="lost_reports") public class LostReport extends ItemReport {
 private String lastSeenTime; protected LostReport(){}
 public LostReport(String a,String b,String c,String d,LocalDate e,String f,User u,String time){super(a,b,c,d,e,f,u,ReportType.LOST);lastSeenTime=time;}
}
