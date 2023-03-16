package video.videoassistant.cloudPage;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkRequestQueue {

    private final Queue<Observable<?>> queue;
    private final AtomicBoolean isRunning;
    private Disposable disposable;

    public NetworkRequestQueue() {
        this.queue = new LinkedList<>();
        this.isRunning = new AtomicBoolean(false);
    }

    public synchronized void add(Observable<?> observable) {
        if (isRunning.get()) {
            throw new IllegalStateException("Cannot add observable to running queue");
        }
        queue.offer(observable);
    }

    public synchronized void start() {
        if (isRunning.get()) {
            return;
        }
        isRunning.set(true);
        disposable = Observable.fromIterable(queue)
                .concatMap(observable -> observable.subscribeOn(Schedulers.io()))
                .subscribe();
    }

    public synchronized void stop() {
        if (!isRunning.get()) {
            return;
        }
        queue.clear();
        isRunning.set(false);
        disposable.dispose();
    }

}
