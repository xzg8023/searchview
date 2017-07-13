package xzg.serachview.wigit;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xzg.com.utils.HideInput;
import cn.xzg.com.utils.LogTools;
import cn.xzg.com.utils.StringUtil;
import xzg.serachview.R;

/**
 * 搜索控件
 *
 * @author xzg
 * @time 2017/7/6  11:01
 */
public class SearchView extends RelativeLayout {

    private final String tag = "searchview";
    private Context mContext;

    private EditText mEt_search;
    private RelativeLayout rl_search_cancle;
    private RelativeLayout rl_search_clear;
    /**
     * 这里可以搜索到提示信息
     */
    private RelativeLayout rl_can_searched_here;
    /**
     * 最近搜索记录
     */
    private RelativeLayout rl_search_history;

    /**
     * 群聊
     */
    private RelativeLayout rl_prompt_chat;

    /**
     * 联系人
     */
    private RelativeLayout rl_prompt_contact;

    /**
     * 企业通讯录
     */
    private RelativeLayout rl_prompt_company;
    /**
     * 输入框监听监听的间隔时间
     */
    private long LastTime = 0;

    /**
     * 输入框监听事件使用
     */
    private boolean isHandlerPost = false;


    /**
     * hint字符
     */
    private String hintString = "";

    private boolean isShowHistory = false;
    private boolean isShowPrompt = false;

    private boolean isShowContactPrompt = false;
    private boolean isShowCompanyPrompt = false;
    private boolean isShowChatPrompt = false;
    private int autoDelayTime = 1000;

    private TagFlowLayout mSearch_history;
    private ImageView mHistoryClear;
    private SearchAdapter mSearchAdapter;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs, defStyleAttr);
        initView();
        initListener();
    }


    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SearchView_hintText:
                    hintString = a.getString(attr);
                    break;
                case R.styleable.SearchView_showHistory:
                    isShowHistory = a.getBoolean(attr, false);
                    break;

                case R.styleable.SearchView_showPromptInfo:
                    isShowPrompt = a.getBoolean(attr, false);
                    break;

                case R.styleable.SearchView_showContactPrompt:
                    isShowContactPrompt = a.getBoolean(attr, false);
                    break;

                case R.styleable.SearchView_showCompanyPrompt:
                    isShowCompanyPrompt = a.getBoolean(attr, false);
                    break;

                case R.styleable.SearchView_showChatGroupPrompt:
                    isShowChatPrompt = a.getBoolean(attr, false);
                case R.styleable.SearchView_delayMilliSecond:
                    autoDelayTime = a.getInt(attr, autoDelayTime);
                    break;

            }
        }
        a.recycle();
    }


    private void initView() {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.layout_searchview, null);
        mEt_search = (EditText) view.findViewById(R.id.et_search);
        rl_search_cancle = (RelativeLayout) view.findViewById(R.id.rl_search_cancle);
        rl_search_clear = (RelativeLayout) view.findViewById(R.id.rl_search_clear);
        rl_can_searched_here = (RelativeLayout) view.findViewById(R.id.rl_can_searched_here);
        rl_search_history = (RelativeLayout) view.findViewById(R.id.rl_search_history);
        rl_prompt_chat = (RelativeLayout) view.findViewById(R.id.rl_prompt_chat);
        rl_prompt_company = (RelativeLayout) view.findViewById(R.id.rl_prompt_company);
        rl_prompt_contact = (RelativeLayout) view.findViewById(R.id.rl_prompt_contact);
        mHistoryClear = (ImageView) view.findViewById(R.id.iv_history_clear);
        mSearch_history = (TagFlowLayout) view.findViewById(R.id.flowlayout_search_history);
        mSearchAdapter = new SearchAdapter(mContext, mSearch_history);
        mSearch_history.setAdapter(mSearchAdapter);

        mEt_search.setHint(hintString);
        //显示键盘
        HideInput.showSoftInputMethod(mEt_search, mContext);
        rl_can_searched_here.setVisibility(isShowPrompt ? VISIBLE : GONE);
        if (isShowPrompt) {
            rl_prompt_chat.setVisibility(isShowChatPrompt ? VISIBLE : GONE);
            rl_prompt_company.setVisibility(isShowCompanyPrompt ? VISIBLE : GONE);
            rl_prompt_contact.setVisibility(isShowContactPrompt ? VISIBLE : GONE);

        }
        this.addView(view);
    }


    private void initListener() {
        rl_search_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancle();
            }
        });
        rl_search_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearKey();
            }
        });
        mEt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                    HideInput.hideSoftInputMethod(mEt_search, mContext);
                    searchContacts(mEt_search.getText());
                    if (mOnSearchListener != null) {
                        String key = mEt_search.getText().toString();
                        if (!TextUtils.isEmpty(key)) {
                            mOnSearchListener.addSearchKey(key);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        mEt_search.addTextChangedListener(new TextWatcher() {
            CharSequence afterS = "";
            CharSequence beforS = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                afterS = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforS = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchContacts(beforS);
            }
        });

        mSearch_history.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String item = mSearchAdapter.getItem(position);
                if (!StringUtil.isEmpty(item)) {
                    mEt_search.setText(item);
                    mEt_search.setSelection(item.length());
                }
                return true;
            }
        });
        mHistoryClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSearchListener != null) {
                    boolean clearHistory = mOnSearchListener.clearHistory();
                    if (clearHistory) {
                        showHistory(false);
                    }

                }
            }
        });

    }


    /**
     * 关键字搜索
     *
     * @param afterS
     */
    private void searchContacts(CharSequence afterS) {
        final String key = mEt_search.getText().toString().trim();
        rl_search_clear.setVisibility(TextUtils.isEmpty(key) ? View.GONE : View.VISIBLE);
        LastTime = System.currentTimeMillis();
        afterS = afterS.toString().trim();
        if (afterS != null && afterS.length() != 0) {
            if (isHandlerPost) {
                isHandlerPost = false;
            }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (System.currentTimeMillis() - LastTime >= autoDelayTime && !isHandlerPost) {
                        LogTools.i(tag, "延迟毫秒::" + autoDelayTime);

                        searchData(key);
                        isHandlerPost = true;
                    }
                }
            },  autoDelayTime);
        } else {
            if (mOnSearchListener != null) {
                mOnSearchListener.updateHistory();
                mOnSearchListener.searchKeyEmpty();
            }
            showSearchInfo(true);
        }
    }

    /**
     * 关键字搜索
     */
    private void searchData(String key) {
        if (!StringUtil.isEmpty(key)) {
            showSearchInfo(false);
            if (mOnSearchListener != null) {
                mOnSearchListener.searchKey(key);
            }
        } else {
            if (mOnSearchListener != null) {
                mOnSearchListener.updateHistory();
                mOnSearchListener.searchKeyEmpty();
            }
            showSearchInfo(true);
        }
    }

    /**
     * 清空搜索输入框
     */
    private void clearKey() {
        mEt_search.setText("");
    }

    private void cancle() {
        HideInput.hideSoftInputMethod(mEt_search, mContext);
        if (mOnSearchListener != null) {
            mOnSearchListener.cancle();
        }
    }

    /**
     * 是否显示搜索记录以及提示信息
     *
     * @param isShow
     */
    public void showSearchInfo(boolean isShow) {
        showHistory(isShow);
        showPrompt(isShow);
    }


    /**
     * 是否显示搜索记录
     *
     * @param isShow
     */
    public void showHistory(boolean isShow) {
        if (isShowHistory && mSearchAdapter != null) {
            rl_search_history.setVisibility(isShow && !mSearchAdapter.isEmpty() ? VISIBLE : GONE);
        }
    }

    /**
     * 是否显示搜索提示
     *
     * @param isShow
     */
    public void showPrompt(boolean isShow) {
        if (isShowPrompt) {
            rl_can_searched_here.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    private OnSearchListener mOnSearchListener;

    /**
     * 设置搜索的监听事件
     *
     * @param mOnSerchListener
     */
    public void setOnSearchListener(OnSearchListener mOnSerchListener) {
        this.mOnSearchListener = mOnSerchListener;
    }

    /**
     * 设置搜索历史记录
     *
     * @param list
     */
    public void setHistoryData(List<String> list) {
        //如果搜索记录为空，则不显示搜索历史相关记录
        if (isShowHistory) {
            if (mSearchAdapter != null && list != null && !list.isEmpty()) {
                mSearchAdapter.setDatas(list);
                showHistory(true);
            } else {
                showHistory(false);
            }
        }
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        if (mEt_search != null) {
            HideInput.hideSoftInputMethod(mEt_search, mContext); //显示软键盘
        }
    }

}
