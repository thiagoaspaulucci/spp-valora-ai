import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { propertyApi } from "../api/client";
import type { Property } from "../types";

const TYPE_LABEL: Record<string, string> = {
  APARTMENT: "Apartamento",
  HOUSE: "Casa",
  COMMERCIAL: "Comercial",
  LAND: "Terreno",
};

export function Properties() {
  const [items, setItems] = useState<Property[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    propertyApi
      .list()
      .then((page) => setItems(page.content))
      .catch((err) => setError(err instanceof Error ? err.message : "Falha ao carregar"))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="muted">Carregando imóveis…</div>;

  return (
    <div>
      <div className="page-head">
        <h2>Meus imóveis</h2>
        <Link to="/properties/new" className="btn primary">
          + Novo imóvel
        </Link>
      </div>
      {error && <div className="alert">{error}</div>}
      {items.length === 0 ? (
        <div className="card empty">
          Nenhum imóvel ainda. <Link to="/properties/new">Cadastre o primeiro</Link>.
        </div>
      ) : (
        <div className="grid">
          {items.map((p) => (
            <Link to={`/properties/${p.id}`} key={p.id} className="card property">
              <div className="tag">{TYPE_LABEL[p.type] ?? p.type}</div>
              <h3>{p.title}</h3>
              <p className="muted">
                {p.city}/{p.state} · {p.areaSqm} m²
              </p>
              <p className="muted small">
                {p.bedrooms} quarto(s) · {p.bathrooms} banheiro(s) · {p.parkingSpots} vaga(s)
              </p>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
