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
package org.jboss.test.ws.jaxws.samples.soapbinding;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import org.jboss.logging.Logger;

/**
 * Test the JSR-181 annotation: jakarta.jws.SOAPBinding
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 16-Oct-2005
 */

@WebService(name = "DocWrapped", serviceName = "DocWrappedService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class DocWrappedServiceImpl implements DocWrapped
{
   // Provide logging
   private static Logger log = Logger.getLogger(DocWrappedServiceImpl.class);

   @WebMethod(operationName = "SubmitPO")
   @WebResult(name = "PurchaseOrderAck")
   public String submitPO(@WebParam(name = "PurchaseOrder") String product)
   {
      log.info("submitPO: " + product);
      return product;
   }

   @WebMethod(operationName = "SubmitNamespacedPO")
   @WebResult(name = "NamespacedPurchaseOrderAck", targetNamespace = "http://namespace/result")
   public String submitNamespacedPO(@WebParam(name = "NamespacedPurchaseOrder", targetNamespace = "http://namespace/purchase")
         String product, @WebParam(name = "NamespacedString", targetNamespace = "http://namespace/string") String string)
   {
      log.info("submitNamespacedPO: " + product + ", " + string);
      return product;
   }
}
