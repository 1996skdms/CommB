<template>
  <div class="weekly-article d-flex flex-column my-3">
    <img 
      class="article-img" 
      :src="feed.feedFileUrl" 
      alt="feed image"
      @click="moveToDetail(feed.id)"
    >
    <div class="article-info">
      <div 
        class="user d-flex align-items-center gap-1"
        @click="$router.push({ name: 'Profile', params: {id: feed.user.id, page: 0} })"
      >
        <img v-if="feed.user.userFileUrl" class="profile" :src="feed.user.userFileUrl" alt="">
        <img v-else class="profile" src="@/assets/image/common/profileDefault.svg" alt="">
        <span class="name">{{ feed.user.nickname }}</span>
      </div>
      <div class="content" @click="moveToDetail(feed.id)">{{ shortenContent }}</div>
      <div class="hashtags mt-1">
        <span
          v-for="(hashtag, idx) in hashtagSlice"
          :key=idx
          class="badge rounded-pill me-1"
          @click="searchTag(hashtag.tag)"
        >#{{ hashtag.tag }}</span>
        <span
          v-if="feed.hashTags.length > 2"
          @click="moveToDetail(feed.id)"
          class="badge and-more rounded-pill me-1">+{{feed.hashTags.length - 2}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import _ from 'lodash'

export default {
  name: 'ChallengeFeedItem',
  props: {
    feed: Object,
    needBook: Boolean
  },
  data () {
    return {
      bookCover: null
    }
  },
  methods: {
    moveToDetail (feedId) {
      this.$router.push({ name: 'FeedView', params: { flag: this.flag, target: feedId }})
    },
    searchTag (keyword) {
      this.$router.push({ 
        name: 'Search', 
        params: { flag: 'feeds' },
        query: { q: keyword }  
      })
    }
  },
  computed: {
    flag () {
      return this.needBook ? 'cd' : 'cw'
    },
    shortenContent () {
      return _.truncate(this.feed.content, {
        'length': 50,
      })
    },
    shortenBookName () {
      return _.split(this.feed.book.bookName, '(')[0]
    },
    hashtagSlice () {
      return _.slice(this.feed.hashTags, 0, 2)
    }
  },
}
</script>

<style scoped>
  .weekly-article {
    position: relative;
    margin: 5px 10px;
    padding: 10px 0;
    height: fit-content;
    min-height: 100px;
    background: #683EC9;
    border-radius: 0px 15px 15px 15px;
  }
  .article-img {
    position: absolute;
    top: 0;
    height: 100%;
    width: 100px;
    border-radius: 0 0 0 15px;
    object-fit: cover;
    cursor: pointer;
  }
  .article-info {
    padding-left: 110px;
    padding-right: 10px;
  }
  .article-info .book {
    font-size: 12px;
    color: #C4C4C4;
  }
  .article-info .content {
    font-size: 10px;
    font-weight: 200;
    text-align: justify;
    margin: 2px 3px 0;
    white-space: pre-line;
    cursor: pointer;
  }
  .user .profile {
    width: 16px;
    height: 16px;
    border-radius: 100%;
    cursor: pointer;
  }
  .user .name {
   font-size: 12px;
   font-weight: 500;
   cursor: pointer;
  }
  .badge {
    font-size: 8px;
    color: #585858;
    background: #FFDC7C;
    cursor: pointer;
  }
  .badge:hover {
    font-size: 9px;
  }
  .and-more {
    background:  rgba(255, 220, 124, 0.8);
  }
</style>