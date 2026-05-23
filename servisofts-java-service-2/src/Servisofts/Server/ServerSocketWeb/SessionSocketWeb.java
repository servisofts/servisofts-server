package Servisofts.Server.ServerSocketWeb;

import java.io.IOException;

import org.json.JSONObject;

// import Server.MensajeSocket;
import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;
import Servisofts.SUtil;

public class SessionSocketWeb extends SSSessionAbstract {
    // private final BlockingQueue<String> messageQueue = new
    // LinkedBlockingQueue<>();
    // private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private jakarta.websocket.Session miSession;

    public SessionSocketWeb(Object session) {
        super(session, ((jakarta.websocket.Session) session).getId(),
                SSServerAbstract.getServer(SSServerAbstract.TIPO_SOCKET_WEB));
        this.miSession = (jakarta.websocket.Session) session;
        this.miSession.setMaxIdleTimeout(5 * 60 * 1000);
        // this.miSession.setIdleTimeout(1000 * 60 * 60);
        // executor.submit(this::processQueue);
        onOpen();

    }

    @Override
    public void onMessage(String mensaje) {
        onMenssage(new JSONObject(mensaje));
    }

    @Override
    public void onClose(JSONObject obj) {
        // executor.shutdownNow();
        // SConsole.warning("Close SessionSocketWeb");
        try {
            miSession.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onClose(obj);

    }

    @Override
    public void onError(JSONObject obj) {
        // TODO Auto-generated method stub
        System.out.println("Error en la session socket Web ");
        try {
            miSession.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onClose(obj);
    }

    @Override
    public void send(String mensaje) {
        String finalmsn = mensaje + "---SSkey---" + SUtil.uuid() + "---SSofts---";
        this.miSession.getAsyncRemote().sendText(finalmsn);
        // messageQueue.offer();
        // try {
        // // MensajeSocket mensajeSocket = new MensajeSocket(mensaje, this);
        // // Future<Void> fut;
        // // fut = this.miSession.getRemote()
        // // .sendStringByFuture(mensaje + "---SSkey---" + SUtil.uuid() +
        // "---SSofts---");
        // // fut.get(5, TimeUnit.SECONDS); // wait for send to complete.
        // this.miSession.getRemote().sendString(mensaje + "---SSkey---" + SUtil.uuid()
        // + "---SSofts---");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    // private void processQueue() {
    // while (true) {
    // try {
    // String mensaje = messageQueue.take();
    // sendMessageWithRetry(mensaje, 3, 1000);
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // break;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }

    // private void sendMessageWithRetry(String mensaje, int maxRetries, long
    // initialDelay) {
    // AtomicInteger attempt = new AtomicInteger(0);
    // sendMessage(mensaje, attempt, maxRetries, initialDelay);
    // }

    // private void sendMessage(String mensaje, AtomicInteger attempt, int
    // maxRetries, long delay) {
    // CompletableFuture.runAsync(() -> {
    // try {
    // if (this.miSession.isOpen()) {

    // // WebSockets.sendText(mensaje, this.miSession, null);
    // this.miSession.getBasicRemote().sendText(mensaje);
    // }
    // // else {
    // // miSession.close();
    // // super.onClose(null);
    // // }
    // } catch (Exception e) {
    // if (attempt.incrementAndGet() < maxRetries) {
    // try {
    // Thread.sleep(delay);
    // } catch (InterruptedException ie) {
    // Thread.currentThread().interrupt();
    // }
    // // Retry with exponential backoff
    // sendMessage(mensaje, attempt, maxRetries, delay * 2);
    // } else {
    // throw new CompletionException(e);
    // }
    // }
    // }).whenComplete((result, ex) -> {
    // if (ex != null) {
    // System.err.println("Failed to send message after retries: " +
    // ex.getMessage());
    // ex.printStackTrace();
    // }
    // });
    // }

    @Override
    public void printLog(String mensaje) {
        System.out.println(getIdSession() + ": " + mensaje);

    }

    // @Override
    // public void send(String mensaje, MensajeSocket mensajeSocket) {
    // // TODO Auto-generated method stub

    // }

    @Override
    public boolean isOpen() {
        if (miSession == null) {
            return false;
        }
        return miSession.isOpen();
    }

}
