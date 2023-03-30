package org.mifos.connector.zeebe;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Contains workers that will run based on the BPMN flow.
 */
@Component
public class ZeebeWorkers {

    @PostConstruct
    public void setupWorkers() {

    }
}
