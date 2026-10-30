import { useEffect, useState } from 'react';
import { loanApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Badge from '../components/Badge';
import Pagination from '../components/Pagination';
import { formatDate, formatMoney, LOAN_STATUSES } from '../utils';

export default function MyLoans() {
  const [loans, setLoans] = useState(null);
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadLoans();
  }, [status, page]);

  function loadLoans() {
    loanApi.my(status, page).then(setLoans).catch(showError);
  }

  async function renew(loan) {
    try {
      await loanApi.renew(loan.id, 14);
      showSuccess(`"${loan.bookTitle}" renewed for 14 more days`);
      loadLoans();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>My Loans</h1>
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
          {LOAN_STATUSES.map((s) => (
            <option key={s}>{s}</option>
          ))}
        </select>
      </div>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Book</th>
              <th>Checked out</th>
              <th>Due</th>
              <th>Returned</th>
              <th>Status</th>
              <th>Renewals</th>
              <th>Fine</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {loans?.content.map((loan) => (
              <tr key={loan.id}>
                <td>
                  <strong>{loan.bookTitle}</strong>
                  <div className="muted">{loan.bookAuthor}</div>
                </td>
                <td>{formatDate(loan.checkoutDate)}</td>
                <td>
                  {formatDate(loan.dueDate)}
                  {loan.isOverdue && <div className="muted">{loan.overdueDays} days overdue</div>}
                </td>
                <td>{formatDate(loan.returnDate)}</td>
                <td>
                  <Badge value={loan.status} />
                </td>
                <td>
                  {loan.renewalCount ?? 0} / {loan.maxRenewals ?? '-'}
                </td>
                <td>{loan.fineAmount ? formatMoney(loan.fineAmount) : '-'}</td>
                <td>
                  {loan.status === 'CHECKED_OUT' && (
                    <button className="btn btn-primary btn-sm" onClick={() => renew(loan)}>
                      Renew
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {loans?.content.length === 0 && <p className="muted">No loans found.</p>}
        <p className="muted">To return a book, hand it in at the library desk — a librarian will check it in.</p>
      </div>

      <Pagination data={loans} onPageChange={setPage} />
    </div>
  );
}
