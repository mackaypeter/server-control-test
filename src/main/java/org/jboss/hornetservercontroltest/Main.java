/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.hornetservercontroltest;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.InitialContext;
import org.hornetq.api.core.management.HornetQServerControl;
import org.hornetq.api.core.management.ObjectNameBuilder;

/**
 *
 * @author pmackay@redhat.com
 */
public class Main {
    
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:3000/jmxrmi";
    private static String jndi=null;
     protected static final Logger log = Logger.getLogger(Main.class
         .getName());
    
    public static void main(String[] args) {
        jndi = args[0];
        try {
            new Main().run();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     private void run() throws Exception {
         
         ObjectName on = ObjectNameBuilder.DEFAULT.getHornetQServerObjectName();
         JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(JMX_URL), new HashMap<String, String>());
         MBeanServerConnection mbsc = connector.getMBeanServerConnection();
         HornetQServerControl serverControl = MBeanServerInvocationHandler.newProxyInstance(mbsc,
                                                                                            on,
                                                                                            HornetQServerControl.class,
                                                                                            false);

         String emptyTransactionsJson = serverControl.listPreparedTransactionDetailsAsJSON();
         if (emptyTransactionsJson == null) {
             emptyTransactionsJson = "NULL";
         } else if (emptyTransactionsJson.equals(""))  {
             emptyTransactionsJson = "EMPTY STRING";
         }
         
         
         
         System.out.println("-----------------------------------");
         System.out.println("This string is returned: " + emptyTransactionsJson);
         System.out.println("-----------------------------------");
     }
     
     protected InitialContext getContext() throws Exception
   {
      Main.log.log(Level.INFO, "using {0} for jndi", jndi);
      Properties props = new Properties();
      props.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
      props.put("java.naming.provider.url", jndi);
      props.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
      return new InitialContext(props);
   }
}
