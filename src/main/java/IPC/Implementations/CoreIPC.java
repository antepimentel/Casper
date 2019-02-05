package IPC.Implementations;

import Core.Bot;
import Core.Linker;
import Exceptions.GroupNotFoundException;
import IPC.AbstractIPCImpl;
import JDBC.MainSQLHandler;
import LFG.LFGHandler;
import net.dv8tion.jda.core.JDA;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public interface CoreIPC extends Remote {
    public int ping() throws RemoteException;
    public String getLinker(String discordId) throws RemoteException;
    class CoreIPCImpl extends AbstractIPCImpl implements CoreIPC
    {
        @Override
        public int ping() throws RemoteException {
            System.out.println("Pong!");
            return 0;
        }

        @Override
        public String getLinker(String discordId) throws RemoteException {
            Linker linker = MainSQLHandler.queryLinker(discordId);
            if(linker != null) {
                return linker.getDestinyMembershipId() + "#" + linker.getPlatform();
            } else {
                return null;
            }
        }

        public void load() throws RemoteException, AlreadyBoundException {
            CoreIPCImpl self = new CoreIPCImpl();
            CoreIPC stub = (CoreIPC) UnicastRemoteObject.exportObject(self, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("rmi:coreipc", stub);
        }
    }
}