
import java.rmi.*;
import java.rmi.registry.*;
import polytweet.interfaces.PolyInterface;
import polytweet.stub.PolyStub;

/**
 *
 * @author Thomas
 */
public class PolyServer {

    public static void main(String[] args) {
        int port = 1099;
        String hostname = "localhost";

        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        if (args.length > 1) {
            hostname = args[1];
        }
        Registry reg = null;
        try {
            reg = LocateRegistry.createRegistry(port);
            PolyInterface stub = new PolyStub();
            reg.rebind("polytweet", stub);
        } catch (RemoteException e) {
            System.err.println("Impossible de démarrer le serveur");
            e.printStackTrace();
        }
        System.out.println("Twitter is in the cloud.");
    }
}
