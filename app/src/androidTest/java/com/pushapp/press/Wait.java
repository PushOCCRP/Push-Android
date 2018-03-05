//courtesy: https://gist.github.com/Peddro/1d6cafc72a9d77c274b7

package com.pushapp.press;

public class Wait {

    private static final int CHECK_INTERVAL = 100;
    private static final int TIMEOUT = 10000;

    public interface Condition {
        boolean check();
    }

    private Condition mCondition;

    public Wait(Condition condition) {
        mCondition = condition;
    }

    public void waitForIt() {
        boolean state = mCondition.check();
        long startTime = System.currentTimeMillis();
        while (!state) {
            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                throw new AssertionError("Wait timeout.");
            }
            state = mCondition.check();
        }
    }
}