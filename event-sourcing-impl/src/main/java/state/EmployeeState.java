package state;

import com.event.sourcing.api.Employee;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class EmployeeState implements CompressedJsonable {
    Optional<Employee> employee;
    String timestamp;
}
