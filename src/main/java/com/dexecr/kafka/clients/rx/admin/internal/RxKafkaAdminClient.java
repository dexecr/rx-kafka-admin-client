package com.dexecr.kafka.clients.rx.admin.internal;

import com.dexecr.kafka.clients.rx.admin.RxAdmin;
import org.apache.kafka.clients.admin.Admin;

import java.time.Duration;
import java.util.Map;

public class RxKafkaAdminClient extends GenericRxAdminOperations implements RxAdmin {

    public RxKafkaAdminClient(Map<String, Object> conf) {
        super(Admin.create(conf));
    }

    @Override
    public void close() {
        admin.close();
    }

    @Override
    public void close(Duration timeout) {
        admin.close(timeout);
    }
}
