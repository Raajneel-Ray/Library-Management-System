import { useEffect, useState } from 'react';
import { reservationApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Badge from '../components/Badge';
import Pagination from '../components/Pagination';
import { formatDateTime, RESERVATION_STATUSES } from '../utils';

export default function MyReservations() {
  const [reservations, setReservations] = useState(null);
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadReservations();
  }, [status, page]);

  function loadReservations() {
    reservationApi.my({ status, page, size: 10 }).then(setReservations).catch(showError);
  }

  async function cancel(reservation) {
    if (!window.confirm(`Cancel reservation for "${reservation.bookTitle}"?`)) return;
    try {
      await reservationApi.cancel(reservation.id);
      showSuccess('Reservation cancelled');
      loadReservations();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>My Reservations</h1>
      <Alert message={message} onClose={clear} />

      <div className="toolbar">
        <select
          value={status}
          onChange={(e) => {
            setStatus(e.target.value);
            setPage(0);
          }}
        >
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
              <th>Book</th>
              <th>Reserved at</th>
              <th>Queue position</th>
              <th>Available until</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {reservations?.content.map((r) => (
              <tr key={r.id}>
                <td>
                  <strong>{r.bookTitle}</strong>
                  <div className="muted">{r.bookAuthor}</div>
                </td>
                <td>{formatDateTime(r.reservedAt)}</td>
                <td>{r.queuePosition ?? '-'}</td>
                <td>{formatDateTime(r.availableUntil)}</td>
                <td>
                  <Badge value={r.status} />
                </td>
                <td>
                  {r.canBeCancelled && (
                    <button className="btn btn-danger btn-sm" onClick={() => cancel(r)}>
                      Cancel
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {reservations?.content.length === 0 && <p className="muted">No reservations found.</p>}
      </div>

      <Pagination data={reservations} onPageChange={setPage} />
    </div>
  );
}
