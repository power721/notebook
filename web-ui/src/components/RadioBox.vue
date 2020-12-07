<template>
    <div class="fields" :class="{inline:inline, grouped:!inline}">
        <label>
            <slot></slot>
        </label>
        <div class="field" v-for="option in options" :key="option.key">
            <div class="ui radio checkbox" :class="{checked: value===option.value}" @click="select(option.value)">
                <input type="radio" class="hidden" :name="option.key" :value="option.value" :checked="value===option.value">
                <label>{{option.text}}</label>
            </div>
        </div>
    </div>
</template>

<script >
    export default {
        name: 'RadioBox',
        model: {
            prop: 'selected',
            event: 'changed'
        },
        props: {
            inline: {
                type: Boolean,
                default: false
            },
            options: {
                type: Array,
                default: () => [],
            },
            selected: [String, Number]
        },
        data() {
            return {
                value: this.selected
            };
        },
        watch: {
            selected(newValue) {
                this.value = newValue;
            }
        },
        methods: {
            select(value) {
                this.value = value;
                this.$emit('changed', value);
            },
        },
    };
</script>

<style scoped>

</style>
