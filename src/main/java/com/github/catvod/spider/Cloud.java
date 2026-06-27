package com.github.catvod.spider;


import com.github.catvod.api.Pan123Api;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Path;
import com.github.catvod.utils.Util;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.github.catvod.api.TianyiApi.URL_CONTAIN;
import static com.github.catvod.spider.Quark.patternQuark;
import static com.github.catvod.spider.UC.patternUC;

/**
 * @author ColaMint & Adam & FongMi
 */
public class Cloud extends Spider {
    private Quark quark = null;
    private Ali ali = null;
    private UC uc = null;
    private TianYi tianYi = null;
    private YiDongYun yiDongYun = null;
    private BaiDuPan baiDuPan = null;
    private Pan123 pan123 = null;

    private boolean isQuarkInit = false;
    private boolean isAliInit = false;
    private boolean isUCInit = false;
    private boolean isUCTokenInit = false;
    private boolean isTianYiInit = false;
    private boolean isYiDongYunInit = false;
    private boolean isBaiDuPanInit = false;
    private boolean isPan123Init = false;

    //获取配置文件状态
    private void getConfigFileState() {


        File[] files = Path.tv().listFiles(file -> file.getName().startsWith("."));
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(".quark")) {
                    isQuarkInit = true;
                }
                if (file.getName().equals(".ali")) {
                    isAliInit = true;
                }
                if (file.getName().equals(".uc")) {
                    isUCInit = true;
                }
                if (file.getName().equals(".uctoken")) {
                    isUCTokenInit = true;
                }
                if (file.getName().equals(".tianyi")) {
                    isTianYiInit = true;
                }
                if (file.getName().equals(".yun139")) {
                    isYiDongYunInit = true;
                }
                if (file.getName().equals(".bd")) {
                    isBaiDuPanInit = true;
                }
                if (file.getName().equals(".pan123")) {
                    isPan123Init = true;
                }
            }
        }
    }

    @Override
    public void init(String extend) throws Exception {
        getConfigFileState();
        JsonObject ext = StringUtils.isAllBlank(extend) ? new JsonObject() : Json.safeObject(extend);
        if (isQuarkInit) {
            quark = new Quark();
            quark.init(ext.has("cookie") ? ext.get("cookie").getAsString() : "");
        }
        /* if (isAliInit) {
            ali = new Ali();
            ali.init(ext.has("token") ? ext.get("token").getAsString() : "");
        }*/
        if (isUCInit&&isUCTokenInit) {
            uc = new UC();
            uc.init(ext.has("uccookie") ? ext.get("uccookie").getAsString() : "");
        }
        if (isTianYiInit) {
            tianYi = new TianYi();
            tianYi.init(ext.has("tianyicookie") ? ext.get("tianyicookie").getAsString() : "");
        }
        if (isYiDongYunInit) {
            yiDongYun = new YiDongYun();
            yiDongYun.init("");
        }
        if (isBaiDuPanInit) {
            baiDuPan = new BaiDuPan();
            baiDuPan.init("");
        }
        if (isPan123Init) {
            pan123 = new Pan123();
            pan123.init("");
        }




    }

    @Override
    public String detailContent(List<String> shareUrl) throws Exception {
       /* if (shareUrl.get(0).matches(Ali.pattern.pattern())) {
            return ali.detailContent(shareUrl);
        } else*/
        if (shareUrl.get(0).matches(patternQuark) && quark != null) {
            return quark.detailContent(shareUrl);
        } else if (shareUrl.get(0).matches(patternUC) && uc != null) {
            return uc.detailContent(shareUrl);
        } else if (shareUrl.get(0).contains(URL_CONTAIN) && tianYi != null) {
            return tianYi.detailContent(shareUrl);
        } else if (shareUrl.get(0).contains(YiDongYun.URL_START) && yiDongYun != null) {
            return yiDongYun.detailContent(shareUrl);
        } else if (shareUrl.get(0).contains(BaiDuPan.URL_START) && baiDuPan != null) {
            return baiDuPan.detailContent(shareUrl);
        } else if (shareUrl.get(0).matches(Pan123Api.regex) && pan123 != null) {
            SpiderDebug.log("Pan123Api shareUrl：" + Json.toJson(shareUrl));
            return pan123.detailContent(shareUrl);
        }
        return null;
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        if (flag.contains("quark") && quark != null) {
            return quark.playerContent(flag, id, vipFlags);
        } else if (flag.contains("uc") && uc != null) {
            return uc.playerContent(flag, id, vipFlags);
        } else if (flag.contains("天意") && tianYi != null) {
            return tianYi.playerContent(flag, id, vipFlags);
        } else if (flag.contains("移动") && yiDongYun != null) {
            return yiDongYun.playerContent(flag, id, vipFlags);
        }/* else {
            return ali.playerContent(flag, id, vipFlags);
        }*/ else if (flag.contains("BD") && baiDuPan != null) {
            return baiDuPan.playerContent(flag, id, vipFlags);
        } else if (flag.contains("pan123") && pan123 != null) {
            return pan123.playerContent(flag, id, vipFlags);
        }/*else {
            return ali.playerContent(flag, id, vipFlags);
        }*/
        return null;
    }

    protected String detailContentVodPlayFrom(List<String> shareLinks) {
        List<String> from = new ArrayList<>();
        int i = 0;
        for (String shareLink : shareLinks) {
            i++;
            try {
                if (shareLink.matches(patternUC) && uc != null) {
                    from.add(uc.detailContentVodPlayFrom(List.of(shareLink), i));
                } else if (shareLink.matches(patternQuark) && quark != null) {
                    from.add(quark.detailContentVodPlayFrom(List.of(shareLink), i));
                } else if (shareLink.contains(URL_CONTAIN) && tianYi != null) {
                    from.add(tianYi.detailContentVodPlayFrom(List.of(shareLink), i));
                } else if (shareLink.contains(YiDongYun.URL_START) && yiDongYun != null) {
                    from.add(yiDongYun.detailContentVodPlayFrom(List.of(shareLink), i));
                } else if (shareLink.contains(BaiDuPan.URL_START) && baiDuPan != null) {
                    from.add(baiDuPan.detailContentVodPlayFrom(List.of(shareLink), i));
                } else if (shareLink.matches(Pan123Api.regex) && pan123 != null) {
                    from.add(pan123.detailContentVodPlayFrom(List.of(shareLink), i));
                } else {
                    from.add("未知网盘[网盘未配置]");
                }
            } catch (Exception e) {
                from.add("解析失败");
            }
        }
        return StringUtils.join(from, "$$$");
    }


    protected String detailContentVodPlayUrl(List<String> shareLinks) throws Exception {
        List<String> urls = new ArrayList<>();
        int i = 0;
        for (String shareLink : shareLinks) {
            i++;
            // 调用对应网盘的 detailContentVodPlayFrom 获取 flag 数量，
            // 据此生成相同数量的 RESOLVE: url，确保 flag 和 url 对齐。
            // （Quark 的 detailContentVodPlayFrom 会返回多个 flag：原画+普画格式，
            // 若不匹配数量会导致前端 flag-url 错位）
            int count = 1;
            try {
                String fromStr = null;
                if (shareLink.matches(patternUC) && uc != null) {
                    fromStr = uc.detailContentVodPlayFrom(List.of(shareLink), i);
                } else if (shareLink.matches(patternQuark) && quark != null) {
                    fromStr = quark.detailContentVodPlayFrom(List.of(shareLink), i);
                } else if (shareLink.contains(URL_CONTAIN) && tianYi != null) {
                    fromStr = tianYi.detailContentVodPlayFrom(List.of(shareLink), i);
                } else if (shareLink.contains(YiDongYun.URL_START) && yiDongYun != null) {
                    fromStr = yiDongYun.detailContentVodPlayFrom(List.of(shareLink), i);
                } else if (shareLink.contains(BaiDuPan.URL_START) && baiDuPan != null) {
                    fromStr = baiDuPan.detailContentVodPlayFrom(List.of(shareLink), i);
                } else if (shareLink.matches(Pan123Api.regex) && pan123 != null) {
                    fromStr = pan123.detailContentVodPlayFrom(List.of(shareLink), i);
                }
                if (fromStr != null && !fromStr.isEmpty()) {
                    count = fromStr.split("\\$\\$\\$").length;
                }
            } catch (Exception e) {
                SpiderDebug.log("detailContentVodPlayUrl: getFromCount error: " + e.getMessage());
            }
            for (int j = 0; j < count; j++) {
                urls.add("点击加载选集$RESOLVE:" + shareLink);
            }
        }
        return StringUtils.join(urls, "$$$");
    }

    /**
     * 延迟解析单个分享链接，返回对应的播放地址。
     * 仅在用户点击具体选集时调用，避免 token 缺失时立即触发扫码。
     */
    public String resolveShare(String flag, String shareLink) throws Exception {
        try {
            if (shareLink.matches(patternUC) && uc != null) {
                return uc.detailContentVodPlayUrl(List.of(shareLink));
            } else if (shareLink.matches(patternQuark) && quark != null) {
                return quark.detailContentVodPlayUrl(List.of(shareLink));
            } else if (shareLink.contains(URL_CONTAIN) && tianYi != null) {
                return tianYi.detailContentVodPlayUrl(List.of(shareLink));
            } else if (shareLink.contains(YiDongYun.URL_START) && yiDongYun != null) {
                return yiDongYun.detailContentVodPlayUrl(List.of(shareLink));
            } else if (shareLink.contains(BaiDuPan.URL_START) && baiDuPan != null) {
                return baiDuPan.detailContentVodPlayUrl(List.of(shareLink));
            } else if (shareLink.matches(Pan123Api.regex) && pan123 != null) {
                return pan123.detailContentVodPlayUrl(List.of(shareLink));
            } else {
                return "http://error.com/网盘未配置";
            }
        } catch (Exception e) {
            return "http://error.com/解析失败: " + e.getMessage();
        }
    }
}
