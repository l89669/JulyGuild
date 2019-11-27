package com.github.julyss2019.mcsp.julyguild.gui;

public interface ReturnableGUI extends GUI {
    GUI getLastGUI();

    default void returnToLast() {
        close();
        getLastGUI().reopen();
    }
}
