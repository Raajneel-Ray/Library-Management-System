import { useEffect, useState } from 'react';
import { paymentApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Pagination from '../../components/Pagination';
import { formatDateTime, formatMoney } from '../../utils';

export default function AllPayments() {
  const [payments, setPayments] = useState(null);
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadPayments();
  }, [page]);

  function loadPayments() {
    paymentApi.all(page).then(setPayments).catch(showError);
  }

  async function cancel(payment) {
    if (!window.confirm(`Cancel payment #${payment.id}?`)) return;
    try {
      await paymentApi.cancel(payment.id);
      showSuccess('Payment cancelled');
      loadPayments();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>Payments</h1>
      <Alert message={message} onClose={clear} />

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Gateway</th>
              <th>Status</th>
              <th>Created</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {payments?.content.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td>
                  {p.userName}
                  <div className="muted">{p.userEmail}</div>
                </td>
                <td>{p.paymentType}</td>
                <td>{formatMoney(p.amount)}</td>
                <td>{p.gateway}</td>
                <td>
                  <Badge value={p.status} />
                  {p.failureReason && <div className="muted">{p.failureReason}</div>}
                </td>
                <td>{formatDateTime(p.createdAt)}</td>
                <td>
                  {(p.status === 'PENDING' || p.status === 'PROCESSING') && (
                    <button className="btn btn-danger btn-sm" onClick={() => cancel(p)}>
                      Cancel
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {payments?.content.length === 0 && <p className="muted">No payments yet.</p>}
      </div>
      <Pagination data={payments} onPageChange={setPage} />
    </div>
  );
}
