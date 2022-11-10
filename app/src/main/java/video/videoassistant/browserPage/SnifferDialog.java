package video.videoassistant.browserPage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.azhon.basic.dialog.AlertDialog;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.DialogHandleBinding;
import video.videoassistant.databinding.DialogSnifferBinding;

public class SnifferDialog {

    DialogSnifferBinding binding;
    public Context mContext;
    public AlertDialog dialog;
    public List<String> list;
    private BrowserModel model;

    public SnifferDialog(Context mContext, List<String> list,BrowserModel model) {
        this.mContext = mContext;
        this.list = list;
        this.model = model;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_sniffer, null, false);
        dialog = new AlertDialog.Builder(mContext)
                .setContentView(binding.getRoot())
                .fullWidth()
                .formBottom(true)
                .addDefaultAnimation()
                .setCancelable(true)
                .create();
        dialog.show();
        init();
    }

    private void init() {
        for (String s : list) {
            View view = View.inflate(mContext, R.layout.item_browser_a, null);
            TextView url = view.findViewById(R.id.url);
            TextView x5 = view.findViewById(R.id.x5);
            TextView play = view.findViewById(R.id.play);
            url.setText(s);
            x5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    model.xiuUrl.postValue("2-"+s);
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    model.xiuUrl.postValue("1-"+s);
                }
            });
            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("BrowserActivity", "onClick: "+s);
                    model.xiuUrl.postValue("3-"+s);
                }
            });

            binding.add.addView(view);
        }
    }
}
