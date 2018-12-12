package IPC;

import Core.Bot;
import Core.PropertyKeys;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public class IPCHandler {
    final static String HANDLERS_PATH = "IPC";
    public static void init() {
        //Start Registry
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Map<String, String> env = processBuilder.environment();
            env.put("CLASSPATH", Bot.props.getProperty(PropertyKeys.IPC_CLASS_PATH_KEY)); // TELL rmiregistry where to load classes from.

            processBuilder.command("rmiregistry");
            processBuilder.start();
        } catch (IOException ex) {
            System.out.println("Failed while starting rmiregistry: "+ex.getMessage());
        }

        //Load IPC Impls
        try {
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
