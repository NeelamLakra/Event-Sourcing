package com.event.sourcing.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;


import java.util.Optional;

public interface EmployeeService extends Service {
    
    ServiceCall<Employee, Done> newEmployee();
    ServiceCall<Employee,Done> updateEmployee(String name);
    ServiceCall<NotUsed,Done> deleteEmployee(String name);
    ServiceCall<NotUsed,Optional<Employee>> getEmployeeState( String name);
    

    @Override
    default Descriptor descriptor(){
        return named("event-sourcing").withCalls(
        
                restCall(Method.POST,"/api/eventSourcing/addEmployee",this::newEmployee),
                restCall(Method.PUT,"/api/eventSourcing/updateEmployee/:name",this::updateEmployee),
                restCall(Method.DELETE,"/api/eventSourcing/deleteEmployee",this::deleteEmployee),
                restCall(Method.GET,"/api/eventSourcing/getEmployee/:name",this::getEmployeeState)
        ).withAutoAcl(true);
    }
    
}
