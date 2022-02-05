<template>
  <Popup @show="load" trigger="hover" :position="position">
    <template slot="trigger">
      <img alt="avatar" :src="user.avatar" class="ui avatar link image" v-if="avatar&&user.avatar">
      <router-link :to="'/users/'+user.id">@{{ user.username }}</router-link>
    </template>

    <div class="ui card">
      <div class="left aligned content">
        <img alt="avatar" :src="details.avatar" v-if="details.avatar" class="right floated ui image">
        <router-link :to="'/users/'+user.id" class="header">{{ details.username }}</router-link>
        <div class="meta">
          加入于{{ details.createdTime | fromNow }} {{ details.createdTime | datetime }}
        </div>
        <div class="description" v-if="details.signature">
          <MdViewer :content="signature"></MdViewer>
        </div>
        <div class="extra content" v-if="details.role==='ROLE_ADMIN'">
          <i class="red user plus icon"></i>
          管理员
        </div>
        <div class="extra content" v-if="details.role==='ROLE_STAFF'">
          <i class="orange user icon"></i>
          员工
        </div>
      </div>
    </div>
  </Popup>
</template>

<script lang="ts">
import axios from 'axios'
import {Component, Prop, Vue} from 'vue-property-decorator'
import Popup from '@/components/Popup.vue'
import MdViewer from '@/components/MdViewer.vue'
import {Cache} from '@/models/Cache'
import {User} from '@/models/User'

const cache: Cache<string, User> = new Cache<string, User>(60_000)

@Component({
  components: {
    Popup,
    MdViewer,
  },
})
export default class UserAvatar extends Vue {
  @Prop({default: true}) private avatar!: boolean
  @Prop({default: 'bottom center'}) private position!: string
  @Prop() private user!: User
  private details: User = this.user

  get signature(): string {
    const signature = this.details.signature
    if (signature) {
      const theme = this.details.mdTheme ? `---\ntheme: ${this.details.mdTheme}\n---\n` : ''
      return theme + '>' + signature
    } else return ''
  }

  load() {
    if (this.details.createdTime) {
      return
    }

    if (cache.has(this.user.id)) {
      this.details = cache.get(this.user.id)
      return
    }

    axios.get('/users/' + this.user.id).then(({data}) => {
      this.details = data
      cache.set(this.user.id, data)
    })
  }
}
</script>

<style scoped>
.ui.card {
  min-width: 385px;
}

@media only screen and (max-width: 767px) {
  .ui.card {
    min-width: 250px;
  }
}

.ui.card .ui.right.floated.image {
  width: 46px;
  margin: -6px 0 0;
}

a {
  color: rgba(0, 0, 0, .4);
}

.link {
  cursor: pointer;
}
</style>
