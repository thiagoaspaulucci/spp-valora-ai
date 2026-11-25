import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { propertyApi, reportApi, valuationApi } from "../api/client";
import type { Property, Report, Valuation } from "../types";

const brl = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

export function PropertyDetail() {
  const { id = "" } = useParams();
  const [property, setProperty] = useState<Property | null>(null);
  const [valuation, setValuation] = useState<Valuation | null>(null);
  const [report, setReport] = useState<Report | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState<"valuation" | "report" | null>(null);

  useEffect(() => {
    propertyApi
      .get(id)
      .then(setProperty)
      .catch((err) => setError(err instanceof Error ? err.message : "Falha ao carregar"))
      .finally(() => setLoading(false));
  }, [id]);

  async function runValuation() {
    setError(null);
    setBusy("valuation");
    try {
      setValuation(await valuationApi.create(id));
      setReport(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Falha ao avaliar");
    } finally {
      setBusy(null);
    }
  }

  async function runReport() {
    if (!valuation) return;
    setError(null);
    setBusy("report");
    try {
      setReport(await reportApi.create(valuation.id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Falha ao gerar laudo");
    } finally {
      setBusy(null);
    }
  }

  if (loading) return <div className="muted">Carregando…</div>;
  if (!property) return <div className="alert">Imóvel não encontrado.</div>;

  return (
    <div className="detail">
      <div className="page-head">
        <h2>{property.title}</h2>
        <Link to="/" className="btn ghost">
          ← Voltar
        </Link>
      </div>
      {error && <div className="alert">{error}</div>}

      <div className="card">
        <dl className="specs">
          <div>
            <dt>Endereço</dt>
            <dd>
              {property.street}
              {property.number ? `, ${property.number}` : ""} — {property.city}/{property.state}
            </dd>
          </div>
          <div>
            <dt>Área</dt>
            <dd>{property.areaSqm} m²</dd>
          </div>
          <div>
            <dt>Cômodos</dt>
            <dd>
              {property.bedrooms} quarto(s) · {property.bathrooms} banheiro(s) ·{" "}
              {property.parkingSpots} vaga(s)
            </dd>
          </div>
          {property.yearBuilt && (
            <div>
              <dt>Ano</dt>
              <dd>{property.yearBuilt}</dd>
            </div>
          )}
        </dl>
      </div>

      <div className="card">
        <div className="card-head">
          <h3>Avaliação</h3>
          <button className="btn primary" onClick={runValuation} disabled={busy !== null}>
            {busy === "valuation" ? "Avaliando…" : valuation ? "Reavaliar" : "Avaliar imóvel"}
          </button>
        </div>
        {valuation ? (
          <div className="result">
            <div className="big-value">{brl.format(valuation.estimatedValue)}</div>
            <p className="muted">
              {brl.format(valuation.pricePerSqm)}/m² · método {valuation.method}
            </p>
            <button className="btn secondary" onClick={runReport} disabled={busy !== null}>
              {busy === "report" ? "Gerando…" : "Gerar laudo"}
            </button>
          </div>
        ) : (
          <p className="muted">Clique em “Avaliar imóvel” para estimar o valor.</p>
        )}
      </div>

      {report && (
        <div className="card">
          <div className="card-head">
            <h3>{report.title}</h3>
          </div>
          <pre className="summary">{report.summary}</pre>
        </div>
      )}
    </div>
  );
}
