const { createApp } = Vue;

createApp({
    data() {
        return {
            token: localStorage.getItem("token") || "",
            me: { username: "", role: "" },
            message: "",
            vehicles: [],
            stats: {},
            auditLogs: [],
            editMode: false,
            editingId: "",
            loginForm: { username: "", password: "" },
            registerForm: { username: "", password: "" },
            adminCreate: { username: "", password: "", role: "USER" },
            vehicleForm: {
                plateNumber: "",
                brand: "",
                model: "",
                ownerName: "",
                phone: "",
                status: "ACTIVE"
            },
            search: { brand: "", status: "" }
        };
    },
    mounted() {
        if (this.token) {
            this.bootstrap();
        }
    },
    methods: {
        headers() {
            return {
                "Content-Type": "application/json",
                Authorization: `Bearer ${this.token}`
            };
        },
        async request(url, options = {}) {
            const resp = await fetch(url, options);
            const data = await resp.json().catch(() => ({}));
            if (!resp.ok) {
                throw new Error(data.message || "请求失败");
            }
            return data;
        },
        async login() {
            try {
                const data = await this.request("/api/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.loginForm)
                });
                this.token = data.token;
                localStorage.setItem("token", data.token);
                await this.bootstrap();
                this.message = `登录成功，角色：${data.role}`;
            } catch (e) {
                this.message = e.message;
            }
        },
        async register() {
            try {
                await this.request("/api/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.registerForm)
                });
                this.message = "注册成功，请登录";
            } catch (e) {
                this.message = e.message;
            }
        },
        async bootstrap() {
            try {
                this.me = await this.request("/api/auth/me", { headers: this.headers() });
                await this.loadVehicles();
                await this.loadStats();
            } catch (e) {
                this.message = e.message;
                this.logoutLocal();
            }
        },
        logoutLocal() {
            this.token = "";
            this.me = { username: "", role: "" };
            this.vehicles = [];
            this.stats = {};
            this.auditLogs = [];
            localStorage.removeItem("token");
        },
        async logout() {
            try {
                await this.request("/api/auth/logout", {
                    method: "POST",
                    headers: this.headers()
                });
            } catch (e) {
                this.message = e.message;
            } finally {
                this.logoutLocal();
            }
        },
        async loadVehicles() {
            try {
                const params = new URLSearchParams();
                if (this.search.brand) params.set("brand", this.search.brand);
                if (this.search.status) params.set("status", this.search.status);
                const suffix = params.toString() ? `?${params}` : "";
                this.vehicles = await this.request(`/api/vehicles${suffix}`, { headers: this.headers() });
            } catch (e) {
                this.message = e.message;
            }
        },
        async saveVehicle() {
            try {
                if (this.editMode) {
                    await this.request(`/api/vehicles/${this.editingId}`, {
                        method: "PUT",
                        headers: this.headers(),
                        body: JSON.stringify(this.vehicleForm)
                    });
                    this.message = "修改成功";
                } else {
                    await this.request("/api/vehicles", {
                        method: "POST",
                        headers: this.headers(),
                        body: JSON.stringify(this.vehicleForm)
                    });
                    this.message = "新增成功";
                }
                this.resetForm();
                await this.loadVehicles();
                await this.loadStats();
            } catch (e) {
                this.message = e.message;
            }
        },
        beginEdit(v) {
            this.editMode = true;
            this.editingId = v.vehicleId;
            this.vehicleForm = {
                plateNumber: v.plateNumber,
                brand: v.brand,
                model: v.model,
                ownerName: v.ownerName,
                phone: v.phone,
                status: v.status
            };
        },
        resetForm() {
            this.editMode = false;
            this.editingId = "";
            this.vehicleForm = {
                plateNumber: "",
                brand: "",
                model: "",
                ownerName: "",
                phone: "",
                status: "ACTIVE"
            };
        },
        async deleteVehicle(vehicleId) {
            try {
                await this.request(`/api/vehicles/${vehicleId}`, {
                    method: "DELETE",
                    headers: this.headers()
                });
                this.message = "删除成功";
                await this.loadVehicles();
                await this.loadStats();
            } catch (e) {
                this.message = e.message;
            }
        },
        async loadStats() {
            try {
                this.stats = await this.request("/api/stats", { headers: this.headers() });
            } catch (e) {
                this.message = e.message;
            }
        },
        async uploadStats() {
            try {
                const data = await this.request("/api/stats/upload", {
                    method: "POST",
                    headers: this.headers()
                });
                this.message = `已上传到 ${data.hdfsPath}`;
            } catch (e) {
                this.message = e.message;
            }
        },
        async loadAudit(vehicleId) {
            try {
                this.auditLogs = await this.request(`/api/vehicles/${vehicleId}/audit`, { headers: this.headers() });
            } catch (e) {
                this.message = e.message;
            }
        },
        async createUserByAdmin() {
            try {
                await this.request("/api/auth/register/admin", {
                    method: "POST",
                    headers: this.headers(),
                    body: JSON.stringify(this.adminCreate)
                });
                this.message = "创建用户成功";
            } catch (e) {
                this.message = e.message;
            }
        },
        resetSearch() {
            this.search = { brand: "", status: "" };
            this.loadVehicles();
        },
        pretty(obj) {
            return JSON.stringify(obj, null, 2);
        }
    }
}).mount("#app");
