package com.ssafy.commb.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.commb.dto.feed.FeedDto;
import com.ssafy.commb.dto.user.MyDto;
import com.ssafy.commb.exception.ApplicationException;
import com.ssafy.commb.model.*;
import com.ssafy.commb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ssafy.commb.dao.FeedDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

@RequiredArgsConstructor
@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private HashTagRepository hashTagRepository;

    @Autowired
    private DailyEventRepository dailyEventRepository;

    @Autowired
    private FeedDao feedDao;

    @Autowired
    private S3Service S3service;

    @Autowired
    private FollowService followService;

    @PersistenceContext
    private EntityManager entityManager;

    public void uploadFeed(FeedDto.RegisterRequest feedReq, MultipartHttpServletRequest request) throws IOException, ServletException {

        User user = new User();
        Book book = new Book();
        Feed feed = new Feed();

        user.setId((Integer) request.getAttribute("userId"));
        book.setId(feedReq.getBookId());
        feed.setUser(user);
        feed.setBook(book);
        feed.setCreateAt(new Date());
        feed.setContent(feedReq.getContents());

        Part part = S3service.extractFile(request.getParts()); // 파일 하나 받아옴
        String fileUrl = S3service.uploadS3(part, "feed");
        feed.setFileUrl(fileUrl);

        feedRepository.save(feed);


        // hashTag 추출 후 DB에 저장
        List<String> Tags = extractHashTag(feedReq.getContents());

        for (int i = 0; i < Tags.size(); i++) {
            HashTag hashTag = new HashTag();

            hashTag.setTag(Tags.get(i));
            hashTag.setFeed(feed);

            hashTagRepository.save(hashTag);
        }
    }

    /**
     * 특정 유저의 피드 리스트 가져오기
     * @param userId : target 유저 ID
     * @param request : 자신 유저 ID
     * @return : 피드 리스트
     */
    @Override
    public FeedDto.ResponseList getUserFeed(int userId, int page, HttpServletRequest request) {

        List<FeedDto> feeds = feedDao.userFeed(userId, page, (Integer) request.getAttribute("userId"));

        if(feeds.size() == 0) throw new ApplicationException(HttpStatus.valueOf(204), "end of page");

        for (FeedDto feed : feeds) {
            feed.setHashTags(feedDao.getHashTags(feed.getId()));
            feed.setComments(feedDao.getComments(feed.getId(), (Integer) request.getAttribute("userId")));
        }

        return FeedDto.ResponseList.builder()
                .data(feeds)
                .build();
    }

    /**
     * 특정 유저의 피드 개수
     * @param userId : 유저 ID
     * @return : 개수 반환
     */
    @Override
    public int getUserFeedCnt(int userId) {
        return feedDao.userFeedCnt(userId);
    }

    public void modifyFeed(String content, int feedId) {
        Optional<Feed> feed = feedRepository.findById(feedId);
        if (!feed.isPresent()) throw new ApplicationException(HttpStatus.valueOf(400), "변경할 피드가 없습니다.");

        // 변경 전 content로 등록한 hashTag 삭제
        List<HashTag> hashTags = hashTagRepository.findByFeedId(feedId);
        for (HashTag hashTag : hashTags) {
            hashTagRepository.delete(hashTag);
        }

        // 변경된 content로 feed 업데이트
        feed.get().setContent(content);
        feedRepository.save(feed.get());

        List<String> Tags = extractHashTag(content);

        for (int i = 0; i < Tags.size(); i++) {
            HashTag hashTag = new HashTag();

            hashTag.setTag(Tags.get(i));
            hashTag.setFeed(feed.get());

            hashTagRepository.save(hashTag);
        }
    }

    public int getUserId(int feedId) {
        Optional<Feed> feed = feedRepository.findById(feedId);
        return feed.get().getUser().getId();
    }

    public void deleteFeed(int feedId) {
        Optional<Feed> feed = feedRepository.findById(feedId);
        if (!feed.isPresent()) throw new ApplicationException(HttpStatus.valueOf(400), "삭제할 피드가 없습니다.");

        String url = feed.get().getFileUrl();
        S3service.deleteS3(url, "feed");

        feedRepository.deleteById(feedId);
    }

    public void reportFeed(int feedId, String reason, int userId) {
        Report report = new Report();

        Optional<Feed> feed = feedRepository.findById(feedId);
        Optional<User> user = userRepository.findById(userId);

        if (!feed.isPresent()) throw new ApplicationException(HttpStatus.valueOf(400), "신고할 피드가 없습니다.");

        report.setFeed(feed.get());
        report.setUser(user.get());
        report.setReason(reason);

        reportRepository.save(report);
    }

    public FeedDto.ResponseList getFollowingFeeds(int page, int userId) {

        List<FeedDto> feeds = feedDao.getFollowingFeeds(page, userId);

        for (FeedDto feed : feeds) {
            feed.setHashTags(feedDao.getHashTags(feed.getId()));
            feed.setComments(feedDao.getComments(feed.getId(), userId));
        }

        return FeedDto.ResponseList.builder()
                .data(feeds)
                .build();
    }

    public MyDto.ResponseList likeFeeds(int feedId, int userId) {

        Optional<Feed> feed = feedRepository.findById(feedId);

        // 피드에 좋아요를 누른 유저 목록
        List<Thumb> thumbsUser = feed.get().getThumbsUser();

        List<User> users = new ArrayList<>();
        for (Thumb thumb : thumbsUser) {
            users.add(thumb.getUser());
        }

        List<MyDto> myDtoList = users
                .stream()
                .map(user -> {
                    User u = userRepository.findUserById(user.getId()).get();

                    return MyDto.builder()
                            .id(u.getId())
                            .nickname(u.getNickname())
                            .userFileUrl(u.getFileUrl())
                            .isFollow(followService.isFollow(userRepository.findUserById(userId).get(), u))
                            .build();
                }).collect(Collectors.toList());

        MyDto.ResponseList myResList = MyDto.ResponseList.builder().data(myDtoList).build();

        return myResList;
    }

    /**
     * 해시태그 피드 검색(FTS), 유사도 기준 정렬
     * @param searchWord : 검색 단어(들), spacebar 기준 검색어 파싱
     * @param userId : 유저 ID
     * @return : 검색 피드들 반환
     */
    public FeedDto.ResponseList getFeeds(String searchWord, int page, int userId){
        StringBuilder dynamicQuery = new StringBuilder();
        String[] words = searchWord.split(" ");

        int countOfWords = words.length;
        if(countOfWords > 0) for (String word : words) dynamicQuery.append("\"").append(word).append("\" ");
        else dynamicQuery.append("\"\"");

        Map<String, Object> map = new HashMap<>();
        map.put("dynamicQuery", dynamicQuery.toString());
        map.put("userId", userId);
        map.put("countOfWords", countOfWords);
        map.put("page", page);
        List<FeedDto> feeds = feedDao.getFeeds(map);

        if(feeds.size() == 0) throw new ApplicationException(HttpStatus.valueOf(204), "end of page");

        for (FeedDto feed : feeds) {
            feed.setHashTags(feedDao.getHashTags(feed.getId()));
            feed.setComments(feedDao.getComments(feed.getId(), userId));
        }

        FeedDto.ResponseList feedResList = new FeedDto.ResponseList();
        feedResList.setData(feeds);

        return FeedDto.ResponseList.builder()
                .data(feeds)
                .build();
    }

    public List<String> extractHashTag(String content) {

        Pattern pattern = Pattern.compile("\\#([0-9a-zA-Z가-힣]*)"); // 주어진 정규표현식으로부터 패턴을 만든다.
        Matcher matcher = pattern.matcher(content); // 대상 문자열이 패턴과 일치할 경우 true 반환
        String extractHashTag = null;
        List<String> hashTags = new ArrayList<>();

        while (matcher.find()) { // 대상 문자열이 패턴과 일치하는 경우 true 반환 후 그 위치로 이동
            extractHashTag = specialCharacter_replace(matcher.group()); // 매칭된 부분을 반환

            if (extractHashTag == null) break;
            if (!hashTags.contains(extractHashTag)) hashTags.add(extractHashTag);
        }

        return hashTags;
    }

    public String specialCharacter_replace(String hashTag) {
        hashTag = StringUtils.replace(hashTag, "#", "");

        if (hashTag.length() < 1) return null;

        return hashTag;
    }

}
