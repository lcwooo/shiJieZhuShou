package video.videoassistant.me.handleManage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.me.urlManage.IItemTouchHelperAdapter;
import video.videoassistant.me.urlManage.IItemTouchHelperViewHolder;
import video.videoassistant.me.urlManage.OnStartDragListener;


public class HandleAdapter extends
        RecyclerView.Adapter<HandleAdapter.ItemViewHolder> implements IItemTouchHelperAdapter {

    private List<HandleEntity> mList;
    private final OnStartDragListener mDragListener;
    private Context mContext;

    public HandleAdapter(List<HandleEntity> list, Context context, OnStartDragListener mDragListener) {
        mList = list;
        this.mDragListener = mDragListener;
        this.mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_url, parent,
                false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        HandleEntity entity = mList.get(position);
        holder.name.setText(entity.name);
        holder.url.setText(entity.url);
        if(TextUtils.isEmpty(entity.remark)){
            holder.remark.setVisibility(View.GONE);
        }else {
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(entity.remark);
        }

        holder.re.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDragListener.onStartDrag(holder);
                return false;
            }
        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDragListener!=null){
                    mDragListener.dragRemove(mList.get(position),holder.edit);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if(fromPosition!=toPosition){
            if(mDragListener!=null){
                mDragListener.dragEnd();
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        //mList.remove(position);
        //notifyItemRemoved(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements IItemTouchHelperViewHolder {
        private TextView name;
        private TextView url;
        private TextView remark;
        private RelativeLayout re;
        private ImageView edit;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);
            remark = itemView.findViewById(R.id.remark);
            re = itemView.findViewById(R.id.rl);
            edit = itemView.findViewById(R.id.edit);
        }


        @Override
        public void onItemSelected() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.setTranslationZ(10);
            }

        }

        @Override
        public void onItemClear() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.setTranslationZ(0);
            }
        }
    }




    public List<HandleEntity> getSortList() {
        return mList;
    }
}
