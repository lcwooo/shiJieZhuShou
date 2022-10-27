package video.videoassistant.me.jsonManage;

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


public class DialogJson {

    DialogAddUrlTypeBinding binding;
    public Context mContext;
    public AlertDialog dialog;
    public AddUrlListener listener;
    JsonEntity urlEntity;

    public interface AddUrlListener{
        void  addUrl(JsonEntity entity);
        void editUrl(JsonEntity entity);
    }

    public void addUrl(AddUrlListener listener){
        this.listener = listener;
    }


    public DialogJson(Context mContext) {
        this.mContext = mContext;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_add_url_type, null, false);
        binding.tt.setText("添加Json解析接口");
        dialog = new AlertDialog.Builder(mContext)
                .setContentView(binding.getRoot())
                .setWidthAndHeight(UiUtil.weight(mContext) * 5 / 6,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
                .addDefaultAnimation()
                .setCancelable(true)
                .create();
        dialog.show();
        init();
    }

    private void init() {
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit();
            }
        });
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

        if (!binding.url.getText().toString().contains(".")) {
            UiUtil.showToastSafe("网址不规范");
            return;
        }
        JsonEntity entity = new JsonEntity();
        entity.setUrl(UiUtil.getHttpUrl(binding.url.getText().toString()));
        entity.setName(binding.name.getText().toString());
        entity.setRemark(binding.remark.getText().toString());
        entity.setPosition(0);

        if(urlEntity==null){
            if(listener!=null){
                listener.addUrl(entity);
            }
        }else {
            urlEntity.setName(binding.name.getText().toString());
            urlEntity.setUrl(binding.url.getText().toString());
            urlEntity.setRemark(binding.remark.getText().toString());
            if(listener!=null){
                listener.editUrl(urlEntity);
            }
        }

        dialog.dismiss();
    }

    public void editInit(JsonEntity entity){
        binding.add.setText("保存");
        urlEntity = entity;
        binding.name.setText(entity.name);
        binding.url.setText(entity.url);
        binding.remark.setText(entity.getRemark());
    }
}
