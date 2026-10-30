import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';

// Links every user can see.
const userLinks = [
  { to: '/', label: 'Dashboard' },
  { to: '/books', label: 'Browse Books' },
  { to: '/my-loans', label: 'My Loans' },
  { to: '/my-reservations', label: 'My Reservations' },
  { to: '/wishlist', label: 'Wishlist' },
  { to: '/my-reviews', label: 'My Reviews' },
  { to: '/my-fines', label: 'My Fines' },
  { to: '/subscriptions', label: 'Subscription' },
  { to: '/profile', label: 'Profile' },
];

// Extra links only admins can see.
const adminLinks = [
  { to: '/admin/books', label: 'Manage Books' },
  { to: '/admin/genres', label: 'Manage Genres' },
  { to: '/admin/loans', label: 'All Loans' },
  { to: '/admin/reservations', label: 'All Reservations' },
  { to: '/admin/fines', label: 'All Fines' },
  { to: '/admin/plans', label: 'Subscription Plans' },
  { to: '/admin/subscriptions', label: 'All Subscriptions' },
  { to: '/admin/payments', label: 'Payments' },
  { to: '/admin/users', label: 'Users' },
];

export default function Layout() {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="layout">
      <aside className="sidebar">
        <h2 className="logo">📚 LibraryMS</h2>

        <nav>
          {userLinks.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.to === '/'}>
              {link.label}
            </NavLink>
          ))}

          {isAdmin && (
            <>
              <p className="nav-heading">Admin</p>
              {adminLinks.map((link) => (
                <NavLink key={link.to} to={link.to}>
                  {link.label}
                </NavLink>
              ))}
            </>
          )}
        </nav>
      </aside>

      <div className="main">
        <header className="topbar">
          <span>
            Hello, <strong>{user?.fullName}</strong>{' '}
            <span className="badge badge-gray">{isAdmin ? 'ADMIN' : 'USER'}</span>
          </span>
          <button className="btn btn-light" onClick={handleLogout}>
            Logout
          </button>
        </header>
        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
