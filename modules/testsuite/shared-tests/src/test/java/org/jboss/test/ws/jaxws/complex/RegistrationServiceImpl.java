/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.complex;

import java.util.ArrayList;
import java.util.List;

import jakarta.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.logging.Logger;

/**
 * A mock registration service that exercises the use of complex types, arrays, inheritence,
 * and exceptions. Note that this test does not yet test polymorphic behavior.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @author Thomas.Diesler@jboss.com
 */
@WebService(endpointInterface = "org.jboss.test.ws.jaxws.complex.Registration", targetNamespace = "http://complex.jaxws.ws.test.jboss.org")
public class RegistrationServiceImpl implements Registration
{
   // Provide logging
   private static Logger log = Logger.getLogger(RegistrationServiceImpl.class);

   public long register(Customer customer, Object when) throws AlreadyRegisteredFault_Exception, ValidationFault_Exception
   {
      Name name = customer.getName();
      if (name == null)
      {
         ValidationFault fault = new ValidationFault();
         fault.getFailedCustomers().add(customer.getId());
         throw new ValidationFault_Exception("No name!", fault);
      }

      if ("al".equalsIgnoreCase(name.getFirstName()) && "capone".equalsIgnoreCase(name.getLastName()))
      {
         AlreadyRegisteredFault fault = new AlreadyRegisteredFault();
         fault.setExistingId(456);
         throw new AlreadyRegisteredFault_Exception("Al Capone is already registered", fault);
      }

      for (Customer c : customer.getReferredCustomers())
      {
         log.info("Refered customer: " + c.getName());
      }

      log.info("registering customer: " + customer);
      return customer.getId();
   }
   
   public void echo(jakarta.xml.ws.Holder<org.jboss.test.ws.jaxws.complex.Customer> customer) {
      log.info("customer: " + customer);
   }

   public List<Long> bulkRegister(List<Customer> customers, Object when) throws AlreadyRegisteredFault_Exception, ValidationFault_Exception
   {
      List<Long> registered = new ArrayList<Long>(customers.size());
      List<Long> failed = new ArrayList<Long>(customers.size());

      for (Customer c : customers)
      {
         try
         {
            registered.add(register(c, when));
         }
         catch (ValidationFault_Exception e)
         {
            failed.add(e.getFaultInfo().getFailedCustomers().get(0));
         }
      }

      if (failed.size() > 0)
      {
         ValidationFault fault = new ValidationFault();
         fault.getFailedCustomers().addAll(failed);
         throw new ValidationFault_Exception("Validation errors on bulk registering customers", fault);
      }

      return registered;
   }

   public boolean registerForInvoice(InvoiceCustomer invoiceCustomer) throws AlreadyRegisteredFault_Exception, ValidationFault_Exception
   {
      log.info("registerForInvoice: " + invoiceCustomer.getCycleDay());
      return true;
   }

   public Statistics getStatistics(Customer customer)
   {
      Statistics stats = new Statistics();
      stats.setHits(10);
      stats.setActivationTime(getCalendar());
      return stats;
   }
   
   private XMLGregorianCalendar getCalendar() 
   {
      try
      {
         DatatypeFactory calFactory = DatatypeFactory.newInstance();
         XMLGregorianCalendar cal = calFactory.newXMLGregorianCalendar(2002, 4, 5, 0, 0, 0, 0, 0);
         return cal;
      }
      catch (DatatypeConfigurationException e)
      {
         throw new RuntimeException(e);
      }
   }
}
