## Configurare Liferay per utilizzare CAS
- In `portal-setup-wizard.properties` aggiungere

```
# CAS 
cas.auth.enabled=true
cas.login.url=<Apereo CAS prefix URL>/cas/login
cas.logout.url=<Apereo CAS prefix URL>/cas/logout
cas.server.name=<Apereo CAS prefix URL>
cas.server.url=<Apereo CAS prefix URL>/cas
```

## Liferay CAS filter
- Buildare `liferay-cas-filter`
- Copiare il jar risultante tra le librerie referenziate nel container che ospita Liferay
 - es.: `<tomcat folder>/webapps/ROOT/WEB-INF/lib`
- Assicurarsi che nella stessa cartella siano presenti `cas-client-core-3.5.1.jar` e `cas-client-support-saml-3.5.1.jar`
- Modificare il file <tomcat folder>/webapps/ROOT/WEB-INF/liferay-web.xml`sostituendo 

```
<filter-name>SSO CAS Filter</filter-name>
<filter-class>com.liferay.portal.servlet.filters.sso.cas.CASFilter</filter-class>
```
con
```
<filter-name>SSO CAS Filter</filter-name>
<filter-class>net.tirasa.liferay.cas.filter.MyLiferayCASFilter</filter-class>
```

## Hook di esempio per recuperare gli attributi utente dal principal
- Modificare nel `pom.xml`la proprietà `liferay.folder` indicando il percorso della cartella di Liferay
- Buildare `liferay-hook`
- `mvn liferay:deploy` per deployare l'hook
