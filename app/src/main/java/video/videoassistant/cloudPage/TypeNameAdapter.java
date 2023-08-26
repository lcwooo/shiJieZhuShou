package video.videoassistant.cloudPage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.AdapteTypeBinding;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.util.UiUtil;

public class TypeNameAdapter extends BaseDBRVAdapter<JointEntity, AdapteTypeBinding> {

    private static final String TAG = "TypeNameAdapter";
    private int selectPosition = 0;
    public SelectItem selectItem;
    public List<TextView> textViewList = new ArrayList<>();
    public List<TextView> typeList = new ArrayList<>();

    public TypeNameAdapter() {
        super(R.layout.adapte_type, BR.bean);
    }

    public interface SelectItem {
        void select(JointEntity joint);
    }

    public void getSelectListener(SelectItem selectItem) {
        this.selectItem = selectItem;
    }


    @Override
    protected void initData(AdapteTypeBinding binding, JointEntity jointEntity, int position) {
        binding.type.setText(jointEntity.name);
        textViewList.add(binding.sum);
        typeList.add(binding.type);
        if (selectPosition == position) {
            binding.type.setBackgroundResource(R.drawable.shape_class_a);
            binding.type.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            binding.type.setTextColor(context.getResources().getColor(R.color.blue));
        } else {
            binding.type.setBackgroundColor(Color.parseColor("#F4F4F4"));
            binding.type.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
            binding.type.setTextColor(context.getResources().getColor(R.color.textColor));
        }
        binding.type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectPosition(position, jointEntity);
            }
        });
    }


    public void setSelectPosition(int selectPosition, JointEntity jointEntity) {
        this.selectPosition = selectPosition;
        selectType(selectPosition);
        if (selectItem != null) {
            selectItem.select(jointEntity);
        }
    }

    public void setSum(int index, int sum) {

        Log.i(TAG, "setSum: " + textViewList.size() + "----请求的位置：" + index);

        try {
            if (sum > 0) {
                textViewList.get(index).setVisibility(View.VISIBLE);
                textViewList.get(index).setText(sum + "");
            } else {
                textViewList.get(index).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            UiUtil.showToastSafe("TypeNameAdapter(84)：" + e.getMessage());
        }
    }

    public void clearSum() {
        for (TextView textView : textViewList) {
            textView.setVisibility(View.GONE);
        }
    }

    public void selectType(int position) {
        for (int i = 0; i < typeList.size(); i++) {
            if (i == position) {
                typeList.get(i).setBackgroundResource(R.drawable.shape_class_a);
                typeList.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                typeList.get(i).setTextColor(context.getResources().getColor(R.color.blue));
            } else {
                typeList.get(i).setBackgroundColor(Color.parseColor("#F4F4F4"));
                typeList.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
                typeList.get(i).setTextColor(context.getResources().getColor(R.color.textColor));
            }
        }
    }
}
