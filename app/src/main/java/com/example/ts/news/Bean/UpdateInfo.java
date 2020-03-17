package com.example.ts.news.Bean;

public class UpdateInfo
{


        /**
         * versionName : 2.0
         * versionCode : 1
         * content : 修复bug
         * url : http://192.168.2.106:8080/update/new.apk
         */

        private String versionName;
        private int versionCode;
        private String content;
        private String url;

        public String getVersionName() {
                return versionName;
        }

        public void setVersionName(String versionName) {
                this.versionName = versionName;
        }

        public int getVersionCode() {
                return versionCode;
        }

        public void setVersionCode(int versionCode) {
                this.versionCode = versionCode;
        }

        public String getContent() {
                return content;
        }

        public void setContent(String content) {
                this.content = content;
        }

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {
                this.url = url;
        }
}
