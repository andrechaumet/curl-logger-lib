package com.andre.limiter;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.nanoTime;

/**
 * monitor and regulate the number of requests within a specific time window
 *
 * @author André Chaumet
 * @date 2024-09-24
 * @version 0.3
 */
final class CycleTracker {

  private final AtomicInteger requestCount;
  private final int throughput;
  private long lapsed;

  CycleTracker(int throughput) {
    this.requestCount = new AtomicInteger();
    this.throughput = throughput;
    this.lapsed = nanoTime();
  }

  void reset(long currentTime) {
    if (currentTime - lapsed >= 1_000_000_000.0) {
      requestCount.set(0);
      lapsed = currentTime;
    }
  }

  boolean priorityPresent(boolean isPriority) {
    if (isPriority) {
      return requestCount.getAndIncrement() <= throughput;
    } else {
      return requestCount.incrementAndGet() <= throughput;
    }
  }

  int leftover() {
    return throughput - requestCount.get();
  }

  boolean exceeded() {
    return requestCount.get() > throughput;
  }

  public long lapsed() {
    return lapsed;
  }
}
