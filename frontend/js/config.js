const API_BASE_KEY = "vehicle_management_api_base";

function normalizeApiBase(value) {
    const normalized = (value || "").trim().replace(/\/+$/, "");
    if (!normalized) {
        return "";
    }
    if (/^https?:\/\/[^/]+$/i.test(normalized)) {
        return `${normalized}/api`;
    }
    return normalized;
}

function inferDefaultApiBase() {
    if (window.location.port === "8080") {
        return `${window.location.origin}/api`;
    }
    return "http://localhost:8080/api";
}

const params = new URLSearchParams(window.location.search);
const queryApiBase = normalizeApiBase(params.get("apiBase"));
const storedApiBase = normalizeApiBase(window.localStorage.getItem(API_BASE_KEY));

export const DEFAULT_API_BASE = inferDefaultApiBase();
export const API_BASE = queryApiBase || storedApiBase || DEFAULT_API_BASE;

export function saveApiBase(value) {
    const normalized = normalizeApiBase(value);
    const finalValue = normalized || DEFAULT_API_BASE;
    window.localStorage.setItem(API_BASE_KEY, finalValue);
    return finalValue;
}

export function getApiBase() {
    return normalizeApiBase(window.localStorage.getItem(API_BASE_KEY)) || API_BASE;
}
