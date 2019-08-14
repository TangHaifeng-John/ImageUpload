package com.haifeng.example.iamgeupload.tool;

import android.content.Context;
import android.widget.Toast;

import com.haifeng.example.iamgeupload.message.ToastMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Toast工具类
 */
public class ToastUtil {

    Context mContext;

    private ToastUtil() {
    }

    public void init(Context context) {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    static class Holder {
        static ToastUtil toastUtil = new ToastUtil();
    }

    public static ToastUtil getInstance() {
        return ToastUtil.Holder.toastUtil;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showToast(ToastMessage toastMessage) {
        Toast.makeText(mContext, toastMessage.getData(), toastMessage.getTime()).show();
    }

    /**
     * 显示500毫秒
     *
     * @param message
     */
    public static void showShortToast(String message) {
        ToastMessage toastMessage = new ToastMessage(message);
        toastMessage.setTime(Toast.LENGTH_SHORT);
        EventBus.getDefault().post(toastMessage);
    }

    /**
     * 长显
     *
     * @param message
     */
    public static void showLongToast(String message) {
        ToastMessage toastMessage = new ToastMessage(message);
        toastMessage.setTime(Toast.LENGTH_LONG);
        EventBus.getDefault().post(toastMessage);
    }
}
