package org.greenrobot.greendao.async;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.database.Database;

class AsyncOperationExecutor implements Runnable, Callback {
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private int countOperationsCompleted;
    private int countOperationsEnqueued;
    private volatile boolean executorRunning;
    private Handler handlerMainThread;
    private int lastSequenceNumber;
    private volatile AsyncOperationListener listener;
    private volatile AsyncOperationListener listenerMainThread;
    private volatile int maxOperationCountToMerge = 50;
    private final BlockingQueue<AsyncOperation> queue = new LinkedBlockingQueue();
    private volatile int waitForMergeMillis = 50;

    AsyncOperationExecutor() {
    }

    public void enqueue(AsyncOperation operation) {
        synchronized (this) {
            int i = this.lastSequenceNumber + 1;
            this.lastSequenceNumber = i;
            operation.sequenceNumber = i;
            this.queue.add(operation);
            this.countOperationsEnqueued++;
            if (!this.executorRunning) {
                this.executorRunning = true;
                executorService.execute(this);
            }
        }
    }

    public int getMaxOperationCountToMerge() {
        return this.maxOperationCountToMerge;
    }

    public void setMaxOperationCountToMerge(int maxOperationCountToMerge) {
        this.maxOperationCountToMerge = maxOperationCountToMerge;
    }

    public int getWaitForMergeMillis() {
        return this.waitForMergeMillis;
    }

    public void setWaitForMergeMillis(int waitForMergeMillis) {
        this.waitForMergeMillis = waitForMergeMillis;
    }

    public AsyncOperationListener getListener() {
        return this.listener;
    }

    public void setListener(AsyncOperationListener listener) {
        this.listener = listener;
    }

    public AsyncOperationListener getListenerMainThread() {
        return this.listenerMainThread;
    }

    public void setListenerMainThread(AsyncOperationListener listenerMainThread) {
        this.listenerMainThread = listenerMainThread;
    }

    public synchronized boolean isCompleted() {
        return this.countOperationsEnqueued == this.countOperationsCompleted;
    }

    public synchronized void waitForCompletion() {
        while (!isCompleted()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
    }

    public synchronized boolean waitForCompletion(int maxMillis) {
        if (!isCompleted()) {
            try {
                wait((long) maxMillis);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
        return isCompleted();
    }

    public void run() {
        while (true) {
            try {
                AsyncOperation operation = (AsyncOperation) this.queue.poll(1, TimeUnit.SECONDS);
                if (operation == null) {
                    synchronized (this) {
                        operation = (AsyncOperation) this.queue.poll();
                        if (operation == null) {
                            this.executorRunning = false;
                            this.executorRunning = false;
                            return;
                        }
                    }
                }
                if (operation.isMergeTx()) {
                    AsyncOperation operation2 = (AsyncOperation) this.queue.poll((long) this.waitForMergeMillis, TimeUnit.MILLISECONDS);
                    if (operation2 != null) {
                        if (operation.isMergeableWith(operation2)) {
                            mergeTxAndExecute(operation, operation2);
                        } else {
                            executeOperationAndPostCompleted(operation);
                            executeOperationAndPostCompleted(operation2);
                        }
                    }
                }
                executeOperationAndPostCompleted(operation);
            } catch (InterruptedException e) {
                DaoLog.w(Thread.currentThread().getName() + " was interruppted", e);
                this.executorRunning = false;
                return;
            } catch (Throwable th) {
                this.executorRunning = false;
            }
        }
    }

    private void mergeTxAndExecute(AsyncOperation operation1, AsyncOperation operation2) {
        ArrayList<AsyncOperation> mergedOps = new ArrayList();
        mergedOps.add(operation1);
        mergedOps.add(operation2);
        Database db = operation1.getDatabase();
        db.beginTransaction();
        boolean success = false;
        int i = 0;
        while (i < mergedOps.size()) {
            try {
                AsyncOperation operation = (AsyncOperation) mergedOps.get(i);
                executeOperation(operation);
                if (!operation.isFailed()) {
                    if (i == mergedOps.size() - 1) {
                        AsyncOperation peekedOp = (AsyncOperation) this.queue.peek();
                        if (i >= this.maxOperationCountToMerge || !operation.isMergeableWith(peekedOp)) {
                            db.setTransactionSuccessful();
                            success = true;
                            break;
                        }
                        AsyncOperation removedOp = (AsyncOperation) this.queue.remove();
                        if (removedOp != peekedOp) {
                            throw new DaoException("Internal error: peeked op did not match removed op");
                        }
                        mergedOps.add(removedOp);
                    }
                    i++;
                }
            } finally {
                try {
                    db.endTransaction();
                } catch (RuntimeException e) {
                    DaoLog.i("Async transaction could not be ended, success so far was: " + success, e);
                    success = false;
                }
            }
        }
        Iterator it;
        if (success) {
            int mergedCount = mergedOps.size();
            it = mergedOps.iterator();
            while (it.hasNext()) {
                AsyncOperation asyncOperation = (AsyncOperation) it.next();
                asyncOperation.mergedOperationsCount = mergedCount;
                handleOperationCompleted(asyncOperation);
            }
            return;
        }
        DaoLog.i("Reverted merged transaction because one of the operations failed. Executing operations one by one instead...");
        it = mergedOps.iterator();
        while (it.hasNext()) {
            asyncOperation = (AsyncOperation) it.next();
            asyncOperation.reset();
            executeOperationAndPostCompleted(asyncOperation);
        }
    }

    private void handleOperationCompleted(AsyncOperation operation) {
        operation.setCompleted();
        AsyncOperationListener listenerToCall = this.listener;
        if (listenerToCall != null) {
            listenerToCall.onAsyncOperationCompleted(operation);
        }
        if (this.listenerMainThread != null) {
            if (this.handlerMainThread == null) {
                this.handlerMainThread = new Handler(Looper.getMainLooper(), this);
            }
            this.handlerMainThread.sendMessage(this.handlerMainThread.obtainMessage(1, operation));
        }
        synchronized (this) {
            this.countOperationsCompleted++;
            if (this.countOperationsCompleted == this.countOperationsEnqueued) {
                notifyAll();
            }
        }
    }

    private void executeOperationAndPostCompleted(AsyncOperation operation) {
        executeOperation(operation);
        handleOperationCompleted(operation);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executeOperation(org.greenrobot.greendao.async.AsyncOperation r5) {
        /*
        r4 = this;
        r2 = java.lang.System.currentTimeMillis();
        r5.timeStarted = r2;
        r1 = org.greenrobot.greendao.async.AsyncOperationExecutor.AnonymousClass1.$SwitchMap$org$greenrobot$greendao$async$AsyncOperation$OperationType;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.type;	 Catch:{ Throwable -> 0x002e }
        r2 = r2.ordinal();	 Catch:{ Throwable -> 0x002e }
        r1 = r1[r2];	 Catch:{ Throwable -> 0x002e }
        switch(r1) {
            case 1: goto L_0x0038;
            case 2: goto L_0x0040;
            case 3: goto L_0x004a;
            case 4: goto L_0x0056;
            case 5: goto L_0x005e;
            case 6: goto L_0x0068;
            case 7: goto L_0x0074;
            case 8: goto L_0x007c;
            case 9: goto L_0x0086;
            case 10: goto L_0x0092;
            case 11: goto L_0x009a;
            case 12: goto L_0x00a4;
            case 13: goto L_0x00b0;
            case 14: goto L_0x00b5;
            case 15: goto L_0x00ba;
            case 16: goto L_0x00ca;
            case 17: goto L_0x00da;
            case 18: goto L_0x00e3;
            case 19: goto L_0x00ea;
            case 20: goto L_0x00f6;
            case 21: goto L_0x0100;
            case 22: goto L_0x010e;
            default: goto L_0x0013;
        };	 Catch:{ Throwable -> 0x002e }
    L_0x0013:
        r1 = new org.greenrobot.greendao.DaoException;	 Catch:{ Throwable -> 0x002e }
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x002e }
        r2.<init>();	 Catch:{ Throwable -> 0x002e }
        r3 = "Unsupported operation: ";
        r2 = r2.append(r3);	 Catch:{ Throwable -> 0x002e }
        r3 = r5.type;	 Catch:{ Throwable -> 0x002e }
        r2 = r2.append(r3);	 Catch:{ Throwable -> 0x002e }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x002e }
        r1.<init>(r2);	 Catch:{ Throwable -> 0x002e }
        throw r1;	 Catch:{ Throwable -> 0x002e }
    L_0x002e:
        r0 = move-exception;
        r5.throwable = r0;
    L_0x0031:
        r2 = java.lang.System.currentTimeMillis();
        r5.timeCompleted = r2;
        return;
    L_0x0038:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1.delete(r2);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0040:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002e }
        r2.deleteInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x004a:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r2.deleteInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0056:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1.insert(r2);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x005e:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002e }
        r2.insertInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0068:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r2.insertInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0074:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1.insertOrReplace(r2);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x007c:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002e }
        r2.insertOrReplaceInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0086:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r2.insertOrReplaceInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0092:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1.update(r2);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x009a:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Iterable) r1;	 Catch:{ Throwable -> 0x002e }
        r2.updateInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00a4:
        r2 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r1 = (java.lang.Object[]) r1;	 Catch:{ Throwable -> 0x002e }
        r2.updateInTx(r1);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00b0:
        r4.executeTransactionRunnable(r5);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00b5:
        r4.executeTransactionCallable(r5);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00ba:
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (org.greenrobot.greendao.query.Query) r1;	 Catch:{ Throwable -> 0x002e }
        r1 = r1.forCurrentThread();	 Catch:{ Throwable -> 0x002e }
        r1 = r1.list();	 Catch:{ Throwable -> 0x002e }
        r5.result = r1;	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00ca:
        r1 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = (org.greenrobot.greendao.query.Query) r1;	 Catch:{ Throwable -> 0x002e }
        r1 = r1.forCurrentThread();	 Catch:{ Throwable -> 0x002e }
        r1 = r1.unique();	 Catch:{ Throwable -> 0x002e }
        r5.result = r1;	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00da:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1.deleteByKey(r2);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00e3:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1.deleteAll();	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00ea:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1 = r1.load(r2);	 Catch:{ Throwable -> 0x002e }
        r5.result = r1;	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x00f6:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r1 = r1.loadAll();	 Catch:{ Throwable -> 0x002e }
        r5.result = r1;	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x0100:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r1.count();	 Catch:{ Throwable -> 0x002e }
        r1 = java.lang.Long.valueOf(r2);	 Catch:{ Throwable -> 0x002e }
        r5.result = r1;	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
    L_0x010e:
        r1 = r5.dao;	 Catch:{ Throwable -> 0x002e }
        r2 = r5.parameter;	 Catch:{ Throwable -> 0x002e }
        r1.refresh(r2);	 Catch:{ Throwable -> 0x002e }
        goto L_0x0031;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.greenrobot.greendao.async.AsyncOperationExecutor.executeOperation(org.greenrobot.greendao.async.AsyncOperation):void");
    }

    private void executeTransactionRunnable(AsyncOperation operation) {
        Database db = operation.getDatabase();
        db.beginTransaction();
        try {
            ((Runnable) operation.parameter).run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void executeTransactionCallable(AsyncOperation operation) throws Exception {
        Database db = operation.getDatabase();
        db.beginTransaction();
        try {
            operation.result = ((Callable) operation.parameter).call();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean handleMessage(Message msg) {
        AsyncOperationListener listenerToCall = this.listenerMainThread;
        if (listenerToCall != null) {
            listenerToCall.onAsyncOperationCompleted((AsyncOperation) msg.obj);
        }
        return false;
    }
}
