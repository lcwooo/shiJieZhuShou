package video.videoassistant.me.urlManage;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface OnStartDragListener {
    /**
     * 当View需要拖拽时回调
     *
     * @param viewHolder The holder of view to drag
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);



    void dragEnd();

    void dragRemove(Object entity, View view);
}
