import { useEffect, useRef, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { paymentApi } from '../api';

// Razorpay redirects the browser here with ?razorpay_payment_id=...
// We ask the backend to verify it, which activates the subscription / settles the fine.
export default function PaymentSuccess() {
  const [params] = useSearchParams();
  const razorpayPaymentId = params.get('razorpay_payment_id');
  const [status, setStatus] = useState('verifying'); // verifying | success | error
  const [payment, setPayment] = useState(null);
  const [error, setError] = useState('');
  const alreadyRan = useRef(false);

  useEffect(() => {
    // StrictMode runs effects twice in development; verify only once.
    if (alreadyRan.current) return;
    alreadyRan.current = true;

    if (!razorpayPaymentId) {
      setStatus('error');
      setError('No payment ID found in the URL.');
      return;
    }
    paymentApi
      .verify(razorpayPaymentId)
      .then((result) => {
        setPayment(result);
        setStatus('success');
      })
      .catch((err) => {
        setError(err.message);
        setStatus('error');
      });
  }, [razorpayPaymentId]);

  const isMembership = payment?.paymentType === 'MEMBERSHIP';

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ textAlign: 'center' }}>
        {status === 'verifying' && <h1>🔄 Verifying payment...</h1>}

        {status === 'success' && (
          <>
            <h1>✅ Payment verified</h1>
            <p>
              {isMembership
                ? 'Your subscription is now active.'
                : 'Your fine has been paid.'}
            </p>
            <Link className="btn btn-primary" to={isMembership ? '/subscriptions' : '/my-fines'}>
              Continue
            </Link>
          </>
        )}

        {status === 'error' && (
          <>
            <h1>❌ Verification failed</h1>
            <p>{error}</p>
            <Link className="btn btn-primary" to="/">
              Back to dashboard
            </Link>
          </>
        )}

        {razorpayPaymentId && <p className="muted">Payment ID: {razorpayPaymentId}</p>}
      </div>
    </div>
  );
}
