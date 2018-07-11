package event;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class EmployeeEventProcessor extends ReadSideProcessor<EmployeeEvent> {
    
    private final CassandraSession session;
    private final CassandraReadSide readSide;
    
    private PreparedStatement writeEmployee;
    private PreparedStatement deleteEmployee;
    
    @Inject
    public EmployeeEventProcessor(CassandraSession session,CassandraReadSide readSide){
        this.session=session;
        this.readSide=readSide;
    }

    @Override
    public PSequence<AggregateEventTag<EmployeeEvent>> aggregateTags() {
        return TreePVector.singleton(EmployeeEventTag.INSTANCES);
    }
    
    @Override
    public ReadSideHandler<EmployeeEvent> buildHandler(){
        return readSide.<EmployeeEvent>builder("Employee_Offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(evtTag -> prepareWriteEmployee()
                .thenCombine(prepareDeleteEmployee(),(d1,d2)->Done.getInstance()))
                .setEventHandler(EmployeeEvent.employeeCreated.class,this::processPostAdded)
                .setEventHandler(EmployeeEvent.employeeUpdated.class,this::processPostUpdated)
                .setEventHandler(EmployeeEvent.employeeDeleted.class,this::processPostDeleted)
                .build();
    }
    
    
    private CompletionStage<Done> createTable() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS employee ( " +
                        "name TEXT primary key, salary Text, projectName Text)"
        );
    }
    
    private CompletionStage<Done> prepareWriteEmployee(){
        return session.prepare("Insert into employee (name,salary,projectName) values(?,?,?)"
        ).thenApply(preparedStatement -> {
            setWriteEmployee(preparedStatement);
            return  Done.getInstance();
        });
    }
    
    private void setWriteEmployee(PreparedStatement statement){
        this.writeEmployee= statement;
    }
    
    private CompletionStage<List<BoundStatement>> processPostAdded(EmployeeEvent.employeeCreated event) {
        BoundStatement bindWriteEmployee = writeEmployee.bind();
        bindWriteEmployee.setString("name", event.getEmployee().getName());
        bindWriteEmployee.setString("salary",event.getEmployee().getSalary());
        bindWriteEmployee.setString("projectName", event.getEmployee().getProjectName());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteEmployee));
    }
    
    private CompletionStage<List<BoundStatement>> processPostUpdated(EmployeeEvent.employeeUpdated event) {
        BoundStatement bindWriteEmployee = writeEmployee.bind();
        bindWriteEmployee.setString("name", event.getEmployee().getName());
        bindWriteEmployee.setString("salary",event.getEmployee().getSalary());
        bindWriteEmployee.setString("projectName", event.getEmployee().getProjectName());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteEmployee));
    }
    
    
    private CompletionStage<Done> prepareDeleteEmployee(){
        return session.prepare("delete from employee where name= ?")
                .thenApply(preparedStatement -> {
                    setDeleteEmployee(preparedStatement);
                    return Done.getInstance();
                });
    }
    
    private void setDeleteEmployee(PreparedStatement deleteStatement){
        this.deleteEmployee=deleteStatement;
    }
    
    private CompletionStage<List<BoundStatement>> processPostDeleted(EmployeeEvent.employeeDeleted event) {
        BoundStatement bindWriteEmployee = writeEmployee.bind();
        bindWriteEmployee.setString("name",event.getEmployee().getName());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteEmployee));
        
    }
    
    
}
