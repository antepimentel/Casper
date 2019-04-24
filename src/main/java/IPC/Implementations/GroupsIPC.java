package IPC.Implementations;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;

import Exceptions.GroupNotFoundException;
import IPC.AbstractIPCImpl;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import IPC.Sendables.SendableGroup;
import com.google.gson.Gson;
import net.dv8tion.jda.core.entities.Member;


public interface GroupsIPC extends Remote {
    public void refreshGroup(String serverId, int id) throws RemoteException;
    public String getGroups(String serverId) throws RemoteException;

    class GroupsIPCImpl extends AbstractIPCImpl implements GroupsIPC
    {
        @Override
        public void refreshGroup(String serverId, int id) throws RemoteException {
            try {
                try {
                    LFGHandler.refreshGroup(serverId, LFGHandler.findGroupByID(serverId, id));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (GroupNotFoundException ex) {
                throw new RemoteException(ex.getMessage());
            }
        }

        @Override
        public String getGroups(String serverId) {
            ArrayList<Group> groups = null;
            try {
                groups = GroupSQL.getGroupsByServer(serverId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ArrayList<SendableGroup> groupsToSend = new ArrayList<>();

            for(Group group : groups) {
                ArrayList<String> players = new ArrayList<>();
                ArrayList<String> subs = new ArrayList<>();

                for(Member m : group.getPlayers()) {
                    players.add(m.getUser().getId());
                }

                for(Member m : group.getSubs()) {
                    subs.add(m.getUser().getId());
                }

                SendableGroup sGroup = new SendableGroup(serverId, group.getID(), group.getName(), group.getDateCreated(),
                                                        group.getDate(), group.getTime(), group.getTimezone(), group.getPlatform(),
                                                        group.getMsgID(), group.getOwnerID(), group.getGroupActivityType(), group.getRollcallCount(),
                                                        players, subs);

                groupsToSend.add(sGroup);
            }

            Gson g = new Gson();
            return g.toJson(groupsToSend);
        }

        public void load() throws RemoteException, AlreadyBoundException {
            GroupsIPCImpl self = new GroupsIPCImpl();
            GroupsIPC stub = (GroupsIPC) UnicastRemoteObject.exportObject(self, 0);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("rmi:groupsipc", stub);
        }
    }
}


