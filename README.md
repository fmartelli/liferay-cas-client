## Liferay + OpenDJ in Docker
- `docker run -h ldap-01.domain.com --network host -p 1389:1389 -p 1636:1636 -p 4444:4444 --name ldap-01 openidentityplatform/opendj`
- `docker run -it -p 8080:8080 -p 8000:8000 -v $(pwd):/opt/liferay/ --network host --name liferay liferay/portal:6.2.5-ga6`
- Per modificare la timezone, aggiungere `CATALINA_OPTS="${CATALINA_OPTS} -Duser.timezone=Europe/Rome"` in fondo a `liferay-folder/tomcat-7.0.62/bin/setenv.sh`
- Per abilitare il debug remoto, aggiungere `CATALINA_OPTS="${CATALINA_OPTS} ${LIFERAY_JVM_OPTS} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"` nello stesso file
- Creare delle utenze di prova su Liferay
- Creare delle utenze di prova su OpenDJ con i seguenti attributi: uid,sn,cn,givenName,mail,userPassword

## Configurare CAS 5.2
- Scaricare e scompattare https://github.com/apereo/cas-overlay-template/tree/5.2
- Creare un keystore sotto `etc/cas` nominato `thekeystore` e con password `changeit`
- Aggiungere a `src/main/resources/application.properties` 

```
cas.serviceRegistry.initFromJson=true
cas.serviceRegistry.json.location=classpath:/services

cas.authn.ldap[0].type=AUTHENTICATED
cas.authn.ldap[0].ldapUrl=ldap://localhost:1389
cas.authn.ldap[0].baseDn=dc=example,dc=com
cas.authn.ldap[0].userFilter=mail={user}
cas.authn.ldap[0].subtreeSearch=true
cas.authn.ldap[0].bindDn=cn=Directory Manager
cas.authn.ldap[0].bindCredential=password
cas.authn.ldap[0].useSsl=false
cas.authn.ldap[0].dnFormat=cn=*,dc=example,dc=com
cas.authn.ldap[0].principalAttributeList=uid,sn,cn,givenName,mail
cas.authn.ldap[0].principalAttributeId=mail

cas.authn.attributeRepository.defaultAttributesToRelease=uid,sn,cn,givenName,mail
```
- Registrare il servizio creando il file `src/main/resources/services/Liferay-10000003.json`

```
{
  "@class": "org.apereo.cas.services.RegexRegisteredService",
  "serviceId": "^http://localhost:8080/.*",
  "name": "Liferay",
  "id": 10000003,
  "accessStrategy": {
    "@class": "org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy",
    "enabled": true,
    "ssoEnabled": true,
    "attributeReleasePolicy": {
      "@class": "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
    }
  }
}
```
- `sudo ./build.sh run` per lanciare CAS

## Configurare Liferay per utilizzare CAS
- In `portal-setup-wizard.properties` aggiungere

```
# CAS 
cas.auth.enabled=true
cas.login.url=https://localhost:8443/cas/login
cas.logout.url=https://localhost:8443/cas/logout
cas.server.name=http://localhost:8080
cas.server.url=https://localhost:8443/cas
```

## Liferay CAS filter
- Buildare `liferay-cas-filter`
- Copiare il jar risultante in `liferay-folder/tomcat-7.0.62/webapps/ROOT/WEB-INF/lib`
- Assicurarsi che nella stessa cartella siano presenti `cas-client-core-3.5.1.jar` e `cas-client-support-saml-3.5.1.jar`
- Modificare il file `liferay-folder/tomcat-7.0.62/webapps/ROOT/WEB-INF/liferay-web.xml`sostituendo 

```
<filter-name>SSO CAS Filter</filter-name>
<filter-class>com.liferay.portal.servlet.filters.sso.cas.CASFilter</filter-class>
```
con
```
<filter-name>SSO CAS Filter</filter-name>
<filter-class>net.tirasa.liferay.cas.filter.MyLiferayCASFilter</filter-class>
```

## Custom hook
- Modificare nel `pom.xml`la proprietà `liferay.folder` indicando il percorso della cartella di Liferay
- Buildare `liferay-hook`
- `mvn liferay:deploy` per deployare l'hook
