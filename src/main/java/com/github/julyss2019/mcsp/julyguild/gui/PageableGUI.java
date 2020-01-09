package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;

public abstract class PageableGUI implements GUI {
    private int currentPage;
    private int totalPage;
    protected final GUIType guiType;
    protected final GuildPlayer guildPlayer;

    public PageableGUI(GUIType guiType, GuildPlayer guildPlayer) {
        this.guiType = guiType;
        this.guildPlayer = guildPlayer;
    }

    // 下一页
    public void nextPage() {
        if (!hasNextPage()) {
            throw new RuntimeException("没有下一页了");
        }

        setCurrentPage(getCurrentPage() + 1);
        close();
        open();
    }

    // 上一页
    public void previousPage() {
        if (!hasPreciousPage()) {
            throw new RuntimeException("没有上一页了");
        }

        setCurrentPage(getCurrentPage() - 1);
        close();
        open();
    }

    // 是否有下一页
    public boolean hasNextPage() {
        return getCurrentPage() < getTotalPage() - 1;
    }

    // 是否有上一页
    public boolean hasPreciousPage() {
        return getCurrentPage() > 0;
    }

    // 打开指定页
    public void setCurrentPage(int page) {
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("页数不合法");
        }

        this.currentPage = page;
    }

    // 得到总页数
    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    // 得到当前页数
    public int getCurrentPage() {
        return currentPage;
    }

    // 是否是有效的页数
    public boolean isValidPage(int p) {
        return p >= 0 && p < getTotalPage();
    }

    public abstract void update();

    // 覆盖 GUI 的 reopen() 实现，尽量使页数靠近最近页数
    @Override
    public void reopen() {
        update();
        close();

        if (isValidPage(currentPage)) {
            setCurrentPage(currentPage);
        } else if (isValidPage(currentPage - 1) ){
            setCurrentPage(currentPage - 1);
        } else if (isValidPage(currentPage + 1)) {
            setCurrentPage(currentPage + 1);
        } else {
            setCurrentPage(0);
        }

        open();
    }

    @Override
    public GUIType getType() {
        return guiType;
    }

    @Override
    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }
}
