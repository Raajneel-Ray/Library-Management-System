import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { reviewApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Pagination from '../components/Pagination';
import { formatDate } from '../utils';

export default function MyReviews() {
  const [reviews, setReviews] = useState(null);
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadReviews();
  }, [page]);

  function loadReviews() {
    reviewApi.my(page).then(setReviews).catch(showError);
  }

  async function remove(review) {
    if (!window.confirm('Delete this review?')) return;
    try {
      await reviewApi.remove(review.id);
      showSuccess('Review deleted');
      loadReviews();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>My Reviews</h1>
      <Alert message={message} onClose={clear} />

      {reviews?.content.length === 0 && <p className="muted">You haven't reviewed any books yet.</p>}

      {reviews?.content.map((review) => (
        <div className="card" key={review.id}>
          <Link to={`/books/${review.bookId}`}>
            <strong>{review.bookTitle}</strong>
          </Link>{' '}
          <span className="muted">by {review.bookAuthor}</span>
          <div>
            <span className="stars">{'★'.repeat(review.rating) + '☆'.repeat(5 - review.rating)}</span>{' '}
            <span className="muted">{formatDate(review.createdAt)}</span>
          </div>
          <p>{review.reviewText}</p>
          <div className="row">
            <Link className="btn btn-light btn-sm" to={`/books/${review.bookId}`}>
              Edit on book page
            </Link>
            <button className="btn btn-danger btn-sm" onClick={() => remove(review)}>
              Delete
            </button>
          </div>
        </div>
      ))}

      <Pagination data={reviews} onPageChange={setPage} />
    </div>
  );
}
