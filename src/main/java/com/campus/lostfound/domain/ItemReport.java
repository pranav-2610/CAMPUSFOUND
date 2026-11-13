package com.campus.lostfound.domain;
import jakarta.persistence.*; import java.time.*; import java.util.UUID;
@Entity @Table(name="item_reports",indexes={@Index(columnList="type,status"),@Index(columnList="category")}) @Inheritance(strategy=InheritanceType.JOINED)
public abstract class ItemReport {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private String title;
 @Column(nullable=false,length=3000) private String description;
 @Column(nullable=false) private String category;
 @Column(nullable=false) private String location;
 @Column(nullable=false) private LocalDate incidentDate;
 @Column(length=3000) private String imagePath;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ReportType type;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ReportStatus status=ReportStatus.ACTIVE;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) private User reporter;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected ItemReport(){}
 protected ItemReport(String title,String description,String category,String location,LocalDate date,String image,User reporter,ReportType type){this.title=title;this.description=description;this.category=category;this.location=location;incidentDate=date;imagePath=image;this.reporter=reporter;this.type=type;}
 public UUID getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public String getCategory(){return category;} public String getLocation(){return location;} public LocalDate getIncidentDate(){return incidentDate;} public ReportType getType(){return type;} public ReportStatus getStatus(){return status;} public User getReporter(){return reporter;} public String getImagePath(){return imagePath;} public Instant getCreatedAt(){return createdAt;} public void updateDetails(String title,String description,String category,String location,LocalDate date,String image){if(title!=null&&!title.isBlank())this.title=title.trim();if(description!=null&&!description.isBlank())this.description=description.trim();if(category!=null&&!category.isBlank())this.category=category.trim();if(location!=null&&!location.isBlank())this.location=location.trim();if(date!=null)this.incidentDate=date;if(image!=null)this.imagePath=image;} public void close(){status=ReportStatus.CLOSED;} public void markClaimed(){status=ReportStatus.CLAIMED;} public void markReturned(){status=ReportStatus.RETURNED;}
}
