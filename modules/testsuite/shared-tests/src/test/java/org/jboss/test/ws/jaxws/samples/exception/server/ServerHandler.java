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
package org.jboss.test.ws.jaxws.samples.exception.server;

import java.util.Iterator;

import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.ws.api.handler.GenericSOAPHandler;

/**
 * A simple server side handler applying uppercase function to the error message strings.
 *
 * @author alessio.soldano@jboss.org
 * @since 12-Feb-2008
 */
public class ServerHandler extends GenericSOAPHandler<SOAPMessageContext>
{
   @Override
   public boolean handleFault(SOAPMessageContext msgContext)
   {
      try
      {
         SOAPMessage soapMessage = msgContext.getMessage();
         SOAPBody soapBody = soapMessage.getSOAPBody();
         SOAPBodyElement soapBodyElement = (SOAPBodyElement)soapBody.getChildElements().next();
         SOAPElement faultStringElement = (SOAPElement)soapBodyElement.getChildElements(new QName("faultstring")).next();
         faultStringElement.setValue(faultStringElement.getValue().toUpperCase());
         Iterator<?> itDetail = soapBodyElement.getChildElements(new QName("detail"));
         if (itDetail.hasNext())
         {
            Iterator<?> itException = ((SOAPElement)itDetail.next()).getChildElements(new QName("http://server.exception.samples.jaxws.ws.test.jboss.org/","UserException"));
            if (itException.hasNext())
            {
               SOAPElement messageElement = (SOAPElement)(((SOAPElement)itException.next()).getChildElements(new QName("message"))).next();
               messageElement.setValue(messageElement.getValue().toUpperCase());
            }
         }
      }
      catch (Exception e)
      {
         throw  new WebServiceException(e);
      }
      return true;
   }
}
