import { useEffect, useState } from 'react';
import { fineApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Badge from '../components/Badge';
import { FINE_STATUSES, formatDate, formatMoney } from '../utils';

export default function MyFines() {
  const [fines, setFines] = useState([]);
  const [status, setStatus] = useState('');
  const { message, showError, clear } = useMessage();

  useEffect(() => {
    fineApi.my(status).then(setFines).catch(showError);
  }, [status]);

  // The backend creates a Razorpay payment link; we send the user there.
  // After paying, Razorpay redirects back to /payment-success.html.
  async function pay(fine) {
    try {
      const res = await fineApi.pay(fine.id);
      if (res.checkOutUrl) {
        window.location.href = res.checkOutUrl;
      } else {
        showError(res.message || 'Could not start payment');
      }
    } catch (err) {
      showError(err);
    }
  }

  const totalDue = fines.reduce((sum, f) => sum + (f.amountOutstanding || 0), 0);

  return (
    <div>
      <h1>My Fines</h1>
      <Alert message={message} onClose={clear} />

      <div className="toolbar">
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">All statuses</option>
          {FINE_STATUSES.map((s) => (
            <option key={s}>{s}</option>
          ))}
        </select>
        <strong>Total outstanding: {formatMoney(totalDue)}</strong>
      </div>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Book</th>
              <th>Type</th>
              <th>Reason</th>
              <th>Amount</th>
              <th>Outstanding</th>
              <th>Status</th>
              <th>Date</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {fines.map((fine) => (
              <tr key={fine.id}>
                <td>{fine.bookTitle}</td>
                <td>{fine.type}</td>
                <td>{fine.reason || '-'}</td>
                <td>{formatMoney(fine.amount)}</td>
                <td>{formatMoney(fine.amountOutstanding)}</td>
                <td>
                  <Badge value={fine.status} />
                </td>
                <td>{formatDate(fine.createdAt)}</td>
                <td>
                  {(fine.status === 'PENDING' || fine.status === 'PARTIALLY_PAID') && (
                    <button className="btn btn-success btn-sm" onClick={() => pay(fine)}>
                      Pay now
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {fines.length === 0 && <p className="muted">No fines. 🎉</p>}
      </div>
    </div>
  );
}
