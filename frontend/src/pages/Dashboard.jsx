import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { bookApi, fineApi, genreApi, loanApi, subscriptionApi, userApi, wishlistApi } from '../api';
import Badge from '../components/Badge';
import { formatDate } from '../utils';

function Stat({ label, value, to }) {
  return (
    <Link to={to} className="stat" style={{ textDecoration: 'none' }}>
      <div className="number">{value ?? '-'}</div>
      <div className="label">{label}</div>
    </Link>
  );
}

export default function Dashboard() {
  const { user, isAdmin } = useAuth();
  const [stats, setStats] = useState({});
  const [loans, setLoans] = useState([]);
  const [subscription, setSubscription] = useState(null);

  useEffect(() => {
    // Each call is independent; a failure in one should not break the page.
    bookApi.stats().then((s) => setStats((old) => ({ ...old, ...s }))).catch(() => {});
    wishlistApi.count().then((r) => setStats((old) => ({ ...old, wishlist: r.count }))).catch(() => {});
    fineApi
      .my('PENDING')
      .then((fines) => setStats((old) => ({ ...old, pendingFines: fines.length })))
      .catch(() => {});
    loanApi
      .my('CHECKED_OUT')
      .then((page) => {
        setLoans(page.content);
        setStats((old) => ({ ...old, activeLoans: page.totalElements }));
      })
      .catch(() => {});
    subscriptionApi.myActive().then(setSubscription).catch(() => setSubscription(null));

    if (isAdmin) {
      userApi.list().then((u) => setStats((old) => ({ ...old, users: u.length }))).catch(() => {});
      genreApi.count().then((c) => setStats((old) => ({ ...old, genres: c }))).catch(() => {});
    }
  }, [isAdmin]);

  return (
    <div>
      <h1>Welcome, {user?.fullName}</h1>

      <div className="grid" style={{ marginBottom: 20 }}>
        <Stat label="Books in library" value={stats.totalActiveBooks} to="/books" />
        <Stat label="Books available" value={stats.totalAvailableBooks} to="/books" />
        <Stat label="My active loans" value={stats.activeLoans} to="/my-loans" />
        <Stat label="My wishlist" value={stats.wishlist} to="/wishlist" />
        <Stat label="My pending fines" value={stats.pendingFines} to="/my-fines" />
        {isAdmin && <Stat label="Registered users" value={stats.users} to="/admin/users" />}
        {isAdmin && <Stat label="Active genres" value={stats.genres} to="/admin/genres" />}
      </div>

      <div className="card">
        <h3>My Subscription</h3>
        {subscription ? (
          <p>
            <strong>{subscription.planName}</strong> — valid until {formatDate(subscription.endDate)} (
            {subscription.daysRemaining} days left). You can borrow up to {subscription.maxBooksAllowed} books.
          </p>
        ) : (
          <p>
            You don't have an active subscription. <Link to="/subscriptions">Choose a plan</Link> to start borrowing
            books.
          </p>
        )}
      </div>

      <div className="card">
        <h3>Books I'm currently reading</h3>
        {loans.length === 0 ? (
          <p className="muted">No active loans.</p>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Book</th>
                  <th>Due date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {loans.map((loan) => (
                  <tr key={loan.id}>
                    <td>{loan.bookTitle}</td>
                    <td>{formatDate(loan.dueDate)}</td>
                    <td>
                      <Badge value={loan.isOverdue ? 'OVERDUE' : loan.status} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
