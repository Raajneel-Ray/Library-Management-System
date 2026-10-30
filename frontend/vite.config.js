import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Port 3000 matches the backend's CORS list and the Razorpay callback URL
// (razorpay.callback.base-url=http://localhost:3000).
export default defineConfig({
  plugins: [react()],
  server: { port: 3000, strictPort: true },
  preview: { port: 3000, strictPort: true },
});
