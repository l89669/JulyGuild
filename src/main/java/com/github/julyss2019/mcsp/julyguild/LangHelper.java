package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

public class LangHelper {
    public static class Global {
        public static String getPrefix() {
            return JulyGuild.getInstance().getLangYamlConfig().getString("Global.prefix");
        }
    }
}
