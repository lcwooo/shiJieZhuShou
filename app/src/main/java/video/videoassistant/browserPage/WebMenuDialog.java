package video.videoassistant.browserPage;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.azhon.basic.dialog.AlertDialog;

import video.videoassistant.R;
import video.videoassistant.databinding.DialogWebviewBinding;

public class WebMenuDialog {

    private DialogWebviewBinding binding;
    public Context mContext;
    public AlertDialog dialog;
    public BrowserModel model;


    public WebMenuDialog(Context mContext, BrowserModel model) {
        this.mContext = mContext;
        this.model = model;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_webview, null, false);
        binding.setModel(model);
        dialog = new AlertDialog.Builder(mContext)
                .setContentView(binding.getRoot())
                .fullWidth()
                .formBottom(true)
                .addDefaultAnimation()
                .setCancelable(true)
                .create();
        dialog.show();
    }
    public void dismiss(){
        dialog.dismiss();
    }
}
