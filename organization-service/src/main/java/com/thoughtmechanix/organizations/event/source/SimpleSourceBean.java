package com.thoughtmechanix.organizations.event.source;

import com.thoughtmechanix.organizations.model.UserContext;
import com.thoughtmechanix.organizations.model.message.OrganizationChange;
import com.thoughtmechanix.organizations.util.context.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleSourceBean {
    private Source source;

    @Autowired
    public SimpleSourceBean(Source source) {
        this.source = source;
    }

    public void publishOrChange(String action, String orgId) {
        System.out.printf("Sending Kafka Message %s for Org ID %s", action, orgId);

        /*
        * The Message to be published
        * */
        OrganizationChange change = new OrganizationChange(
                OrganizationChange.class.getTypeName(), action, orgId,
                UserContextHolder.getContext().getCorrelationId());

        /*
        * Message will be sent to the "output" channel
        * that is mentioned in the config
        * */
        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
