public class App {
    public static void main(String[] args) throws Exception {
        Servisofts.Servisofts.Manejador = Manejador::onMessage;
        Servisofts.Servisofts.initialize();
    }
}
