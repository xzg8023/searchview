package xzg.serachview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.xzg.com.utils.ToastUtil;
import xzg.serachview.wigit.OnSearchListener;
import xzg.serachview.wigit.SearchView;

public class MainActivity extends AppCompatActivity {

    private final String tag = "searchview";
    private Context mContext;
    private SearchView mSearch_view;

    private List<String> listHistory = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
    }

    private void initView() {
        mSearch_view = (SearchView) findViewById(R.id.search_view);
        mSearch_view.setOnSearchListener(new OnSearchListener() {
            @Override
            public void cancle() {
                ToastUtil.show(mContext,"cancle");
            }

            @Override
            public void searchKey(String key) {
                ToastUtil.show(mContext, key);
            }

            @Override
            public void searchKeyEmpty() {
                ToastUtil.show(mContext, "searchKeyEmpty");
            }

            @Override
            public boolean clearHistory() {
                ToastUtil.show(mContext, "clearHistory");
                listHistory.clear();
                return true;
            }

            @Override
            public void addSearchKey(String searchKey) {
                listHistory.add(searchKey);
            }

            @Override
            public void updateHistory() {
                mSearch_view.setHistoryData(listHistory);
            }
        });
    }
}
