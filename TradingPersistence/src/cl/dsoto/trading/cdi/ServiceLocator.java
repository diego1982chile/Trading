package cl.dsoto.trading.cdi; /**
 * Created by root on 15-05-17.
 */

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceLocator {

    private static final ServiceLocator instance = new ServiceLocator();

    private static WeldContainer container;

    /** Mapa de interfaces por su nombre. */
    private Map<String, Object> servicesByName;

    private synchronized void lookupRemoteStatelessEJB(Class type) throws Exception {

        // start the container, retrieve a bean and do work with it
        this.servicesByName.put(type.getName(), container.select(type).get());
        //container.close();
        // shuts down automatically after the try with resources block.
    }

    /**
     * Constructor privado para el Singleton del Factory.
     * EJBClientContext using EJBClientAPI
     */
    private ServiceLocator() {

        try {
            Weld weld = new Weld();
            container = weld.initialize();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // shuts down automatically after the try with resources block.
        this.servicesByName = new ConcurrentHashMap<>();
    }


    public static ServiceLocator getInstance() {
        return instance;
    }

    /**
     * Este método es responsable de retornar el manager correspondiente.
     *
     * @return Retorna una instancia de FSN.
     */
    public Object getService(Class type) {

        if (!servicesByName.containsKey(type.getName())) {
            try {
                lookupRemoteStatelessEJB(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.servicesByName.get(type.getName());
    }

    public void closeContext() {
        container.close();
    }


}