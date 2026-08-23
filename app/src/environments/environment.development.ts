export const environment = {
  apiBaseUrl: 'http://localhost:8081',
  oidc: {
    authorizationEndpoint: 'http://localhost:8082/realms/project-name/protocol/openid-connect/auth',
    clientId: 'project-name-app',
    endSessionEndpoint: 'http://localhost:8082/realms/project-name/protocol/openid-connect/logout',
    nativeRedirectUri: 'com.example.projectname://auth/callback',
    tokenEndpoint: 'http://localhost:8082/realms/project-name/protocol/openid-connect/token',
  },
  production: false,
};
