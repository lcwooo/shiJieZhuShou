package video.videoassistant.browserPage;

import android.util.Log;
import android.webkit.JavascriptInterface;


/**
 * 逻辑处理
 *
 * @author linzewu
 */
final class InJavaScriptLocalObj {

    private static final String TAG="InJavaScriptLocalObj";

    @JavascriptInterface
    public void showErrorInfo(String html) {
        //解析html，弹窗显示错误日志
        Log.i(TAG, "showErrorInfo: "+html);
    }
}
