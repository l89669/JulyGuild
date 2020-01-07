package com.github.julyss2019.mcsp.julyguild.request.entities;

import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import org.bukkit.configuration.ConfigurationSection;

public class JoinRequest extends BaseRequest {
    public JoinRequest() {

    }

    public JoinRequest(Sender sender, Receiver receiver) {
        super(sender, receiver);
    }

    @Override
    public void onSave(ConfigurationSection section) {
        super.onSave(section);
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }
}
