package com.thoughtmechanix.licenses.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomStreamChannels {

    /*
    * Annotation for an INPUT channel
    * Defines the name of the channel
    * Methods with this annotation must return SubscribeChannel class
    * */
    @Input("inBoundOrgChanges")
    SubscribableChannel inBoundOrg();

    /*
    * Annotation for OUTPUT channel
    * Methods with this annotation must return MessageChannel class
    * */
    @Output("outBoundOrgChanges")
    MessageChannel outBoundOrg();
}
