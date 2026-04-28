import { defineComponent } from "../vue.js";
import { ApiConfigPanel } from "./ApiConfigPanel.js";

export const AuthPanel = defineComponent({
    name: "AuthPanel",
    components: {
        ApiConfigPanel
    },
    props: {
        apiBase: { type: String, required: true },
        healthStatus: { type: String, default: "unknown" },
        loginForm: { type: Object, required: true },
        registerForm: { type: Object, required: true }
    },
    emits: ["save-api", "check-health", "login", "register"],
    data() {
        return {
            activeTab: "login"
        };
    },
    template: `
        <section class="auth-landing">
            <div class="auth-landing__hero">
                <div class="hero-copy">
                    <p class="eyebrow">Fleet Mission Control</p>
                    <h1>车辆管理系统</h1>
                    <p>一个更现代的车辆运营工作台。登录、审计、统计和车辆档案现在由 Vue 组件化前端统一驱动，交互和状态会稳得多。</p>
                </div>

                <div class="hero-ribbon">
                    <span>Vue Components</span>
                    <span>HBase / Redis / JWT</span>
                    <span>Vehicle Ops</span>
                </div>

                <div class="hero-visual">
                    <div class="hero-orbit hero-orbit--one"></div>
                    <div class="hero-orbit hero-orbit--two"></div>
                    <div class="hero-card">
                        <span class="hero-card__label">实时能力</span>
                        <strong>统一车辆档案</strong>
                        <p>年检、保险、状态变更与审计追踪都在一个工作台中完成。</p>
                    </div>
                </div>
            </div>

            <div class="auth-landing__side">
                <ApiConfigPanel
                    :api-base="apiBase"
                    :health-status="healthStatus"
                    @save="$emit('save-api', $event)"
                    @check-health="$emit('check-health')"
                />

                <section class="auth-card">
                    <div class="auth-tabs">
                        <button :class="{ active: activeTab === 'login' }" type="button" @click="activeTab = 'login'">登录</button>
                        <button :class="{ active: activeTab === 'register' }" type="button" @click="activeTab = 'register'">注册</button>
                    </div>

                    <form v-if="activeTab === 'login'" class="auth-form" @submit.prevent="$emit('login')">
                        <p class="auth-form__title">进入车辆工作台</p>
                        <input v-model="loginForm.username" placeholder="用户名" autocomplete="username">
                        <input v-model="loginForm.password" placeholder="密码" type="password" autocomplete="current-password">
                        <button type="submit">进入系统</button>
                    </form>

                    <form v-else class="auth-form" @submit.prevent="$emit('register')">
                        <p class="auth-form__title">创建普通用户</p>
                        <input v-model="registerForm.username" placeholder="用户名" autocomplete="username">
                        <input v-model="registerForm.password" placeholder="密码" type="password" autocomplete="new-password">
                        <button type="submit" class="secondary">创建账号</button>
                    </form>
                </section>
            </div>
        </section>
    `
});
