import { useEffect, useState } from 'react';
import { subscriptionApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import { formatDate, formatMoney } from '../../utils';

export default function AllSubscriptions() {
  const [subscriptions, setSubscriptions] = useState([]);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadSubscriptions();
  }, []);

  function loadSubscriptions() {
    subscriptionApi.all().then(setSubscriptions).catch(showError);
  }

  async function run(action, successText) {
    try {
      await action();
      showSuccess(successText);
      loadSubscriptions();
    } catch (err) {
      showError(err);
    }
  }

  function cancel(sub) {
    const reason = window.prompt(`Reason for cancelling ${sub.userName}'s subscription:`);
    if (reason !== null) run(() => subscriptionApi.cancel(sub.id, reason), 'Subscription cancelled');
  }

  return (
    <div>
      <div className="page-header">
        <h1>All Subscriptions</h1>
        <button
          className="btn btn-warning"
          onClick={() => run(subscriptionApi.deactivateExpired, 'Expired subscriptions deactivated')}
        >
          Deactivate expired
        </button>
      </div>
      <Alert message={message} onClose={clear} />
      <p className="muted">The backend returns the 10 most recent subscriptions.</p>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Plan</th>
              <th>Price</th>
              <th>Start</th>
              <th>End</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {subscriptions.map((sub) => (
              <tr key={sub.id}>
                <td>{sub.id}</td>
                <td>
                  {sub.userName}
                  <div className="muted">{sub.userEmail}</div>
                </td>
                <td>{sub.planName}</td>
                <td>{formatMoney(sub.price, sub.currency)}</td>
                <td>{formatDate(sub.startDate)}</td>
                <td>{formatDate(sub.endDate)}</td>
                <td>
                  <Badge value={sub.isActive ? 'ACTIVE' : 'INACTIVE'} />
                  {sub.cancellationReason && <div className="muted">{sub.cancellationReason}</div>}
                </td>
                <td>
                  {sub.isActive && (
                    <button className="btn btn-danger btn-sm" onClick={() => cancel(sub)}>
                      Cancel
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {subscriptions.length === 0 && <p className="muted">No subscriptions yet.</p>}
      </div>
    </div>
  );
}
