<template>
  <div class="info-box my-4 d-flex align-items-center gap-2">
    <div 
      v-if="needBtn && isMine"
      class="book-cover"
    >
      <img 
        :src="book.bookFileUrl" 
        alt=""
        @click="$router.push({ name: 'BookInfo', params: {id: book.id} })"
      >
      <div class="hover-content d-flex flex-column align-items-center gap-1">
        <button
          class="btn-6 btn-yellow hover-btn"
          @click="$router.push({ name: 'BookInfo', params: {id: book.id} })"
        >상세보기</button>
        <button
          class="btn-6 btn-primary1 hover-btn"
          @click="addCollection"
        >컬렉션 +</button>
      </div>
    </div>
    <img 
      v-else
      class="book-cover" 
      :src="book.bookFileUrl" 
      alt=""
      @click="$router.push({ name: 'BookInfo', params: {id: book.id} })"
    >
    <div>
      <div class="title">{{ bookName }}</div>
      <div class="subtitle mt-1">{{ author }} | {{ book.publisher }}</div>
    </div>
    <div 
      v-if="profileInfo.user.id === myInfo.id"
      class="d-flex flex-column right-fix gap-2"
    >
      <button 
        v-if="from === 'library'"
        class="btn-65 btn-yellow"
        @click="$router.push({ name: 'Write', params: { id: book.id }})"
      >글쓰기</button>
      <button 
        v-if="from === 'bookcart' && isMine"
        class="btn-65 btn-yellow"
        @click="onMoveBook"
      >완독</button>
      <button 
        v-if="isMine"
        class="btn-65 btn-grey"
        @click="onDeleteBook"
      >삭제</button>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import userApi from '@/api/user.js'
import _ from 'lodash'
export default {
  name: 'ProfileBookListItem',
  props: {
    book: Object,
    from: String
  },
  methods: {
    ...mapActions('book', ['deleteBook']),
    ...mapActions('user', ['getCollections']),
    onDeleteBook () {
      this.deleteBook(this.book.id)
      this.$emit('delete', this.book.id) 
    },
    onMoveBook () {
      this.$store.commit('user/SET_MOVE_TARGET', this.book)
    },
    async addCollection () {
      await userApi.addBookCollection(this.myInfo.id, this.book.id)
        .then((res) => {
          console.log(res)
        })
        .catch((err) => {
          console.log(err.response)
        })
      this.getCollections(this.profileInfo.user.id)
    }
  },
  computed: {
    ...mapState('user', ['profileInfo', 'myInfo']),
    author () {
      const authors = _.split(this.book.author, ',')
      if (authors.length > 1) {
        return authors[0] + ` 외 ${authors.length - 1}명`
      }
      return this.book.author
    },
    bookName () {
      return _.split(this.book.bookName, '(')[0]
    },
    isMine () {
      return this.myInfo.id === this.profileInfo.user.id
    },
    needBtn () {
      return this.from === 'library' 
              && this.profileInfo.bookCollection.length < 5
              && !this.inCollection
    },
    collection () {
      return this.profileInfo.bookCollection.map((book) => {
        return book.id
      })
    },
    inCollection () {
      return this.collection.includes(this.book.id)
    }
  }
}
</script>

<style scoped>
  .info-box {
    position: relative;
    min-height: 70px;
    height: 100%;
    width: 270px;
    padding: 10px 10px 10px 75px;
    background: #f1f1f1;
    border-radius: 10px;
    filter: drop-shadow(0px 2px 5px rgb(0, 0, 0, 0.25));
    color: #212121;
  }
  .info-box .book-cover {
    position: absolute;
    left: 0;
    top: 50%;
    transform: translate(0, -50%);
    height: 90px;
    width: auto;
    overflow: hidden;
    border-radius: 10px;
    filter: drop-shadow(0px 2px 5px rgba(0, 0, 0, 0.25));
    cursor: pointer;
    background: #212121;
  }
  .book-cover img {
    height: 90px;
    width: auto;
  }
  .book-cover:hover {
    height: 95px;
    width: auto;
  }
  .book-cover:hover img {
    height: 95px;
    width: auto;
    opacity: 0.8;
  }
  .book-cover:hover .hover-content{
    opacity: 1;
  }
  .hover-content {
    position: absolute;
    left: 7px;
    top: 50%;
    transform: translate(0, -50%);
    opacity: 0;
    z-index: 1;
    transition: all 600ms ease;
  }
  .hover-btn {
    font-size: 0.6rem;
  } 
  .hover-btn:hover {
    font-size: 0.6rem;
    font-weight: 700;
  } 
  .info-box .title {
    width: 100%;
    max-width: 110px;
    font-size: 14px;
    line-height: 20px;
    font-weight: 700;
  }
  .info-box .subtitle {
    width: 100%;
    max-width: 110px;
    font-size: 11px;
    line-height: 12px;
  }
  .btn-65 {
    border: none;
    width: 65px;
    height: 25px;
    border-radius: 13px;
    outline: none;
    font-size: 12px;
    font-weight: 700;
  }
  .right-fix {
    position: absolute;
    right: 10px
  }
</style>