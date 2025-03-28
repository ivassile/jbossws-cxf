/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.samples.securityDomain;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;
import org.jboss.ws.api.annotation.AuthMethod;
import org.jboss.ws.api.annotation.TransportGuarantee;
import org.jboss.ws.api.annotation.WebContext;

@Stateless(name = "PermitAllSecureEndpoint2")
@SOAPBinding(style = Style.RPC)
@WebService
(
   name = "PermitAllSecureEndpoint2",
   serviceName = "PermitAllSecureEndpoint2Service",
   targetNamespace = "http://org.jboss.ws/securityDomain"
)
@WebContext
(
   contextRoot="/jaxws-securityDomain-permitall", 
   urlPattern="/two",
   authMethod = AuthMethod.BASIC,
   transportGuarantee = TransportGuarantee.NONE,
   secureWSDLAccess = false
)
@PermitAll
@SecurityDomain("JBossWSSecurityDomainPermitAllTest")
public class PermitAllSecureEndpoint2Impl
{
   // Provide logging
   private static Logger log = Logger.getLogger(PermitAllSecureEndpoint2Impl.class);

   @WebMethod
   @RolesAllowed("friend")
   public String echo(String input)
   {
      log.info(input);
      return input;
   }

   @WebMethod
   public String echoForAll(String input)
   {
      log.info(input);
      return input;
   }
}
