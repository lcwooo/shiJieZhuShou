package video.videoassistant.playPage;

import static com.arialyy.aria.core.command.CommandManager.init;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cast.dlna.dmc.DLNACastManager;
import com.android.cast.dlna.dmc.OnDeviceRegistryListener;
import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.dialog.AlertDialog;
import com.jeremyliao.liveeventbus.LiveEventBus;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.DialogDlnaBinding;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class DlnaDialog {

    public Context mContext;
    public AlertDialog dialog;
    public DialogDlnaBinding binding;
    private DlnaAdapter dlnaAdapter;
    private static final String TAG = "DlnaDialog";
    List<Device> deviceList = new ArrayList<>();

    public DlnaDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_dlna, null, false);
        dialog = new AlertDialog.Builder(mContext)
                .setContentView(binding.getRoot())
                .setWidthAndHeight(UiUtil.weight(mContext) * 5 / 6,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
                .addDefaultAnimation()
                .setCancelable(true)
                .create();
        dialog.show();
        initRecyc();
        initDlna();
    }

    private void initRecyc() {
        binding.recyc.setLayoutManager(new LinearLayoutManager(mContext));
        dlnaAdapter = new DlnaAdapter();
        binding.recyc.setAdapter(dlnaAdapter);
        dlnaAdapter.setOnItemListener(new OnItemClickListener<Device>() {
            @Override
            public void onItemClick(Device device, int position) {
                LiveEventBus.get(Constant.dlnaPlay, Device.class)
                        .post(device);
                dialog.dismiss();
            }

            @Override
            public boolean onItemLongClick(Device device, int position) {
                return false;
            }
        });
    }

    private void initDlna() {
        DLNACastManager.getInstance().registerDeviceListener(new OnDeviceRegistryListener() {
            @Override
            public void onDeviceAdded(Device<?, ?, ?> device) {

                if (!deviceList.contains(device)) {
                    dlnaAdapter.addData(device);
                }

            }

            @Override
            public void onDeviceUpdated(Device<?, ?, ?> device) {

            }

            @Override
            public void onDeviceRemoved(Device<?, ?, ?> device) {

            }
        });
    }
}
