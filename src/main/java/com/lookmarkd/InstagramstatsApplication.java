package com.lookmarkd;

import com.lookmarkd.domain.FOSUser;
import com.lookmarkd.domain.SocialStatistic;
import com.lookmarkd.repository.FOSUserRepository;
import com.lookmarkd.repository.SocialStatisticsRepository;
import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RabbitListener(queues = "asyncstats")
@EnableScheduling
@ComponentScan(basePackages = "com.lookmarkd")
public class InstagramstatsApplication {

    @Autowired
    FOSUserRepository fosUserRepository;

    @Autowired
    SocialStatisticsRepository socialStatisticsRepository;

    @Autowired
    Environment env;

    private final Logger logger = LoggerFactory.getLogger(InstagramstatsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InstagramstatsApplication.class, args);
    }

    @Bean
    public Queue asyncstats() {
        return new Queue("asyncstats");
    }


    @RabbitHandler
    public void process(@Payload String asyncstats) {
        logger.info("Message acquired:" + asyncstats);
        Map<String, Object> message = JsonParserFactory.getJsonParser().parseMap(asyncstats);
        if (message.containsKey("body") && ((Map<String, Object>) message.get("body")).containsKey("username")) {
            String username = (String) ((Map<String, Object>) message.get("body")).get("username");
            logger.info("Username:" + username);
            FOSUser user = this.fosUserRepository.findByUsername(username);
            if (null != user) {
                logger.info("user acquired: " + user.getId());
                String secret = env.getProperty("instagram.client.secret");
                String instagramAccessToken = user.getInstagramAccessToken();
                logger.info("Using accesstoken:" + instagramAccessToken);
                Token accessToken = new Token(instagramAccessToken, secret);
                logger.info("Connecting to instagram");
                Instagram instagram = new Instagram(accessToken);
                logger.info("instagram connected");
                try {
                    int followers = 0;
                    String instagramUserId = instagram.getCurrentUserInfo().getData().getId();
                    UserFeed userFollowedByList = instagram.getUserFollowedByList(instagramUserId);
                    do {
                        if (followers > 0) {
                            userFollowedByList = instagram.getUserFollowedByListNextPage(userFollowedByList.getPagination());
                        }
                        if (null != userFollowedByList && null != userFollowedByList.getUserList()) {
                            followers += userFollowedByList.getUserList().size();
                        }
                    } while (userFollowedByList.getPagination().hasNextPage());
                    logger.info("Followers:" + followers);
                    SocialStatistic followersStats = new SocialStatistic.Builder()
                            .providerType("instagram")
                            .statisticsType(SocialStatistic.STAT_TYPE_FOLLOWERS)
                            .user(user)
                            .recordedAt(Calendar.getInstance().getTime())
                            .statistic(followers)
                            .build();
                    try {
                        socialStatisticsRepository.save(followersStats);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    int media = 0;
                    int comments = 0;
                    int likes = 0;
                    MediaFeed recentMediaFeed = instagram.getUserRecentMedia();
                    do {
                        if (media > 0) {
                            recentMediaFeed = instagram.getRecentMediaNextPage(recentMediaFeed.getPagination());
                        }
                        if (null != recentMediaFeed && null != recentMediaFeed.getData()) {
                            media += recentMediaFeed.getData().size();
                            for (MediaFeedData mediaFeedData : recentMediaFeed.getData()) {
                                comments += mediaFeedData.getComments().getCount();
                                likes += mediaFeedData.getLikes().getCount();
                            }
                        }
                    } while (recentMediaFeed.getPagination().hasNextPage());
                    logger.info("Total media:" + media);
                    SocialStatistic mediaStats = new SocialStatistic.Builder()
                            .providerType("instagram")
                            .user(user)
                            .recordedAt(Calendar.getInstance().getTime())
                            .statisticsType(SocialStatistic.STAT_TYPE_MEDIA_COUNT)
                            .statistic(media)
                            .build();
                    socialStatisticsRepository.save(mediaStats);
                    logger.info("Comments:" + comments);
                    SocialStatistic mediaCommentsStats = new SocialStatistic.Builder()
                            .providerType("instagram")
                            .user(user)
                            .recordedAt(Calendar.getInstance().getTime())
                            .statisticsType(SocialStatistic.STAT_TYPE_MEDIA_COMMENTS)
                            .statistic(comments)
                            .build();
                    socialStatisticsRepository.save(mediaCommentsStats);
                    logger.info("likes:" + likes);
                    SocialStatistic mediaLikes = new SocialStatistic.Builder()
                            .providerType("instagram")
                            .user(user)
                            .recordedAt(Calendar.getInstance().getTime())
                            .statisticsType(SocialStatistic.STAT_TYPE_MEDIA_LIKES)
                            .statistic(likes)
                            .build();
                    socialStatisticsRepository.save(mediaLikes);

                } catch (InstagramException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
        }
    }

}
