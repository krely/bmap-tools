package com.jixiaoyu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.jixiaoyu.model.AjaxResult;
import com.jixiaoyu.model.BMapResp;
import com.jixiaoyu.model.Data;
import com.jixiaoyu.model.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jixiaoyu
 */
@Controller
public class HomeController {

    @Value("${url}")
    private String url;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping("/")
    public String index() {
        return "home";
    }



    @RequestMapping("pointChange")
    @ResponseBody
    public AjaxResult PointChange(@RequestBody Data data) {
        try {
            String point = data.points;

            if (point == null || null == "") {
                throw new Exception("不输入参数没法转换的呀");
            }
            String[] pts = point.split("\n");

            List<Point> from = new ArrayList<>();
            List<Point> to = new ArrayList<Point>();

            int idx = 0;
            for (String pt : pts) {
                idx++;
                if (pt == null || pt == "") {
                    throw new Exception("第" + idx + "行为空");
                }

                if (!pt.contains(",")) {
                    throw new Exception("第" + idx + "行格式不对");
                }
                String[] pp = pt.split(",");

                String lng = pp[0];
                String lat = pp[1];
                Point fpt = new Point(lng, lat);

                from.add(fpt);
                try {
                    Point tpt = change(fpt);
                    to.add(tpt);
                } catch (Exception ex) {
                    throw new Exception("第" + idx + "行转换失败," + ex.getMessage());
                }
            }
            String rest = procResult(from, to);

            return AjaxResult.success(rest);
        } catch (Exception ex) {
            return AjaxResult.error(ex.getMessage());
        }
    }

    private List<Point> change(List<Point> points) {
        String reqUrl = url + buildRequestParams(points);
        return request(reqUrl);
    }


    public String procResult(List<Point> list, List<Point> toList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Point from = list.get(i);
            Point to = toList.get(i);
            sb.append(from.ToStringT() + "\t" + to.ToStringT() + "\n");
        }
        return sb.toString();
    }

    private Point change(Point points) {
        String reqUrl = url + points.toString();
        return request(reqUrl).get(0);
    }

    private List<Point> request(String urlStr) {
        URL url = null;

        // 2. 得到网络访问对象java.net.HttpURLConnection
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            String msg = "";
            // 正常响应
            if (code == 200) {
                // 从流中读取响应信息
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                // 循环从流中读取
                while ((line = reader.readLine()) != null) {
                    msg += line;
                }
                reader.close(); // 关闭流
            }

            BMapResp resp = mapper.readValue(msg, BMapResp.class);

            connection.disconnect();
            System.out.println(msg);
            if (resp.getStatus() == 0) {
                return resp.getResult();
            } else {
                throw new RuntimeException(message(resp.getStatus()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private String message(int code) {
        switch (code) {
            case 1:
                return "内部错误";
            case 4:
                return "转换失败";
            case 21:
                return "from非法";
            case 22:
                return "to非法";
            case 24:
                return "coords格式非法";
            case 25:
                return "coords个数非法，超过限制";
            case 26:
                return "参数错误";
            default:
                return "";
        }
    }

    private String buildRequestParams(List<Point> points) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            Point pt = points.get(i);
            sb.append(pt.toString());
            if (i != points.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

}
