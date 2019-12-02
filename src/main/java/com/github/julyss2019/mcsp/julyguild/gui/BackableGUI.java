package com.github.julyss2019.mcsp.julyguild.gui;

import java.util.Optional;

public interface BackableGUI extends GUI {
    default void back() {
        Optional.ofNullable(getLastGUI()).ifPresent(gui -> {
            close();
            gui.reopen();
        });
    }

    GUI getLastGUI();
}
