import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WebSocketServerApp extends WebSocketServer {
    private static final int PORT = 8080;
    private final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public WebSocketServerApp() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("Новое подключение: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("Соединение закрыто: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Получено сообщение: " + message);

        // Отправляем сообщение всем клиентам, кроме отправителя
        for (WebSocket client : clients) {
            if (!client.equals(conn)) {
                client.send(message);
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Ошибка: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public static void main(String[] args) {
        WebSocketServerApp server = new WebSocketServerApp();
        server.start();
        System.out.println("WebSocket сервер запущен!");
    }
}
