package com.rentiq.concurrency;

import com.rentiq.model.BookingRequest;
import java.util.List;

/**
 * Producer worker task implementing Runnable.
 * Generates and submits BookingRequest instances into the shared BookingRequestBuffer.
 */
public class BookingProducer implements Runnable {
    private final BookingRequestBuffer buffer;
    private final List<BookingRequest> requestsToSubmit;
    private final long productionDelayMillis;

    public BookingProducer(BookingRequestBuffer buffer, List<BookingRequest> requestsToSubmit, long productionDelayMillis) {
        this.buffer = buffer;
        this.requestsToSubmit = requestsToSubmit;
        this.productionDelayMillis = productionDelayMillis;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] BookingProducer started. Preparing to submit %d requests...\n",
            threadName, requestsToSubmit.size());

        try {
            for (BookingRequest req : requestsToSubmit) {
                if (buffer.isShutdown() || Thread.currentThread().isInterrupted()) {
                    break;
                }
                boolean produced = buffer.produce(req);
                if (!produced) {
                    break;
                }
                if (productionDelayMillis > 0) {
                    Thread.sleep(productionDelayMillis);
                }
            }
        } catch (InterruptedException e) {
            System.out.printf("[%s] BookingProducer interrupted during sleep or wait. Exiting...\n", threadName);
            Thread.currentThread().interrupt();
        } finally {
            System.out.printf("[%s] BookingProducer finished submission work.\n", threadName);
        }
    }
}
