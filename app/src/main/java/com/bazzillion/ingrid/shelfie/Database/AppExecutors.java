package com.bazzillion.ingrid.shelfie.Database;

import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.*;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors instance;
    private final Executor diskIo;
    private final Executor mainThread;
    private final Executor networkIo;

    private AppExecutors(Executor diskIo, Executor mainThread, Executor networkIo){
        this.diskIo = diskIo;
        this.mainThread = mainThread;
        this.networkIo = networkIo;
    }

    public static AppExecutors getInstance() {
        if (instance == null){
            synchronized (LOCK){
                instance = new AppExecutors(Executors.newSingleThreadExecutor(), new MainThreadExecutor(), Executors.newFixedThreadPool(3));
            }

        }
        return instance;
    }

    public Executor getDiskIo() {
        return diskIo;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIo() {
        return networkIo;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
