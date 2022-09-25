package com.dexecr.kafka.clients.admin.rx;

import com.dexecr.kafka.clients.admin.rx.internal.RxKafkaAdminClient;

import java.util.Map;

public interface RxAdmin extends RxAdminOperations, AutoCloseable {

    /**
     * Create a new RxAdmin with the given configuration.
     *
     * @param conf The configuration.
     * @return The new RxKafkaAdminClient.
     */
    static RxAdmin create(Map<String, Object> conf) {
        return new RxKafkaAdminClient(conf);
    }

    void close();
}
