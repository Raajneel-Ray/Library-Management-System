import { useEffect, useState } from 'react';
import { genreApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';

const EMPTY_GENRE = { code: '', name: '', description: '', displayOrder: 0, parentGenreId: '' };

export default function ManageGenres() {
  const [genres, setGenres] = useState([]);
  const [form, setForm] = useState(null);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadGenres();
  }, []);

  function loadGenres() {
    genreApi.list().then(setGenres).catch(showError);
  }

  function update(field, value) {
    setForm({ ...form, [field]: value });
  }

  async function save(e) {
    e.preventDefault();
    const payload = {
      ...form,
      displayOrder: Number(form.displayOrder) || 0,
      parentGenreId: form.parentGenreId ? Number(form.parentGenreId) : null,
    };
    try {
      if (form.id) await genreApi.update(form.id, payload);
      else await genreApi.create(payload);
      showSuccess(form.id ? 'Genre updated' : 'Genre created');
      setForm(null);
      loadGenres();
    } catch (err) {
      showError(err);
    }
  }

  async function remove(genre, permanent) {
    if (!window.confirm(`${permanent ? 'Permanently delete' : 'Deactivate'} genre "${genre.name}"?`)) return;
    try {
      if (permanent) await genreApi.hardDelete(genre.id);
      else await genreApi.softDelete(genre.id);
      showSuccess('Genre deleted');
      loadGenres();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Manage Genres</h1>
        <button className="btn btn-primary" onClick={() => setForm({ ...EMPTY_GENRE })}>
          + Add genre
        </button>
      </div>
      <Alert message={message} onClose={clear} />

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Code</th>
              <th>Name</th>
              <th>Parent</th>
              <th>Books</th>
              <th>Order</th>
              <th>Active</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {genres.map((genre) => (
              <tr key={genre.id}>
                <td>{genre.id}</td>
                <td>{genre.code}</td>
                <td>
                  <strong>{genre.name}</strong>
                  <div className="muted">{genre.description}</div>
                </td>
                <td>{genre.parentGenreName || '-'}</td>
                <td>{genre.bookCount ?? '-'}</td>
                <td>{genre.displayOrder}</td>
                <td>
                  <Badge value={genre.active ? 'ACTIVE' : 'INACTIVE'} />
                </td>
                <td>
                  <div className="row">
                    <button
                      className="btn btn-light btn-sm"
                      onClick={() =>
                        setForm({
                          id: genre.id,
                          code: genre.code || '',
                          name: genre.name || '',
                          description: genre.description || '',
                          displayOrder: genre.displayOrder ?? 0,
                          parentGenreId: genre.parentGenreId || '',
                        })
                      }
                    >
                      Edit
                    </button>
                    <button className="btn btn-warning btn-sm" onClick={() => remove(genre, false)}>
                      Deactivate
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => remove(genre, true)}>
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {form && (
        <Modal title={form.id ? 'Edit genre' : 'Add genre'} onClose={() => setForm(null)}>
          <form className="form" onSubmit={save}>
            <div className="form-grid">
              <label>
                Code *
                <input value={form.code} onChange={(e) => update('code', e.target.value)} required />
              </label>
              <label>
                Name *
                <input value={form.name} onChange={(e) => update('name', e.target.value)} required />
              </label>
              <label>
                Parent genre
                <select value={form.parentGenreId} onChange={(e) => update('parentGenreId', e.target.value)}>
                  <option value="">None (top level)</option>
                  {genres
                    .filter((g) => g.id !== form.id)
                    .map((g) => (
                      <option key={g.id} value={g.id}>
                        {g.name}
                      </option>
                    ))}
                </select>
              </label>
              <label>
                Display order
                <input type="number" min="0" value={form.displayOrder} onChange={(e) => update('displayOrder', e.target.value)} />
              </label>
            </div>
            <label>
              Description
              <textarea value={form.description} maxLength={500} onChange={(e) => update('description', e.target.value)} />
            </label>
            <button className="btn btn-primary">Save</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
