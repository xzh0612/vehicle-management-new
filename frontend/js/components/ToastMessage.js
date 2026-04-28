import { defineComponent } from "../vue.js";

export const ToastMessage = defineComponent({
    name: "ToastMessage",
    props: {
        message: { type: String, default: "" },
        type: { type: String, default: "info" }
    },
    template: `
        <transition name="toast-fade">
            <section v-if="message" class="toast" :class="type">
                <strong>系统消息</strong>
                <p>{{ message }}</p>
            </section>
        </transition>
    `
});
