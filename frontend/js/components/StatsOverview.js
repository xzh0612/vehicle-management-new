import { defineComponent } from "../vue.js";

export const StatsOverview = defineComponent({
    name: "StatsOverview",
    props: {
        stats: { type: Object, required: true },
        overdueCount: { type: Number, default: 0 }
    },
    template: `
        <section class="overview-grid">
            <article class="overview-card">
                <span>车辆总数</span>
                <strong>{{ stats.total || 0 }}</strong>
                <p>系统当前在管车辆档案</p>
            </article>
            <article class="overview-card overview-card--mint">
                <span>年检预警</span>
                <strong>{{ stats.inspectionReminders?.expiringWithin30Days || 0 }}</strong>
                <p>30 天内需要处理</p>
            </article>
            <article class="overview-card overview-card--amber">
                <span>保险预警</span>
                <strong>{{ stats.insuranceReminders?.expiringWithin30Days || 0 }}</strong>
                <p>到期风险即时可见</p>
            </article>
            <article class="overview-card overview-card--alert">
                <span>逾期风险</span>
                <strong>{{ overdueCount }}</strong>
                <p>年检与保险逾期总量</p>
            </article>
        </section>
    `
});
