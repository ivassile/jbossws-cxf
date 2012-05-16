/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.cxf.jbws3497;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.workqueue.AutomaticWorkQueue;
import org.apache.cxf.workqueue.AutomaticWorkQueueImpl;
import org.jboss.logging.Logger;

@WebService(name = "EndpointOne", targetNamespace = "http://org.jboss.ws.jaxws.cxf/jbws3497", serviceName = "ServiceOne")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class EndpointOneImpl
{
   private static AtomicInteger count = new AtomicInteger(0);
   
   @WebMethod
   public String echo(String input)
   {
      Bus bus = BusFactory.getThreadDefaultBus(false);
      ConfiguredBeanLocator locator = bus.getExtension(ConfiguredBeanLocator.class);
      AutomaticWorkQueueImpl queue = (AutomaticWorkQueueImpl)locator.getBeanOfType("cxf.default.workqueue", AutomaticWorkQueue.class);
      if (queue.getMaxSize() != 10) {
         throw new RuntimeException("Expected max queue size '10' but got '" + queue.getMaxSize() + "'!");
      }
      Logger.getLogger(this.getClass()).info("echo: " + input);
      count.incrementAndGet();
      return input;
   }
   
   @WebMethod
   @Oneway
   public void echoOneWay(String input)
   {
      Logger.getLogger(this.getClass()).info("echoOneWay: " + input);
      count.incrementAndGet();
   }
   
   @WebMethod
   public int getCount()
   {
      return count.get();
   }
}
