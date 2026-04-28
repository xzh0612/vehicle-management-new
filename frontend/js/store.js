const TOKEN_KEY = "vehicle_management_token";
const USER_KEY = "vehicle_management_user";

export function createEmptyVehicleForm() {
    return {
        plateNumber: "",
        vin: "",
        engineNumber: "",
        brand: "",
        model: "",
        ownerName: "",
        phone: "",
        status: "ACTIVE",
        registerDate: "",
        annualInspectionDate: "",
        insuranceExpireDate: "",
        mileage: 0,
        remark: ""
    };
}

export function createEmptySearchForm() {
    return {
        keyword: "",
        brand: "",
        status: "",
        ownerName: "",
        sortBy: "updatedAt",
        sortDir: "desc"
    };
}

export function createInitialState(apiBase) {
    return {
        config: {
            apiBase
        },
        session: {
            token: window.localStorage.getItem(TOKEN_KEY) || "",
            user: readStoredUser()
        },
        ui: {
            busy: false,
            message: "",
            messageType: "info"
        },
        data: {
            vehicles: [],
            stats: {},
            auditLogs: [],
            selectedVehicleId: ""
        },
        forms: {
            login: { username: "", password: "" },
            register: { username: "", password: "" },
            adminCreate: { username: "", password: "", role: "USER" },
            vehicle: createEmptyVehicleForm(),
            search: createEmptySearchForm()
        },
        pagination: {
            page: 1,
            size: 10,
            total: 0,
            totalPages: 1
        },
        editMode: false,
        editingId: ""
    };
}

export function saveSession(token, user) {
    window.localStorage.setItem(TOKEN_KEY, token);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user || {}));
}

export function clearSession() {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
}

export function getSelectedVehicle(state) {
    return state.data.vehicles.find(vehicle => vehicle.vehicleId === state.data.selectedVehicleId) || null;
}

function readStoredUser() {
    try {
        const raw = window.localStorage.getItem(USER_KEY);
        return raw ? JSON.parse(raw) : { username: "", role: "" };
    } catch (error) {
        return { username: "", role: "" };
    }
}
