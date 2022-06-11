package codes.wasabi.r4j.oauth;

import codes.wasabi.r4j.exception.RedditOAuthDeniedException;
import codes.wasabi.r4j.exception.RedditOAuthException;
import codes.wasabi.r4j.exception.RedditOAuthUnacceptableException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * Manages the OAuth flow
 */
public class RedditOAuthServer {

    private final ServerSocket serverSocket;
    private final Thread handlerThread;

    /**
     * Initializes an HTTP OAuth server on the given port. This should NOT be done manually!
     * @param port The port
     * @throws IOException The server was unable to open the port
     */
    public RedditOAuthServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        handlerThread = new Thread(() -> {
            while (isRunning()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    continue;
                }
                serve(socket);
            }
        });
        handlerThread.setName("Reddit OAuth Server Thread");
        handlerThread.start();
    }

    /**
     * Checks if the server socket has not been closed
     * @return True if the OAuth server is running
     */
    public boolean isRunning() {
        return !serverSocket.isClosed();
    }

    /**
     * Manually closes the OAuth server
     * @return True if this was necessary and successful
     */
    public boolean close() {
        try {
            serverSocket.close();
            handlerThread.interrupt();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private final Map<String, List<Consumer<RedditOAuthResponse>>> callbackMap = new HashMap<>();
    /**
     * Executest the given callback when the OAuth session with the given state supplies a code to the server
     * @param stateString The state identifier
     * @param callback The callback to run
     */
    public void awaitResponse(String stateString, Consumer<RedditOAuthResponse> callback) {
        List<Consumer<RedditOAuthResponse>> list = Objects.requireNonNullElseGet(callbackMap.get(stateString), ArrayList::new);
        list.add(callback);
        callbackMap.put(stateString, list);
    }

    private boolean readPath(String path) {
        int idx = path.indexOf("?");
        if (idx < 0) return false;
        String query = path.substring(idx + 1);
        idx = query.indexOf("#");
        if (idx >= 0) {
            query = query.substring(0, idx);
        }
        String[] parts = query.split("&");
        String error = null;
        String code = null;
        String state = null;
        for (String part : parts) {
            int index = part.indexOf("=");
            String value;
            if (index < 0) {
                index = part.length();
                value = "";
            } else {
                value = URLDecoder.decode(part.substring(index + 1), StandardCharsets.UTF_8);
            }
            String key = URLDecoder.decode(part.substring(0, index), StandardCharsets.UTF_8);
            if (key.equalsIgnoreCase("error")) {
                error = value;
            } else if (key.equalsIgnoreCase("code")) {
                code = value;
            } else if (key.equalsIgnoreCase("state")) {
                state = value;
            }
        }
        if (state == null) return false;
        RedditOAuthResponse response;
        if (error != null) {
            RedditOAuthException t;
            if (error.equalsIgnoreCase("access_denied")) {
                t = new RedditOAuthDeniedException("OAuth responded with error code " + error);
            } else {
                t = new RedditOAuthUnacceptableException("OAuth responded with error code " + error);
            }
            response = new RedditOAuthResponse(null, t);
        } else {
            if (code == null) return false;
            response = new RedditOAuthResponse(code, null);
        }
        List<Consumer<RedditOAuthResponse>> callbacks = callbackMap.get(state);
        if (callbacks != null) {
            for (Consumer<RedditOAuthResponse> cb : callbacks) cb.accept(response);
        }
        return true;
    }

    private void serve(Socket socket) {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            dataOut = new BufferedOutputStream(socket.getOutputStream());

            String input = in.readLine();
            StringTokenizer tokenizer = new StringTokenizer(input);
            String method = tokenizer.nextToken().toUpperCase(Locale.ROOT);
            String path = tokenizer.nextToken();

            if (!method.equals("GET")) {
                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: Reddit4J OAuth Server");
                out.println("Date: " + new Date());
                out.println("Content-Type: text/plain");
                out.println("Content-length: 0");
                out.println();
                out.flush();
                dataOut.flush();
            } else {
                byte[] bytes;
                if (readPath(path)) {
                    bytes = "<center><h1>You may now close this window.</h1></center>".getBytes(StandardCharsets.UTF_8);
                } else {
                    bytes = "<center><h1>Invalid response.</h1></center>".getBytes(StandardCharsets.UTF_8);
                }
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Reddit4J OAuth Server");
                out.println("Date: " + new Date());
                out.println("Content-Type: text/html; charset=utf-8");
                out.println("Content-Length: " + bytes.length);
                out.println();
                out.flush();
                dataOut.write(bytes, 0, bytes.length);
                dataOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (dataOut != null) dataOut.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
