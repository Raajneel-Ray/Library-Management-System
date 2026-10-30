import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { wishlistApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Pagination from '../components/Pagination';

export default function Wishlist() {
  const [items, setItems] = useState(null);
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadWishlist();
  }, [page]);

  function loadWishlist() {
    wishlistApi.my(page).then(setItems).catch(showError);
  }

  async function remove(item) {
    try {
      await wishlistApi.remove(item.id);
      showSuccess(`"${item.bookTitle}" removed from wishlist`);
      loadWishlist();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>My Wishlist</h1>
      <Alert message={message} onClose={clear} />

      {items?.content.length === 0 && (
        <p className="muted">
          Your wishlist is empty. <Link to="/books">Browse books</Link>
        </p>
      )}

      <div className="grid">
        {items?.content.map((item) => (
          <div className="book-card" key={item.id}>
            {item.bookCoverImageUrl ? (
              <img src={item.bookCoverImageUrl} alt={item.bookTitle} />
            ) : (
              <div className="book-cover-placeholder">📖</div>
            )}
            <div className="body">
              <strong>{item.bookTitle}</strong>
              <span className="muted">by {item.bookAuthor}</span>
              <span className="muted">{item.isBookAvailable ? `${item.availableCopies} available` : 'Not available'}</span>
              <div className="actions">
                <Link className="btn btn-light btn-sm" to={`/books/${item.bookId}`}>
                  View
                </Link>
                <button className="btn btn-danger btn-sm" onClick={() => remove(item)}>
                  Remove
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      <Pagination data={items} onPageChange={setPage} />
    </div>
  );
}
