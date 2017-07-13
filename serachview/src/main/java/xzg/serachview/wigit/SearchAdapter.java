package xzg.serachview.wigit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import xzg.serachview.R;

/**
 * @author xzg
 * @time 2017/7/6  20:22
 */
public class SearchAdapter extends TagAdapter<String> {
    Context mContext;
    FlowLayout mFlowLayout;


    public SearchAdapter(Context context, FlowLayout flowLayout) {
        mContext = context;
        mFlowLayout = flowLayout;
    }

    @Override
    public View getView(FlowLayout parent, int position, String s) {
        TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_search_history, mFlowLayout, false);
        tv.setText(s);
        return tv;
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
    }

    public String getItem(int position) {

        if (mDatas != null && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return "";
    }
}

