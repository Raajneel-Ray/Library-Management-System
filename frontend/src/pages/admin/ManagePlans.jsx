import { useEffect, useState } from 'react';
import { planApi } from '../../api';
import Alert, { useMessage } from '../../components/Alert';
import Badge from '../../components/Badge';
import Modal from '../../components/Modal';
import { formatMoney } from '../../utils';

const EMPTY_PLAN = {
  planCode: '',
  planName: '',
  description: '',
  durationDays: 30,
  price: '',
  currency: 'INR',
  maximumBooksAllowed: 3,
  maxDaysPerBook: 14,
  displayOrder: 0,
  isActive: true,
  isFeatured: true,
  badgeText: '',
  adminNotes: '',
};

export default function ManagePlans() {
  const [plans, setPlans] = useState([]);
  const [form, setForm] = useState(null);
  const { message, showSuccess, showError, clear } = useMessage();

  useEffect(() => {
    loadPlans();
  }, []);

  function loadPlans() {
    planApi.all().then(setPlans).catch(showError);
  }

  function update(field, value) {
    setForm({ ...form, [field]: value });
  }

  function openEdit(plan) {
    const values = { ...EMPTY_PLAN };
    Object.keys(values).forEach((key) => {
      values[key] = plan[key] ?? values[key];
    });
    setForm({ ...values, id: plan.id });
  }

  async function save(e) {
    e.preventDefault();
    const payload = {
      ...form,
      durationDays: Number(form.durationDays),
      price: Number(form.price),
      maximumBooksAllowed: Number(form.maximumBooksAllowed),
      maxDaysPerBook: Number(form.maxDaysPerBook),
      displayOrder: Number(form.displayOrder) || 0,
    };
    try {
      if (form.id) await planApi.update(form.id, payload);
      else await planApi.create(payload);
      showSuccess(form.id ? 'Plan updated' : 'Plan created');
      setForm(null);
      loadPlans();
    } catch (err) {
      showError(err);
    }
  }

  async function remove(plan) {
    if (!window.confirm(`Delete plan "${plan.planName}"?`)) return;
    try {
      await planApi.remove(plan.id);
      showSuccess('Plan deleted');
      loadPlans();
    } catch (err) {
      showError(err);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Subscription Plans</h1>
        <button className="btn btn-primary" onClick={() => setForm({ ...EMPTY_PLAN })}>
          + Add plan
        </button>
      </div>
      <Alert message={message} onClose={clear} />
      <p className="muted">Only plans that are both Active and Featured are shown to users.</p>

      <div className="card table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Code</th>
              <th>Name</th>
              <th>Price</th>
              <th>Duration</th>
              <th>Books / Days per book</th>
              <th>Active</th>
              <th>Featured</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {plans.map((plan) => (
              <tr key={plan.id}>
                <td>{plan.planCode}</td>
                <td>
                  <strong>{plan.planName}</strong>
                  <div className="muted">{plan.description}</div>
                </td>
                <td>{formatMoney(plan.price, plan.currency)}</td>
                <td>{plan.durationDays} days</td>
                <td>
                  {plan.maximumBooksAllowed} / {plan.maxDaysPerBook}
                </td>
                <td>
                  <Badge value={plan.isActive ? 'YES' : 'NO'} />
                </td>
                <td>
                  <Badge value={plan.isFeatured ? 'YES' : 'NO'} />
                </td>
                <td>
                  <div className="row">
                    <button className="btn btn-light btn-sm" onClick={() => openEdit(plan)}>
                      Edit
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => remove(plan)}>
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
        <Modal title={form.id ? 'Edit plan' : 'Add plan'} onClose={() => setForm(null)}>
          <form className="form" onSubmit={save}>
            <div className="form-grid">
              <label>
                Plan code *
                <input value={form.planCode} onChange={(e) => update('planCode', e.target.value)} required />
              </label>
              <label>
                Plan name *
                <input value={form.planName} onChange={(e) => update('planName', e.target.value)} required />
              </label>
              <label>
                Price (₹) *
                <input type="number" min="1" value={form.price} onChange={(e) => update('price', e.target.value)} required />
              </label>
              <label>
                Duration (days) *
                <input type="number" min="1" value={form.durationDays} onChange={(e) => update('durationDays', e.target.value)} required />
              </label>
              <label>
                Max books at a time *
                <input type="number" min="1" value={form.maximumBooksAllowed} onChange={(e) => update('maximumBooksAllowed', e.target.value)} required />
              </label>
              <label>
                Max days per book *
                <input type="number" min="1" value={form.maxDaysPerBook} onChange={(e) => update('maxDaysPerBook', e.target.value)} required />
              </label>
              <label>
                Badge text
                <input value={form.badgeText} onChange={(e) => update('badgeText', e.target.value)} />
              </label>
              <label>
                Display order
                <input type="number" min="0" value={form.displayOrder} onChange={(e) => update('displayOrder', e.target.value)} />
              </label>
              <label className="checkbox">
                <input type="checkbox" checked={form.isActive} onChange={(e) => update('isActive', e.target.checked)} />
                Active
              </label>
              <label className="checkbox">
                <input type="checkbox" checked={form.isFeatured} onChange={(e) => update('isFeatured', e.target.checked)} />
                Featured (visible to users)
              </label>
            </div>
            <label>
              Description
              <textarea value={form.description} onChange={(e) => update('description', e.target.value)} />
            </label>
            <label>
              Admin notes
              <input value={form.adminNotes} onChange={(e) => update('adminNotes', e.target.value)} />
            </label>
            <button className="btn btn-primary">Save</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
