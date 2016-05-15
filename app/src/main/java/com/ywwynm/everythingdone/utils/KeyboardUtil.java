package com.ywwynm.everythingdone.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ywwynm on 2015/7/27.
 * show/hide keyboard and etc
 */
public class KeyboardUtil {

    public static final String TAG = "EverythingDone$KeyboardUtil";

    public static final int HIDE_DELAY = 280;

    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }

        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        view.clearFocus();

        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Window window) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public interface KeyboardCallback {
        void onKeyboardShow(int keyboardHeight);
        void onKeyboardHide();
    }

    public static void addKeyboardCallback(final Window window, final KeyboardCallback callback) {
        final View decorView = window.getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    private Rect r = new Rect();
                    private int initialDiff = -1;
                    private float possibleKeyboardHeight =
                            96 * DisplayUtil.getScreenDensity(decorView.getContext());
                    private boolean mKeyboardOpened = false;

                    @Override
                    public void onGlobalLayout() {
                        // decor.getRoot.getHeight is always full height
                        int fullHeight = decorView.getRootView().getHeight();
                        Log.d(TAG, "fullHeight: " + fullHeight);

                        // r will be populated with the coordinates of your view that area still visible.
                        decorView.getWindowVisibleDisplayFrame(r);
                        Log.d(TAG, "r.bottom - r.top: " + (r.bottom - r.top));

                        // get the height diff as px
                        int heightDiff = fullHeight - (r.bottom - r.top);
                        Log.d(TAG, "height diff: " + heightDiff);

                        // set the initialDiff at the beginning.
                        if (initialDiff == -1) {
                            initialDiff = heightDiff;
                        }

                        int diff = heightDiff - initialDiff;
                        Log.d(TAG, "heightDiff - initialDiff: " + diff);
                        Log.d(TAG, " ");

                        // if it could be a keyboard add the padding to the view
                        if (diff > possibleKeyboardHeight) {
                            if (!mKeyboardOpened) {
                                mKeyboardOpened = true;
                                if (callback != null) {
                                    callback.onKeyboardShow(diff);
                                }
                            }
                        } else if (diff == 0) {
                            if (mKeyboardOpened) {
                                if (callback != null) {
                                    callback.onKeyboardHide();
                                }
                                mKeyboardOpened = false;
                            }
                        }
                    }
                });
    }
}
