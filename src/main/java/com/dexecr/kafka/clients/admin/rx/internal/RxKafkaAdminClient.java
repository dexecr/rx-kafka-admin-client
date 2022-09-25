package com.dexecr.kafka.clients.admin.rx.internal;

import com.dexecr.kafka.clients.admin.rx.RxAdmin;
import org.apache.kafka.clients.admin.Admin;

import java.util.Map;

public class RxKafkaAdminClient extends GenericRxAdminOperations implements RxAdmin {

    public RxKafkaAdminClient(Map<String, Object> conf) {
        super(Admin.create(conf));
    }

    @Override
    public void close() {
        admin.close();
    }
}
