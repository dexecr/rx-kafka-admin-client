package com.dexecr.kafka.clients.rx.admin.internal;

import com.dexecr.kafka.clients.rx.admin.RxAdmin;
import org.apache.kafka.clients.admin.Admin;

import java.time.Duration;
import java.util.Map;

public class RxKafkaAdminClient extends GenericRxAdminOperations implements RxAdmin {

    public RxKafkaAdminClient(Map<String, Object> conf) {
        super(Admin.create(conf));
    }

    /**
     * Close the RxAdmin and release all associated resources.
     * <p>
     * See {@link #close(Duration)}
     */
    @Override
    public void close() {
        admin.close();
    }

    /**
     * Close the RxAdmin client and release all associated resources.
     * <p>
     * The close operation has a grace period during which current operations will be allowed to
     * complete, specified by the given duration.
     * New operations will not be accepted during the grace period. Once the grace period is over,
     * all operations that have not yet been completed will be aborted with a {@link org.apache.kafka.common.errors.TimeoutException}.
     *
     * @param timeout The time to use for the wait time.
     */
    @Override
    public void close(Duration timeout) {
        admin.close(timeout);
    }
}
