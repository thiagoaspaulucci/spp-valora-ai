import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Em dev, as chamadas /api e /oauth2 são encaminhadas ao gateway (8090).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      "/api": { target: "http://localhost:8090", changeOrigin: true },
      "/oauth2": { target: "http://localhost:8090", changeOrigin: true },
    },
  },
});
