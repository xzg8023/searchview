package xzg.serachview.wigit;

/**
 * 搜索相关事件
 * @author xzg
 * @time 2017/7/6  20:05
 */
public interface OnSearchListener {

    /**
     * 取消
     */
    void cancle();

    /**
     * 搜索关键字
     *
     * @param key
     */
    void searchKey(String key);

    /**
     * 搜索关键字为空
     */
    void searchKeyEmpty();

    /**
     * 清除历史记录
     *
     * @return true隐藏搜索记录
     */
    boolean clearHistory();

    /**
     * 添加搜索关键字
     *
     * @param searchKey
     */
    void addSearchKey(String searchKey);

    /**
     * 当输入框为空时，如果需要刷新历史搜索记录，须调用 {@linkplain SearchView#setHistoryData} 方法
     */
    void updateHistory();
}
