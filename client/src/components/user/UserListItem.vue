<template>
  <div class="d-flex align-items-center gap-2 my-3">
    <img 
      v-if="user.userFileUrl" 
      :src="user.userFileUrl" 
      alt=""
      class="profile"
      @click="$router.push({ name: 'Profile', params: {id: user.id, page: 0} })" 
    >
    <img 
      v-else 
      src="@/assets/image/common/profileDefault.svg" 
      alt=""
      class="profile"
      @click="$router.push({ name: 'Profile', params: {id: user.id, page: 0} })"
    >
    <span 
      class="nickname"
      @click="$router.push({ name: 'Profile', params: {id: user.id, page: 0} })"
    >{{ user.nickname }}</span>
    <div v-if="!isMe">
      <button 
        v-if="isFollow" 
        class="btn-follow btn-grey"
        @click="unfollow"
      >언팔로우</button>
      <button 
        v-else 
        class="btn-follow btn-yellow"
        @click="follow"
      >팔로우</button>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import userApi from '@/api/user'
export default {
  name: 'UserListItem',
  props: {
    user: Object
  },
  data () {
    return {
      isFollow: null
    }
  },
  methods: {
    ...mapActions('user', ['getFollowing', 'getFollower']),
    async follow () {
      this.isFollow = true
      await userApi.follow(this.user.id)
        .then(() => {
          this.getFollowing(this.$route.params.id)
          this.getFollower(this.$route.params.id)
        })
    },
    async unfollow () {
      this.isFollow = false
      await userApi.unfollow(this.user.id)
        .then(() => {
          this.getFollowing(this.$route.params.id)
          this.getFollower(this.$route.params.id)
        })
    }
  },
  computed: {
    ...mapState('user', ['myInfo']),
    isMe () {
      return this.user.id === this.myInfo.id
    }
  },
  created () {
    this.isFollow = this.user.isFollow
  }
}
</script>

<style scoped>
  .profile {
    width: 40px;
    height: 40px;
    border-radius: 100%;
    cursor: pointer;
  }
  .nickname {
    font-size: 14px;
    font-weight: 500;
    width: 130px;
    cursor: pointer;
  }
  .btn-follow {
    border: none;
    outline: none;
    width: 70px;
    height: 25px;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 700;
  }
</style>