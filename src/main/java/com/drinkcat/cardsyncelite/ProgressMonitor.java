package com.drinkcat.cardsyncelite;

import java.util.concurrent.atomic.AtomicLong;

public class ProgressMonitor {

    private final AtomicLong totalSize;
    private final AtomicLong transferredSize;

    public ProgressMonitor(long totalSize) {
        this.totalSize = new AtomicLong(totalSize);
        this.transferredSize = new AtomicLong(0L);
    }

    public void updateProgress(long delta) {
        transferredSize.addAndGet(delta);
    }

    public double getCurrentProgress() {
        return (double) transferredSize.get() / totalSize.get();
    }
}
