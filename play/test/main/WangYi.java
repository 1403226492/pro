package main;
import book.JSONUtils;
import book.database.MongoDBUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tian on 2017/7/25.
 */
public class WangYi {
    public static void main(String[] args) throws Exception {
        //爬取条数,10的倍数，网易新闻每10条预留大约2个广告位，所以爬取新闻的真实条数大约为80%
        int deep = 30;
        //爬取宽度，0:首页，1:社会，2:国内，3:国际，4:历史
        int width = 1;

        //网易新闻类型
        String[] typeArray={"BBM54PGAwangning","BCR1UC1Qwangning","BD29LPUBwangning","BD29MJTVwangning","C275ML7Gwangning"};
        String type = typeArray[width];

        //网易新闻列表url
        String url1 = "http://3g.163.com/touch/reconstruct/article/list/";
        //网易新闻内容url
        String url2 = "http://3g.163.com/news/article/";


        List<String> ids = new ArrayList<>();

        //根据url1，爬取条数，新闻类型获取新闻docid
        ids = getIds(url1,deep,type);
        //根据url2，新闻docid获取内容并存储到MongoDB
        getContent(url2,ids);
    }

    //设置爬取深度，循环多次获取docid
    private static List<String> getIds(String url1,int num,String type) {
        List<String> id = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i=0;i<=num;i+=10){
            id = getDocid(url1,i,type);
            ids.addAll(id);
        }
        return ids;
    }

    //根据新闻列表url，获取新闻docid,并把docid存储到list中
    private static List<String> getDocid(String url,int num,String type) {
        String json = null;
        List<String> id=new ArrayList<>();
        Map map=null;
        JSONArray parseArray=null;
        String jsonStrM="";
        json = JSONUtils.loadJson(url+type+"/"+num+"-10.html");
        String jsonStr = StringUtils.substringBeforeLast(json, ")");
        String jsonStrO = StringUtils.substringAfter(jsonStr,"artiList(");
        Map parse = (Map) JSONObject.parse(jsonStrO);
        parseArray = (JSONArray) parse.get(type);
        for(int j=0;j<parseArray.size();j++){
            map = (Map)parseArray.get(j);
            id.add((String) map.get("docid"));
        }
        return id;
    }

    //根据内容url2获取新闻信息并进行存储
    private static void getContent(String url2, List<String> ids) {
        System.out.println("存储开始！！");
        String url = null;
        Connection connection = Jsoup.connect(url2);
        int i = 1;
        for (;i<ids.size();i++){
            url = url2+ids.get(i)+".html";
            connection = Jsoup.connect(url);
            try {
                Document document = connection.get();
                //获取文章标题
                Elements title = document.select("[class=title]");
                //获取文章来源和文章发布时间
                Elements articleInfo = document.select("[class=info]");
                Elements src = articleInfo.select("[class=source js-source]");
                Elements time = articleInfo.select("[class=time js-time]");
                //获取文章内容
                Elements contentEle = document.select("[class=page js-page on]");
                DBCollection dbCollection= null;
                try {
                    dbCollection = MongoDBUtils.connMongoDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BasicDBObject obj = new BasicDBObject();
                obj.put("title", src.html());
                obj.put("srcFrom", src.html());
                obj.put("time", time.html());
                obj.put("content", contentEle.html());
                dbCollection.insert(obj);
                DBCursor dbCursor = dbCollection.find();
                while(dbCursor.hasNext()){
                    Map map = (Map)dbCursor.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("本次共计存储"+i*0.8+"条数据");
    }

}
