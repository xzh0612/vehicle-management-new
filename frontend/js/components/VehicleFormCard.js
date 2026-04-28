import { defineComponent } from "../vue.js";

export const VehicleFormCard = defineComponent({
    name: "VehicleFormCard",
    props: {
        form: { type: Object, required: true },
        editMode: { type: Boolean, default: false },
        statuses: { type: Array, required: true }
    },
    emits: ["submit", "cancel"],
    template: `
        <section class="panel panel--soft">
            <div class="panel-head">
                <div>
                    <p class="eyebrow">Vehicle Profile</p>
                    <h3>{{ editMode ? '编辑车辆档案' : '新增车辆档案' }}</h3>
                </div>
                <button v-if="editMode" type="button" class="ghost" @click="$emit('cancel')">取消编辑</button>
            </div>

            <form class="vehicle-form" @submit.prevent="$emit('submit')">
                <div class="vehicle-form__grid">
                    <input v-model="form.plateNumber" placeholder="车牌号">
                    <input v-model="form.vin" placeholder="车架号 VIN">
                    <input v-model="form.engineNumber" placeholder="发动机号">
                    <input v-model="form.brand" placeholder="品牌">
                    <input v-model="form.model" placeholder="型号">
                    <input v-model="form.ownerName" placeholder="车主姓名">
                    <input v-model="form.phone" placeholder="联系电话">
                    <select v-model="form.status">
                        <option v-for="status in statuses" :key="status" :value="status">{{ status }}</option>
                    </select>
                    <input v-model="form.registerDate" type="date">
                    <input v-model="form.annualInspectionDate" type="date">
                    <input v-model="form.insuranceExpireDate" type="date">
                    <input v-model.number="form.mileage" type="number" min="0" placeholder="里程数">
                </div>
                <textarea v-model="form.remark" rows="4" placeholder="备注，例如保养记录、车况说明、特殊提醒"></textarea>
                <div class="vehicle-form__footer">
                    <button type="submit">{{ editMode ? '保存修改' : '创建车辆档案' }}</button>
                </div>
            </form>
        </section>
    `
});
