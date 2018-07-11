package event;
import com.event.sourcing.api.Employee;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

public interface EmployeeEvent extends Jsonable, AggregateEvent<EmployeeEvent> {
    
    @Override
    default AggregateEventTagger<EmployeeEvent> aggregateTag()
    {
    return EmployeeEventTag.INSTANCES;
    }
    
    @Value
    @AllArgsConstructor
    @Builder
    final class employeeCreated implements EmployeeEvent, CompressedJsonable{
        Employee employee;
        String entityId;
        
    }
    
    @Value
    @AllArgsConstructor
    @Builder
    final class employeeUpdated implements EmployeeEvent, CompressedJsonable{
        Employee employee;
        String entityId;
    }
    
    @Value
    @AllArgsConstructor
    @Builder
    final class employeeDeleted implements EmployeeEvent, CompressedJsonable{
        Employee employee;
        String entityId;
    }
}
