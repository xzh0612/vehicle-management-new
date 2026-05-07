import { defineComponent } from "../vue.js";

export const ToastMessage = defineComponent({
    name: "ToastMessage",
    props: {
        message: { type: String, default: "" },
        type: { type: String, default: "info" }
    },
    emits: ["close"],
    data() {
        return {
            visible: false
        };
    },
    watch: {
        message(val) {
            if (val) {
                this.visible = true;
                clearTimeout(this._timer);
                this._timer = setTimeout(() => this.doClose(), 3000);
            }
        }
    },
    mounted() {
        if (this.message) {
            this.visible = true;
            this._timer = setTimeout(() => this.doClose(), 3000);
        }
    },
    beforeUnmount() {
        clearTimeout(this._timer);
    },
    methods: {
        doClose() {
            this.visible = false;
            clearTimeout(this._timer);
            this.$emit("close");
        }
    },
    template: `
        <section v-if="visible" class="toast" :class="type">
            <button class="toast__close" type="button" @click="doClose">&times;</button>
            <strong>系统消息</strong>
            <p>{{ message }}</p>
        </section>
    `
});
