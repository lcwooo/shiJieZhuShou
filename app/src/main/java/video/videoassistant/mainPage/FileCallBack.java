package video.videoassistant.mainPage;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class FileCallBack implements Callback<ResponseBody> {


    private String destFileDir; //存储地址
    private String destFileName; //存储文件名

    public FileCallBack(Context context, String destFileName) {
        this.destFileName = destFileName;
        destFileDir = context.getFilesDir().getAbsolutePath();
    }

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }


    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        new DownloadAsync(destFileDir,destFileName).execute(response.body());
    }


    class DownloadAsync extends AsyncTask<ResponseBody, Progress, Progress> {

        private String destFileDir; //存储地址
        private String destFileName; //存储文件名

        public DownloadAsync(String destFileDir, String destFileName) {
            this.destFileDir = destFileDir;
            this.destFileName = destFileName;
        }

        @Override
        protected Progress doInBackground(ResponseBody... responseBodies) {
            Progress progress = new Progress();

            ResponseBody body = responseBodies[0];
            File file = new File(destFileDir + File.separator + destFileName);
            FileOutputStream fos = null;
            InputStream is;

            progress.fileName = file.getName();
            progress.filePath = file.getPath();

            try {
                progress.status = Progress.LOADING;
                progress.totalSize = body.contentLength();
                fos = new FileOutputStream(file);
                is = body.byteStream();
                byte[] buffer = new byte[8192];

                int len;
                long downloaded = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    downloaded += len;
                    progress.downloadedSize = downloaded;
                    publishProgress(progress);
                }
                fos.flush();
                progress.status = Progress.FINISH;
                is.close();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
                progress.status = Progress.ERROR;
            }
            return progress;
        }


        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);
            onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Progress progress) {
            super.onPostExecute(progress);
            if(progress.status == Progress.FINISH)
                onSuccess(new File(progress.filePath),progress);
        }
    }

    public abstract void onSuccess(File file, Progress progress);
    public abstract void onProgress(Progress progress);

    public class Progress {
        public static final int NONE = 0;         //无状态
        public static final int WAITING = 1;      //等待
        public static final int LOADING = 2;      //下载中
        public static final int PAUSE = 3;        //暂停
        public static final int ERROR = 4;        //错误
        public static final int FINISH = 5;       //完成

        public long totalSize = 0;
        public long downloadedSize = 0;
        public String fileName;
        public String filePath;
        public int status;
        public String url;
        public String tag;
    }

}
