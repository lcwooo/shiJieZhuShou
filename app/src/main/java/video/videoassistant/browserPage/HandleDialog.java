package video.videoassistant.browserPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.azhon.basic.dialog.AlertDialog;
import com.azhon.basic.utils.TimeUtil;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.cloudPage.TypeAdapter;
import video.videoassistant.cloudPage.YearAdapter;
import video.videoassistant.databinding.DialogHandleBinding;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.util.UiUtil;

public class HandleDialog {

    DialogHandleBinding binding;
    public Context mContext;
    public AlertDialog dialog;
    List<HandleEntity> list;
    private BrowserFlowAdapter adapter;
    private BrowserModel browserModel;

    public HandleDialog(Context mContext, List<HandleEntity> list, BrowserModel browserModel) {
        this.mContext = mContext;
        this.list = list;
        this.browserModel = browserModel;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_handle, null, false);
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
        adapter = new BrowserFlowAdapter(list, mContext);
        binding.flowView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String typeId, String name) {
                dialog.dismiss();
                browserModel.lineUrl.postValue(typeId);
            }
        });
    }
}
