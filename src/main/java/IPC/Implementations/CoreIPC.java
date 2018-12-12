package IPC.Implementations;

import Exceptions.GroupNotFoundException;
import IPC.AbstractIPCImpl;
import LFG.LFGHandler;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public interface CoreIPC extends Remote {
    public int ping() throws RemoteException;
    public String[] getStubs() throws RemoteException;

    class CoreIPCImpl extends AbstractIPCImpl implements CoreIPC
    {
        @Override
        public int ping() throws RemoteException {
            System.out.println("Pong!");
            return 0;
        }

        @Override
        public String[] getStubs() throws RemoteException {
            Registry reg = LocateRegistry.getRegistry();
            return reg.list();
        }

        @Override
        public void load() throws RemoteException, AlreadyBoundException {
            CoreIPCImpl self = new CoreIPCImpl();
            CoreIPC stub = (CoreIPC) UnicastRemoteObject.exportObject(self, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("rmi:coreipc", stub);
        }
    }
}