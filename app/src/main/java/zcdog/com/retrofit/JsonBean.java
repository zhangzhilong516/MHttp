package zcdog.com.retrofit;

import java.util.List;

/**
 * @author: zhangzhilong
 * @date: 2019/2/13
 * @des:
 */
public class JsonBean {

    private String message;
    private StatusBean status;
    private double responseTime;
    private List<ChessboardinfoBean> chessboardinfo;
    private List<?> commodity;
    private List<MallcontentBean> mallcontent;
    private List<?> activity;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public List<ChessboardinfoBean> getChessboardinfo() {
        return chessboardinfo;
    }

    public void setChessboardinfo(List<ChessboardinfoBean> chessboardinfo) {
        this.chessboardinfo = chessboardinfo;
    }

    public List<?> getCommodity() {
        return commodity;
    }

    public void setCommodity(List<?> commodity) {
        this.commodity = commodity;
    }

    public List<MallcontentBean> getMallcontent() {
        return mallcontent;
    }

    public void setMallcontent(List<MallcontentBean> mallcontent) {
        this.mallcontent = mallcontent;
    }

    public List<?> getActivity() {
        return activity;
    }

    public void setActivity(List<?> activity) {
        this.activity = activity;
    }

    public static class StatusBean {
        /**
         * dateTime : 2019-02-13T17:06:38.019+0800
         * code : 20000
         */

        private String dateTime;
        private int code;

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public static class ChessboardinfoBean {

        private String gridid;
        private String names;
        private String name;
        private String remarks;
        private String iconurl;
        private String type;
        private String url;
        private String packageName;

        public String getGridid() {
            return gridid;
        }

        public void setGridid(String gridid) {
            this.gridid = gridid;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getIconurl() {
            return iconurl;
        }

        public void setIconurl(String iconurl) {
            this.iconurl = iconurl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
    }

    public static class MallcontentBean {

        private String endtime;
        private int seqid;
        private String desc;
        private String starttime;
        private String redirecttype;
        private int type;
        private int isneedlogin;
        private String typename;
        private String parenttabid;
        private List<SubBannersBean> subBanners;

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public int getSeqid() {
            return seqid;
        }

        public void setSeqid(int seqid) {
            this.seqid = seqid;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getRedirecttype() {
            return redirecttype;
        }

        public void setRedirecttype(String redirecttype) {
            this.redirecttype = redirecttype;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getIsneedlogin() {
            return isneedlogin;
        }

        public void setIsneedlogin(int isneedlogin) {
            this.isneedlogin = isneedlogin;
        }

        public String getTypename() {
            return typename;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public String getParenttabid() {
            return parenttabid;
        }

        public void setParenttabid(String parenttabid) {
            this.parenttabid = parenttabid;
        }

        public List<SubBannersBean> getSubBanners() {
            return subBanners;
        }

        public void setSubBanners(List<SubBannersBean> subBanners) {
            this.subBanners = subBanners;
        }

        public static class SubBannersBean {
            /**
             * createtime :
             * id : 63
             * commodityid :
             * redirecturl : TG181130112421902
             * name : 邀粉专区
             * redirecttype : native_commodityList
             * subseqid : 5
             * tabid : 2001
             * imgurl : http://static.zcdog.com/zcdog/active/20190104153658_nian.png
             */

            private String createtime;
            private int id;
            private String commodityid;
            private String redirecturl;
            private String name;
            private String redirecttype;
            private int subseqid;
            private String tabid;
            private String imgurl;

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCommodityid() {
                return commodityid;
            }

            public void setCommodityid(String commodityid) {
                this.commodityid = commodityid;
            }

            public String getRedirecturl() {
                return redirecturl;
            }

            public void setRedirecturl(String redirecturl) {
                this.redirecturl = redirecturl;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getRedirecttype() {
                return redirecttype;
            }

            public void setRedirecttype(String redirecttype) {
                this.redirecttype = redirecttype;
            }

            public int getSubseqid() {
                return subseqid;
            }

            public void setSubseqid(int subseqid) {
                this.subseqid = subseqid;
            }

            public String getTabid() {
                return tabid;
            }

            public void setTabid(String tabid) {
                this.tabid = tabid;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }
        }
    }
}
