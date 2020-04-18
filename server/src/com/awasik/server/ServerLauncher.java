package com.awasik.server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;

//Note that server web socket implementation is not provided by gdx-websocket. This class uses an external library: Vert.x.
public class ServerLauncher {
    public static void main(final String... args) throws Exception {
        new HeadlessApplication(new ServerListener());
    }
}
