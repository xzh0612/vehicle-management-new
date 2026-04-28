import { defineComponent } from "../vue.js";

export const VehicleTableCard = defineComponent({
    name: "VehicleTableCard",
    props: {
        vehicles: { type: Array, required: true },
        pagination: { type: Object, required: true },
        userRole: { type: String, default: "" }
    },
    emits: ["detail", "edit", "audit", "delete", "prev-page", "next-page", "refresh-stats", "upload-stats"],
    methods: {
        formatMileage(value) {
            return `${Number(value || 0).toLocaleString()} km`;
        }
    },
    template: `
        <section class="panel panel--table">
            <div class="panel-head">
                <div>
                    <p class="eyebrow">Fleet Table</p>
                    <h3>车辆列表</h3>
                </div>
                <div class="panel-actions">
                    <button type="button" class="secondary" @click="$emit('refresh-stats')">刷新统计</button>
                    <button type="button" @click="$emit('upload-stats')" :disabled="userRole !== 'ADMIN'">上传到 HDFS</button>
                </div>
            </div>

            <div class="table-shell">
                <table class="vehicle-table">
                    <thead>
                        <tr>
                            <th>车牌 / VIN</th>
                            <th>品牌型号</th>
                            <th>车主</th>
                            <th>状态</th>
                            <th>里程</th>
                            <th>保险到期</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="!vehicles.length">
                            <td colspan="7" class="empty-row">暂无数据，请先新增车辆或调整筛选条件。</td>
                        </tr>
                        <tr v-for="vehicle in vehicles" :key="vehicle.vehicleId">
                            <td>
                                <strong>{{ vehicle.plateNumber }}</strong>
                                <small>{{ vehicle.vin }}</small>
                            </td>
                            <td>{{ vehicle.brand }} {{ vehicle.model }}</td>
                            <td>{{ vehicle.ownerName }}</td>
                            <td><span class="status-pill">{{ vehicle.status }}</span></td>
                            <td>{{ formatMileage(vehicle.mileage) }}</td>
                            <td>{{ vehicle.insuranceExpireDate || '-' }}</td>
                            <td class="table-actions">
                                <button type="button" class="ghost" @click="$emit('detail', vehicle.vehicleId)">详情</button>
                                <button type="button" class="ghost" @click="$emit('edit', vehicle.vehicleId)">编辑</button>
                                <button type="button" class="ghost" @click="$emit('audit', vehicle.vehicleId)">审计</button>
                                <button type="button" class="ghost danger" :disabled="userRole !== 'ADMIN'" @click="$emit('delete', vehicle.vehicleId)">删除</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="pager">
                <button type="button" class="secondary" :disabled="pagination.page <= 1" @click="$emit('prev-page')">上一页</button>
                <span>第 {{ pagination.page }} / {{ pagination.totalPages || 1 }} 页，共 {{ pagination.total }} 条</span>
                <button type="button" class="secondary" :disabled="pagination.page >= (pagination.totalPages || 1)" @click="$emit('next-page')">下一页</button>
            </div>
        </section>
    `
});
