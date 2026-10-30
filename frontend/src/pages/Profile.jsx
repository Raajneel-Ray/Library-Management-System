import { useEffect, useState } from 'react';
import { userApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import { formatDateTime } from '../utils';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const { message, showError, clear } = useMessage();

  useEffect(() => {
    userApi.profile().then(setProfile).catch(showError);
  }, []);

  return (
    <div>
      <h1>My Profile</h1>
      <Alert message={message} onClose={clear} />

      {profile && (
        <div className="card">
          <table>
            <tbody>
              <tr>
                <th>Name</th>
                <td>{profile.fullName}</td>
              </tr>
              <tr>
                <th>Email</th>
                <td>{profile.email}</td>
              </tr>
              <tr>
                <th>Phone</th>
                <td>{profile.phone || '-'}</td>
              </tr>
              <tr>
                <th>Role</th>
                <td>{profile.role === 'ROLE_ADMIN' ? 'Admin' : 'User'}</td>
              </tr>
              <tr>
                <th>Member since</th>
                <td>{formatDateTime(profile.createdAt)}</td>
              </tr>
              <tr>
                <th>Last login</th>
                <td>{formatDateTime(profile.lastLogin)}</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
