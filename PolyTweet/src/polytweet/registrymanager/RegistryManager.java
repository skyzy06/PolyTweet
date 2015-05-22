package polytweet.registrymanager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Thomas
 */
public class RegistryManager {
        public static final int PORT = 10000;
        private Registry registry;
        
        public RegistryManager() throws RemoteException {
            registry = LocateRegistry.getRegistry(PORT);
        }
        
        public PublicStub getPublicStub(String id) throws RMIException,UserNotFoundException {
        try {
            return (PublicStub) registry.lookup(id);
        } catch (NotBoundException ex) {
            throw new UserNotFoundException(id);
        } catch (RemoteException ex) {
            throw new RMIException();
        }
    }
    
    public class UserNotFoundException extends Exception{ 
        public String id;
        public UserNotFoundException(String id) { this.id = id; }
    };
    
    public class RMIException extends Exception{ };
}
