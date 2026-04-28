import { defineComponent } from "../vue.js";

export const ApiConfigPanel = defineComponent({
    name: "ApiConfigPanel",
    props: {
        apiBase: { type: String, required: true },
        healthStatus: { type: String, default: "unknown" },
        saving: { type: Boolean, default: false }
    },
    emits: ["save", "check-health"],
    data() {
        return {
            localApiBase: this.apiBase
        };
    },
    watch: {
        apiBase(value) {
            this.localApiBase = value;
        }
    },
    computed: {
        statusLabel() {
            if (this.healthStatus === "up") return "API 已连接";
            if (this.healthStatus === "down") return "API 未连接";
            if (this.healthStatus === "checking") return "正在检测";
            return "未检测";
        }
    },
    methods: {
        onSubmit() {
            this.$emit("save", this.localApiBase);
        }
    },
    template: `
        <section class="api-panel">
            <div class="api-panel__top">
                <div>
                    <p class="eyebrow">Connection</p>
                    <h3>API 连接设置</h3>
                </div>
                <span class="api-health" :class="healthStatus">{{ statusLabel }}</span>
            </div>

            <form class="api-panel__form" @submit.prevent="onSubmit">
                <input
                    v-model="localApiBase"
                    placeholder="http://localhost:8080/api"
                    autocomplete="off"
                >
                <div class="api-panel__actions">
                    <button type="button" class="secondary" @click="$emit('check-health')">连接检测</button>
                    <button type="submit" :disabled="saving">保存地址</button>
                </div>
            </form>

            <p class="helper">输入 <code>http://localhost:8080</code> 时会自动补成 <code>/api</code>。默认账号仍然是 <code>admin / 123456</code>。</p>
        </section>
    `
});
