/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.ws.jaxws.cxf.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.Configurer;
import org.jboss.wsf.stack.cxf.client.configuration.JBossWSBusFactory;
import org.jboss.wsf.test.ClientHelper;

/**
 * Helper for performing various checks on Spring effects over the JBossWS-CXF integration when available in user applications. 
 * 
 * @author alessio.soldano@jboss.com
 * @since 02-Apr-2012
 *
 */
public class Helper implements ClientHelper
{
   private String targetEndpoint;

   @Override
   public void setTargetEndpoint(String address)
   {
      targetEndpoint = address;
   }

   /**
    * Verify the web app classloader 'sees' Spring (i.e. Spring jars are in the web app)
    * 
    * @return
    */
   public boolean testSpringAvailability()
   {
      return isSpringAvailable(Thread.currentThread().getContextClassLoader());
   }
   
   private static boolean isSpringAvailable(ClassLoader... loaders)
   {
      if (loaders == null || loaders.length == 0)
      {
         loaders = new ClassLoader[]{Thread.currentThread().getContextClassLoader()};
      }
      for (ClassLoader cl : loaders)
      {
         if (cl == null) 
         {
            continue;
         }
         try
         {
            cl.loadClass("org.springframework.context.ApplicationContext");
            return true;
         }
         catch (Exception e) {} //ignore
      }
      return false;
   }

   /**
    * Verify the BusFactory.newInstance() still return the JBossWS-CXF version of BusFactory
    * (the web app has a dependency on jbossws-cxf-client) and that still create a plain bus
    * version, without being fooled by the Spring availability in the TCCL when Spring is not
    * installed in the AS.
    * 
    * @return
    */
   public boolean testJBossWSCXFBus()
   {
      BusFactory factory = BusFactory.newInstance();
      if (!(factory instanceof JBossWSBusFactory))
      {
         throw new RuntimeException("Expected JBossWSBusFactory");
      }
      Bus bus = null;
      try
      {
         //set Configurer.USER_CFG_FILE_PROPERTY_NAME so that if the SpringBusFactory is
         //internally erroneously used, that won't fallback delegating to the non Spring
         //one, which would shade the issue
         final String prop = System.getProperty(Configurer.USER_CFG_FILE_PROPERTY_NAME);
         try
         {
            System.setProperty(Configurer.USER_CFG_FILE_PROPERTY_NAME, "unexistentfile.xml");
            bus = factory.createBus();
            //the created bus should not be a SpringBus, as the classloader for CXF has no visibility over the deployment spring jars 
            return !isSpringBus(bus);
         }
         finally
         {
            if (prop == null)
            {
               System.clearProperty(Configurer.USER_CFG_FILE_PROPERTY_NAME);
            }
            else
            {
               System.setProperty(Configurer.USER_CFG_FILE_PROPERTY_NAME, prop);
            }
         }
      }
      finally
      {
         if (bus != null)
         {
            bus.shutdown(true);
         }
      }
   }

   /**
    * Verify a JAXWS client can be properly created and used to invoke a ws endpoint
    * 
    * @return
    * @throws Exception
    */
   public boolean testJAXWSClient() throws Exception
   {
      Bus bus = BusFactory.newInstance().createBus();
      try
      {
         BusFactory.setThreadDefaultBus(bus);

         URL wsdlURL = new URL(targetEndpoint + "?wsdl");
         QName serviceName = new QName("http://org.jboss.ws/spring", "EndpointService");

         Service service = Service.create(wsdlURL, serviceName);
         Endpoint port = (Endpoint) service.getPort(Endpoint.class);
         return "Hello".equals(port.echo("Hello"));
      }
      finally
      {
         bus.shutdown(true);
      }
   }
   
   private static boolean isSpringBus(Bus bus) {
      //avoid compile/runtime Spring dependency for the check only
      return "org.apache.cxf.bus.spring.SpringBus".equals(bus.getClass().getName());
   }

   public boolean testSpringFunctionalities() throws Exception
   {
      //use reflection to avoid compile Spring dependency (this test is to be run within the non-spring testsuite too,
      //the Spring classes are coming from the jars included in the app on server side)
      URL url = Thread.currentThread().getContextClassLoader().getResource("spring-dd.xml");
      Class<?> cpXmlAppCtxClass = Class.forName("org.springframework.context.support.ClassPathXmlApplicationContext");
      Constructor<?> cons = cpXmlAppCtxClass.getConstructor(String.class);
      Object applicationContext = cons.newInstance(url.toString());
      Class<?> appCtxClass = applicationContext.getClass();
      Method m = appCtxClass.getMethod("getBean", String.class, Class.class);
      Foo foo = (Foo)m.invoke(applicationContext, "foo", Foo.class);
      return "Bar".equals(foo.getMessage());
   }
}
