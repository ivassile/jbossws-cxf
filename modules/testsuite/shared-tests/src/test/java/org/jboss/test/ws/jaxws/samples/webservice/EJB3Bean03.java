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
package org.jboss.test.ws.jaxws.samples.webservice;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.jws.WebService;

import org.jboss.ws.api.annotation.WebContext;

/**
 * Test the JSR-181 annotation: jakarta.jws.WebService
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Apr-2005
 */
@WebService(endpointInterface = "org.jboss.test.ws.jaxws.samples.webservice.EndpointInterface03", targetNamespace = "http://www.openuri.org/2004/04/HelloWorld", serviceName = "EndpointService")
@WebContext(contextRoot="/jaxws-samples-webservice03-ejb3", urlPattern="/*")
@Remote(EJB3RemoteInterface.class)
@Stateless
public class EJB3Bean03 implements EJB3RemoteInterface
{
   public String echo(String input)
   {
      return input;
   }
}
