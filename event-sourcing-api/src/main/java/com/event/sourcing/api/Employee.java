package com.event.sourcing.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Value
@Builder
public final class Employee {
    String name;
    String salary;
    String projectName;
    
}