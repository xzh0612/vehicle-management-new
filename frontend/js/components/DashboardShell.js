import { defineComponent } from "../vue.js";
import { StatsOverview } from "./StatsOverview.js";
import { VehicleFormCard } from "./VehicleFormCard.js";
import { SearchToolbar } from "./SearchToolbar.js";
import { VehicleTableCard } from "./VehicleTableCard.js";
import { SideInsights } from "./SideInsights.js";

export const DashboardShell = defineComponent({
    name: "DashboardShell",
    components: {
        StatsOverview,
        VehicleFormCard,
        SearchToolbar,
        VehicleTableCard,
        SideInsights
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
    template: `
        <div class="dashboard">
            <section class="dashboard-hero">
                <div>
                    <h2>车辆管理面板</h2>
                    <p>欢迎回来，{{ user.username }}</p>
                </div>
                <div class="dashboard-hero__actions">
                    <span class="role-pill">{{ user.role }}</span>
                    <button type="button" class="secondary" @click="$emit('refresh-dashboard')">刷新</button>
                    <button type="button" class="danger" @click="$emit('logout')">退出</button>
                </div>
            </section>

            <StatsOverview :stats="stats" :overdue-count="overdueCount" />

            <section class="dashboard-grid">
                <div class="dashboard-grid__main">
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
                        @detail="$emit('detail', $event)"
                        @edit="$emit('edit', $event)"
                        @audit="$emit('audit', $event)"
                        @delete="$emit('delete', $event)"
                        @prev-page="$emit('prev-page')"
                        @next-page="$emit('next-page')"
                        @refresh-stats="$emit('refresh-stats')"
                        @upload-stats="$emit('upload-stats')"
                    />
                </div>

                <SideInsights
                    :selected-vehicle="selectedVehicle"
                    :stats="stats"
                    :audit-logs="auditLogs"
                    :admin-create="adminCreate"
                    :user-role="user.role"
                    @create-user="$emit('create-user')"
                />
            </section>
        </div>
    `
});
