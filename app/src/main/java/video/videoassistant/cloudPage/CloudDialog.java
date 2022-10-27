package video.videoassistant.cloudPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.azhon.basic.dialog.AlertDialog;
import com.azhon.basic.utils.TimeUtil;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.DialogAddUrlTypeBinding;
import video.videoassistant.databinding.DialogJsonTypeBinding;
import video.videoassistant.util.UiUtil;

public class CloudDialog {

    DialogJsonTypeBinding binding;
    public Context mContext;
    public AlertDialog dialog;
    List<TypeBean> typeBeanList;
    private TypeAdapter adapter;
    private TypeListener typeListener;
    private YearAdapter yearAdapter;


    public CloudDialog(Context mContext, List<TypeBean> typeBeanList) {
        this.mContext = mContext;
        this.typeBeanList = typeBeanList;
    }

    public void getTypeListener(TypeListener typeListener){
        this.typeListener = typeListener;
    }

    public void show() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_json_type, null, false);
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

        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        adapter = new TypeAdapter(typeBeanList,mContext);
        binding.flex.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String typeId,String name) {
                if(typeListener!=null){
                    typeListener.type(typeId,name);
                }
            }
        });
        
        yearAdapter = new YearAdapter(TimeUtil.getYears(),mContext);
        binding.year.setAdapter(yearAdapter);
        yearAdapter.notifyDataSetChanged();
        yearAdapter.setOnItemClickListener(new YearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String typeId, String typeName) {
                if(typeListener!=null){
                    typeListener.year(typeId);
                }
            }
        });
    }
}
