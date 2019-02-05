package IPC;

import Core.Bot;
import Core.PropertyKeys;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Set;

public class IPCHandler {
    final static String HANDLERS_PATH = "IPC";
    static Registry r; //store in static variable to prevent garbage collection
    public static void init(){


        //Load IPC Impls
        try {
            r = LocateRegistry.createRegistry(1099);
            Reflections reflections = new Reflections(HANDLERS_PATH);
            Set<Class<? extends AbstractIPCImpl>> types = reflections.getSubTypesOf(AbstractIPCImpl.class);
            for(Class<? extends AbstractIPCImpl> a : types) {
                System.out.println("Registering: "+a.getName());
                AbstractIPCImpl abstractIPCImpl = a.getConstructor().newInstance();
                abstractIPCImpl.load();
            }

            System.out.println("RMI Server is running! ");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | RemoteException | AlreadyBoundException ex) {
            System.out.println("Failed while registering: "+ex.getMessage());
            System.out.println("If you are not using CasperAPI, ignore this message!");
        }
    }
}
