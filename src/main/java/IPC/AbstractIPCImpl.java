package IPC;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

//For Reflections
public abstract class AbstractIPCImpl {
    public abstract void load() throws RemoteException, AlreadyBoundException;
}
