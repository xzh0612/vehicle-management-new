import { defineComponent } from "../vue.js";

export const ToastMessage = defineComponent({
    name: "ToastMessage",
    props: {
        message: { type: String, default: "" },
        type: { type: String, default: "info" }
    },
    emits: ["close"],
    mounted() {
        if (this.message) {
            this._timer = setTimeout(() => this.$emit("close"), 3000);
        }
    },
    beforeUnmount() {
        clearTimeout(this._timer);
    },
    watch: {
        message(newVal) {
            clearTimeout(this._timer);
            if (newVal) {
                this._timer = setTimeout(() => this.$emit("close"), 3000);
            }
        }
    },
    template: `
        <transition name="toast-fade">
            <section v-if="message" class="toast" :class="type">
                <button class="toast__close" @click="$emit('close')">&times;</button>
                <strong>系统消息</strong>
                <p>{{ message }}</p>
            </section>
        </transition>
    `
});
