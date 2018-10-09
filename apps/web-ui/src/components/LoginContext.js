import React from 'react';

/**
 * The login context contains the auth token and the user information.
 * This information is broadcast throughout the React tree to every component.
 */
const LoginContext = React.createContext(null);

export default LoginContext;