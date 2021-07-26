package com.ssafy.commb.controller;

import com.ssafy.commb.dto.book.BookDto;
import com.ssafy.commb.dto.feed.CommentDto;
import com.ssafy.commb.dto.feed.FeedDto;
import com.ssafy.commb.dto.feed.HashTagDto;
import com.ssafy.commb.dto.report.ReportDto;
import com.ssafy.commb.dto.user.MyDto;
import com.ssafy.commb.dto.user.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;

@RestController
@RequestMapping(value="/feeds")
@Api("Feed Controller API V1")
public class FeedController {

    static final int id = 1;
    static final String nickname = "크루엘라";
    static final String url = "URL 자리";
    static final String tag = "#오늘도 힘내세요";
    static final String content = "이것은 글입니다.";
    static final int cnt = 134;
    static Date date;
    static final boolean bool = true;

    static {
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS").parse("2021-07-31 10:12:15");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // /feeds?searchWord="abc"
    // 게시물 리스트 검색
    @GetMapping("")
    @ApiOperation(value="피드 리스트 검색", response = FeedDto.Response.class)
    public ResponseEntity<List<FeedDto.Response>> getFeeds(@RequestParam String searchWord) throws ParseException {
        UserDto user = UserDto.builder().id(id).nickname(nickname).userFileUrl(url).build();
        BookDto book = BookDto.builder().id(id).bookName("책이름").build();

        List<HashTagDto> hashTags = new ArrayList<>();
        hashTags.add(HashTagDto.builder().tag(tag).build());

        List<CommentDto> comments = new ArrayList<>();
        comments.add(CommentDto.builder().id(id).content(content).userId(id).nickname(nickname).thumbCnt(cnt).createAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-07-31 12:10:00")).isThumb(bool).isMod(bool).build());


        FeedDto feed = FeedDto.builder().id(id).createAt(date).content(content).isThumb(bool)
                .thumbCnt(cnt).feedFileUrl(url).user(user).book(book).hashTags(hashTags).comments(comments).build();

        FeedDto.Response feedRes = new FeedDto.Response();
        feedRes.setData(feed);

        List<FeedDto.Response> feedResList = new ArrayList<>();
        feedResList.add(feedRes);

        return new ResponseEntity<List<FeedDto.Response>>(feedResList, HttpStatus.OK);
    }

    // 게시물 작성
    @PostMapping("")
    @ApiOperation(value="피드 작성")
    public ResponseEntity uploadFeed(@RequestBody FeedDto feed, MultipartHttpServletRequest request){

        return new ResponseEntity(HttpStatus.valueOf(201));
    }

    // 게시물 수정
    @PutMapping("/{feedId}")
    @ApiOperation(value="피드 수정")
    public ResponseEntity modifyFeed(@RequestBody FeedDto feed, @PathVariable Integer feedId){

        return new ResponseEntity(HttpStatus.OK);
    }

    // 게시물 삭제
    @DeleteMapping("/{feedId}")
    @ApiOperation(value="피드 삭제")
    public ResponseEntity deleteFeed(@PathVariable Integer feedId){

        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    // 게시물 좋아요
    @PostMapping("/{feedId}/feed-like")
    @ApiOperation(value="게시물 좋아요 누르기")
    public ResponseEntity likeFeed(@PathVariable Integer feedId){

        return new ResponseEntity(HttpStatus.valueOf(201));
    }

    // 게시물 좋아요 취소
    @DeleteMapping("/{feedId}/feed-like")
    @ApiOperation(value="피드 좋아요 취소")
    public ResponseEntity deleteLikeFeed(@PathVariable Integer feedId){

        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    // /feeds/5/feed-likes
    // 게시물 좋아요 목록
    @GetMapping("/{feedId}/feed-likes")
    @ApiOperation(value="피드 좋아요 리스트", response = MyDto.Response.class)
    public ResponseEntity<MyDto.Response> likeFeeds(@PathVariable Integer feedId){
        MyDto.Response myRes = new MyDto.Response();
        MyDto my = MyDto.builder().id(id).nickname(nickname).userFileUrl(url).isFollow(bool).build();

        myRes.setData(my);
        return new ResponseEntity<MyDto.Response>(myRes, HttpStatus.OK);
    }

    // 댓글 작성
    @PostMapping("/{feedId}/comments")
    @ApiOperation(value="댓글 작성")
    public ResponseEntity uploadComment(@PathVariable Integer feedId, @RequestBody FeedDto feed){

        return new ResponseEntity(HttpStatus.valueOf(201));
    }


    // 댓글 수정
    @PutMapping("/{feedId}/comments/{commentId}")
    @ApiOperation(value="댓글 수정")
    public ResponseEntity modifyComment(@PathVariable Integer commentId, @PathVariable Integer feedId, @RequestBody FeedDto feed){

        return new ResponseEntity(HttpStatus.valueOf(201));
    }

    // 댓글 삭제
    @DeleteMapping("/{feedId}/comments/{commentId}")
    @ApiOperation(value="댓글 삭제")
    public ResponseEntity deleteComment(@PathVariable Integer commentId, @PathVariable Integer feedId){

        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    // 댓글 좋아요 or 취소
    @PostMapping("/{feedId}/comments/{commentId}/comment-like")
    @ApiOperation(value="댓글 좋아요 누르기")
    public ResponseEntity likeComment(@PathVariable Integer feedId, @PathVariable Integer commentId){

        return new ResponseEntity(HttpStatus.valueOf(201));
    }

    @DeleteMapping("/{feedId}/comments/{commentId}/comment-like")
    @ApiOperation(value="댓글 좋아요 취소")
    public ResponseEntity deleteLikeComment(@PathVariable Integer feedId, @PathVariable Integer commentId){

        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    // /feeds/5/following/feeds
    // 내가 팔로잉하는 사람들의 피드 목록
    @GetMapping("/{userid}/following/feeds")
    @ApiOperation(value="내가 팔로잉 하는 사람들의 피드 리스트", response = FeedDto.Response.class)
    public ResponseEntity<List<FeedDto.Response>> getFollowingFeeds(@PathVariable String userid) throws ParseException {
        UserDto user = UserDto.builder().id(id).nickname(nickname).userFileUrl(url).build();
        BookDto book = new BookDto();

        List<HashTagDto> hashTags = new ArrayList<>();
        hashTags.add(HashTagDto.builder().tag(tag).build());

        List<CommentDto> comments = new ArrayList<>();
        comments.add(CommentDto.builder().id(id).content(content).userId(id).nickname(nickname).thumbCnt(cnt).createAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-07-31 12:10:00")).isThumb(bool).isMod(bool).build());


        FeedDto feed = FeedDto.builder().id(id).createAt(new SimpleDateFormat("yyyy-MM-dd HH:MM:SS").parse("2021-07-31 10:12:15")).content(content).isThumb(bool)
                .thumbCnt(cnt).feedFileUrl(url).user(user).book(book).hashTags(hashTags).comments(comments).build();

        FeedDto.Response feedRes = new FeedDto.Response();
        feedRes.setData(feed);

        List<FeedDto.Response> feedResList = new ArrayList<>();
        feedResList.add(feedRes);

        return new ResponseEntity<List<FeedDto.Response>>(feedResList, HttpStatus.OK);
    }


    // 피드 신고
    @PostMapping("/{feedId}/reports")
    @ApiOperation(value="피드 신고")
    public ResponseEntity reportFeed(@PathVariable Integer feedId, @RequestBody ReportDto report){

         return new ResponseEntity(HttpStatus.valueOf(201));
    }


}