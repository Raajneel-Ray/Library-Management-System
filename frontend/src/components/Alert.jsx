import { useState } from 'react';

// Hook that stores one success / error message for a page.
export function useMessage() {
  const [message, setMessage] = useState(null);
  return {
    message,
    showSuccess: (text) => setMessage({ type: 'success', text }),
    showError: (error) => setMessage({ type: 'error', text: error?.message || String(error) }),
    clear: () => setMessage(null),
  };
}

// Displays the message from useMessage().
export default function Alert({ message, onClose }) {
  if (!message) return null;
  return (
    <div className={`alert alert-${message.type}`}>
      <span>{message.text}</span>
      {onClose && (
        <button className="alert-close" onClick={onClose}>
          ×
        </button>
      )}
    </div>
  );
}
