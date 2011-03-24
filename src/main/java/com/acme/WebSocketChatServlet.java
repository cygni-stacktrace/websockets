package com.acme;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class WebSocketChatServlet extends WebSocketServlet {
    private final Set<ChatWebSocket> _members = new CopyOnWriteArraySet<ChatWebSocket>();

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getNamedDispatcher("default").forward(request,
                response);
    }

    protected WebSocket doWebSocketConnect(HttpServletRequest request,
            String protocol) {
        return new ChatWebSocket();
    }

    class ChatWebSocket implements WebSocket {
        Outbound _outbound;

        public void onConnect(Outbound outbound) {
            _outbound = outbound;
            _members.add(this);
        }

        public void onMessage(byte frame, byte[] data, int offset, int length) {
        }

        public void onMessage(byte frame, String data) {
            for (ChatWebSocket member : _members) {
                try {
                    member._outbound.sendMessage(frame, data);
                } catch (IOException e) {
                    Log.warn(e);
                }
            }
        }

        public void onDisconnect() {
            _members.remove(this);
        }

        public void onFragment(boolean more, byte opcode, byte[] data,
                int offset, int length) {
        }
    }
}