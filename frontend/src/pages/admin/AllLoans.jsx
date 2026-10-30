import { useEffect, useState } from 'react';
import { fineApi, loanApi, userApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';
import { FINE_TYPES, formatDate, formatMoney, LOAN_STATUSES } from '../../utils';

export default function AllLoans() {
  const [loans, setLoans] = useState(null);
  const [users, setUsers] = useState([]);
  const [filters, setFilters] = useState({ userId: '', status: '', overdueOnly: false });
  const [page, setPage] = useState(0);
  // Which popup is open: { type: 'issue' | 'checkin' | 'fine', loan?, ...fields }
  const [popup, setPopup] = useState(null);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    userApi.list().then(setUsers).catch(showError);
  }, []);

  useEffect(() => {
    loadLoans();
  }, [filters, page]);

  function loadLoans() {
    loanApi
      .search({
        userId: filters.userId || null,
        status: filters.status || null,
        overdueOnly: filters.overdueOnly || null,
        page,
        size: 15,
      })
      .then(setLoans)
      .catch(showError);
  }

  function changeFilter(field, value) {
    setFilters({ ...filters, [field]: value });
    setPage(0);
  }

  async function run(action, successText) {
    try {
      await action();
      showSuccess(successText);
      setPopup(null);
      loadLoans();
    } catch (err) {
      showError(err);
    }
  }

  function updatePopup(field, value) {
    setPopup({ ...popup, [field]: value });
  }

  function submitPopup(e) {
    e.preventDefault();
    if (popup.type === 'issue') {
      run(
        () => loanApi.checkoutForUser(popup.userId, Number(popup.bookId), Number(popup.days) || null, popup.notes),
        'Book issued to user'
      );
    } else if (popup.type === 'checkin') {
      run(() => loanApi.checkin(popup.loan.id, popup.condition, popup.notes), 'Book checked in');
    } else if (popup.type === 'fine') {
      run(
        () =>
          fineApi.create({
            bookLoanId: popup.loan.id,
            type: popup.fineType,
            amount: Number(popup.amount),
            reason: popup.reason,
          }),
        'Fine created'
      );
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>All Book Loans</h1>
        <div className="row">
          <button className="btn btn-primary" onClick={() => setPopup({ type: 'issue', userId: '', bookId: '', days: 14, notes: '' })}>
            + Issue book to user
          </button>
          <button className="btn btn-warning" onClick={() => run(loanApi.updateOverdue, 'Overdue loans updated')}>
            Mark overdue loans
          </button>
        </div>
      </div>
      <Alert message={message} onClose={clear} />

      <div className="toolbar">
        <select value={filters.userId} onChange={(e) => changeFilter('userId', e.target.value)}>
          <option value="">All users</option>
          {users.map((u) => (
            <option key={u.id} value={u.id}>
              {u.fullName} ({u.email})
            </option>
          ))}
        </select>
        <select value={filters.status} onChange={(e) => changeFilter('status', e.target.value)}>
          <option value="">All statuses</option>
          {LOAN_STATUSES.map((s) => (
            <option key={s}>{s}</option>
          ))}
        </select>
        <label className="row" style={{ alignItems: 'center' }}>
          <input type="checkbox" checked={filters.overdueOnly} onChange={(e) => changeFilter('overdueOnly', e.target.checked)} />
          Overdue only
        </label>
      </div>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Book</th>
              <th>Checked out</th>
              <th>Due</th>
              <th>Status</th>
              <th>Fine</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loans?.content.map((loan) => (
              <tr key={loan.id}>
                <td>{loan.id}</td>
                <td>
                  {loan.userName}
                  <div className="muted">{loan.userEmail}</div>
                </td>
                <td>{loan.bookTitle}</td>
                <td>{formatDate(loan.checkoutDate)}</td>
                <td>{formatDate(loan.dueDate)}</td>
                <td>
                  <Badge value={loan.status} />
                </td>
                <td>{loan.fineAmount ? formatMoney(loan.fineAmount) : '-'}</td>
                <td>
                  <div className="row">
                    {(loan.status === 'CHECKED_OUT' || loan.status === 'OVERDUE') && (
                      <>
                        <button
                          className="btn btn-success btn-sm"
                          onClick={() => setPopup({ type: 'checkin', loan, condition: 'RETURNED', notes: '' })}
                        >
                          Check in
                        </button>
                        <button
                          className="btn btn-light btn-sm"
                          onClick={() => run(() => loanApi.renew(loan.id, 14), 'Loan renewed for 14 days')}
                        >
                          Renew
                        </button>
                      </>
                    )}
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => setPopup({ type: 'fine', loan, fineType: 'OVERDUE', amount: '', reason: '' })}
                    >
                      Add fine
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {loans?.content.length === 0 && <p className="muted">No loans found.</p>}
      </div>
      <Pagination data={loans} onPageChange={setPage} />

      {popup?.type === 'issue' && (
        <Modal title="Issue book to user" onClose={() => setPopup(null)}>
          <form className="form" onSubmit={submitPopup}>
            <label>
              User *
              <select value={popup.userId} onChange={(e) => updatePopup('userId', e.target.value)} required>
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
              <input type="number" value={popup.bookId} onChange={(e) => updatePopup('bookId', e.target.value)} required />
            </label>
            <label>
              Days
              <input type="number" min="1" value={popup.days} onChange={(e) => updatePopup('days', e.target.value)} />
            </label>
            <label>
              Notes
              <input value={popup.notes} onChange={(e) => updatePopup('notes', e.target.value)} />
            </label>
            <button className="btn btn-primary">Issue</button>
          </form>
        </Modal>
      )}

      {popup?.type === 'checkin' && (
        <Modal title={`Check in "${popup.loan.bookTitle}"`} onClose={() => setPopup(null)}>
          <form className="form" onSubmit={submitPopup}>
            <label>
              Condition
              <select value={popup.condition} onChange={(e) => updatePopup('condition', e.target.value)}>
                <option value="RETURNED">Returned (good condition)</option>
                <option value="DAMAGED">Damaged</option>
                <option value="LOST">Lost</option>
              </select>
            </label>
            <label>
              Notes
              <input value={popup.notes} onChange={(e) => updatePopup('notes', e.target.value)} />
            </label>
            <button className="btn btn-success">Check in</button>
          </form>
        </Modal>
      )}

      {popup?.type === 'fine' && (
        <Modal title={`Add fine for "${popup.loan.bookTitle}"`} onClose={() => setPopup(null)}>
          <form className="form" onSubmit={submitPopup}>
            <label>
              Type
              <select value={popup.fineType} onChange={(e) => updatePopup('fineType', e.target.value)}>
                {FINE_TYPES.map((t) => (
                  <option key={t}>{t}</option>
                ))}
              </select>
            </label>
            <label>
              Amount (₹) *
              <input type="number" min="1" value={popup.amount} onChange={(e) => updatePopup('amount', e.target.value)} required />
            </label>
            <label>
              Reason
              <input value={popup.reason} onChange={(e) => updatePopup('reason', e.target.value)} />
            </label>
            <button className="btn btn-danger">Create fine</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
