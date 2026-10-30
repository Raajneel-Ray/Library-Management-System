import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { authApi } from '../api';

export default function ResetPassword() {
  const [params] = useSearchParams();
  const [token, setToken] = useState(params.get('token') || '');
  const [newPassword, setNewPassword] = useState('');
  const [message, setMessage] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const res = await authApi.resetPassword(token, newPassword);
      setMessage({ type: 'success', text: res.message + '. You can now log in.' });
    } catch (err) {
      setMessage({ type: 'error', text: err.message });
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Reset Password</h1>
        {message && <div className={`alert alert-${message.type}`}>{message.text}</div>}
        <form className="form" onSubmit={handleSubmit}>
          <label>
            Reset token (from your email)
            <input value={token} onChange={(e) => setToken(e.target.value)} required />
          </label>
          <label>
            New password
            <input
              type="password"
              minLength={6}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </label>
          <button className="btn btn-primary">Reset password</button>
        </form>
        <div className="auth-links">
          <Link to="/login">Back to login</Link>
        </div>
      </div>
    </div>
  );
}
