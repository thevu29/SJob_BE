package org.example.jobseekerservice.dto.Experience;

import lombok.Data;
import org.example.jobseekerservice.entity.EmployeeType;
import org.example.jobseekerservice.entity.LocationType;

@Data
public class ExperienceDTO {
    private String id;
    private String company;
    private String position;
    private String location;
    private LocationType locationType;
    private String description;
    private EmployeeType employeeType;
    private String startDate;
    private String endDate;
    private String jobSeekerId;
}
