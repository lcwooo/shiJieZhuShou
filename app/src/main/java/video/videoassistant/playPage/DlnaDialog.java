package video.videoassistant.playPage;

import static com.arialyy.aria.core.command.CommandManager.init;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class DlnaDialog {

    public Context mContext;
    public AlertDialog dialog;
    public DialogDlnaBinding binding;
    private DlnaAdapter dlnaAdapter;
    private static final String TAG = "DlnaDialog";
    List<Device> deviceList = new ArrayList<>();
    List<JsonEntity> jsonEntityList;
    List<TextView> textViewList = new ArrayList<>();
    String jieUrl;
    JsonEntity jsonEntity;
    Device selectDevice;


    public DlnaDialog(Context mContext, List<JsonEntity> jsonEntityList, String jieUrl) {
        this.mContext = mContext;
        this.jsonEntityList = jsonEntityList;
        this.jieUrl = jieUrl;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_dlna, null, false);
        dialog = new AlertDialog.Builder(mContext)
                .setContentView(binding.getRoot())
                .setWidthAndHeight(UiUtil.weight(mContext) * 5 / 6,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
                .addDefaultAnimation()
                .setCancelable(false)
                .create();
        dialog.show();
        initState();
        setSelectItem(0);
        initRecyc();
        initDlna();

    }

    private void setSelectItem(int i) {
        jsonEntity = jsonEntityList.get(i);
        for (int j = 0; j < textViewList.size(); j++) {
            if (i == j) {
                textViewList.get(j).setBackground(mContext.getResources().getDrawable(R.drawable.border_red));
                textViewList.get(j).setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                textViewList.get(j).setBackground(mContext.getResources().getDrawable(R.drawable.border));
                textViewList.get(j).setTextColor(mContext.getResources().getColor(R.color.textColor));
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initState() {

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        binding.add.removeAllViews();
        textViewList.clear();
        if (!UiUtil.listIsEmpty(jsonEntityList) && !jieUrl.endsWith("m3u8") && !jieUrl.endsWith("mp4")) {
            binding.llJson.setVisibility(View.VISIBLE);
            for (int i = 0; i < jsonEntityList.size(); i++) {
                JsonEntity entity = jsonEntityList.get(i);
                View view = View.inflate(mContext, R.layout.json_line, null);
                TextView textView = view.findViewById(R.id.textview);
                textView.setText(entity.getName());
                int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setSelectItem(finalI);
                        if(selectDevice!=null){
                            dlnaSelect.selectDlna(selectDevice,entity);
                        }
                    }
                });
                textViewList.add(textView);
                binding.add.addView(view);
            }
        } else {
            binding.llJson.setVisibility(View.GONE);
        }
    }

    private void initRecyc() {
        binding.recyc.setLayoutManager(new LinearLayoutManager(mContext));
        dlnaAdapter = new DlnaAdapter();
        binding.recyc.setAdapter(dlnaAdapter);
        dlnaAdapter.setOnItemListener(new OnItemClickListener<Device>() {
            @Override
            public void onItemClick(Device device, int position) {
                if (dlnaSelect != null) {
                    selectDevice = device;
                    dlnaSelect.selectDlna(device, jsonEntity);
                }
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

    public void dismiss(){
        dialog.dismiss();
    }

    public DlnaSelect dlnaSelect;

    public interface DlnaSelect {
        void selectDlna(Device device, JsonEntity entity);
    }

    public void getDlnaSelectListener(DlnaSelect dlnaSelect) {
        this.dlnaSelect = dlnaSelect;
    }
}
