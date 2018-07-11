package event;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

public class EmployeeEventTag {
    public static final AggregateEventTag<EmployeeEvent> INSTANCES = AggregateEventTag.of(EmployeeEvent.class);
}
