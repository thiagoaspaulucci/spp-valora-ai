import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { propertyApi, type PropertyInput } from "../api/client";
import type { PropertyType } from "../types";

const EMPTY: PropertyInput = {
  title: "",
  type: "APARTMENT",
  street: "",
  number: "",
  neighborhood: "",
  city: "",
  state: "",
  zipCode: "",
  areaSqm: 0,
  bedrooms: 0,
  bathrooms: 0,
  parkingSpots: 0,
};

export function PropertyForm() {
  const navigate = useNavigate();
  const [form, setForm] = useState<PropertyInput>(EMPTY);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  function update<K extends keyof PropertyInput>(key: K, value: PropertyInput[K]) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      const created = await propertyApi.create(form);
      navigate(`/properties/${created.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Falha ao salvar");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div>
      <div className="page-head">
        <h2>Novo imóvel</h2>
      </div>
      {error && <div className="alert">{error}</div>}
      <form className="card form" onSubmit={onSubmit}>
        <label className="full">
          Título
          <input value={form.title} onChange={(e) => update("title", e.target.value)} required />
        </label>
        <label>
          Tipo
          <select value={form.type} onChange={(e) => update("type", e.target.value as PropertyType)}>
            <option value="APARTMENT">Apartamento</option>
            <option value="HOUSE">Casa</option>
            <option value="COMMERCIAL">Comercial</option>
            <option value="LAND">Terreno</option>
          </select>
        </label>
        <label>
          Área (m²)
          <input
            type="number"
            step="0.01"
            value={form.areaSqm || ""}
            onChange={(e) => update("areaSqm", Number(e.target.value))}
            required
          />
        </label>
        <label className="full">
          Rua
          <input value={form.street} onChange={(e) => update("street", e.target.value)} required />
        </label>
        <label>
          Número
          <input value={form.number} onChange={(e) => update("number", e.target.value)} />
        </label>
        <label>
          Bairro
          <input
            value={form.neighborhood}
            onChange={(e) => update("neighborhood", e.target.value)}
          />
        </label>
        <label>
          Cidade
          <input value={form.city} onChange={(e) => update("city", e.target.value)} required />
        </label>
        <label>
          UF
          <input
            value={form.state}
            maxLength={2}
            onChange={(e) => update("state", e.target.value.toUpperCase())}
            required
          />
        </label>
        <label>
          CEP
          <input value={form.zipCode} onChange={(e) => update("zipCode", e.target.value)} required />
        </label>
        <label>
          Quartos
          <input
            type="number"
            value={form.bedrooms}
            onChange={(e) => update("bedrooms", Number(e.target.value))}
          />
        </label>
        <label>
          Banheiros
          <input
            type="number"
            value={form.bathrooms}
            onChange={(e) => update("bathrooms", Number(e.target.value))}
          />
        </label>
        <label>
          Vagas
          <input
            type="number"
            value={form.parkingSpots}
            onChange={(e) => update("parkingSpots", Number(e.target.value))}
          />
        </label>
        <label>
          Ano de construção
          <input
            type="number"
            value={form.yearBuilt ?? ""}
            onChange={(e) =>
              update("yearBuilt", e.target.value ? Number(e.target.value) : undefined)
            }
          />
        </label>
        <div className="full actions">
          <button type="button" className="btn ghost" onClick={() => navigate("/")}>
            Cancelar
          </button>
          <button className="btn primary" disabled={busy}>
            {busy ? "Salvando…" : "Salvar imóvel"}
          </button>
        </div>
      </form>
    </div>
  );
}
