
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
            e.printStackTrace();
        }
        System.out.println("server Started.");
    }
}
