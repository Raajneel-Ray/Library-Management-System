import { useEffect, useState } from 'react';
import { fineApi, userApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';
import { FINE_STATUSES, FINE_TYPES, formatDate, formatMoney } from '../../utils';

export default function AllFines() {
  const [fines, setFines] = useState(null);
  const [users, setUsers] = useState([]);
  const [filters, setFilters] = useState({ userId: '', status: '', type: '' });
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    userApi.list().then(setUsers).catch(showError);
  }, []);

  useEffect(() => {
    loadFines();
  }, [filters, page]);

  function loadFines() {
    fineApi.all({ ...filters, page, size: 15 }).then(setFines).catch(showError);
  }

  function changeFilter(field, value) {
    setFilters({ ...filters, [field]: value });
    setPage(0);
  }

  async function run(action, successText) {
    try {
      await action();
      showSuccess(successText);
      loadFines();
    } catch (err) {
      showError(err);
    }
  }

  function waive(fine) {
    const reason = window.prompt('Reason for waiving this fine:');
    if (reason) run(() => fineApi.waive(fine.id, reason), 'Fine waived');
  }

  // For cash / offline payments the admin enters a reference number.
  function markPaid(fine) {
    const transactionId = window.prompt('Enter a transaction / receipt number for this cash payment:');
    if (transactionId) run(() => fineApi.pay(fine.id, transactionId), 'Fine marked as paid');
  }

  return (
    <div>
      <h1>All Fines</h1>
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
        <select value={filters.status} onChange={(e) => changeFilter('status', e.target.value)}>
          <option value="">All statuses</option>
          {FINE_STATUSES.map((s) => (
            <option key={s}>{s}</option>
          ))}
        </select>
        <select value={filters.type} onChange={(e) => changeFilter('type', e.target.value)}>
          <option value="">All types</option>
          {FINE_TYPES.map((t) => (
            <option key={t}>{t}</option>
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
              <th>Type</th>
              <th>Amount</th>
              <th>Outstanding</th>
              <th>Status</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {fines?.content.map((fine) => (
              <tr key={fine.id}>
                <td>{fine.id}</td>
                <td>
                  {fine.userName}
                  <div className="muted">{fine.userEmail}</div>
                </td>
                <td>{fine.bookTitle}</td>
                <td>{fine.type}</td>
                <td>{formatMoney(fine.amount)}</td>
                <td>{formatMoney(fine.amountOutstanding)}</td>
                <td>
                  <Badge value={fine.status} />
                  {fine.waiverReason && <div className="muted">{fine.waiverReason}</div>}
                </td>
                <td>{formatDate(fine.createdAt)}</td>
                <td>
                  {(fine.status === 'PENDING' || fine.status === 'PARTIALLY_PAID') && (
                    <div className="row">
                      <button className="btn btn-success btn-sm" onClick={() => markPaid(fine)}>
                        Mark paid
                      </button>
                      <button className="btn btn-warning btn-sm" onClick={() => waive(fine)}>
                        Waive
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {fines?.content.length === 0 && <p className="muted">No fines found.</p>}
        <p className="muted">To create a new fine, use "Add fine" on the All Loans page.</p>
      </div>
      <Pagination data={fines} onPageChange={setPage} />
    </div>
  );
}
