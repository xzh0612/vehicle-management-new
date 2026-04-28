function buildUrl(baseUrl, path) {
    return `${baseUrl}${path}`;
}

export async function apiRequest(baseUrl, path, options = {}) {
    const response = await fetch(buildUrl(baseUrl, path), options);
    const rawText = await response.text();
    let payload = {};
    try {
        payload = rawText ? JSON.parse(rawText) : {};
    } catch (error) {
        payload = {};
    }

    if (!response.ok || payload.success === false) {
        const fallbackMessage = response.status >= 500
                ? `服务器内部错误 (${response.status})，请检查后端日志、Redis 与 HBase 连接`
                : `请求失败 (${response.status})`;
        const error = new Error(payload.message || rawText || fallbackMessage);
        error.status = response.status;
        error.payload = payload;
        throw error;
    }

    return payload.data;
}

export const api = {
    health(baseUrl) {
        return apiRequest(baseUrl, "/health");
    },
    login(baseUrl, body) {
        return apiRequest(baseUrl, "/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });
    },
    register(baseUrl, body) {
        return apiRequest(baseUrl, "/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });
    },
    me(baseUrl, token) {
        return apiRequest(baseUrl, "/auth/me", { headers: authHeaders(token) });
    },
    logout(baseUrl, token) {
        return apiRequest(baseUrl, "/auth/logout", {
            method: "POST",
            headers: authHeaders(token)
        });
    },
    vehicles(baseUrl, token, queryString) {
        return apiRequest(baseUrl, `/vehicles${queryString ? `?${queryString}` : ""}`, {
            headers: authHeaders(token)
        });
    },
    saveVehicle(baseUrl, token, vehicleId, body) {
        return apiRequest(baseUrl, vehicleId ? `/vehicles/${vehicleId}` : "/vehicles", {
            method: vehicleId ? "PUT" : "POST",
            headers: authHeaders(token),
            body: JSON.stringify(body)
        });
    },
    deleteVehicle(baseUrl, token, vehicleId) {
        return apiRequest(baseUrl, `/vehicles/${vehicleId}`, {
            method: "DELETE",
            headers: authHeaders(token)
        });
    },
    audit(baseUrl, token, vehicleId) {
        return apiRequest(baseUrl, `/vehicles/${vehicleId}/audit`, {
            headers: authHeaders(token)
        });
    },
    stats(baseUrl, token) {
        return apiRequest(baseUrl, "/stats", { headers: authHeaders(token) });
    },
    uploadStats(baseUrl, token) {
        return apiRequest(baseUrl, "/stats/upload", {
            method: "POST",
            headers: authHeaders(token)
        });
    },
    createUser(baseUrl, token, body) {
        return apiRequest(baseUrl, "/auth/register/admin", {
            method: "POST",
            headers: authHeaders(token),
            body: JSON.stringify(body)
        });
    }
};

function authHeaders(token) {
    return {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
    };
}
