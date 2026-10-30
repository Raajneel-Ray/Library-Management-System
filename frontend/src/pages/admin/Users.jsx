import { useEffect, useState } from 'react';
import { userApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import { formatDateTime } from '../../utils';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState('');
  const { message, showError, clear } = useMessage();

  useEffect(() => {
    userApi.list().then(setUsers).catch(showError);
  }, []);

  const text = search.toLowerCase();
  const visibleUsers = users.filter(
    (u) => u.fullName?.toLowerCase().includes(text) || u.email?.toLowerCase().includes(text)
  );

  return (
    <div>
      <h1>Users</h1>
      <Alert message={message} onClose={clear} />

      <div className="toolbar">
        <input placeholder="Search name or email" value={search} onChange={(e) => setSearch(e.target.value)} />
        <span className="muted">{visibleUsers.length} users</span>
      </div>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Role</th>
              <th>Last login</th>
            </tr>
          </thead>
          <tbody>
            {visibleUsers.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.fullName}</td>
                <td>{u.email}</td>
                <td>{u.phone || '-'}</td>
                <td>
                  <Badge value={u.role === 'ROLE_ADMIN' ? 'ADMIN' : 'USER'} />
                </td>
                <td>{formatDateTime(u.lastLogin)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
