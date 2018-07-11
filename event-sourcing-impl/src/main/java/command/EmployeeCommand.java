package command;
import akka.Done;
import com.event.sourcing.api.Employee;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

public interface EmployeeCommand extends Jsonable {
    
    @Value
    @AllArgsConstructor
    @Builder
    final class createEmployee implements EmployeeCommand, PersistentEntity.ReplyType<Done>
    {
        Employee employee;
    }
    
    @Value
    @AllArgsConstructor
    @Builder
    final class updateEmployee implements EmployeeCommand,PersistentEntity.ReplyType<Done>
    {
        Employee employee;
    }
    
    @Value
    @AllArgsConstructor
    @Builder
    final class deleteEmployee implements EmployeeCommand,PersistentEntity.ReplyType<Done>
    {
        Employee employee;
    }
    
    @Value
    @Builder
    @AllArgsConstructor
    final class EmployeeCurrentState implements EmployeeCommand,PersistentEntity.ReplyType<Optional<Employee>>
    {}
}
