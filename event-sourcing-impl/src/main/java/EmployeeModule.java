import com.event.sourcing.api.EmployeeService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class EmployeeModule extends AbstractModule implements ServiceGuiceSupport {
    
    @Override
     protected void configure() {
        bindServices(serviceBinding(EmployeeService.class,EmployeeServiceImpl.class));
    }
}
