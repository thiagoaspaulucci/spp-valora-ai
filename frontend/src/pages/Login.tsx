import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      await login(email, password);
      navigate("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Falha no login");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="center">
      <form className="card auth-card" onSubmit={onSubmit}>
        <h1 className="brand lg">
          SPP <span>Valora</span> AI
        </h1>
        <p className="muted">Entre para avaliar seus imóveis.</p>
        {error && <div className="alert">{error}</div>}
        <label>
          E-mail
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Senha
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <button className="btn primary" disabled={busy}>
          {busy ? "Entrando…" : "Entrar"}
        </button>
        <p className="muted small">
          Não tem conta? <Link to="/register">Criar conta</Link>
        </p>
      </form>
    </div>
  );
}
