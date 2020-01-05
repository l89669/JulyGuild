package com.github.julyss2019.mcsp.julyguild.request;

import java.io.Serializable;
import java.util.UUID;

public interface Request extends Serializable {
    Sender getSender();
    Receiver getReceiver();
    long getCreationTime();
    UUID getUuid();
    void destroy(); // 销毁请求，从 Sender 和 Receiver 中删除记录
    RequestType getType();
}
