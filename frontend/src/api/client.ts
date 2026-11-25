import type {
  Page,
  Property,
  PropertyType,
  Report,
  TokenResponse,
  UserResponse,
  Valuation,
} from "../types";

const BASE = import.meta.env.VITE_API_BASE ?? "";
const ACCESS_KEY = "spp.accessToken";
const REFRESH_KEY = "spp.refreshToken";

export const tokenStore = {
  get access() {
    return localStorage.getItem(ACCESS_KEY);
  },
  get refresh() {
    return localStorage.getItem(REFRESH_KEY);
  },
  set(tokens: TokenResponse) {
    localStorage.setItem(ACCESS_KEY, tokens.accessToken);
    localStorage.setItem(REFRESH_KEY, tokens.refreshToken);
  },
  clear() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
  },
};

export class ApiException extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function parseError(res: Response): Promise<string> {
  try {
    const body = await res.json();
    return body.message ?? body.error ?? res.statusText;
  } catch {
    return res.statusText || `Erro ${res.status}`;
  }
}

async function tryRefresh(): Promise<boolean> {
  const refreshToken = tokenStore.refresh;
  if (!refreshToken) return false;
  const res = await fetch(`${BASE}/api/v1/auth/refresh`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
  });
  if (!res.ok) {
    tokenStore.clear();
    return false;
  }
  tokenStore.set((await res.json()) as TokenResponse);
  return true;
}

async function request<T>(
  method: string,
  path: string,
  body?: unknown,
  retry = true,
): Promise<T> {
  const headers: Record<string, string> = {};
  if (body !== undefined) headers["Content-Type"] = "application/json";
  const access = tokenStore.access;
  if (access) headers["Authorization"] = `Bearer ${access}`;

  const res = await fetch(`${BASE}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401 && retry && (await tryRefresh())) {
    return request<T>(method, path, body, false);
  }
  if (!res.ok) {
    throw new ApiException(res.status, await parseError(res));
  }
  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}

// --- Autenticação ---
export const authApi = {
  login: (email: string, password: string) =>
    request<TokenResponse>("POST", "/api/v1/auth/login", { email, password }),
  register: (name: string, email: string, password: string) =>
    request<TokenResponse>("POST", "/api/v1/auth/register", { name, email, password }),
  me: () => request<UserResponse>("GET", "/api/v1/auth/me"),
  logout: (refreshToken: string) =>
    request<void>("POST", "/api/v1/auth/logout", { refreshToken }),
};

export interface PropertyInput {
  title: string;
  type: PropertyType;
  street: string;
  number?: string;
  complement?: string;
  neighborhood?: string;
  city: string;
  state: string;
  zipCode: string;
  areaSqm: number;
  bedrooms: number;
  bathrooms: number;
  parkingSpots: number;
  yearBuilt?: number;
  askingPrice?: number;
}

// --- Recursos de domínio ---
export const propertyApi = {
  list: () => request<Page<Property>>("GET", "/api/v1/properties?size=50&sort=createdAt,desc"),
  get: (id: string) => request<Property>("GET", `/api/v1/properties/${id}`),
  create: (input: PropertyInput) => request<Property>("POST", "/api/v1/properties", input),
};

export const valuationApi = {
  create: (propertyId: string) =>
    request<Valuation>("POST", "/api/v1/valuations", { propertyId }),
  listByProperty: () => request<Page<Valuation>>("GET", "/api/v1/valuations?size=50"),
};

export const reportApi = {
  create: (valuationId: string) =>
    request<Report>("POST", "/api/v1/reports", { valuationId }),
};
