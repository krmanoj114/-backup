package com.tpex.commonfiles;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
@Data
public class Report {
	
	
    @NotBlank(message = "Name is mandatory")
    private String reportName;
    
    @Min(0)
    @Max(10)
    private int count;
    
    @NotBlank(message = "comments is mandatory")
    private String comments;
    
    
    

}
