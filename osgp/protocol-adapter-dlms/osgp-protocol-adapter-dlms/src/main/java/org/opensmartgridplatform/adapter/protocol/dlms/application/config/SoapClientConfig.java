package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.support.KeyManagersFactoryBean;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

@Configuration
public class SoapClientConfig {

    private static final String XSD_SCHEMA_PACKAGE = "org.opensmartgridplatform.schemas.security.secretmanagement._2020._05";

    @Value("${soapclient.use.client.auth:false}")
    private String useClientAuth;

    @Value("${soapclient.default-uri}")
    private String defaultUri;

    @Value("${soapclient.ssl.trust-store}")
    private Resource trustStore;

    @Value("${soapclient.ssl.trust-store-password}")
    private String trustStorePassword;

    @Value("${soapclient.ssl.key-store}")
    private Resource keyStore;

    @Value("${soapclient.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${soapclient.ssl.key-password}")
    private String keyPassword;

    @Bean
    Jaxb2Marshaller soapClientJaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath(XSD_SCHEMA_PACKAGE);
        return jaxb2Marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() throws Exception {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(soapClientJaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(soapClientJaxb2Marshaller());
        webServiceTemplate.setDefaultUri(defaultUri);

        if (Boolean.parseBoolean(useClientAuth)) {
            webServiceTemplate.setMessageSender(httpsUrlConnectionMessageSender());
        }

        return webServiceTemplate;
    }

    @Bean
    public HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender() throws Exception {
        HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender =
                new HttpsUrlConnectionMessageSender();
        // set the trust store(s)
        httpsUrlConnectionMessageSender.setTrustManagers(trustManagersFactoryBean().getObject());
        // set the key store(s)
        httpsUrlConnectionMessageSender.setKeyManagers(keyManagersFactoryBean().getObject());

        return httpsUrlConnectionMessageSender;
    }

    @Bean
    public KeyStoreFactoryBean trustStore() {
        KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
        keyStoreFactoryBean.setLocation(trustStore);
        keyStoreFactoryBean.setPassword(trustStorePassword);

        return keyStoreFactoryBean;
    }

    @Bean
    public TrustManagersFactoryBean trustManagersFactoryBean() {
        TrustManagersFactoryBean trustManagersFactoryBean = new TrustManagersFactoryBean();
        trustManagersFactoryBean.setKeyStore(trustStore().getObject());

        return trustManagersFactoryBean;
    }

    @Bean
    public KeyStoreFactoryBean keyStore() {
        KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
        keyStoreFactoryBean.setLocation(keyStore);
        keyStoreFactoryBean.setPassword(keyStorePassword);

        return keyStoreFactoryBean;
    }

    @Bean
    public KeyManagersFactoryBean keyManagersFactoryBean() {
        KeyManagersFactoryBean keyManagersFactoryBean = new KeyManagersFactoryBean();
        keyManagersFactoryBean.setKeyStore(keyStore().getObject());
        // set the password of the key pair to be used
        keyManagersFactoryBean.setPassword(keyPassword);

        return keyManagersFactoryBean;
    }
}
