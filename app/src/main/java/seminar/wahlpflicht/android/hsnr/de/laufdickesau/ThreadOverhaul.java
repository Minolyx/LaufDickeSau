package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

//TODO: Do NOT touch this class!!!! ################################################################
final public class ThreadOverhaul<T> extends Thread {

    private static ArrayList<ThreadOverhaul> queue = null;
    private int milliSeconds = 0, loop = 1, counter = 0, proceed = 0;
    private T obj = null;
    private boolean reuseThread = false;
    private String array[] = null;
    private Method methodList[] = null;
    private boolean infinity = false;
    private boolean killable = true;
    private boolean restartable = true;
    private boolean wakeable = true;
    private boolean snooze = true;

    public final static int LOOP_INFINIT = -1;
    public final static boolean REMEMBER = true;

    ThreadOverhaul(String name, int milliSeconds, String invokeMethods, T obj, boolean reuseThread, int loop) {

        if (queue == null)
            queue = new ArrayList<ThreadOverhaul>();

        if(!this.getName().equals(name)) this.setName(name);
        else return;

        this.milliSeconds = milliSeconds;
        this.loop = this.counter = loop;
        this.obj = obj;
        this.reuseThread = reuseThread;
        this.array = invokeMethods.split(" ");

        if (this.loop == -1) {
            this.infinity = true;
            this.killable = false;
        }

        if (this.reuseThread)
            queue.add(this);
    }

    ThreadOverhaul(int milliSeconds, String invokeMethods, T obj, boolean reuseThread, int loop) {

        if (queue == null)
            queue = new ArrayList<ThreadOverhaul>();

        this.milliSeconds = milliSeconds;
        this.loop = this.counter = loop;
        this.obj = obj;
        this.reuseThread = reuseThread;
        this.array = invokeMethods.split(" ");

        if (this.loop == -1) {
            this.infinity = true;
            this.killable = false;
        }

        if (this.reuseThread)
            queue.add(this);
    }

    ThreadOverhaul(int milliSeconds, String invokeMethods, T obj) {

        this.milliSeconds = milliSeconds;
        this.obj = obj;
        this.array = invokeMethods.split(" ");

    }

    @Override
    public void run() {

        while (this.loop > 0 || infinity) {

            this.loop--;
            this.proceed = loop;
            this.restartable = true;
            this.wakeable = true;
            this.snooze = false;

            try {
                sleep(milliSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                methodList = obj.getClass().getMethods();
                for (Method method : methodList) {
                    for (String str : array) {
                        if (method.getName().equals(str))
                            method.invoke(obj, (Object[]) null);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (this.loop == 0) {
                while (this.reuseThread && this.restartable && this.loop > -1) {
                    try {
                        Log.d("Hibernating", "Thread \"".concat(this.getName()).concat("\": now hibernating..\n"));
                        while (!snooze) sleep(Integer.MAX_VALUE);

                    } catch (InterruptedException e) {
                        if (!wakeable) this.loop = proceed;
                        else if (wakeable) this.loop = this.counter;
                        if (this.killable)
                            Log.d("Hibernating", "Thread \"".concat(this.getName()).concat("\": hibernating interrupted!\n"));

                    }
                }
            }

            if (!this.killable) {
                this.loop = 0;
                if (this.loop != -1) queue.remove(this);
            }
        }
    }

    @Override
    public void start() {

        try {
            if (!isAlive()) {
                super.start();
                Log.d("Start", "Thread \"".concat(this.getName()).concat("\": started!\n"));
            }
        } catch (Exception e) {
        }

        try {
            if (!isInterrupted() && !this.infinity) {
                this.restartable = false;
                this.snooze = true;
                this.loop = this.counter;
                interrupt();
            }
        } catch (Exception e) {
            Log.d("Exception", "Exception on restart");
        }

    }

    protected ThreadOverhaul hibernate() {
        if (this.killable && this.restartable && this.loop > -1) {
            this.proceed = this.loop;
            this.loop = 0;
        }
        return this;
    }

    protected ThreadOverhaul wake() {
        if (this.killable && this.restartable) {
            if (!isInterrupted()) {
                this.snooze = true;
                this.restartable = false;
                this.wakeable = false;
                this.loop = this.proceed;
                interrupt();
            }
        }
        return this;
    }

    protected ThreadOverhaul getThreadByName(String str) {

        try {
            if (queue != null) {

                for (ThreadOverhaul thr : queue) {
                    if (thr.getName() == str) return thr;
                }

                System.err.print("\n Thread \"" + str + "\" not found or needs to be initialized with a name!\n");
            }
        } catch (Exception e) {
            Log.d("Exception", "Exception on getThreadByName: ".concat(e.getMessage()));
        }

        return this;
    }



    protected ThreadOverhaul setRefreshRate(int milliSeconds) {
        this.milliSeconds = milliSeconds;
        Log.d("Hibernating", "Thread \"".concat(this.getName()).concat("\": refresh rate changed!\n"));
        return this;
    }

    protected ThreadOverhaul kill() {
        if (killable && this.loop > -1) {
            this.reuseThread = false;
            this.infinity = false;
            this.killable = false;
            this.snooze = false;
            Log.d("Hibernating", "Thread \"".concat(this.getName()).concat("\": has terminated..\n"));
            if (!this.isInterrupted()) this.interrupt();
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThreadOverhaul)) return false;

        ThreadOverhaul<?> that = (ThreadOverhaul<?>) o;

        if (milliSeconds != that.milliSeconds) return false;
        if (loop != that.loop) return false;
        if (counter != that.counter) return false;
        if (reuseThread != that.reuseThread) return false;
        if (infinity != that.infinity) return false;
        if (killable != that.killable) return false;
        if (restartable != that.restartable) return false;
        return Arrays.equals(methodList, that.methodList);

    }

    @Override
    public int hashCode() {
        int result = milliSeconds;
        result = 31 * result + loop;
        result = 31 * result + counter;
        result = 31 * result + (obj != null ? obj.hashCode() : 0);
        result = 31 * result + (reuseThread ? 1 : 0);
        result = 31 * result + Arrays.hashCode(array);
        result = 31 * result + Arrays.hashCode(methodList);
        result = 31 * result + (infinity ? 1 : 0);
        result = 31 * result + (killable ? 1 : 0);
        result = 31 * result + (restartable ? 1 : 0);
        return result;
    }

}
