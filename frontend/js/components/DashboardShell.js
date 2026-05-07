import { defineComponent } from "../vue.js";
import { StatsOverview } from "./StatsOverview.js";
import { VehicleFormCard } from "./VehicleFormCard.js";
import { SearchToolbar } from "./SearchToolbar.js";
import { VehicleTableCard } from "./VehicleTableCard.js";

const MENU_ITEMS = [
    { id: "overview", label: "总览面板" },
    { id: "vehicles", label: "车辆管理" },
    { id: "detail", label: "车辆详情" },
    { id: "stats", label: "数据统计" },
    { id: "audit", label: "审计日志" },
    { id: "admin", label: "系统管理" },
];

export const DashboardShell = defineComponent({
    name: "DashboardShell",
    components: {
        StatsOverview,
        VehicleFormCard,
        SearchToolbar,
        VehicleTableCard
    },
    props: {
        user: { type: Object, required: true },
        stats: { type: Object, required: true },
        overdueCount: { type: Number, required: true },
        vehicleForm: { type: Object, required: true },
        search: { type: Object, required: true },
        vehicles: { type: Array, required: true },
        pagination: { type: Object, required: true },
        selectedVehicle: { type: Object, default: null },
        auditLogs: { type: Array, required: true },
        adminCreate: { type: Object, required: true },
        editMode: { type: Boolean, default: false },
        statuses: { type: Array, required: true }
    },
    emits: [
        "refresh-dashboard",
        "logout",
        "save-vehicle",
        "cancel-edit",
        "search",
        "reset-search",
        "detail",
        "edit",
        "audit",
        "delete",
        "prev-page",
        "next-page",
        "refresh-stats",
        "upload-stats",
        "create-user"
    ],
    data() {
        return {
            currentView: "overview"
        };
    },
    computed: {
        visibleMenuItems() {
            return MENU_ITEMS.filter(item => item.id !== "admin" || this.user.role === "ADMIN");
        }
    },
    methods: {
        formatMileage(value) {
            return `${Number(value || 0).toLocaleString()} km`;
        },
        formatDateTime(timestamp) {
            return timestamp ? new Date(timestamp).toLocaleString() : "-";
        },
        onDetail(vehicleId) {
            this.currentView = "detail";
            this.$emit("detail", vehicleId);
        },
        onAudit(vehicleId) {
            this.currentView = "audit";
            this.$emit("audit", vehicleId);
        },
        onEdit(vehicleId) {
            this.currentView = "vehicles";
            this.$emit("edit", vehicleId);
        }
    },
    template: `
        <div class="dashboard-layout">
            <aside class="sidebar">
                <div class="sidebar-brand">
                    <strong>车辆管理系统</strong>
                </div>
                <nav class="sidebar-nav">
                    <a v-for="item in visibleMenuItems"
                       :key="item.id"
                       :class="{ active: currentView === item.id }"
                       @click="currentView = item.id"
                       href="#">{{ item.label }}</a>
                </nav>
                <div class="sidebar-footer">
                    <div class="sidebar-user">
                        <span class="role-pill">{{ user.role }}</span>
                        <span class="sidebar-username">{{ user.username }}</span>
                    </div>
                    <button class="secondary sidebar-logout" @click="$emit('logout')">退出登录</button>
                </div>
            </aside>

            <main class="content-area">
                <!-- Overview -->
                <section v-if="currentView === 'overview'" class="content-section">
                    <div class="content-header">
                        <h2>总览面板</h2>
                        <p>欢迎回来，{{ user.username }}</p>
                    </div>
                    <StatsOverview :stats="stats" :overdue-count="overdueCount" />
                </section>

                <!-- Vehicles -->
                <section v-if="currentView === 'vehicles'" class="content-section">
                    <div class="content-header">
                        <h2>车辆管理</h2>
                        <p>车辆档案管理、搜索与筛选</p>
                    </div>
                    <VehicleFormCard
                        :form="vehicleForm"
                        :edit-mode="editMode"
                        :statuses="statuses"
                        @submit="$emit('save-vehicle')"
                        @cancel="$emit('cancel-edit')"
                    />
                    <SearchToolbar
                        :search="search"
                        :statuses="statuses"
                        @search="$emit('search')"
                        @reset="$emit('reset-search')"
                    />
                    <VehicleTableCard
                        :vehicles="vehicles"
                        :pagination="pagination"
                        :user-role="user.role"
                        @detail="onDetail"
                        @edit="onEdit"
                        @audit="onAudit"
                        @delete="$emit('delete', $event)"
                        @prev-page="$emit('prev-page')"
                        @next-page="$emit('next-page')"
                        @refresh-stats="$emit('refresh-stats')"
                        @upload-stats="$emit('upload-stats')"
                    />
                </section>

                <!-- Vehicle Detail -->
                <section v-if="currentView === 'detail'" class="content-section">
                    <div class="content-header">
                        <h2>车辆详情</h2>
                        <p>{{ selectedVehicle ? selectedVehicle.plateNumber : '选择车辆以查看档案' }}</p>
                    </div>
                    <div v-if="selectedVehicle" class="panel">
                        <div class="detail-grid">
                            <div><span>品牌型号</span><strong>{{ selectedVehicle.brand }} {{ selectedVehicle.model }}</strong></div>
                            <div><span>车主</span><strong>{{ selectedVehicle.ownerName }}</strong></div>
                            <div><span>联系电话</span><strong>{{ selectedVehicle.phone }}</strong></div>
                            <div><span>车架号</span><strong>{{ selectedVehicle.vin }}</strong></div>
                            <div><span>发动机号</span><strong>{{ selectedVehicle.engineNumber }}</strong></div>
                            <div><span>登记日期</span><strong>{{ selectedVehicle.registerDate }}</strong></div>
                            <div><span>年检到期</span><strong>{{ selectedVehicle.annualInspectionDate }}</strong></div>
                            <div><span>保险到期</span><strong>{{ selectedVehicle.insuranceExpireDate }}</strong></div>
                            <div><span>里程</span><strong>{{ formatMileage(selectedVehicle.mileage) }}</strong></div>
                            <div><span>创建人</span><strong>{{ selectedVehicle.createdBy || '未知' }}</strong></div>
                            <div class="detail-grid__remark"><span>备注</span><p>{{ selectedVehicle.remark || '暂无备注' }}</p></div>
                        </div>
                    </div>
                    <p v-else class="empty-hint">请先在「车辆管理」中点击「详情」查看车辆完整档案。</p>
                </section>

                <!-- Stats Distribution -->
                <section v-if="currentView === 'stats'" class="content-section">
                    <div class="content-header">
                        <h2>数据统计</h2>
                        <p>车辆分布与运营概览</p>
                    </div>
                    <div class="dashboard-stats-grid">
                        <div class="panel">
                            <div class="panel-head"><h3>按状态</h3></div>
                            <div class="mini-summary">
                                <ul>
                                    <li v-for="(count, key) in (stats.byStatus || {})" :key="key">{{ key }}: {{ count }}</li>
                                </ul>
                            </div>
                        </div>
                        <div class="panel">
                            <div class="panel-head"><h3>按品牌</h3></div>
                            <div class="mini-summary">
                                <ul>
                                    <li v-for="(count, key) in (stats.byBrand || {})" :key="key">{{ key }}: {{ count }}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Audit Logs -->
                <section v-if="currentView === 'audit'" class="content-section">
                    <div class="content-header">
                        <h2>审计日志</h2>
                        <p>车辆操作历史记录</p>
                    </div>
                    <div v-if="auditLogs.length" class="audit-stream">
                        <article v-for="log in auditLogs" :key="log.recordId" class="audit-stream__item">
                            <div class="audit-stream__top">
                                <strong>{{ log.operation }}</strong>
                                <span>{{ log.operator }}</span>
                            </div>
                            <p>{{ log.detail || '无详细说明' }}</p>
                            <time>{{ formatDateTime(log.timestamp) }}</time>
                        </article>
                    </div>
                    <p v-else class="empty-hint">请先在「车辆管理」中点击「审计」查看该车辆的操作历史。</p>
                </section>

                <!-- Admin -->
                <section v-if="currentView === 'admin'" class="content-section">
                    <div class="content-header">
                        <h2>系统管理</h2>
                        <p>创建系统用户</p>
                    </div>
                    <div class="panel panel--soft">
                        <form class="admin-form" @submit.prevent="$emit('create-user')">
                            <input v-model="adminCreate.username" placeholder="用户名">
                            <input v-model="adminCreate.password" type="password" placeholder="密码">
                            <select v-model="adminCreate.role">
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                            <button type="submit">创建用户</button>
                        </form>
                    </div>
                </section>
            </main>
        </div>
    `
});
