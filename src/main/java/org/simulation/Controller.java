package org.simulation;

public class Controller {
    private boolean isPaused;
    private boolean isStepRequired;
    private final Object lock = new Object();

    public void pause() {
        synchronized (lock) {
            isPaused = true;
        }
    }

    public void resume() {
        synchronized (lock) {
            isPaused = false;
            lock.notifyAll();
        }
    }

    public void oneMoreMove() {
        synchronized (lock) {
            if (isPaused) {
                isStepRequired = true;
            }
            lock.notifyAll();
        }
    }

public void awaitPermission() throws InterruptedException {
    synchronized (lock) {
        while (isPaused && !isStepRequired) {
            lock.wait();
        }

        if (isStepRequired) {
            isStepRequired = false;
        }
    }
}
}
