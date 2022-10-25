package video.videoassistant.me.urlManage;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.azhon.basic.dialog.AlertDialog;

import video.videoassistant.R;
import video.videoassistant.databinding.DialogAddUrlTypeBinding;
import video.videoassistant.util.UiUtil;


public class DialogAddUrl {

    DialogAddUrlTypeBinding binding;
    public Context mContext;
    public AlertDialog dialog;
    public AddUrlListener listener;

    public interface AddUrlListener{
        void  addUrl(CollectionUrlEntity entity);
    }

    public void addUrl(AddUrlListener listener){
        this.listener = listener;
    }


    public DialogAddUrl(Context mContext) {
        this.mContext = mContext;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_add_url_type, null, false);
        binding.setView(this);
        dialog = new AlertDialog.Builder(mContext)
                .setContentView(binding.getRoot())
                .setWidthAndHeight(UiUtil.weight(mContext) * 5 / 6,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
                .addDefaultAnimation()
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public void commit() {

        if (TextUtils.isEmpty(binding.name.getText().toString())) {
            UiUtil.showToastSafe(binding.name.getHint().toString());
            return;
        }

        if (TextUtils.isEmpty(binding.url.getText().toString())) {
            UiUtil.showToastSafe(binding.url.getHint().toString());
            return;
        }
        CollectionUrlEntity entity = new CollectionUrlEntity();
        entity.setUrl(UiUtil.getHttpUrl(binding.url.getText().toString()));
        entity.setName(binding.name.getText().toString());
        entity.setRemark(binding.remark.getText().toString());
        entity.setPosition(0);
        if(listener!=null){
            listener.addUrl(entity);
        }
        dialog.dismiss();
    }
}
