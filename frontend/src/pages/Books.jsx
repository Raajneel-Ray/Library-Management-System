import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { bookApi, genreApi, loanApi, reservationApi, wishlistApi } from '../api';
import Alert, { useMessage } from '../components/Alert';
import Pagination from '../components/Pagination';

export default function Books() {
  const [books, setBooks] = useState(null);
  const [genres, setGenres] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [genreId, setGenreId] = useState('');
  const [availableOnly, setAvailableOnly] = useState(false);
  const [page, setPage] = useState(0);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    genreApi.list().then(setGenres).catch(showError);
  }, []);

  // Reload whenever the page or a filter changes.
  useEffect(() => {
    loadBooks();
  }, [page, genreId, availableOnly]);

  function loadBooks() {
    bookApi
      .search({
        searchTerm: searchTerm || null,
        genreId: genreId || null,
        availableOnly,
        page,
        size: 12,
      })
      .then(setBooks)
      .catch(showError);
  }

  function handleSearch(e) {
    e.preventDefault();
    if (page === 0) loadBooks();
    else setPage(0);
  }

  // Runs an action, shows the result, then refreshes the list.
  async function run(action, successText) {
    try {
      await action();
      showSuccess(successText);
      loadBooks();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <h1>Browse Books</h1>
      <Alert message={message} onClose={clear} />

      <form className="toolbar" onSubmit={handleSearch}>
        <input
          placeholder="Search by title, author or ISBN"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={{ minWidth: 260 }}
        />
        <select
          value={genreId}
          onChange={(e) => {
            setGenreId(e.target.value);
            setPage(0);
          }}
        >
          <option value="">All genres</option>
          {genres.map((g) => (
            <option key={g.id} value={g.id}>
              {g.name}
            </option>
          ))}
        </select>
        <label className="row" style={{ alignItems: 'center' }}>
          <input
            type="checkbox"
            checked={availableOnly}
            onChange={(e) => {
              setAvailableOnly(e.target.checked);
              setPage(0);
            }}
          />
          Available only
        </label>
        <button className="btn btn-primary">Search</button>
      </form>

      {!books ? (
        <p>Loading...</p>
      ) : books.content.length === 0 ? (
        <p className="muted">No books found.</p>
      ) : (
        <div className="grid">
          {books.content.map((book) => (
            <div className="book-card" key={book.id}>
              {book.coverImageUrl ? (
                <img src={book.coverImageUrl} alt={book.title} />
              ) : (
                <div className="book-cover-placeholder">📖</div>
              )}
              <div className="body">
                <strong>{book.title}</strong>
                <span className="muted">by {book.author}</span>
                <span className="muted">{book.genreName}</span>
                <span className="muted">
                  {book.availableCopies} of {book.totalCopies} available
                </span>

                <div className="actions">
                  <Link className="btn btn-light btn-sm" to={`/books/${book.id}`}>
                    Details
                  </Link>

                  {book.alreadyHaveLoan ? (
                    <span className="badge badge-yellow">Borrowed</span>
                  ) : book.availableCopies > 0 ? (
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => run(() => loanApi.checkout(book.id), `You borrowed "${book.title}"`)}
                    >
                      Borrow
                    </button>
                  ) : book.alreadyHaveReservation ? (
                    <span className="badge badge-yellow">Reserved</span>
                  ) : (
                    <button
                      className="btn btn-warning btn-sm"
                      onClick={() => run(() => reservationApi.create(book.id), `"${book.title}" reserved`)}
                    >
                      Reserve
                    </button>
                  )}

                  <button
                    className="btn btn-light btn-sm"
                    onClick={() => run(() => wishlistApi.add(book.id), `"${book.title}" added to wishlist`)}
                  >
                    ♥ Wishlist
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Pagination data={books} onPageChange={setPage} />
    </div>
  );
}
