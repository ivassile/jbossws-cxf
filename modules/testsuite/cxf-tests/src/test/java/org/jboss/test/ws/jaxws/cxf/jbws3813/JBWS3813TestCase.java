/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.test.ws.jaxws.cxf.jbws3813;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * [JBWS-3813] Add exception name to faultstring/detail/stackTrace
 * 
 * @author rsearls@redhat.com
 */
@RunWith(Arquillian.class)
public class JBWS3813TestCase extends JBossWSTest
{
   @ArquillianResource
   private URL baseURL;
   
   @Deployment(testable = false)
   public static WebArchive createDeployment() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, "jaxws-cxf-jbws3813.war");
      archive.addManifest()
         .addClass(org.jboss.test.ws.jaxws.cxf.jbws3813.EndpointOne.class)
         .addClass(org.jboss.test.ws.jaxws.cxf.jbws3813.EndpointOneImpl.class)
         .add(new FileAsset(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jbws3813/WEB-INF/jaxws-endpoint-config.xml")), "jaxws-endpoint-config.xml")
         .setWebXML(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jbws3813/WEB-INF/web.xml"));
      return archive;
   }

   @Test
   @RunAsClient
   public void testExceptionFlags() throws Exception {
      QName serviceName = new QName("http://org.jboss.ws.jaxws.cxf/jbws3813", "ServiceOne");
      Service service = Service.create(new URL(baseURL + "/ServiceOne?wsdl"), serviceName);
      EndpointOne proxy = service.getPort(EndpointOne.class);
      try
      {
         proxy.echo("foo");
         fail("test did not fail as required");
      } catch (jakarta.xml.ws.soap.SOAPFaultException ex) {
         String text = ex.getFault().getDetail().getFirstChild().getFirstChild().getTextContent();
         assertTrue("stack data not found", text.contains(EndpointOneImpl.class.getName()));
         assertTrue("Root exception name not found", text.startsWith("Caused by: java.lang.RuntimeException: my error"));
      }
   }
}
