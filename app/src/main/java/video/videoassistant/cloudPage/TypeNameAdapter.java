package video.videoassistant.cloudPage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.AdapteTypeBinding;
import video.videoassistant.me.jointManage.JointEntity;

public class TypeNameAdapter extends BaseDBRVAdapter<JointEntity, AdapteTypeBinding> {

    private int selectPosition = 0;
    public SelectItem selectItem;

    public TypeNameAdapter() {
        super(R.layout.adapte_type, BR.bean);
    }
    public interface SelectItem{
        void select(JointEntity joint);
    }

    public void getSelectListener(SelectItem selectItem){
        this.selectItem = selectItem;
    }


    @Override
    protected void initData(AdapteTypeBinding binding, JointEntity jointEntity, int position) {
        binding.type.setText(jointEntity.name);
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
                setSelectPosition(position,jointEntity);
            }
        });
    }


    public void setSelectPosition(int selectPosition,JointEntity jointEntity) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
        if(selectItem!=null){
            selectItem.select(jointEntity);
        }
    }
}
