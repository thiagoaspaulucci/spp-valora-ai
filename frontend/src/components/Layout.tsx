import { Link, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  return (
    <div className="app">
      <header className="topbar">
        <Link to="/" className="brand">
          SPP <span>Valora</span> AI
        </Link>
        <nav>
          <Link to="/">Imóveis</Link>
          <Link to="/properties/new">Novo imóvel</Link>
        </nav>
        <div className="spacer" />
        {user && (
          <div className="user">
            <span className="muted">{user.email}</span>
            <button className="btn ghost" onClick={handleLogout}>
              Sair
            </button>
          </div>
        )}
      </header>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
