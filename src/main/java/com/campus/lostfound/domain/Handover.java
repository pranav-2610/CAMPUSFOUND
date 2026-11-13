package com.campus.lostfound.domain;
import jakarta.persistence.*; import java.time.*; import java.util.UUID;
@Entity @Table(name="handovers") public class Handover {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false,unique=true) private Claim claim;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) private User coordinator;
 @Column(nullable=false) private String meetingLocation;
 @Column(nullable=false) private Instant scheduledAt;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private HandoverStatus status=HandoverStatus.SCHEDULED;
 private Instant ownerConfirmedAt; private Instant completedAt;
 protected Handover(){}
 public Handover(Claim claim,User coordinator,String place,Instant time){this.claim=claim;this.coordinator=coordinator;meetingLocation=place;scheduledAt=time;}
 public UUID getId(){return id;} public Claim getClaim(){return claim;} public User getCoordinator(){return coordinator;} public String getMeetingLocation(){return meetingLocation;} public Instant getScheduledAt(){return scheduledAt;} public HandoverStatus getStatus(){return status;}
 public void confirmOwner(){if(status!=HandoverStatus.SCHEDULED)throw new IllegalStateException("Handover is not awaiting owner confirmation");status=HandoverStatus.OWNER_CONFIRMED;ownerConfirmedAt=Instant.now();}
 public void complete(){if(status!=HandoverStatus.OWNER_CONFIRMED)throw new IllegalStateException("Owner confirmation is required first");status=HandoverStatus.COMPLETED;completedAt=Instant.now();}
}
