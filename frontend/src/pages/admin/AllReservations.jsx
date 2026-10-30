import { useEffect, useState } from 'react';
import { reservationApi, userApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';
import { formatDateTime, RESERVATION_STATUSES } from '../../utils';

export default function AllReservations() {
  const [reservations, setReservations] = useState(null);
  const [users, setUsers] = useState([]);
  const [filters, setFilters] = useState({ userId: '', bookId: '', status: '' });
  const [page, setPage] = useState(0);
  const [form, setForm] = useState(null); // "reserve for user" popup
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    userApi.list().then(setUsers).catch(showError);
  }, []);

  useEffect(() => {
    loadReservations();
  }, [filters, page]);

  function loadReservations() {
    reservationApi.search({ ...filters, page, size: 15 }).then(setReservations).catch(showError);
  }

  function changeFilter(field, value) {
    setFilters({ ...filters, [field]: value });
    setPage(0);
  }

  async function run(action, successText) {
    try {
      await action();
      showSuccess(successText);
      setForm(null);
      loadReservations();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>All Reservations</h1>
        <button className="btn btn-primary" onClick={() => setForm({ userId: '', bookId: '', notes: '' })}>
          + Reserve for user
        </button>
      </div>
      <Alert message={message} onClose={clear} />

      <div className="toolbar">
        <select value={filters.userId} onChange={(e) => changeFilter('userId', e.target.value)}>
          <option value="">All users</option>
          {users.map((u) => (
            <option key={u.id} value={u.id}>
              {u.fullName}
            </option>
          ))}
        </select>
        <input
          type="number"
          placeholder="Book ID"
          value={filters.bookId}
          onChange={(e) => changeFilter('bookId', e.target.value)}
          style={{ width: 110 }}
        />
        <select value={filters.status} onChange={(e) => changeFilter('status', e.target.value)}>
          <option value="">All statuses</option>
          {RESERVATION_STATUSES.map((s) => (
            <option key={s}>{s}</option>
          ))}
        </select>
      </div>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Book</th>
              <th>Reserved at</th>
              <th>Queue</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {reservations?.content.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>
                  {r.userName}
                  <div className="muted">{r.userEmail}</div>
                </td>
                <td>{r.bookTitle}</td>
                <td>{formatDateTime(r.reservedAt)}</td>
                <td>{r.queuePosition ?? '-'}</td>
                <td>
                  <Badge value={r.status} />
                </td>
                <td>
                  <div className="row">
                    {(r.status === 'PENDING' || r.status === 'AVAILABLE') && (
                      <button
                        className="btn btn-success btn-sm"
                        onClick={() => run(() => reservationApi.fulfill(r.id), 'Reservation fulfilled (book issued)')}
                      >
                        Fulfill
                      </button>
                    )}
                    {r.canBeCancelled && (
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => run(() => reservationApi.cancel(r.id), 'Reservation cancelled')}
                      >
                        Cancel
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {reservations?.content.length === 0 && <p className="muted">No reservations found.</p>}
      </div>
      <Pagination data={reservations} onPageChange={setPage} />

      {form && (
        <Modal title="Reserve a book for a user" onClose={() => setForm(null)}>
          <form
            className="form"
            onSubmit={(e) => {
              e.preventDefault();
              run(() => reservationApi.createForUser(form.userId, Number(form.bookId), form.notes), 'Reservation created');
            }}
          >
            <label>
              User *
              <select value={form.userId} onChange={(e) => setForm({ ...form, userId: e.target.value })} required>
                <option value="">Select user</option>
                {users.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.fullName} ({u.email})
                  </option>
                ))}
              </select>
            </label>
            <label>
              Book ID *
              <input type="number" value={form.bookId} onChange={(e) => setForm({ ...form, bookId: e.target.value })} required />
            </label>
            <label>
              Notes
              <input value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
            </label>
            <button className="btn btn-primary">Reserve</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
