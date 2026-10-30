import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { bookApi, loanApi, reservationApi, reviewApi, wishlistApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Pagination from '../components/Pagination';
import { formatDate } from '../utils';

function Stars({ rating }) {
  return <span className="stars">{'★'.repeat(rating) + '☆'.repeat(5 - rating)}</span>;
}

export default function BookDetails() {
  const { id } = useParams();
  const { user, isAdmin } = useAuth();
  const [book, setBook] = useState(null);
  const [summary, setSummary] = useState(null);
  const [reviews, setReviews] = useState(null);
  const [reviewPage, setReviewPage] = useState(0);
  const [rating, setRating] = useState(5);
  const [reviewText, setReviewText] = useState('');
  const [editingId, setEditingId] = useState(null);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadBook();
  }, [id]);

  useEffect(() => {
    loadReviews();
  }, [id, reviewPage]);

  function loadBook() {
    bookApi.getById(id).then(setBook).catch(showError);
  }

  function loadReviews() {
    reviewApi.forBook(id, reviewPage).then(setReviews).catch(showError);
    reviewApi.summary(id).then(setSummary).catch(() => {});
  }

  async function run(action, successText) {
    try {
      await action();
      showSuccess(successText);
      loadBook();
      loadReviews();
    } catch (err) {
      showError(err);
    }
  }

  function startEdit(review) {
    setEditingId(review.id);
    setRating(review.rating);
    setReviewText(review.reviewText || '');
  }

  function resetForm() {
    setEditingId(null);
    setRating(5);
    setReviewText('');
  }

  async function submitReview(e) {
    e.preventDefault();
    const action = editingId
      ? () => reviewApi.update(editingId, Number(id), rating, reviewText)
      : () => reviewApi.create(Number(id), rating, reviewText);
    await run(action, editingId ? 'Review updated' : 'Review posted');
    resetForm();
  }

  if (!book) return <Alert message={message} onClose={clear} />;

  return (
    <div>
      <Link to="/books">← Back to books</Link>
      <Alert message={message} onClose={clear} />

      <div className="card" style={{ display: 'flex', gap: 24, flexWrap: 'wrap', marginTop: 12 }}>
        <div style={{ width: 200 }}>
          {book.coverImageUrl ? (
            <img src={book.coverImageUrl} alt={book.title} style={{ width: '100%', borderRadius: 8 }} />
          ) : (
            <div className="book-cover-placeholder">📖</div>
          )}
        </div>
        <div style={{ flex: 1, minWidth: 260 }}>
          <h1>{book.title}</h1>
          <p>
            by <strong>{book.author}</strong>
          </p>
          {summary && summary.totalReviews > 0 && (
            <p>
              <Stars rating={Math.round(summary.averageRating)} /> {summary.averageRating.toFixed(1)} (
              {summary.totalReviews} reviews)
            </p>
          )}
          <p>{book.description}</p>
          <p className="muted">
            ISBN: {book.isbn} · Genre: {book.genreName} · Publisher: {book.publisher || '-'} · Published:{' '}
            {formatDate(book.publicationDate)} · Language: {book.language || '-'} · Pages: {book.pages || '-'}
          </p>
          <p>
            <strong>
              {book.availableCopies} of {book.totalCopies}
            </strong>{' '}
            copies available
          </p>

          <div className="row">
            {book.alreadyHaveLoan ? (
              <span className="badge badge-yellow">You have borrowed this book</span>
            ) : book.availableCopies > 0 ? (
              <button className="btn btn-primary" onClick={() => run(() => loanApi.checkout(book.id), 'Book borrowed')}>
                Borrow
              </button>
            ) : book.alreadyHaveReservation ? (
              <span className="badge badge-yellow">You have reserved this book</span>
            ) : (
              <button
                className="btn btn-warning"
                onClick={() => run(() => reservationApi.create(book.id), 'Book reserved')}
              >
                Reserve
              </button>
            )}
            <button className="btn btn-light" onClick={() => run(() => wishlistApi.add(book.id), 'Added to wishlist')}>
              ♥ Add to wishlist
            </button>
            {isAdmin && (
              <Link className="btn btn-light" to="/admin/books">
                Edit in admin
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="card">
        <h3>{editingId ? 'Edit your review' : 'Write a review'}</h3>
        <form className="form" onSubmit={submitReview}>
          <label>
            Rating
            <select value={rating} onChange={(e) => setRating(Number(e.target.value))}>
              {[5, 4, 3, 2, 1].map((r) => (
                <option key={r} value={r}>
                  {'★'.repeat(r)} ({r})
                </option>
              ))}
            </select>
          </label>
          <label>
            Review
            <textarea value={reviewText} maxLength={2000} onChange={(e) => setReviewText(e.target.value)} />
          </label>
          <div className="row">
            <button className="btn btn-primary">{editingId ? 'Update review' : 'Post review'}</button>
            {editingId && (
              <button type="button" className="btn btn-light" onClick={resetForm}>
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="card">
        <h3>Reviews</h3>
        {!reviews || reviews.content.length === 0 ? (
          <p className="muted">No reviews yet.</p>
        ) : (
          reviews.content.map((review) => (
            <div key={review.id} style={{ borderBottom: '1px solid #e5e7eb', padding: '10px 0' }}>
              <Stars rating={review.rating} /> <strong>{review.userName}</strong>{' '}
              <span className="muted">{formatDate(review.createdAt)}</span>
              <p style={{ margin: '6px 0' }}>{review.reviewText}</p>
              {(review.userId === user?.id || isAdmin) && (
                <div className="row">
                  {review.userId === user?.id && (
                    <button className="btn btn-light btn-sm" onClick={() => startEdit(review)}>
                      Edit
                    </button>
                  )}
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => run(() => reviewApi.remove(review.id), 'Review deleted')}
                  >
                    Delete
                  </button>
                </div>
              )}
            </div>
          ))
        )}
        <Pagination data={reviews} onPageChange={setReviewPage} />
      </div>
    </div>
  );
}
