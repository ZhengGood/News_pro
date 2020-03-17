package com.example.ts.news.Bean;

import java.util.List;

public class day {


    /**
     * code : 200
     * msg : success
     * newslist : [{"oneid":2604,"word":"一个人就是一片荒原，偶尔有房客，有雷声，有春暖花开。","wordfrom":"《此刻，月光洒在中年的庭院》","imgurl":"http://image.wufazhuce.com/FordKXIwo_85nVFcvOn3TC8oZXXH","imgauthor":"EmilieCotterill","date":"2019-10-18"}]
     */

    private int code;
    private String msg;
    private List<NewslistBean> newslist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<NewslistBean> getNewslist() {
        return newslist;
    }

    public void setNewslist(List<NewslistBean> newslist) {
        this.newslist = newslist;
    }

    public static class NewslistBean {
        /**
         * oneid : 2604
         * word : 一个人就是一片荒原，偶尔有房客，有雷声，有春暖花开。
         * wordfrom : 《此刻，月光洒在中年的庭院》
         * imgurl : http://image.wufazhuce.com/FordKXIwo_85nVFcvOn3TC8oZXXH
         * imgauthor : EmilieCotterill
         * date : 2019-10-18
         */

        private int oneid;
        private String word;
        private String wordfrom;
        private String imgurl;
        private String imgauthor;
        private String date;

        public int getOneid() {
            return oneid;
        }

        public void setOneid(int oneid) {
            this.oneid = oneid;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getWordfrom() {
            return wordfrom;
        }

        public void setWordfrom(String wordfrom) {
            this.wordfrom = wordfrom;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getImgauthor() {
            return imgauthor;
        }

        public void setImgauthor(String imgauthor) {
            this.imgauthor = imgauthor;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return
                     word+"   "+"每日鸡汤";
        }
    }
}
