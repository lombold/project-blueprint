export const environment = {
  apiBaseUrl: 'https://api.example.com',
  oidc: {
    authorizationEndpoint: 'https://identity.example.com/oauth2/authorize',
    clientId: 'project-name-app',
    endSessionEndpoint: 'https://identity.example.com/oauth2/logout',
    nativeRedirectUri: 'com.example.projectname://auth/callback',
    tokenEndpoint: 'https://identity.example.com/oauth2/token',
  },
  production: true,
};
