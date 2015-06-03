package com.push.app.interfaces;

/**
 * @author Bryan
 */
public interface ScrollViewMovable {
    /**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * scrollBy(int, int) or scrollTo(int, int) having been called.
     *
     * @param currentHorizontal
     *            Current horizontal scroll origin.
     * @param currentVertical
     *            Current vertical scroll origin.
     * @param previourHorizontal
     *            Previous horizontal scroll origin.
     * @param previousVertical
     *            Previous vertical scroll origin.
     */
    public void onScrollChanged(int currentHorizontal, int currentVertical,
                                int previourHorizontal, int previousVertical);
}
