import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authApi } from '../api';

export default function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const res = await authApi.forgotPassword(email);
      setMessage({ type: 'success', text: res.message });
    } catch (err) {
      setMessage({ type: 'error', text: err.message });
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Forgot Password</h1>
        {message && <div className={`alert alert-${message.type}`}>{message.text}</div>}
        <form className="form" onSubmit={handleSubmit}>
          <label>
            Email
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </label>
          <button className="btn btn-primary">Send reset token</button>
        </form>
        <div className="auth-links">
          Got a token? <Link to="/reset-password">Reset password</Link>
          <br />
          <Link to="/login">Back to login</Link>
        </div>
      </div>
    </div>
  );
}
