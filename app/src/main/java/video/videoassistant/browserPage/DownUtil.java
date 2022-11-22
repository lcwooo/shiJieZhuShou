package video.videoassistant.browserPage;

public class DownUtil {
    private volatile static DownUtil downUtil;

    public static DownUtil getInstance() {
        if (downUtil == null) {
            synchronized (DownUtil.class) {
                downUtil = new DownUtil();
            }
        }
        return downUtil;
    }


    public static void m3u8Down(){

    }
}
