<template>
  <div class="weekly-book">
    <div v-if="weeklyBook" class="d-flex flex-column align-items-center">
      <img 
        class="book mt-3" 
        :src="weeklyBook.bookFileUrl" 
        alt="weeklyBookCover"
        @click="$router.push({ name: 'BookInfo', params: {id: weeklyBook.id} })"
      >
      <div class="title mt-3 mb-2">{{ weeklyBook.bookName }}</div>
      <div class="info d-flex">
        <div>{{ author }}</div>
        <div class="mx-1">|</div>
        <div>{{ weeklyBook.publisher }}</div>
        <div class="mx-1">|</div>
        <div>{{ weeklyBook.year }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import _ from 'lodash'
import { mapState } from 'vuex'

export default {
  name: 'ChallengeWeeklyBook',
  computed: {
    ...mapState('challenge', ['weeklyBook']),
    author () {
      const authors = _.split(this.weeklyBook.author, ',')
      if (authors.length > 1) {
        return authors[0] + ` 외 ${authors.length - 1}명`
      }
      return this.weeklyBook.author
    }
  }
}
</script>

<style scoped>
  .weekly-book .book {
    width: 100px;
    height: auto;
    border-radius: 10px;
    box-shadow: 5px 5px 5px 3px rgba(0, 0, 0, 0.25);
    cursor: pointer;
  }
  .weekly-book .book:hover {
    width: 105px;
  }
  .weekly-book .title {
    width: 80%;
    text-align: center;
    font-size: 18px;
    line-height: 20px;
    font-weight: 700;
  }
  .weekly-book .info {
    font-size: 13px;
    font-weight: 300;
  }
</style>