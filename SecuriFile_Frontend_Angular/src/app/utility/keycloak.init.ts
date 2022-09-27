
import {  KeycloakService } from 'keycloak-angular';


export function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8100',
        realm: 'SecuriFile',
        clientId: 'angular-client'
      },
      initOptions:{
        checkLoginIframe:true,
        checkLoginIframeInterval:25
      },
      loadUserProfileAtStartUp:true
    
    });
}