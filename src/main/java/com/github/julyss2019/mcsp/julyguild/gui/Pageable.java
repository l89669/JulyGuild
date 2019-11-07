package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.gui.GUI;

public interface Pageable extends GUI {
    // 下一页
    void nextPage();

    // 上一页
    void previousPage();

    // 是否有下一页
    default boolean hasNext() {
        return getCurrentPage() < getTotalPage() - 1;
    }

    // 是否有上一页
    default boolean hasPrecious() {
        return getCurrentPage() > 0;
    }

    // 打开指定页
    void setCurrentPage(int page);

    // 得到总页数
    int getTotalPage();

    // 得到当前页数
    int getCurrentPage();

    // 是否是有效的页数
    default boolean isValidPage(int p) {
        return p >= 0 && p < getTotalPage();
    }
}
