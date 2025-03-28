/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.cxf.jbws3809;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.WebServiceProvider;

/**
 * User: rsearls
 * Date: 7/25/14
 */
@WebServiceProvider(
   portName = "EjbWebServiceProviderServicePort",
   targetNamespace = "http://org.jboss.ws.test",
   serviceName="MyEjbWebServiceProvider")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public class EjbWebServiceProvider implements BasicEjb {
   @WebMethod(operationName = "getStr")
   public String getStr (@WebParam(name = "str") String str) {
      return this.getClass().getSimpleName() + ": " + str;
   }
}
