<template>
  <div class="write-box mt-3">
    <div class="book-info d-flex flex-column justify-content-center">
      <img  class="book-cover" :src="bookInfo.bookFileUrl" alt="">
      <div class="title">{{ bookInfo.bookName }}</div>
      <div class="subtitle mt-1">{{ author }} | {{ bookInfo.publisher }}</div>
      <i 
        class="fi-rr-picture add-photo-btn"
        data-bs-toggle="modal" 
        data-bs-target="#imageCropModal"
      ></i>
    </div>
    <textarea 
      class="form-control content-input mt-2" 
      type="text" 
      :placeholder="bookInfo.bookName + '에 대한 글을 작성해주세요!'"
      @input="insertContent"
      :value="content"
    ></textarea>
    <img 
      v-if="preview"
      class="image-preview" 
      :src="preview" 
      alt="">
    <div v-if="!preview" class="image-position d-flex justify-content-center align-items-center">
      <i 
        class="fi-sr-add add-photo"
        data-bs-toggle="modal" 
        data-bs-target="#imageCropModal"
      ></i>
    </div>
    <span v-if="!preview" class="notice-text"><strong>사진을 추가</strong>해야 <strong>글쓰기를 완료</strong>할 수 있습니다. 게시글에 어울리는 사진을 선택해주세요!</span>
  </div>
</template>

<script>
import _ from 'lodash'
import { mapState } from 'vuex'

export default {
  name: 'WriteBox',
  props: {
    preview: String
  },
  data () {
    return {
      content: '',
      placeholder: "this.bookInfo.bookName + '글을 작성해주세요!'"
    }
  },
  methods: {
    insertContent (event) {
      this.content = event.target.value
      this.$emit('write', this.content)
    }
  },
  computed: {
    ...mapState('book', ['bookInfo']),
    author () {
      const authors = _.split(this.bookInfo.author, ',')
      if (authors.length > 1) {
        return authors[0] + ` 외 ${authors.length - 1}명`
      }
      return this.bookInfo.author
    }
  },
}
</script>

<style scoped>
  .write-box {
    position: relative;
    width: 280px;
    height: 350px;
    background: #FFFFFF;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
    border-radius: 10px;
    color: #212121;
  }
  .write-box .book-info {
    position: relative;
    width: 100%;
    height: auto;
    min-height: 70px;
    background: #F1F1F1;
    border-radius: 10px 10px 0px 0px;
    padding: 10px 10px 15px 75px;
  }
  .book-info .book-cover {
    position: absolute;
    left: 0;
    bottom: -2px;
    height: 90px;
    width: auto;
    border-radius: 10px;
    box-shadow: 0px 4px 5px rgba(0, 0, 0, 0.25);
  }
  .book-info .title {
    width: 100%;
    max-width: 180px;
    font-size: 14px;
    line-height: 20px;
    font-weight: 700;
  }
  .book-info .subtitle {
    width: 100%;
    max-width: 180px;
    font-size: 12px;
    line-height: 12px;
  }
  .book-info .add-photo-btn {
    position: absolute;
    right: 10px;
    bottom: 3px;
    font-size: 1.1rem;
    cursor: pointer;
  }
  .add-photo {
    margin-top: 5px;
    font-size: 1.5rem;
    cursor: pointer;
    color: #C4C4C4;
  }
  .write-box .image-preview {
    position: absolute;
    left: 10px;
    bottom: 10px;
    border-radius: 10px;
    width: 50px;
    height: auto;
  }
  .write-box .image-position {
    position: absolute;
    left: 10px;
    bottom: 10px;
    border-radius: 10px;
    width: 50px;
    height: 50px;
    border: #F1F1F1 dashed 3px;
    text-align: center;
  }
  .notice-text {
    position: absolute;
    left: 60px;
    bottom: 10px;
    width: 210px;
    padding-left: 10px;
    color: #7540EE;
    font-size: 0.75rem;
  }
  .content-input {
    background: none;
    box-shadow: none;
    border-radius: 0;
    border: none;
    font-size: 14px;
    letter-spacing: 1px;
    word-spacing: 1px;
    line-height: 24px;
    outline: none;
    padding: 1rem;
    height: 200px;
    word-wrap: break-word;
    resize: none;
  }
  .content-input::-webkit-scrollbar {
    display: none;
  }
  
</style>