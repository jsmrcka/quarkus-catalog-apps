package io.quarkus.qe.utils;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.qe.configuration.Channels;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;

public class InMemoryKafkaResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory(Channels.NEW_REPOSITORY));
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}