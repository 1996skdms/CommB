<template>
  <div class="Weelky">
    <ChallengeWeeklyBook/>
    <ChallengeFeedList
      :cnt=weeklyCnt
      :feeds=weeklyFeed
      :need-book=needBook
      @last=addFeed
    />
  </div>
</template>

<script>
import moment from 'moment'
import { mapState, mapActions } from 'vuex'
import ChallengeWeeklyBook from './ChallengeWeeklyBook'
import ChallengeFeedList from './ChallengeFeedList'

export default {
  name: 'ChallengeWeekly',
  components: {
    ChallengeWeeklyBook,
    ChallengeFeedList
  },
  data() {
    return {
      page: 1,
      isEnd: false,
      needBook: false
    }
  },
  methods: {
    ...mapActions('challenge', ['getWeeklyBook', 'getWeeklyFeeds', 'getWeeklyParticipate']),
    addFeed () {
      this.getWeeklyFeeds(this.page)
      this.page ++
    }
  },
  computed: {
    ...mapState('challenge', ['weeklyCnt', 'weeklyFeed'])
  },
  async created () {
    await this.getWeeklyBook(moment().format('YYYY-MM-DD'))
    this.getWeeklyFeeds(0)
    this.getWeeklyParticipate()
  }
}
</script>

<style>

</style>