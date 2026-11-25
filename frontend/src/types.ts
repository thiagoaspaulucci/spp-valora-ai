export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserResponse {
  id: string;
  name: string;
  email: string;
  roles: string[];
  enabled: boolean;
}

export type PropertyType = "APARTMENT" | "HOUSE" | "COMMERCIAL" | "LAND";

export interface Property {
  id: string;
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
  status: string;
  ownerSubject: string;
  createdAt: string;
  updatedAt?: string;
}

export interface Valuation {
  id: string;
  propertyId: string;
  estimatedValue: number;
  pricePerSqm: number;
  areaSqm: number;
  propertyType: string;
  city: string;
  state: string;
  method: string;
  status: string;
  createdAt: string;
}

export interface Report {
  id: string;
  valuationId: string;
  propertyId: string;
  title: string;
  estimatedValue: number;
  summary: string;
  status: string;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path?: string;
}
