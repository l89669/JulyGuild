package com.github.julyss2019.mcsp.julyguild.request;

public class JoinRequest extends BaseRequest implements Request {
    public JoinRequest(RequestType requestType, Sender sender, Receiver receiver) {
        super(requestType, sender, receiver);
    }

    @Override
    public void destroy() {

    }
}
