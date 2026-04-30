import { computed, createApp, reactive } from "./vue.js";
import { api } from "./api.js";
import { API_BASE, saveApiBase } from "./config.js";
import {
    clearSession,
    createEmptySearchForm,
    createEmptyVehicleForm,
    createInitialState,
    getSelectedVehicle,
    saveSession
} from "./store.js";
import { AuthPanel } from "./components/AuthPanel.js";
import { DashboardShell } from "./components/DashboardShell.js";
import { ToastMessage } from "./components/ToastMessage.js";

const STATUS_OPTIONS = ["ACTIVE", "MAINTENANCE", "SCRAPPED", "TRANSFERRED"];

const app = createApp({
    components: {
        AuthPanel,
        DashboardShell,
        ToastMessage
    },
    setup() {
        const state = reactive(createInitialState(API_BASE));
        state.healthStatus = "unknown";

        const selectedVehicle = computed(() => getSelectedVehicle(state));
        const overdueCount = computed(() =>
            (state.data.stats.inspectionReminders?.overdue || 0) +
            (state.data.stats.insuranceReminders?.overdue || 0)
        );
        const hasSession = computed(() => !!state.session.token);

        function showMessage(message, type = "info") {
            state.ui.message = message;
            state.ui.messageType = type;
        }

        function resetSession() {
            clearSession();
            state.session.token = "";
            state.session.user = { username: "", role: "" };
            state.data.vehicles = [];
            state.data.stats = {};
            state.data.auditLogs = [];
            state.data.selectedVehicleId = "";
            state.pagination = { page: 1, size: 10, total: 0, totalPages: 1 };
            state.editMode = false;
            state.editingId = "";
            state.forms.vehicle = createEmptyVehicleForm();
        }

        function handleAuthFailure(error) {
            resetSession();
            showMessage(error.message || "登录态失效，请重新登录", "error");
        }

        function handleError(error, fallbackMessage) {
            const message = error?.message || fallbackMessage;
            showMessage(message, "error");
            if (error?.status === 401) {
                handleAuthFailure(error);
            }
        }

        async function checkHealth() {
            state.healthStatus = "checking";
            try {
                await api.health(state.config.apiBase);
                state.healthStatus = "up";
                showMessage("API 连通正常");
            } catch (error) {
                state.healthStatus = "down";
                handleError(error, "API 连接失败");
            }
        }

        async function saveApiBaseValue(value) {
            const normalized = saveApiBase(value);
            state.config.apiBase = normalized;
            showMessage(`API 地址已更新为 ${normalized}`);
            await checkHealth();
            if (state.session.token) {
                await bootstrap();
            }
        }

        async function login() {
            try {
                const payload = {
                    username: state.forms.login.username.trim(),
                    password: state.forms.login.password
                };
                const data = await api.login(state.config.apiBase, payload);
                state.session.token = data.token;
                state.session.user = {
                    username: data.username || payload.username,
                    role: data.role || ""
                };
                saveSession(data.token, state.session.user);
                showMessage(`登录成功，角色：${data.role}`);
                await bootstrap();
            } catch (error) {
                handleError(error, "登录失败");
            }
        }

        async function register() {
            try {
                const payload = {
                    username: state.forms.register.username.trim(),
                    password: state.forms.register.password
                };
                await api.register(state.config.apiBase, payload);
                showMessage("注册成功，请登录");
            } catch (error) {
                handleError(error, "注册失败");
            }
        }

        async function bootstrap() {
            try {
                const me = await api.me(state.config.apiBase, state.session.token);
                state.session.user = me || state.session.user;
                saveSession(state.session.token, state.session.user);
            } catch (error) {
                handleAuthFailure(error);
                return;
            }

            await loadDashboard();
        }

        async function loadVehicles() {
            const params = new URLSearchParams();
            const search = state.forms.search;
            Object.entries({
                keyword: search.keyword,
                brand: search.brand,
                status: search.status,
                ownerName: search.ownerName,
                page: state.pagination.page,
                size: state.pagination.size,
                sortBy: search.sortBy,
                sortDir: search.sortDir
            }).forEach(([key, value]) => {
                if (value !== "" && value !== null && value !== undefined) {
                    params.set(key, value);
                }
            });

            const data = await api.vehicles(state.config.apiBase, state.session.token, params.toString());
            state.data.vehicles = data.items || [];
            state.pagination = {
                page: data.page || 1,
                size: data.size || 10,
                total: data.total || 0,
                totalPages: data.totalPages || 1
            };
            if (!state.data.selectedVehicleId && state.data.vehicles.length) {
                state.data.selectedVehicleId = state.data.vehicles[0].vehicleId;
            }
            if (state.data.selectedVehicleId && !getSelectedVehicle(state) && state.data.vehicles.length) {
                state.data.selectedVehicleId = state.data.vehicles[0].vehicleId;
            }
        }

        async function loadStats() {
            state.data.stats = await api.stats(state.config.apiBase, state.session.token);
        }

        async function loadDashboard() {
            const results = await Promise.allSettled([loadVehicles(), loadStats()]);
            const rejected = results.find(result => result.status === "rejected");
            if (rejected) {
                handleError(rejected.reason, "部分数据加载失败");
            }
        }

        async function saveVehicle() {
            try {
                const payload = {
                    ...state.forms.vehicle,
                    plateNumber: state.forms.vehicle.plateNumber.trim(),
                    vin: state.forms.vehicle.vin.trim(),
                    engineNumber: state.forms.vehicle.engineNumber.trim(),
                    brand: state.forms.vehicle.brand.trim(),
                    model: state.forms.vehicle.model.trim(),
                    ownerName: state.forms.vehicle.ownerName.trim(),
                    phone: state.forms.vehicle.phone.trim(),
                    registerDate: state.forms.vehicle.registerDate.trim(),
                    annualInspectionDate: state.forms.vehicle.annualInspectionDate.trim(),
                    insuranceExpireDate: state.forms.vehicle.insuranceExpireDate.trim(),
                    remark: state.forms.vehicle.remark.trim(),
                    mileage: Number(state.forms.vehicle.mileage || 0)
                };
                await api.saveVehicle(
                    state.config.apiBase,
                    state.session.token,
                    state.editMode ? state.editingId : "",
                    payload
                );
                showMessage(state.editMode ? "修改成功" : "新增成功");
                cancelEdit();
                state.pagination.page = 1;
                await loadDashboard();
            } catch (error) {
                handleError(error, "保存车辆失败");
            }
        }

        function beginEdit(vehicleId) {
            const vehicle = state.data.vehicles.find(item => item.vehicleId === vehicleId);
            if (!vehicle) {
                return;
            }
            state.editMode = true;
            state.editingId = vehicleId;
            state.data.selectedVehicleId = vehicleId;
            state.forms.vehicle = {
                plateNumber: vehicle.plateNumber || "",
                vin: vehicle.vin || "",
                engineNumber: vehicle.engineNumber || "",
                brand: vehicle.brand || "",
                model: vehicle.model || "",
                ownerName: vehicle.ownerName || "",
                phone: vehicle.phone || "",
                status: vehicle.status || "ACTIVE",
                registerDate: vehicle.registerDate || "",
                annualInspectionDate: vehicle.annualInspectionDate || "",
                insuranceExpireDate: vehicle.insuranceExpireDate || "",
                mileage: vehicle.mileage || 0,
                remark: vehicle.remark || ""
            };
        }

        function cancelEdit() {
            state.editMode = false;
            state.editingId = "";
            state.forms.vehicle = createEmptyVehicleForm();
        }

        async function applySearch() {
            state.pagination.page = 1;
            await loadDashboard();
        }

        async function resetSearch() {
            state.forms.search = createEmptySearchForm();
            state.pagination.page = 1;
            await loadDashboard();
        }

        async function loadAudit(vehicleId) {
            try {
                state.data.selectedVehicleId = vehicleId;
                state.data.auditLogs = await api.audit(state.config.apiBase, state.session.token, vehicleId);
            } catch (error) {
                handleError(error, "加载审计日志失败");
            }
        }

        async function deleteVehicle(vehicleId) {
            try {
                await api.deleteVehicle(state.config.apiBase, state.session.token, vehicleId);
                showMessage("删除成功");
                if (state.data.selectedVehicleId === vehicleId) {
                    state.data.selectedVehicleId = "";
                }
                await loadDashboard();
            } catch (error) {
                handleError(error, "删除车辆失败");
            }
        }

        async function uploadStats() {
            try {
                const data = await api.uploadStats(state.config.apiBase, state.session.token);
                showMessage(`统计已上传到 ${data.hdfsPath}`);
            } catch (error) {
                handleError(error, "上传统计失败");
            }
        }

        async function createUser() {
            try {
                const payload = {
                    username: state.forms.adminCreate.username.trim(),
                    password: state.forms.adminCreate.password,
                    role: state.forms.adminCreate.role
                };
                await api.createUser(state.config.apiBase, state.session.token, payload);
                state.forms.adminCreate = { username: "", password: "", role: "USER" };
                showMessage("创建用户成功");
            } catch (error) {
                handleError(error, "创建用户失败");
            }
        }

        async function changePage(step) {
            const page = state.pagination.page + step;
            if (page < 1 || page > (state.pagination.totalPages || 1)) {
                return;
            }
            state.pagination.page = page;
            await loadDashboard();
        }

        async function logout() {
            try {
                if (state.session.token) {
                    await api.logout(state.config.apiBase, state.session.token);
                }
            } catch (error) {
                handleError(error, "退出登录失败");
            } finally {
                resetSession();
                showMessage("已退出登录");
            }
        }

        checkHealth();
        if (state.session.token) {
            bootstrap();
        }

        return {
            state,
            STATUS_OPTIONS,
            selectedVehicle,
            overdueCount,
            hasSession,
            saveApiBaseValue,
            checkHealth,
            login,
            register,
            loadDashboard,
            saveVehicle,
            beginEdit,
            cancelEdit,
            applySearch,
            resetSearch,
            loadAudit,
            deleteVehicle,
            uploadStats,
            createUser,
            changePage,
            logout
        };
    },
    template: `
        <main class="app-shell">
            <AuthPanel
                v-if="!hasSession"
                :api-base="state.config.apiBase"
                :health-status="state.healthStatus"
                :login-form="state.forms.login"
                :register-form="state.forms.register"
                @save-api="saveApiBaseValue"
                @check-health="checkHealth"
                @login="login"
                @register="register"
            />

            <DashboardShell
                v-else
                :user="state.session.user"
                :stats="state.data.stats"
                :overdue-count="overdueCount"
                :vehicle-form="state.forms.vehicle"
                :search="state.forms.search"
                :vehicles="state.data.vehicles"
                :pagination="state.pagination"
                :selected-vehicle="selectedVehicle"
                :audit-logs="state.data.auditLogs"
                :admin-create="state.forms.adminCreate"
                :edit-mode="state.editMode"
                :statuses="STATUS_OPTIONS"
                @refresh-dashboard="loadDashboard"
                @logout="logout"
                @save-vehicle="saveVehicle"
                @cancel-edit="cancelEdit"
                @search="applySearch"
                @reset-search="resetSearch"
                @detail="state.data.selectedVehicleId = $event"
                @edit="beginEdit"
                @audit="loadAudit"
                @delete="deleteVehicle"
                @prev-page="changePage(-1)"
                @next-page="changePage(1)"
                @refresh-stats="loadDashboard"
                @upload-stats="uploadStats"
                @create-user="createUser"
            />

            <ToastMessage :message="state.ui.message" :type="state.ui.messageType" @close="state.ui.message = ''" />
        </main>
    `
});

try {
    app.mount("#app");
} catch (error) {
    console.error("Vue app mount failed", error);
    const root = document.querySelector("#app");
    if (root) {
        root.innerHTML = `
            <section class="auth-card" style="max-width: 720px; margin: 6rem auto;">
                <p class="eyebrow">Startup Error</p>
                <h2>前端启动失败</h2>
                <p>请检查组件模板或浏览器控制台错误信息。</p>
                <pre style="white-space: pre-wrap; overflow: auto;">${error?.stack || error?.message || String(error)}</pre>
            </section>
        `;
    }
}
