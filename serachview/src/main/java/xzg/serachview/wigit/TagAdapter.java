package xzg.serachview.wigit;

import android.view.View;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TagAdapter<T> {
    protected List<T> mDatas = new ArrayList<>();
    private OnDataChangedListener mOnDataChangedListener;
    private HashSet<Integer> mCheckedPosList = new HashSet<Integer>();

    private final int maxCount = 15;

    public TagAdapter(List<T> datas) {
        if (datas != null) {
            mDatas = datas;
        }
    }

    public TagAdapter() {

    }

    public void setDatas(List<T> datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataChanged();
        }
    }

    /**
     * 判断数据是否为空
     * @return true 为空，false不为空
     */
    public boolean isEmpty(){
       return mDatas.isEmpty();
    }

    public TagAdapter(T[] datas) {
        mDatas = new ArrayList<T>(Arrays.asList(datas));
    }

    interface OnDataChangedListener {
        void onChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    public void setSelectedList(int... poses) {
        Set<Integer> set = new HashSet<>();
        for (int pos : poses) {
            set.add(pos);
        }
        setSelectedList(set);
    }

    public void setSelectedList(Set<Integer> set) {
        mCheckedPosList.clear();
        if (set != null)
            mCheckedPosList.addAll(set);
        notifyDataChanged();
    }

    HashSet<Integer> getPreCheckedList() {
        return mCheckedPosList;
    }


    public int getCount() {
        return Math.min(maxCount, mDatas.size());
    }

    public void notifyDataChanged() {
        if (mOnDataChangedListener != null)
            mOnDataChangedListener.onChanged();
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

    public boolean setSelected(int position, T t) {
        return false;
    }


}
