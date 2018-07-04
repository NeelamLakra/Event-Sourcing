import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import command.EmployeeCommand;
import event.EmployeeEvent;
import state.EmployeeState;

import java.time.LocalDateTime;
import java.util.Optional;

public class EmployeeEntity extends PersistentEntity<EmployeeCommand, EmployeeEvent, EmployeeState> {
    
    
    @Override
    public Behavior initialBehavior(Optional<EmployeeState> snapshotState){
        
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                EmployeeState.builder().employee(Optional.empty()).timestamp(LocalDateTime.now().toString()).build());
    
        
        behaviorBuilder.setCommandHandler(EmployeeCommand.createEmployee.class,(cmd,ctx)->
                ctx.thenPersist(EmployeeEvent.employeeCreated.builder().employee(cmd.getEmployee()).entityId("employee").build(),
                        event ->ctx.reply(Done.getInstance()))
        );
        
        behaviorBuilder.setEventHandler(EmployeeEvent.employeeCreated.class,
                event-> EmployeeState.builder().employee(Optional.of(event.getEmployee())).timestamp(LocalDateTime.now().toString()).build());
        
        
        behaviorBuilder.setCommandHandler(EmployeeCommand.updateEmployee.class,(cmd,ctx)->
        ctx.thenPersist(EmployeeEvent.employeeUpdated.builder().employee(cmd.getEmployee()).entityId("employee").build(),
                event->ctx.reply(Done.getInstance()))
        );
        
        behaviorBuilder.setEventHandler(EmployeeEvent.employeeUpdated.class,
                event -> EmployeeState.builder().employee(Optional.of(event.getEmployee())).timestamp(LocalDateTime.now().toString()).build());
        
        
        behaviorBuilder.setCommandHandler(EmployeeCommand.deleteEmployee.class,(cmd,ctx)->
        ctx.thenPersist(EmployeeEvent.employeeDeleted.builder().employee(cmd.getEmployee()).entityId("employee").build(),
                event->ctx.reply(Done.getInstance()))
        );
        
        behaviorBuilder.setEventHandler(EmployeeEvent.employeeDeleted.class,
                event->EmployeeState.builder().employee(Optional.of(event.getEmployee())).build());
        
        
        behaviorBuilder.setReadOnlyCommandHandler(EmployeeCommand.EmployeeCurrentState.class,(cmd, ctx)->
        ctx.reply(state().getEmployee())
        );
        
        return behaviorBuilder.build();
    }
}
