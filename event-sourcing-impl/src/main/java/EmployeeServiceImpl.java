import akka.Done;
import akka.NotUsed;
import com.event.sourcing.api.Employee;
import com.event.sourcing.api.EmployeeService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import command.EmployeeCommand;
import event.EmployeeEventProcessor;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class EmployeeServiceImpl implements EmployeeService {
    
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession session;


    @Inject
    public EmployeeServiceImpl(PersistentEntityRegistry registry, CassandraSession session, ReadSide readside)
    {
        this.persistentEntityRegistry=registry;
        this.session=session;
        
        persistentEntityRegistry.register(EmployeeEntity.class);
        readside.register(EmployeeEventProcessor.class);
    
    }
    
    @Override
    public ServiceCall<NotUsed, Optional<Employee>> getEmployeeState(String name) {
        return request ->{
            CompletionStage<Optional<Employee>> employee=
                    session.selectAll("select * from employee where name= ?",name)
                            .thenApply(row->row.stream()
                                    .map(result-> Employee.builder().name(result.getString("name")).projectName(result.getString("projectName")).salary(result.getString("salary"))
                                            .build()).findFirst()
                            );
            return employee;
        };
    }
    
    
    private PersistentEntityRef<EmployeeCommand> employeeEntityRef(Employee employee){
        return persistentEntityRegistry.refFor(EmployeeEntity.class,employee.getProjectName());
    }
    
    @Override
    public ServiceCall<Employee, Done> newEmployee() {
        return employee -> {
            PersistentEntityRef<EmployeeCommand> ref= employeeEntityRef(employee);
            return ref.ask(EmployeeCommand.createEmployee.builder().employee(employee).build());
            
        };
    }
    
    @Override
    public ServiceCall<Employee, Done> updateEmployee(String name) {
        return employee->{
            PersistentEntityRef<EmployeeCommand> ref =employeeEntityRef(employee);
            return ref.ask(EmployeeCommand.updateEmployee.builder().employee(employee).build());
        };
    }
    
    @Override
    public ServiceCall<NotUsed, Done> deleteEmployee(String name) {
        return request ->{
            Employee employee=Employee.builder().name(name).build();
            PersistentEntityRef<EmployeeCommand> ref = employeeEntityRef(employee);
           return ref.ask(EmployeeCommand.deleteEmployee.builder().employee(employee).build());
        };
    }
    
   
    }
