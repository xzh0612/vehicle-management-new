import { defineComponent } from "../vue.js";

export const SideInsights = defineComponent({
    name: "SideInsights",
    props: {
        selectedVehicle: { type: Object, default: null },
        stats: { type: Object, required: true },
        auditLogs: { type: Array, required: true },
        adminCreate: { type: Object, required: true },
        userRole: { type: String, default: "" }
    },
    emits: ["create-user"],
    methods: {
        formatMileage(value) {
            return `${Number(value || 0).toLocaleString()} km`;
        },
        formatDateTime(timestamp) {
            return timestamp ? new Date(timestamp).toLocaleString() : "-";
        }
    },
    template: `
        <div class="side-stack">
            <section class="panel">
                <div class="panel-head">
                    <div>
                        <p class="eyebrow">Detail Focus</p>
                        <h3>{{ selectedVehicle ? selectedVehicle.plateNumber : '车辆详情' }}</h3>
                    </div>
                </div>
                <div v-if="selectedVehicle" class="detail-grid">
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
                <p v-else class="empty-note">点击车辆列表里的“详情”即可查看完整档案。</p>
            </section>

            <section class="panel">
                <div class="panel-head">
                    <div>
                        <p class="eyebrow">Distribution</p>
                        <h3>运营概览</h3>
                    </div>
                </div>
                <div class="mini-summary">
                    <div>
                        <h4>按状态</h4>
                        <ul>
                            <li v-for="(count, key) in (stats.byStatus || {})" :key="key">{{ key }}: {{ count }}</li>
                        </ul>
                    </div>
                    <div>
                        <h4>按品牌</h4>
                        <ul>
                            <li v-for="(count, key) in (stats.byBrand || {})" :key="key">{{ key }}: {{ count }}</li>
                        </ul>
                    </div>
                </div>
            </section>

            <section class="panel">
                <div class="panel-head">
                    <div>
                        <p class="eyebrow">Audit Stream</p>
                        <h3>最近操作</h3>
                    </div>
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
                <p v-else class="empty-note">点击车辆列表里的“审计”查看该车辆的操作历史。</p>
            </section>

            <section v-if="userRole === 'ADMIN'" class="panel panel--soft">
                <div class="panel-head">
                    <div>
                        <p class="eyebrow">Admin Action</p>
                        <h3>创建系统用户</h3>
                    </div>
                </div>
                <form class="admin-form" @submit.prevent="$emit('create-user')">
                    <input v-model="adminCreate.username" placeholder="用户名">
                    <input v-model="adminCreate.password" type="password" placeholder="密码">
                    <select v-model="adminCreate.role">
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>
                    <button type="submit">创建用户</button>
                </form>
            </section>
        </div>
    `
});
