import { useEffect, useState } from 'react';
import { planApi, subscriptionApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import { formatDate, formatMoney } from '../utils';

export default function Subscriptions() {
  const [plans, setPlans] = useState([]);
  const [active, setActive] = useState(null);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    load();
  }, []);

  function load() {
    planApi.featured().then(setPlans).catch(showError);
    // The backend returns an error when there is no active subscription.
    subscriptionApi.myActive().then(setActive).catch(() => setActive(null));
  }

  // Subscribing returns a Razorpay checkout link. After paying, Razorpay
  // redirects to /payment-success.html which activates the subscription.
  async function subscribe(plan) {
    try {
      const res = await subscriptionApi.subscribe(plan.id);
      if (res.checkOutUrl) {
        window.location.href = res.checkOutUrl;
      } else {
        showSuccess(res.message || 'Subscription request created');
      }
    } catch (err) {
      showError(err);
    }
  }

  async function cancel() {
    const reason = window.prompt('Why are you cancelling? (optional)');
    if (reason === null) return;
    try {
      await subscriptionApi.cancel(active.id, reason);
      showSuccess('Subscription cancelled');
      load();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>Subscription</h1>
      <Alert message={message} onClose={clear} />

      <div className="card">
        <h3>My current plan</h3>
        {active ? (
          <>
            <p>
              <strong>{active.planName}</strong> ({active.planCode}) — {formatMoney(active.price, active.currency)}
            </p>
            <p>
              From {formatDate(active.startDate)} to {formatDate(active.endDate)} · {active.daysRemaining} days left
            </p>
            <p>
              Up to {active.maxBooksAllowed} books at a time, {active.maxDaysPerBook} days per book.
            </p>
            <button className="btn btn-danger" onClick={cancel}>
              Cancel subscription
            </button>
          </>
        ) : (
          <p className="muted">You don't have an active subscription. Pick a plan below.</p>
        )}
      </div>

      <h2>Available plans</h2>
      <div className="grid">
        {plans.map((plan) => (
          <div className="card" key={plan.id}>
            {plan.badgeText && <span className="badge badge-yellow">{plan.badgeText}</span>}
            <h3 style={{ marginTop: 8 }}>{plan.planName}</h3>
            <div className="stat" style={{ boxShadow: 'none', padding: 0 }}>
              <div className="number">{formatMoney(plan.price, plan.currency)}</div>
              <div className="label">for {plan.durationDays} days</div>
            </div>
            <p>{plan.description}</p>
            <ul>
              <li>{plan.maximumBooksAllowed} books at a time</li>
              <li>{plan.maxDaysPerBook} days per book</li>
            </ul>
            <button className="btn btn-primary" disabled={!!active} onClick={() => subscribe(plan)}>
              {active ? 'Already subscribed' : 'Subscribe'}
            </button>
          </div>
        ))}
      </div>
      {plans.length === 0 && <p className="muted">No plans available right now.</p>}
    </div>
  );
}
