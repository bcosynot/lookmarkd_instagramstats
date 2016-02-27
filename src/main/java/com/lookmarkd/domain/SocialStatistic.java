package com.lookmarkd.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "social_statistic")
public class SocialStatistic implements Serializable {

    public static final int STAT_TYPE_FOLLOWERS = 1;
    public static final int STAT_TYPE_MEDIA_LIKES = 2;
    public static final int STAT_TYPE_MEDIA_COUNT = 3;
    public static final int STAT_TYPE_MEDIA_COMMENTS = 4;
    private static final long serialVersionUID = 1568632497143747991L;

    @Id
    private int id;

    @OneToOne
    @JoinColumn(name = "user")
    private FOSUser user;

    @Column(name = "provider_type")
    private String providerType;

    @Column(name = "statistics_type")
    private int statisticsType;

    @Column(name="recorded_at")
    private Date recordedAt;

    @Column(name = "statistic")
    private long statistic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FOSUser getUser() {
        return user;
    }

    public void setUser(FOSUser user) {
        this.user = user;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public int getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(int statisticsType) {
        this.statisticsType = statisticsType;
    }

    public Date getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Date recordedAt) {
        this.recordedAt = recordedAt;
    }

    public long getStatistic() {
        return statistic;
    }

    public void setStatistic(long statistic) {
        this.statistic = statistic;
    }

    private SocialStatistic(Builder builder) {
        this.user = builder.user;
        this.providerType = builder.providerType;
        this.statisticsType = builder.statisticsType;
        this.recordedAt = builder.recordedAt;
        this.statistic = builder.statistic;
    }

    public SocialStatistic() {
        super();
    }

    public static class Builder {
        private FOSUser user;
        private String providerType;
        private int statisticsType;
        private Date recordedAt;
        private int statistic;

        public Builder user(FOSUser user) {
            this.user = user;
            return this;
        }

        public Builder providerType(String providerType) {
            this.providerType = providerType;
            return this;
        }

        public Builder statisticsType(int statisticsType) {
            this.statisticsType = statisticsType;
            return this;
        }

        public Builder recordedAt(Date recordedAt) {
            this.recordedAt = recordedAt;
            return this;
        }

        public Builder statistic(int statistic) {
            this.statistic = statistic;
            return this;
        }

        public Builder(){
            super();
        }

        public SocialStatistic build() {
            return new SocialStatistic(this);
        }

    }
}
