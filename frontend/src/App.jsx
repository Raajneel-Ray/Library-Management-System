import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './AuthContext';
import Layout from './components/Layout';

import Login from './pages/Login';
import Signup from './pages/Signup';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import PaymentSuccess from './pages/PaymentSuccess';

import Dashboard from './pages/Dashboard';
import Books from './pages/Books';
import BookDetails from './pages/BookDetails';
import MyLoans from './pages/MyLoans';
import MyReservations from './pages/MyReservations';
import Wishlist from './pages/Wishlist';
import MyFines from './pages/MyFines';
import MyReviews from './pages/MyReviews';
import Subscriptions from './pages/Subscriptions';
import Profile from './pages/Profile';

import ManageBooks from './pages/admin/ManageBooks';
import ManageGenres from './pages/admin/ManageGenres';
import AllLoans from './pages/admin/AllLoans';
import AllReservations from './pages/admin/AllReservations';
import AllFines from './pages/admin/AllFines';
import ManagePlans from './pages/admin/ManagePlans';
import AllSubscriptions from './pages/admin/AllSubscriptions';
import AllPayments from './pages/admin/AllPayments';
import Users from './pages/admin/Users';

// Only logged-in users can see these pages.
function RequireLogin({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" replace />;
}

// Only admins can see these pages; normal users are sent to the dashboard.
function RequireAdmin({ children }) {
  const { user, isAdmin } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return isAdmin ? children : <Navigate to="/" replace />;
}

export default function App() {
  return (
    <Routes>
      {/* Public pages */}
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      {/* Razorpay redirects here after payment (see RazorpayService callback_url) */}
      <Route path="/payment-success.html" element={<PaymentSuccess />} />
      <Route path="/payment-success" element={<PaymentSuccess />} />

      {/* Pages for every logged-in user */}
      <Route element={<RequireLogin><Layout /></RequireLogin>}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/books" element={<Books />} />
        <Route path="/books/:id" element={<BookDetails />} />
        <Route path="/my-loans" element={<MyLoans />} />
        <Route path="/my-reservations" element={<MyReservations />} />
        <Route path="/wishlist" element={<Wishlist />} />
        <Route path="/my-fines" element={<MyFines />} />
        <Route path="/my-reviews" element={<MyReviews />} />
        <Route path="/subscriptions" element={<Subscriptions />} />
        <Route path="/profile" element={<Profile />} />

        {/* Admin-only pages */}
        <Route path="/admin/books" element={<RequireAdmin><ManageBooks /></RequireAdmin>} />
        <Route path="/admin/genres" element={<RequireAdmin><ManageGenres /></RequireAdmin>} />
        <Route path="/admin/loans" element={<RequireAdmin><AllLoans /></RequireAdmin>} />
        <Route path="/admin/reservations" element={<RequireAdmin><AllReservations /></RequireAdmin>} />
        <Route path="/admin/fines" element={<RequireAdmin><AllFines /></RequireAdmin>} />
        <Route path="/admin/plans" element={<RequireAdmin><ManagePlans /></RequireAdmin>} />
        <Route path="/admin/subscriptions" element={<RequireAdmin><AllSubscriptions /></RequireAdmin>} />
        <Route path="/admin/payments" element={<RequireAdmin><AllPayments /></RequireAdmin>} />
        <Route path="/admin/users" element={<RequireAdmin><Users /></RequireAdmin>} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
