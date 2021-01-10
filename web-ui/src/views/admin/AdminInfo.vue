<template>
  <div class="ui left aligned container">
    <div class="ui raised segment">
      <div class="ui divided items">
        <div class="item">
          <div class="content">
            <a class="header">网络</a>
            <div class="description">
              <div>IP地址： {{info.ip}}</div>
              <div>主机名： {{info.hostName}}</div>
            </div>
          </div>
        </div>
        <div class="item">
          <div class="content">
            <a class="header">Java</a>
            <div class="description">
              <div>版本： {{info.javaVendor}} {{info.javaVersion}}</div>
              <div>JRE目录： {{info.javaHome}}</div>
              <div>JVM名称： {{info.jvmName}}</div>
              <div>JVM CPU： {{info.jvmCpus}}</div>
              <div>JVM 内存：
                <span data-tooltip="空闲内存">{{info.jvmFreeMemory | byte2string}}</span> /
                <span data-tooltip="总内存">{{info.jvmTotalMemory | byte2string}}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="item">
          <div class="content">
            <a class="header">系统</a>
            <div class="description">
              <div>名称： {{info.osName}}</div>
              <div>版本： {{info.osVersion}}</div>
              <div>架构： {{info.osArch}}</div>
            </div>
          </div>
        </div>
        <div class="item">
          <div class="content">
            <a class="header">用户</a>
            <div class="description">
              <div>用户名称： {{info.userName}}</div>
              <div>用户目录： {{info.userHome}}</div>
            </div>
          </div>
        </div>
        <div class="item">
          <div class="content">
            <a class="header">其它</a>
            <div class="description">
              <div>工作目录： {{info.workDir}}</div>
              <div>时区： {{info.timezone}}</div>
              <div>PID： {{info.pid}}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import configService from '@/services/config.service'
  import {SystemInfo} from '@/models/SystemInfo'

  @Component
  export default class AdminInfo extends Vue {
    info: SystemInfo = new SystemInfo()

    mounted() {
      configService.setTitle('系统信息')
      this.load()
    }

    load() {
      axios.get('/admin/info').then(({data}) => {
        this.info = data
      })
    }
  }
</script>

<style scoped>

</style>
