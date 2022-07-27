package org.chelonix.jpscp.nhknewseasy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsItem implements Comparable<NewsItem>{

    @JsonProperty("news_id")
    String id;

    String title;

    @JsonProperty("news_prearranged_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    Date time;

    @JsonProperty("news_priority_number")
    int priorityNumber;

    @Override
    public int compareTo(NewsItem o) {
        if (this.time.after(o.time)) {
            return -1;
        } else if (this.time.before(o.time)) {
            return 1;
        } else {
            return this.priorityNumber - o.priorityNumber;
        }
    }

    /*
    news_priority_number: "1",
news_prearranged_time: "2022-07-11 16:10:00",
news_id: "k10013711301000",
title: "参議院の選挙　自民党が半分以上の63人になった",
title_with_ruby: "<ruby>参議院<rt>さんぎいん</rt></ruby>の<ruby>選挙<rt>せんきょ</rt></ruby>　<ruby>自民党<rt>じみんとう</rt></ruby>が<ruby>半分<rt>はんぶん</rt></ruby><ruby>以上<rt>いじょう</rt></ruby>の63<ruby>人<rt>にん</rt></ruby>になった",
news_file_ver: true,
news_creation_time: "2022-07-19 16:09:52",
news_preview_time: "2022-07-19 16:09:52",
news_publication_time: "2022-07-19 11:59:55",
news_publication_status: true,
has_news_web_image: true,
has_news_web_movie: false,
has_news_easy_image: false,
has_news_easy_movie: false,
has_news_easy_voice: true,
news_web_image_uri: "https://www3.nhk.or.jp/news/html/20220710/../20220710/K10013711301_2207110732_0711073518_01_02.jpg",
news_web_movie_uri: "",
news_easy_image_uri: "''",
news_easy_movie_uri: "''",
news_easy_voice_uri: "k10013711301000.mp4",
news_display_flag: true,
news_web_url: "https://www3.nhk.or.jp/news/html/20220710/k10013711301000.html"
     */
}
