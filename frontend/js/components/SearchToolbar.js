import { defineComponent } from "../vue.js";

export const SearchToolbar = defineComponent({
    name: "SearchToolbar",
    props: {
        search: { type: Object, required: true },
        statuses: { type: Array, required: true }
    },
    emits: ["search", "reset"],
    template: `
        <section class="panel">
            <div class="panel-head">
                <div>
                    <p class="eyebrow">Discovery</p>
                    <h3>筛选与排序</h3>
                </div>
            </div>

            <form class="toolbar-grid" @submit.prevent="$emit('search')">
                <input v-model="search.keyword" placeholder="关键字：车牌 / VIN / 品牌 / 车主">
                <input v-model="search.brand" placeholder="品牌">
                <input v-model="search.ownerName" placeholder="车主">
                <select v-model="search.status">
                    <option value="">全部状态</option>
                    <option v-for="status in statuses" :key="status" :value="status">{{ status }}</option>
                </select>
                <select v-model="search.sortBy">
                    <option value="updatedAt">最近更新</option>
                    <option value="createdAt">创建时间</option>
                    <option value="plateNumber">车牌号</option>
                    <option value="brand">品牌</option>
                    <option value="ownerName">车主</option>
                    <option value="mileage">里程数</option>
                </select>
                <select v-model="search.sortDir">
                    <option value="desc">降序</option>
                    <option value="asc">升序</option>
                </select>

                <div class="toolbar-actions">
                    <button type="submit">应用筛选</button>
                    <button type="button" class="secondary" @click="$emit('reset')">重置</button>
                </div>
            </form>
        </section>
    `
});
