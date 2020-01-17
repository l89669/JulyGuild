package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public abstract class PageableGUI extends BasePlayerGUI {
    private int currentPage = -1;
    private int totalPage;

    public PageableGUI(@Nullable GUI lastGUI, GUIType guiType, GuildPlayer guildPlayer) {
        super(lastGUI, guiType, guildPlayer);
    }

    @Override
    public GUI getLastGUI() {
        return lastGUI;
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
        if (totalPage == 0) {
            return p == -1;
        }

        return p >= 0 && p < totalPage;
    }

    /**
     * 数据和界面分离，通常是个List，在这 update() 并设置 totalPage，而 createInventory() 仅仅只是根据当前数据（页数）返回GUI
     */
    public abstract void update();

    @Override
    public void open() {
        update();

        if (totalPage != 0) {
            if (currentPage == -1) {
                setCurrentPage(0);
            } else if (isValidPage(currentPage - 1) ){
                setCurrentPage(currentPage - 1);
            } else if (isValidPage(currentPage + 1)) {
                setCurrentPage(currentPage + 1);
            } else {
                setCurrentPage(0);
            }

        } else {
            setCurrentPage(-1);
        }

        super.open();
    }
}
