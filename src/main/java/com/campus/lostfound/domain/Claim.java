package com.campus.lostfound.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="claims",indexes=@Index(columnList="status")) public class Claim {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) private FoundReport foundReport; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User claimant;
 @Column(nullable=false,length=2000) private String reason; @Column(nullable=false,length=2000) private String privateEvidence;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ClaimStatus status=ClaimStatus.PENDING;
 @ManyToOne(fetch=FetchType.LAZY) private User reviewer; private String reviewComment; private Instant reviewedAt; @Column(nullable=false) private Instant createdAt=Instant.now(); protected Claim(){}
 public Claim(FoundReport report,User user,String reason,String evidence){foundReport=report;claimant=user;this.reason=reason;privateEvidence=evidence;}
 public UUID getId(){return id;} public ClaimStatus getStatus(){return status;} public FoundReport getFoundReport(){return foundReport;} public User getClaimant(){return claimant;} public String getReason(){return reason;} public String getPrivateEvidence(){return privateEvidence;} public User getReviewer(){return reviewer;} public String getReviewComment(){return reviewComment;} public Instant getCreatedAt(){return createdAt;}
 public void review(User staff,boolean approve,String comment){reviewer=staff;reviewComment=comment;reviewedAt=Instant.now();status=approve?ClaimStatus.APPROVED:ClaimStatus.REJECTED;}
}
