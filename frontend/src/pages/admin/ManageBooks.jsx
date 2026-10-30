import { useEffect, useState } from 'react';
import { bookApi, genreApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import Pagination from '../../components/Pagination';

const EMPTY_BOOK = {
  isbn: '',
  title: '',
  author: '',
  genreId: '',
  publisher: '',
  publicationDate: '',
  language: '',
  pages: '',
  description: '',
  totalCopies: 1,
  availableCopies: 1,
  price: '',
  coverImageUrl: '',
};

export default function ManageBooks() {
  const [books, setBooks] = useState(null);
  const [genres, setGenres] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [form, setForm] = useState(null); // null = modal closed
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    genreApi.list().then(setGenres).catch(showError);
  }, []);

  useEffect(() => {
    loadBooks();
  }, [page]);

  function loadBooks() {
    bookApi.search({ searchTerm: searchTerm || null, page, size: 15 }).then(setBooks).catch(showError);
  }

  function openEdit(book) {
    // Replace nulls with '' so the inputs stay controlled.
    const values = { ...EMPTY_BOOK };
    Object.keys(values).forEach((key) => {
      values[key] = book[key] ?? '';
    });
    setForm({ ...values, id: book.id });
  }

  function update(field, value) {
    setForm({ ...form, [field]: value });
  }

  async function save(e) {
    e.preventDefault();
    // Convert empty strings to null and number fields to numbers.
    const payload = {
      ...form,
      genreId: Number(form.genreId),
      pages: form.pages ? Number(form.pages) : null,
      totalCopies: Number(form.totalCopies),
      availableCopies: Number(form.availableCopies),
      price: form.price === '' ? null : Number(form.price),
      publicationDate: form.publicationDate || null,
    };
    try {
      if (form.id) {
        await bookApi.update(form.id, payload);
        showSuccess('Book updated');
      } else {
        await bookApi.create(payload);
        showSuccess('Book added');
      }
      setForm(null);
      loadBooks();
    } catch (err) {
      showError(err);
    }
  }

  async function remove(book, permanent) {
    const text = permanent
      ? `Permanently delete "${book.title}"? This cannot be undone.`
      : `Deactivate "${book.title}"? (soft delete)`;
    if (!window.confirm(text)) return;
    try {
      if (permanent) await bookApi.hardDelete(book.id);
      else await bookApi.softDelete(book.id);
      showSuccess(permanent ? 'Book permanently deleted' : 'Book deactivated');
      loadBooks();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Manage Books</h1>
        <button className="btn btn-primary" onClick={() => setForm({ ...EMPTY_BOOK })}>
          + Add book
        </button>
      </div>
      <Alert message={message} onClose={clear} />

      <form
        className="toolbar"
        onSubmit={(e) => {
          e.preventDefault();
          page === 0 ? loadBooks() : setPage(0);
        }}
      >
        <input placeholder="Search title, author, ISBN" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
        <button className="btn btn-primary">Search</button>
      </form>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>ISBN</th>
              <th>Genre</th>
              <th>Copies</th>
              <th>Active</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {books?.content.map((book) => (
              <tr key={book.id}>
                <td>{book.id}</td>
                <td>
                  <strong>{book.title}</strong>
                  <div className="muted">{book.author}</div>
                </td>
                <td>{book.isbn}</td>
                <td>{book.genreName}</td>
                <td>
                  {book.availableCopies} / {book.totalCopies}
                </td>
                <td>
                  <Badge value={book.active ? 'ACTIVE' : 'INACTIVE'} />
                </td>
                <td>
                  <div className="row">
                    <button className="btn btn-light btn-sm" onClick={() => openEdit(book)}>
                      Edit
                    </button>
                    <button className="btn btn-warning btn-sm" onClick={() => remove(book, false)}>
                      Deactivate
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => remove(book, true)}>
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <Pagination data={books} onPageChange={setPage} />

      {form && (
        <Modal title={form.id ? 'Edit book' : 'Add book'} onClose={() => setForm(null)}>
          <form className="form" onSubmit={save}>
            <div className="form-grid">
              <label>
                Title *
                <input value={form.title} onChange={(e) => update('title', e.target.value)} required />
              </label>
              <label>
                Author *
                <input value={form.author} onChange={(e) => update('author', e.target.value)} required />
              </label>
              <label>
                ISBN *
                <input value={form.isbn} onChange={(e) => update('isbn', e.target.value)} required />
              </label>
              <label>
                Genre *
                <select value={form.genreId} onChange={(e) => update('genreId', e.target.value)} required>
                  <option value="">Select genre</option>
                  {genres.map((g) => (
                    <option key={g.id} value={g.id}>
                      {g.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Total copies *
                <input type="number" min="0" value={form.totalCopies} onChange={(e) => update('totalCopies', e.target.value)} required />
              </label>
              <label>
                Available copies *
                <input type="number" min="0" value={form.availableCopies} onChange={(e) => update('availableCopies', e.target.value)} required />
              </label>
              <label>
                Publisher
                <input value={form.publisher} onChange={(e) => update('publisher', e.target.value)} />
              </label>
              <label>
                Publication date
                <input type="date" value={form.publicationDate} onChange={(e) => update('publicationDate', e.target.value)} />
              </label>
              <label>
                Language
                <input value={form.language} onChange={(e) => update('language', e.target.value)} />
              </label>
              <label>
                Pages
                <input type="number" min="1" value={form.pages} onChange={(e) => update('pages', e.target.value)} />
              </label>
              <label>
                Price
                <input type="number" min="0" step="0.01" value={form.price} onChange={(e) => update('price', e.target.value)} />
              </label>
              <label>
                Cover image URL
                <input value={form.coverImageUrl} onChange={(e) => update('coverImageUrl', e.target.value)} />
              </label>
            </div>
            <label>
              Description
              <textarea value={form.description} onChange={(e) => update('description', e.target.value)} />
            </label>
            <button className="btn btn-primary">Save</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
