// Previous / Next buttons for backend PageResponse objects.
// Works with both PageResponse (pageNumber) and Spring Page (number).
export default function Pagination({ data, onPageChange }) {
  if (!data || data.totalPages <= 1) return null;
  const current = data.pageNumber ?? data.number ?? 0;

  return (
    <div className="pagination">
      <button className="btn btn-light" disabled={current === 0} onClick={() => onPageChange(current - 1)}>
        ← Previous
      </button>
      <span>
        Page {current + 1} of {data.totalPages}
      </span>
      <button
        className="btn btn-light"
        disabled={current + 1 >= data.totalPages}
        onClick={() => onPageChange(current + 1)}
      >
        Next →
      </button>
    </div>
  );
}
