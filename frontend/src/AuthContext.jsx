import { createContext, useContext, useState } from 'react';
import { authApi } from './api';

// Keeps the logged-in user available to every component.
const AuthContext = createContext(null);

function loadUser() {
  try {
    return JSON.parse(localStorage.getItem('user'));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadUser);

  function saveSession(response) {
    localStorage.setItem('jwt', response.jwt);
    localStorage.setItem('user', JSON.stringify(response.user));
    setUser(response.user);
  }

  async function login(email, password) {
    saveSession(await authApi.login(email, password));
  }

  async function signup(form) {
    saveSession(await authApi.signup(form));
  }

  function logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    setUser(null);
  }

  const isAdmin = user?.role === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider value={{ user, isAdmin, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
