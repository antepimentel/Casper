package IPC.Implementations;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Exceptions.GroupNotFoundException;
import IPC.AbstractIPCImpl;
import LFG.LFGHandler;


public interface GroupsIPC extends Remote {
    public void refreshGroup(String serverId, int id) throws RemoteException;

    class GroupsIPCImpl extends AbstractIPCImpl implements GroupsIPC
    {
        @Override
        public void refreshGroup(String serverId, int id) throws RemoteException {
            try {
                LFGHandler.refreshGroup(serverId, LFGHandler.findGroupByID(serverId, id));
            } catch (GroupNotFoundException ex) {
                throw new RemoteException(ex.getMessage());
            }
        }

        @Override
        public void load() throws RemoteException, AlreadyBoundException {
            GroupsIPCImpl self = new GroupsIPCImpl();
            GroupsIPC stub = (GroupsIPC) UnicastRemoteObject.exportObject(self, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("rmi:groupsipc", stub);
        }
    }
}


